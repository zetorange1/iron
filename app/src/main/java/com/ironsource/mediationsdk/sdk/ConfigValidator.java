/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.config.ConfigValidationResult;

public interface ConfigValidator {
    public ConfigValidationResult isRVConfigValid();

    public ConfigValidationResult isISConfigValid();

    public ConfigValidationResult isBannerConfigValid();
}

