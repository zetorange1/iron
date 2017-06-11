/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.listeners;

public interface OnInterstitialListener {
    public void onInterstitialInitSuccess();

    public void onInterstitialInitFailed(String var1);

    public void onInterstitialLoadSuccess();

    public void onInterstitialLoadFailed(String var1);

    public void onInterstitialOpen();

    public void onInterstitialClose();

    public void onInterstitialShowSuccess();

    public void onInterstitialShowFailed(String var1);

    public void onInterstitialClick();
}

