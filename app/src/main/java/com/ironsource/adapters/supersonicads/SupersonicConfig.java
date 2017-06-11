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
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.ProviderSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

public class SupersonicConfig
extends AbstractAdapterConfig {
    private final String CUSTOM_PARAM_PREFIX = "custom_";
    private final String CLIENT_SIDE_CALLBACKS = "useClientSideCallbacks";
    private final String MAX_VIDEO_LENGTH = "maxVideoLength";
    private final String DYNAMIC_CONTROLLER_URL = "controllerUrl";
    private final String DYNAMIC_CONTROLLER_DEBUG_MODE = "debugMode";
    private final String CAMPAIGN_ID = "campaignId";
    private final String LANGUAGE = "language";
    private final String APPLICATION_PRIVATE_KEY = "privateKey";
    private final String ITEM_NAME = "itemName";
    private final String ITEM_COUNT = "itemCount";
    private Map<String, String> mRewardedVideoCustomParams;
    private Map<String, String> mOfferwallCustomParams;
    private static SupersonicConfig mInstance;

    public static SupersonicConfig getConfigObj() {
        if (mInstance == null) {
            mInstance = new SupersonicConfig();
        }
        return mInstance;
    }

    private SupersonicConfig() {
        super("Mediation");
    }

    public void setClientSideCallbacks(boolean status) {
        this.mProviderSettings.setRewardedVideoSettings("useClientSideCallbacks", String.valueOf(status));
    }

    public void setCustomControllerUrl(String url) {
        this.mProviderSettings.setRewardedVideoSettings("controllerUrl", url);
        this.mProviderSettings.setInterstitialSettings("controllerUrl", url);
    }

    public void setDebugMode(int debugMode) {
        this.mProviderSettings.setRewardedVideoSettings("debugMode", debugMode);
        this.mProviderSettings.setInterstitialSettings("debugMode", debugMode);
    }

    public void setCampaignId(String id) {
        this.mProviderSettings.setRewardedVideoSettings("campaignId", id);
    }

    public void setLanguage(String language) {
        this.mProviderSettings.setRewardedVideoSettings("language", language);
        this.mProviderSettings.setInterstitialSettings("language", language);
    }

    public void setRewardedVideoCustomParams(Map<String, String> rvCustomParams) {
        this.mRewardedVideoCustomParams = this.convertCustomParams(rvCustomParams);
    }

    public void setOfferwallCustomParams(Map<String, String> owCustomParams) {
        this.mOfferwallCustomParams = this.convertCustomParams(owCustomParams);
    }

    private Map<String, String> convertCustomParams(Map<String, String> customParams) {
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            Set<String> keys;
            if (customParams != null && (keys = customParams.keySet()) != null) {
                for (String k : keys) {
                    String value;
                    if (TextUtils.isEmpty((CharSequence)k) || TextUtils.isEmpty((CharSequence)(value = customParams.get(k)))) continue;
                    result.put("custom_" + k, value);
                }
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, ":convertCustomParams()", e);
        }
        return result;
    }

    public boolean getClientSideCallbacks() {
        boolean csc = false;
        if (this.mProviderSettings != null && this.mProviderSettings.getRewardedVideoSettings() != null && this.mProviderSettings.getRewardedVideoSettings().has("useClientSideCallbacks")) {
            csc = this.mProviderSettings.getRewardedVideoSettings().optBoolean("useClientSideCallbacks", false);
        }
        return csc;
    }

    public Map<String, String> getOfferwallCustomParams() {
        return this.mOfferwallCustomParams;
    }

    public Map<String, String> getRewardedVideoCustomParams() {
        return this.mRewardedVideoCustomParams;
    }

    @Override
    protected ArrayList<String> initializeMandatoryFields() {
        return null;
    }

    @Override
    protected ArrayList<String> initializeOptionalFields() {
        return null;
    }

    @Override
    protected void validateOptionalField(JSONObject config, String key, ConfigValidationResult result) {
    }

    @Override
    protected void validateMandatoryField(JSONObject config, String key, ConfigValidationResult result) {
    }

    @Override
    protected void adapterPostValidation(JSONObject config, ConfigValidationResult result) {
    }
}

