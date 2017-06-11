/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.listeners;

import com.ironsource.sdk.data.AdUnitsReady;

public interface DSRewardedVideoListener {
    public void onRVInitSuccess(AdUnitsReady var1, String var2);

    public void onRVInitFail(String var1, String var2);

    public void onRVNoMoreOffers(String var1);

    public void onRVAdCredited(int var1, String var2);

    public void onRVAdClosed(String var1);

    public void onRVAdOpened(String var1);

    public void onRVShowFail(String var1, String var2);

    public void onRVAdClicked(String var1);
}

