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
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.MediationInitializer;
import com.ironsource.mediationsdk.config.ConfigFile;
import com.ironsource.mediationsdk.events.InterstitialEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLogger.IronSourceTag;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.model.BannerConfigurations;
import com.ironsource.mediationsdk.model.BannerPlacement;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.sdk.BannerApi;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.BannerManagerListener;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class BannerManager
extends AbstractAdUnitManager
implements BannerApi,
BannerManagerListener,
MediationInitializer.OnMediationInitializationListener {
    private final String TAG;
    private static final long LOAD_FAILED_COOLDOWN_IN_MILLIS = 15000;
    private boolean mDidCallLoad;
    private boolean mLoadInProgress;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    LoadFailedRunnable mLoadFailedRunnable;
    private long mLastLoadFailTimestamp;
    private boolean mDidFinishToInitBanner;
    private ArrayList<AbstractAdapter> mNotInitAdapters;
    private ArrayList<AbstractAdapter> mLoadFailedAdapters;
    private ArrayList<AbstractAdapter> mInitiatedAdapters;
    private IronSourceBannerLayout mPendingToLoadBannerLayout;
    private AbstractAdapter mLoadingAdapter;
    private AbstractAdapter mReadyAdapter;
    private boolean isFirstLoad;

    public BannerManager() {
        this.TAG = this.getClass().getName();
        this.mDidCallLoad = false;
        this.mLoadInProgress = false;
        this.mHandlerThread = new HandlerThread("IronSourceBannerHandler");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mLastLoadFailTimestamp = 0;
        this.mNotInitAdapters = new ArrayList();
        this.mInitiatedAdapters = new ArrayList();
        this.mLoadFailedAdapters = new ArrayList();
        this.isFirstLoad = true;
    }

    public void initBanners(Activity activity, String appKey, String userId) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":initBanners(appKey: " + appKey + ", userId: " + userId + ")", 1);
        this.mAppKey = appKey;
        this.mUserId = userId;
        this.mActivity = activity;
        this.mServerResponseWrapper = IronSourceObject.getInstance().getCurrentServerResponse();
        if (this.mServerResponseWrapper != null) {
            this.startNextAdapter();
        }
    }

    private AbstractAdapter startNextAdapter() {
        AbstractAdapter initiatedAdapter = null;
        while (this.mServerResponseWrapper.hasMoreBannerProvidersToLoad() && initiatedAdapter == null) {
            initiatedAdapter = this.startAdapter(this.mServerResponseWrapper.getNextBannerProvider());
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
                sso.addToBannerAdaptersList(providerAdapter);
            }
            this.setCustomParams(providerAdapter);
            providerAdapter.setLogListener(this.mLoggerManager);
            providerAdapter.setBannerTimeout(this.mServerResponseWrapper.getConfigurations().getBannerConfigurations().getBannerAdaptersSmartLoadTimeout());
            providerAdapter.setBannerPriority(this.mServerResponseWrapper.getBannerAdaptersLoadPosition());
            providerAdapter.setBannerConfigurations(this.mServerResponseWrapper.getConfigurations().getBannerConfigurations());
            providerAdapter.setBannerListener(this);
            if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginType())) {
                providerAdapter.setPluginData(ConfigFile.getConfigFile().getPluginType(), ConfigFile.getConfigFile().getPluginFrameworkVersion());
            }
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":startAdapter(providerAdapter: " + providerAdapter.getProviderName(), 0);
            providerAdapter.initBanners(this.mActivity, this.mAppKey, this.mUserId);
        }
        catch (Throwable e) {
            IronSourceError error = ErrorBuilder.buildInitFailedError(providerName + " initialization failed - please verify that required dependencies are in you build path.", "Banner");
            this.mServerResponseWrapper.decreaseMaxBannerAdapters();
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":startAdapter", e);
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
    public boolean isBannerPlacementCapped(String placementName) {
        return false;
    }

    @Override
    public IronSourceBannerLayout createBanner(Activity activity, EBannerSize size) {
        IronSourceBannerLayout bannerLayout = new IronSourceBannerLayout(activity, size, this);
        return bannerLayout;
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner) {
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner, String placementName) {
        try {
            if (banner == null) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Load Banner can't be called on null object", 1);
                return;
            }
            if (banner.isDestoyed()) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Banner is already destroyed and can't be used anymore. Please create a new one using IronSource.createBanner API", 1);
                return;
            }
            if (this.mLoadInProgress) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Load Banner is already in progress", 1);
                return;
            }
            this.resetLoadRound(true);
            this.mDidCallLoad = true;
            this.mPendingToLoadBannerLayout = banner;
            this.mLoadInProgress = true;
            banner.setPlacementName(placementName);
            MediationInitializer.EInitStatus currentInitStatus = MediationInitializer.getInstance().getCurrentInitStatus();
            String loadFailMsg = "Load Banner can't be called before the Banner ad unit initialization completed successfully";
            if (currentInitStatus == MediationInitializer.EInitStatus.INIT_FAILED || currentInitStatus == MediationInitializer.EInitStatus.NOT_INIT) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildLoadFailedError(loadFailMsg), false);
                return;
            }
            if (currentInitStatus == MediationInitializer.EInitStatus.INIT_IN_PROGRESS) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildLoadFailedError(loadFailMsg), true);
                return;
            }
            if (!IronSourceUtils.isNetworkConnected((Context)this.mActivity)) {
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildNoInternetConnectionLoadFailError("Banner"), false);
                return;
            }
            if (this.mServerResponseWrapper == null || this.mInitiatedAdapters.size() == 0) {
                if (this.mServerResponseWrapper != null && !this.mDidFinishToInitBanner) {
                    return;
                }
                this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
                return;
            }
            if (this.mServerResponseWrapper != null) {
                this.isFirstLoad = false;
                BannerPlacement placement = this.validatePlacement(placementName);
                this.sendMediationLevelLoadEvent(banner, placement.getPlacementName());
                CappingManager.ECappingStatus status = IronSourceObject.getInstance().getBannerCappingStatus(placement.getPlacementName());
                String cappedMessage = IronSourceObject.getInstance().getCappingMessage(placement.getPlacementName(), status);
                if (!TextUtils.isEmpty((CharSequence)cappedMessage)) {
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, cappedMessage, 1);
                    this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildCappedError("Banner", cappedMessage), false);
                    return;
                }
                banner.setPlacementName(placement.getPlacementName());
            }
            AbstractAdapter adapter = this.mInitiatedAdapters.get(0);
            this.addLoadingBannerAdapter(adapter);
            this.loadAdapterAndSendEvent(adapter, banner);
        }
        catch (Exception e) {
            IronSourceError error = ErrorBuilder.buildLoadFailedError("loadBanner exception");
            this.sendOrScheduleLoadFailedCallback(error, false);
        }
    }

    private void sendMediationLevelLoadEvent(IronSourceBannerLayout banner, String placementName) {
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            int bannerSizeData = 0;
            if (banner != null) {
                bannerSizeData = banner.getSize().getValue();
            }
            data.put("bannerAdSize", bannerSizeData);
            data.put("placement", (Object)placementName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData event = new EventData(402, data);
        InterstitialEventsManager.getInstance().log(event);
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

    @Override
    public void onInitSuccess(List<IronSource.AD_UNIT> adUnits, boolean revived) {
    }

    @Override
    public void onInitFailed(String reason) {
        if (this.mDidCallLoad) {
            this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
        }
    }

    private synchronized void removeScheduledLoadFailedCallback() {
        if (this.mHandler != null && this.mLoadFailedRunnable != null) {
            this.mHandler.removeCallbacks((Runnable)this.mLoadFailedRunnable);
        }
    }

    private synchronized void resetLoadRound(boolean moveAdaptersToInitiated) {
        if (moveAdaptersToInitiated) {
            this.moveAdaptersToInitiated();
        }
        this.mLoadInProgress = false;
        this.mDidCallLoad = false;
        this.mPendingToLoadBannerLayout = null;
        if (this.mLoadFailedRunnable != null) {
            this.mHandler.removeCallbacks((Runnable)this.mLoadFailedRunnable);
        }
    }

    private synchronized void moveAdaptersToInitiated() {
        AbstractAdapter adapter;
        if (this.mReadyAdapter != null) {
            adapter = this.mReadyAdapter;
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
            this.addInitiatedBannerAdapter(adapter);
        }
        if (this.mLoadingAdapter != null) {
            adapter = this.mLoadingAdapter;
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
            this.addInitiatedBannerAdapter(adapter);
        }
        if (this.mLoadFailedAdapters.size() > 0) {
            Iterator it = ((ArrayList) this.mLoadFailedAdapters.clone()).iterator();
            while (it.hasNext()) {
                adapter = (AbstractAdapter) it.next();
                this.mLoggerManager.log(IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Initiated' list", 0);
                addInitiatedBannerAdapter(adapter);
            }
        }
    }

    @Override
    public void destroyBanner(IronSourceBannerLayout banner) {
        if (banner != null) {
            if (banner.isDestoyed()) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Banner is already destroyed and can't be used anymore. Please create a new one using IronSource.createBanner API", 1);
                return;
            }
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            EventData event = new EventData(406, data);
            InterstitialEventsManager.getInstance().log(event);
            this.mLoadInProgress = false;
            this.mDidCallLoad = false;
            banner.destroyBanner();
        }
    }

    @Override
    void shouldTrackNetworkState(Context context, boolean track) {
    }

    @Override
    boolean isPremiumAdapter(String providerName) {
        return false;
    }

    @Override
    boolean isBackFillAvailable() {
        return false;
    }

    private synchronized void loadAdapterAndSendEvent(AbstractAdapter adapter, IronSourceBannerLayout bannerLayout) {
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            if (bannerLayout != null && !TextUtils.isEmpty((CharSequence)bannerLayout.getPlacementName())) {
                data.put("placement", (Object)bannerLayout.getPlacementName());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData event = new EventData(402, data);
        InterstitialEventsManager.getInstance().log(event);
        adapter.loadBanner(bannerLayout);
    }

    @Override
    public synchronized void onBannerInitSuccess(AbstractAdapter adapter) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + " :onBannerInitSuccess()", 1);
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ": startAdapter(" + adapter.getProviderName() + ") moved to 'Initiated' list", 0);
        this.mDidFinishToInitBanner = true;
        if (this.mDidCallLoad && this.mReadyAdapter == null && this.mLoadingAdapter == null) {
            if (this.mServerResponseWrapper != null && this.mPendingToLoadBannerLayout != null && this.isFirstLoad) {
                this.isFirstLoad = false;
                BannerPlacement placement = this.validatePlacement(this.mPendingToLoadBannerLayout.getPlacementName());
                this.mPendingToLoadBannerLayout.setPlacementName(placement.getPlacementName());
                this.sendMediationLevelLoadEvent(this.mPendingToLoadBannerLayout, this.mPendingToLoadBannerLayout.getPlacementName());
                CappingManager.ECappingStatus status = IronSourceObject.getInstance().getBannerCappingStatus(this.mPendingToLoadBannerLayout.getPlacementName());
                String cappedMessage = IronSourceObject.getInstance().getCappingMessage(this.mPendingToLoadBannerLayout.getPlacementName(), status);
                if (!TextUtils.isEmpty((CharSequence)cappedMessage)) {
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, cappedMessage, 1);
                    this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildCappedError("Banner", cappedMessage), false);
                    return;
                }
            }
            this.addLoadingBannerAdapter(adapter);
            if (this.mPendingToLoadBannerLayout != null) {
                this.loadAdapterAndSendEvent(adapter, this.mPendingToLoadBannerLayout);
            }
        } else {
            this.addInitiatedBannerAdapter(adapter);
        }
    }

    @Override
    public synchronized void onBannerInitFailed(IronSourceError error, AbstractAdapter adapter) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, adapter.getProviderName() + ":onBannerInitFailed(" + error + ")", 1);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Not Ready' list", 0);
            this.addNotInitBannerAdapter(adapter);
            if (this.mNotInitAdapters.size() >= this.mServerResponseWrapper.getMaxBannerAdapters()) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - initialization failed - no adapters are initiated and no more left to init, error: " + error.getErrorMessage(), 2);
                if (this.mDidCallLoad) {
                    this.sendOrScheduleLoadFailedCallback(ErrorBuilder.buildGenericError("no ads to show"), false);
                }
                this.mDidFinishToInitBanner = true;
            } else {
                this.startNextAdapter();
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onBannerInitFailed(error:" + error + ", " + "provider:" + adapter.getProviderName() + ")", e);
        }
    }

    @Override
    public void onBannerImpression(AbstractAdapter adapter, IronSourceBannerLayout banner) {
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            int bannerSizeData = 0;
            if (banner != null) {
                bannerSizeData = banner.getSize().getValue();
            }
            data.put("bannerAdSize", bannerSizeData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData event = new EventData(405, data);
        InterstitialEventsManager.getInstance().log(event);
        this.moveAdaptersToInitiated();
    }

    @Override
    public void onBannerAdLoaded(AbstractAdapter adapter) {
        if (this.mDidCallLoad) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, "Smart Loading - " + adapter.getProviderName() + " moved to 'Ready' list", 0);
            this.addReadyBannerAdapter(adapter);
        }
        this.removeScheduledLoadFailedCallback();
        this.mLoadInProgress = false;
    }

    @Override
    public void onBannerAdLoadFailed(IronSourceError error, AbstractAdapter adapter) {
        boolean shouldReportFailed = false;
        this.addLoadFailedBannerAdapter(adapter);
        if (this.mReadyAdapter == null) {
            if (this.mInitiatedAdapters.size() > 0) {
                AbstractAdapter nextAdapter = this.mInitiatedAdapters.get(0);
                this.addLoadingBannerAdapter(nextAdapter);
                if (this.mPendingToLoadBannerLayout != null) {
                    this.loadAdapterAndSendEvent(nextAdapter, this.mPendingToLoadBannerLayout);
                }
            } else {
                AbstractAdapter initiatedAdapter = this.startNextAdapter();
                if (initiatedAdapter == null && this.mDidCallLoad && this.mReadyAdapter == null && this.mLoadingAdapter == null) {
                    shouldReportFailed = true;
                }
            }
        }
        if (shouldReportFailed) {
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("status", (Object)"false");
                data.put("errorCode", error.getErrorCode());
                if (this.mPendingToLoadBannerLayout != null && this.mPendingToLoadBannerLayout.getSize() != null) {
                    int bannerSizeData = this.mPendingToLoadBannerLayout.getSize().getValue();
                    data.put("bannerAdSize", bannerSizeData);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            EventData event = new EventData(407, data);
            InterstitialEventsManager.getInstance().log(event);
            this.sendOrScheduleLoadFailedCallback(error, false);
        }
    }

    @Override
    public void onBannerAdClicked(AbstractAdapter adapter) {
    }

    @Override
    public void onBannerAdScreenPresented(AbstractAdapter adapter) {
    }

    @Override
    public void onBannerAdScreenDismissed(AbstractAdapter adapter) {
    }

    @Override
    public void onBannerAdLeftApplication(AbstractAdapter adapter) {
    }

    public BannerPlacement getPlacementByName(String placementName) {
        if (this.mServerResponseWrapper == null || this.mServerResponseWrapper.getConfigurations() == null || this.mServerResponseWrapper.getConfigurations().getBannerConfigurations() == null) {
            return null;
        }
        BannerPlacement placement = null;
        try {
            placement = this.mServerResponseWrapper.getConfigurations().getBannerConfigurations().getBannerPlacement(placementName);
            if (placement == null && (placement = this.mServerResponseWrapper.getConfigurations().getBannerConfigurations().getDefaultBannerPlacement()) == null) {
                String noDefaultPlacement = "Default placement was not found";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return placement;
    }

    private synchronized void addInitiatedBannerAdapter(AbstractAdapter adapter) {
        this.addToInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromReady(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addReadyBannerAdapter(AbstractAdapter adapter) {
        this.addToReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addNotInitBannerAdapter(AbstractAdapter adapter) {
        this.addToNotInit(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromLoadFailed(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addLoadFailedBannerAdapter(AbstractAdapter adapter) {
        this.addToLoadFailed(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromLoading(adapter);
    }

    private synchronized void addLoadingBannerAdapter(AbstractAdapter adapter) {
        this.addToLoading(adapter);
        this.removeFromReady(adapter);
        this.removeFromInitiated(adapter);
        this.removeFromNotInit(adapter);
        this.removeFromLoadFailed(adapter);
    }

    private synchronized void addToInitiated(AbstractAdapter adapter) {
        int priorityLocation = this.mInitiatedAdapters.size();
        if (!this.mInitiatedAdapters.contains(adapter)) {
            for (AbstractAdapter ia : this.mInitiatedAdapters) {
                if (adapter.getBannerPriority() > ia.getBannerPriority()) continue;
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
        this.mReadyAdapter = adapter;
    }

    private synchronized void removeFromReady(AbstractAdapter adapter) {
        if (this.mReadyAdapter != null && this.mReadyAdapter.equals(adapter)) {
            this.mReadyAdapter = null;
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
        this.mLoadingAdapter = adapter;
    }

    private synchronized void removeFromLoading(AbstractAdapter adapter) {
        if (this.mLoadingAdapter != null && this.mLoadingAdapter.equals(adapter)) {
            this.mLoadingAdapter = null;
        }
    }

    private BannerPlacement validatePlacement(String placementName) {
        BannerPlacement placement = this.mServerResponseWrapper.getConfigurations().getBannerConfigurations().getBannerPlacement(placementName);
        if (placement == null) {
            String noPlacementMessage = "Placement is not valid, please make sure you are using the right placements, using the default placement.";
            if (placementName != null) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noPlacementMessage, 3);
            }
            if ((placement = this.mServerResponseWrapper.getConfigurations().getBannerConfigurations().getDefaultBannerPlacement()) == null) {
                String noDefaultPlacement = "Default placement was not found, please make sure you are using the right placements.";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
            }
        }
        return placement;
    }

    private class LoadFailedRunnable
    implements Runnable {
        IronSourceError error;

        LoadFailedRunnable(IronSourceError error) {
            this.error = error;
        }

        @Override
        public void run() {
            BannerManager.this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Load Banner failed: " + this.error.getErrorMessage(), 1);
            BannerManager.this.mLastLoadFailTimestamp = System.currentTimeMillis();
            if (BannerManager.this.mPendingToLoadBannerLayout != null && BannerManager.this.mPendingToLoadBannerLayout.getBannerListener() != null) {
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdLoadFailed(), error: " + this.error.getErrorMessage(), 1);
                JSONObject data = IronSourceUtils.getMediationAdditionalData();
                try {
                    int bannerSizeData = BannerManager.this.mPendingToLoadBannerLayout.getSize().getValue();
                    data.put("status", (Object)"false");
                    data.put("errorCode", this.error.getErrorCode());
                    data.put("bannerAdSize", bannerSizeData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                EventData event = new EventData(407, data);
                InterstitialEventsManager.getInstance().log(event);
                BannerManager.this.mPendingToLoadBannerLayout.getBannerListener().onBannerAdLoadFailed(this.error);
            }
            BannerManager.this.resetLoadRound(true);
        }
    }

}

