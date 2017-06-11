/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.adapters.ris;

import android.app.Activity;
import com.ironsource.adapters.supersonicads.DemandSourceConfig;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.sdk.InterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoManagerListener;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.sdk.SSAFactory;
import com.ironsource.sdk.SSAPublisher;
import com.ironsource.sdk.data.AdUnitsReady;
import com.ironsource.sdk.listeners.OnRewardedVideoListener;
import com.ironsource.sdk.utils.SDKUtils;
import java.util.HashMap;
import java.util.Map;

public class RISAdapter
extends AbstractAdapter
implements OnRewardedVideoListener {
    private DemandSourceConfig mAdapterConfig;
    private SSAPublisher mSSAPublisher;
    private InterstitialManagerListener mInterstitialManager;
    private boolean hasAdAvailable = false;
    private boolean mDidReportInitStatus = false;

    public static RISAdapter startAdapter(String providerName, String providerUrl) {
        return new RISAdapter(providerName);
    }

    private RISAdapter(String providerName) {
        super(providerName, null);
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
        return 0;
    }

    @Override
    public int getMaxISAdsPerIteration() {
        return this.mAdapterConfig.getMaxISAdsPerIteration();
    }

    @Override
    public String getVersion() {
        return IronSourceUtils.getSDKVersion();
    }

    @Override
    public String getCoreSDKVersion() {
        return SDKUtils.getSDKVersion();
    }

    @Override
    public void initInterstitial(final Activity activity, final String appKey, final String userId) {
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":initInterstitial(userId:" + userId + ")", 1);
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                RISAdapter.this.mSSAPublisher = SSAFactory.getPublisherInstance(activity);
                SSAFactory.getPublisherInstance(activity).initRewardedVideo(appKey, userId, RISAdapter.this.getProviderName(), new HashMap<String, String>(), RISAdapter.this);
            }
        });
    }

    @Override
    public void loadInterstitial() {
        if (this.mInterstitialManager != null) {
            if (this.hasAdAvailable) {
                this.mInterstitialManager.onInterstitialAdReady(this);
            } else {
                this.mInterstitialManager.onInterstitialAdLoadFailed(ErrorBuilder.buildLoadFailedError("No ad available"), this);
            }
        }
    }

    @Override
    public void showInterstitial() {
    }

    @Override
    public void showInterstitial(String placementName) {
        this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + ":showRewardedVideo(placement:" + placementName + ")", 1);
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.showRewardedVideo(this.getProviderName());
        } else if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdShowFailed(new IronSourceError(509, "Please call init before calling showRewardedVideo"), this);
        }
    }

    @Override
    public boolean isInterstitialReady() {
        return this.hasAdAvailable;
    }

    @Override
    public void setRewardedVideoListener(RewardedVideoManagerListener manager) {
    }

    @Override
    public void initRewardedVideo(Activity activity, String appKey, String userId) {
    }

    @Override
    public void showRewardedVideo() {
    }

    @Override
    public boolean isRewardedVideoAvailable() {
        return false;
    }

    @Override
    public void showRewardedVideo(String placementName) {
    }

    @Override
    public void setInterstitialListener(InterstitialManagerListener manager) {
        this.mInterstitialManager = manager;
    }

    @Override
    public void onResume(Activity activity) {
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        if (this.mSSAPublisher != null) {
            this.mSSAPublisher.onPause(activity);
        }
    }

    @Override
    public void setAge(int age) {
        this.mAdapterConfig.setUserAgeGroup(age);
    }

    @Override
    public void setGender(String gender) {
        this.mAdapterConfig.setUserGender(gender);
    }

    @Override
    public void setMediationSegment(String segment) {
        this.mAdapterConfig.setMediationSegment(segment);
    }

    @Override
    public void onRVInitSuccess(AdUnitsReady adUnitsReady) {
        int numOfAdUnits = 0;
        try {
            numOfAdUnits = Integer.parseInt(adUnitsReady.getNumOfAdUnits());
        }
        catch (NumberFormatException e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "onRVInitSuccess:parseInt()", e);
        }
        boolean bl = this.hasAdAvailable = numOfAdUnits > 0;
        if (this.mInterstitialManager != null && !this.mDidReportInitStatus) {
            this.mDidReportInitStatus = true;
            this.mInterstitialManager.onInterstitialInitSuccess(this);
        }
    }

    @Override
    public void onRVInitFail(String description) {
        this.hasAdAvailable = false;
        if (this.mInterstitialManager != null && !this.mDidReportInitStatus) {
            this.mDidReportInitStatus = true;
            this.mInterstitialManager.onInterstitialInitFailed(ErrorBuilder.buildInitFailedError("Adapter initialization failure - " + this.getProviderName() + " - " + description, "Interstitial"), this);
        }
    }

    @Override
    public void onRVNoMoreOffers() {
        if (this.mInterstitialManager != null && !this.mDidReportInitStatus) {
            this.mDidReportInitStatus = true;
            this.mInterstitialManager.onInterstitialInitSuccess(this);
        }
    }

    @Override
    public void onRVAdCredited(int credits) {
        if (this.mRewardedInterstitialManager != null) {
            this.mRewardedInterstitialManager.onInterstitialAdRewarded(this);
        }
    }

    @Override
    public void onRVAdClosed() {
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdClosed(this);
        }
    }

    @Override
    public void onRVAdOpened() {
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdShowSucceeded(this);
            this.mInterstitialManager.onInterstitialAdOpened(this);
        }
    }

    @Override
    public void onRVShowFail(String description) {
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdShowFailed(new IronSourceError(509, "Show Failed"), this);
        }
    }

    @Override
    public void onRVAdClicked() {
        if (this.mInterstitialManager != null) {
            this.mInterstitialManager.onInterstitialAdClicked(this);
        }
    }

}

