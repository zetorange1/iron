/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.text.TextUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import com.ironsource.environment.DeviceStatus;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.BannerManager;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.InterstitialManager;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.MediationInitializer;
import com.ironsource.mediationsdk.OfferwallManager;
import com.ironsource.mediationsdk.RewardedVideoManager;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.events.InterstitialEventsManager;
import com.ironsource.mediationsdk.events.RewardedVideoEventsManager;
import com.ironsource.mediationsdk.events.SuperLooper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.logger.PublisherLogger;
import com.ironsource.mediationsdk.model.ApplicationConfigurations;
import com.ironsource.mediationsdk.model.ApplicationEvents;
import com.ironsource.mediationsdk.model.ApplicationLogger;
import com.ironsource.mediationsdk.model.BannerConfigurations;
import com.ironsource.mediationsdk.model.BannerPlacement;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.InterstitialConfigurations;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.OfferwallConfigurations;
import com.ironsource.mediationsdk.model.OfferwallPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;
import com.ironsource.mediationsdk.sdk.InternalOfferwallListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.IronSourceInterface;
import com.ironsource.mediationsdk.sdk.ListenersWrapper;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.server.HttpFunctions;
import com.ironsource.mediationsdk.server.ServerURL;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.GeneralPropertiesWorker;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;

public class IronSourceObject
implements IronSourceInterface,
MediationInitializer.OnMediationInitializationListener {
    private final String TAG;
    private ArrayList<AbstractAdapter> mRVAdaptersList;
    private ArrayList<AbstractAdapter> mISAdaptersList;
    private ArrayList<AbstractAdapter> mBannerAdaptersList;
    private RewardedVideoManager mRewardedVideoManager;
    private InterstitialManager mInterstitialManager;
    private OfferwallManager mOfferwallManager;
    private BannerManager mBannerManager;
    private IronSourceLoggerManager mLoggerManager;
    private ListenersWrapper mListenersWrapper;
    private PublisherLogger mPublisherLogger;
    private AtomicBoolean mEventManagersInit;
    private final Object serverResponseLocker;
    private ServerResponseWrapper currentServerResponse;
    private String mAppKey;
    private String mUserId;
    private Integer mUserAge;
    private String mUserGender;
    private String mSegment;
    private String mDynamicUserId;
    private String mMediationType;
    private Activity mActivity;
    private Set<IronSource.AD_UNIT> mRequestedAdUnits;
    private boolean mShouldSendGetInstanceEvent;
    private static IronSourceObject sInstance;

    public static synchronized IronSourceObject getInstance() {
        if (sInstance == null) {
            sInstance = new IronSourceObject();
        }
        return sInstance;
    }

    private IronSourceObject() {
        this.TAG = this.getClass().getName();
        this.serverResponseLocker = new Object();
        this.currentServerResponse = null;
        this.mAppKey = null;
        this.mUserId = null;
        this.mUserAge = null;
        this.mUserGender = null;
        this.mSegment = null;
        this.mDynamicUserId = null;
        this.mMediationType = null;
        this.mShouldSendGetInstanceEvent = true;
        this.initializeManagers();
        this.mEventManagersInit = new AtomicBoolean();
        this.mRVAdaptersList = new ArrayList();
        this.mISAdaptersList = new ArrayList();
        this.mBannerAdaptersList = new ArrayList();
        this.mRequestedAdUnits = new HashSet<IronSource.AD_UNIT>();
    }

    public synchronized /* varargs */ void init(Activity activity, String appKey, IronSource.AD_UNIT ... adUnits) {
        if (adUnits == null || adUnits.length == 0) {
            for (IronSource.AD_UNIT adUnit : IronSource.AD_UNIT.values()) {
                this.mRequestedAdUnits.add(adUnit);
            }
        } else {
            for (IronSource.AD_UNIT adUnit : adUnits) {
                this.mRequestedAdUnits.add(adUnit);
            }
        }
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "init(appKey:" + appKey + ")", 1);
        if (activity == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "Init Fail - provided activity is null", 2);
            return;
        }
        this.mActivity = activity;
        this.prepareEventManagers(activity);
        ConfigValidationResult validationResultAppKey = this.validateAppKey(appKey);
        if (!validationResultAppKey.isValid()) {
            if (this.mRequestedAdUnits.contains((Object)IronSource.AD_UNIT.REWARDED_VIDEO)) {
                this.mListenersWrapper.onRewardedVideoAvailabilityChanged(false);
            }
            if (this.mRequestedAdUnits.contains((Object)IronSource.AD_UNIT.OFFERWALL)) {
                this.mListenersWrapper.onOfferwallAvailable(false, validationResultAppKey.getIronSourceError());
            }
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, validationResultAppKey.getIronSourceError().toString(), 1);
            return;
        }
        this.setIronSourceAppKey(appKey);
        if (this.mShouldSendGetInstanceEvent) {
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                if (adUnits != null) {
                    for (IronSource.AD_UNIT adUnit : adUnits) {
                        data.put(adUnit.toString(), true);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            EventData instanceEvent = new EventData(14, data);
            RewardedVideoEventsManager.getInstance().log(instanceEvent);
            this.mShouldSendGetInstanceEvent = false;
        }
        if (this.mRequestedAdUnits.contains((Object)IronSource.AD_UNIT.INTERSTITIAL)) {
            MediationInitializer.getInstance().addMediationInitializationListener(this.mInterstitialManager);
        }
        MediationInitializer.getInstance().addMediationInitializationListener(this);
        MediationInitializer.getInstance().init(activity, appKey, this.mUserId, adUnits);
    }

    @Override
    public void onInitSuccess(List<IronSource.AD_UNIT> adUnits, boolean revived) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "onInitSuccess()", 1);
            if (revived) {
                JSONObject data = IronSourceUtils.getMediationAdditionalData();
                try {
                    data.put("revived", revived);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                EventData revivedEvent = new EventData(114, (JSONObject)data);
                RewardedVideoEventsManager.getInstance().log(revivedEvent);
            }
            InterstitialEventsManager.getInstance().triggerEventsSend();
            RewardedVideoEventsManager.getInstance().triggerEventsSend();
            for (IronSource.AD_UNIT adUnit : IronSource.AD_UNIT.values()) {
                if (!this.mRequestedAdUnits.contains((Object)adUnit)) continue;
                if (adUnits.contains((Object)adUnit)) {
                    switch (adUnit) {
                        case REWARDED_VIDEO: {
                            this.mRewardedVideoManager.initRewardedVideo(this.mActivity, this.getIronSourceAppKey(), this.getIronSourceUserId());
                            break;
                        }
                        case INTERSTITIAL: {
                            this.mInterstitialManager.initInterstitial(this.mActivity, this.getIronSourceAppKey(), this.getIronSourceUserId());
                            break;
                        }
                        case OFFERWALL: {
                            this.mOfferwallManager.initOfferwall(this.mActivity, this.getIronSourceAppKey(), this.getIronSourceUserId());
                            break;
                        }
                        case BANNER: {
                            this.mBannerManager.initBanners(this.mActivity, this.getIronSourceAppKey(), this.getIronSourceUserId());
                        }
                    }
                    continue;
                }
                this.notifyPublisherAboutInitFailed(adUnit);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitFailed(String reason) {
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "onInitFailed(reason:" + reason + ")", 1);
            if (this.mListenersWrapper != null) {
                for (IronSource.AD_UNIT adUnit : this.mRequestedAdUnits) {
                    this.notifyPublisherAboutInitFailed(adUnit);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyPublisherAboutInitFailed(IronSource.AD_UNIT adUnit) {
        switch (adUnit) {
            case REWARDED_VIDEO: {
                this.mListenersWrapper.onRewardedVideoAvailabilityChanged(false);
                break;
            }
            case INTERSTITIAL: {
                break;
            }
            case OFFERWALL: {
                this.mListenersWrapper.onOfferwallAvailable(false);
                break;
            }
        }
    }

    private void prepareEventManagers(Activity activity) {
        if (this.mEventManagersInit != null && this.mEventManagersInit.compareAndSet(false, true)) {
            SuperLooper.getLooper().post(new GeneralPropertiesWorker(activity.getApplicationContext()));
            InterstitialEventsManager.getInstance().start(activity.getApplicationContext());
            RewardedVideoEventsManager.getInstance().start(activity.getApplicationContext());
        }
    }

    public synchronized void addToAdaptersList(AbstractAdapter adapter) {
        if (this.mRVAdaptersList != null && adapter != null && !this.mRVAdaptersList.contains(adapter)) {
            this.mRVAdaptersList.add(adapter);
        }
    }

    public synchronized void addToISAdaptersList(AbstractAdapter adapter) {
        if (this.mISAdaptersList != null && adapter != null && !this.mISAdaptersList.contains(adapter)) {
            this.mISAdaptersList.add(adapter);
        }
    }

    public synchronized void addToBannerAdaptersList(AbstractAdapter adapter) {
        if (this.mBannerAdaptersList != null && adapter != null && !this.mBannerAdaptersList.contains(adapter)) {
            this.mBannerAdaptersList.add(adapter);
        }
    }

    public synchronized AbstractAdapter getExistingAdapter(String providerName) {
        try {
            if (this.mRVAdaptersList != null) {
                for (AbstractAdapter adapter : this.mRVAdaptersList) {
                    if (!adapter.getProviderName().equals(providerName)) continue;
                    return adapter;
                }
            }
            if (this.mISAdaptersList != null) {
                for (AbstractAdapter adapter : this.mISAdaptersList) {
                    if (!adapter.getProviderName().equals(providerName)) continue;
                    return adapter;
                }
            }
            if (this.mBannerAdaptersList != null) {
                for (AbstractAdapter adapter : this.mBannerAdaptersList) {
                    if (!adapter.getProviderName().equals(providerName)) continue;
                    return adapter;
                }
            }
        }
        catch (Exception e) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, "getExistingAdapter exception: " + e, 1);
        }
        return null;
    }

    private void initializeManagers() {
        this.mLoggerManager = IronSourceLoggerManager.getLogger(0);
        this.mPublisherLogger = new PublisherLogger(null, 1);
        this.mLoggerManager.addLogger(this.mPublisherLogger);
        this.mListenersWrapper = new ListenersWrapper();
        this.mRewardedVideoManager = new RewardedVideoManager();
        this.mRewardedVideoManager.setRewardedVideoListener(this.mListenersWrapper);
        this.mInterstitialManager = new InterstitialManager();
        this.mInterstitialManager.setInterstitialListener(this.mListenersWrapper);
        this.mInterstitialManager.setRewardedInterstitialListener(this.mListenersWrapper);
        this.mOfferwallManager = new OfferwallManager();
        this.mOfferwallManager.setInternalOfferwallListener(this.mListenersWrapper);
        this.mBannerManager = new BannerManager();
    }

    @Override
    public void onResume(Activity activity) {
        String logMessage = "onResume()";
        try {
            this.mActivity = activity;
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            if (this.mRewardedVideoManager != null) {
                this.mRewardedVideoManager.onResume(activity);
            }
            for (AbstractAdapter adapter22 : this.mRVAdaptersList) {
                adapter22.onResume(activity);
            }
            if (this.mInterstitialManager != null) {
                this.mInterstitialManager.onResume(activity);
            }
            for (AbstractAdapter adapter22 : this.mISAdaptersList) {
                adapter22.onResume(activity);
            }
            if (this.mBannerManager != null) {
                this.mBannerManager.onResume(activity);
            }
            for (AbstractAdapter adapter22 : this.mBannerAdaptersList) {
                adapter22.onResume(activity);
            }
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
        }
    }

    @Override
    public void onPause(Activity activity) {
        String logMessage = "onPause()";
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            for (AbstractAdapter adapter22 : this.mRVAdaptersList) {
                adapter22.onPause(activity);
            }
            for (AbstractAdapter adapter22 : this.mISAdaptersList) {
                adapter22.onPause(activity);
            }
            for (AbstractAdapter adapter22 : this.mBannerAdaptersList) {
                adapter22.onPause(activity);
            }
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
        }
    }

    @Override
    public synchronized void setAge(int age) {
        try {
            String logMessage = this.TAG + ":setAge(age:" + age + ")";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            ConfigValidationResult result = new ConfigValidationResult();
            this.validateAge(age, result);
            if (result.isValid()) {
                this.mUserAge = age;
            } else {
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, result.getIronSourceError().toString(), 2);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":setAge(age:" + age + ")", e);
        }
    }

    @Override
    public synchronized void setGender(String gender) {
        try {
            String logMessage = this.TAG + ":setGender(gender:" + gender + ")";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            ConfigValidationResult result = new ConfigValidationResult();
            this.validateGender(gender, result);
            if (result.isValid()) {
                this.mUserGender = gender;
            } else {
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, result.getIronSourceError().toString(), 2);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":setGender(gender:" + gender + ")", e);
        }
    }

    @Override
    public void setMediationSegment(String segment) {
        try {
            String logMessage = this.TAG + ":setMediationSegment(segment:" + segment + ")";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            ConfigValidationResult result = new ConfigValidationResult();
            this.validateSegment(segment, result);
            if (result.isValid()) {
                this.mSegment = segment;
            } else {
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, result.getIronSourceError().toString(), 2);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":setMediationSegment(segment:" + segment + ")", e);
        }
    }

    @Override
    public boolean setDynamicUserId(String dynamicUserId) {
        try {
            String logMessage = this.TAG + ":setDynamicUserId(dynamicUserId:" + dynamicUserId + ")";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            ConfigValidationResult result = new ConfigValidationResult();
            this.validateDynamicUserId(dynamicUserId, result);
            if (result.isValid()) {
                this.mDynamicUserId = dynamicUserId;
                return true;
            }
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, result.getIronSourceError().toString(), 2);
            return false;
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":setDynamicUserId(dynamicUserId:" + dynamicUserId + ")", e);
            return false;
        }
    }

    @Override
    public void setAdaptersDebug(boolean enabled) {
        IronSourceLoggerManager.getLogger().setAdaptersDebug(enabled);
    }

    @Override
    public void setMediationType(String mediationType) {
        try {
            String logMessage = this.TAG + ":setMediationType(mediationType:" + mediationType + ")";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, logMessage, 1);
            if (this.validateLength(mediationType, 1, 64) && this.validateAlphanumeric(mediationType)) {
                this.mMediationType = mediationType;
            } else {
                logMessage = " mediationType value is invalid - should be alphanumeric and 1-64 chars in length";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, logMessage, 1);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":setMediationType(mediationType:" + mediationType + ")", e);
        }
    }

    public synchronized Integer getAge() {
        return this.mUserAge;
    }

    public synchronized String getGender() {
        return this.mUserGender;
    }

    public synchronized String getMediationSegment() {
        return this.mSegment;
    }

    public synchronized String getDynamicUserId() {
        return this.mDynamicUserId;
    }

    public synchronized String getMediationType() {
        return this.mMediationType;
    }

    @Override
    public void initRewardedVideo(Activity activity, String appKey, String userId) {
    }

    @Override
    public void initInterstitial(Activity activity, String appKey, String userId) {
    }

    @Override
    public void initOfferwall(Activity activity, String appKey, String userId) {
    }

    private boolean isRewardedVideoConfigurationsReady() {
        return this.currentServerResponse != null && this.currentServerResponse.getConfigurations() != null && this.currentServerResponse.getConfigurations().getRewardedVideoConfigurations() != null;
    }

    @Override
    public void showRewardedVideo() {
        String logMessage = "showRewardedVideo()";
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            if (!this.isRewardedVideoConfigurationsReady()) {
                this.mListenersWrapper.onRewardedVideoAdShowFailed(ErrorBuilder.buildInitFailedError("showRewardedVideo can't be called before the Rewarded Video ad unit initialization completed successfully", "Rewarded Video"));
                return;
            }
            Placement defaultPlacement = this.currentServerResponse.getConfigurations().getRewardedVideoConfigurations().getDefaultRewardedVideoPlacement();
            if (defaultPlacement != null) {
                String placementName = defaultPlacement.getPlacementName();
                this.showRewardedVideo(placementName);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onRewardedVideoAdShowFailed(ErrorBuilder.buildInitFailedError("showRewardedVideo can't be called before the Rewarded Video ad unit initialization completed successfully", "Rewarded Video"));
        }
    }

    @Override
    public void showRewardedVideo(String placementName) {
        String logMessage = "showRewardedVideo(" + placementName + ")";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            if (!this.isRewardedVideoConfigurationsReady()) {
                this.mListenersWrapper.onRewardedVideoAdShowFailed(ErrorBuilder.buildInitFailedError("showRewardedVideo can't be called before the Rewarded Video ad unit initialization completed successfully", "Rewarded Video"));
                return;
            }
            Placement placement = this.currentServerResponse.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoPlacement(placementName);
            if (placement == null) {
                String noPlacementMessage = "Placement is not valid, please make sure you are using the right placements, using the default placement.";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noPlacementMessage, 3);
                placement = this.currentServerResponse.getConfigurations().getRewardedVideoConfigurations().getDefaultRewardedVideoPlacement();
                if (placement == null) {
                    String noDefaultPlacement = "Default placement was not found, please make sure you are using the right placements.";
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
                    return;
                }
            }
            CappingManager.ECappingStatus cappingStatus = this.getRewardedVideoCappingStatus(placement.getPlacementName());
            String cappedMessage = this.getCappingMessage(placement.getPlacementName(), cappingStatus);
//            if (!TextUtils.isEmpty((CharSequence)cappedMessage)) {
//                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, cappedMessage, 1);
//                this.mListenersWrapper.onRewardedVideoAdShowFailed(ErrorBuilder.buildCappedError("Rewarded Video", cappedMessage));
//                return;
//            }
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("placement", (Object)placement.getPlacementName());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData event = new EventData(2, data);
            RewardedVideoEventsManager.getInstance().log(event);
            this.mRewardedVideoManager.showRewardedVideo(placement.getPlacementName());
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onRewardedVideoAdShowFailed(ErrorBuilder.buildInitFailedError("showRewardedVideo can't be called before the Rewarded Video ad unit initialization completed successfully", "Rewarded Video"));
        }
    }

    @Override
    public boolean isRewardedVideoAvailable() {
        boolean isAvailable = false;
        try {
            isAvailable = this.mRewardedVideoManager.isRewardedVideoAvailable();
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("status", (Object)String.valueOf(isAvailable));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData event = new EventData(18, data);
            RewardedVideoEventsManager.getInstance().log(event);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "isRewardedVideoAvailable():" + isAvailable, 1);
        }
        catch (Throwable e) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "isRewardedVideoAvailable():" + isAvailable, 1);
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, "isRewardedVideoAvailable()", e);
            isAvailable = false;
        }
        return isAvailable;
    }

    @Override
    public void setRewardedVideoListener(RewardedVideoListener listener) {
        if (listener == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setRewardedVideoListener(RVListener:null)", 1);
        } else {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setRewardedVideoListener(RVListener)", 1);
        }
        this.mListenersWrapper.setRewardedVideoListener(listener);
    }

    @Override
    public void loadInterstitial() {
        String logMessage = "loadInterstitial()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            this.mInterstitialManager.loadInterstitial();
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
        }
    }

    private boolean isInterstitialConfigurationsReady() {
        return this.currentServerResponse != null && this.currentServerResponse.getConfigurations() != null && this.currentServerResponse.getConfigurations().getInterstitialConfigurations() != null;
    }

    @Override
    public void showInterstitial() {
        String logMessage = "showInterstitial()";
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            if (!this.isInterstitialConfigurationsReady()) {
                this.mListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildInitFailedError("showInterstitial can't be called before the Interstitial ad unit initialization completed successfully", "Interstitial"));
                return;
            }
            InterstitialPlacement defaultPlacement = this.currentServerResponse.getConfigurations().getInterstitialConfigurations().getDefaultInterstitialPlacement();
            if (defaultPlacement != null) {
                String placementName = defaultPlacement.getPlacementName();
                this.showInterstitial(placementName);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildInitFailedError("showInterstitial can't be called before the Interstitial ad unit initialization completed successfully", "Interstitial"));
        }
    }

    @Override
    public void showInterstitial(String placementName) {
        String logMessage = "showInterstitial(" + placementName + ")";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            if (!this.isInterstitialConfigurationsReady()) {
                this.mListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildInitFailedError("showInterstitial can't be called before the Interstitial ad unit initialization completed successfully", "Interstitial"));
                return;
            }
            InterstitialPlacement placement = this.currentServerResponse.getConfigurations().getInterstitialConfigurations().getInterstitialPlacement(placementName);
            if (placement == null) {
                String noPlacementMessage = "Placement is not valid, please make sure you are using the right placements, using the default placement.";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noPlacementMessage, 3);
                placement = this.currentServerResponse.getConfigurations().getInterstitialConfigurations().getDefaultInterstitialPlacement();
                if (placement == null) {
                    String noDefaultPlacement = "Default placement was not found, please make sure you are using the right placements.";
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
                    return;
                }
            }
            CappingManager.ECappingStatus cappingStatus = this.getInterstitialCappingStatus(placement.getPlacementName());
            String cappedMessage = this.getCappingMessage(placement.getPlacementName(), cappingStatus);
            if (!TextUtils.isEmpty((CharSequence)cappedMessage)) {
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, cappedMessage, 1);
                this.mListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildCappedError("Interstitial", cappedMessage));
                return;
            }
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("placement", (Object)placement.getPlacementName());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData event = new EventData(23, data);
            InterstitialEventsManager.getInstance().log(event);
            this.mInterstitialManager.showInterstitial(placement.getPlacementName());
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onInterstitialAdShowFailed(ErrorBuilder.buildInitFailedError("showInterstitial can't be called before the Interstitial ad unit initialization completed successfully", "Interstitial"));
        }
    }

    @Override
    public void setInterstitialListener(InterstitialListener listener) {
        if (listener == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setInterstitialListener(ISListener:null)", 1);
        } else {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setInterstitialListener(ISListener)", 1);
        }
        this.mListenersWrapper.setInterstitialListener(listener);
    }

    private boolean isOfferwallConfigurationsReady() {
        return this.currentServerResponse != null && this.currentServerResponse.getConfigurations() != null && this.currentServerResponse.getConfigurations().getOfferwallConfigurations() != null;
    }

    @Override
    public void showOfferwall() {
        String logMessage = "showOfferwall()";
        try {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
            if (!this.isOfferwallConfigurationsReady()) {
                this.mListenersWrapper.onOfferwallShowFailed(ErrorBuilder.buildInitFailedError("showOfferwall can't be called before the Offerwall ad unit initialization completed successfully", "Offerwall"));
                return;
            }
            OfferwallPlacement defaultPlacement = this.currentServerResponse.getConfigurations().getOfferwallConfigurations().getDefaultOfferwallPlacement();
            if (defaultPlacement != null) {
                String placementName = defaultPlacement.getPlacementName();
                this.showOfferwall(placementName);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onOfferwallShowFailed(ErrorBuilder.buildInitFailedError("showOfferwall can't be called before the Offerwall ad unit initialization completed successfully", "Offerwall"));
        }
    }

    @Override
    public void showOfferwall(String placementName) {
        String logMessage = "showOfferwall(" + placementName + ")";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            if (!this.isOfferwallConfigurationsReady()) {
                this.mListenersWrapper.onOfferwallShowFailed(ErrorBuilder.buildInitFailedError("showOfferwall can't be called before the Offerwall ad unit initialization completed successfully", "Offerwall"));
                return;
            }
            OfferwallPlacement placement = this.currentServerResponse.getConfigurations().getOfferwallConfigurations().getOfferwallPlacement(placementName);
            if (placement == null) {
                String noPlacementMessage = "Placement is not valid, please make sure you are using the right placements, using the default placement.";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noPlacementMessage, 3);
                placement = this.currentServerResponse.getConfigurations().getOfferwallConfigurations().getDefaultOfferwallPlacement();
                if (placement == null) {
                    String noDefaultPlacement = "Default placement was not found, please make sure you are using the right placements.";
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, noDefaultPlacement, 3);
                    return;
                }
            }
            this.mOfferwallManager.showOfferwall(placement.getPlacementName());
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
            this.mListenersWrapper.onOfferwallShowFailed(ErrorBuilder.buildInitFailedError("showOfferwall can't be called before the Offerwall ad unit initialization completed successfully", "Offerwall"));
        }
    }

    @Override
    public boolean isOfferwallAvailable() {
        boolean result = false;
        try {
            if (this.mOfferwallManager != null) {
                result = this.mOfferwallManager.isOfferwallAvailable();
            }
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public void getOfferwallCredits() {
        String logMessage = "getOfferwallCredits()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            this.mOfferwallManager.getOfferwallCredits();
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
        }
    }

    @Override
    public void setOfferwallListener(OfferwallListener offerwallListener) {
        if (offerwallListener == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setOfferwallListener(OWListener:null)", 1);
        } else {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setOfferwallListener(OWListener)", 1);
        }
        this.mListenersWrapper.setOfferwallListener(offerwallListener);
    }

    @Override
    public void setLogListener(LogListener logListener) {
        if (logListener == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setLogListener(LogListener:null)", 1);
        } else {
            this.mPublisherLogger.setLogListener(logListener);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "setLogListener(LogListener:" + logListener.getClass().getSimpleName() + ")", 1);
        }
    }

    @Override
    public void setRewardedInterstitialListener(RewardedInterstitialListener listener) {
        this.mListenersWrapper.setRewardedInterstitialListener(listener);
    }

    private boolean isBannerConfigurationsReady() {
        return this.currentServerResponse != null && this.currentServerResponse.getConfigurations() != null && this.currentServerResponse.getConfigurations().getBannerConfigurations() != null;
    }

    @Override
    public IronSourceBannerLayout createBanner(Activity activity, EBannerSize size) {
        String logMessage = "createBanner()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        if (activity == null) {
            logMessage = "createBanner() : Activity cannot be null";
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 3);
            return null;
        }
        return this.mBannerManager.createBanner(activity, size);
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner, String placementName) {
        String logMessage = "loadBanner(" + placementName + ")";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        if (banner == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "loadBanner can't be called with a null parameter", 1);
            return;
        }
        this.mBannerManager.loadBanner(banner, placementName);
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner) {
        String logMessage = "loadBanner()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        if (banner == null) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "loadBanner can't be called with a null parameter", 1);
            return;
        }
        this.loadBanner(banner, null);
    }

    @Override
    public void destroyBanner(IronSourceBannerLayout banner) {
        String logMessage = "destroyBanner()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        try {
            this.mBannerManager.destroyBanner(banner);
        }
        catch (Throwable e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, logMessage, e);
        }
    }

    public ServerResponseWrapper getServerResponse(Context context, String userId) {
        return this.getServerResponse(context, userId, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerResponseWrapper getServerResponse(Context context, String userId, IResponseListener listener) {
        ServerResponseWrapper response;
        Object object = this.serverResponseLocker;
        synchronized (object) {
            if (this.currentServerResponse != null) {
                return new ServerResponseWrapper(this.currentServerResponse);
            }
            response = this.connectAndGetServerResponse(context, userId, listener);
            if (response == null || !response.isValidResponse()) {
                response = this.getCachedResponse(context, userId);
            }
            if (response != null) {
                this.currentServerResponse = response;
                IronSourceUtils.saveLastResponse(context, response.toString());
                this.initializeSettingsFromServerResponse(this.currentServerResponse, context);
            }
            InterstitialEventsManager.getInstance().setHasServerResponse(true);
            RewardedVideoEventsManager.getInstance().setHasServerResponse(true);
        }
        return response;
    }

    private ServerResponseWrapper getCachedResponse(Context context, String userId) {
        JSONObject cachedJsonObject;
        ServerResponseWrapper response = null;
        String cachedResponseString = IronSourceUtils.getLastResponse(context);
        try {
            cachedJsonObject = new JSONObject(cachedResponseString);
        }
        catch (JSONException e) {
            cachedJsonObject = new JSONObject();
        }
        String cachedAppKey = cachedJsonObject.optString("appKey");
        String cachedUserId = cachedJsonObject.optString("userId");
        String cachedSettings = cachedJsonObject.optString("response");
        if (!TextUtils.isEmpty((CharSequence)cachedAppKey) && !TextUtils.isEmpty((CharSequence)cachedUserId) && !TextUtils.isEmpty((CharSequence)cachedSettings) && this.getIronSourceAppKey() != null && cachedAppKey.equals(this.getIronSourceAppKey()) && cachedUserId.equals(userId)) {
            response = new ServerResponseWrapper(context, cachedAppKey, cachedUserId, cachedSettings);
            IronSourceError sse = ErrorBuilder.buildUsingCachedConfigurationError(cachedAppKey, cachedUserId);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, sse.toString(), 1);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, sse.toString() + ": " + response.toString(), 0);
        }
        return response;
    }

    private ServerResponseWrapper connectAndGetServerResponse(Context context, String userId, IResponseListener listener) {
        if (!IronSourceUtils.isNetworkConnected(context)) {
            return null;
        }
        ServerResponseWrapper response = null;
        try {
            String serverResponseString;
            String gaid = this.getAdvertiserId(context);
            if (TextUtils.isEmpty((CharSequence)gaid)) {
                gaid = DeviceStatus.getOrGenerateOnceUniqueIdentifier(context);
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "using custom identifier", 1);
            }
            if ((serverResponseString = HttpFunctions.getStringFromURL(ServerURL.getCPVProvidersURL(this.getIronSourceAppKey(), userId, gaid), listener)) == null) {
                return null;
            }
            response = new ServerResponseWrapper(context, this.getIronSourceAppKey(), userId, serverResponseString);
            if (!response.isValidResponse()) {
                return null;
            }
        }
        catch (Exception gaid) {
            // empty catch block
        }
        return response;
    }

    private void initializeSettingsFromServerResponse(ServerResponseWrapper response, Context context) {
        this.initializeLoggerManager(response);
        this.initializeEventsSettings(response, context);
    }

    private void initializeEventsSettings(ServerResponseWrapper response, Context context) {
        boolean isRVEventsEnabled = false;
        if (this.isRewardedVideoConfigurationsReady()) {
            isRVEventsEnabled = response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().isEventsEnabled();
        }
        boolean isISEventsEnabled = false;
        if (this.isInterstitialConfigurationsReady()) {
            isISEventsEnabled = response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().isEventsEnabled();
        }
        if (isRVEventsEnabled) {
            RewardedVideoEventsManager.getInstance().setFormatterType(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getEventsType(), context);
            RewardedVideoEventsManager.getInstance().setEventsUrl(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getEventsURL(), context);
            RewardedVideoEventsManager.getInstance().setMaxNumberOfEvents(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getMaxNumberOfEvents());
            RewardedVideoEventsManager.getInstance().setMaxEventsPerBatch(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getMaxEventsPerBatch());
            RewardedVideoEventsManager.getInstance().setBackupThreshold(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getEventsBackupThreshold());
            RewardedVideoEventsManager.getInstance().setOptOutEvents(response.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoEventsConfigurations().getOptOutEvents(), context);
        } else {
            RewardedVideoEventsManager.getInstance().setIsEventsEnabled(isRVEventsEnabled);
        }
        if (isISEventsEnabled) {
            InterstitialEventsManager.getInstance().setFormatterType(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getEventsType(), context);
            InterstitialEventsManager.getInstance().setEventsUrl(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getEventsURL(), context);
            InterstitialEventsManager.getInstance().setMaxNumberOfEvents(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getMaxNumberOfEvents());
            InterstitialEventsManager.getInstance().setMaxEventsPerBatch(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getMaxEventsPerBatch());
            InterstitialEventsManager.getInstance().setBackupThreshold(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getEventsBackupThreshold());
            InterstitialEventsManager.getInstance().setOptOutEvents(response.getConfigurations().getInterstitialConfigurations().getInterstitialEventsConfigurations().getOptOutEvents(), context);
        } else {
            InterstitialEventsManager.getInstance().setIsEventsEnabled(isISEventsEnabled);
        }
    }

    private void initializeLoggerManager(ServerResponseWrapper response) {
        this.mPublisherLogger.setDebugLevel(response.getConfigurations().getApplicationConfigurations().getLoggerConfigurations().getPublisherLoggerLevel());
        this.mLoggerManager.setLoggerDebugLevel("console", response.getConfigurations().getApplicationConfigurations().getLoggerConfigurations().getConsoleLoggerLevel());
        this.mLoggerManager.setLoggerDebugLevel("server", response.getConfigurations().getApplicationConfigurations().getLoggerConfigurations().getServerLoggerLevel());
    }

    @Override
    public void removeRewardedVideoListener() {
        String logMessage = "removeRewardedVideoListener()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        this.mListenersWrapper.setRewardedVideoListener(null);
    }

    @Override
    public void removeInterstitialListener() {
        String logMessage = "removeInterstitialListener()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        this.mListenersWrapper.setInterstitialListener(null);
    }

    @Override
    public void removeOfferwallListener() {
        String logMessage = "removeOfferwallListener()";
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        this.mListenersWrapper.setOfferwallListener(null);
    }

    public synchronized void setIronSourceAppKey(String appKey) {
        if (this.mAppKey == null) {
            this.mAppKey = appKey;
        }
    }

    public synchronized void setIronSourceUserId(String userId) {
        this.mUserId = userId;
    }

    public synchronized String getIronSourceAppKey() {
        return this.mAppKey;
    }

    public synchronized String getIronSourceUserId() {
        return this.mUserId;
    }

    private ConfigValidationResult validateAppKey(String appKey) {
        ConfigValidationResult result = new ConfigValidationResult();
        if (appKey != null) {
            if (this.validateLength(appKey, 5, 10)) {
                if (!this.validateAlphanumeric(appKey)) {
                    IronSourceError error = ErrorBuilder.buildInvalidCredentialsError("appKey", appKey, "should contain only english characters and numbers");
                    result.setInvalid(error);
                }
            } else {
                IronSourceError error = ErrorBuilder.buildInvalidCredentialsError("appKey", appKey, "length should be between 5-10 characters");
                result.setInvalid(error);
            }
        } else {
            IronSourceError error = ErrorBuilder.buildInvalidCredentialsError("appKey", appKey, "it's missing");
            result.setInvalid(error);
        }
        return result;
    }

    private void validateGender(String gender, ConfigValidationResult result) {
        try {
            if (!(gender == null || "male".equals(gender = gender.toLowerCase().trim()) || "female".equals(gender) || "unknown".equals(gender))) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("gender", "SupersonicAds", "gender value should be one of male/female/unknown."));
            }
        }
        catch (Exception e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("gender", "SupersonicAds", "gender value should be one of male/female/unknown."));
        }
    }

    private void validateAge(int age, ConfigValidationResult result) {
        try {
            if (age < 5 || age > 120) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("age", "SupersonicAds", "age value should be between 5-120"));
            }
        }
        catch (NumberFormatException e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("age", "SupersonicAds", "age value should be between 5-120"));
        }
    }

    private void validateSegment(String segment, ConfigValidationResult result) {
        try {
            if (segment != null && segment.length() > 64) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("segment", "SupersonicAds", "segment value should not exceed 64 characters."));
            }
        }
        catch (Exception e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("segment", "SupersonicAds", "segment value should not exceed 64 characters."));
        }
    }

    private void validateDynamicUserId(String dynamicUserId, ConfigValidationResult result) {
        if (!this.validateLength(dynamicUserId, 1, 64) || !this.validateAlphanumeric(dynamicUserId)) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("dynamicUserId", "SupersonicAds", "dynamicUserId is invalid, should be alphanumeric and between 1-64 chars in length."));
        }
    }

    private boolean validateLength(String key, int minLength, int maxLength) {
        if (key == null) {
            return false;
        }
        return key.length() >= minLength && key.length() <= maxLength;
    }

    private boolean validateAlphanumeric(String key) {
        if (key == null) {
            return false;
        }
        String pattern = "^[a-zA-Z0-9]*$";
        return key.matches(pattern);
    }

    @Override
    public InterstitialPlacement getInterstitialPlacementInfo(String placementName) {
        InterstitialPlacement result = null;
        try {
            result = this.currentServerResponse.getConfigurations().getInterstitialConfigurations().getInterstitialPlacement(placementName);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "getPlacementInfo(placement: " + placementName + "):" + result, 1);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return result;
    }

    @Override
    public Placement getRewardedVideoPlacementInfo(String placementName) {
        Placement result = null;
        try {
            result = this.currentServerResponse.getConfigurations().getRewardedVideoConfigurations().getRewardedVideoPlacement(placementName);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "getPlacementInfo(placement: " + placementName + "):" + result, 1);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return result;
    }

    @Override
    public String getAdvertiserId(Context context) {
        try {
            String[] deviceInfo = DeviceStatus.getAdvertisingIdInfo(context);
            if (deviceInfo.length > 0 && deviceInfo[0] != null) {
                return deviceInfo[0];
            }
        }
        catch (Exception e) {
            return "";
        }
        return "";
    }

    @Override
    public void shouldTrackNetworkState(Context context, boolean track) {
        if (this.mRewardedVideoManager != null) {
            this.mRewardedVideoManager.shouldTrackNetworkState(context, track);
        }
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.shouldTrackNetworkState(context, track);
        }
        if (this.mBannerManager != null) {
            this.mBannerManager.shouldTrackNetworkState(context, track);
        }
    }

    @Override
    public boolean isInterstitialReady() {
        boolean isAvailable = false;
        try {
            isAvailable = this.mInterstitialManager.isInterstitialReady();
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("status", (Object)String.valueOf(isAvailable));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData event = new EventData(30, data);
            InterstitialEventsManager.getInstance().log(event);
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "isInterstitialReady():" + isAvailable, 1);
        }
        catch (Throwable e) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, "isInterstitialReady():" + isAvailable, 1);
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, "isInterstitialReady()", e);
            isAvailable = false;
        }
        return isAvailable;
    }

    @Override
    public boolean isInterstitialPlacementCapped(String placementName) {
        boolean isCapped = false;
        CappingManager.ECappingStatus cappingStatus = this.getInterstitialCappingStatus(placementName);
        if (cappingStatus != null) {
            switch (cappingStatus) {
                case CAPPED_PER_DELIVERY: 
                case CAPPED_PER_COUNT: 
                case CAPPED_PER_PACE: {
                    isCapped = true;
                    break;
                }
            }
        }
        this.sendIsCappedEvent("Interstitial", isCapped);
        return isCapped;
    }

    @Override
    public boolean isRewardedVideoPlacementCapped(String placementName) {
        boolean isCapped = false;
        CappingManager.ECappingStatus cappingStatus = this.getRewardedVideoCappingStatus(placementName);
        if (cappingStatus != null) {
            switch (cappingStatus) {
                case CAPPED_PER_DELIVERY: 
                case CAPPED_PER_COUNT: 
                case CAPPED_PER_PACE: {
                    isCapped = true;
                    break;
                }
            }
        }
        this.sendIsCappedEvent("Rewarded Video", isCapped);
        return isCapped;
    }

    @Override
    public boolean isBannerPlacementCapped(String placementName) {
        boolean isCapped = false;
        CappingManager.ECappingStatus cappingStatus = this.getBannerCappingStatus(placementName);
        if (cappingStatus != null) {
            switch (cappingStatus) {
                case CAPPED_PER_DELIVERY: 
                case CAPPED_PER_COUNT: 
                case CAPPED_PER_PACE: {
                    isCapped = true;
                    break;
                }
            }
        }
        this.sendIsCappedEvent("Banner", isCapped);
        return isCapped;
    }

    private CappingManager.ECappingStatus getInterstitialCappingStatus(String placementName) {
        if (this.mInterstitialManager == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        InterstitialPlacement placement = this.mInterstitialManager.getPlacementByName(placementName);
        if (placement == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        CappingManager.ECappingStatus cappingStatus = CappingManager.isPlacementCapped((Context)this.mActivity, placement);
        return cappingStatus;
    }

    private CappingManager.ECappingStatus getRewardedVideoCappingStatus(String placementName) {
        if (this.mRewardedVideoManager == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        Placement placement = this.mRewardedVideoManager.getPlacementByName(placementName);
        if (placement == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        CappingManager.ECappingStatus cappingStatus = CappingManager.isPlacementCapped((Context)this.mActivity, placement);
        return cappingStatus;
    }

    public CappingManager.ECappingStatus getBannerCappingStatus(String placementName) {
        if (this.mBannerManager == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        BannerPlacement placement = this.mBannerManager.getPlacementByName(placementName);
        if (placement == null) {
            return CappingManager.ECappingStatus.NOT_CAPPED;
        }
        CappingManager.ECappingStatus cappingStatus = CappingManager.isPlacementCapped((Context)this.mActivity, placement);
        return cappingStatus;
    }

    private void sendIsCappedEvent(String adUnit, boolean isCapped) {
        if (!isCapped) {
            return;
        }
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("reason", isCapped ? 1 : 0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if ("Interstitial".equals(adUnit)) {
            EventData event = new EventData(34, data);
            InterstitialEventsManager.getInstance().log(event);
        } else if ("Rewarded Video".equals(adUnit)) {
            EventData event = new EventData(20, data);
            RewardedVideoEventsManager.getInstance().log(event);
        } else if ("Banner".equals(adUnit)) {
            EventData event = new EventData(414, data);
            InterstitialEventsManager.getInstance().log(event);
        }
    }

    public String getCappingMessage(String placementName, CappingManager.ECappingStatus cappingStatus) {
        if (cappingStatus == null) {
            return null;
        }
        switch (cappingStatus) {
            case CAPPED_PER_DELIVERY: {
                return "Placement " + placementName + " is capped by disabled delivery";
            }
            case CAPPED_PER_COUNT: {
                return "Placement " + placementName + " has reached its capping limit";
            }
            case CAPPED_PER_PACE: {
                return "Placement " + placementName + " has reached its limit as defined per pace";
            }
        }
        return null;
    }

    public ServerResponseWrapper getCurrentServerResponse() {
        return this.currentServerResponse;
    }

    public static interface IResponseListener {
        public void onUnrecoverableError(String var1);
    }

}

