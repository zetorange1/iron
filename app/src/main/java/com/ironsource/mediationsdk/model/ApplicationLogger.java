/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

public class ApplicationLogger {
    private int mServer;
    private int mPublisher;
    private int mConsole;

    public ApplicationLogger() {
    }

    public ApplicationLogger(int serverLoggerLevel, int publisherLoggerLevel, int consoleLoggerLevel) {
        this.mServer = serverLoggerLevel;
        this.mPublisher = publisherLoggerLevel;
        this.mConsole = consoleLoggerLevel;
    }

    public int getServerLoggerLevel() {
        return this.mServer;
    }

    public int getPublisherLoggerLevel() {
        return this.mPublisher;
    }

    public int getConsoleLoggerLevel() {
        return this.mConsole;
    }
}

