/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;

public interface RewardedVideoManagerListener {
    public void onRewardedVideoAdShowFailed(IronSourceError var1, AbstractAdapter var2);

    public void onRewardedVideoAdOpened(AbstractAdapter var1);

    public void onRewardedVideoAdClosed(AbstractAdapter var1);

    public void onRewardedVideoAvailabilityChanged(boolean var1, AbstractAdapter var2);

    public void onRewardedVideoAdStarted(AbstractAdapter var1);

    public void onRewardedVideoAdEnded(AbstractAdapter var1);

    public void onRewardedVideoAdRewarded(Placement var1, AbstractAdapter var2);
}

