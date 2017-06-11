/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.ironsource.mediationsdk.logger;

import android.util.Log;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.LogsSender;
import com.ironsource.mediationsdk.logger.ServerLogEntry;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerLogger
extends IronSourceLogger {
    public static final String NAME = "server";
    private final int SERVER_LOGS_SIZE_LIMIT = 1000;
    private ArrayList<ServerLogEntry> mLogs = new ArrayList();

    public ServerLogger() {
        super("server");
    }

    public ServerLogger(int debugLevel) {
        super("server", debugLevel);
    }

    private synchronized void addLogEntry(ServerLogEntry entry) {
        this.mLogs.add(entry);
        boolean shouldSendLogs = this.shouldSendLogs();
        if (shouldSendLogs) {
            this.send();
        } else if (this.mLogs.size() > 1000) {
            try {
                ArrayList<ServerLogEntry> newerLog = new ArrayList<ServerLogEntry>();
                for (int i = 500; i < this.mLogs.size(); ++i) {
                    newerLog.add(this.mLogs.get(i));
                }
                this.mLogs = newerLog;
            }
            catch (Exception e) {
                this.mLogs = new ArrayList();
            }
        }
    }

    private boolean shouldSendLogs() {
        return this.mLogs.get(this.mLogs.size() - 1).getLogLevel() == 3;
    }

    private void send() {
        IronSourceUtils.createAndStartWorker(new LogsSender(this.mLogs), "LogsSender");
        this.mLogs = new ArrayList();
    }

    private String getTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public synchronized void log(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        this.addLogEntry(new ServerLogEntry(tag, this.getTimestamp(), message, logLevel));
    }

    @Override
    public synchronized void logException(IronSourceLogger.IronSourceTag tag, String message, Throwable e) {
        StringBuilder logMessage = new StringBuilder(message);
        if (e != null) {
            logMessage.append(":stacktrace[");
            logMessage.append(Log.getStackTraceString((Throwable)e)).append("]");
        }
        this.addLogEntry(new ServerLogEntry(tag, this.getTimestamp(), logMessage.toString(), 3));
    }

    private class SendingCalc {
        private int DEFAULT_SIZE;
        private int DEFAULT_TIME;
        private int DEFAULT_DEBUG_LEVEL;

        public SendingCalc() {
            this.DEFAULT_SIZE = 1;
            this.DEFAULT_TIME = 1;
            this.DEFAULT_DEBUG_LEVEL = 3;
            this.initDefaults();
        }

        private void initDefaults() {
        }

        public void notifyEvent(int event) {
            if (this.calc(event)) {
                ServerLogger.this.send();
            }
        }

        private boolean calc(int event) {
            if (this.error(event)) {
                return true;
            }
            if (this.size()) {
                return true;
            }
            if (this.time()) {
                return true;
            }
            return false;
        }

        private boolean time() {
            return false;
        }

        private boolean error(int event) {
            return event == 3;
        }

        private boolean size() {
            return false;
        }
    }

}

