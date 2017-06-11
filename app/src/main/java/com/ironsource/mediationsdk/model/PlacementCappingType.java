/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

public enum PlacementCappingType {
    PER_DAY("d"),
    PER_HOUR("h");
    
    public String value;

    private PlacementCappingType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

