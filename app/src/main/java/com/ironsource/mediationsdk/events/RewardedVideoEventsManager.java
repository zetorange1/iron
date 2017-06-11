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

public class RewardedVideoEventsManager
extends BaseEventsManager {
    private static RewardedVideoEventsManager sInstance;
    private String mCurrentRVPlacment;
    private String mCurrentOWPlacment;

    private RewardedVideoEventsManager() {
        this.mFormatterType = "outcome";
        this.mAdUnitType = 3;
        this.mEventType = "RV";
        this.mCurrentRVPlacment = "";
        this.mCurrentOWPlacment = "";
    }

    public static RewardedVideoEventsManager getInstance() {
        if (sInstance == null) {
            sInstance = new RewardedVideoEventsManager();
            sInstance.initState();
        }
        return sInstance;
    }

    @Override
    protected boolean shouldExtractCurrentPlacement(EventData event) {
        return event.getEventId() == 2 || event.getEventId() == 10;
    }

    @Override
    protected boolean shouldIncludeCurrentPlacement(EventData event) {
        return event.getEventId() == 5 || event.getEventId() == 6 || event.getEventId() == 8 || event.getEventId() == 9 || event.getEventId() == 19 || event.getEventId() == 20 || event.getEventId() == 305;
    }

    @Override
    protected boolean isTopPriorityEvent(EventData currentEvent) {
        return currentEvent.getEventId() == 6 || currentEvent.getEventId() == 10 || currentEvent.getEventId() == 14 || currentEvent.getEventId() == 305;
    }

    @Override
    protected int getSessionDepth(EventData event) {
        int sessionDepth = SessionDepthManager.getInstance().getSessionDepth(1);
        if (event.getEventId() == 15 || event.getEventId() >= 300 && event.getEventId() < 400) {
            sessionDepth = SessionDepthManager.getInstance().getSessionDepth(0);
        }
        return sessionDepth;
    }

    @Override
    protected void setCurrentPlacement(EventData event) {
        if (event.getEventId() == 15 || event.getEventId() >= 300 && event.getEventId() < 400) {
            this.mCurrentOWPlacment = event.getAdditionalDataJSON().optString("placement");
        } else {
            this.mCurrentRVPlacment = event.getAdditionalDataJSON().optString("placement");
        }
    }

    @Override
    protected String getCurrentPlacement(int eventId) {
        if (eventId == 15 || eventId >= 300 && eventId < 400) {
            return this.mCurrentOWPlacment;
        }
        return this.mCurrentRVPlacment;
    }

    @Override
    protected boolean increaseSessionDepthIfNeeded(EventData event) {
        if (event.getEventId() == 6) {
            SessionDepthManager.getInstance().increaseSessionDepth(1);
            return false;
        }
        if (event.getEventId() == 305) {
            SessionDepthManager.getInstance().increaseSessionDepth(0);
            return false;
        }
        return false;
    }
}

