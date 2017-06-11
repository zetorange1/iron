/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.sdk;

import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

public class GeneralProperties {
    public static final String ALLOW_LOCATION_SHARED_PREFS_KEY = "GeneralProperties.ALLOW_LOCATION_SHARED_PREFS_KEY";
    public static final String USER_ID_TYPE = "userIdType";
    private static GeneralProperties mInstance;
    private JSONObject mProperties = new JSONObject();

    private GeneralProperties() {
    }

    public static synchronized GeneralProperties getProperties() {
        if (mInstance == null) {
            mInstance = new GeneralProperties();
        }
        return mInstance;
    }

    public synchronized void putKeys(Map<String, Object> properties) {
        if (properties != null) {
            for (String key : properties.keySet()) {
                this.putKey(key, properties.get(key));
            }
        }
    }

    public synchronized void putKey(String key, Object value) {
        try {
            this.mProperties.put(key, value);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
    }

    public synchronized String get(String key) {
        return this.mProperties.optString(key);
    }

    public synchronized JSONObject toJSON() {
        return this.mProperties;
    }
}

