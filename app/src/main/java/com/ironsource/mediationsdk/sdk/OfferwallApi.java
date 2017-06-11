/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.mediationsdk.sdk;

import android.app.Activity;
import com.ironsource.mediationsdk.sdk.BaseApi;
import com.ironsource.mediationsdk.sdk.OfferwallListener;

public interface OfferwallApi
extends BaseApi {
    public void showOfferwall();

    public void showOfferwall(String var1);

    public boolean isOfferwallAvailable();

    public void getOfferwallCredits();

    public void setOfferwallListener(OfferwallListener var1);

    public void initOfferwall(Activity var1, String var2, String var3);
}

