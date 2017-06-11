/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.os.Handler
 *  android.os.HandlerThread
 *  android.os.Looper
 *  android.text.TextUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.AbstractAdUnitManager;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.MediationInitializer;
import com.ironsource.mediationsdk.config.ConfigFile;
import com.ironsource.mediationsdk.events.InterstitialEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLogger.IronSourceTag;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.InterstitialConfigurations;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.sdk.InterstitialApi;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.InterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialApi;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialManagerListener;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

class InterstitialManager
extends AbstractAdUnitManager
implements InterstitialApi,
InterstitialManagerListener,
MediationInitializer.OnMediationInitializationListener,
RewardedInterstitialManagerListener,
RewardedInterstitialApi {
    private final String TAG;
    private static final long LOAD_FAILED_COOLDOWN_IN_MILLIS = 15000;
    private ArrayList<AbstractAdapter> mInitiatedAdapters;
    private ArrayList<AbstractAdapter> mNotInitAdapters;
    private ArrayList<AbstractAdapter> mLoadingAdapters;
    private ArrayList<AbstractAdapter> mReadyAdapters;
    private ArrayList<AbstractAdapter> mLoadFailedAdapters;
    private ArrayList<AbstractAdapter> mExhaustedAdapters;
    private InterstitialListener mInterstitialListenersWrapper;
    private RewardedInterstitialListener mRewardedInterstitialListenerWrapper;
    private int mNumberOfAdaptersToLoad;
    private boolean mDidCallLoad;
    private String mLastPlacementForShowFail;
    private boolean mLoadInProgress;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    LoadFailedRunnable mLoadFailedRunnable;
    private long mLastLoadFailTimestamp;
    private boolean mDidFinishToInitInterstitial;

    public InterstitialManager() {
        this.TAG = this.getClass().getName();
        this.mNumberOfAdaptersToLoad = 1;
        this.mDidCallLoad = false;
        this.mLastPlacementForShowFail = "";
        this.mLoadInProgress = false;
        this.prepareStateForInit();
    }

    private void prepareStateForInit() {
        this.mReadyAdapters = new ArrayList();
        this.mInitiatedAdapters = new ArrayList();
        this.mNotInitAdapters = new ArrayList();
        this.mExhaustedAdapters = new ArrayList();
        this.mLoadFailedAdapters = new ArrayList();
        this.mLoadingAdapters = new ArrayList();
        this.mHandlerThread = new HandlerThread("IronSourceInterstitialHandler");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mLastLoadFailTimestamp = 0;
    }

    @Override
    void shouldTrackNetworkState(Context context, boolean track) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, this.TAG + " Should Track Network State: " + track, 0);
        this.mShouldTrackNetworkState = track;
    }

    @Override
    boolean isBackFillAvailable() {
        if (this.mBackFillAdapter != null) {
            return this.mBackFillAdapter.isInterstitialReady();
        }
        return false;
    }

    @Override
    boolean isPremiumAdapter(String providerName) {
        String premiumAdapterName = this.mServerResponseWrapper.getISPremiumProvider();
        if (!TextUtils.isEmpty((CharSequence)premiumAdapterName) && !TextUtils.isEmpty((CharSequence)providerName)) {
            return providerName.equals(premiumAdapterName);
        }
        return false;
    }

    @Override
    public synchronized void initInterstitial(Activity activity, String appKey, String userId) {
        this.removeScheduledLoadFailedCallback();
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":initInterstitial(appKey: " + appKey + ", userId: " + userId + ")", 1);
        this.mAppKey = appKey;
        this.mUserId = userId;
        this.mActivity = activity;
        this.mServerResponseWrapper = IronSourceObject.getInstance().getCurrentServerResponse();
        if (this.mServerResponseWrapper != null) {
            AbstractAdapter lastLoadedAdapter;
            int numOfAdaptersToLoad;
            this.mNumberOfAdaptersToLoad = numOfAdaptersToLoad = this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations().getInterstitialAdaptersSmartLoadAmount();
            for (int i = 0; i < numOfAdaptersToLoad && (lastLoadedAdapter = this.startNextAdapter()) != null; ++i) {
            }
        }
    }

    @Override
    public synchronized void loadInterstitial() {
        try {
            int i;
            AbstractAdapter adapter;
            if (this.mLoadInProgress) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Load Interstitial is already in progress", 1);
                return;
            }
            this.resetLoadRound(true);
            this.mDidCallLoad = true;
            this.mLoadInProgress = true;
            EventData event = new EventData(22, IronSourceUtils.getMediationAdditionalData());
            InterstitialEventsManager.getInstance().log(event);
            MediationInitializer.EInitStatus currentInitStatus = MediationInitializer.getInstance().getCurrentInitStatus();
            String loadFailMsg = "Load Interstitial can't be called before the Interstitial ad unit initialization completed successfully";
            if (currentInitStatus == MediationInitializer.EInitStatus.INIT_FAILED || currentInitStatus == MediationInitializer.EInitStatus.NOT_INIT) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildLoadFailedError(loadFailMsg), false);
                return;
            }
            if (currentInitStatus == MediationInitializer.EInitStatus.INIT_IN_PROGRESS) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildLoadFailedError(loadFailMsg), true);
                return;
            }
            if (!IronSourceUtils.isNetworkConnected((Context)this.mActivity)) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildNoInternetConnectionLoadFailError("Interstitial"), false);
                return;
            }
            if (this.mServerResponseWrapper == null || this.mInitiatedAdapters.size() == 0) {
                if (this.mServerResponseWrapper != null && !this.mDidFinishToInitInterstitial) {
                    return;
                }
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
                return;
            }
            ArrayList tempInitiatedAdaptersList = (ArrayList)this.mInitiatedAdapters.clone();
            for (i = 0; i < this.mNumberOfAdaptersToLoad && i < tempInitiatedAdaptersList.size(); ++i) {
                adapter = (AbstractAdapter)tempInitiatedAdaptersList.get(i);
                this.addLoadingInterstitialAdapter(adapter);
            }
            for (i = 0; i < this.mNumberOfAdaptersToLoad && i < tempInitiatedAdaptersList.size(); ++i) {
                adapter = (AbstractAdapter)tempInitiatedAdaptersList.get(i);
                this.loadAdapterAndSendEvent(adapter);
            }
        }
        catch (Exception e) {
            IronSourceError error = ErrorBuilder.buildLoadFailedError("loadInterstitial exception");
            this.sendOrScheduleLoadFailedCallback(error, false);
        }
    }

    private synchronized void sendOrScheduleLoadFailedCallback(IronSourceError error, boolean shouldWait) {
        long timeFromPreviousLoadFailed;
        this.removeScheduledLoadFailedCallback();
        this.mLoadFailedRunnable = new LoadFailedRunnable(error);
        long currentTimestamp = System.currentTimeMillis();
        long l = timeFromPreviousLoadFailed = shouldWait ? 0 : currentTimestamp - this.mLastLoadFailTimestamp;
        if (timeFromPreviousLoadFailed >= 15000) {
            if (this.mHandler != null) {
                this.mHandler.post((Runnable)this.mLoadFailedRunnable);
            }
        } else {
            long timeToNextLoadFailed = 15000 - timeFromPreviousLoadFailed;
            if (this.mHandler != null) {
                this.mHandler.postDelayed((Runnable)this.mLoadFailedRunnable, timeToNextLoadFailed);
            }
        }
    }

    private synchronized void removeScheduledLoadFailedCallback() {
        if (this.mHandler != null && this.mLoadFailedRunnable != null) {
            this.mHandler.removeCallbacks((Runnable)this.mLoadFailedRunnable);
        }
    }

    @Override
    public void onInterstitialAdRewarded(AbstractAdapter adapter) {
        EventData event = new EventData(290, IronSourceUtils.getProviderAdditionalData(adapter));
        InterstitialEventsManager.getInstance().log(event);
        if (this.mRewardedInterstitialListenerWrapper != null) {
            this.mRewardedInterstitialListenerWrapper.onInterstitialAdRewarded();
        }
    }

    private synchronized void loadAdapterAndSendEvent(AbstractAdapter adapter) {
        EventData event = new EventData(22, IronSourceUtils.getProviderAdditionalData(adapter));
        InterstitialEventsManager.getInstance().log(event);
        adapter.loadInterstitial();
    }

    private synchronized void resetLoadRound(boolean moveAdaptersToInitiated) {
        if (moveAdaptersToInitiated) {
            this.moveAdaptersToInitiated();
        }
        this.mLoadInProgress = false;
        this.mDidCallLoad = false;
        if (this.mLoadFailedRunnable != null) {
            this.mHandler.removeCallbacks((Runnable)this.mLoadFailedRunnable);
        }
    }

    private synchronized void moveAdaptersToInitiated() {
        Iterator it;
        if (this.mReadyAdapters.size() > 0) {
            it = ((ArrayList) this.mReadyAdapters.clone()).iterator();
            while (it.hasNext()) {
                AbstractAdapter adapter = (AbstractAdapter) it.next();
                this.mLoggerManager.log(IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
                addInitiatedInterstitialAdapter(adapter);
            }
        }
        if (this.mLoadingAdapters.size() > 0) {
            it = ((ArrayList) this.mLoadingAdapters.clone()).iterator();
            while (it.hasNext()) {
                AbstractAdapter adapter = (AbstractAdapter) it.next();
                this.mLoggerManager.log(IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
                addInitiatedInterstitialAdapter(adapter);
            }
        }
        if (this.mLoadFailedAdapters.size() > 0) {
            it = ((ArrayList) this.mLoadFailedAdapters.clone()).iterator();
            while (it.hasNext()) {
                AbstractAdapter adapter = (AbstractAdapter) it.next();
                this.mLoggerManager.log(IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
                addInitiatedInterstitialAdapter(adapter);
            }
        }
    }

    @Override
    public synchronized void showInterstitial() {
    }

    @Override
    public void showInterstitial(String placementName) {
        if (this.mShouldTrackNetworkState && this.mActivity != null && !IronSourceUtils.isNetworkConnected((Context)this.mActivity)) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, this.TAG + ":showInterstitial fail - no internet connection", 2);
            this.mInterstitialListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildNoInternetConnectionShowFailError("Interstitial"));
            return;
        }
        if (!this.mDidCallLoad) {
            this.mInterstitialListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildShowFailedError("Interstitial", "showInterstitial failed - You need to load interstitial before showing it"));
            return;
        }
        if (this.mReadyAdapters == null || this.mReadyAdapters.size() == 0) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, this.TAG + ":No adapters to show", 2);
            this.mInterstitialListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildShowFailedError("Interstitial", "showInterstitial failed - No adapters ready to show"));
            return;
        }
        this.mLastPlacementForShowFail = placementName;
        AbstractAdapter adapter = this.mReadyAdapters.get(0);
        if (adapter == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, this.TAG + ":No adapters to show", 2);
            this.mInterstitialListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildShowFailedError("Interstitial", "showInterstitial failed - No adapters ready to show"));
            return;
        }
        adapter.increaseNumberOfAdsPlayed();
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, adapter.getProviderName() + ": " + adapter.getNumberOfAdsPlayed() + "/" + adapter.getMaxISAdsPerIteration() + " ads played", 0);
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("placement", (Object)placementName);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(23, data);
        InterstitialEventsManager.getInstance().log(event);
        adapter.showInterstitial(placementName);
        CappingManager.incrementShowCounter((Context)this.mActivity, this.getPlacementByName(placementName));
        this.resetLoadRound(false);
    }

    @Override
    public void setInterstitialListener(InterstitialListener listener) {
        this.mInterstitialListenersWrapper = listener;
    }

    @Override
    public void setRewardedInterstitialListener(RewardedInterstitialListener listener) {
        this.mRewardedInterstitialListenerWrapper = listener;
    }

    private boolean isIterationRoundComplete() {
        boolean isComplete = this.allAdaptersAreInTheLoop() && this.mReadyAdapters.size() == 0 && this.mInitiatedAdapters.size() == 0 && this.mLoadingAdapters.size() == 0;
        return isComplete;
    }

    private boolean allAdaptersAreInTheLoop() {
        int totalNumOfAdapters = this.mServerResponseWrapper.getMaxISAdapters();
        boolean areInTheLoop = this.mNotInitAdapters.size() + this.mExhaustedAdapters.size() + this.mInitiatedAdapters.size() + this.mLoadingAdapters.size() + this.mLoadFailedAdapters.size() + this.mReadyAdapters.size() == totalNumOfAdapters;
        return areInTheLoop;
    }

    private void completeIterationRound() {
        this.mLoggerManager.log(IronSourceTag.INTERNAL, "Reset Iteration", 0);
        Iterator it = ((ArrayList) this.mExhaustedAdapters.clone()).iterator();
        while (it.hasNext()) {
            AbstractAdapter exhaustedAdapter = (AbstractAdapter) it.next();
            this.mLoggerManager.log(IronSourceTag.INTERNAL, exhaustedAdapter.getProviderName() + ": " + "moved to 'Initiated' list", 0);
            addInitiatedInterstitialAdapter(exhaustedAdapter);
            exhaustedAdapter.resetNumberOfAdsPlayed();
        }
        this.mLoggerManager.log(IronSourceTag.INTERNAL, "End of Reset Iteration", 0);
    }

    private void completeAdapterIteration(AbstractAdapter adapter) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":completeIteration", 1);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Exhausted' list", 0);
            this.addExhaustedInterstitialAdapter(adapter);
            if (this.mInitiatedAdapters.size() + this.mReadyAdapters.size() < this.mNumberOfAdaptersToLoad) {
                this.startNextAdapter();
            }
            adapter.resetNumberOfAdsPlayed();
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "completeIteration(provider:" + adapter.getProviderName() + ")", e);
        }
    }

    private void completeAdapterShow(AbstractAdapter adapter) {
        if (adapter.getNumberOfAdsPlayed() == adapter.getMaxISAdsPerIteration()) {
            this.completeAdapterIteration(adapter);
            if (this.isIterationRoundComplete()) {
                this.completeIterationRound();
            }
        } else {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
            this.addInitiatedInterstitialAdapter(adapter);
        }
    }

    private AbstractAdapter startNextAdapter() {
        AbstractAdapter initiatedAdapter = null;
        while (this.mServerResponseWrapper.hasMoreISProvidersToLoad() && initiatedAdapter == null) {
            initiatedAdapter = this.startAdapter(this.mServerResponseWrapper.getNextISProvider());
        }
        return initiatedAdapter;
    }

    private AbstractAdapter startAdapter(String providerName) {
        AbstractAdapter providerAdapter;
        if (TextUtils.isEmpty((CharSequence)providerName)) {
            return null;
        }
        ProviderSettings providerSettings = this.mServerResponseWrapper.getProviderSettingsHolder().getProviderSettings(providerName);
        if (providerSettings == null) {
            return null;
        }
        String requestUrl = "";
        if (providerSettings.getRewardedVideoSettings() != null) {
            requestUrl = providerSettings.getRewardedVideoSettings().optString("requestUrl");
        }
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":startAdapter(" + providerName + ")", 1);
        try {
            Class mAdapterClass;
            Method startAdapterMethod;
            IronSourceObject sso = IronSourceObject.getInstance();
            providerAdapter = sso.getExistingAdapter(providerName);
            if (providerAdapter == null && (providerAdapter = (AbstractAdapter)(startAdapterMethod = (mAdapterClass = Class.forName("com.ironsource.adapters." + providerName.toLowerCase() + "." + providerName + "Adapter")).getMethod("startAdapter", String.class, String.class)).invoke(mAdapterClass, providerName, requestUrl)) != null) {
                sso.addToISAdaptersList(providerAdapter);
            }
            if (providerAdapter.getMaxISAdsPerIteration() < 1) {
                return null;
            }
            this.setCustomParams(providerAdapter);
            providerAdapter.setLogListener(this.mLoggerManager);
            providerAdapter.setInterstitialTimeout(this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations().getInterstitialAdaptersSmartLoadTimeout());
            providerAdapter.setInterstitialPriority(this.mServerResponseWrapper.getISAdaptersLoadPosition());
            providerAdapter.setInterstitialConfigurations(this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations());
            if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginType())) {
                providerAdapter.setPluginData(ConfigFile.getConfigFile().getPluginType(), ConfigFile.getConfigFile().getPluginFrameworkVersion());
            }
            providerAdapter.setInterstitialListener(this);
            if (this.mRewardedInterstitialListenerWrapper != null) {
                providerAdapter.setRewardedInterstitialListener(this);
            }
            providerAdapter.initInterstitial(this.mActivity, this.mAppKey, this.mUserId);
        }
        catch (Throwable e) {
            IronSourceError error = ErrorBuilder.buildInitFailedError(providerName + " initialization failed - please verify that required dependencies are in you build path.", "Interstitial");
            this.mServerResponseWrapper.decreaseMaxISAdapters();
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":startAdapter", e);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, error.toString(), 2);
            return null;
        }
        return providerAdapter;
    }

    private synchronized void addInitiatedInterstitialAdapter(AbstractAdapter adapter) {
        this.addToInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromReady(adapter);
        this.removeFromExhausted(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addReadyInterstitialAdapter(AbstractAdapter adapter) {
        this.addToReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromExhausted(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addNotInitInterstitialAdapter(AbstractAdapter adapter) {
        this.addToNotInit(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromExhausted(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addExhaustedInterstitialAdapter(AbstractAdapter adapter) {
        this.addToExhausted(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addLoadFailedInterstitialAdapter(AbstractAdapter adapter) {
        this.addToLoadFailed(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromExhausted(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addLoadingInterstitialAdapter(AbstractAdapter adapter) {
        this.addToLoading(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromExhausted(adapter);
        this.removeFromLoadFailed(adapter);
    }

    private synchronized void addToInitiated(AbstractAdapter adapter) {
        int priorityLocation = this.mInitiatedAdapters.size();
        if (!this.mInitiatedAdapters.contains(adapter)) {
            for (AbstractAdapter ia : this.mInitiatedAdapters) {
                if (adapter.getInterstitialPriority() > ia.getInterstitialPriority()) continue;
                priorityLocation = this.mInitiatedAdapters.indexOf(ia);
                break;
            }
            this.mInitiatedAdapters.add(priorityLocation, adapter);
        }
    }

    private synchronized void removeFromInitiated(AbstractAdapter adapter) {
        if (this.mInitiatedAdapters.contains(adapter)) {
            this.mInitiatedAdapters.remove(adapter);
        }
    }

    private synchronized void addToReady(AbstractAdapter adapter) {
        int priorityLocation = this.mReadyAdapters.size();
        if (!this.mReadyAdapters.contains(adapter)) {
            for (AbstractAdapter ia : this.mReadyAdapters) {
                if (adapter.getInterstitialPriority() > ia.getInterstitialPriority()) continue;
                priorityLocation = this.mReadyAdapters.indexOf(ia);
                break;
            }
            this.mReadyAdapters.add(priorityLocation, adapter);
        }
    }

    private synchronized void removeFromReady(AbstractAdapter adapter) {
        if (this.mReadyAdapters.contains(adapter)) {
            this.mReadyAdapters.remove(adapter);
        }
    }

    private synchronized void addToNotInit(AbstractAdapter adapter) {
        if (!this.mNotInitAdapters.contains(adapter)) {
            this.mNotInitAdapters.add(adapter);
        }
    }

    private synchronized void removeFromNotInit(AbstractAdapter adapter) {
        if (this.mNotInitAdapters.contains(adapter)) {
            this.mNotInitAdapters.remove(adapter);
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

    private synchronized void addToLoadFailed(AbstractAdapter adapter) {
        if (!this.mLoadFailedAdapters.contains(adapter)) {
            this.mLoadFailedAdapters.add(adapter);
        }
    }

    private synchronized void removeFromLoadFailed(AbstractAdapter adapter) {
        if (this.mLoadFailedAdapters.contains(adapter)) {
            this.mLoadFailedAdapters.remove(adapter);
        }
    }

    private synchronized void addToLoading(AbstractAdapter adapter) {
        if (!this.mLoadingAdapters.contains(adapter)) {
            this.mLoadingAdapters.add(adapter);
        }
    }

    private synchronized void removeFromLoading(AbstractAdapter adapter) {
        if (this.mLoadingAdapters.contains(adapter)) {
            this.mLoadingAdapters.remove(adapter);
        }
    }

    @Override
    public synchronized void onInterstitialInitSuccess(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + " :onInterstitialInitSuccess()", 1);
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ": startAdapter(" + adapter.getProviderName() + ") moved to 'Initiated' list", 0);
        this.addInitiatedInterstitialAdapter(adapter);
        this.mDidFinishToInitInterstitial = true;
        if (this.mDidCallLoad && this.mReadyAdapters.size() + this.mLoadingAdapters.size() < this.mNumberOfAdaptersToLoad) {
            this.addLoadingInterstitialAdapter(adapter);
            this.loadAdapterAndSendEvent(adapter);
        }
    }

    @Override
    public synchronized void onInterstitialInitFailed(IronSourceError error, AbstractAdapter adapter) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialInitFailed(" + error + ")", 1);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Not Ready' list", 0);
            this.addNotInitInterstitialAdapter(adapter);
            if (this.mNotInitAdapters.size() >= this.mServerResponseWrapper.getMaxISAdapters()) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - initialization failed - no adapters are initiated and no more left to init, error: " + error.getErrorMessage(), 2);
                if (this.mDidCallLoad) {
                    this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
                }
                this.mDidFinishToInitInterstitial = true;
            } else {
                this.startNextAdapter();
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onInterstitialInitFailed(error:" + error + ", " + "provider:" + adapter.getProviderName() + ")", e);
        }
    }

    @Override
    public synchronized void onInterstitialAdReady(AbstractAdapter adapter) {
        boolean shouldReportReady = false;
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdReady()", 1);
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("status", (Object)"true");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(27, data);
        InterstitialEventsManager.getInstance().log(event);
        if (this.mDidCallLoad) {
            if (this.mReadyAdapters.size() == 0) {
                shouldReportReady = true;
            }
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Ready' list", 0);
            this.addReadyInterstitialAdapter(adapter);
        }
        this.removeScheduledLoadFailedCallback();
        this.mLoadInProgress = false;
        if (shouldReportReady) {
            this.mInterstitialListenersWrapper.onInterstitialAdReady();
        }
    }

    @Override
    public synchronized void onInterstitialAdLoadFailed(IronSourceError error, AbstractAdapter adapter) {
        boolean shouldReportFailed = false;
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdLoadFailed(" + error + ")", 1);
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            data.put("status", (Object)"false");
            data.put("errorCode", error.getErrorCode());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(27, data);
        InterstitialEventsManager.getInstance().log(event);
        this.addLoadFailedInterstitialAdapter(adapter);
        if (this.mReadyAdapters.size() < this.mNumberOfAdaptersToLoad) {
            if (this.mInitiatedAdapters.size() > 0) {
                AbstractAdapter nextAdapter = this.mInitiatedAdapters.get(0);
                this.addLoadingInterstitialAdapter(nextAdapter);
                this.loadAdapterAndSendEvent(nextAdapter);
            } else {
                AbstractAdapter initiatedAdapter = this.startNextAdapter();
                if (initiatedAdapter == null && this.mDidCallLoad && this.mReadyAdapters.size() == 0 && this.mLoadingAdapters.size() == 0) {
                    shouldReportFailed = true;
                    if (this.isIterationRoundComplete()) {
                        this.completeIterationRound();
                    }
                }
            }
        }
        if (shouldReportFailed) {
            this.sendOrScheduleLoadFailedCallback(error, false);
        }
    }

    @Override
    public void onInterstitialAdOpened(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdOpened()", 1);
        EventData event = new EventData(25, IronSourceUtils.getProviderAdditionalData(adapter));
        InterstitialEventsManager.getInstance().log(event);
        this.mInterstitialListenersWrapper.onInterstitialAdOpened();
    }

    @Override
    public void onInterstitialAdClosed(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdClosed()", 1);
        EventData event = new EventData(26, IronSourceUtils.getProviderAdditionalData(adapter));
        InterstitialEventsManager.getInstance().log(event);
        this.mInterstitialListenersWrapper.onInterstitialAdClosed();
    }

    @Override
    public void onInterstitialAdShowSucceeded(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdShowSucceeded()", 1);
        if (this.mReadyAdapters.size() > 0) {
            Iterator it = ((ArrayList) this.mReadyAdapters.clone()).iterator();
            while (it.hasNext()) {
                completeAdapterShow((AbstractAdapter) it.next());
            }
        }
        moveAdaptersToInitiated();
        this.mInterstitialListenersWrapper.onInterstitialAdShowSucceeded();
    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError error, AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdShowFailed(" + error + ")", 1);
        this.completeAdapterShow(adapter);
        if (this.mReadyAdapters.size() > 0) {
            this.mDidCallLoad = true;
            this.showInterstitial(this.mLastPlacementForShowFail);
        } else {
            this.mInterstitialListenersWrapper.onInterstitialAdShowFailed(error);
        }
    }

    @Override
    public void onInterstitialAdClicked(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onInterstitialAdClicked()", 1);
        EventData event = new EventData(28, IronSourceUtils.getProviderAdditionalData(adapter));
        InterstitialEventsManager.getInstance().log(event);
        this.mInterstitialListenersWrapper.onInterstitialAdClicked();
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
    public boolean isInterstitialReady() {
        if (this.mShouldTrackNetworkState && this.mActivity != null && !IronSourceUtils.isNetworkConnected((Context)this.mActivity)) {
            return false;
        }
        for (AbstractAdapter adapter : this.mReadyAdapters) {
            if (!adapter.isInterstitialReady()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isInterstitialPlacementCapped(String placementName) {
        return false;
    }

    public InterstitialPlacement getPlacementByName(String placementName) {
        if (this.mServerResponseWrapper == null || this.mServerResponseWrapper.getConfigurations() == null || this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations() == null) {
            return null;
        }
        InterstitialPlacement placement = null;
        try {
            placement = this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations().getInterstitialPlacement(placementName);
            if (placement == null && (placement = this.mServerResponseWrapper.getConfigurations().getInterstitialConfigurations().getDefaultInterstitialPlacement()) == null) {
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
    public void onInitSuccess(List<IronSource.AD_UNIT> adUnits, boolean revived) {
    }

    @Override
    public void onInitFailed(String reason) {
        if (this.mDidCallLoad) {
            this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
        }
    }

    private class LoadFailedRunnable
    implements Runnable {
        IronSourceError error;

        LoadFailedRunnable(IronSourceError error) {
            this.error = error;
        }

        @Override
        public void run() {
            InterstitialManager.this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Load Interstitial failed: " + this.error.getErrorMessage(), 1);
            InterstitialManager.this.mLastLoadFailTimestamp = System.currentTimeMillis();
            InterstitialManager.this.mInterstitialListenersWrapper.onInterstitialAdLoadFailed(this.error);
            InterstitialManager.this.resetLoadRound(true);
        }
    }

}

