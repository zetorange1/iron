/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.net.ConnectivityManager
 *  android.net.NetworkInfo
 *  android.telephony.TelephonyManager
 */
package com.ironsource.environment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectivityService {
    public static final String NETWORK_TYPE_3G = "3g";
    public static final String NETWORK_TYPE_WIFI = "wifi";

    public static String getConnectionType(Context context) {
        StringBuilder connectionType = new StringBuilder();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            String typeName = activeNetwork.getTypeName();
            int typeId = activeNetwork.getType();
            if (typeId == 0) {
                connectionType.append("3g");
            } else if (typeId == 1) {
                connectionType.append("wifi");
            } else {
                connectionType.append(typeName);
            }
        }
        return connectionType.toString();
    }

    public static boolean isConnectedWifi(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = connMgr.getNetworkInfo(1);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isConnectedMobile(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = connMgr.getNetworkInfo(0);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getNetworkOperator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
            return tm.getNetworkOperator();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static int getNetworkMCC(Context context) {
        try {
            return context.getResources().getConfiguration().mcc;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int getNetworkMNC(Context context) {
        try {
            return context.getResources().getConfiguration().mnc;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static String getSimOperator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
            return tm.getSimOperator();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static int getPhoneType(Context context) {
        int result = -1;
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
            result = tm.getPhoneType();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}

