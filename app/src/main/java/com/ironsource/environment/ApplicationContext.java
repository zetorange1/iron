/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.pm.PackageInfo
 *  android.content.pm.PackageManager
 *  android.content.pm.PackageManager$NameNotFoundException
 */
package com.ironsource.environment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;

public class ApplicationContext {
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static int getAppOrientation(Activity a) {
        return a.getRequestedOrientation();
    }

    public static String getDiskCacheDirPath(Context context) {
        String path = null;
        File internalFile = context.getCacheDir();
        if (internalFile != null) {
            path = internalFile.getPath();
        }
        return path;
    }

    public static boolean isPermissionGranted(Context context, String permission2) {
        int res = context.checkCallingOrSelfPermission(permission2);
        return res == 0;
    }

    static PackageInfo getAppPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(ApplicationContext.getPackageName(context), 0);
    }

    public static long getFirstInstallTime(Context context) {
        try {
            PackageInfo packageInfo = ApplicationContext.getAppPackageInfo(context);
            return packageInfo.firstInstallTime;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long getLastUpdateTime(Context context) {
        try {
            PackageInfo packageInfo = ApplicationContext.getAppPackageInfo(context);
            return packageInfo.lastUpdateTime;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getApplicationVersionName(Context context) {
        try {
            PackageInfo packageInfo = ApplicationContext.getAppPackageInfo(context);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}

