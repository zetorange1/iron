/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.events;

import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.events.BaseEventsManager;
import com.ironsource.mediationsdk.utils.SessionDepthManager;
import org.json.JSONObject;

public class InterstitialEventsManager
extends BaseEventsManager {
    private static InterstitialEventsManager sInstance;
    private String mCurrentISPlacement;
    private String mCurrentBNPlacement;

    private InterstitialEventsManager() {
        this.mFormatterType = "ironbeast";
        this.mAdUnitType = 2;
        this.mEventType = "IS";
        this.mCurrentISPlacement = "";
        this.mCurrentBNPlacement = "";
    }

    public static InterstitialEventsManager getInstance() {
        if (sInstance == null) {
            sInstance = new InterstitialEventsManager();
            sInstance.initState();
        }
        return sInstance;
    }

    @Override
    protected boolean shouldExtractCurrentPlacement(EventData event) {
        return event.getEventId() == 23 || event.getEventId() == 402;
    }

    @Override
    protected boolean shouldIncludeCurrentPlacement(EventData event) {
        return event.getEventId() == 25 || event.getEventId() == 26 || event.getEventId() == 28 || event.getEventId() == 29 || event.getEventId() == 34 || event.getEventId() == 405 || event.getEventId() == 407 || event.getEventId() == 408 || event.getEventId() == 414;
    }

    @Override
    protected boolean isTopPriorityEvent(EventData currentEvent) {
        return currentEvent.getEventId() == 26 || currentEvent.getEventId() == 405;
    }

    @Override
    protected int getSessionDepth(EventData event) {
        int sessionDepth = SessionDepthManager.getInstance().getSessionDepth(2);
        if (event.getEventId() >= 400 && event.getEventId() < 500) {
            sessionDepth = SessionDepthManager.getInstance().getSessionDepth(3);
        }
        return sessionDepth;
    }

    @Override
    protected boolean increaseSessionDepthIfNeeded(EventData event) {
        if (event.getEventId() == 26) {
            SessionDepthManager.getInstance().increaseSessionDepth(2);
            return false;
        }
        if (event.getEventId() == 402 && this.getProviderNameForEvent(event).equals("Mediation")) {
            SessionDepthManager.getInstance().increaseSessionDepth(3);
            return true;
        }
        return false;
    }

    @Override
    protected void setCurrentPlacement(EventData event) {
        if (event.getEventId() >= 400 && event.getEventId() < 500) {
            this.mCurrentBNPlacement = event.getAdditionalDataJSON().optString("placement");
        } else {
            this.mCurrentISPlacement = event.getAdditionalDataJSON().optString("placement");
        }
    }

    @Override
    protected String getCurrentPlacement(int eventId) {
        if (eventId >= 400 && eventId < 500) {
            return this.mCurrentBNPlacement;
        }
        return this.mCurrentISPlacement;
    }
}

