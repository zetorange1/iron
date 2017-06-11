/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.sdk.BaseRewardedVideoApi;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

public interface RewardedVideoApi
extends BaseRewardedVideoApi {
    public void setRewardedVideoListener(RewardedVideoListener var1);

    public boolean isRewardedVideoPlacementCapped(String var1);
}

