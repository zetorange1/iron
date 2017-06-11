/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.net.ConnectivityManager
 *  android.net.NetworkInfo
 *  android.text.TextUtils
 *  android.util.Base64
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.ThreadExceptionHandler;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.json.JSONException;
import org.json.JSONObject;

public class IronSourceUtils {
    private static final String SDK_VERSION = "6.6.0";
    private static final String SHARED_PREFERENCES_NAME = "Mediation_Shared_Preferences";
    private static final String LAST_RESPONSE = "last_response";
    private static final String GENERAL_PROPERTIES = "general_properties";
    private static final String DEFAULT_RV_EVENTS_URL = "default_rv_events_url";
    private static final String DEFAULT_IS_EVENTS_URL = "default_is_events_url";
    private static final String DEFAULT_RV_EVENTS_FORMATTER_TYPE = "default_rv_events_formatter_type";
    private static final String DEFAULT_IS_EVENTS_FORMATTER_TYPE = "default_is_events_formatter_type";
    private static final String DEFAULT_RV_OPT_OUT_EVENTS = "default_rv_opt_out_events";
    private static final String DEFAULT_IS_OPT_OUT_EVENTS = "default_is_opt_out_events";
    private static final String PROVIDER_KEY = "provider";
    private static final String SDK_VERSION_KEY = "providerSDKVersion";
    private static final String ADAPTER_VERSION_KEY = "providerAdapterVersion";

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (Throwable e) {
            if (input == null) {
                IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getMD5(input:null)", e);
            } else {
                IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getMD5(input:" + input + ")", e);
            }
            return "";
        }
    }

    private static String getSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, digest);
            return String.format("%064x", number);
        }
        catch (NoSuchAlgorithmException e) {
            if (input == null) {
                IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getSHA256(input:null)", e);
            } else {
                IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getSHA256(input:" + input + ")", e);
            }
            return "";
        }
    }

    public static String getTransId(String strToTransId) {
        return IronSourceUtils.getSHA256(strToTransId);
    }

    public static int getCurrentTimestamp() {
        return (int)(System.currentTimeMillis() / 1000);
    }

    public static String getSDKVersion() {
        return "6.6.0";
    }

    public static void createAndStartWorker(Runnable runnable, String threadName) {
        Thread worker = new Thread(runnable, threadName);
        worker.setUncaughtExceptionHandler(new ThreadExceptionHandler());
        worker.start();
    }

    public static String getBase64Auth(String loginUsername, String loginPass) {
        String source = loginUsername + ":" + loginPass;
        return "Basic " + Base64.encodeToString((byte[])source.getBytes(), (int)10);
    }

    private static String getDefaultEventsUrlByEventType(String eventType) {
        if ("IS".equals(eventType)) {
            return "default_is_events_url";
        }
        if ("RV".equals(eventType)) {
            return "default_rv_events_url";
        }
        return "";
    }

    private static String getDefaultOptOutEventsByEventType(String eventType) {
        if ("IS".equals(eventType)) {
            return "default_is_opt_out_events";
        }
        if ("RV".equals(eventType)) {
            return "default_rv_opt_out_events";
        }
        return "";
    }

    private static String getDefaultFormatterTypeByEventType(String eventType) {
        if ("IS".equals(eventType)) {
            return "default_is_events_formatter_type";
        }
        if ("RV".equals(eventType)) {
            return "default_rv_events_formatter_type";
        }
        return "";
    }

    public static synchronized void saveDefaultEventsURL(Context context, String eventType, String eventsUrl) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(IronSourceUtils.getDefaultEventsUrlByEventType(eventType), eventsUrl);
            editor.commit();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:saveDefaultEventsURL(eventType: " + eventType + ", eventsUrl:" + eventsUrl + ")", e);
        }
    }

    public static synchronized void saveDefaultOptOutEvents(Context context, String eventType, int[] optOutEvents) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            SharedPreferences.Editor editor = preferences.edit();
            String optOutEventsString = null;
            if (optOutEvents != null) {
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < optOutEvents.length; ++i) {
                    str.append(optOutEvents[i]).append(",");
                }
                optOutEventsString = str.toString();
            }
            editor.putString(IronSourceUtils.getDefaultOptOutEventsByEventType(eventType), optOutEventsString);
            editor.commit();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:saveDefaultOptOutEvents(eventType: " + eventType + ", optOutEvents:" + optOutEvents + ")", e);
        }
    }

    public static synchronized void saveDefaultEventsFormatterType(Context context, String eventType, String formatterType) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(IronSourceUtils.getDefaultFormatterTypeByEventType(eventType), formatterType);
            editor.commit();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:saveDefaultEventsFormatterType(eventType: " + eventType + ", formatterType:" + formatterType + ")", e);
        }
    }

    public static synchronized String getDefaultEventsFormatterType(Context context, String eventType, String defaultFormatterType) {
        String formatterType = defaultFormatterType;
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            formatterType = preferences.getString(IronSourceUtils.getDefaultFormatterTypeByEventType(eventType), defaultFormatterType);
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:getDefaultEventsFormatterType(eventType: " + eventType + ", defaultFormatterType:" + defaultFormatterType + ")", e);
        }
        return formatterType;
    }

    public static synchronized String getDefaultEventsURL(Context context, String eventType, String defaultEventsURL) {
        String serverUrl = defaultEventsURL;
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            serverUrl = preferences.getString(IronSourceUtils.getDefaultEventsUrlByEventType(eventType), defaultEventsURL);
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:getDefaultEventsURL(eventType: " + eventType + ", defaultEventsURL:" + defaultEventsURL + ")", e);
        }
        return serverUrl;
    }

    public static synchronized int[] getDefaultOptOutEvents(Context context, String eventType) {
        int[] optOutEvents = null;
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            String optOutEventsString = preferences.getString(IronSourceUtils.getDefaultOptOutEventsByEventType(eventType), null);
            if (!TextUtils.isEmpty((CharSequence)optOutEventsString)) {
                StringTokenizer stringTokenizer = new StringTokenizer(optOutEventsString, ",");
                ArrayList<Integer> result = new ArrayList<Integer>();
                while (stringTokenizer.hasMoreTokens()) {
                    result.add(Integer.parseInt(stringTokenizer.nextToken()));
                }
                optOutEvents = new int[result.size()];
                for (int i = 0; i < optOutEvents.length; ++i) {
                    optOutEvents[i] = (Integer)result.get(i);
                }
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "IronSourceUtils:getDefaultOptOutEvents(eventType: " + eventType + ")", e);
        }
        return optOutEvents;
    }

    public static synchronized void saveLastResponse(Context context, String response) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_response", response);
        editor.apply();
    }

    public static String getLastResponse(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        return preferences.getString("last_response", "");
    }

    static synchronized void saveGeneralProperties(Context context, JSONObject properties) {
        if (context == null || properties == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("general_properties", properties.toString());
        editor.apply();
    }

    public static synchronized JSONObject getGeneralProperties(Context context) {
        JSONObject result = new JSONObject();
        if (context == null) {
            return result;
        }
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        String generalPropertiesString = preferences.getString("general_properties", result.toString());
        try {
            result = new JSONObject(generalPropertiesString);
        }
        catch (JSONException var4_4) {
            // empty catch block
        }
        return result;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }
        return activeNetwork.isConnected();
    }

    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public static JSONObject getProviderAdditionalData(AbstractAdapter adapter) {
        JSONObject data = new JSONObject();
        try {
            data.put("provider", (Object)adapter.getProviderName());
            data.put("providerSDKVersion", (Object)adapter.getCoreSDKVersion());
            data.put("providerAdapterVersion", (Object)adapter.getVersion());
        }
        catch (JSONException var2_2) {
            // empty catch block
        }
        return data;
    }

    public static JSONObject getMediationAdditionalData() {
        JSONObject data = new JSONObject();
        try {
            data.put("provider", (Object)"Mediation");
        }
        catch (JSONException var1_1) {
            // empty catch block
        }
        return data;
    }

    public static void saveStringToSharedPrefs(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringFromSharedPrefs(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        return preferences.getString(key, defaultValue);
    }

    public static void saveBooleanToSharedPrefs(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanFromSharedPrefs(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        return preferences.getBoolean(key, defaultValue);
    }

    public static void saveIntToSharedPrefs(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntFromSharedPrefs(Context context, String key, int defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        return preferences.getInt(key, defaultValue);
    }

    public static void saveLongToSharedPrefs(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLongFromSharedPrefs(Context context, String key, long defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
        return preferences.getLong(key, defaultValue);
    }
}

