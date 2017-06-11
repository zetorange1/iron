/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  org.json.JSONObject
 */
package com.ironsource.adapters.supersonicads;

import android.text.TextUtils;
import com.ironsource.mediationsdk.config.AbstractAdapterConfig;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import java.util.ArrayList;
import org.json.JSONObject;

public class DemandSourceConfig
extends AbstractAdapterConfig {
    private final String TAG = DemandSourceConfig.class.getSimpleName();
    private static final String CUSTOM_PARAM_PREFIX = "custom_";
    private final String APPLICATION_KEY = "applicationKey";
    private final String USER_ID = "userId";
    static final String CLIENT_SIDE_CALLBACKS = "useClientSideCallbacks";
    static final String APPLICATION_USER_GENDER = "applicationUserGender";
    static final String APPLICATION_USER_AGE_GROUP = "applicationUserAgeGroup";
    private final String AGE = "age";
    private final String GENDER = "gender";
    static final String LANGUAGE = "language";
    private final String APPLICATION_PRIVATE_KEY = "privateKey";
    static final String MAX_VIDEO_LENGTH = "maxVideoLength";
    static final String ITEM_NAME = "itemName";
    static final String ITEM_COUNT = "itemCount";
    private final String SDK_PLUGIN_TYPE = "SDKPluginType";
    static final String CAMPAIGN_ID = "campaignId";
    static final String CUSTOM_SEGMENT = "custom_Segment";
    private final String DYNAMIC_CONTROLLER_URL = "controllerUrl";
    private final String DYNAMIC_CONTROLLER_DEBUG_MODE = "debugMode";
    private final String DYNAMIC_CONTROLLER_CONFIG = "controllerConfig";
    private String mProviderName;

    public DemandSourceConfig(String providerName) {
        super(providerName);
        this.mProviderName = providerName;
    }

    String getRVUserAgeGroup() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("applicationUserAgeGroup");
    }

    String getISUserAgeGroup() {
        return this.mProviderSettings.getInterstitialSettings().optString("applicationUserAgeGroup");
    }

    public String getRVDynamicControllerUrl() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("controllerUrl");
    }

    String getISDynamicControllerUrl() {
        return this.mProviderSettings.getInterstitialSettings().optString("controllerUrl");
    }

    public int getRVDebugMode() {
        int mode = 0;
        if (this.mProviderSettings.getRewardedVideoSettings().has("debugMode")) {
            mode = this.mProviderSettings.getRewardedVideoSettings().optInt("debugMode");
        }
        return mode;
    }

    public int getISDebugMode() {
        int mode = 0;
        if (this.mProviderSettings.getInterstitialSettings().has("debugMode")) {
            mode = this.mProviderSettings.getInterstitialSettings().optInt("debugMode");
        }
        return mode;
    }

    public String getRVControllerConfig() {
        String config = "";
        if (this.mProviderSettings != null && this.mProviderSettings.getRewardedVideoSettings() != null && this.mProviderSettings.getRewardedVideoSettings().has("controllerConfig")) {
            config = this.mProviderSettings.getRewardedVideoSettings().optString("controllerConfig");
        }
        return config;
    }

    public String getISControllerConfig() {
        String config = "";
        if (this.mProviderSettings != null && this.mProviderSettings.getInterstitialSettings() != null && this.mProviderSettings.getInterstitialSettings().has("controllerConfig")) {
            config = this.mProviderSettings.getInterstitialSettings().optString("controllerConfig");
        }
        return config;
    }

    public String getMaxVideoLength() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("maxVideoLength");
    }

    public String getLanguage() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("language");
    }

    public String getPrivateKey() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("privateKey");
    }

    public String getItemName() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("itemName");
    }

    public int getItemCount() {
        int itemCount = -1;
        try {
            String itemCountString = this.mProviderSettings.getRewardedVideoSettings().optString("itemCount");
            if (!TextUtils.isEmpty((CharSequence)itemCountString)) {
                itemCount = Integer.valueOf(itemCountString);
            }
        }
        catch (NumberFormatException e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":getItemCount()", e);
        }
        return itemCount;
    }

    String getCampaignId() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("campaignId");
    }

    String getMediationSegment() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("custom_Segment");
    }

    int getMaxVideos() {
        return this.getMaxVideosToPresent();
    }

    int getMaxRVAdsPerIteration() {
        return this.getMaxRVAdsPerIterationToPresent();
    }

    public int getMaxISAdsPerIteration() {
        return this.getMaxISAdsPerIterationToPresent();
    }

    String getRVUserGender() {
        return this.mProviderSettings.getRewardedVideoSettings().optString("applicationUserGender");
    }

    String getISUserGender() {
        return this.mProviderSettings.getInterstitialSettings().optString("applicationUserGender");
    }

    public void setMediationSegment(String segment) {
        this.mProviderSettings.setRewardedVideoSettings("custom_Segment", segment);
    }

    public void setUserAgeGroup(int age) {
        String ageGroup = "0";
        if (age >= 13 && age <= 17) {
            ageGroup = "1";
        } else if (age >= 18 && age <= 20) {
            ageGroup = "2";
        } else if (age >= 21 && age <= 24) {
            ageGroup = "3";
        } else if (age >= 25 && age <= 34) {
            ageGroup = "4";
        } else if (age >= 35 && age <= 44) {
            ageGroup = "5";
        } else if (age >= 45 && age <= 54) {
            ageGroup = "6";
        } else if (age >= 55 && age <= 64) {
            ageGroup = "7";
        } else if (age > 65 && age <= 120) {
            ageGroup = "8";
        }
        this.mProviderSettings.setRewardedVideoSettings("applicationUserAgeGroup", ageGroup);
        this.mProviderSettings.setInterstitialSettings("applicationUserAgeGroup", ageGroup);
    }

    public void setUserGender(String gender) {
        this.mProviderSettings.setRewardedVideoSettings("applicationUserGender", gender);
        this.mProviderSettings.setInterstitialSettings("applicationUserGender", gender);
    }

    @Override
    protected ArrayList<String> initializeMandatoryFields() {
        ArrayList<String> result = new ArrayList<String>();
        result.add("controllerUrl");
        return result;
    }

    @Override
    protected ArrayList<String> initializeOptionalFields() {
        ArrayList<String> result = new ArrayList<String>();
        result.add("useClientSideCallbacks");
        result.add("applicationUserGender");
        result.add("applicationUserAgeGroup");
        result.add("language");
        result.add("maxAdsPerSession");
        result.add("maxAdsPerIteration");
        result.add("privateKey");
        result.add("maxVideoLength");
        result.add("itemName");
        result.add("itemCount");
        result.add("SDKPluginType");
        result.add("controllerConfig");
        result.add("debugMode");
        result.add("requestUrl");
        result.add("custom_Segment");
        return result;
    }

    @Override
    protected void validateOptionalField(JSONObject config, String key, ConfigValidationResult result) {
        try {
            if ("maxAdsPerSession".equals(key)) {
                int maxVideos = config.optInt(key);
                this.validateMaxVideos(maxVideos, result);
            } else if (!("maxAdsPerIteration".equals(key) || "debugMode".equals(key) || "controllerConfig".equals(key))) {
                String value = (String)config.get(key);
                if ("useClientSideCallbacks".equals(key)) {
                    this.validateClientSideCallbacks(value, result);
                } else if ("applicationUserGender".equals(key)) {
                    this.validateGender(value, result);
                } else if ("applicationUserAgeGroup".equals(key)) {
                    this.validateAgeGroup(value, result);
                } else if ("language".equals(key)) {
                    this.validateLanguage(value, result);
                } else if ("maxVideoLength".equals(key)) {
                    this.validateMaxVideoLength(value, result);
                } else if ("privateKey".equals(key)) {
                    this.validatePrivateKey(value, result);
                } else if ("itemName".equals(key)) {
                    this.validateItemName(value, result);
                } else if ("itemCount".equals(key)) {
                    this.validateItemCount(value, result);
                }
            }
        }
        catch (Throwable e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(key, this.mProviderName, null));
        }
    }

    private void validateItemCount(String value, ConfigValidationResult result) {
        try {
            value = value.trim();
            int itemCount = Integer.parseInt(value);
            if (itemCount < 1 || itemCount > 100000) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("itemCount", this.mProviderName, "itemCount value should be between 1-100000"));
            }
        }
        catch (NumberFormatException e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("itemCount", this.mProviderName, "itemCount value should be between 1-100000"));
        }
    }

    private void validateItemName(String value, ConfigValidationResult result) {
        if (value != null) {
            if (value.length() < 1 || value.length() > 50) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("itemName", this.mProviderName, "itemNamelength should be between 1-50 characters"));
            }
        } else {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("itemName", this.mProviderName, "itemNamelength should be between 1-50 characters"));
        }
    }

    private void validatePrivateKey(String value, ConfigValidationResult result) {
        if (value != null) {
            if (value.length() >= 5 && value.length() <= 30) {
                String pattern = "^[a-zA-Z0-9]*$";
                if (!value.matches(pattern)) {
                    result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("privateKey", this.mProviderName, "privateKey should contains only characters and numbers"));
                }
            } else {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("privateKey", this.mProviderName, "privateKey length should be between 5-30 characters"));
            }
        } else {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("privateKey", this.mProviderName, "privateKey length should be between 5-30 characters"));
        }
    }

    private void validateMaxVideoLength(String value, ConfigValidationResult result) {
        try {
            value = value.trim();
            int age = Integer.parseInt(value);
            if (age < 1 || age > 1000) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("maxVideoLength", this.mProviderName, "maxVideoLength value should be between 1-1000"));
            }
        }
        catch (NumberFormatException e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("maxVideoLength", this.mProviderName, "maxVideoLength value should be between 1-1000"));
        }
    }

    private void validateLanguage(String value, ConfigValidationResult result) {
        if (value != null) {
            String pattern;
            if (!(value = value.trim()).matches(pattern = "^[a-zA-Z]*$") || value.length() != 2) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("language", this.mProviderName, "language value should be two letters format."));
            }
        } else {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("language", this.mProviderName, "language value should be two letters format."));
        }
    }

    private void validateAgeGroup(String value, ConfigValidationResult result) {
        try {
            value = value.trim();
            int age = Integer.parseInt(value);
            if (age < 0 || age > 8) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("applicationUserAgeGroup", this.mProviderName, "applicationUserAgeGroup value should be between 0-8"));
            }
        }
        catch (NumberFormatException e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("applicationUserAgeGroup", this.mProviderName, "applicationUserAgeGroup value should be between 0-8"));
        }
    }

    private void validateGender(String gender, ConfigValidationResult result) {
        try {
            if (!(gender == null || "male".equals(gender = gender.toLowerCase().trim()) || "female".equals(gender) || "unknown".equals(gender))) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("gender", this.mProviderName, "gender value should be one of male/female/unknown."));
            }
        }
        catch (Exception e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("gender", this.mProviderName, "gender value should be one of male/female/unknown."));
        }
    }

    private void validateClientSideCallbacks(String value, ConfigValidationResult result) {
        this.validateBoolean("useClientSideCallbacks", value, result);
    }

    @Override
    protected void validateMandatoryField(JSONObject config, String key, ConfigValidationResult result) {
        try {
            String value = config.optString(key);
            if ("applicationKey".equals(key)) {
                this.validateApplicationKey(value, result);
            } else if ("userId".equals(key)) {
                this.validateUserId(value, result);
            } else if ("controllerUrl".equals(key)) {
                this.validateDynamicUrl(value, result);
            }
        }
        catch (Throwable e) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError(key, this.mProviderName, null));
        }
    }

    @Override
    protected void adapterPostValidation(JSONObject config, ConfigValidationResult result) {
        try {
            this.validatePrivateKeyItemNameCountCombination(config, result);
        }
        catch (Exception e) {
            result.setInvalid(ErrorBuilder.buildGenericError(""));
        }
    }

    private void validatePrivateKeyItemNameCountCombination(JSONObject config, ConfigValidationResult result) {
        if (!(!config.has("privateKey") && !config.has("itemName") && !config.has("itemCount") || config.has("privateKey") && config.has("itemName") && config.has("itemCount"))) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("itemName, itemCount or privateKey", this.mProviderName, "configure itemName/itemCount requires the following configurations: itemName, itemCount and privateKey"));
        }
    }

    private void validateUserId(String value, ConfigValidationResult result) {
        if (value != null) {
            if (value.length() < 1 || value.length() > 64) {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("userId", this.mProviderName, "userId value should be between 1-64 characters"));
            }
        } else {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("userId", this.mProviderName, "userId is missing"));
        }
    }

    private void validateDynamicUrl(String value, ConfigValidationResult result) {
        if (TextUtils.isEmpty((CharSequence)value)) {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("controllerUrl", this.mProviderName, "controllerUrl is missing"));
        }
    }

    private void validateApplicationKey(String value, ConfigValidationResult result) {
        if (value != null) {
            if ((value = value.trim()).length() >= 5 && value.length() <= 10) {
                String pattern = "^[a-zA-Z0-9]*$";
                if (!value.matches(pattern)) {
                    result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("applicationKey", this.mProviderName, "applicationKey value should contains only english characters and numbers"));
                }
            } else {
                result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("applicationKey", this.mProviderName, "applicationKey length should be between 5-10 characters"));
            }
        } else {
            result.setInvalid(ErrorBuilder.buildInvalidKeyValueError("applicationKey", this.mProviderName, "applicationKey value is missing"));
        }
    }
}

