/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.utils;

public class SessionDepthManager {
    private int mRewardedVideoDepth = 1;
    private int mInterstitialDepth = 1;
    private int mOfferwallDepth = 1;
    private int mBannerDepth = 0;
    public static final int NONE = -1;
    public static final int OFFERWALL = 0;
    public static final int REWARDEDVIDEO = 1;
    public static final int INTERSTITIAL = 2;
    public static final int BANNER = 3;
    private static SessionDepthManager mInstance;

    public static synchronized SessionDepthManager getInstance() {
        if (mInstance == null) {
            mInstance = new SessionDepthManager();
        }
        return mInstance;
    }

    public synchronized void increaseSessionDepth(int adUnit) {
        switch (adUnit) {
            case 0: {
                ++this.mOfferwallDepth;
                break;
            }
            case 1: {
                ++this.mRewardedVideoDepth;
                break;
            }
            case 2: {
                ++this.mInterstitialDepth;
                break;
            }
            case 3: {
                ++this.mBannerDepth;
                break;
            }
        }
    }

    public synchronized int getSessionDepth(int adUnit) {
        switch (adUnit) {
            case 0: {
                return this.mOfferwallDepth;
            }
            case 1: {
                return this.mRewardedVideoDepth;
            }
            case 2: {
                return this.mInterstitialDepth;
            }
            case 3: {
                return this.mBannerDepth;
            }
        }
        return -1;
    }
}

