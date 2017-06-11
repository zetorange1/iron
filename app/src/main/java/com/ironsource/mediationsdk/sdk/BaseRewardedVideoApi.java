/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.mediationsdk.sdk;

import android.app.Activity;
import com.ironsource.mediationsdk.sdk.BaseApi;

public interface BaseRewardedVideoApi
extends BaseApi {
    public void initRewardedVideo(Activity var1, String var2, String var3);

    public void showRewardedVideo();

    public boolean isRewardedVideoAvailable();

    public void showRewardedVideo(String var1);
}

