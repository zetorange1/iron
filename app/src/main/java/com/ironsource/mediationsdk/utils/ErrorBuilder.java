/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 */
package com.ironsource.mediationsdk.utils;

import android.text.TextUtils;
import com.ironsource.mediationsdk.logger.IronSourceError;

public class ErrorBuilder {
    public static IronSourceError buildNoConfigurationAvailableError(String adUnit) {
        return new IronSourceError(501, "" + adUnit + " Init Fail - Unable to retrieve configurations from the server");
    }

    public static IronSourceError buildInvalidConfigurationError(String adUnit) {
        return new IronSourceError(501, "" + adUnit + " Init Fail - Configurations from the server are not valid");
    }

    public static IronSourceError buildUsingCachedConfigurationError(String appKey, String userId) {
        return new IronSourceError(502, "Mediation - Unable to retrieve configurations from IronSource server, using cached configurations with appKey:" + appKey + " and userId:" + userId);
    }

    public static IronSourceError buildKeyNotSetError(String key, String provider, String adUnit) {
        if (TextUtils.isEmpty((CharSequence)key) || TextUtils.isEmpty((CharSequence)provider)) {
            return ErrorBuilder.getGenericErrorForMissingParams();
        }
        return new IronSourceError(505, adUnit + " Mediation - " + key + " is not set for " + provider);
    }

    public static IronSourceError buildInvalidKeyValueError(String key, String provider, String optionalReason) {
        if (TextUtils.isEmpty((CharSequence)key) || TextUtils.isEmpty((CharSequence)provider)) {
            return ErrorBuilder.getGenericErrorForMissingParams();
        }
        return new IronSourceError(506, "Mediation - " + key + " value is not valid for " + provider + (!TextUtils.isEmpty((CharSequence)optionalReason) ? new StringBuilder().append(" - ").append(optionalReason).toString() : ""));
    }

    public static IronSourceError buildInvalidCredentialsError(String credentialName, String credentialValue, String errorMessage) {
        String resultingMessage = "Init Fail - " + credentialName + " value " + credentialValue + " is not valid" + (!TextUtils.isEmpty((CharSequence)errorMessage) ? new StringBuilder().append(" - ").append(errorMessage).toString() : "");
        return new IronSourceError(506, resultingMessage);
    }

    public static IronSourceError buildInitFailedError(String errorMsg, String adUnit) {
        errorMsg = TextUtils.isEmpty((CharSequence)errorMsg) ? adUnit + " init failed due to an unknown error" : adUnit + " - " + errorMsg;
        return new IronSourceError(508, errorMsg);
    }

    public static IronSourceError buildNoAdsToShowError(String adUnit) {
        return new IronSourceError(509, adUnit + " Show Fail - No ads to show");
    }

    public static IronSourceError buildShowFailedError(String adUnit, String error) {
        return new IronSourceError(509, adUnit + " Show Fail - " + error);
    }

    public static IronSourceError buildLoadFailedError(String adUnit, String adapterName, String errorMsg) {
        String resultingMessage = "" + adUnit + " Load Fail" + (!TextUtils.isEmpty((CharSequence)adapterName) ? new StringBuilder().append(" ").append(adapterName).toString() : "") + " - ";
        if (TextUtils.isEmpty((CharSequence)errorMsg)) {
            errorMsg = "unknown error";
        }
        resultingMessage = resultingMessage + errorMsg;
        return new IronSourceError(510, resultingMessage);
    }

    public static IronSourceError buildGenericError(String errorMsg) {
        if (TextUtils.isEmpty((CharSequence)errorMsg)) {
            errorMsg = "An error occurred";
        }
        return new IronSourceError(510, errorMsg);
    }

    public static IronSourceError buildNoInternetConnectionInitFailError(String adUnit) {
        return new IronSourceError(520, "" + adUnit + " Init Fail - No Internet connection");
    }

    public static IronSourceError buildNoInternetConnectionLoadFailError(String adUnit) {
        return new IronSourceError(520, "" + adUnit + " Load Fail - No Internet connection");
    }

    public static IronSourceError buildNoInternetConnectionShowFailError(String adUnit) {
        return new IronSourceError(520, "" + adUnit + " Show Fail - No Internet connection");
    }

    public static IronSourceError buildCappedError(String adUnit, String error) {
        return new IronSourceError(524, adUnit + " Show Fail - " + error);
    }

    private static IronSourceError getGenericErrorForMissingParams() {
        return ErrorBuilder.buildGenericError("Mediation - wrong configuration");
    }

    public static IronSourceError buildLoadFailedError(String errorMsg) {
        errorMsg = TextUtils.isEmpty((CharSequence)errorMsg) ? "Load failed due to an unknown error" : "Load failed - " + errorMsg;
        return new IronSourceError(510, errorMsg);
    }
}

