/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.events;

import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.events.AbstractEventsFormatter;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IronbeastEventsFormatter
extends AbstractEventsFormatter {
    private final String DEFAULT_IB_EVENTS_URL = "https://track.atom-data.io";
    private final String IB_TABLE_NAME = "super.dwh.mediation_events";
    private final String IB_KEY_TABLE = "table";
    private final String IB_KEY_DATA = "data";

    public IronbeastEventsFormatter(int adUnit) {
        this.mAdUnit = adUnit;
    }

    @Override
    public String getDefaultEventsUrl() {
        return "https://track.atom-data.io";
    }

    @Override
    public String getFormatterType() {
        return "ironbeast";
    }

    @Override
    public String format(ArrayList<EventData> toSend, JSONObject generalProperties) {
        JSONObject jsonBody = new JSONObject();
        this.mGeneralProperties = generalProperties == null ? new JSONObject() : generalProperties;
        try {
            JSONArray eventsArray = new JSONArray();
            if (toSend != null && !toSend.isEmpty()) {
                for (EventData event : toSend) {
                    JSONObject jsonEvent = this.createJSONForEvent(event);
                    if (jsonEvent == null) continue;
                    eventsArray.put((Object)jsonEvent);
                }
            }
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, "AAAAAAAAAAAA    " + eventsArray.toString(), 1);
            jsonBody.put("table", (Object)"super.dwh.mediation_events");
            jsonBody.put("data", (Object)this.createDataToSend(eventsArray));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody.toString();
    }
}

