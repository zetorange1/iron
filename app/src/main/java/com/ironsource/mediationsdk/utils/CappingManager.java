/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.text.TextUtils
 */
package com.ironsource.mediationsdk.utils;

import android.content.Context;
import android.text.TextUtils;
import com.ironsource.mediationsdk.model.BannerPlacement;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.model.PlacementAvailabilitySettings;
import com.ironsource.mediationsdk.model.PlacementCappingType;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.util.Calendar;
import java.util.TimeZone;

public class CappingManager {
    private static final String IS_DELIVERY_ENABLED = "CappingManager.IS_DELIVERY_ENABLED";
    private static final String IS_CAPPING_ENABLED = "CappingManager.IS_CAPPING_ENABLED";
    private static final String IS_PACING_ENABLED = "CappingManager.IS_PACING_ENABLED";
    private static final String MAX_NUMBER_OF_SHOWS = "CappingManager.MAX_NUMBER_OF_SHOWS";
    private static final String CAPPING_TYPE = "CappingManager.CAPPING_TYPE";
    private static final String SECONDS_BETWEEN_SHOWS = "CappingManager.SECONDS_BETWEEN_SHOWS";
    private static final String CURRENT_NUMBER_OF_SHOWS = "CappingManager.CURRENT_NUMBER_OF_SHOWS";
    private static final String CAPPING_TIME_THRESHOLD = "CappingManager.CAPPING_TIME_THRESHOLD";
    private static final String TIME_OF_THE_PREVIOUS_SHOW = "CappingManager.TIME_OF_THE_PREVIOUS_SHOW";

    public static synchronized void addCappingInfo(Context context, InterstitialPlacement placement) {
        if (context == null || placement == null) {
            return;
        }
        PlacementAvailabilitySettings availabilitySettings = placement.getPlacementAvailabilitySettings();
        if (availabilitySettings == null) {
            return;
        }
        CappingManager.addCappingInfo(context, "Interstitial", placement.getPlacementName(), availabilitySettings);
    }

    public static synchronized void addCappingInfo(Context context, Placement placement) {
        if (context == null || placement == null) {
            return;
        }
        PlacementAvailabilitySettings availabilitySettings = placement.getPlacementAvailabilitySettings();
        if (availabilitySettings == null) {
            return;
        }
        CappingManager.addCappingInfo(context, "Rewarded Video", placement.getPlacementName(), availabilitySettings);
    }

    public static synchronized void addCappingInfo(Context context, BannerPlacement placement) {
        if (context == null || placement == null) {
            return;
        }
        PlacementAvailabilitySettings availabilitySettings = placement.getPlacementAvailabilitySettings();
        if (availabilitySettings == null) {
            return;
        }
        CappingManager.addCappingInfo(context, "Banner", placement.getPlacementName(), availabilitySettings);
    }

    public static synchronized ECappingStatus isPlacementCapped(Context context, InterstitialPlacement placement) {
        if (context == null || placement == null || placement.getPlacementAvailabilitySettings() == null) {
            return ECappingStatus.NOT_CAPPED;
        }
        return CappingManager.isPlacementCapped(context, "Interstitial", placement.getPlacementName());
    }

    public static synchronized ECappingStatus isPlacementCapped(Context context, BannerPlacement placement) {
        if (context == null || placement == null || placement.getPlacementAvailabilitySettings() == null) {
            return ECappingStatus.NOT_CAPPED;
        }
        return CappingManager.isPlacementCapped(context, "Banner", placement.getPlacementName());
    }

    public static synchronized ECappingStatus isPlacementCapped(Context context, Placement placement) {
        if (context == null || placement == null || placement.getPlacementAvailabilitySettings() == null) {
            return ECappingStatus.NOT_CAPPED;
        }
        return CappingManager.isPlacementCapped(context, "Rewarded Video", placement.getPlacementName());
    }

    public static synchronized void incrementShowCounter(Context context, InterstitialPlacement placement) {
        if (placement != null) {
            CappingManager.incrementShowCounter(context, "Interstitial", placement.getPlacementName());
        }
    }

    public static synchronized void incrementShowCounter(Context context, Placement placement) {
        if (placement != null) {
            CappingManager.incrementShowCounter(context, "Rewarded Video", placement.getPlacementName());
        }
    }

    public static synchronized void incrementShowCounter(Context context, String placementName) {
        if (!TextUtils.isEmpty((CharSequence)placementName)) {
            CappingManager.incrementShowCounter(context, "Banner", placementName);
        }
    }

    private static String constructSharedPrefsKey(String adUnit, String baseConst, String placementName) {
        return adUnit + "_" + baseConst + "_" + placementName;
    }

    private static ECappingStatus isPlacementCapped(Context context, String adUnit, String placementName) {
        String secondsBetweenShowsKey;
        String timeOfPreviousShowKey;
        int secondsBetweenShows;
        long timeOfPreviousShow;
        long currentTime = System.currentTimeMillis();
        String deliveryKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_DELIVERY_ENABLED", placementName);
        boolean isDeliveryEnabled = IronSourceUtils.getBooleanFromSharedPrefs(context, deliveryKey, true);
        if (!isDeliveryEnabled) {
            return ECappingStatus.CAPPED_PER_DELIVERY;
        }
        String isPacingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_PACING_ENABLED", placementName);
        boolean isPacingEnabled = IronSourceUtils.getBooleanFromSharedPrefs(context, isPacingEnabledKey, false);
        if (isPacingEnabled && currentTime - (timeOfPreviousShow = IronSourceUtils.getLongFromSharedPrefs(context, timeOfPreviousShowKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.TIME_OF_THE_PREVIOUS_SHOW", placementName), 0)) < (long)((secondsBetweenShows = IronSourceUtils.getIntFromSharedPrefs(context, secondsBetweenShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.SECONDS_BETWEEN_SHOWS", placementName), 0)) * 1000)) {
            return ECappingStatus.CAPPED_PER_PACE;
        }
        String isCappingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_CAPPING_ENABLED", placementName);
        boolean isCappingEnabled = IronSourceUtils.getBooleanFromSharedPrefs(context, isCappingEnabledKey, false);
        if (isCappingEnabled) {
            String maxNumberOfShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.MAX_NUMBER_OF_SHOWS", placementName);
            int maxNumberOfShows = IronSourceUtils.getIntFromSharedPrefs(context, maxNumberOfShowsKey, 0);
            String currentNumberOfShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CURRENT_NUMBER_OF_SHOWS", placementName);
            int currentNumberOfShows = IronSourceUtils.getIntFromSharedPrefs(context, currentNumberOfShowsKey, 0);
            String timeThresholdKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CAPPING_TIME_THRESHOLD", placementName);
            long timeThreshold = IronSourceUtils.getLongFromSharedPrefs(context, timeThresholdKey, 0);
            if (currentTime >= timeThreshold) {
                IronSourceUtils.saveIntToSharedPrefs(context, currentNumberOfShowsKey, 0);
                IronSourceUtils.saveLongToSharedPrefs(context, timeThresholdKey, 0);
            } else if (currentNumberOfShows >= maxNumberOfShows) {
                return ECappingStatus.CAPPED_PER_COUNT;
            }
        }
        return ECappingStatus.NOT_CAPPED;
    }

    private static void incrementShowCounter(Context context, String adUnit, String placementName) {
        boolean isCappingEnabled;
        String isCappingEnabledKey;
        String isPacingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_PACING_ENABLED", placementName);
        boolean isPacingEnabled = IronSourceUtils.getBooleanFromSharedPrefs(context, isPacingEnabledKey, false);
        if (isPacingEnabled) {
            long currentTime = System.currentTimeMillis();
            String timeOfPreviousShowKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.TIME_OF_THE_PREVIOUS_SHOW", placementName);
            IronSourceUtils.saveLongToSharedPrefs(context, timeOfPreviousShowKey, currentTime);
        }
        if (isCappingEnabled = IronSourceUtils.getBooleanFromSharedPrefs(context, isCappingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_CAPPING_ENABLED", placementName), false)) {
            boolean isFirstShow;
            String maxNumberOfShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.MAX_NUMBER_OF_SHOWS", placementName);
            int maxNumberOfShows = IronSourceUtils.getIntFromSharedPrefs(context, maxNumberOfShowsKey, 0);
            String currentNumberOfShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CURRENT_NUMBER_OF_SHOWS", placementName);
            int currentNumberOfShows = IronSourceUtils.getIntFromSharedPrefs(context, currentNumberOfShowsKey, 0);
            boolean bl = isFirstShow = currentNumberOfShows == 0;
            if (isFirstShow) {
                String cappingTypeKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CAPPING_TYPE", placementName);
                String cappingTypeString = IronSourceUtils.getStringFromSharedPrefs(context, cappingTypeKey, PlacementCappingType.PER_DAY.toString());
                PlacementCappingType cappingType = null;
                for (PlacementCappingType type : PlacementCappingType.values()) {
                    if (!type.value.equals(cappingTypeString)) continue;
                    cappingType = type;
                    break;
                }
                long timeThreshold = CappingManager.initTimeThreshold(cappingType);
                String timeThresholdKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CAPPING_TIME_THRESHOLD", placementName);
                IronSourceUtils.saveLongToSharedPrefs(context, timeThresholdKey, timeThreshold);
            }
            IronSourceUtils.saveIntToSharedPrefs(context, currentNumberOfShowsKey, ++currentNumberOfShows);
        }
    }

    private static long initTimeThreshold(PlacementCappingType cappingType) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        switch (cappingType) {
            case PER_DAY: {
                calendar.set(14, 0);
                calendar.set(13, 0);
                calendar.set(12, 0);
                calendar.set(11, 0);
                calendar.add(6, 1);
                break;
            }
            case PER_HOUR: {
                calendar.set(14, 0);
                calendar.set(13, 0);
                calendar.set(12, 0);
                calendar.add(11, 1);
            }
        }
        return calendar.getTimeInMillis();
    }

    private static void addCappingInfo(Context context, String adUnit, String placementName, PlacementAvailabilitySettings availabilitySettings) {
        boolean isDeliveryEnabled = availabilitySettings.isDeliveryEnabled();
        String deliveryKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_DELIVERY_ENABLED", placementName);
        IronSourceUtils.saveBooleanToSharedPrefs(context, deliveryKey, isDeliveryEnabled);
        if (!isDeliveryEnabled) {
            return;
        }
        boolean isCappingEnabled = availabilitySettings.isCappingEnabled();
        String isCappingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_CAPPING_ENABLED", placementName);
        IronSourceUtils.saveBooleanToSharedPrefs(context, isCappingEnabledKey, isCappingEnabled);
        if (isCappingEnabled) {
            int maxNumberOfShows = availabilitySettings.getCappingValue();
            String maxNumberOfShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.MAX_NUMBER_OF_SHOWS", placementName);
            IronSourceUtils.saveIntToSharedPrefs(context, maxNumberOfShowsKey, maxNumberOfShows);
            PlacementCappingType cappingType = availabilitySettings.getCappingType();
            String cappingTypeKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.CAPPING_TYPE", placementName);
            IronSourceUtils.saveStringToSharedPrefs(context, cappingTypeKey, cappingType.toString());
        }
        boolean isPacingEnabled = availabilitySettings.isPacingEnabled();
        String isPacingEnabledKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.IS_PACING_ENABLED", placementName);
        IronSourceUtils.saveBooleanToSharedPrefs(context, isPacingEnabledKey, isPacingEnabled);
        if (isPacingEnabled) {
            int secondsBetweenShows = availabilitySettings.getPacingValue();
            String secondsBetweenShowsKey = CappingManager.constructSharedPrefsKey(adUnit, "CappingManager.SECONDS_BETWEEN_SHOWS", placementName);
            IronSourceUtils.saveIntToSharedPrefs(context, secondsBetweenShowsKey, secondsBetweenShows);
        }
    }

    public static enum ECappingStatus {
        CAPPED_PER_DELIVERY,
        CAPPED_PER_COUNT,
        CAPPED_PER_PACE,
        NOT_CAPPED;
        

        private ECappingStatus() {
        }
    }

}

