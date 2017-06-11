/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.logger;

import com.ironsource.mediationsdk.logger.ConsoleLogger;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.logger.ServerLogger;
import java.util.ArrayList;

public class IronSourceLoggerManager
extends IronSourceLogger
implements LogListener {
    private static IronSourceLoggerManager mInstance;
    private ArrayList<IronSourceLogger> mLoggers = new ArrayList();
    private boolean mIsDebugEnabled = false;

    private IronSourceLoggerManager(String loggerName) {
        super(loggerName);
        this.initSubLoggers();
    }

    private IronSourceLoggerManager(String loggerName, int debugLevel) {
        super(loggerName, debugLevel);
        this.initSubLoggers();
    }

    private void initSubLoggers() {
        this.mLoggers.add(new ConsoleLogger(1));
        this.mLoggers.add(new ServerLogger(0));
    }

    public static synchronized IronSourceLoggerManager getLogger() {
        if (mInstance == null) {
            mInstance = new IronSourceLoggerManager(IronSourceLoggerManager.class.getSimpleName());
        }
        return mInstance;
    }

    public static synchronized IronSourceLoggerManager getLogger(int debugLevel) {
        if (mInstance == null) {
            mInstance = new IronSourceLoggerManager(IronSourceLoggerManager.class.getSimpleName());
        } else {
            IronSourceLoggerManager.mInstance.mDebugLevel = debugLevel;
        }
        return mInstance;
    }

    public void addLogger(IronSourceLogger toAdd) {
        this.mLoggers.add(toAdd);
    }

    @Override
    public synchronized void log(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        if (logLevel < this.mDebugLevel) {
            return;
        }
        for (IronSourceLogger logger : this.mLoggers) {
            if (logger.getDebugLevel() > logLevel) continue;
            logger.log(tag, message, logLevel);
        }
    }

    @Override
    public synchronized void onLog(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        this.log(tag, message, logLevel);
    }

    @Override
    public synchronized void logException(IronSourceLogger.IronSourceTag tag, String message, Throwable e) {
        if (e == null) {
            for (IronSourceLogger logger : this.mLoggers) {
                logger.log(tag, message, 3);
            }
        } else {
            for (IronSourceLogger logger : this.mLoggers) {
                logger.logException(tag, message, e);
            }
        }
    }

    private IronSourceLogger findLoggerByName(String loggerName) {
        IronSourceLogger result = null;
        for (IronSourceLogger logger : this.mLoggers) {
            if (!logger.getLoggerName().equals(loggerName)) continue;
            result = logger;
            break;
        }
        return result;
    }

    public void setLoggerDebugLevel(String loggerName, int debugLevel) {
        if (loggerName == null) {
            return;
        }
        IronSourceLogger logger = this.findLoggerByName(loggerName);
        if (logger != null) {
            if (debugLevel >= 0 && debugLevel <= 3) {
                this.log(IronSourceLogger.IronSourceTag.NATIVE, "setLoggerDebugLevel(loggerName:" + loggerName + " ,debugLevel:" + debugLevel + ")", 0);
                logger.setDebugLevel(debugLevel);
            } else {
                this.mLoggers.remove(logger);
            }
        } else {
            this.log(IronSourceLogger.IronSourceTag.NATIVE, "Failed to find logger:setLoggerDebugLevel(loggerName:" + loggerName + " ,debugLevel:" + debugLevel + ")", 0);
        }
    }

    public boolean isDebugEnabled() {
        return this.mIsDebugEnabled;
    }

    public void setAdaptersDebug(boolean enabled) {
        this.mIsDebugEnabled = enabled;
    }
}

