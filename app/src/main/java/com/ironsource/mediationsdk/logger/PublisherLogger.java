/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.logger;

import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.LogListener;

public class PublisherLogger
extends IronSourceLogger {
    private static final String NAME = "publisher";
    private LogListener mLogListener;

    private PublisherLogger() {
        super("publisher");
    }

    public PublisherLogger(LogListener logListener, int debugLevel) {
        super("publisher", debugLevel);
        this.mLogListener = logListener;
    }

    @Override
    public synchronized void log(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        if (this.mLogListener != null && message != null) {
            this.mLogListener.onLog(tag, message, logLevel);
        }
    }

    @Override
    public void logException(IronSourceLogger.IronSourceTag tag, String message, Throwable e) {
        if (e != null) {
            this.log(tag, e.getMessage(), 3);
        }
    }

    public void setLogListener(LogListener listener) {
        this.mLogListener = listener;
    }
}

