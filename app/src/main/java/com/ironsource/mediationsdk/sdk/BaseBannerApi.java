/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.mediationsdk.sdk;

import android.app.Activity;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.sdk.BaseApi;

public interface BaseBannerApi
extends BaseApi {
    public IronSourceBannerLayout createBanner(Activity var1, EBannerSize var2);

    public void loadBanner(IronSourceBannerLayout var1, String var2);

    public void loadBanner(IronSourceBannerLayout var1);

    public void destroyBanner(IronSourceBannerLayout var1);
}

