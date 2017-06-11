/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.pm.PackageInfo
 *  android.content.pm.PackageManager
 *  android.location.Location
 *  android.location.LocationManager
 *  android.net.ConnectivityManager
 *  android.net.NetworkInfo
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Environment
 *  android.os.StatFs
 *  android.telephony.TelephonyManager
 *  android.text.TextUtils
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.ironsource.environment.DeviceStatus;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.config.ConfigFile;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.sdk.GeneralProperties;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import org.json.JSONObject;

public class GeneralPropertiesWorker
implements Runnable {
    private final String TAG;
    private final String BUNDLE_ID = "bundleId";
    private final String ADVERTISING_ID = "advertisingId";
    private final String ADVERTISING_ID_IS_LIMIT_TRACKING = "isLimitAdTrackingEnabled";
    private final String APPLICATION_KEY = "appKey";
    private final String DEVICE_OS = "deviceOS";
    private final String ANDROID_OS_VERSION = "osVersion";
    private final String CONNECTION_TYPE = "connectionType";
    public static final String SDK_VERSION = "sdkVersion";
    private final String LANGUAGE = "language";
    private final String DEVICE_OEM = "deviceOEM";
    private final String DEVICE_MODEL = "deviceModel";
    private final String MOBILE_CARRIER = "mobileCarrier";
    private final String EXTERNAL_FREE_MEMORY = "externalFreeMemory";
    private final String INTERNAL_FREE_MEMORY = "internalFreeMemory";
    private final String BATTERY_LEVEL = "battery";
    private final String LOCATION_LAT = "lat";
    private final String LOCATION_LON = "lon";
    private final String GMT_MINUTES_OFFSET = "gmtMinutesOffset";
    private final String PUBLISHER_APP_VERSION = "appVersion";
    private final String KEY_SESSION_ID = "sessionId";
    private final String KEY_PLUGIN_TYPE = "pluginType";
    private final String KEY_PLUGIN_VERSION = "pluginVersion";
    private final String KEY_PLUGIN_FW_VERSION = "plugin_fw_v";
    private final String KEY_IS_ROOT = "jb";
    private final String ADVERTISING_ID_TYPE = "advertisingIdType";
    private final String MEDIATION_TYPE = "mt";
    private static final String CONNECTION_NONE_INT = "none";
    private static final String CONNECTION_WIFI_INT = "wifi";
    private static final String CONNECTION_CELLULAR_2G = "2g";
    private static final String CONNECTION_CELLULAR_3G = "3g";
    private static final String CONNECTION_CELLULAR_4G_LTE = "4g/lte";
    private static final String CONNECTION_WIMAX_INT = "wimax";
    private static final String CONNECTION_ETHERNET_INT = "ethernet";
    private static final String CONNECTION_WIFI = "WIFI";
    private static final String CONNECTION_CELLULAR = "MOBILE";
    private static final String CONNECTION_WIMAX = "WIMAX";
    private static final String CONNECTION_ETHERNET = "ETHERNET";
    private static final String UUID_TYPE = "UUID";
    private static final int MINUTES_OFFSET_STEP = 15;
    private static final int MAX_MINUTES_OFFSET = 840;
    private static final int MIN_MINUTES_OFFSET = -720;
    private Context mContext;

    private GeneralPropertiesWorker() {
        this.TAG = this.getClass().getSimpleName();
    }

    public GeneralPropertiesWorker(Context ctx) {
        this.TAG = this.getClass().getSimpleName();
        this.mContext = ctx.getApplicationContext();
    }

    @Override
    public void run() {
        try {
            Map<String, Object> params = this.collectInformation();
            GeneralProperties.getProperties().putKeys(params);
            IronSourceUtils.saveGeneralProperties(this.mContext, GeneralProperties.getProperties().toJSON());
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "Thread name = " + this.getClass().getSimpleName(), e);
        }
    }

    private Map<String, Object> collectInformation() {
        int gmtMinutesOffset;
        double[] lastKnownLocation;
        HashMap<String, Object> result = new HashMap<String, Object>();
        String strVal = this.generateUUID();
        if (!TextUtils.isEmpty((CharSequence)strVal)) {
            result.put("sessionId", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getBundleId()))) {
            result.put("bundleId", strVal);
            String publAppVersion = this.getPublisherApplicationVersion(strVal);
            if (!TextUtils.isEmpty((CharSequence)publAppVersion)) {
                result.put("appVersion", publAppVersion);
            }
        }
        result.put("appKey", this.getApplicationKey());
        String advertisingId = "";
        String advertisingIdType = "";
        boolean isLimitAdTrackingEnabled = false;
        try {
            String[] advertisingIdInfo = DeviceStatus.getAdvertisingIdInfo(this.mContext);
            if (advertisingIdInfo != null && advertisingIdInfo.length == 2) {
                if (!TextUtils.isEmpty((CharSequence)advertisingIdInfo[0])) {
                    advertisingId = advertisingIdInfo[0];
                }
                isLimitAdTrackingEnabled = Boolean.valueOf(advertisingIdInfo[1]);
            }
        }
        catch (Exception var10_7) {
            // empty catch block
        }
        if (!TextUtils.isEmpty((CharSequence)advertisingId)) {
            advertisingIdType = "GAID";
        } else {
            advertisingId = DeviceStatus.getOrGenerateOnceUniqueIdentifier(this.mContext);
            if (!TextUtils.isEmpty((CharSequence)advertisingId)) {
                advertisingIdType = "UUID";
            }
        }
        if (!TextUtils.isEmpty((CharSequence)advertisingId)) {
            result.put("advertisingId", advertisingId);
            result.put("advertisingIdType", advertisingIdType);
            result.put("isLimitAdTrackingEnabled", isLimitAdTrackingEnabled);
        }
        result.put("deviceOS", this.getDeviceOS());
        strVal = this.getAndroidVersion();
        if (!TextUtils.isEmpty((CharSequence)strVal)) {
            result.put("osVersion", this.getAndroidVersion());
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getConnectionType()))) {
            result.put("connectionType", strVal);
        }
        result.put("sdkVersion", this.getSDKVersion());
        strVal = this.getLanguage();
        if (!TextUtils.isEmpty((CharSequence)strVal)) {
            result.put("language", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getDeviceOEM()))) {
            result.put("deviceOEM", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getDeviceModel()))) {
            result.put("deviceModel", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getMobileCarrier()))) {
            result.put("mobileCarrier", strVal);
        }
        long longVal = this.getInternalStorageFreeSize();
        result.put("internalFreeMemory", longVal);
        longVal = this.getExternalStorageFreeSize();
        result.put("externalFreeMemory", longVal);
        int intVal = this.getBatteryLevel();
        result.put("battery", intVal);
        boolean allowLocation = IronSourceUtils.getBooleanFromSharedPrefs(this.mContext, "GeneralProperties.ALLOW_LOCATION_SHARED_PREFS_KEY", false);
        if (allowLocation && (lastKnownLocation = this.getLastKnownLocation()) != null && lastKnownLocation.length == 2) {
            result.put("lat", lastKnownLocation[0]);
            result.put("lon", lastKnownLocation[1]);
        }
        if (this.validateGmtMinutesOffset(gmtMinutesOffset = this.getGmtMinutesOffset())) {
            result.put("gmtMinutesOffset", gmtMinutesOffset);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getPluginType()))) {
            result.put("pluginType", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getPluginVersion()))) {
            result.put("pluginVersion", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getPluginFrameworkVersion()))) {
            result.put("plugin_fw_v", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = String.valueOf(DeviceStatus.isRootedDevice())))) {
            result.put("jb", strVal);
        }
        if (!TextUtils.isEmpty((CharSequence)(strVal = this.getMediationType()))) {
            result.put("mt", strVal);
        }
        return result;
    }

    private String getPublisherApplicationVersion(String packageName) {
        String result = "";
        try {
            result = this.mContext.getPackageManager().getPackageInfo((String)packageName, (int)0).versionName;
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return result;
    }

    private String getPluginType() {
        String result = "";
        try {
            result = ConfigFile.getConfigFile().getPluginType();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getPluginType()", e);
        }
        return result;
    }

    private String getPluginVersion() {
        String result = "";
        try {
            result = ConfigFile.getConfigFile().getPluginVersion();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getPluginVersion()", e);
        }
        return result;
    }

    private String getPluginFrameworkVersion() {
        String result = "";
        try {
            result = ConfigFile.getConfigFile().getPluginFrameworkVersion();
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, "getPluginFrameworkVersion()", e);
        }
        return result;
    }

    private String getBundleId() {
        try {
            return this.mContext.getPackageName();
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getApplicationKey() {
        return IronSourceObject.getInstance().getIronSourceAppKey();
    }

    private String getDeviceOS() {
        return "Android";
    }

    private String getAndroidVersion() {
        try {
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            return "" + sdkVersion + "(" + release + ")";
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getConnectionType() {
        if (this.mContext == null) {
            return "unknown";
        }
        ConnectivityManager cm = (ConnectivityManager)this.mContext.getSystemService("connectivity");
        if (cm == null) {
            return "unknown";
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
                int networkType = info.getSubtype();
                switch (networkType) {
                    case 1: 
                    case 2: 
                    case 4: 
                    case 7: 
                    case 11: {
                        return "2g";
                    }
                    case 3: 
                    case 5: 
                    case 6: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 12: 
                    case 14: 
                    case 15: {
                        return "3g";
                    }
                    case 13: {
                        return "4g/lte";
                    }
                }
            }
            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                return "wifi";
            }
            if (info.getTypeName().equalsIgnoreCase("WIMAX")) {
                return "wimax";
            }
            if (info.getTypeName().equalsIgnoreCase("ETHERNET")) {
                return "ethernet";
            }
        }
        return "none";
    }

    private String getSDKVersion() {
        return IronSourceUtils.getSDKVersion();
    }

    private String getLanguage() {
        try {
            return Locale.getDefault().getLanguage();
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getDeviceOEM() {
        try {
            return Build.MANUFACTURER;
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getDeviceModel() {
        try {
            return Build.MODEL;
        }
        catch (Exception e) {
            return "";
        }
    }

    private String getMobileCarrier() {
        String ret = "";
        try {
            String operatorName;
            TelephonyManager telephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
            if (telephonyManager != null && !(operatorName = telephonyManager.getNetworkOperatorName()).equals("")) {
                ret = operatorName;
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":getMobileCarrier()", e);
        }
        return ret;
    }

    private boolean isExternalStorageAbvailable() {
        try {
            return Environment.getExternalStorageState().equals("mounted");
        }
        catch (Exception e) {
            return false;
        }
    }

    private long getInternalStorageFreeSize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / 0x100000;
        }
        catch (Exception e) {
            return -1;
        }
    }

    private long getExternalStorageFreeSize() {
        if (this.isExternalStorageAbvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / 0x100000;
        }
        return -1;
    }

    private int getBatteryLevel() {
        int result = -1;
        try {
            int scale;
            Intent batteryIntent = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            int level = batteryIntent != null ? batteryIntent.getIntExtra("level", -1) : 0;
            int n = scale = batteryIntent != null ? batteryIntent.getIntExtra("scale", -1) : 0;
            if (level != -1 && scale != -1) {
                result = (int)((float)level / (float)scale * 100.0f);
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":getBatteryLevel()", e);
        }
        return result;
    }

    @SuppressLint(value={"MissingPermission"})
    private double[] getLastKnownLocation() {
        double[] result = new double[]{};
        long bestLocationTime = Long.MIN_VALUE;
        try {
            if (this.locationPermissionGranted()) {
                LocationManager locationManager = (LocationManager)this.mContext.getApplicationContext().getSystemService("location");
                Location bestLocation = null;
                for (String provider : locationManager.getAllProviders()) {
                    long currentTime;
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location == null || (currentTime = location.getTime()) <= bestLocationTime) continue;
                    bestLocation = location;
                    bestLocationTime = bestLocation.getTime();
                }
                if (bestLocation != null) {
                    double lat = bestLocation.getLatitude();
                    double lon = bestLocation.getLongitude();
                    result = new double[]{lat, lon};
                }
            }
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":getLastLocation()", e);
            result = new double[]{};
        }
        return result;
    }

    private boolean locationPermissionGranted() {
        try {
            String permission2 = "android.permission.ACCESS_FINE_LOCATION";
            int res = this.mContext.checkCallingOrSelfPermission(permission2);
            return res == 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    private int getGmtMinutesOffset() {
        int result = 0;
        try {
            TimeZone tz = TimeZone.getDefault();
            Calendar cal = GregorianCalendar.getInstance(tz);
            int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
            result = offsetInMillis / 1000 / 60;
            result = Math.round(result / 15) * 15;
        }
        catch (Exception e) {
            IronSourceLoggerManager.getLogger().logException(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":getGmtMinutesOffset()", e);
        }
        return result;
    }

    private boolean validateGmtMinutesOffset(int offset) {
        boolean isValid = offset <= 840 && offset >= -720 && offset % 15 == 0;
        return isValid;
    }

    private String generateUUID() {
        String result = UUID.randomUUID().toString().replaceAll("-", "");
        result = result + IronSourceUtils.getTimeStamp();
        return result;
    }

    private String getMediationType() {
        return IronSourceObject.getInstance().getMediationType();
    }
}

