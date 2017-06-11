/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.logger;

public abstract class IronSourceLogger {
    int mDebugLevel;
    private String mLoggerName;

    IronSourceLogger(String loggerName) {
        this.mLoggerName = loggerName;
        this.mDebugLevel = 0;
    }

    IronSourceLogger(String loggerName, int debugLevel) {
        this.mLoggerName = loggerName;
        this.mDebugLevel = debugLevel;
    }

    String getLoggerName() {
        return this.mLoggerName;
    }

    int getDebugLevel() {
        return this.mDebugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.mDebugLevel = debugLevel;
    }

    public boolean equals(Object other) {
        if (other != null && other instanceof IronSourceLogger) {
            IronSourceLogger otherLogger = (IronSourceLogger)other;
            return this.mLoggerName != null && this.mLoggerName.equals(otherLogger.mLoggerName);
        }
        return false;
    }

    public abstract void log(IronSourceTag var1, String var2, int var3);

    public abstract void logException(IronSourceTag var1, String var2, Throwable var3);

    public static enum IronSourceTag {
        API,
        ADAPTER_API,
        CALLBACK,
        ADAPTER_CALLBACK,
        NETWORK,
        INTERNAL,
        NATIVE;
        

        private IronSourceTag() {
        }
    }

    public class IronSourceLogLevel {
        public static final int VERBOSE = 0;
        public static final int INFO = 1;
        public static final int WARNING = 2;
        public static final int ERROR = 3;
    }

}

