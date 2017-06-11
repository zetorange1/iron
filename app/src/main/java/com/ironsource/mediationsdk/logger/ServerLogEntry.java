/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.logger;

import com.ironsource.mediationsdk.logger.IronSourceLogger;
import org.json.JSONException;
import org.json.JSONObject;

class ServerLogEntry {
    private IronSourceLogger.IronSourceTag mTag;
    private String mTimetamp;
    private String mMessage;
    private int mLogLevel;

    public ServerLogEntry(IronSourceLogger.IronSourceTag tag, String timestamp, String message, int level) {
        this.mTag = tag;
        this.mTimetamp = timestamp;
        this.mMessage = message;
        this.mLogLevel = level;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        try {
            result.put("timestamp", (Object)this.mTimetamp);
            result.put("tag", (Object)this.mTag);
            result.put("level", this.mLogLevel);
            result.put("message", (Object)this.mMessage);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getLogLevel() {
        return this.mLogLevel;
    }
}

