/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.config;

import android.text.TextUtils;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.sdk.ConfigValidator;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;

public abstract class AbstractAdapterConfig
implements ConfigValidator {
    protected final String MAX_ADS_KEY = "maxAdsPerSession";
    protected final String MAX_ADS_PER_ITERATION_KEY = "maxAdsPerIteration";
    protected final String REQUEST_URL_KEY = "requestUrl";
    private ArrayList<String> mMandatoryKeys;
    private ArrayList<String> mOptionalKeys;
    protected ProviderSettings mProviderSettings;
    private String mProviderName;

    public AbstractAdapterConfig(String providerName) {
        this.mProviderSettings = ProviderSettingsHolder.getProviderSettingsHolder().getProviderSettings(providerName);
        this.mProviderName = providerName;
        this.mMandatoryKeys = this.initializeMandatoryFields();
        if (this.mMandatoryKeys == null) {
            this.mMandatoryKeys = new ArrayList();
        }
        this.mOptionalKeys = this.initializeOptionalFields();
        if (this.mOptionalKeys == null) {
            this.mOptionalKeys = new ArrayList();
        }
    }

    protected int getMaxRVAdsPerIterationToPresent() {
        int result = Integer.MAX_VALUE;
        try {
            if (this.mProviderSettings != null) {
                result = this.mProviderSettings.getRewardedVideoSettings().optInt("maxAdsPerIteration");
            }
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        return result;
    }

    protected int getMaxISAdsPerIterationToPresent() {
        int result = Integer.MAX_VALUE;
        try {
            if (this.mProviderSettings != null) {
                result = this.mProviderSettings.getInterstitialSettings().optInt("maxAdsPerIteration");
            }
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        return result;
    }

    protected int getMaxVideosToPresent() {
        int result = Integer.MAX_VALUE;
        try {
            if (this.mProviderSettings != null && this.mProviderSettings.getRewardedVideoSettings().has("maxAdsPerSession")) {
                result = this.mProviderSettings.getRewardedVideoSettings().optInt("maxAdsPerSession");
            }
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        return result;
    }

    @Override
    public ConfigValidationResult isRVConfigValid() {
        ConfigValidationResult result = new ConfigValidationResult();
        this.checkForAllMandatoryFields(this.mProviderSettings.getRewardedVideoSettings(), this.mMandatoryKeys, result);
        if (result.isValid()) {
            this.validateAllFields(this.mProviderSettings.getRewardedVideoSettings(), result);
        }
        if (result.isValid()) {
            this.adapterPostValidation(this.mProviderSettings.getRewardedVideoSettings(), result);
            if (!result.isValid()) {
                this.logConfigWarningMessage(result.getIronSourceError());
                result.setValid();
            }
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, this.mProviderName + ":isConfigValid:result(valid:" + result.isValid() + ")", 0);
        return result;
    }

    @Override
    public ConfigValidationResult isISConfigValid() {
        ConfigValidationResult result = new ConfigValidationResult();
        this.checkForAllMandatoryFields(this.mProviderSettings.getInterstitialSettings(), this.mMandatoryKeys, result);
        if (result.isValid()) {
            this.validateAllFields(this.mProviderSettings.getInterstitialSettings(), result);
        }
        if (result.isValid()) {
            this.adapterPostValidation(this.mProviderSettings.getInterstitialSettings(), result);
            if (!result.isValid()) {
                this.logConfigWarningMessage(result.getIronSourceError());
                result.setValid();
            }
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, this.mProviderName + ":isConfigValid:result(valid:" + result.isValid() + ")", 0);
        return result;
    }

    @Override
    public ConfigValidationResult isBannerConfigValid() {
        ConfigValidationResult result = new ConfigValidationResult();
        this.checkForAllMandatoryFields(this.mProviderSettings.getBannerSettings(), this.mMandatoryKeys, result);
        if (result.isValid()) {
            this.validateAllFields(this.mProviderSettings.getBannerSettings(), result);
        }
        if (result.isValid()) {
            this.adapterPostValidation(this.mProviderSettings.getBannerSettings(), result);
            if (!result.isValid()) {
                this.logConfigWarningMessage(result.getIronSourceError());
                result.setValid();
            }
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, this.mProviderName + ":isConfigValid:result(valid:" + result.isValid() + ")", 0);
        return result;
    }

    public void validateOptionalKeys(ArrayList<String> keys) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, this.mProviderName + ":validateOptionalKeys", 1);
        ConfigValidationResult result = new ConfigValidationResult();
        for (String key : keys) {
            if (this.isOptionalField(key)) {
                this.validateOptionalField(this.mProviderSettings.getRewardedVideoSettings(), key, result);
                if (result.isValid()) continue;
                this.logConfigWarningMessage(result.getIronSourceError());
                result.setValid();
                continue;
            }
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NATIVE, this.mProviderName + ":validateOptionalKeys(" + key + ")", 0);
        }
    }

    private void checkForAllMandatoryFields(JSONObject config, ArrayList<String> mandatoryKeys, ConfigValidationResult result) {
        if (mandatoryKeys == null || config == null) {
            result.setInvalid(ErrorBuilder.buildGenericError(this.mProviderName + " - Wrong configuration"));
            return;
        }
        for (String mandatory : mandatoryKeys) {
            if (!config.has(mandatory)) {
                result.setInvalid(ErrorBuilder.buildKeyNotSetError(mandatory, this.mProviderName, ""));
                return;
            }
            try {
                String value = config.get(mandatory).toString();
                if (!TextUtils.isEmpty((CharSequence)value)) continue;
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(mandatory, this.mProviderName, null));
                return;
            }
            catch (Throwable e) {
                e.printStackTrace();
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(mandatory, this.mProviderName, null));
                return;
            }
        }
    }

    private void validateAllFields(JSONObject config, ConfigValidationResult result) {
        try {
            Iterator keysIterator = config.keys();
            while (result.isValid() && keysIterator.hasNext()) {
                String key = (String)keysIterator.next();
                if (this.isMandatoryField(key)) {
                    this.validateMandatoryField(config, key, result);
                    continue;
                }
                if (this.isOptionalField(key)) {
                    this.validateOptionalField(config, key, result);
                    if (result.isValid()) continue;
                    this.logConfigWarningMessage(result.getIronSourceError());
                    keysIterator.remove();
                    result.setValid();
                    continue;
                }
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.mProviderName + ":Unknown key in configuration - " + key, 2);
            }
        }
        catch (Throwable e) {
            result.setInvalid(ErrorBuilder.buildGenericError(this.mProviderName + " - Invalid configuration"));
        }
    }

    private boolean isOptionalField(String key) {
        return this.mOptionalKeys.contains(key);
    }

    private boolean isMandatoryField(String key) {
        return this.mMandatoryKeys.contains(key);
    }

    protected void validateMaxVideos(int maxVideos, ConfigValidationResult result) {
        if (maxVideos < 0) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("maxVideos", this.mProviderName, "maxVideos value should be any integer >= 0, your value is:" + maxVideos));
        }
    }

    protected void validateNonEmptyString(String key, String value, ConfigValidationResult result) {
        if (TextUtils.isEmpty((CharSequence)value)) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(key, this.mProviderName, "value is empty"));
        }
    }

    private void logConfigWarningMessage(IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.ADAPTER_API, error.toString(), 2);
    }

    protected void validateBoolean(String key, String value, ConfigValidationResult result) {
        if (!(value = value.trim()).equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(key, this.mProviderName, "value should be 'true'/'false'"));
        }
    }

    protected abstract ArrayList<String> initializeMandatoryFields();

    protected abstract ArrayList<String> initializeOptionalFields();

    protected abstract void validateOptionalField(JSONObject var1, String var2, ConfigValidationResult var3);

    protected abstract void validateMandatoryField(JSONObject var1, String var2, ConfigValidationResult var3);

    protected abstract void adapterPostValidation(JSONObject var1, ConfigValidationResult var2);
}

