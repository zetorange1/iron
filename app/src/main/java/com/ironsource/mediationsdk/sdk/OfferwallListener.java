/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.logger.IronSourceError;

public interface OfferwallListener {
    public void onOfferwallAvailable(boolean var1);

    public void onOfferwallOpened();

    public void onOfferwallShowFailed(IronSourceError var1);

    public boolean onOfferwallAdCredited(int var1, int var2, boolean var3);

    public void onGetOfferwallCreditsFailed(IronSourceError var1);

    public void onOfferwallClosed();
}

