/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

public abstract class IronSource {
    public static void setUserId(String userId) {
        IronSourceObject.getInstance().setIronSourceUserId(userId);
    }

    public static void init(Activity activity, String appKey) {
        IronSource.init(activity, appKey, null);
    }

    public static /* varargs */ void init(Activity activity, String appKey, AD_UNIT ... adUnits) {
        IronSourceObject.getInstance().init(activity, appKey, adUnits);
    }

    public static void onResume(Activity activity) {
        IronSourceObject.getInstance().onResume(activity);
    }

    public static void onPause(Activity activity) {
        IronSourceObject.getInstance().onPause(activity);
    }

    public static synchronized void setAge(int age) {
        IronSourceObject.getInstance().setAge(age);
    }

    public static synchronized void setGender(String gender) {
        IronSourceObject.getInstance().setGender(gender);
    }

    public static void setMediationSegment(String segment) {
        IronSourceObject.getInstance().setMediationSegment(segment);
    }

    public static boolean setDynamicUserId(String dynamicUserId) {
        return IronSourceObject.getInstance().setDynamicUserId(dynamicUserId);
    }

    public static void setMediationType(String mediationType) {
        IronSourceObject.getInstance().setMediationType(mediationType);
    }

    public static void removeRewardedVideoListener() {
        IronSourceObject.getInstance().removeRewardedVideoListener();
    }

    public static void removeInterstitialListener() {
        IronSourceObject.getInstance().removeInterstitialListener();
    }

    public static void removeOfferwallListener() {
        IronSourceObject.getInstance().removeOfferwallListener();
    }

    public static InterstitialPlacement getInterstitialPlacementInfo(String placementName) {
        return IronSourceObject.getInstance().getInterstitialPlacementInfo(placementName);
    }

    public static Placement getRewardedVideoPlacementInfo(String placementName) {
        return IronSourceObject.getInstance().getRewardedVideoPlacementInfo(placementName);
    }

    public static String getAdvertiserId(Context context) {
        return IronSourceObject.getInstance().getAdvertiserId(context);
    }

    public static void shouldTrackNetworkState(Context context, boolean track) {
        IronSourceObject.getInstance().shouldTrackNetworkState(context, track);
    }

    public static void setLogListener(LogListener listener) {
        IronSourceObject.getInstance().setLogListener(listener);
    }

    public static void setAdaptersDebug(boolean enabled) {
        IronSourceObject.getInstance().setAdaptersDebug(enabled);
    }

    public static void showRewardedVideo() {
        IronSourceObject.getInstance().showRewardedVideo();
    }

    public static void showRewardedVideo(String placementName) {
        IronSourceObject.getInstance().showRewardedVideo(placementName);
    }

    public static boolean isRewardedVideoAvailable() {
        return IronSourceObject.getInstance().isRewardedVideoAvailable();
    }

    public static void setRewardedVideoListener(RewardedVideoListener listener) {
        IronSourceObject.getInstance().setRewardedVideoListener(listener);
    }

    public static boolean isRewardedVideoPlacementCapped(String placementName) {
        return IronSourceObject.getInstance().isRewardedVideoPlacementCapped(placementName);
    }

    public static void loadInterstitial() {
        IronSourceObject.getInstance().loadInterstitial();
    }

    public static void showInterstitial() {
        IronSourceObject.getInstance().showInterstitial();
    }

    public static void showInterstitial(String placementName) {
        IronSourceObject.getInstance().showInterstitial(placementName);
    }

    public static boolean isInterstitialReady() {
        return IronSourceObject.getInstance().isInterstitialReady();
    }

    public static void setInterstitialListener(InterstitialListener listener) {
        IronSourceObject.getInstance().setInterstitialListener(listener);
    }

    public static void setRewardedInterstitialListener(RewardedInterstitialListener listener) {
        IronSourceObject.getInstance().setRewardedInterstitialListener(listener);
    }

    public static boolean isInterstitialPlacementCapped(String placementName) {
        return IronSourceObject.getInstance().isInterstitialPlacementCapped(placementName);
    }

    public static void showOfferwall() {
        IronSourceObject.getInstance().showOfferwall();
    }

    public static void showOfferwall(String placementName) {
        IronSourceObject.getInstance().showOfferwall(placementName);
    }

    public static boolean isOfferwallAvailable() {
        return IronSourceObject.getInstance().isOfferwallAvailable();
    }

    public static void getOfferwallCredits() {
        IronSourceObject.getInstance().getOfferwallCredits();
    }

    public static void setOfferwallListener(OfferwallListener listener) {
        IronSourceObject.getInstance().setOfferwallListener(listener);
    }

    public static IronSourceBannerLayout createBanner(Activity activity, EBannerSize size) {
        return IronSourceObject.getInstance().createBanner(activity, size);
    }

    public static void loadBanner(IronSourceBannerLayout banner) {
        IronSourceObject.getInstance().loadBanner(banner);
    }

    public static void loadBanner(IronSourceBannerLayout banner, String placementName) {
        IronSourceObject.getInstance().loadBanner(banner, placementName);
    }

    public static void destroyBanner(IronSourceBannerLayout banner) {
        IronSourceObject.getInstance().destroyBanner(banner);
    }

    public static boolean isBannerPlacementCapped(String placementName) {
        return IronSourceObject.getInstance().isBannerPlacementCapped(placementName);
    }

    public static enum AD_UNIT {
        REWARDED_VIDEO("rewardedVideo"),
        INTERSTITIAL("interstitial"),
        OFFERWALL("offerwall"),
        BANNER("banner");
        
        private String mValue;

        private AD_UNIT(String value) {
            this.mValue = value;
        }

        public String toString() {
            return this.mValue;
        }
    }

}

