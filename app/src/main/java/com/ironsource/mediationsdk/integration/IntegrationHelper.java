/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.content.pm.ApplicationInfo
 *  android.content.pm.PackageManager
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.text.TextUtils
 *  android.util.Log
 *  android.util.Pair
 */
package com.ironsource.mediationsdk.integration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.integration.AdapterObject;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegrationHelper {
    private static final String TAG = "IntegrationHelper";
    private static String[] SDK_COMPATIBILITY_VERSION_ARR = new String[]{"3.0", "3.1"};

    public static void validateIntegration(Activity activity) {
        String ironSource = "IronSource";
        String adcolony = "AdColony";
        String applovin = "AppLovin";
        String chartboost = "Chartboost";
        String hyprmx = "HyprMX";
        String unityads = "UnityAds";
        String vungle = "Vungle";
        String inmobi = "InMobi";
        String facebook = "Facebook";
        String mediaBrix = "MediaBrix";
        String tapjoy = "Tapjoy";
        String admob = "AdMob";
        List<String> generalPermissions = Arrays.asList("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE");
        String vungleWriteExternalStoragePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int vungleWriteExternalStoragePermissionMaxSdkVersion = 18;
        HashMap<String, Integer> vunglePermissionsToMaxSdkVersionMap = new HashMap<String, Integer>();
        vunglePermissionsToMaxSdkVersionMap.put("android.permission.WRITE_EXTERNAL_STORAGE", 18);
        List<String> vunglePermissions = Collections.singletonList("android.permission.WRITE_EXTERNAL_STORAGE");
        List<String> ironSourceActivities = Arrays.asList("com.ironsource.ironsourcesdkdemo.VideoActivity", "com.ironsource.sdk.controller.InterstitialActivity", "com.ironsource.sdk.controller.OpenUrlActivity");
        List<String> adColonyActivities = Arrays.asList("com.adcolony.sdk.AdColonyInterstitialActivity", "com.adcolony.sdk.AdColonyAdViewActivity");
        List<String> appLovinActivities = Arrays.asList("com.applovin.adview.AppLovinInterstitialActivity", "com.applovin.adview.AppLovinConfirmationActivity");
        List<String> chartboostActivities = Collections.singletonList("com.chartboost.sdk.CBImpressionActivity");
        List<String> hyprMXActivities = Arrays.asList("com.hyprmx.android.sdk.activity.HyprMXOfferViewerActivity", "com.hyprmx.android.sdk.activity.HyprMXRequiredInformationActivity", "com.hyprmx.android.sdk.activity.HyprMXNoOffersActivity", "com.hyprmx.android.sdk.videoplayer.HyprMXVideoPlayerActivity");
        List<String> vungleActivities = Arrays.asList("com.vungle.publisher.VideoFullScreenAdActivity", "com.vungle.publisher.MraidFullScreenAdActivity");
        List<String> inMobiActivities = Collections.singletonList("com.inmobi.rendering.InMobiAdActivity");
        List<String> inMobiBroadcastReceivers = Collections.singletonList("com.inmobi.commons.core.utilities.uid.ImIdShareBroadCastReceiver");
        List<String> facebookActivities = Collections.singletonList("com.facebook.ads.InterstitialAdActivity");
        List<String> mediaBrixActivities = Collections.singletonList("com.mediabrix.android.service.AdViewActivity");
        List<String> tapjoyActivities = Arrays.asList("com.tapjoy.TJAdUnitActivity", "com.tapjoy.mraid.view.ActionHandler", "com.tapjoy.mraid.view.Browser", "com.tapjoy.TJContentActivity");
        List<String> admobActivities = Collections.singletonList("com.google.android.gms.ads.AdActivity");
        List<String> unityAdsActivities = Arrays.asList("com.unity3d.ads.adunit.AdUnitActivity", "com.unity3d.ads.adunit.AdUnitSoftwareActivity");
        ArrayList<Pair<String, String>> vungleExternalLibraries = new ArrayList<Pair<String, String>>(){};
        String hyprMXSdk = "com.hyprmx.android.sdk.activity.HyprMXOfferViewerActivity";
        final AdapterObject ironSourceAdapter = new AdapterObject("IronSource", ironSourceActivities, false);
        ironSourceAdapter.setPermissions(generalPermissions);
        final AdapterObject adColonyAdapter = new AdapterObject("AdColony", adColonyActivities, true);
        final AdapterObject appLovinAdapter = new AdapterObject("AppLovin", appLovinActivities, true);
        final AdapterObject chartboostAdapter = new AdapterObject("Chartboost", chartboostActivities, true);
        final AdapterObject hyprMXAdapter = new AdapterObject("HyprMX", hyprMXActivities, true);
        hyprMXAdapter.setSdkName("com.hyprmx.android.sdk.activity.HyprMXOfferViewerActivity");
        final AdapterObject unityAdsAdapter = new AdapterObject("UnityAds", unityAdsActivities, true);
        final AdapterObject vungleAdapter = new AdapterObject("Vungle", vungleActivities, true);
        vungleAdapter.setExternalLibraries(vungleExternalLibraries);
        vungleAdapter.setPermissions(vunglePermissions);
        vungleAdapter.setPermissionToMaxSdkVersion(vunglePermissionsToMaxSdkVersionMap);
        final AdapterObject inMobiAdapter = new AdapterObject("InMobi", inMobiActivities, true);
        inMobiAdapter.setBroadcastReceivers(inMobiBroadcastReceivers);
        final AdapterObject facebookAdapter = new AdapterObject("Facebook", facebookActivities, true);
        final AdapterObject mediaBrixAdapter = new AdapterObject("MediaBrix", mediaBrixActivities, true);
        final AdapterObject tapjoyAdapter = new AdapterObject("Tapjoy", tapjoyActivities, true);
        final AdapterObject admobAdapter = new AdapterObject("AdMob", admobActivities, true);
        ArrayList<AdapterObject> adapters = new ArrayList<AdapterObject>(){};
        Log.i((String)"IntegrationHelper", (String)"Verifying Integration:");
        for (AdapterObject adapterObject : adapters) {
            boolean verified = true;
            Log.w((String)"IntegrationHelper", (String)("--------------- " + adapterObject.getName() + " --------------"));
            if (adapterObject.isAdapter() && !IntegrationHelper.validateAdapter(adapterObject)) {
                verified = false;
            }
            if (verified) {
                if (adapterObject.getSdkName() != null && !IntegrationHelper.validateSdk(adapterObject.getSdkName())) {
                    verified = false;
                }
                if (adapterObject.getPermissions() != null && !IntegrationHelper.validatePermissions(activity, adapterObject)) {
                    verified = false;
                }
                if (adapterObject.getActivities() != null && !IntegrationHelper.validateActivities(activity, adapterObject.getActivities())) {
                    verified = false;
                }
                if (adapterObject.getExternalLibraries() != null && !IntegrationHelper.validateExternalLibraries(adapterObject.getExternalLibraries())) {
                    verified = false;
                }
                if (adapterObject.getBroadcastReceivers() != null && !IntegrationHelper.validateBroadcastReceivers(activity, adapterObject.getBroadcastReceivers())) {
                    verified = false;
                }
            }
            if (verified) {
                Log.w((String)"IntegrationHelper", (String)(">>>> " + adapterObject.getName() + " - VERIFIED"));
                continue;
            }
            Log.e((String)"IntegrationHelper", (String)(">>>> " + adapterObject.getName() + " - NOT VERIFIED"));
        }
        IntegrationHelper.validateGooglePlayServices(activity);
    }

    private static void validateGooglePlayServices(final Activity activity) {
        String mGooglePlayServicesMetaData = "com.google.android.gms.version";
        String mGooglePlayServices = "Google Play Services";
        Thread thread = new Thread(){

            @Override
            public void run() {
                try {
                    Log.w((String)"IntegrationHelper", (String)"--------------- Google Play Services --------------");
                    PackageManager packageManager = activity.getPackageManager();
                    ApplicationInfo ai = packageManager.getApplicationInfo(activity.getPackageName(), 128);
                    Bundle bundle = ai.metaData;
                    boolean exists = bundle.containsKey("com.google.android.gms.version");
                    if (exists) {
                        IntegrationHelper.validationMessageIsPresent("Google Play Services", true);
                        String gaid = IronSourceObject.getInstance().getAdvertiserId((Context)activity);
                        if (!TextUtils.isEmpty((CharSequence)gaid)) {
                            Log.i((String)"IntegrationHelper", (String)("GAID is: " + gaid + " (use this for test devices)"));
                        }
                    } else {
                        IntegrationHelper.validationMessageIsPresent("Google Play Services", false);
                    }
                }
                catch (Exception e) {
                    IntegrationHelper.validationMessageIsPresent("Google Play Services", false);
                }
            }
        };
        thread.start();
    }

    private static boolean validateAdapter(AdapterObject adapter) {
        boolean result;
        block7 : {
            result = false;
            try {
                Class localClass = Class.forName(adapter.getAdapterName());
                try {
                    Field versionField = localClass.getDeclaredField("VERSION");
                    versionField.setAccessible(true);
                    String adapterVersion = (String)versionField.get(null);
                    for (int i = 0; i < SDK_COMPATIBILITY_VERSION_ARR.length; ++i) {
                        String sdkCompatVersion = SDK_COMPATIBILITY_VERSION_ARR[i];
                        if (TextUtils.isEmpty((CharSequence)adapterVersion) || adapterVersion.indexOf(sdkCompatVersion) != 0) continue;
                        result = true;
                        break;
                    }
                    if (result) {
                        IntegrationHelper.validationMessageIsVerified("Adapter version", true);
                        break block7;
                    }
                    Log.e((String)"IntegrationHelper", (String)(adapter.getName() + " adapter " + adapterVersion + " is incompatible with SDK version " + IronSourceUtils.getSDKVersion() + ", please update your adapter to version " + SDK_COMPATIBILITY_VERSION_ARR[0] + ".*"));
                }
                catch (Exception e) {
                    Log.e((String)"IntegrationHelper", (String)(adapter.getName() + " adapter version is incompatible with SDK version " + IronSourceUtils.getSDKVersion() + ", please update your adapter to version " + SDK_COMPATIBILITY_VERSION_ARR[0] + ".*"));
                }
            }
            catch (ClassNotFoundException e) {
                IntegrationHelper.validationMessageIsPresent("Adapter", false);
            }
        }
        if (result) {
            IntegrationHelper.validationMessageIsVerified("Adapter", true);
        }
        return result;
    }

    private static boolean validateSdk(String sdkName) {
        boolean result = false;
        try {
            Class localClass = Class.forName(sdkName);
            result = true;
            IntegrationHelper.validationMessageIsPresent("SDK", true);
        }
        catch (ClassNotFoundException e) {
            IntegrationHelper.validationMessageIsPresent("SDK", false);
        }
        return result;
    }

    private static boolean validateActivities(Activity activity, List<String> activities) {
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Log.i((String)"IntegrationHelper", (String)"*** Activities ***");
        for (String act : activities) {
            try {
                Class localClass = Class.forName(act);
                Intent intent = new Intent((Context)activity, localClass);
                List list = packageManager.queryIntentActivities(intent, 65536);
                if (list.size() > 0) {
                    IntegrationHelper.validationMessageIsPresent(act, true);
                    continue;
                }
                result = false;
                IntegrationHelper.validationMessageIsPresent(act, false);
            }
            catch (ClassNotFoundException e) {
                result = false;
                IntegrationHelper.validationMessageIsPresent(act, false);
            }
        }
        return result;
    }

    private static boolean validatePermissions(Activity activity, AdapterObject adapterObject) {
        List<String> permissions = adapterObject.getPermissions();
        Map<String, Integer> permissionsToMaxSdkVersionMap = adapterObject.getPermissionToMaxSdkVersion();
        Map<String, Integer> permissionsToMinSdkVersionMap = adapterObject.getPermissionToMinSdkVersion();
        int currentSdkVersion = Build.VERSION.SDK_INT;
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Log.i((String)"IntegrationHelper", (String)"*** Permissions ***");
        for (String permission2 : permissions) {
            int minSdkVersion;
            int maxSdkVersion;
            if (permissionsToMaxSdkVersionMap != null && permissionsToMaxSdkVersionMap.containsKey(permission2) && (maxSdkVersion = permissionsToMaxSdkVersionMap.get(permission2).intValue()) < currentSdkVersion || permissionsToMinSdkVersionMap != null && permissionsToMinSdkVersionMap.containsKey(permission2) && (minSdkVersion = permissionsToMinSdkVersionMap.get(permission2).intValue()) > currentSdkVersion) continue;
            int permissionExists = packageManager.checkPermission(permission2, activity.getPackageName());
            if (permissionExists == 0) {
                IntegrationHelper.validationMessageIsPresent(permission2, true);
                continue;
            }
            result = false;
            IntegrationHelper.validationMessageIsPresent(permission2, false);
        }
        return result;
    }

    private static boolean validateExternalLibraries(ArrayList<Pair<String, String>> externalLibraries) {
        boolean result = true;
        Log.i((String)"IntegrationHelper", (String)"*** External Libraries ***");
        for (Pair<String, String> externalLibrary : externalLibraries) {
            try {
                Class localClass = Class.forName((String)externalLibrary.first);
                IntegrationHelper.validationMessageIsPresent((String)externalLibrary.second, true);
            }
            catch (ClassNotFoundException e) {
                result = false;
                IntegrationHelper.validationMessageIsPresent((String)externalLibrary.second, false);
            }
        }
        return result;
    }

    private static boolean validateBroadcastReceivers(Activity activity, List<String> broadcastReceivers) {
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Log.i((String)"IntegrationHelper", (String)"*** Broadcast Receivers ***");
        for (String broadcastReceiver : broadcastReceivers) {
            try {
                Class localClass = Class.forName(broadcastReceiver);
                Intent intent = new Intent((Context)activity, localClass);
                List list = packageManager.queryBroadcastReceivers(intent, 65536);
                if (list.size() > 0) {
                    IntegrationHelper.validationMessageIsPresent(broadcastReceiver, true);
                    continue;
                }
                result = false;
                IntegrationHelper.validationMessageIsPresent(broadcastReceiver, false);
            }
            catch (ClassNotFoundException e) {
                result = false;
                IntegrationHelper.validationMessageIsPresent(broadcastReceiver, false);
            }
        }
        return result;
    }

    private static void validationMessageIsPresent(String paramToValidate, boolean successful) {
        if (successful) {
            Log.i((String)"IntegrationHelper", (String)(paramToValidate + " - VERIFIED"));
        } else {
            Log.e((String)"IntegrationHelper", (String)(paramToValidate + " - MISSING"));
        }
    }

    private static void validationMessageIsVerified(String paramToValidate, boolean successful) {
        if (successful) {
            Log.i((String)"IntegrationHelper", (String)(paramToValidate + " - VERIFIED"));
        } else {
            Log.e((String)"IntegrationHelper", (String)(paramToValidate + " - NOT VERIFIED"));
        }
    }

}

