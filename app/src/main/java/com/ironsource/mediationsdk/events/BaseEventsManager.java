/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.AsyncTask
 *  android.os.Handler
 *  android.os.HandlerThread
 *  android.os.Looper
 *  android.text.TextUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.events;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import com.ironsource.eventsmodule.DataBaseEventsStorage;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.eventsmodule.EventsSender;
import com.ironsource.eventsmodule.IEventsManager;
import com.ironsource.eventsmodule.IEventsSenderResultListener;
import com.ironsource.mediationsdk.events.AbstractEventsFormatter;
import com.ironsource.mediationsdk.events.EventsFormatterFactory;
import com.ironsource.mediationsdk.sdk.GeneralProperties;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseEventsManager
implements IEventsManager {
    final int DEFAULT_BACKUP_THRESHOLD = 1;
    final int DEFAULT_MAX_NUMBER_OF_EVENTS = 100;
    final int DEFAULT_MAX_EVENTS_PER_BATCH = 5000;
    final int DATABASE_VERSION = 5;
    final String DATABASE_NAME = "supersonic_sdk.db";
    public static final String KEY_SESSION_DEPTH = "sessionDepth";
    final String KEY_PROVIDER = "provider";
    final String KEY_PLACEMENT = "placement";
    protected boolean mHasServerResponse;
    protected boolean mHadTopPriorityEvent = false;
    protected DataBaseEventsStorage mDbStorage;
    protected AbstractEventsFormatter mFormatter;
    protected String mCurrentPlacement;
    protected ArrayList<EventData> mLocalEvents;
    protected boolean mIsEventsEnabled = true;
    protected int mTotalEvents;
    protected int mMaxNumberOfEvents = 100;
    protected int mMaxEventsPerBatch = 5000;
    protected int mBackupThreshold = 1;
    protected int[] mOptOutEvents;
    protected int mAdUnitType;
    protected String mFormatterType;
    protected String mEventType;
    private EventThread mEventThread;

    protected void initState() {
        this.mLocalEvents = new ArrayList();
        this.mTotalEvents = 0;
        this.mCurrentPlacement = "";
        this.mFormatter = EventsFormatterFactory.getFormatter(this.mFormatterType, this.mAdUnitType);
        this.mEventThread = new EventThread(this.mEventType + "EventThread");
        this.mEventThread.start();
        this.mEventThread.prepareHandler();
    }

    public synchronized void start(Context context) {
        this.mFormatterType = IronSourceUtils.getDefaultEventsFormatterType(context, this.mEventType, this.mFormatterType);
        this.verifyCurrentFormatter(this.mFormatterType);
        this.mFormatter.setEventsServerUrl(IronSourceUtils.getDefaultEventsURL(context, this.mEventType, null));
        this.mDbStorage = DataBaseEventsStorage.getInstance(context, "supersonic_sdk.db", 5);
        this.backupEventsToDb();
        this.mOptOutEvents = IronSourceUtils.getDefaultOptOutEvents(context, this.mEventType);
    }

    @Override
    public synchronized void log(final EventData event) {
        this.mEventThread.postTask(new Runnable(){

            @Override
            public void run() {
                if (event == null || !BaseEventsManager.this.mIsEventsEnabled) {
                    return;
                }
                if (BaseEventsManager.this.shouldEventBeLogged(event)) {
                    int sessionDepth = BaseEventsManager.this.getSessionDepth(event);
                    boolean shouldUseNewDepth = BaseEventsManager.this.increaseSessionDepthIfNeeded(event);
                    if (shouldUseNewDepth) {
                        sessionDepth = BaseEventsManager.this.getSessionDepth(event);
                    }
                    event.addToAdditionalData("sessionDepth", sessionDepth);
                    if (BaseEventsManager.this.shouldExtractCurrentPlacement(event)) {
                        BaseEventsManager.this.setCurrentPlacement(event);
                    } else if (!TextUtils.isEmpty((CharSequence)BaseEventsManager.this.getCurrentPlacement(event.getEventId())) && BaseEventsManager.this.shouldIncludeCurrentPlacement(event)) {
                        event.addToAdditionalData("placement", BaseEventsManager.this.getCurrentPlacement(event.getEventId()));
                    }
                    BaseEventsManager.this.mLocalEvents.add(event);
                    ++BaseEventsManager.this.mTotalEvents;
                }
                boolean isTopPriority = BaseEventsManager.this.isTopPriorityEvent(event);
                if (!BaseEventsManager.this.mHadTopPriorityEvent && isTopPriority) {
                    BaseEventsManager.this.mHadTopPriorityEvent = true;
                }
                if (BaseEventsManager.this.mDbStorage != null) {
                    if (BaseEventsManager.this.shouldSendEvents()) {
                        BaseEventsManager.this.sendEvents();
                    } else if (BaseEventsManager.this.shouldBackupEventsToDb(BaseEventsManager.this.mLocalEvents) || isTopPriority) {
                        BaseEventsManager.this.backupEventsToDb();
                    }
                }
            }
        });
    }

    private void sendEvents() {
        this.mHadTopPriorityEvent = false;
        ArrayList<EventData> storedEvents = this.mDbStorage.loadEvents(this.mEventType);
        ArrayList<EventData> combinedEventList = this.initCombinedEventList(this.mLocalEvents, storedEvents, this.mMaxEventsPerBatch);
        this.mLocalEvents.clear();
        this.mDbStorage.clearEvents(this.mEventType);
        this.mTotalEvents = 0;
        if (combinedEventList.size() > 0) {
            JSONObject generalProperties = GeneralProperties.getProperties().toJSON();
            String dataToSend = this.mFormatter.format(combinedEventList, generalProperties);
            new EventsSender(new IEventsSenderResultListener(){

                @Override
                public synchronized void onEventsSenderResult(final ArrayList<EventData> extraData, final boolean success) {
                    BaseEventsManager.this.mEventThread.postTask(new Runnable(){

                        @Override
                        public void run() {
                            if (success) {
                                ArrayList<EventData> events = BaseEventsManager.this.mDbStorage.loadEvents(BaseEventsManager.this.mEventType);
                                BaseEventsManager.this.mTotalEvents = events.size() + BaseEventsManager.this.mLocalEvents.size();
                            } else if (extraData != null) {
                                BaseEventsManager.this.mDbStorage.saveEvents(extraData, BaseEventsManager.this.mEventType);
                                ArrayList<EventData> storedEvents = BaseEventsManager.this.mDbStorage.loadEvents(BaseEventsManager.this.mEventType);
                                BaseEventsManager.this.mTotalEvents = storedEvents.size() + BaseEventsManager.this.mLocalEvents.size();
                            }
                        }
                    });
                }

            }).execute(new Object[]{dataToSend, this.mFormatter.getEventsServerUrl(), combinedEventList});
        }
    }

    protected ArrayList<EventData> initCombinedEventList(ArrayList<EventData> localEvents, ArrayList<EventData> storedEvents, int maxSize) {
        ArrayList result2;
        ArrayList<EventData> allEvents = new ArrayList<EventData>();
        allEvents.addAll(localEvents);
        allEvents.addAll(storedEvents);
        Collections.sort(allEvents, new Comparator<EventData>(){

            @Override
            public int compare(EventData event1, EventData event2) {
                if (event1.getTimeStamp() >= event2.getTimeStamp()) {
                    return 1;
                }
                return -1;
            }
        });
        if (allEvents.size() <= maxSize) {
            result2 = new ArrayList<EventData>(allEvents);
        } else {
            result2 = new ArrayList(allEvents.subList(0, maxSize));
            List<EventData> eventsToSave = allEvents.subList(maxSize, allEvents.size());
            this.mDbStorage.saveEvents(eventsToSave, this.mEventType);
        }
        return result2;
    }

    protected void verifyCurrentFormatter(String formatterType) {
        if (this.mFormatter == null || !this.mFormatter.getFormatterType().equals(formatterType)) {
            this.mFormatter = EventsFormatterFactory.getFormatter(formatterType, this.mAdUnitType);
        }
    }

    @Override
    public void setBackupThreshold(int backupThreshold) {
        if (backupThreshold > 0) {
            this.mBackupThreshold = backupThreshold;
        }
    }

    @Override
    public void setMaxNumberOfEvents(int maxNumberOfEvents) {
        if (maxNumberOfEvents > 0) {
            this.mMaxNumberOfEvents = maxNumberOfEvents;
        }
    }

    @Override
    public void setMaxEventsPerBatch(int maxEventsPerBatch) {
        if (maxEventsPerBatch > 0) {
            this.mMaxEventsPerBatch = maxEventsPerBatch;
        }
    }

    @Override
    public void setOptOutEvents(int[] optOutEvents, Context context) {
        this.mOptOutEvents = optOutEvents;
        IronSourceUtils.saveDefaultOptOutEvents(context, this.mEventType, optOutEvents);
    }

    @Override
    public void setEventsUrl(String eventsUrl, Context context) {
        if (!TextUtils.isEmpty((CharSequence)eventsUrl)) {
            if (this.mFormatter != null) {
                this.mFormatter.setEventsServerUrl(eventsUrl);
            }
            IronSourceUtils.saveDefaultEventsURL(context, this.mEventType, eventsUrl);
        }
    }

    @Override
    public void setFormatterType(String formatterType, Context context) {
        if (!TextUtils.isEmpty((CharSequence)formatterType)) {
            this.mFormatterType = formatterType;
            IronSourceUtils.saveDefaultEventsFormatterType(context, this.mEventType, formatterType);
            this.verifyCurrentFormatter(formatterType);
        }
    }

    @Override
    public void setIsEventsEnabled(boolean isEnabled) {
        this.mIsEventsEnabled = isEnabled;
    }

    protected void backupEventsToDb() {
        this.mDbStorage.saveEvents(this.mLocalEvents, this.mEventType);
        this.mLocalEvents.clear();
    }

    protected boolean shouldSendEvents() {
        boolean shouldSendEvents = (this.mTotalEvents >= this.mMaxNumberOfEvents || this.mHadTopPriorityEvent) && this.mHasServerResponse;
        return shouldSendEvents;
    }

    protected boolean shouldBackupEventsToDb(ArrayList<EventData> events) {
        boolean shouldBackup = false;
        if (events != null) {
            shouldBackup = events.size() >= this.mBackupThreshold;
        }
        return shouldBackup;
    }

    protected boolean shouldEventBeLogged(EventData event) {
        boolean logEvent = true;
        if (event != null && this.mOptOutEvents != null && this.mOptOutEvents.length > 0) {
            int eventId = event.getEventId();
            for (int i = 0; i < this.mOptOutEvents.length; ++i) {
                if (eventId != this.mOptOutEvents[i]) continue;
                logEvent = false;
                break;
            }
        }
        return logEvent;
    }

    public void setHasServerResponse(boolean hasResponse) {
        this.mHasServerResponse = hasResponse;
    }

    protected String getProviderNameForEvent(EventData event) {
        String provider;
        try {
            JSONObject eventData = new JSONObject(event.getAdditionalData());
            provider = eventData.optString("provider", "");
        }
        catch (JSONException e) {
            return "";
        }
        return provider;
    }

    public void triggerEventsSend() {
        this.sendEvents();
    }

    protected abstract boolean shouldExtractCurrentPlacement(EventData var1);

    protected abstract boolean shouldIncludeCurrentPlacement(EventData var1);

    protected abstract boolean isTopPriorityEvent(EventData var1);

    protected abstract int getSessionDepth(EventData var1);

    protected abstract void setCurrentPlacement(EventData var1);

    protected abstract String getCurrentPlacement(int var1);

    protected abstract boolean increaseSessionDepthIfNeeded(EventData var1);

    private class EventThread
    extends HandlerThread {
        private Handler mHandler;

        public EventThread(String name) {
            super(name);
        }

        public void postTask(Runnable task) {
            this.mHandler.post(task);
        }

        public void prepareHandler() {
            this.mHandler = new Handler(this.getLooper());
        }
    }

}

