/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  android.util.Pair
 */
package com.ironsource.mediationsdk.server;

import android.text.TextUtils;
import android.util.Pair;
import com.ironsource.mediationsdk.config.ConfigFile;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;

public class ServerURL {
    private static String BASE_URL_PREFIX = "https://init.supersonicads.com/sdk/v";
    private static String BASE_URL_SUFFIX = "?platform=android&";
    private static final String APPLICATION_KEY = "applicationKey";
    private static final String APPLICATION_USER_ID = "applicationUserId";
    private static final String SDK_VERSION = "sdkVersion";
    private static final String PLUGIN_TYPE = "pluginType";
    private static final String PLUGIN_VERSION = "pluginVersion";
    private static final String PLUGIN_FW_VERSION = "plugin_fw_v";
    private static final String GAID = "advId";
    private static final String IMPRESSION = "impression";
    private static final String PLACEMENT = "placementId";
    private static final String EQUAL = "=";
    private static final String AMPERSAND = "&";

    public static String getCPVProvidersURL(String applicationKey, String applicationUserId, String gaid) throws UnsupportedEncodingException {
        Vector<Pair<String, String>> array = new Vector<Pair<String, String>>();
        array.add((Pair)new Pair((Object)"applicationKey", (Object)applicationKey));
        array.add((Pair)new Pair((Object)"applicationUserId", (Object)applicationUserId));
        array.add((Pair)new Pair((Object)"sdkVersion", (Object)IronSourceUtils.getSDKVersion()));
        if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginType())) {
            array.add((Pair)new Pair((Object)"pluginType", (Object)ConfigFile.getConfigFile().getPluginType()));
        }
        if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginVersion())) {
            array.add((Pair)new Pair((Object)"pluginVersion", (Object)ConfigFile.getConfigFile().getPluginVersion()));
        }
        if (!TextUtils.isEmpty((CharSequence)ConfigFile.getConfigFile().getPluginFrameworkVersion())) {
            array.add((Pair)new Pair((Object)"plugin_fw_v", (Object)ConfigFile.getConfigFile().getPluginFrameworkVersion()));
        }
        if (!TextUtils.isEmpty((CharSequence)gaid)) {
            array.add((Pair)new Pair((Object)"advId", (Object)gaid));
        }
        String params = ServerURL.createURLParams(array);
        return ServerURL.getBaseUrl(IronSourceUtils.getSDKVersion()) + params;
    }

    public static String getRequestURL(String requestUrl, boolean hit, int placementId) throws UnsupportedEncodingException {
        Vector<Pair<String, String>> array = new Vector<Pair<String, String>>();
        array.add((Pair)new Pair((Object)"impression", (Object)Boolean.toString(hit)));
        array.add((Pair)new Pair((Object)"placementId", (Object)Integer.toString(placementId)));
        String params = ServerURL.createURLParams(array);
        return requestUrl + "&" + params;
    }

    private static String createURLParams(Vector<Pair<String, String>> array) throws UnsupportedEncodingException {
        String str = "";
        for (Pair<String, String> pair : array) {
            if (str.length() > 0) {
                str = str + "&";
            }
            str = str + (String)pair.first + "=" + URLEncoder.encode((String)pair.second, "UTF-8");
        }
        return str;
    }

    private static String getBaseUrl(String sdkVersion) {
        return BASE_URL_PREFIX + sdkVersion + BASE_URL_SUFFIX;
    }
}

