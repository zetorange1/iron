/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.model;

import com.ironsource.mediationsdk.model.ProviderSettings;
import java.util.ArrayList;

public class ProviderSettingsHolder {
    private ArrayList<ProviderSettings> mProviderSettingsArrayList = new ArrayList();
    private static ProviderSettingsHolder mInstance;

    public static synchronized ProviderSettingsHolder getProviderSettingsHolder() {
        if (mInstance == null) {
            mInstance = new ProviderSettingsHolder();
        }
        return mInstance;
    }

    private ProviderSettingsHolder() {
    }

    public void addProviderSettings(ProviderSettings providerSettings) {
        if (providerSettings != null) {
            this.mProviderSettingsArrayList.add(providerSettings);
        }
    }

    public ProviderSettings getProviderSettings(String providerName) {
        for (ProviderSettings providerSettings : this.mProviderSettingsArrayList) {
            if (!providerSettings.getProviderName().equals(providerName)) continue;
            return providerSettings;
        }
        ProviderSettings ps = new ProviderSettings(providerName);
        this.addProviderSettings(ps);
        return ps;
    }

    public boolean containsProviderSettings(String providerName) {
        for (ProviderSettings providerSettings : this.mProviderSettingsArrayList) {
            if (!providerSettings.getProviderName().equals(providerName)) continue;
            return true;
        }
        return false;
    }

    public ArrayList<ProviderSettings> getProviderSettingsArrayList() {
        return this.mProviderSettingsArrayList;
    }
}

