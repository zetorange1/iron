/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.logger.IronSourceError;

public interface InterstitialListener {
    public void onInterstitialAdReady();

    public void onInterstitialAdLoadFailed(IronSourceError var1);

    public void onInterstitialAdOpened();

    public void onInterstitialAdClosed();

    public void onInterstitialAdShowSucceeded();

    public void onInterstitialAdShowFailed(IronSourceError var1);

    public void onInterstitialAdClicked();
}

