/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.sdk.data;

import android.content.Context;
import com.ironsource.environment.ConnectivityService;
import com.ironsource.sdk.utils.SDKUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SSASession {
    public final String SESSION_START_TIME = "sessionStartTime";
    public final String SESSION_END_TIME = "sessionEndTime";
    public final String SESSION_TYPE = "sessionType";
    public final String CONNECTIVITY = "connectivity";
    private long sessionStartTime;
    private long sessionEndTime;
    private SessionType sessionType;
    private String connectivity;

    public SSASession(Context context, SessionType type) {
        this.setSessionStartTime(SDKUtils.getCurrentTimeMillis());
        this.setSessionType(type);
        this.setConnectivity(ConnectivityService.getConnectionType(context));
    }

    public SSASession(JSONObject jsonObj) {
        try {
            jsonObj.get("sessionStartTime");
            jsonObj.get("sessionEndTime");
            jsonObj.get("sessionType");
            jsonObj.get("connectivity");
        }
        catch (JSONException var2_2) {
            // empty catch block
        }
    }

    public void endSession() {
        this.setSessionEndTime(SDKUtils.getCurrentTimeMillis());
    }

    public long getSessionStartTime() {
        return this.sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public long getSessionEndTime() {
        return this.sessionEndTime;
    }

    public void setSessionEndTime(long sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public SessionType getSessionType() {
        return this.sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getConnectivity() {
        return this.connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public static enum SessionType {
        launched,
        backFromBG;
        

        private SessionType() {
        }
    }

}

