/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.PlacementAvailabilitySettings;

public class InterstitialPlacement {
    private int mPlacementId;
    private String mPlacementName;
    private PlacementAvailabilitySettings mPlacementAvailabilitySettings;

    public InterstitialPlacement(int placementId, String placementName, PlacementAvailabilitySettings placementAvailabilitySettings) {
        this.mPlacementId = placementId;
        this.mPlacementName = placementName;
        this.mPlacementAvailabilitySettings = placementAvailabilitySettings;
    }

    public int getPlacementId() {
        return this.mPlacementId;
    }

    public String getPlacementName() {
        return this.mPlacementName;
    }

    public String toString() {
        return "placement name: " + this.mPlacementName;
    }

    public PlacementAvailabilitySettings getPlacementAvailabilitySettings() {
        return this.mPlacementAvailabilitySettings;
    }
}

