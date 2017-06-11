/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;

public interface RewardedVideoListener {
    public void onRewardedVideoAdOpened();

    public void onRewardedVideoAdClosed();

    public void onRewardedVideoAvailabilityChanged(boolean var1);

    public void onRewardedVideoAdStarted();

    public void onRewardedVideoAdEnded();

    public void onRewardedVideoAdRewarded(Placement var1);

    public void onRewardedVideoAdShowFailed(IronSourceError var1);
}

