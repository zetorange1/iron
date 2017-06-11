/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.ContentValues
 *  android.content.Context
 *  android.database.Cursor
 *  android.database.sqlite.SQLiteDatabase
 *  android.database.sqlite.SQLiteDatabase$CursorFactory
 *  android.database.sqlite.SQLiteOpenHelper
 *  android.provider.BaseColumns
 *  org.json.JSONObject
 */
package com.ironsource.eventsmodule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.eventsmodule.IEventsStorageHelper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class DataBaseEventsStorage
extends SQLiteOpenHelper
implements IEventsStorageHelper {
    private static DataBaseEventsStorage mInstance;
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    private final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS events";
    private final String SQL_CREATE_ENTRIES = "CREATE TABLE events (_id INTEGER PRIMARY KEY,eventid INTEGER,timestamp INTEGER,type TEXT,data TEXT )";

    public DataBaseEventsStorage(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    public static synchronized DataBaseEventsStorage getInstance(Context context, String databaseName, int databaseVersion) {
        if (mInstance == null) {
            mInstance = new DataBaseEventsStorage(context, databaseName, databaseVersion);
        }
        return mInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void saveEvents(List<EventData> events, String type) {
        if (events == null || events.isEmpty()) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (EventData toInsert : events) {
                ContentValues values = this.getContentValuesForEvent(toInsert, type);
                if (db == null || values == null) continue;
                db.insert("events", null, values);
            }
        }
        catch (Exception var4_5) {}
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized ArrayList<EventData> loadEvents(String type) {
        ArrayList<EventData> events;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        events = new ArrayList<EventData>();
        try {
            String whereClause = "type = ?";
            String[] whereArgs = new String[]{type};
            String orderByClause = "timestamp ASC";
            cursor = db.query("events", null, whereClause, whereArgs, null, null, orderByClause);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int eventId = cursor.getInt(cursor.getColumnIndex("eventid"));
                    long timeStamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                    String data = cursor.getString(cursor.getColumnIndex("data"));
                    EventData event = new EventData(eventId, timeStamp, new JSONObject(data));
                    events.add(event);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        catch (Exception whereClause) {}
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return events;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void clearEvents(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "type = ?";
        String[] whereArgs = new String[]{type};
        try {
            db.delete("events", whereClause, whereArgs);
        }
        catch (Exception var5_5) {}
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private ContentValues getContentValuesForEvent(EventData event, String type) {
        ContentValues values = null;
        if (event != null) {
            values = new ContentValues(4);
            values.put("eventid", Integer.valueOf(event.getEventId()));
            values.put("timestamp", Long.valueOf(event.getTimeStamp()));
            values.put("type", type);
            values.put("data", event.getAdditionalData());
        }
        return values;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY,eventid INTEGER,timestamp INTEGER,type TEXT,data TEXT )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        this.onCreate(db);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized long getLatestEventTimestamp(String type) {
        long timeStamp;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        timeStamp = System.currentTimeMillis();
        try {
            String whereClause = "type = ?";
            String[] whereArgs = new String[]{type};
            String orderByClause = "timestamp DESC";
            cursor = db.query("events", null, whereClause, whereArgs, null, null, orderByClause, "1");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                timeStamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                cursor.close();
            }
        }
        catch (Exception whereClause) {}
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return timeStamp;
    }

    static abstract class EventEntry
    implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final int NUMBER_OF_COLUMNS = 4;
        public static final String COLUMN_NAME_EVENT_ID = "eventid";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DATA = "data";

        EventEntry() {
        }
    }

}

