/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.logger;

import com.ironsource.mediationsdk.logger.ServerLogEntry;
import com.ironsource.mediationsdk.sdk.GeneralProperties;
import com.ironsource.mediationsdk.server.HttpFunctions;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class LogsSender
implements Runnable {
    private final String LOG_URL = "https://mobilelogs.supersonic.com";
    private final String AUTHO_USERNAME = "mobilelogs";
    private final String AUTHO_PASSWORD = "k@r@puz";
    private ArrayList<ServerLogEntry> mLogs;

    public LogsSender(ArrayList<ServerLogEntry> logs) {
        this.mLogs = logs;
    }

    private JSONObject getJSONToSend() {
        JSONObject logContent = new JSONObject();
        try {
            logContent.put("general_properties", (Object)GeneralProperties.getProperties().toJSON());
            JSONArray logData = new JSONArray();
            for (ServerLogEntry log : this.mLogs) {
                logData.put((Object)log.toJSON());
            }
            logContent.put("log_data", (Object)logData);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return logContent;
    }

    private void sendLogs(JSONObject logContent) {
        HttpFunctions.getStringFromPostWithAutho("https://mobilelogs.supersonic.com", logContent.toString(), "mobilelogs", "k@r@puz");
    }

    @Override
    public void run() {
    }
}

