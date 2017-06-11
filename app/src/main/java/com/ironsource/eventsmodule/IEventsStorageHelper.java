/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.eventsmodule;

import com.ironsource.eventsmodule.EventData;
import java.util.ArrayList;
import java.util.List;

public interface IEventsStorageHelper {
    public void saveEvents(List<EventData> var1, String var2);

    public ArrayList<EventData> loadEvents(String var1);

    public void clearEvents(String var1);
}

