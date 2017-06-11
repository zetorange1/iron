/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.data;

import com.ironsource.sdk.data.SSAObj;

public class AdUnitsReady
extends SSAObj {
    private static String TYPE = "type";
    private static String NUM_OF_AD_UNITS = "numOfAdUnits";
    private static String FIRST_CAMPAIGN_CREDITS = "firstCampaignCredits";
    private static String TOTAL_NUMBER_CREDITS = "totalNumberCredits";
    private static String PRODUCT_TYPE = "productType";
    private String mType;
    private String mProductType;
    private String mNumOfAdUnits;
    private String mFirstCampaignCredits;
    private String mTotalNumberCredits;
    private boolean mNumOfAdUnitsExist;

    public AdUnitsReady(String value) {
        super(value);
        if (this.containsKey(TYPE)) {
            this.setType(this.getString(TYPE));
        }
        if (this.containsKey(NUM_OF_AD_UNITS)) {
            this.setNumOfAdUnits(this.getString(NUM_OF_AD_UNITS));
            this.setNumOfAdUnitsExist(true);
        } else {
            this.setNumOfAdUnitsExist(false);
        }
        if (this.containsKey(FIRST_CAMPAIGN_CREDITS)) {
            this.setFirstCampaignCredits(this.getString(FIRST_CAMPAIGN_CREDITS));
        }
        if (this.containsKey(TOTAL_NUMBER_CREDITS)) {
            this.setTotalNumberCredits(this.getString(TOTAL_NUMBER_CREDITS));
        }
        if (this.containsKey(PRODUCT_TYPE)) {
            this.setProductType(this.getString(PRODUCT_TYPE));
        }
    }

    public String getType() {
        return this.mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getNumOfAdUnits() {
        return this.mNumOfAdUnits;
    }

    public void setNumOfAdUnits(String numOfAdUnits) {
        this.mNumOfAdUnits = numOfAdUnits;
    }

    public String getFirstCampaignCredits() {
        return this.mFirstCampaignCredits;
    }

    public void setFirstCampaignCredits(String firstCampaignCredits) {
        this.mFirstCampaignCredits = firstCampaignCredits;
    }

    public String getTotalNumberCredits() {
        return this.mTotalNumberCredits;
    }

    public void setTotalNumberCredits(String totalNumberCredits) {
        this.mTotalNumberCredits = totalNumberCredits;
    }

    private void setNumOfAdUnitsExist(boolean value) {
        this.mNumOfAdUnitsExist = value;
    }

    public boolean isNumOfAdUnitsExist() {
        return this.mNumOfAdUnitsExist;
    }

    public String getProductType() {
        return this.mProductType;
    }

    public void setProductType(String productType) {
        this.mProductType = productType;
    }
}

