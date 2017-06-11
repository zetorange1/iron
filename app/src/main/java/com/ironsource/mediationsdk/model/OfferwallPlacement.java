/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

public class OfferwallPlacement {
    private int mPlacementId;
    private String mPlacementName;

    public OfferwallPlacement(int placementId, String placementName) {
        this.mPlacementId = placementId;
        this.mPlacementName = placementName;
    }

    public int getPlacementId() {
        return this.mPlacementId;
    }

    public String getPlacementName() {
        return this.mPlacementName;
    }

    public String toString() {
        return "placement name: " + this.mPlacementName + ", placement id: " + this.mPlacementId;
    }
}

