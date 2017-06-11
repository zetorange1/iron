/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.text.TextUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.utils;

import android.content.Context;
import android.text.TextUtils;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.ApplicationConfigurations;
import com.ironsource.mediationsdk.model.ApplicationEvents;
import com.ironsource.mediationsdk.model.ApplicationLogger;
import com.ironsource.mediationsdk.model.BannerConfigurations;
import com.ironsource.mediationsdk.model.BannerPlacement;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.InterstitialConfigurations;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.OfferwallConfigurations;
import com.ironsource.mediationsdk.model.OfferwallPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.model.PlacementAvailabilitySettings;
import com.ironsource.mediationsdk.model.PlacementCappingType;
import com.ironsource.mediationsdk.model.ProviderOrder;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerResponseWrapper {
    public static final String APP_KEY_FIELD = "appKey";
    public static final String USER_ID_FIELD = "userId";
    public static final String RESPONSE_FIELD = "response";
    private final String ERROR_KEY = "error";
    private final int DEFAULT_LOG_LEVEL = 3;
    private final String DEFAULT_ADAPTER_ALGORITHM = "KTO";
    private final int DEFAULT_ADAPTERS_SMARTLOAD_AMOUNT = 2;
    private final int DEFAULT_ADAPTERS_SMARTLOAD_TIMEOUT = 60;
    private final int DEFAULT_BANNER_SMARTLOAD_TIMEOUT = 200;
    private final int DEFAULT_MAX_EVENTS_PER_BATCH = 5000;
    private final String PROVIDER_ORDER_FIELD = "providerOrder";
    private final String PROVIDER_SETTINGS_FIELD = "providerSettings";
    private final String CONFIGURATIONS_FIELD = "configurations";
    private final String AD_UNITS_FIELD = "adUnits";
    private final String PROVIDER_LOAD_NAME_FIELD = "providerLoadName";
    private final String APPLICATION_FIELD = "application";
    private final String RV_FIELD = "rewardedVideo";
    private final String IS_FIELD = "interstitial";
    private final String OW_FIELD = "offerwall";
    private final String BN_FIELD = "banner";
    private final String LOGGERS_FIELD = "loggers";
    private final String EVENTS_FIELD = "events";
    private final String MAX_NUM_OF_ADAPTERS_TO_LOAD_ON_START_FIELD = "maxNumOfAdaptersToLoadOnStart";
    private final String ADAPTER_TIMEOUT_IN_SECS_FIELD = "adapterTimeOutInSeconds";
    private final String ADAPTER_TIMEOUT_IN_MILLIS_FIELD = "atim";
    private final String SERVER_FIELD = "server";
    private final String PUBLISHER_FIELD = "publisher";
    private final String CONSOLE_FIELD = "console";
    private final String SEND_ULTRA_EVENTS_FIELD = "sendUltraEvents";
    private final String SEND_EVENTS_TOGGLE_FIELD = "sendEventsToggle";
    private final String SERVER_EVENTS_URL_FIELD = "serverEventsURL";
    private final String SERVER_EVENTS_TYPE = "serverEventsType";
    private final String BACKUP_THRESHOLD_FIELD = "backupThreshold";
    private final String MAX_NUM_OF_EVENTS_FIELD = "maxNumberOfEvents";
    private final String MAX_EVENTS_PER_BATCH = "maxEventsPerBatch";
    private final String OPT_OUT_EVENTS_FIELD = "optOut";
    private final String ALLOW_LOCATION = "allowLocation";
    private final String PLACEMENTS_FIELD = "placements";
    private final String PLACEMENT_ID_FIELD = "placementId";
    private final String PLACEMENT_NAME_FIELD = "placementName";
    private final String PLACEMENT_SETTINGS_DELIVERY_FIELD = "delivery";
    private final String PLACEMENT_SETTINGS_CAPPING_FIELD = "capping";
    private final String PLACEMENT_SETTINGS_PACING_FIELD = "pacing";
    private final String PLACEMENT_SETTINGS_ENABLED_FIELD = "enabled";
    private final String PLACEMENT_SETTINGS_CAPPING_VALUE_FIELD = "maxImpressions";
    private final String PLACEMENT_SETTINGS_PACING_VALUE_FIELD = "numOfSeconds";
    private final String PLACEMENT_SETTINGS_CAPPING_UNIT_FIELD = "unit";
    private final String VIRTUAL_ITEM_NAME_FIELD = "virtualItemName";
    private final String VIRTUAL_ITEM_COUNT_FIELD = "virtualItemCount";
    private final String BACKFILL_FIELD = "backFill";
    private final String PREMIUM_FIELD = "premium";
    private final String UUID_ENABLED_FIELD = "uuidEnabled";
    private ProviderOrder mProviderOrder;
    private ProviderSettingsHolder mProviderSettingsHolder;
    private Configurations mConfigurations;
    private int mRVLoadPosition = -1;
    private int mISLoadPosition = -1;
    private int mBannerLoadPosition = -1;
    private String mAppKey;
    private String mUserId;
    private JSONObject mResponse;
    private int mMaxRVAdapters;
    private int mMaxISAdapters;
    private int mMaxBannerAdapters;
    private Context mContext;

    public ServerResponseWrapper(Context context, String appKey, String userId, String jsonData) {
        this.mContext = context;
        try {
            this.mResponse = TextUtils.isEmpty((CharSequence)jsonData) ? new JSONObject() : new JSONObject(jsonData);
            this.parseProviderSettings();
            this.parseConfigurations();
            this.parseProviderOrder();
            this.mMaxRVAdapters = this.mProviderOrder.getRewardedVideoProviderOrder().size();
            this.mMaxISAdapters = this.mProviderOrder.getInterstitialProviderOrder().size();
            this.mMaxBannerAdapters = this.mProviderOrder.getBannerProviderOrder().size();
            this.mAppKey = TextUtils.isEmpty((CharSequence)appKey) ? "" : appKey;
            this.mUserId = TextUtils.isEmpty((CharSequence)userId) ? "" : userId;
        }
        catch (JSONException e) {
            this.defaultInit();
        }
    }

    public ServerResponseWrapper(ServerResponseWrapper srw) {
        try {
            this.mContext = srw.getContext();
            this.mResponse = new JSONObject(srw.mResponse.toString());
            this.mMaxRVAdapters = srw.getMaxRVAdapters();
            this.mMaxISAdapters = srw.getMaxISAdapters();
            this.mMaxBannerAdapters = srw.getMaxBannerAdapters();
            this.mAppKey = srw.mAppKey;
            this.mUserId = srw.mUserId;
            this.mProviderOrder = srw.getProviderOrder();
            this.mProviderSettingsHolder = srw.getProviderSettingsHolder();
            this.mConfigurations = srw.getConfigurations();
        }
        catch (Exception e) {
            this.defaultInit();
        }
    }

    private void defaultInit() {
        this.mResponse = new JSONObject();
        this.mMaxRVAdapters = 0;
        this.mMaxISAdapters = 0;
        this.mMaxBannerAdapters = 0;
        this.mAppKey = "";
        this.mUserId = "";
        this.mProviderOrder = new ProviderOrder();
        this.mProviderSettingsHolder = ProviderSettingsHolder.getProviderSettingsHolder();
        this.mConfigurations = new Configurations();
    }

    public String toString() {
        JSONObject resultObject = new JSONObject();
        try {
            resultObject.put("appKey", (Object)this.mAppKey);
            resultObject.put("userId", (Object)this.mUserId);
            resultObject.put("response", (Object)this.mResponse);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return resultObject.toString();
    }

    public boolean isValidResponse() {
        boolean valid = this.mResponse != null;
        valid = valid && !this.mResponse.has("error");
        valid = valid && this.mProviderOrder != null;
        valid = valid && this.mProviderSettingsHolder != null;
        valid = valid && this.mConfigurations != null;
        return valid;
    }

    public List<IronSource.AD_UNIT> getInitiatedAdUnits() {
        if (this.mResponse == null || this.mConfigurations == null) {
            return null;
        }
        ArrayList<IronSource.AD_UNIT> adUnits = new ArrayList<IronSource.AD_UNIT>();
        if (this.mConfigurations.getRewardedVideoConfigurations() != null && this.mProviderOrder != null && this.mProviderOrder.getRewardedVideoProviderOrder().size() > 0) {
            adUnits.add(IronSource.AD_UNIT.REWARDED_VIDEO);
        }
        if (this.mConfigurations.getInterstitialConfigurations() != null && this.mProviderOrder != null && this.mProviderOrder.getInterstitialProviderOrder().size() > 0) {
            adUnits.add(IronSource.AD_UNIT.INTERSTITIAL);
        }
        if (this.mConfigurations.getOfferwallConfigurations() != null) {
            adUnits.add(IronSource.AD_UNIT.OFFERWALL);
        }
        if (this.mConfigurations.getBannerConfigurations() != null) {
            adUnits.add(IronSource.AD_UNIT.BANNER);
        }
        return adUnits;
    }

    public IronSourceError getReponseError() {
        if (this.isValidResponse()) {
            return null;
        }
        String errorMsg = "";
        if (this.mResponse != null && this.mResponse.has("error")) {
            errorMsg = this.mResponse.optString("error");
        }
        if (errorMsg == null) {
            errorMsg = "";
        }
        return ErrorBuilder.buildInitFailedError(errorMsg, "Mediation");
    }

    private void parseProviderOrder() {
        try {
            String providerName;
            String premiumProviderName;
            String backFillProviderName;
            int i;
            JSONObject providerOrderSection = this.getSection(this.mResponse, "providerOrder");
            JSONArray rvOrderSection = providerOrderSection.optJSONArray("rewardedVideo");
            JSONArray isOrderSection = providerOrderSection.optJSONArray("interstitial");
            JSONArray bnOrderSection = providerOrderSection.optJSONArray("banner");
            this.mProviderOrder = new ProviderOrder();
            if (rvOrderSection != null && this.getConfigurations() != null && this.getConfigurations().getRewardedVideoConfigurations() != null) {
                backFillProviderName = this.getConfigurations().getRewardedVideoConfigurations().getBackFillProviderName();
                premiumProviderName = this.getConfigurations().getRewardedVideoConfigurations().getPremiumProviderName();
                for (i = 0; i < rvOrderSection.length(); ++i) {
                    providerName = rvOrderSection.optString(i);
                    if (providerName.equals(backFillProviderName)) {
                        this.mProviderOrder.setRVBackFillProvider(backFillProviderName);
                        continue;
                    }
                    if (providerName.equals(premiumProviderName)) {
                        this.mProviderOrder.setRVPremiumProvider(premiumProviderName);
                    }
                    this.mProviderOrder.addRewardedVideoProvider(providerName);
                }
            }
            if (isOrderSection != null && this.getConfigurations() != null && this.getConfigurations().getInterstitialConfigurations() != null) {
                backFillProviderName = this.getConfigurations().getInterstitialConfigurations().getBackFillProviderName();
                premiumProviderName = this.getConfigurations().getInterstitialConfigurations().getPremiumProviderName();
                for (i = 0; i < isOrderSection.length(); ++i) {
                    providerName = isOrderSection.optString(i);
                    if (providerName.equals(backFillProviderName)) {
                        this.mProviderOrder.setISBackFillProvider(backFillProviderName);
                        continue;
                    }
                    if (providerName.equals(premiumProviderName)) {
                        this.mProviderOrder.setISPremiumProvider(premiumProviderName);
                    }
                    this.mProviderOrder.addInterstitialProvider(providerName);
                }
            }
            if (bnOrderSection != null) {
                for (int i2 = 0; i2 < bnOrderSection.length(); ++i2) {
                    String providerName2 = bnOrderSection.optString(i2);
                    this.mProviderOrder.addBannerProvider(providerName2);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseProviderSettings() {
        try {
            this.mProviderSettingsHolder = ProviderSettingsHolder.getProviderSettingsHolder();
            JSONObject providerSettingsSection = this.getSection(this.mResponse, "providerSettings");
            Iterator keys = providerSettingsSection.keys();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                JSONObject concreteProviderSettingsSection = providerSettingsSection.optJSONObject(key);
                if (concreteProviderSettingsSection == null) continue;
                String nameForReflection = concreteProviderSettingsSection.optString("providerLoadName", key);
                JSONObject adUnitSection = this.getSection(concreteProviderSettingsSection, "adUnits");
                JSONObject appSection = this.getSection(concreteProviderSettingsSection, "application");
                JSONObject rvSection = this.getSection(adUnitSection, "rewardedVideo");
                JSONObject isSection = this.getSection(adUnitSection, "interstitial");
                JSONObject bnSection = this.getSection(adUnitSection, "banner");
                JSONObject rewardedVideoSettings = this.mergeJsons(rvSection, appSection);
                JSONObject interstitialSettings = this.mergeJsons(isSection, appSection);
                JSONObject bannerSettings = this.mergeJsons(bnSection, appSection);
                if (this.mProviderSettingsHolder.containsProviderSettings(key)) {
                    ProviderSettings providerLocalSettings = this.mProviderSettingsHolder.getProviderSettings(key);
                    JSONObject providerLocalRVSettings = providerLocalSettings.getRewardedVideoSettings();
                    JSONObject providerLocalISSettings = providerLocalSettings.getInterstitialSettings();
                    JSONObject providerLocalBNSettings = providerLocalSettings.getBannerSettings();
                    providerLocalSettings.setRewardedVideoSettings(this.mergeJsons(providerLocalRVSettings, rewardedVideoSettings));
                    providerLocalSettings.setInterstitialSettings(this.mergeJsons(providerLocalISSettings, interstitialSettings));
                    providerLocalSettings.setBannerSettings(this.mergeJsons(providerLocalBNSettings, bannerSettings));
                    continue;
                }
                if (this.mProviderSettingsHolder.containsProviderSettings("Mediation") && ("SupersonicAds".toLowerCase().equals(nameForReflection.toLowerCase()) || "RIS".toLowerCase().equals(nameForReflection.toLowerCase()))) {
                    ProviderSettings mediationLocalSettings = this.mProviderSettingsHolder.getProviderSettings("Mediation");
                    JSONObject mediationLocalRVSettings = mediationLocalSettings.getRewardedVideoSettings();
                    JSONObject mediationLocalISSettings = mediationLocalSettings.getInterstitialSettings();
                    JSONObject mergedRVSettings = new JSONObject(mediationLocalRVSettings.toString());
                    JSONObject mergedISSettings = new JSONObject(mediationLocalISSettings.toString());
                    rewardedVideoSettings = this.mergeJsons(mergedRVSettings, rewardedVideoSettings);
                    interstitialSettings = this.mergeJsons(mergedISSettings, interstitialSettings);
                    this.mProviderSettingsHolder.addProviderSettings(new ProviderSettings(key, nameForReflection, rewardedVideoSettings, interstitialSettings));
                    continue;
                }
                ProviderSettings settings = new ProviderSettings(key, nameForReflection, rewardedVideoSettings, interstitialSettings);
                if (bannerSettings.length() > 0) {
                    settings.setBannerSettings(bannerSettings);
                }
                this.mProviderSettingsHolder.addProviderSettings(settings);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseConfigurations() {
        try {
            JSONObject configurationsSection = this.getSection(this.mResponse, "configurations");
            JSONObject adUnitSection = this.getSection(configurationsSection, "adUnits");
            JSONObject appSection = this.getSection(configurationsSection, "application");
            JSONObject rvSection = this.getSection(adUnitSection, "rewardedVideo");
            JSONObject isSection = this.getSection(adUnitSection, "interstitial");
            JSONObject owSection = this.getSection(adUnitSection, "offerwall");
            JSONObject bnSection = this.getSection(adUnitSection, "banner");
            JSONObject appEventsSection = this.getSection(appSection, "events");
            JSONObject loggerSection = this.getSection(appSection, "loggers");
            RewardedVideoConfigurations rvConfig = null;
            InterstitialConfigurations isConfig = null;
            OfferwallConfigurations owConfig = null;
            BannerConfigurations bannerConfig = null;
            if (appSection != null) {
                boolean isUuidEnabled = appSection.optBoolean("uuidEnabled", true);
                IronSourceUtils.saveBooleanToSharedPrefs(this.mContext, "uuidEnabled", isUuidEnabled);
            }
            if (rvSection != null) {
                String backFillProviderName;
                String premiumProviderName;
                JSONArray rvPlacementsSection = rvSection.optJSONArray("placements");
                JSONObject rvEventsSection = this.getSection(rvSection, "events");
                int rvSmartLoadAmount = this.getIntConfigValue(rvSection, appSection, "maxNumOfAdaptersToLoadOnStart", 2);
                int rvSmartLoadTimeout = this.getIntConfigValue(rvSection, appSection, "adapterTimeOutInSeconds", 60);
                String rvAlgorithm = "KTO";
                JSONObject rewardedVideoCombinedEvents = this.mergeJsons(rvEventsSection, appEventsSection);
                boolean rvUltraEvents = rewardedVideoCombinedEvents.optBoolean("sendUltraEvents", false);
                boolean rvEventsToggle = rewardedVideoCombinedEvents.optBoolean("sendEventsToggle", false);
                String rvEventsUrl = rewardedVideoCombinedEvents.optString("serverEventsURL", "");
                String rvEventsType = rewardedVideoCombinedEvents.optString("serverEventsType", "");
                int rvBackupThreshold = rewardedVideoCombinedEvents.optInt("backupThreshold", -1);
                int rvMaxNumOfEvents = rewardedVideoCombinedEvents.optInt("maxNumberOfEvents", -1);
                int rvMaxEventsPerBatch = rewardedVideoCombinedEvents.optInt("maxEventsPerBatch", 5000);
                int[] optOutEvents = null;
                JSONArray optOutJsonArray = rewardedVideoCombinedEvents.optJSONArray("optOut");
                if (optOutJsonArray != null) {
                    optOutEvents = new int[optOutJsonArray.length()];
                    for (int i = 0; i < optOutJsonArray.length(); ++i) {
                        optOutEvents[i] = optOutJsonArray.optInt(i);
                    }
                }
                ApplicationEvents rvEvents = new ApplicationEvents(rvUltraEvents, rvEventsToggle, rvEventsUrl, rvEventsType, rvBackupThreshold, rvMaxNumOfEvents, rvMaxEventsPerBatch, optOutEvents);
                rvConfig = new RewardedVideoConfigurations(rvSmartLoadAmount, rvSmartLoadTimeout, rvAlgorithm, rvEvents);
                if (rvPlacementsSection != null) {
                    for (int i = 0; i < rvPlacementsSection.length(); ++i) {
                        JSONObject singlePlacementJson = rvPlacementsSection.optJSONObject(i);
                        Placement placement = this.parseSingleRVPlacement(singlePlacementJson);
                        if (placement == null) continue;
                        rvConfig.addRewardedVideoPlacement(placement);
                    }
                }
                if (!TextUtils.isEmpty((CharSequence)(backFillProviderName = rvSection.optString("backFill")))) {
                    rvConfig.setBackFillProviderName(backFillProviderName);
                }
                if (!TextUtils.isEmpty((CharSequence)(premiumProviderName = rvSection.optString("premium")))) {
                    rvConfig.setPremiumProviderName(premiumProviderName);
                }
            }
            if (isSection != null) {
                String backFillProviderName;
                String premiumProviderName;
                JSONArray isPlacementsSection = isSection.optJSONArray("placements");
                JSONObject isEventsSection = this.getSection(isSection, "events");
                int isSmartLoadAmount = this.getIntConfigValue(isSection, appSection, "maxNumOfAdaptersToLoadOnStart", 2);
                int isSmartLoadTimeout = this.getIntConfigValue(isSection, appSection, "adapterTimeOutInSeconds", 60);
                JSONObject interstitialCombinedEvents = this.mergeJsons(isEventsSection, appEventsSection);
                boolean isEventsToggle = interstitialCombinedEvents.optBoolean("sendEventsToggle", false);
                String isEventsUrl = interstitialCombinedEvents.optString("serverEventsURL", "");
                String isEventsType = interstitialCombinedEvents.optString("serverEventsType", "");
                int isBackupThreshold = interstitialCombinedEvents.optInt("backupThreshold", -1);
                int isMaxNumOfEvents = interstitialCombinedEvents.optInt("maxNumberOfEvents", -1);
                int isMaxEventsPerBatch = interstitialCombinedEvents.optInt("maxEventsPerBatch", 5000);
                int[] optOutEvents = null;
                JSONArray optOutJsonArray = interstitialCombinedEvents.optJSONArray("optOut");
                if (optOutJsonArray != null) {
                    optOutEvents = new int[optOutJsonArray.length()];
                    for (int i = 0; i < optOutJsonArray.length(); ++i) {
                        optOutEvents[i] = optOutJsonArray.optInt(i);
                    }
                }
                ApplicationEvents isEvents = new ApplicationEvents(false, isEventsToggle, isEventsUrl, isEventsType, isBackupThreshold, isMaxNumOfEvents, isMaxEventsPerBatch, optOutEvents);
                isConfig = new InterstitialConfigurations(isSmartLoadAmount, isSmartLoadTimeout, isEvents);
                if (isPlacementsSection != null) {
                    for (int i = 0; i < isPlacementsSection.length(); ++i) {
                        JSONObject singlePlacementJson = isPlacementsSection.optJSONObject(i);
                        InterstitialPlacement placement = this.parseSingleISPlacement(singlePlacementJson);
                        if (placement == null) continue;
                        isConfig.addInterstitialPlacement(placement);
                    }
                }
                if (!TextUtils.isEmpty((CharSequence)(backFillProviderName = isSection.optString("backFill")))) {
                    isConfig.setBackFillProviderName(backFillProviderName);
                }
                if (!TextUtils.isEmpty((CharSequence)(premiumProviderName = isSection.optString("premium")))) {
                    isConfig.setPremiumProviderName(premiumProviderName);
                }
            }
            if (bnSection != null) {
                JSONArray bnPlacementsSection = bnSection.optJSONArray("placements");
                JSONObject bnEventsSection = this.getSection(bnSection, "events");
                int bnSmartLoadAmount = this.getIntConfigValue(bnSection, appSection, "maxNumOfAdaptersToLoadOnStart", 1);
                long bnSmartLoadTimeout = this.getLongConfigValue(bnSection, appSection, "atim", 200);
                JSONObject bannerCombinedEvents = this.mergeJsons(bnEventsSection, appEventsSection);
                boolean bnEventsToggle = bannerCombinedEvents.optBoolean("sendEventsToggle", false);
                String bnEventsUrl = bannerCombinedEvents.optString("serverEventsURL", "");
                String bnEventsType = bannerCombinedEvents.optString("serverEventsType", "");
                int bnBackupThreshold = bannerCombinedEvents.optInt("backupThreshold", -1);
                int bnMaxNumOfEvents = bannerCombinedEvents.optInt("maxNumberOfEvents", -1);
                int bnMaxEventsPerBatch = bannerCombinedEvents.optInt("maxEventsPerBatch", 5000);
                int[] optOutEvents = null;
                JSONArray optOutJsonArray = bannerCombinedEvents.optJSONArray("optOut");
                if (optOutJsonArray != null) {
                    optOutEvents = new int[optOutJsonArray.length()];
                    for (int i = 0; i < optOutJsonArray.length(); ++i) {
                        optOutEvents[i] = optOutJsonArray.optInt(i);
                    }
                }
                ApplicationEvents bnEvents = new ApplicationEvents(false, bnEventsToggle, bnEventsUrl, bnEventsType, bnBackupThreshold, bnMaxNumOfEvents, bnMaxEventsPerBatch, optOutEvents);
                bannerConfig = new BannerConfigurations(bnSmartLoadAmount, bnSmartLoadTimeout, bnEvents);
                if (bnPlacementsSection != null) {
                    for (int i = 0; i < bnPlacementsSection.length(); ++i) {
                        JSONObject singlePlacementJson = bnPlacementsSection.optJSONObject(i);
                        BannerPlacement placement = this.parseSingleBNPlacement(singlePlacementJson);
                        if (placement == null) continue;
                        bannerConfig.addBannerPlacement(placement);
                    }
                }
            }
            if (owSection != null) {
                JSONArray owPlacementsSection = owSection.optJSONArray("placements");
                owConfig = new OfferwallConfigurations();
                if (owPlacementsSection != null) {
                    for (int i = 0; i < owPlacementsSection.length(); ++i) {
                        JSONObject singlePlacementJson = owPlacementsSection.optJSONObject(i);
                        OfferwallPlacement placement = this.parseSingleOWPlacement(singlePlacementJson);
                        if (placement == null) continue;
                        owConfig.addOfferwallPlacement(placement);
                    }
                }
            }
            int serverLoggerLevel = loggerSection.optInt("server", 3);
            int publisherLoggerLevel = loggerSection.optInt("publisher", 3);
            int consoleLoggerLevel = loggerSection.optInt("console", 3);
            ApplicationLogger logger = new ApplicationLogger(serverLoggerLevel, publisherLoggerLevel, consoleLoggerLevel);
            ApplicationConfigurations appConfig = new ApplicationConfigurations(logger);
            boolean allowLocation = appSection.optBoolean("allowLocation", false);
            IronSourceUtils.saveBooleanToSharedPrefs(this.mContext, "GeneralProperties.ALLOW_LOCATION_SHARED_PREFS_KEY", allowLocation);
            this.mConfigurations = new Configurations(rvConfig, isConfig, owConfig, bannerConfig, appConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getIntConfigValue(JSONObject mainJson, JSONObject secondaryJson, String key, int defaultValue) {
        int result = 0;
        if (mainJson.has(key)) {
            result = mainJson.optInt(key, 0);
        } else if (secondaryJson.has(key)) {
            result = secondaryJson.optInt(key, 0);
        }
        if (result == 0) {
            result = defaultValue;
        }
        return result;
    }

    private long getLongConfigValue(JSONObject mainJson, JSONObject secondaryJson, String key, long defaultValue) {
        long result = 0;
        if (mainJson.has(key)) {
            result = mainJson.optLong(key, 0);
        } else if (secondaryJson.has(key)) {
            result = secondaryJson.optLong(key, 0);
        }
        if (result == 0) {
            result = defaultValue;
        }
        return result;
    }

    private Placement parseSingleRVPlacement(JSONObject placementJson) {
        Placement result = null;
        if (placementJson != null) {
            int placementId = placementJson.optInt("placementId", -1);
            String placementName = placementJson.optString("placementName", "");
            String virtualItemName = placementJson.optString("virtualItemName", "");
            int virtualItemCount = placementJson.optInt("virtualItemCount", -1);
            PlacementAvailabilitySettings settings = this.getPlacementAvailabilitySettings(placementJson);
            if (placementId >= 0 && !TextUtils.isEmpty((CharSequence)placementName) && !TextUtils.isEmpty((CharSequence)virtualItemName) && virtualItemCount > 0) {
                result = new Placement(placementId, placementName, virtualItemName, virtualItemCount, settings);
                if (settings != null) {
                    CappingManager.addCappingInfo(this.mContext, result);
                }
            }
        }
        return result;
    }

    private InterstitialPlacement parseSingleISPlacement(JSONObject placementJson) {
        InterstitialPlacement result = null;
        if (placementJson != null) {
            int placementId = placementJson.optInt("placementId", -1);
            String placementName = placementJson.optString("placementName", "");
            PlacementAvailabilitySettings settings = this.getPlacementAvailabilitySettings(placementJson);
            if (placementId >= 0 && !TextUtils.isEmpty((CharSequence)placementName)) {
                result = new InterstitialPlacement(placementId, placementName, settings);
                if (settings != null) {
                    CappingManager.addCappingInfo(this.mContext, result);
                }
            }
        }
        return result;
    }

    private OfferwallPlacement parseSingleOWPlacement(JSONObject placementJson) {
        OfferwallPlacement result = null;
        if (placementJson != null) {
            int placementId = placementJson.optInt("placementId", -1);
            String placementName = placementJson.optString("placementName", "");
            if (placementId >= 0 && !TextUtils.isEmpty((CharSequence)placementName)) {
                result = new OfferwallPlacement(placementId, placementName);
            }
        }
        return result;
    }

    private BannerPlacement parseSingleBNPlacement(JSONObject placementJson) {
        BannerPlacement result = null;
        if (placementJson != null) {
            int placementId = placementJson.optInt("placementId", -1);
            String placementName = placementJson.optString("placementName", "");
            PlacementAvailabilitySettings settings = this.getPlacementAvailabilitySettings(placementJson);
            if (placementId >= 0 && !TextUtils.isEmpty((CharSequence)placementName)) {
                result = new BannerPlacement(placementId, placementName, settings);
                if (settings != null) {
                    CappingManager.addCappingInfo(this.mContext, result);
                }
            }
        }
        return result;
    }

    private PlacementAvailabilitySettings getPlacementAvailabilitySettings(JSONObject placementJson) {
        JSONObject pacingJson;
        if (placementJson == null) {
            return null;
        }
        PlacementAvailabilitySettings.PlacementAvailabilitySettingsBuilder settingsBuilder = new PlacementAvailabilitySettings.PlacementAvailabilitySettingsBuilder();
        boolean delivery = placementJson.optBoolean("delivery", true);
        settingsBuilder.delivery(delivery);
        JSONObject cappingJson = placementJson.optJSONObject("capping");
        if (cappingJson != null) {
            PlacementCappingType cappingType = null;
            String cappingUnitString = cappingJson.optString("unit");
            if (!TextUtils.isEmpty((CharSequence)cappingUnitString)) {
                if (PlacementCappingType.PER_DAY.toString().equals(cappingUnitString)) {
                    cappingType = PlacementCappingType.PER_DAY;
                } else if (PlacementCappingType.PER_HOUR.toString().equals(cappingUnitString)) {
                    cappingType = PlacementCappingType.PER_HOUR;
                }
            }
            int cappingValue = cappingJson.optInt("maxImpressions", 0);
            boolean isCappingEnabled = cappingJson.optBoolean("enabled", false) && cappingValue > 0;
            settingsBuilder.capping(isCappingEnabled, cappingType, cappingValue);
        }
        if ((pacingJson = placementJson.optJSONObject("pacing")) != null) {
            int pacingValue = pacingJson.optInt("numOfSeconds", 0);
            boolean isPacingEnabled = pacingJson.optBoolean("enabled", false) && pacingValue > 0;
            settingsBuilder.pacing(isPacingEnabled, pacingValue);
        }
        return settingsBuilder.build();
    }

    private JSONObject getSection(JSONObject json, String sectionName) {
        JSONObject result = null;
        if (json != null) {
            result = json.optJSONObject(sectionName);
        }
        return result;
    }

    private JSONObject mergeJsons(JSONObject mainJson, JSONObject secondaryJson) {
        try {
            if (mainJson == null && secondaryJson == null) {
                return new JSONObject();
            }
            if (mainJson == null) {
                return secondaryJson;
            }
            if (secondaryJson == null) {
                return mainJson;
            }
            Iterator it = secondaryJson.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                if (mainJson.has(key)) continue;
                mainJson.put(key, secondaryJson.get(key));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return mainJson;
    }

    public int getMaxRVAdapters() {
        return this.mMaxRVAdapters;
    }

    public int getMaxISAdapters() {
        return this.mMaxISAdapters;
    }

    public int getMaxBannerAdapters() {
        return this.mMaxBannerAdapters;
    }

    public int decreaseMaxRVAdapters() {
        if (this.mMaxRVAdapters > 0) {
            --this.mMaxRVAdapters;
        }
        return this.mMaxRVAdapters;
    }

    public int decreaseMaxISAdapters() {
        if (this.mMaxISAdapters > 0) {
            --this.mMaxISAdapters;
        }
        return this.mMaxISAdapters;
    }

    public int decreaseMaxBannerAdapters() {
        if (this.mMaxBannerAdapters > 0) {
            --this.mMaxBannerAdapters;
        }
        return this.mMaxBannerAdapters;
    }

    public String getNextRVProvider() {
        try {
            ++this.mRVLoadPosition;
            if (this.mProviderOrder.getRewardedVideoProviderOrder().size() > this.mRVLoadPosition) {
                return this.mProviderOrder.getRewardedVideoProviderOrder().get(this.mRVLoadPosition);
            }
            return null;
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getNextProvider(RVLoadPosition: " + this.mRVLoadPosition + " RVProviders.size(): " + this.mProviderOrder.getRewardedVideoProviderOrder().size() + ")", e);
            return null;
        }
    }

    public String getNextISProvider() {
        try {
            ++this.mISLoadPosition;
            if (this.mProviderOrder.getInterstitialProviderOrder().size() > this.mISLoadPosition) {
                return this.mProviderOrder.getInterstitialProviderOrder().get(this.mISLoadPosition);
            }
            return null;
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getNextProvider(ISLoadPosition: " + this.mISLoadPosition + " ISProviders.size(): " + this.mProviderOrder.getInterstitialProviderOrder().size() + ")", e);
            return null;
        }
    }

    public String getNextBannerProvider() {
        try {
            ++this.mBannerLoadPosition;
            if (this.mProviderOrder.getBannerProviderOrder().size() > this.mBannerLoadPosition) {
                return this.mProviderOrder.getBannerProviderOrder().get(this.mBannerLoadPosition);
            }
            return null;
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getNextProvider(BannerLoadPosition: " + this.mBannerLoadPosition + " BannerProviders.size(): " + this.mProviderOrder.getBannerProviderOrder().size() + ")", e);
            return null;
        }
    }

    public String getRVBackFillProvider() {
        try {
            return this.mProviderOrder.getRVBackFillProvider();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getRVBackFillProvider", e);
            return null;
        }
    }

    public String getRVPremiumProvider() {
        try {
            return this.mProviderOrder.getRVPremiumProvider();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getRVPremiumProvider", e);
            return null;
        }
    }

    public String getISBackFillProvider() {
        try {
            return this.mProviderOrder.getISBackFillProvider();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getISBackFillProvider", e);
            return null;
        }
    }

    public String getISPremiumProvider() {
        try {
            return this.mProviderOrder.getISPremiumProvider();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.INTERNAL, "getISPremiumProvider", e);
            return null;
        }
    }

    public boolean hasMoreRVProvidersToLoad() {
        return this.mRVLoadPosition < this.mProviderOrder.getRewardedVideoProviderOrder().size() && this.mProviderOrder.getRewardedVideoProviderOrder().size() > 0;
    }

    public boolean hasMoreISProvidersToLoad() {
        return this.mISLoadPosition < this.mProviderOrder.getInterstitialProviderOrder().size() && this.mProviderOrder.getInterstitialProviderOrder().size() > 0;
    }

    public boolean hasMoreBannerProvidersToLoad() {
        return this.mBannerLoadPosition < this.mProviderOrder.getBannerProviderOrder().size() && this.mProviderOrder.getBannerProviderOrder().size() > 0;
    }

    public int getRVAdaptersLoadPosition() {
        return this.mRVLoadPosition;
    }

    public int getISAdaptersLoadPosition() {
        return this.mISLoadPosition;
    }

    public int getBannerAdaptersLoadPosition() {
        return this.mBannerLoadPosition;
    }

    public ProviderSettingsHolder getProviderSettingsHolder() {
        return this.mProviderSettingsHolder;
    }

    public ProviderOrder getProviderOrder() {
        return this.mProviderOrder;
    }

    public Configurations getConfigurations() {
        return this.mConfigurations;
    }

    private Context getContext() {
        return this.mContext;
    }
}

