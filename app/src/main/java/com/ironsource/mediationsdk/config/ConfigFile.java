/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.config;

import java.util.Arrays;

public class ConfigFile {
    private static ConfigFile mInstance;
    private String mPluginType;
    private String mPluginVersion;
    private String mPluginFrameworkVersion;
    private String[] mSupportedPlugins = new String[]{"Unity", "AdobeAir", "Xamarin", "Corona", "AdMob", "MoPub"};

    public static synchronized ConfigFile getConfigFile() {
        if (mInstance == null) {
            mInstance = new ConfigFile();
        }
        return mInstance;
    }

    public void setPluginData(String pluginType, String pluginVersion, String pluginFrameworkVersion) {
        if (pluginType != null) {
            this.mPluginType = Arrays.asList(this.mSupportedPlugins).contains(pluginType) ? pluginType : null;
        }
        if (pluginVersion != null) {
            this.mPluginVersion = pluginVersion;
        }
        if (pluginFrameworkVersion != null) {
            this.mPluginFrameworkVersion = pluginFrameworkVersion;
        }
    }

    public String getPluginType() {
        return this.mPluginType;
    }

    public String getPluginVersion() {
        return this.mPluginVersion;
    }

    public String getPluginFrameworkVersion() {
        return this.mPluginFrameworkVersion;
    }
}

