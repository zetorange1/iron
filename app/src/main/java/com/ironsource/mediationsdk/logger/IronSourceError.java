/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.logger;

public class IronSourceError {
    public static final int ERROR_CODE_NO_CONFIGURATION_AVAILABLE = 501;
    public static final int ERROR_CODE_USING_CACHED_CONFIGURATION = 502;
    public static final int ERROR_CODE_KEY_NOT_SET = 505;
    public static final int ERROR_CODE_INVALID_KEY_VALUE = 506;
    public static final int ERROR_CODE_INIT_FAILED = 508;
    public static final int ERROR_CODE_NO_ADS_TO_SHOW = 509;
    public static final int ERROR_CODE_GENERIC = 510;
    public static final int ERROR_NO_INTERNET_CONNECTION = 520;
    public static final int ERROR_REACHED_CAP_LIMIT = 524;
    private String mErrorMsg;
    private int mErrorCode;

    public IronSourceError(int key, String msg) {
        this.mErrorCode = key;
        if (msg == null) {
            msg = "";
        }
        this.mErrorMsg = msg;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorMessage() {
        return this.mErrorMsg;
    }

    public String toString() {
        return "errorCode:" + this.mErrorCode + ", errorMessage:" + this.mErrorMsg;
    }
}

