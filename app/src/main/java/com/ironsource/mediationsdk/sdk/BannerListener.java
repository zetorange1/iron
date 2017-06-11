/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.logger.IronSourceError;

public interface BannerListener {
    public void onBannerAdLoaded();

    public void onBannerAdLoadFailed(IronSourceError var1);

    public void onBannerAdClicked();

    public void onBannerAdScreenPresented();

    public void onBannerAdScreenDismissed();

    public void onBannerAdLeftApplication();
}

