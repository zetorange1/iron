/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.ApplicationLogger;

public class ApplicationConfigurations {
    private ApplicationLogger mLogger;

    public ApplicationConfigurations() {
        this.mLogger = new ApplicationLogger();
    }

    public ApplicationConfigurations(ApplicationLogger logger) {
        this.mLogger = logger;
    }

    public ApplicationLogger getLoggerConfigurations() {
        return this.mLogger;
    }
}

