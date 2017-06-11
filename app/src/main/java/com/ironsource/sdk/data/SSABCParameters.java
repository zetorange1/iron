/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.data;

import com.ironsource.sdk.data.SSAObj;

public class SSABCParameters
extends SSAObj {
    private String CONNECTION_RETRIES = "connectionRetries";
    private String mConnectionRetries;

    public SSABCParameters() {
    }

    public SSABCParameters(String value) {
        super(value);
        if (this.containsKey(this.CONNECTION_RETRIES)) {
            this.setConnectionRetries(this.getString(this.CONNECTION_RETRIES));
        }
    }

    public String getConnectionRetries() {
        return this.mConnectionRetries;
    }

    public void setConnectionRetries(String connectionRetries) {
        this.mConnectionRetries = connectionRetries;
    }
}

