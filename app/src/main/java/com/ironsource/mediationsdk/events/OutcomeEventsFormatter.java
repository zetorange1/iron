/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.events;

import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.events.AbstractEventsFormatter;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class OutcomeEventsFormatter
extends AbstractEventsFormatter {
    private final String DEFAULT_OC_EVENTS_URL = "https://outcome.supersonicads.com/mediation/";

    public OutcomeEventsFormatter(int adUnit) {
        this.mAdUnit = adUnit;
    }

    @Override
    public String getDefaultEventsUrl() {
        return "https://outcome.supersonicads.com/mediation/";
    }

    @Override
    public String getFormatterType() {
        return "outcome";
    }

    @Override
    public String format(ArrayList<EventData> toSend, JSONObject generalProperties) {
        this.mGeneralProperties = generalProperties == null ? new JSONObject() : generalProperties;
        JSONArray eventsArray = new JSONArray();
        if (toSend != null && !toSend.isEmpty()) {
            for (EventData event : toSend) {
                JSONObject jsonEvent = this.createJSONForEvent(event);
                if (jsonEvent == null) continue;
                eventsArray.put((Object)jsonEvent);
            }
        }
        return this.createDataToSend(eventsArray);
    }
}

