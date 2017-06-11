/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.ironsource.mediationsdk.sdk;

import android.content.Context;
import com.ironsource.mediationsdk.logger.LoggingApi;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerApi;
import com.ironsource.mediationsdk.sdk.InterstitialApi;
import com.ironsource.mediationsdk.sdk.OfferwallApi;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialApi;
import com.ironsource.mediationsdk.sdk.RewardedVideoApi;

public interface IronSourceInterface
extends RewardedVideoApi,
InterstitialApi,
OfferwallApi,
LoggingApi,
RewardedInterstitialApi,
BannerApi {
    public void removeRewardedVideoListener();

    public void removeInterstitialListener();

    public void removeOfferwallListener();

    public Placement getRewardedVideoPlacementInfo(String var1);

    public InterstitialPlacement getInterstitialPlacementInfo(String var1);

    public String getAdvertiserId(Context var1);

    public void shouldTrackNetworkState(Context var1, boolean var2);

    public boolean setDynamicUserId(String var1);

    public void setAdaptersDebug(boolean var1);

    public void setMediationType(String var1);
}

