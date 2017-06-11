/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.listeners;

import com.ironsource.sdk.data.AdUnitsReady;

public interface OnRewardedVideoListener {
    public void onRVInitSuccess(AdUnitsReady var1);

    public void onRVInitFail(String var1);

    public void onRVNoMoreOffers();

    public void onRVAdCredited(int var1);

    public void onRVAdClosed();

    public void onRVAdOpened();

    public void onRVShowFail(String var1);

    public void onRVAdClicked();
}

