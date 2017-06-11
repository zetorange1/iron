/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.sdk.BaseInterstitialApi;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

public interface InterstitialApi
extends BaseInterstitialApi {
    public void setInterstitialListener(InterstitialListener var1);

    public boolean isInterstitialPlacementCapped(String var1);
}

