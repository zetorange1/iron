/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.sdk;

import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.OfferwallListener;

public interface InternalOfferwallListener
extends OfferwallListener {
    public void onOfferwallAvailable(boolean var1, IronSourceError var2);
}

