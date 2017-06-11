/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk;

public enum EBannerSize {
    BANNER("banner"),
    LARGE("large"),
    RECTANGLE("rectangle");
    
    private String mValue;

    private EBannerSize(String value) {
        this.mValue = value;
    }

    public String toString() {
        return this.mValue;
    }

    public int getValue() {
        return this.ordinal() + 1;
    }
}

