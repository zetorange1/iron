/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.ironsource.eventsmodule;

import com.ironsource.eventsmodule.EventData;
import java.util.ArrayList;
import org.json.JSONObject;

public interface IEventsFormatter {
    public String format(ArrayList<EventData> var1, JSONObject var2);
}

