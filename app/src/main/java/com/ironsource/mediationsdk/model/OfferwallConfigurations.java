/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.OfferwallPlacement;
import java.util.ArrayList;

public class OfferwallConfigurations {
    private ArrayList<OfferwallPlacement> mOWPlacements = new ArrayList();
    private static final int DEFAULT_OW_PLACEMENT_ID = 0;
    private OfferwallPlacement mDefaultOWPlacement;

    public void addOfferwallPlacement(OfferwallPlacement placement) {
        if (placement != null) {
            this.mOWPlacements.add(placement);
            if (0 == placement.getPlacementId()) {
                this.mDefaultOWPlacement = placement;
            }
        }
    }

    public OfferwallPlacement getOfferwallPlacement(String placementName) {
        for (OfferwallPlacement placement : this.mOWPlacements) {
            if (!placement.getPlacementName().equals(placementName)) continue;
            return placement;
        }
        return null;
    }

    public OfferwallPlacement getDefaultOfferwallPlacement() {
        return this.mDefaultOWPlacement;
    }
}

