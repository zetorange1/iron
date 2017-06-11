/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.sdk;

import android.app.Activity;
import com.ironsource.sdk.listeners.OnGenericFunctionListener;
import com.ironsource.sdk.listeners.OnInterstitialListener;
import com.ironsource.sdk.listeners.OnOfferWallListener;
import com.ironsource.sdk.listeners.OnRewardedVideoListener;
import java.util.Map;

public interface SSAPublisher {
    public void initOfferWall(String var1, String var2, Map<String, String> var3, OnOfferWallListener var4);

    public void showOfferWall(Map<String, String> var1);

    public void getOfferWallCredits(String var1, String var2, OnOfferWallListener var3);

    public void initRewardedVideo(String var1, String var2, String var3, Map<String, String> var4, OnRewardedVideoListener var5);

    public void showRewardedVideo(String var1);

    public void initInterstitial(String var1, String var2, Map<String, String> var3, OnInterstitialListener var4);

    public void loadInterstitial();

    public void showInterstitial();

    public void forceShowInterstitial();

    public boolean isInterstitialAdAvailable();

    public void release(Activity var1);

    public void onPause(Activity var1);

    public void onResume(Activity var1);

    public void runGenericFunction(String var1, Map<String, String> var2, OnGenericFunctionListener var3);
}

