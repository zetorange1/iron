/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.PlacementAvailabilitySettings;

public class Placement {
    private int mPlacementId;
    private String mPlacementName;
    private String mRewardName;
    private int mRewardAmount;
    private PlacementAvailabilitySettings mPlacementAvailabilitySettings;

    public Placement(int placementId, String placementName, String rewardName, int rewardAmount, PlacementAvailabilitySettings placementAvailabilitySettings) {
        this.mPlacementId = placementId;
        this.mPlacementName = placementName;
        this.mRewardName = rewardName;
        this.mRewardAmount = rewardAmount;
        this.mPlacementAvailabilitySettings = placementAvailabilitySettings;
    }

    public int getPlacementId() {
        return this.mPlacementId;
    }

    public String getPlacementName() {
        return this.mPlacementName;
    }

    public String getRewardName() {
        return this.mRewardName;
    }

    public int getRewardAmount() {
        return this.mRewardAmount;
    }

    public String toString() {
        return "placement name: " + this.mPlacementName + ", reward name: " + this.mRewardName + " , amount:" + this.mRewardAmount;
    }

    public PlacementAvailabilitySettings getPlacementAvailabilitySettings() {
        return this.mPlacementAvailabilitySettings;
    }
}

