/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.logger.IronSourceError;

public interface InterstitialManagerListener {
    public void onInterstitialInitSuccess(AbstractAdapter var1);

    public void onInterstitialInitFailed(IronSourceError var1, AbstractAdapter var2);

    public void onInterstitialAdReady(AbstractAdapter var1);

    public void onInterstitialAdLoadFailed(IronSourceError var1, AbstractAdapter var2);

    public void onInterstitialAdOpened(AbstractAdapter var1);

    public void onInterstitialAdClosed(AbstractAdapter var1);

    public void onInterstitialAdShowSucceeded(AbstractAdapter var1);

    public void onInterstitialAdShowFailed(IronSourceError var1, AbstractAdapter var2);

    public void onInterstitialAdClicked(AbstractAdapter var1);
}

