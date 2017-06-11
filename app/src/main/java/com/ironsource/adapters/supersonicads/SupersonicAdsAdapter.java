/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.text.TextUtils
 */
package com.ironsource.adapters.supersonicads;

import android.app.Activity;
import android.text.TextUtils;
import com.ironsource.adapters.supersonicads.DemandSourceConfig;
import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.config.AbstractAdapterConfig;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;
import com.ironsource.mediationsdk.sdk.InternalOfferwallApi;
import com.ironsource.mediationsdk.sdk.InternalOfferwallListener;
import com.ironsource.mediationsdk.sdk.InterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoManagerListener;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.RewardedVideoHelper;
import com.ironsource.sdk.SSAFactory;
import com.ironsource.sdk.SSAPublisher;
import com.ironsource.sdk.data.AdUnitsReady;
import com.ironsource.sdk.listeners.OnInterstitialListener;
import com.ironsource.sdk.listeners.OnOfferWallListener;
import com.ironsource.sdk.listeners.OnRewardedVideoListener;
import com.ironsource.sdk.utils.SDKUtils;
import java.util.HashMap;
import java.util.Map;

class SupersonicAdsAdapter
extends AbstractAdapter
implements InternalOfferwallApi,
OnOfferWallListener,
OnInterstitialListener,
OnRewardedVideoListener {
    private final String VERSION = "6.6.0";
    private final String TIMESTAMP = "timestamp";
    private final String ITEM_SIGNATURE = "itemSignature";
    private final String SDK_PLUGIN_TYPE = "SDKPluginType";
    private final String OW_PLACEMENT_ID = "placementId";
    private SSAPublisher mSSAPublisher;
    private RewardedVideoManagerListener mRewardedVideoManager;
    private InterstitialManagerListener mInterstitialManager;
    private InternalOfferwallListener mOfferwallListener;
    private RewardedVideoHelper mRewardedVideoHelper = new RewardedVideoHelper();
    private DemandSourceConfig mAdapterConfig;

    public static SupersonicAdsAdapter startAdapter(String providerName, String providerUrl) {
        return new SupersonicAdsAdapter(providerName, providerUrl);
    }

    private SupersonicAdsAdapter(String providerName, String providerUrl) {
        super(providerName, providerUrl);
        this.mAdapterConfig = new DemandSourceConfig(providerName);
        SDKUtils.setControllerUrl(this.mAdapterConfig.getRVDynamicControllerUrl());
        if (this.isAdaptersDebugEnabled()) {
            SDKUtils.setDebugMode(0);
        } else {
            SDKUtils.setDebugMode(this.mAdapterConfig.getRVDebugMode());
        }
        SDKUtils.setControllerConfig(this.mAdapterConfig.getRVControllerConfig());
    }

    @Override
    public int getMaxRVAdsPerIteration() {
        return this.mAdapterConfig.getMaxRVAdsPerIteration();
    }

    @Override
    public int getMaxISAdsPerIteration() {
        return this.mAdapterConfig.getMaxISAdsPerIteration();
    }

    @Override
    public String getVersion() {
        return "6.6.0";
    }

    @Override
    public String getCoreSDKVersion() {
        return SDKUtils.getSDKVersion();
    }

    private HashMap<String, String> getGenenralExtraParams() {
        String uGender;
        String pluginType;
        HashMap<String, String> params = new HashMap<String, String>();
        DemandSourceConfig config = this.mAdapterConfig;
        String ageGroup = config.getRVUserAgeGroup();
        if (!TextUtils.isEmpty((CharSequence)ageGroup)) {
            params.put("applicationUserAgeGroup", ageGroup);
        }
        if (!TextUtils.isEmpty((CharSequence)(uGender = config.getRVUserGender()))) {
            params.put("applicationUserGender", uGender);
        }
        if (!TextUtils.isEmpty((CharSequence)(pluginType = this.getPluginType()))) {
            params.put("SDKPluginType", pluginType);
        }
        return params;
    }

    private HashMap<String, String> getRewardedVideoExtraParams() {
        String segment;
        String campaignId;
        String maxVideoLength;
        HashMap<String, String> rvExtraParams = this.getGenenralExtraParams();
        DemandSourceConfig config = this.mAdapterConfig;
        String language = config.getLanguage();
        if (!TextUtils.isEmpty((CharSequence)language)) {
            rvExtraParams.put("language", language);
        }
        if (!TextUtils.isEmpty((CharSequence)(maxVideoLength = config.getMaxVideoLength()))) {
            rvExtraParams.put("maxVideoLength", maxVideoLength);
        }
        if (!TextUtils.isEmpty((CharSequence)(campaignId = config.getCampaignId()))) {
            rvExtraParams.put("campaignId", campaignId);
        }
        if (!TextUtils.isEmpty((CharSequence)(segment = config.getMediationSegment()))) {
            rvExtraParams.put("custom_Segment", segment);
        }
        this.addItemNameCountSignature(rvExtraParams);
        Map<String, String> customParams = SupersonicConfig.getConfigObj().getRewardedVideoCustomParams();
        if (customParams != null && !customParams.isEmpty()) {
            rvExtraParams.putAll(customParams);
        }
        return rvExtraParams;
    }

    private HashMap<String, String> getInterstitialExtraParams() {
        return this.getGenenralExtraParams();
    }

    private HashMap<String, String> getOfferwallExtraParams() {
        HashMap<String, String> owExtraParams = this.getGenenralExtraParams();
        String language = this.mAdapterConfig.getLanguage();
        if (!TextUtils.isEmpty((CharSequence)language)) {
            owExtraParams.put("language", language);
        }
        boolean clientSideCallbacks = SupersonicConfig.getConfigObj().getClientSideCallbacks();
        owExtraParams.put("useClientSideCallbacks", String.valueOf(clientSideCallbacks));
        Map<String, String> customParams = SupersonicConfig.getConfigObj().getOfferwallCustomParams();
        if (customParams != null && !customParams.isEmpty()) {
            owExtraParams.putAll(customParams);
        }
        this.addItemNameCountSignature(owExtraParams);
        return owExtraParams;
    }

    private void addItemNameCountSignature(HashMap<String, String> params) {
        try {
            String itemName = this.mAdapterConfig.getItemName();
            int itemCount = this.mAdapterConfig.getItemCount();
            String privateKey = this.mAdapterConfig.getPrivateKey();
            boolean shouldAddSignature = true;
            if (TextUtils.isEmpty((CharSequence)itemName)) {
                shouldAddSignature = false;
            } else {
                params.put("itemName", itemName);
            }
            if (TextUtils.isEmpty((CharSequence)privateKey)) {
                shouldAddSignature = false;
            }
            if (itemCount == -1) {
                shouldAddSignature = false;
            } else {
                params.put("itemCount", String.valueOf(itemCount));
            }
            if (shouldAddSignature) {
                int timestamp = IronSourceUtils.getCurrentTimestamp();
                params.put("timestamp", String.valueOf(timestamp));
                params.put("itemSignature", this.createItemSig(timestamp, itemName, itemCount, privateKey));
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.ADAPTER_API, " addItemNameCountSignature", e);
        }
    }

    private String createItemSig(int timestamp, String itemName, int itemCount, String privateKey) {
        return IronSourceUtils.getMD5("" + timestamp + itemName + itemCount + privateKey);
    }

    private String createMinimumOfferCommissionSig(double min, String privateKey) {
        return IronSourceUtils.getMD5("" + min + privateKey);
    }

    private String createUserCreationDateSig(String userid, String uCreationDate, String privateKey) {
        return IronSourceUtils.getMD5(userid + uCreationDate + privateKey);
    }

    @Override
    public void initRewardedVideo(final Activity activity, final String appKey, final String userId) {
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":initRewardedVideo(userId:" + userId + ")", 1);
        this.mRewardedVideoHelper.reset();
        ConfigValidationResult validationResult = this.validateConfigBeforeInitAndCallAvailabilityChangedForInvalid(this.mAdapterConfig, this.mRewardedVideoManager);
        if (!validationResult.isValid()) {
            return;
        }
        this.mRewardedVideoHelper.setMaxVideo(this.mAdapterConfig.getMaxVideos());
        this.startRVTimer(this.mRewardedVideoManager);
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                SupersonicAdsAdapter.this.mSSAPublisher = SSAFactory.getPublisherInstance(activity);
                HashMap rewardedVideoExtraParams = SupersonicAdsAdapter.this.getRewardedVideoExtraParams();
                SupersonicAdsAdapter.this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, SupersonicAdsAdapter.this.getProviderName() + ":initRewardedVideo(appKey:" + appKey + ", userId:" + userId + ", demandSource: " + SupersonicAdsAdapter.this.getProviderName() + ", extraParams:" + rewardedVideoExtraParams + ")", 1);
                SupersonicAdsAdapter.this.mSSAPublisher.initRewardedVideo(appKey, userId, SupersonicAdsAdapter.this.getProviderName(), rewardedVideoExtraParams, SupersonicAdsAdapter.this);
            }
        });
    }

    @Override
    public void onPause(Activity activity) {
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":onPause()", 1);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.onPause(activity);
        }
    }

    @Override
    public void setAge(int age) {
        this.mAdapterConfig.setUserAgeGroup(age);
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":setAge(age:" + age + ")", 1);
    }

    @Override
    public void setGender(String gender) {
        this.mAdapterConfig.setUserGender(gender);
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":setGender(gender:" + gender + ")", 1);
    }

    @Override
    public void setMediationSegment(String segment) {
        this.mAdapterConfig.setMediationSegment(segment);
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":setMediationSegment(segment:" + segment + ")", 1);
    }

    @Override
    public void onResume(Activity activity) {
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":onResume()", 1);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.onResume(activity);
        }
    }

    @Override
    public synchronized boolean isRewardedVideoAvailable() {
        boolean availability = this.mRewardedVideoHelper.isVideoAvailable();
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":isRewardedVideoAvailable():" + availability, 1);
        return availability;
    }

    @Override
    public void setRewardedVideoListener(RewardedVideoManagerListener rewardedVideoManager) {
        this.mRewardedVideoManager = rewardedVideoManager;
    }

    @Override
    public void showRewardedVideo() {
    }

    @Override
    public void showRewardedVideo(String placementName) {
        boolean shouldNotify;
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":showRewardedVideo(placement:" + placementName + ")", 1);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.showRewardedVideo(this.getProviderName());
            this.mRewardedVideoHelper.setPlacementName(placementName);
            shouldNotify = this.mRewardedVideoHelper.increaseCurrentVideo();
        } else {
            shouldNotify = this.mRewardedVideoHelper.setVideoAvailability(false);
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Please call init before calling showRewardedVideo", 2);
            this.mRewardedVideoManager.onRewardedVideoAdShowFailed(new IronSourceError(509, "Please call init before calling showRewardedVideo"), this);
        }
        if (this.mRewardedVideoManager != null && shouldNotify) {
            this.mRewardedVideoManager.onRewardedVideoAvailabilityChanged(this.mRewardedVideoHelper.isVideoAvailable(), this);
        }
    }

    @Override
    public void onRVNoMoreOffers() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVNoMoreOffers ", 1);
        this.cancelRVTimer();
        boolean shouldNotify = this.mRewardedVideoHelper.setVideoAvailability(false);
        if (this.mRewardedVideoManager != null && shouldNotify) {
            this.mRewardedVideoManager.onRewardedVideoAvailabilityChanged(this.mRewardedVideoHelper.isVideoAvailable(), this);
        }
    }

    @Override
    public void onRVInitSuccess(AdUnitsReady aur) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVInitSuccess ", 1);
        this.cancelRVTimer();
        int numOfAdUnits = 0;
        try {
            numOfAdUnits = Integer.parseInt(aur.getNumOfAdUnits());
        }
        catch (NumberFormatException e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "onRVInitSuccess:parseInt()", e);
        }
        boolean availability = numOfAdUnits > 0;
        boolean shouldNotify = this.mRewardedVideoHelper.setVideoAvailability(availability);
        if (this.mRewardedVideoManager != null && shouldNotify) {
            this.mRewardedVideoManager.onRewardedVideoAvailabilityChanged(this.mRewardedVideoHelper.isVideoAvailable(), this);
        }
    }

    @Override
    public void onRVInitFail(String error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVInitFail ", 1);
        this.cancelRVTimer();
        boolean shouldNotify = this.mRewardedVideoHelper.setVideoAvailability(false);
        if (shouldNotify && this.mRewardedVideoManager != null) {
            this.mRewardedVideoManager.onRewardedVideoAvailabilityChanged(this.mRewardedVideoHelper.isVideoAvailable(), this);
        }
    }

    @Override
    public void onRVAdClicked() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVAdClicked ", 1);
    }

    @Override
    public void onRVShowFail(String error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVShowFail ", 1);
        if (this.mRewardedVideoManager != null) {
            this.mRewardedVideoManager.onRewardedVideoAdShowFailed(new IronSourceError(509, error), this);
        }
    }

    @Override
    public void onRVAdCredited(int amount) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVAdCredited ", 1);
        if (this.mRewardedVideoManager != null) {
            Placement placement = this.mRewardedVideoConfig.getRewardedVideoPlacement(this.mRewardedVideoHelper.getPlacementName());
            this.mRewardedVideoManager.onRewardedVideoAdRewarded(placement, this);
        }
    }

    @Override
    public void onRVAdClosed() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVAdClosed ", 1);
        if (this.mRewardedVideoManager != null) {
            this.mRewardedVideoManager.onRewardedVideoAdClosed(this);
        }
    }

    @Override
    public void onRVAdOpened() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onRVAdOpened ", 1);
        if (this.mRewardedVideoManager != null) {
            this.mRewardedVideoManager.onRewardedVideoAdOpened(this);
        }
    }

    @Override
    public void getOfferwallCredits() {
        if (this.mSSAPublisher != null) {
            String appKey = IronSourceObject.getInstance().getIronSourceAppKey();
            String userId = IronSourceObject.getInstance().getIronSourceUserId();
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":getOfferwallCredits(appKey:" + appKey + "userId:" + userId + ")", 1);
            this.mSSAPublisher.getOfferWallCredits(appKey, userId, this);
        } else {
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Please call init before calling getOfferwallCredits", 2);
        }
    }

    @Override
    public void setOfferwallListener(OfferwallListener owListener) {
    }

    @Override
    public void setInternalOfferwallListener(InternalOfferwallListener listener) {
        this.mOfferwallListener = listener;
    }

    @Override
    public void initOfferwall(final Activity activity, final String appKey, final String userId) {
        try {
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":initOfferwall(appKey:" + appKey + ", userId:" + userId + ")", 1);
            activity.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    HashMap offerwallExtraParams = SupersonicAdsAdapter.this.getOfferwallExtraParams();
                    SupersonicAdsAdapter.this.mSSAPublisher = SSAFactory.getPublisherInstance(activity);
                    SupersonicAdsAdapter.this.mSSAPublisher.initOfferWall(appKey, userId, offerwallExtraParams, SupersonicAdsAdapter.this);
                }
            });
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":initOfferwall(userId:" + userId + ")", e);
            this.mOfferwallListener.onOfferwallAvailable(false, ErrorBuilder.buildInitFailedError("Adapter initialization failure - " + this.getProviderName() + " - " + e.getMessage(), "Offerwall"));
        }
    }

    @Override
    public void showOfferwall() {
    }

    @Override
    public void showOfferwall(String placementId) {
        HashMap<String, String> offerwallExtraParams = this.getOfferwallExtraParams();
        if (offerwallExtraParams != null) {
            offerwallExtraParams.put("placementId", placementId);
        }
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":showOfferwall(" + "extraParams:" + offerwallExtraParams + ")", 1);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.showOfferWall(offerwallExtraParams);
        } else {
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Please call init before calling showOfferwall", 2);
        }
    }

    @Override
    public boolean isOfferwallAvailable() {
        return true;
    }

    @Override
    public void onOWShowSuccess(String placementId) {
        if (TextUtils.isEmpty((CharSequence)placementId)) {
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":onOWShowSuccess()", 1);
        } else {
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":onOWShowSuccess(placementId:" + placementId + ")", 1);
        }
        if (this.mOfferwallListener != null) {
            this.mOfferwallListener.onOfferwallOpened();
        }
    }

    @Override
    public void onOWShowFail(String desc) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onOWShowFail ", 1);
        if (this.mOfferwallListener != null) {
            IronSourceError sse = ErrorBuilder.buildGenericError(desc);
            this.mOfferwallListener.onOfferwallShowFailed(sse);
        }
    }

    @Override
    public void onOWGeneric(String arg0, String arg1) {
    }

    @Override
    public boolean onOWAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onOWAdCredited ", 1);
        return this.mOfferwallListener != null && this.mOfferwallListener.onOfferwallAdCredited(credits, totalCredits, totalCreditsFlag);
    }

    @Override
    public void onOWAdClosed() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onOWAdClosed ", 1);
        if (this.mOfferwallListener != null) {
            this.mOfferwallListener.onOfferwallClosed();
        }
    }

    @Override
    public void onGetOWCreditsFailed(String desc) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onGetOWCreditsFailed ", 1);
        if (this.mOfferwallListener != null) {
            IronSourceError sse = ErrorBuilder.buildGenericError(desc);
            this.mOfferwallListener.onGetOfferwallCreditsFailed(sse);
        }
    }

    @Override
    public void onOfferwallInitSuccess() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onOfferwallInitSuccess ", 1);
        if (this.mOfferwallListener != null) {
            this.mOfferwallListener.onOfferwallAvailable(true);
        }
    }

    @Override
    public void onOfferwallInitFail(String description) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onOfferwallInitFail ", 1);
        if (this.mOfferwallListener != null) {
            IronSourceError sse = ErrorBuilder.buildGenericError(description);
            this.mOfferwallListener.onOfferwallAvailable(false, sse);
        }
    }

    @Override
    public void setInterstitialListener(InterstitialManagerListener manager) {
        this.mInterstitialManager = manager;
    }

    @Override
    public void initInterstitial(final Activity activity, final String appKey, final String userId) {
        ConfigValidationResult validationResult = this.validateConfigBeforeInitAndCallInitFailForInvalid(this.mAdapterConfig, this.mInterstitialManager);
        if (!validationResult.isValid()) {
            return;
        }
        this.startISInitTimer(this.mInterstitialManager);
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                SupersonicAdsAdapter.this.mSSAPublisher = SSAFactory.getPublisherInstance(activity);
                HashMap interstitialExtraParams = SupersonicAdsAdapter.this.getInterstitialExtraParams();
                SupersonicAdsAdapter.this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, SupersonicAdsAdapter.this.getProviderName() + ":initInterstitial(appKey:" + appKey + ", userId:" + userId + ", extraParams:" + interstitialExtraParams + ")", 1);
                SupersonicAdsAdapter.this.mSSAPublisher.initInterstitial(appKey, userId, interstitialExtraParams, SupersonicAdsAdapter.this);
            }
        });
    }

    @Override
    public void loadInterstitial() {
        this.startISLoadTimer(this.mInterstitialManager);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.loadInterstitial();
        } else {
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Please call initInterstitial before calling loadInterstitial", 2);
        }
    }

    @Override
    public void showInterstitial() {
    }

    @Override
    public void showInterstitial(String placementName) {
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.showInterstitial();
        } else {
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Please call loadInterstitial before calling showInterstitial", 2);
        }
    }

    @Override
    public boolean isInterstitialReady() {
        return this.mSSAPublisher != null && this.mSSAPublisher.isInterstitialAdAvailable();
    }

    @Override
    public void onInterstitialInitSuccess() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialInitSuccess ", 1);
        this.cancelISInitTimer();
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialInitSuccess(this);
        }
    }

    @Override
    public void onInterstitialInitFailed(String description) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialInitFailed ", 1);
        this.cancelISInitTimer();
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialInitFailed(ErrorBuilder.buildInitFailedError("Adapter initialization failure - " + this.getProviderName() + " - " + description, "Interstitial"), this);
        }
    }

    @Override
    public void onInterstitialLoadSuccess() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialLoadSuccess ", 1);
        this.cancelISLoadTimer();
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdReady(this);
        }
    }

    @Override
    public void onInterstitialLoadFailed(String description) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdLoadFailed ", 1);
        this.cancelISLoadTimer();
        if (this.mInterstitialManager != null) {
            String errorString = "Interstitial Load Fail, " + this.getProviderName() + " - " + description;
            IronSourceError error = ErrorBuilder.buildLoadFailedError(errorString);
            this.mInterstitialManager.onInterstitialAdLoadFailed(error, this);
        }
    }

    @Override
    public void onInterstitialOpen() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdOpened ", 1);
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdOpened(this);
        }
    }

    @Override
    public void onInterstitialClose() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdClosed ", 1);
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdClosed(this);
        }
    }

    @Override
    public void onInterstitialShowSuccess() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdShowSucceeded ", 1);
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdShowSucceeded(this);
        }
    }

    @Override
    public void onInterstitialShowFailed(String description) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdShowFailed ", 1);
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdShowFailed(ErrorBuilder.buildShowFailedError("Interstitial", description), this);
        }
    }

    @Override
    public void onInterstitialClick() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, this.getProviderName() + " :onInterstitialAdClicked ", 1);
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdClicked(this);
        }
    }

}

