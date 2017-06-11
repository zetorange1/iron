/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ironsource.sdk.handlers;

import android.app.Activity;
import com.ironsource.sdk.agent.IronSourceAdsPublisherAgent;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.data.SSAEnums;
import com.ironsource.sdk.utils.IronSourceSharedPrefHelper;

public class BackButtonHandler {
    public static BackButtonHandler mInstance;

    public static BackButtonHandler getInstance() {
        if (mInstance == null) {
            return new BackButtonHandler();
        }
        return mInstance;
    }

    public boolean handleBackButton(Activity activity) {
        SSAEnums.BackButtonState state = IronSourceSharedPrefHelper.getSupersonicPrefHelper().getBackButtonState();
        switch (state) {
            case None: {
                return false;
            }
            case Device: {
                return false;
            }
            case Controller: {
                IronSourceAdsPublisherAgent ssaPubAgt = IronSourceAdsPublisherAgent.getInstance(activity);
                IronSourceWebView webViewController = ssaPubAgt.getWebViewController();
                if (webViewController != null) {
                    webViewController.nativeNavigationPressed("back");
                }
                return true;
            }
        }
        return false;
    }

}

