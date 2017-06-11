/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.ApplicationConfigurations;
import com.ironsource.mediationsdk.model.BannerConfigurations;
import com.ironsource.mediationsdk.model.InterstitialConfigurations;
import com.ironsource.mediationsdk.model.OfferwallConfigurations;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;

public class Configurations {
    private RewardedVideoConfigurations mRewardedVideoConfig;
    private InterstitialConfigurations mInterstitialConfig;
    private OfferwallConfigurations mOfferwallConfig;
    private BannerConfigurations mBannerConfig;
    private ApplicationConfigurations mApplicationConfig;

    public Configurations() {
    }

    public Configurations(RewardedVideoConfigurations rewardedVideoConfigurations, InterstitialConfigurations interstitialConfigurations, OfferwallConfigurations offerwallConfigurations, BannerConfigurations bannerConfigurations, ApplicationConfigurations applicationConfigurations) {
        if (rewardedVideoConfigurations != null) {
            this.mRewardedVideoConfig = rewardedVideoConfigurations;
        }
        if (interstitialConfigurations != null) {
            this.mInterstitialConfig = interstitialConfigurations;
        }
        if (offerwallConfigurations != null) {
            this.mOfferwallConfig = offerwallConfigurations;
        }
        if (bannerConfigurations != null) {
            this.mBannerConfig = bannerConfigurations;
        }
        this.mApplicationConfig = applicationConfigurations;
    }

    public ApplicationConfigurations getApplicationConfigurations() {
        return this.mApplicationConfig;
    }

    public RewardedVideoConfigurations getRewardedVideoConfigurations() {
        return this.mRewardedVideoConfig;
    }

    public InterstitialConfigurations getInterstitialConfigurations() {
        return this.mInterstitialConfig;
    }

    public OfferwallConfigurations getOfferwallConfigurations() {
        return this.mOfferwallConfig;
    }

    public BannerConfigurations getBannerConfigurations() {
        return this.mBannerConfig;
    }
}

