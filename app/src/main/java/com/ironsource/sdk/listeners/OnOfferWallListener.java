/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.listeners;

public interface OnOfferWallListener {
    public void onOWShowSuccess(String var1);

    public void onOWShowFail(String var1);

    public boolean onOWAdCredited(int var1, int var2, boolean var3);

    public void onGetOWCreditsFailed(String var1);

    public void onOWAdClosed();

    public void onOWGeneric(String var1, String var2);

    public void onOfferwallInitSuccess();

    public void onOfferwallInitFail(String var1);
}

