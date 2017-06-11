/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.ApplicationEvents;
import com.ironsource.mediationsdk.model.Placement;
import java.util.ArrayList;

public class RewardedVideoConfigurations {
    private ArrayList<Placement> mRVPlacements = new ArrayList();
    private ApplicationEvents mRVEvents;
    private int mRVAdaptersSmartLoadAmount;
    private int mRVAdaptersTimeOutInSeconds;
    private String mRVAdaptersAlgorithm;
    private String mBackFillProviderName;
    private String mPremiumProviderName;
    private static final int DEFAULT_RV_PLACEMENT_ID = 0;
    private Placement mDefaultRVPlacement;

    public RewardedVideoConfigurations() {
        this.mRVEvents = new ApplicationEvents();
    }

    public RewardedVideoConfigurations(int adaptersSmartLoadAmount, int adaptersSmartLoadTimeout, String adaptersAlgorithm, ApplicationEvents events) {
        this.mRVAdaptersSmartLoadAmount = adaptersSmartLoadAmount;
        this.mRVAdaptersTimeOutInSeconds = adaptersSmartLoadTimeout;
        this.mRVAdaptersAlgorithm = adaptersAlgorithm;
        this.mRVEvents = events;
    }

    public int getRewardedVideoAdaptersSmartLoadTimeout() {
        return this.mRVAdaptersTimeOutInSeconds;
    }

    public void addRewardedVideoPlacement(Placement placement) {
        if (placement != null) {
            this.mRVPlacements.add(placement);
            if (placement.getPlacementId() == 0) {
                this.mDefaultRVPlacement = placement;
            }
        }
    }

    public Placement getRewardedVideoPlacement(String placementName) {
        for (Placement placement : this.mRVPlacements) {
            if (!placement.getPlacementName().equals(placementName)) continue;
            return placement;
        }
        return null;
    }

    public Placement getDefaultRewardedVideoPlacement() {
        return this.mDefaultRVPlacement;
    }

    public int getRewardedVideoAdaptersSmartLoadAmount() {
        return this.mRVAdaptersSmartLoadAmount;
    }

    public String getRewardedVideoAdapterAlgorithm() {
        return this.mRVAdaptersAlgorithm;
    }

    public ApplicationEvents getRewardedVideoEventsConfigurations() {
        return this.mRVEvents;
    }

    public String getBackFillProviderName() {
        return this.mBackFillProviderName;
    }

    public void setBackFillProviderName(String backFillProviderName) {
        this.mBackFillProviderName = backFillProviderName;
    }

    public String getPremiumProviderName() {
        return this.mPremiumProviderName;
    }

    public void setPremiumProviderName(String premiumProviderName) {
        this.mPremiumProviderName = premiumProviderName;
    }
}

