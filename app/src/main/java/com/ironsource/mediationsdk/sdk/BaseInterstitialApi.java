/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.mediationsdk.sdk;

import android.app.Activity;
import com.ironsource.mediationsdk.sdk.BaseApi;

public interface BaseInterstitialApi
extends BaseApi {
    public void initInterstitial(Activity var1, String var2, String var3);

    public void loadInterstitial();

    public void showInterstitial();

    public void showInterstitial(String var1);

    public boolean isInterstitialReady();
}

