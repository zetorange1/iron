/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.annotation.TargetApi
 *  android.app.Activity
 *  android.content.BroadcastReceiver
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.content.pm.ApplicationInfo
 *  android.content.pm.PackageManager
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.media.AudioManager
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Environment
 *  android.os.StatFs
 *  android.provider.Settings
 *  android.provider.Settings$System
 *  android.telephony.TelephonyManager
 *  android.text.TextUtils
 *  android.util.DisplayMetrics
 *  android.view.Display
 *  android.view.View
 *  android.view.Window
 *  android.view.WindowManager
 */
package com.ironsource.environment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class DeviceStatus {
    private static final String DEVICE_OS = "android";
    private static final String GOOGLE_PLAY_SERVICES_CLASS_NAME = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
    private static final String GOOGLE_PLAY_SERVICES_GET_AID_INFO_METHOD_NAME = "getAdvertisingIdInfo";
    private static final String GOOGLE_PLAY_SERVICES_GET_AID_METHOD_NAME = "getId";
    private static final String GOOGLE_PLAY_SERVICES_IS_LIMITED_AD_TRACKING_METHOD_NAME = "isLimitAdTrackingEnabled";
    private static String uniqueId = null;
    private static final String MEDIATION_SHARED_PREFS = "Mediation_Shared_Preferences";
    public static final String UUID_ENABLED = "uuidEnabled";
    private static final String CACHED_UUID_KEY = "cachedUUID";

    public static long getDeviceLocalTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = calendar.getTime();
        return currentLocalTime.getTime();
    }

    public static int getDeviceTimeZoneOffsetInMinutes() {
        return - TimeZone.getDefault().getOffset(DeviceStatus.getDeviceLocalTime()) / 60000;
    }

    public static String[] getAdvertisingIdInfo(Context c) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class mAdvertisingIdClientClass = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
        Method getAdvertisingIdInfoMethod = mAdvertisingIdClientClass.getMethod("getAdvertisingIdInfo", Context.class);
        Object mInfoClass = getAdvertisingIdInfoMethod.invoke(mAdvertisingIdClientClass, new Object[]{c});
        Method getIdMethod = mInfoClass.getClass().getMethod("getId", new Class[0]);
        Method isLimitAdTrackingEnabledMethod = mInfoClass.getClass().getMethod("isLimitAdTrackingEnabled", new Class[0]);
        String advertisingId = getIdMethod.invoke(mInfoClass, new Object[0]).toString();
        boolean isLimitedTrackingEnabled = (Boolean)isLimitAdTrackingEnabledMethod.invoke(mInfoClass, new Object[0]);
        return new String[]{advertisingId, "" + isLimitedTrackingEnabled};
    }

    public static String getDeviceLanguage(Context c) throws Exception {
        return c.getResources().getConfiguration().locale.getLanguage();
    }

    private static long getFreeStorageInBytes(File f) {
        long SIZE_KB = 1024;
        long SIZE_MB = 0x100000;
        StatFs stat = new StatFs(f.getPath());
        long res = Build.VERSION.SDK_INT < 19 ? (long)stat.getAvailableBlocks() * (long)stat.getBlockSize() : stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return res / 0x100000;
    }

    public static boolean isExternalMemoryAvailableWritable() {
        return "mounted".equals(Environment.getExternalStorageState()) && Environment.isExternalStorageRemovable();
    }

    public static String getMobileCarrier(Context c) {
        TelephonyManager tm = (TelephonyManager)c.getSystemService("phone");
        return tm.getNetworkOperatorName();
    }

    public static String getAndroidOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getAndroidAPIVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getDeviceOEM() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceOs() {
        return "android";
    }

    public static boolean isRootedDevice() {
        return DeviceStatus.findBinary("su");
    }

    private static boolean findBinary(String binaryName) {
        boolean found;
        found = false;
        try {
            String[] paths;
            for (String path : paths = new String[]{"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"}) {
                File file = new File(path + binaryName);
                if (!file.exists()) continue;
                found = true;
                break;
            }
        }
        catch (Exception paths) {
            // empty catch block
        }
        return found;
    }

    public static boolean isRTL(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 17 && config.getLayoutDirection() == 1) {
            return true;
        }
        return false;
    }

    public static int getApplicationRotation(Context context) {
        Display defaultDisplay = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getRotation();
    }

    public static float getSystemVolumePercent(Context context) {
        AudioManager audio = (AudioManager)context.getSystemService("audio");
        return (float)audio.getStreamVolume(3) / (float)audio.getStreamMaxVolume(3);
    }

    public static int getDeviceWidth() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getDeviceHeight() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getActivityRequestedOrientation(Context activity) {
        return activity instanceof Activity ? ((Activity)activity).getRequestedOrientation() : -1;
    }

    public static int getDeviceDefaultOrientation(Context context) {
        int rotation = DeviceStatus.getApplicationRotation(context);
        int orientation = DeviceStatus.getDeviceOrientation(context);
        if ((rotation == 0 || rotation == 2) && orientation == 2 || (rotation == 1 || rotation == 3) && orientation == 1) {
            return 2;
        }
        return 1;
    }

    public static int getDeviceOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static float getDeviceDensity() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static List<ApplicationInfo> getInstalledApplications(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.getInstalledApplications(0);
    }

    public static boolean isDeviceOrientationLocked(Context context) {
        return Settings.System.getInt((ContentResolver)context.getContentResolver(), (String)"accelerometer_rotation", (int)0) != 1;
    }

    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    public static String getInternalCacheDirPath(Context context) {
        String path = null;
        File internalFile = context.getCacheDir();
        if (internalFile != null) {
            path = internalFile.getPath();
        }
        return path;
    }

    public static long getAvailableInternalMemorySizeInMegaBytes() {
        File path = Environment.getDataDirectory();
        long res = DeviceStatus.getFreeStorageInBytes(path);
        return res;
    }

    public static long getAvailableMemorySizeInMegaBytes(String path) {
        return DeviceStatus.getFreeStorageInBytes(new File(path));
    }

    public static long getAvailableExternalMemorySizeInMegaBytes() {
        long res = 0;
        if (DeviceStatus.isExternalMemoryAvailableWritable()) {
            File path = Environment.getExternalStorageDirectory();
            res = DeviceStatus.getFreeStorageInBytes(path);
        }
        return res;
    }

    @TargetApi(value=19)
    public static boolean isImmersiveSupported(Activity activity) {
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        return (uiOptions | 4096) == uiOptions || (uiOptions | 2048) == uiOptions;
    }

    public static int getBatteryLevel(Context context) {
        int batteryLevel = -1;
        try {
            int scale;
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            int level = batteryIntent != null ? batteryIntent.getIntExtra("level", -1) : 0;
            int n = scale = batteryIntent != null ? batteryIntent.getIntExtra("scale", -1) : 0;
            if (level != -1 && scale != -1) {
                batteryLevel = (int)((float)level / (float)scale * 100.0f);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return batteryLevel;
    }

    public static synchronized String getOrGenerateOnceUniqueIdentifier(Context context) {
        if (!TextUtils.isEmpty((CharSequence)uniqueId)) {
            return uniqueId;
        }
        try {
            SharedPreferences preferences = context.getSharedPreferences("Mediation_Shared_Preferences", 0);
            boolean isEnabled = preferences.getBoolean("uuidEnabled", true);
            if (isEnabled) {
                String id = preferences.getString("cachedUUID", "");
                if (TextUtils.isEmpty((CharSequence)id)) {
                    uniqueId = UUID.randomUUID().toString();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cachedUUID", uniqueId);
                    editor.apply();
                } else {
                    uniqueId = id;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueId;
    }
}

