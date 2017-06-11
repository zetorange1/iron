/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;

abstract class AbstractAdUnitManager {
    Activity mActivity;
    String mUserId;
    String mAppKey;
    IronSourceLoggerManager mLoggerManager = IronSourceLoggerManager.getLogger();
    ServerResponseWrapper mServerResponseWrapper;
    boolean mShouldTrackNetworkState = false;
    protected AbstractAdapter mBackFillAdapter;
    protected boolean mBackFillInitStarted;
    protected boolean mCanShowPremium = true;

    AbstractAdUnitManager() {
    }

    abstract void shouldTrackNetworkState(Context var1, boolean var2);

    abstract boolean isPremiumAdapter(String var1);

    abstract boolean isBackFillAvailable();

    protected void setCustomParams(AbstractAdapter providerAdapter) {
        try {
            String segment;
            String gender;
            Integer age = IronSourceObject.getInstance().getAge();
            if (age != null) {
                providerAdapter.setAge(age);
            }
            if ((gender = IronSourceObject.getInstance().getGender()) != null) {
                providerAdapter.setGender(gender);
            }
            if ((segment = IronSourceObject.getInstance().getMediationSegment()) != null) {
                providerAdapter.setMediationSegment(segment);
            }
        }
        catch (Exception e) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, providerAdapter.getProviderName() + ":setCustomParams():" + e.toString(), 3);
        }
    }

    protected boolean isBackFillAdapter(AbstractAdapter adapter) {
        if (this.mBackFillAdapter != null && adapter != null) {
            return adapter.getProviderName().equals(this.mBackFillAdapter.getProviderName());
        }
        return false;
    }

    protected synchronized boolean canShowPremium() {
        return this.mCanShowPremium;
    }

    protected synchronized void disablePremiumForCurrentSession() {
        this.mCanShowPremium = false;
    }
}

