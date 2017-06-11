/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.text.TextUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.ironsource.environment.NetworkStateReceiver;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.AbstractAdUnitManager;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.config.ConfigFile;
import com.ironsource.mediationsdk.events.RewardedVideoEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLogger.IronSourceTag;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.model.ApplicationEvents;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.model.ProviderOrder;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;
import com.ironsource.mediationsdk.sdk.RewardedVideoApi;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoManagerListener;
import com.ironsource.mediationsdk.server.Server;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import org.json.JSONException;
import org.json.JSONObject;

class RewardedVideoManager
extends AbstractAdUnitManager
implements RewardedVideoApi,
RewardedVideoManagerListener,
NetworkStateReceiver.NetworkStateReceiverListener {
    private final String TAG;
    private final String KTO_ALGORITHM = "KTO";
    private ArrayList<AbstractAdapter> mInitiatedAdapters;
    private ArrayList<AbstractAdapter> mNotAvailableAdapters;
    private ArrayList<AbstractAdapter> mAvailableAdapters;
    private ArrayList<AbstractAdapter> mExhaustedAdapters;
    private RewardedVideoListener mListenersWrapper;
    private boolean mIsAdAvailable;
    private NetworkStateReceiver mNetworkStateReceiver;
    private boolean mPauseSmartLoadDueToNetworkUnavailability;
    private boolean mDidReportInitialAvailability;

    public RewardedVideoManager() {
        this.TAG = this.getClass().getSimpleName();
        this.mPauseSmartLoadDueToNetworkUnavailability = false;
        this.mDidReportInitialAvailability = false;
        this.prepareStateForInit();
    }

    private void prepareStateForInit() {
        this.mIsAdAvailable = false;
        this.mAvailableAdapters = new ArrayList();
        this.mInitiatedAdapters = new ArrayList();
        this.mNotAvailableAdapters = new ArrayList();
        this.mExhaustedAdapters = new ArrayList();
    }

    private synchronized void reportImpression(String adapterUrl, boolean hit, int placementId) {
        String url = "";
        try {
            url = url + adapterUrl;
            url = url + "&sdkVersion=" + IronSourceUtils.getSDKVersion();
            Server.callAsyncRequestURL(url, hit, placementId);
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.NETWORK, "reportImpression:(providerURL:" + url + ", " + "hit:" + hit + ")", e);
        }
    }

    private void reportFalseImpressionsOnHigherPriority(int priority, int placementId) {
        ArrayList<String> providers = this.mServerResponseWrapper.getProviderOrder().getRewardedVideoProviderOrder();
        for (int i = 0; i < priority; ++i) {
            ProviderSettings providerSettings;
            if (this.isExhausted(providers.get(i)) || this.isPremiumAdapter(providers.get(i)) && !this.canShowPremium() || (providerSettings = this.mServerResponseWrapper.getProviderSettingsHolder().getProviderSettings(providers.get(i))) == null) continue;
            String requestUrl = providerSettings.getRewardedVideoSettings().optString("requestUrl");
            this.reportImpression(requestUrl, false, placementId);
        }
    }

    private boolean isExhausted(String providerName) {
        for (AbstractAdapter exAdapter : this.mExhaustedAdapters) {
            if (!exAdapter.getProviderName().equalsIgnoreCase(providerName)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized void initRewardedVideo(Activity activity, String appKey, String userId) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":initRewardedVideo(appKey: " + appKey + ", userId: " + userId + ")", 1);
        this.mAppKey = appKey;
        this.mUserId = userId;
        this.mActivity = activity;
        this.mServerResponseWrapper = IronSourceObject.getInstance().getCurrentServerResponse();
        if (this.mServerResponseWrapper != null) {
            AbstractAdapter lastLoadedAdapter;
            int numOfAdaptersToLoad = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoAdaptersSmartLoadAmount();
            for (int i = 0; i < numOfAdaptersToLoad && (lastLoadedAdapter = this.loadNextAdapter()) != null; ++i) {
            }
        }
    }

    private synchronized void reportShowFail(IronSourceError error) {
        this.mListenersWrapper.onRewardedVideoAdShowFailed(error);
    }

    private synchronized AbstractAdapter startAdapter(String providerName) {
        return this.startAdapter(providerName, true);
    }

    private synchronized AbstractAdapter startAdapter(String providerName, boolean regularOrder) {
        AbstractAdapter providerAdapter;
        if (TextUtils.isEmpty((CharSequence)providerName)) {
            return null;
        }
        ProviderSettings providerSettings = this.mServerResponseWrapper.getProviderSettingsHolder().getProviderSettings(providerName);
        if (providerSettings == null) {
            return null;
        }
        String providerNameForReflection = providerSettings.getProviderTypeForReflection();
        String requestUrl = providerSettings.getRewardedVideoSettings().optString("requestUrl");
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":startAdapter(" + providerName + ")", 1);
        if (providerName.isEmpty()) {
            return null;
        }
        try {
            Class mAdapterClass;
            Method startAdapterMethod;
            IronSourceObject sso = IronSourceObject.getInstance();
            providerAdapter = sso.getExistingAdapter(providerName);
            if (providerAdapter == null && (providerAdapter = (AbstractAdapter)(startAdapterMethod = (mAdapterClass = Class.forName("com.ironsource.adapters." + providerNameForReflection.toLowerCase() + "." + providerNameForReflection + "Adapter")).getMethod("startAdapter", String.class, String.class)).invoke(mAdapterClass, providerName, requestUrl)) != null) {
                sso.addToAdaptersList(providerAdapter);
            }
            if (providerAdapter.getMaxRVAdsPerIteration() < 1) {
                return null;
            }
            this.setCustomParams(providerAdapter);
            providerAdapter.setLogListener(this.mLoggerManager);
            providerAdapter.setRewardedVideoTimeout(this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoAdaptersSmartLoadTimeout());
            if (regularOrder) {
                providerAdapter.setRewardedVideoPriority(this.mServerResponseWrapper.getRVAdaptersLoadPosition());
            }
            providerAdapter.setRewardedVideoConfigurations(this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations());
            if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginType())) {
                providerAdapter.setPluginData(ConfigFile.getConfigFile().getPluginType(), ConfigFile.getConfigFile().getPluginFrameworkVersion());
            }
            providerAdapter.setRewardedVideoListener(this);
            if (regularOrder) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ": startAdapter(" + providerName + ") moved to 'Initiated' list", 0);
                this.addInitiatedRewardedVideoAdapter(providerAdapter);
            }
            String appKey = sso.getIronSourceAppKey();
            providerAdapter.initRewardedVideo(this.mActivity, appKey, this.mUserId);
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":startAdapter(" + providerName + ")", e);
            if (regularOrder) {
                this.mServerResponseWrapper.decreaseMaxRVAdapters();
                if (this.shouldNotifyAvailabilityChanged(false)) {
                    this.mListenersWrapper.onRewardedVideoAvailabilityChanged(false);
                }
            }
            IronSourceError error = ErrorBuilder.buildInitFailedError(providerName + " initialization failed - please verify that required dependencies are in you build path.", "Rewarded Video");
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, error.toString(), 2);
            return null;
        }
        return providerAdapter;
    }

    @Override
    public void onResume(Activity activity) {
        if (activity != null) {
            this.mActivity = activity;
        }
    }

    @Override
    public void onPause(Activity activity) {
    }

    @Override
    public void setAge(int age) {
    }

    @Override
    public void setGender(String gender) {
    }

    @Override
    public void setMediationSegment(String segment) {
    }

    @Override
    public void showRewardedVideo() {
    }

    @Override
    public synchronized void showRewardedVideo(String placementName) {
        if (!IronSourceUtils.isNetworkConnected((Context)this.mActivity)) {
            this.reportShowFail(ErrorBuilder.buildNoInternetConnectionShowFailError("Rewarded Video"));
            return;
        }
        this.sendShowCheckAvailabilityEvents(placementName);
        if (this.mAvailableAdapters.size() > 0) {
            ArrayList<AbstractAdapter> tempAvailableAdapter = new ArrayList<AbstractAdapter>(this.mAvailableAdapters);
            for (AbstractAdapter adapter : tempAvailableAdapter) {
                if (adapter.isRewardedVideoAvailable()) {
                    if (this.showRVAdapter(placementName, adapter)) {
                        if (!this.isPremiumAdapter(adapter.getProviderName())) {
                            this.disablePremiumForCurrentSession();
                        }
                        adapter.increaseNumberOfVideosPlayed();
                        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, adapter.getProviderName() + ": " + adapter.getNumberOfVideosPlayed() + "/" + adapter.getMaxRVAdsPerIteration() + " videos played", 0);
                        if (adapter.getNumberOfVideosPlayed() == adapter.getMaxRVAdsPerIteration()) {
                            this.completeAdapterIteration(adapter);
                        }
                        this.completeIterationRound();
                    }
                    break;
                }
                this.onRewardedVideoAvailabilityChanged(false, adapter);
                Exception e = new Exception("FailedToShowVideoException");
                this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.INTERNAL, adapter.getProviderName() + " Failed to show video", e);
            }
        } else if (this.isBackFillAvailable()) {
            this.showRVAdapter(placementName, this.mBackFillAdapter);
        }
    }

    private synchronized boolean showRVAdapter(String placementName, AbstractAdapter adapter) {
        if (TextUtils.isEmpty((CharSequence)placementName) || adapter == null) {
            return false;
        }
        CappingManager.incrementShowCounter((Context)this.mActivity, this.getPlacementByName(placementName));
        if (this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().isUltraEventsEnabled()) {
            Placement placement = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoPlacement(placementName);
            this.reportImpression(adapter.getUrl(), true, placement.getPlacementId());
            int priority = adapter.getRewardedVideoPriority();
            this.reportFalseImpressionsOnHigherPriority(priority, placement.getPlacementId());
        }
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("placement", (Object)placementName);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(2, data);
        RewardedVideoEventsManager.getInstance().log(event);
        adapter.showRewardedVideo(placementName);
        return true;
    }

    private void sendShowCheckAvailabilityEvents(String placementName) {
        for (AbstractAdapter adapter2 : this.mAvailableAdapters) {
            this.createAndSendShowCheckAvailabilityEvent(adapter2, placementName, true);
        }
        for (AbstractAdapter adapter2 : this.mNotAvailableAdapters) {
            if (this.isPremiumAdapter(adapter2.getProviderName()) && !this.canShowPremium()) continue;
            this.createAndSendShowCheckAvailabilityEvent(adapter2, placementName, false);
        }
        if (this.mBackFillAdapter != null) {
            this.createAndSendShowCheckAvailabilityEvent(this.mBackFillAdapter, placementName, this.isBackFillAvailable());
        }
    }

    private void createAndSendShowCheckAvailabilityEvent(AbstractAdapter adapter, String placementName, boolean status) {
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("placement", (Object)placementName);
            data.put("status", (Object)(status ? "true" : "false"));
            data.put("providerPriority", adapter.getRewardedVideoPriority());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(19, data);
        RewardedVideoEventsManager.getInstance().log(event);
    }

    @Override
    public synchronized boolean isRewardedVideoAvailable() {
        if (this.mPauseSmartLoadDueToNetworkUnavailability) {
            return false;
        }
        ArrayList<AbstractAdapter> tepmAvailableAdaptersList = new ArrayList<AbstractAdapter>(this.mAvailableAdapters);
        for (AbstractAdapter adapter : tepmAvailableAdaptersList) {
            if (adapter.isRewardedVideoAvailable()) {
                return true;
            }
            this.onRewardedVideoAvailabilityChanged(false, adapter);
        }
        return false;
    }

    @Override
    public void setRewardedVideoListener(RewardedVideoListener listener) {
        this.mListenersWrapper = listener;
    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError error, AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdShowFailed(" + error + ")", 1);
        this.mListenersWrapper.onRewardedVideoAdShowFailed(error);
    }

    private AbstractAdapter loadNextAdapter() {
        int numOfAdaptersToLoad = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoAdaptersSmartLoadAmount();
        AbstractAdapter initiatedAdapter = null;
        if (this.mAvailableAdapters.size() + this.mInitiatedAdapters.size() < numOfAdaptersToLoad) {
            while (this.mServerResponseWrapper.hasMoreRVProvidersToLoad() && initiatedAdapter == null) {
                initiatedAdapter = this.startAdapter(this.mServerResponseWrapper.getNextRVProvider());
            }
        }
        return initiatedAdapter;
    }

    @Override
    public void onRewardedVideoAdOpened(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdOpened()", 1);
        EventData event = new EventData(5, IronSourceUtils.getProviderAdditionalData(adapter));
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onRewardedVideoAdOpened();
    }

    @Override
    public void onRewardedVideoAdClosed(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdClosed()", 1);
        EventData event = new EventData(6, IronSourceUtils.getProviderAdditionalData(adapter));
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onRewardedVideoAdClosed();
        this.notifyIsAdAvailableForStatistics();
    }

    @Override
    public synchronized void onRewardedVideoAvailabilityChanged(boolean available, AbstractAdapter adapter) {
        if (!this.mPauseSmartLoadDueToNetworkUnavailability) {
            try {
                JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
                try {
                    data.put("status", (Object)String.valueOf(available));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                EventData event = new EventData(7, data);
                RewardedVideoEventsManager.getInstance().log(event);
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAvailabilityChanged(available:" + available + ")", 1);
                if (this.isPremiumAdapter(adapter.getProviderName())) {
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + " is a Premium adapter, canShowPremium: " + this.canShowPremium(), 1);
                }
                if (this.isBackFillAdapter(adapter)) {
                    if (this.shouldNotifyAvailabilityChanged(available)) {
                        this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                    }
                    return;
                }
                if (this.isPremiumAdapter(adapter.getProviderName()) && !this.canShowPremium()) {
                    this.addUnavailableRewardedVideoAdapter(adapter);
                    if (this.shouldNotifyAvailabilityChanged(false)) {
                        this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                    }
                    return;
                }
                if (!this.mExhaustedAdapters.contains(adapter)) {
                    if (available) {
                        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Available' list", 0);
                        this.addAvailableRewardedVideoAdapter(adapter, false);
                        if (this.shouldNotifyAvailabilityChanged(available)) {
                            this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                        }
                    } else {
                        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Not Available' list", 0);
                        this.addUnavailableRewardedVideoAdapter(adapter);
                        if (this.shouldNotifyAvailabilityChanged(available)) {
                            if (this.mBackFillAdapter == null && !this.mBackFillInitStarted) {
                                String backFillAdapterName = this.mServerResponseWrapper.getRVBackFillProvider();
                                if (!TextUtils.isEmpty((CharSequence)backFillAdapterName)) {
                                    this.mBackFillInitStarted = true;
                                    this.mBackFillAdapter = this.startAdapter(backFillAdapterName, false);
                                }
                                if (this.mBackFillAdapter == null) {
                                    this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                                }
                            } else if (this.isBackFillAvailable()) {
                                if (this.shouldNotifyAvailabilityChanged(true)) {
                                    this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                                }
                            } else {
                                this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
                            }
                        }
                        this.loadNextAdapter();
                        this.completeIterationRound();
                    }
                }
            }
            catch (Throwable e) {
                this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onRewardedVideoAvailabilityChanged(available:" + available + ", " + "provider:" + adapter.getProviderName() + ")", e);
            }
        }
    }

    private synchronized void completeAdapterIteration(AbstractAdapter adapter) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":completeIteration", 1);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Exhausted' list", 0);
            this.addExhaustedRewardedVideoAdapter(adapter);
            this.loadNextAdapter();
            adapter.resetNumberOfVideosPlayed();
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "completeIteration(provider:" + adapter.getProviderName() + ")", e);
        }
    }

    private synchronized boolean isIterationRoundComplete() {
        return this.mInitiatedAdapters.size() == 0 && this.mAvailableAdapters.size() == 0 && this.mExhaustedAdapters.size() > 0;
    }

    private synchronized void completeIterationRound() {
        if (isIterationRoundComplete()) {
            this.mLoggerManager.log(IronSourceTag.INTERNAL, "Reset Iteration", 0);
            boolean isAvailable = false;
            Iterator it = ((ArrayList) this.mExhaustedAdapters.clone()).iterator();
            while (it.hasNext()) {
                AbstractAdapter exhaustedAdapter = (AbstractAdapter) it.next();
                if (exhaustedAdapter.isRewardedVideoAvailable()) {
                    this.mLoggerManager.log(IronSourceTag.INTERNAL, exhaustedAdapter.getProviderName() + ": " + "moved to 'Available'", 0);
                    addAvailableRewardedVideoAdapter(exhaustedAdapter, true);
                    isAvailable = true;
                } else {
                    this.mLoggerManager.log(IronSourceTag.INTERNAL, exhaustedAdapter.getProviderName() + ": " + "moved to 'Not Available'", 0);
                    addUnavailableRewardedVideoAdapter(exhaustedAdapter);
                }
            }
            this.mLoggerManager.log(IronSourceTag.INTERNAL, "End of Reset Iteration", 0);
            if (shouldNotifyAvailabilityChanged(isAvailable)) {
                this.mListenersWrapper.onRewardedVideoAvailabilityChanged(this.mIsAdAvailable);
            }
        }
    }

    private synchronized boolean shouldNotifyAvailabilityChanged(boolean adapterAvailability) {
        boolean shouldNotify = false;
        if (!this.mIsAdAvailable && adapterAvailability && (this.mAvailableAdapters.size() > 0 || this.isBackFillAvailable())) {
            this.mIsAdAvailable = true;
            shouldNotify = true;
        } else if (this.mIsAdAvailable && !adapterAvailability && this.mAvailableAdapters.size() <= 0 && !this.isBackFillAvailable()) {
            this.mIsAdAvailable = false;
            shouldNotify = true;
        } else if (!adapterAvailability && this.mNotAvailableAdapters.size() >= this.mServerResponseWrapper.getMaxRVAdapters() && !this.isBackFillAvailable()) {
            this.mIsAdAvailable = false;
            shouldNotify = !this.mDidReportInitialAvailability;
        }
        return shouldNotify;
    }

    private synchronized void addToAvailable(AbstractAdapter adapter, boolean forceOrder) {
        String adapterAlgorithm = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoAdapterAlgorithm();
        int priorityLocation = this.mAvailableAdapters.size();
        if (!this.mAvailableAdapters.contains(adapter)) {
            if ("KTO".equalsIgnoreCase(adapterAlgorithm) || forceOrder) {
                for (AbstractAdapter rwa : this.mAvailableAdapters) {
                    if (adapter.getRewardedVideoPriority() > rwa.getRewardedVideoPriority()) continue;
                    priorityLocation = this.mAvailableAdapters.indexOf(rwa);
                    break;
                }
            }
            this.mAvailableAdapters.add(priorityLocation, adapter);
        }
    }

    private synchronized void removeFromAvailable(AbstractAdapter adapter) {
        if (this.mAvailableAdapters.contains(adapter)) {
            this.mAvailableAdapters.remove(adapter);
        }
    }

    private synchronized void addToNotAvailable(AbstractAdapter adapter) {
        if (!this.mNotAvailableAdapters.contains(adapter)) {
            this.mNotAvailableAdapters.add(adapter);
        }
    }

    private synchronized void removeFromUnavailable(AbstractAdapter adapter) {
        if (this.mNotAvailableAdapters.contains(adapter)) {
            this.mNotAvailableAdapters.remove(adapter);
        }
    }

    private synchronized void addToInitiated(AbstractAdapter adapter) {
        if (!this.mInitiatedAdapters.contains(adapter)) {
            this.mInitiatedAdapters.add(adapter);
        }
    }

    private synchronized void removeFromInitiated(AbstractAdapter adapter) {
        if (this.mInitiatedAdapters.contains(adapter)) {
            this.mInitiatedAdapters.remove(adapter);
        }
    }

    private synchronized void addToExhausted(AbstractAdapter adapter) {
        if (!this.mExhaustedAdapters.contains(adapter)) {
            this.mExhaustedAdapters.add(adapter);
        }
    }

    private synchronized void removeFromExhausted(AbstractAdapter adapter) {
        if (this.mExhaustedAdapters.contains(adapter)) {
            this.mExhaustedAdapters.remove(adapter);
        }
    }

    public synchronized void addAvailableRewardedVideoAdapter(AbstractAdapter adapter, boolean forceOrder) {
        this.addToAvailable(adapter, forceOrder);
        this.removeFromInitiated(adapter);
        this.removeFromUnavailable(adapter);
        this.removeFromExhausted(adapter);
    }

    private synchronized void addInitiatedRewardedVideoAdapter(AbstractAdapter adapter) {
        this.addToInitiated(adapter);
        this.removeFromUnavailable(adapter);
        this.removeFromAvailable(adapter);
        this.removeFromExhausted(adapter);
    }

    private synchronized void addUnavailableRewardedVideoAdapter(AbstractAdapter adapter) {
        this.addToNotAvailable(adapter);
        this.removeFromAvailable(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromExhausted(adapter);
    }

    private synchronized void addExhaustedRewardedVideoAdapter(AbstractAdapter adapter) {
        this.addToExhausted(adapter);
        this.removeFromAvailable(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromUnavailable(adapter);
    }

    @Override
    public void onRewardedVideoAdStarted(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdStarted()", 1);
        EventData event = new EventData(8, IronSourceUtils.getProviderAdditionalData(adapter));
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onRewardedVideoAdStarted();
    }

    @Override
    public void onRewardedVideoAdEnded(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdEnded()", 1);
        EventData event = new EventData(9, IronSourceUtils.getProviderAdditionalData(adapter));
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onRewardedVideoAdEnded();
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement, AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onRewardedVideoAdRewarded(" + placement + ")", 1);
        if (placement == null) {
            placement = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getDefaultRewardedVideoPlacement();
        }
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("placement", (Object)placement.getPlacementName());
            data.put("rewardName", (Object)placement.getRewardName());
            data.put("rewardAmount", placement.getRewardAmount());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(10, data);
        if (!TextUtils.isEmpty((CharSequence)this.mAppKey)) {
            String strToTransId = "" + Long.toString(event.getTimeStamp()) + this.mAppKey + adapter.getProviderName();
            String transId = IronSourceUtils.getTransId(strToTransId);
            event.addToAdditionalData("transId", transId);
            if (!TextUtils.isEmpty((CharSequence)IronSourceObject.getInstance().getDynamicUserId())) {
                event.addToAdditionalData("dynamicUserId", IronSourceObject.getInstance().getDynamicUserId());
            }
        }
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onRewardedVideoAdRewarded(placement);
    }

    private synchronized void notifyIsAdAvailableForStatistics() {
        boolean mediationStatus = false;
        if (this.mAvailableAdapters != null && this.mAvailableAdapters.size() > 0) {
            mediationStatus = true;
        }
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("status", (Object)String.valueOf(mediationStatus));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(3, data);
        RewardedVideoEventsManager.getInstance().log(event);
        ArrayList<AbstractAdapter> tempAvailableAdapter = new ArrayList<AbstractAdapter>(this.mAvailableAdapters);
        for (AbstractAdapter availableAdapter : tempAvailableAdapter) {
            JSONObject availableData = IronSourceUtils.getProviderAdditionalData(availableAdapter);
            try {
                availableData.put("status", (Object)"true");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData availableEvent = new EventData(3, availableData);
            RewardedVideoEventsManager.getInstance().log(availableEvent);
        }
        for (AbstractAdapter notavailableAdapter : this.mNotAvailableAdapters) {
            if (this.isPremiumAdapter(notavailableAdapter.getProviderName()) && !this.canShowPremium()) continue;
            JSONObject notAvailableData = IronSourceUtils.getProviderAdditionalData(notavailableAdapter);
            try {
                notAvailableData.put("status", (Object)"false");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData notAvailableEvent = new EventData(3, notAvailableData);
            RewardedVideoEventsManager.getInstance().log(notAvailableEvent);
        }
        for (AbstractAdapter initiatedAdapter : this.mInitiatedAdapters) {
            JSONObject initiatedData = IronSourceUtils.getProviderAdditionalData(initiatedAdapter);
            try {
                initiatedData.put("status", (Object)"false");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData initiatedEvent = new EventData(3, initiatedData);
            RewardedVideoEventsManager.getInstance().log(initiatedEvent);
        }
        if (this.mBackFillAdapter != null) {
            JSONObject backFillData = IronSourceUtils.getProviderAdditionalData(this.mBackFillAdapter);
            try {
                backFillData.put("status", (Object)(this.isBackFillAvailable() ? "true" : "false"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData backFillEvent = new EventData(3, backFillData);
            RewardedVideoEventsManager.getInstance().log(backFillEvent);
        }
    }

    @Override
    void shouldTrackNetworkState(Context context, boolean track) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, this.TAG + " Should Track Network State: " + track, 0);
        this.mShouldTrackNetworkState = track;
        if (this.mShouldTrackNetworkState) {
            if (this.mNetworkStateReceiver == null) {
                this.mNetworkStateReceiver = new NetworkStateReceiver(context, this);
            }
            context.registerReceiver((BroadcastReceiver)this.mNetworkStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } else if (this.mNetworkStateReceiver != null) {
            context.unregisterReceiver((BroadcastReceiver)this.mNetworkStateReceiver);
        }
    }

    @Override
    public void onNetworkAvailabilityChanged(boolean connected) {
        if (this.mShouldTrackNetworkState) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, "Network Availability Changed To: " + connected, 0);
            if (this.shouldNotifyNetworkAvailabilityChanged(connected)) {
                this.mPauseSmartLoadDueToNetworkUnavailability = !connected;
                this.mListenersWrapper.onRewardedVideoAvailabilityChanged(connected);
            }
        }
    }

    private boolean shouldNotifyNetworkAvailabilityChanged(boolean networkState) {
        boolean shouldNotify = false;
        if (!this.mIsAdAvailable && networkState && this.mAvailableAdapters.size() > 0) {
            this.mIsAdAvailable = true;
            shouldNotify = true;
        } else if (this.mIsAdAvailable && !networkState) {
            this.mIsAdAvailable = false;
            shouldNotify = true;
        }
        return shouldNotify;
    }

    @Override
    public boolean isRewardedVideoPlacementCapped(String placementName) {
        return false;
    }

    public Placement getPlacementByName(String placementName) {
        if (this.mServerResponseWrapper == null || this.mServerResponseWrapper.getConfigurations() == null || this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations() == null) {
            return null;
        }
        Placement placement = null;
        try {
            placement = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoPlacement(placementName);
            if (placement == null && (placement = this.mServerResponseWrapper.getConfigurations().getRewardedVideoConfigurations().getDefaultRewardedVideoPlacement()) == null) {
                String noDefaultPlacement = "Default placement was not found";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return placement;
    }

    @Override
    protected synchronized boolean isBackFillAvailable() {
        if (this.mBackFillAdapter != null) {
            return this.mBackFillAdapter.isRewardedVideoAvailable();
        }
        return false;
    }

    @Override
    boolean isPremiumAdapter(String providerName) {
        String premiumAdapterName = this.mServerResponseWrapper.getRVPremiumProvider();
        if (!TextUtils.isEmpty((CharSequence)premiumAdapterName) && !TextUtils.isEmpty((CharSequence)providerName)) {
            return providerName.equals(premiumAdapterName);
        }
        return false;
    }

    @Override
    protected synchronized void disablePremiumForCurrentSession() {
        super.disablePremiumForCurrentSession();
        ArrayList<AbstractAdapter> tempAvailableAdapter = new ArrayList<AbstractAdapter>(this.mAvailableAdapters);
        for (AbstractAdapter adapter : tempAvailableAdapter) {
            String providerName = adapter.getProviderName();
            if (!this.isPremiumAdapter(providerName)) continue;
            this.moveAdapterToUnavailableAndLoadNext(adapter);
            return;
        }
        ArrayList<AbstractAdapter> tempExhaustedAdapter = new ArrayList<AbstractAdapter>(this.mExhaustedAdapters);
        for (AbstractAdapter adapter2 : tempExhaustedAdapter) {
            String providerName = adapter2.getProviderName();
            if (!this.isPremiumAdapter(providerName)) continue;
            this.moveAdapterToUnavailableAndLoadNext(adapter2);
            return;
        }
    }

    private synchronized void moveAdapterToUnavailableAndLoadNext(AbstractAdapter adapter) {
        this.addUnavailableRewardedVideoAdapter(adapter);
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Unavailable' list", 0);
        this.loadNextAdapter();
    }
}

