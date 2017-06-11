/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  android.util.Log
 */
package com.ironsource.sdk.utils;

import android.text.TextUtils;
import android.util.Log;
import com.ironsource.sdk.data.SSAEnums;

public class Logger {
    private static boolean enableLogging;

    public static void enableLogging(int mode) {
        enableLogging = SSAEnums.DebugMode.MODE_0.getValue() != mode;
    }

    public static void i(String tag, String message) {
        if (enableLogging) {
            Log.i((String)tag, (String)message);
        }
    }

    public static void i(String tag, String message, Throwable tr) {
        if (enableLogging && !TextUtils.isEmpty((CharSequence)message)) {
            Log.i((String)tag, (String)message, (Throwable)tr);
        }
    }

    public static void e(String tag, String message) {
        if (enableLogging) {
            Log.e((String)tag, (String)message);
        }
    }

    public static void e(String tag, String message, Throwable tr) {
        if (enableLogging) {
            Log.e((String)tag, (String)message, (Throwable)tr);
        }
    }

    public static void w(String tag, String message) {
        if (enableLogging) {
            Log.w((String)tag, (String)message);
        }
    }

    public static void w(String tag, String message, Throwable tr) {
        if (enableLogging) {
            Log.w((String)tag, (String)message, (Throwable)tr);
        }
    }

    public static void d(String tag, String message) {
        if (enableLogging) {
            Log.d((String)tag, (String)message);
        }
    }

    public static void d(String tag, String message, Throwable tr) {
        if (enableLogging) {
            Log.d((String)tag, (String)message, (Throwable)tr);
        }
    }

    public static void v(String tag, String message) {
        if (enableLogging) {
            Log.v((String)tag, (String)message);
        }
    }

    public static void v(String tag, String message, Throwable tr) {
        if (enableLogging) {
            Log.v((String)tag, (String)message, (Throwable)tr);
        }
    }
}

