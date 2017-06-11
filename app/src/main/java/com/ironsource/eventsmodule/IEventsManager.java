/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.ironsource.eventsmodule;

import android.content.Context;
import com.ironsource.eventsmodule.EventData;

public interface IEventsManager {
    public void setBackupThreshold(int var1);

    public void setMaxNumberOfEvents(int var1);

    public void setMaxEventsPerBatch(int var1);

    public void setOptOutEvents(int[] var1, Context var2);

    public void setEventsUrl(String var1, Context var2);

    public void setIsEventsEnabled(boolean var1);

    public void log(EventData var1);

    public void setFormatterType(String var1, Context var2);
}

