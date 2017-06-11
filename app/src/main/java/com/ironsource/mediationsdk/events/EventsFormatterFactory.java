/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.events;

import com.ironsource.mediationsdk.events.AbstractEventsFormatter;
import com.ironsource.mediationsdk.events.IronbeastEventsFormatter;
import com.ironsource.mediationsdk.events.OutcomeEventsFormatter;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;

class EventsFormatterFactory {
    public static final String TYPE_IRONBEAST = "ironbeast";
    public static final String TYPE_OUTCOME = "outcome";
    public static final int AD_UNIT_REWARDED_VIDEO = 3;
    public static final int AD_UNIT_INTERSTITIAL = 2;

    EventsFormatterFactory() {
    }

    public static AbstractEventsFormatter getFormatter(String type, int adUnit) {
        if ("ironbeast".equals(type)) {
            return new IronbeastEventsFormatter(adUnit);
        }
        if ("outcome".equals(type)) {
            return new OutcomeEventsFormatter(adUnit);
        }
        if (adUnit == 2) {
            return new IronbeastEventsFormatter(adUnit);
        }
        if (adUnit == 3) {
            return new OutcomeEventsFormatter(adUnit);
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, "EventsFormatterFactory failed to instantiate a formatter (type: " + type + ", adUnit: " + adUnit + ")", 2);
        return null;
    }
}

