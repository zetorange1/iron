/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.BannerAdaptersListener;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;

public interface BannerManagerListener
extends BannerAdaptersListener {
    public void onBannerInitSuccess(AbstractAdapter var1);

    public void onBannerInitFailed(IronSourceError var1, AbstractAdapter var2);

    public void onBannerImpression(AbstractAdapter var1, IronSourceBannerLayout var2);
}

