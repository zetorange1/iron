/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.config;

import com.ironsource.mediationsdk.logger.IronSourceError;

public class ConfigValidationResult {
    private boolean mIsValid = true;
    private IronSourceError mIronSourceError = null;

    public void setInvalid(IronSourceError error) {
        this.mIsValid = false;
        this.mIronSourceError = error;
    }

    public void setValid() {
        this.mIsValid = true;
        this.mIronSourceError = null;
    }

    public boolean isValid() {
        return this.mIsValid;
    }

    public IronSourceError getIronSourceError() {
        return this.mIronSourceError;
    }

    public String toString() {
        if (this.isValid()) {
            return "valid:" + this.mIsValid;
        }
        return "valid:" + this.mIsValid + ", IronSourceError:" + this.mIronSourceError;
    }
}

