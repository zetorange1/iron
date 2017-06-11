/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.events;

import android.text.TextUtils;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractEventsFormatter {
    private final String KEY_EVENT_ID = "eventId";
    private final String KEY_TIMESTAMP = "timestamp";
    private final String KEY_AD_UNIT = "adUnit";
    private final String EVENTS_KEY_IS = "InterstitialEvents";
    private final String EVENTS_KEY_RV = "events";
    private final String EVENTS_KEY_DEFAULT = "events";
    JSONObject mGeneralProperties;
    int mAdUnit;
    private String mServerUrl;

    private String getEventsKey(int adUnit) {
        switch (adUnit) {
            case 2: {
                return "InterstitialEvents";
            }
            case 3: {
                return "events";
            }
        }
        return "events";
    }

    JSONObject createJSONForEvent(EventData event) {
        JSONObject jsonEvent;
        try {
            jsonEvent = new JSONObject(event.getAdditionalData());
            jsonEvent.put("eventId", event.getEventId());
            jsonEvent.put("timestamp", event.getTimeStamp());
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonEvent = null;
        }
        return jsonEvent;
    }

    String createDataToSend(JSONArray eventsArray) {
        String result = "";
        try {
            if (this.mGeneralProperties != null) {
                JSONObject data = new JSONObject(this.mGeneralProperties.toString());
                long timeStamp = IronSourceUtils.getTimeStamp();
                data.put("timestamp", timeStamp);
                data.put("adUnit", this.mAdUnit);
                data.put(this.getEventsKey(this.mAdUnit), (Object)eventsArray);
                result = data.toString();
            }
        }
        catch (Exception data) {
            // empty catch block
        }
        return result;
    }

    public String getEventsServerUrl() {
        return TextUtils.isEmpty((CharSequence)this.mServerUrl) ? this.getDefaultEventsUrl() : this.mServerUrl;
    }

    public void setEventsServerUrl(String url) {
        this.mServerUrl = url;
    }

    protected abstract String getDefaultEventsUrl();

    public abstract String getFormatterType();

    public abstract String format(ArrayList<EventData> var1, JSONObject var2);
}

