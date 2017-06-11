/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.sdk.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AdUnitsState
implements Parcelable {
    private String mRVAppKey;
    private String mRVUserId;
    private String mDisplayedDemandSourceName;
    private boolean mShouldRestore;
    private int mDisplayedProduct;
    private boolean mInterstitialReportInit;
    private boolean mInterstitialInitSuccess;
    private boolean mInterstitialReportLoad;
    private boolean mInterstitialLoadSuccess;
    private String mInterstitialAppKey;
    private String mInterstitialUserId;
    private Map<String, String> mInterstitialExtraParams;
    private boolean mOfferwallReportInit;
    private boolean mOfferwallInitSuccess;
    private Map<String, String> mOfferWallExtraParams;
    public static final Parcelable.Creator<AdUnitsState> CREATOR = new Parcelable.Creator<AdUnitsState>(){

        public AdUnitsState createFromParcel(Parcel source) {
            return new AdUnitsState(source);
        }

        public AdUnitsState[] newArray(int size) {
            return new AdUnitsState[size];
        }
    };

    public AdUnitsState() {
        this.initialize();
    }

    private AdUnitsState(Parcel source) {
        this.initialize();
        try {
            this.mShouldRestore = source.readByte() != 0;
            this.mDisplayedProduct = source.readInt();
            this.mRVAppKey = source.readString();
            this.mRVUserId = source.readString();
            this.mDisplayedDemandSourceName = source.readString();
            this.mInterstitialReportInit = source.readByte() != 0;
            this.mInterstitialInitSuccess = source.readByte() != 0;
            this.mInterstitialAppKey = source.readString();
            this.mInterstitialUserId = source.readString();
            this.mInterstitialExtraParams = this.getMapFromJsonString(source.readString());
            this.mOfferwallInitSuccess = source.readByte() != 0;
            this.mOfferwallReportInit = source.readByte() != 0;
            this.mOfferWallExtraParams = this.getMapFromJsonString(source.readString());
        }
        catch (Throwable e) {
            this.initialize();
        }
    }

    private void initialize() {
        this.mShouldRestore = false;
        this.mDisplayedProduct = -1;
        this.mInterstitialReportInit = true;
        this.mOfferwallReportInit = true;
        this.mOfferwallInitSuccess = false;
        this.mInterstitialInitSuccess = false;
        this.mInterstitialUserId = "";
        this.mInterstitialAppKey = "";
        this.mInterstitialExtraParams = new HashMap<String, String>();
        this.mOfferWallExtraParams = new HashMap<String, String>();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeByte((byte) (this.mShouldRestore ? 1 : 0));
            dest.writeInt(this.mDisplayedProduct);
            dest.writeString(this.mRVAppKey);
            dest.writeString(this.mRVUserId);
            dest.writeString(this.mDisplayedDemandSourceName);
            dest.writeByte((byte) (this.mInterstitialReportInit ? 1 : 0));
            dest.writeByte((byte) (this.mInterstitialInitSuccess ? 1 : 0));
            dest.writeString(this.mInterstitialAppKey);
            dest.writeString(this.mInterstitialUserId);
            dest.writeString(new JSONObject(this.mInterstitialExtraParams).toString());
            dest.writeByte((byte) (this.mOfferwallInitSuccess ? 1 : 0));
            dest.writeByte((byte) (this.mOfferwallReportInit ? 1 : 0));
            dest.writeString(new JSONObject(this.mOfferWallExtraParams).toString());
        }
        catch (Throwable var3_3) {
            // empty catch block
        }
    }

    public boolean isInterstitialInitSuccess() {
        return this.mInterstitialInitSuccess;
    }

    public boolean isInterstitialLoadSuccess() {
        return this.mInterstitialLoadSuccess;
    }

    public String getInterstitialAppKey() {
        return this.mInterstitialAppKey;
    }

    public String getInterstitialUserId() {
        return this.mInterstitialUserId;
    }

    public Map<String, String> getInterstitialExtraParams() {
        return this.mInterstitialExtraParams;
    }

    public boolean reportInitInterstitial() {
        return this.mInterstitialReportInit;
    }

    public boolean reportLoadInterstitial() {
        return this.mInterstitialReportLoad;
    }

    public boolean shouldRestore() {
        return this.mShouldRestore;
    }

    public int getDisplayedProduct() {
        return this.mDisplayedProduct;
    }

    public String getDisplayedDemandSourceName() {
        return this.mDisplayedDemandSourceName;
    }

    public boolean getOfferwallInitSuccess() {
        return this.mOfferwallInitSuccess;
    }

    public Map<String, String> getOfferWallExtraParams() {
        return this.mOfferWallExtraParams;
    }

    public boolean reportInitOfferwall() {
        return this.mOfferwallReportInit;
    }

    public void setOfferWallExtraParams(Map<String, String> offerWallExtraParams) {
        this.mOfferWallExtraParams = offerWallExtraParams;
    }

    public void setInterstitialInitSuccess(boolean mInterstitialInitSuccess) {
        this.mInterstitialInitSuccess = mInterstitialInitSuccess;
    }

    public void setInterstitialLoadSuccess(boolean mInterstitialLoadSuccess) {
        this.mInterstitialLoadSuccess = mInterstitialLoadSuccess;
    }

    public void setInterstitialAppKey(String mInterstitialAppKey) {
        this.mInterstitialAppKey = mInterstitialAppKey;
    }

    public void setInterstitialUserId(String mInterstitialUserId) {
        this.mInterstitialUserId = mInterstitialUserId;
    }

    public void setInterstitialExtraParams(Map<String, String> mInterstitialExtraParams) {
        this.mInterstitialExtraParams = mInterstitialExtraParams;
    }

    public void setReportInitInterstitial(boolean shouldReport) {
        this.mInterstitialReportInit = shouldReport;
    }

    public void setReportLoadInterstitial(boolean shouldReport) {
        this.mInterstitialReportLoad = shouldReport;
    }

    public void setShouldRestore(boolean mShouldRestore) {
        this.mShouldRestore = mShouldRestore;
    }

    public void adOpened(int product) {
        this.mDisplayedProduct = product;
    }

    public void adClosed() {
        this.mDisplayedProduct = -1;
    }

    public void setOfferwallInitSuccess(boolean offerwallInitSuccess) {
        this.mOfferwallInitSuccess = offerwallInitSuccess;
    }

    public void setOfferwallReportInit(boolean offerwallReportInit) {
        this.mOfferwallReportInit = offerwallReportInit;
    }

    public String getRVAppKey() {
        return this.mRVAppKey;
    }

    public void setRVAppKey(String mRVAppKey) {
        this.mRVAppKey = mRVAppKey;
    }

    public String getRVUserId() {
        return this.mRVUserId;
    }

    public void setDisplayedDemandSourceName(String displayedDemandSourceName) {
        this.mDisplayedDemandSourceName = displayedDemandSourceName;
    }

    public void setRVUserId(String mRVUserId) {
        this.mRVUserId = mRVUserId;
    }

    private Map<String, String> getMapFromJsonString(String jsonString) {
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            JSONObject json = new JSONObject(jsonString);
            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                String value = json.getString(key);
                result.put(key, value);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("shouldRestore:").append(this.mShouldRestore).append(", ");
            builder.append("displayedProduct:").append(this.mDisplayedProduct).append(", ");
            builder.append("ISReportInit:").append(this.mInterstitialReportInit).append(", ");
            builder.append("ISInitSuccess:").append(this.mInterstitialInitSuccess).append(", ");
            builder.append("ISAppKey").append(this.mInterstitialAppKey).append(", ");
            builder.append("ISUserId").append(this.mInterstitialUserId).append(", ");
            builder.append("ISExtraParams").append(this.mInterstitialExtraParams).append(", ");
            builder.append("OWReportInit").append(this.mOfferwallReportInit).append(", ");
            builder.append("OWInitSuccess").append(this.mOfferwallInitSuccess).append(", ");
            builder.append("OWExtraParams").append(this.mOfferWallExtraParams).append(", ");
        }
        catch (Throwable var2_2) {
            // empty catch block
        }
        return builder.toString();
    }

}

