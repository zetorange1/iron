/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.sdk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SSAObj {
    private JSONObject mJsonObject;

    public SSAObj() {
    }

    public SSAObj(String value) {
        this.setJsonObject(value);
    }

    private void setJsonObject(String value) {
        try {
            this.mJsonObject = new JSONObject(value);
        }
        catch (JSONException e) {
            this.mJsonObject = new JSONObject();
        }
    }

    private JSONObject getJsonObject() {
        return this.mJsonObject;
    }

    public boolean containsKey(String key) {
        return this.getJsonObject().has(key);
    }

    public boolean isNull(String key) {
        return this.getJsonObject().isNull(key);
    }

    public Object get(String key) {
        try {
            return this.getJsonObject().get(key);
        }
        catch (JSONException e) {
            return null;
        }
    }

    public String getString(String key) {
        try {
            return this.mJsonObject.getString(key);
        }
        catch (JSONException e) {
            return null;
        }
    }

    public String getString(String key, String fallback) {
        return this.mJsonObject.optString(key, fallback);
    }

    public boolean getBoolean(String key) {
        try {
            return this.mJsonObject.getBoolean(key);
        }
        catch (JSONException e) {
            return false;
        }
    }

    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map)object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), SSAObj.toJSON(map.get(key)));
            }
            return json;
        }
        if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : (Iterable)object) {
                json.put(value);
            }
            return json;
        }
        return object;
    }

    public static boolean isEmptyObject(JSONObject object) {
        return object.names() == null;
    }

    public List toList(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
            list.add(this.fromJson(array.get(i)));
        }
        return list;
    }

    private Map<String, Object> getMap(JSONObject object, String key) throws JSONException {
        return this.toMap(object.getJSONObject(key));
    }

    private Map<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            map.put(key, this.fromJson(object.get(key)));
        }
        return map;
    }

    private Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        }
        if (json instanceof JSONObject) {
            return this.toMap((JSONObject)json);
        }
        if (json instanceof JSONArray) {
            return this.toList((JSONArray)json);
        }
        return json;
    }
}

