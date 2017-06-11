/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.eventsmodule;

import org.json.JSONException;
import org.json.JSONObject;

public class EventData {
    private int mEventId = -1;
    private long mTimeStamp = -1;
    private JSONObject mAdditionalData;

    public EventData(int eventId, long timeStamp, JSONObject additionalData) {
        this.mEventId = eventId;
        this.mTimeStamp = timeStamp;
        this.mAdditionalData = additionalData == null ? new JSONObject() : additionalData;
    }

    public EventData(int eventId, JSONObject additionalData) {
        this.mEventId = eventId;
        this.mTimeStamp = System.currentTimeMillis();
        this.mAdditionalData = additionalData == null ? new JSONObject() : additionalData;
    }

    public int getEventId() {
        return this.mEventId;
    }

    public long getTimeStamp() {
        return this.mTimeStamp;
    }

    public String getAdditionalData() {
        return this.mAdditionalData.toString();
    }

    public JSONObject getAdditionalDataJSON() {
        return this.mAdditionalData;
    }

    public void addToAdditionalData(String key, Object value) {
        try {
            this.mAdditionalData.put(key, value);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

