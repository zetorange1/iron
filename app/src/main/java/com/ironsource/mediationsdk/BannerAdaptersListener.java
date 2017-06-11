/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk;

import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.logger.IronSourceError;

public interface BannerAdaptersListener {
    public void onBannerAdLoaded(AbstractAdapter var1);

    public void onBannerAdLoadFailed(IronSourceError var1, AbstractAdapter var2);

    public void onBannerAdClicked(AbstractAdapter var1);

    public void onBannerAdScreenPresented(AbstractAdapter var1);

    public void onBannerAdScreenDismissed(AbstractAdapter var1);

    public void onBannerAdLeftApplication(AbstractAdapter var1);
}

