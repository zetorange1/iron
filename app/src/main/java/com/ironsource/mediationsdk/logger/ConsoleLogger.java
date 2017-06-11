/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.ironsource.mediationsdk.logger;

import android.util.Log;
import com.ironsource.mediationsdk.logger.IronSourceLogger;

public class ConsoleLogger
extends IronSourceLogger {
    public static final String NAME = "console";

    private ConsoleLogger() {
        super("console");
    }

    public ConsoleLogger(int debugLevel) {
        super("console", debugLevel);
    }

    @Override
    public void log(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        switch (logLevel) {
            case 0: {
                Log.v((String)("" + (Object)((Object)tag)), (String)message);
                break;
            }
            case 1: {
                Log.i((String)("" + (Object)((Object)tag)), (String)message);
                break;
            }
            case 2: {
                Log.w((String)("" + (Object)((Object)tag)), (String)message);
                break;
            }
            case 3: {
                Log.e((String)("" + (Object)((Object)tag)), (String)message);
            }
        }
    }

    @Override
    public void logException(IronSourceLogger.IronSourceTag tag, String message, Throwable e) {
        this.log(tag, message + ":stacktrace[" + Log.getStackTraceString((Throwable)e) + "]", 3);
    }
}

