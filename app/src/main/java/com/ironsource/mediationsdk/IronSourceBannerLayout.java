/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.view.View
 *  android.view.ViewTreeObserver
 *  android.view.ViewTreeObserver$OnGlobalLayoutListener
 *  android.widget.FrameLayout
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.BannerAdaptersListener;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.events.InterstitialEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.BannerManagerListener;
import com.ironsource.mediationsdk.utils.CappingManager;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import org.json.JSONObject;

public class IronSourceBannerLayout
extends FrameLayout
implements BannerAdaptersListener {
    private AbstractAdapter mAdapter;
    private View mBannerView;
    private Activity mActivity;
    private EBannerSize mSize;
    private String mPlacementName;
    private boolean isImpressionReported = false;
    private boolean isAdLoaded = false;
    private boolean isDestoyed = false;
    private BannerManagerListener mBannerManager;
    private BannerListener mBannerListener;

    public IronSourceBannerLayout(Activity activity, EBannerSize size, BannerManagerListener bannerManager) {
        super((Context)activity);
        this.mBannerManager = bannerManager;
        this.mActivity = activity;
        if (size == null) {
            size = EBannerSize.BANNER;
        }
        this.mSize = size;
    }

    public void attachAdapterToBanner(AbstractAdapter adapter, View bannerView) {
        this.mAdapter = adapter;
        this.mBannerView = bannerView;
        this.resetBannerImpression();
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            public void onGlobalLayout() {
                if (IronSourceBannerLayout.this.isShown()) {
                    if (Build.VERSION.SDK_INT < 16) {
                        IronSourceBannerLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                    } else {
                        IronSourceBannerLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                    }
                    if (IronSourceBannerLayout.this.isAdLoaded) {
                        IronSourceBannerLayout.this.reportBannerImpression();
                    }
                }
            }
        });
    }

    public void destroyBanner() {
        this.isDestoyed = true;
        if (this.mAdapter != null) {
            this.mAdapter.destroyBanner(this);
        }
        this.resetBannerImpression();
        this.mBannerManager = null;
        this.mBannerListener = null;
        this.mActivity = null;
        this.mSize = null;
        this.mPlacementName = null;
        this.mBannerView = null;
    }

    public boolean isDestoyed() {
        return this.isDestoyed;
    }

    public View getBannerView() {
        return this.mBannerView;
    }

    public Activity getActivity() {
        return this.mActivity;
    }

    public EBannerSize getSize() {
        return this.mSize;
    }

    public String getPlacementName() {
        return this.mPlacementName;
    }

    public void setPlacementName(String placementName) {
        this.mPlacementName = placementName;
    }

    public void setBannerListener(BannerListener listener) {
        String logMessage = "setBannerListener()";
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        this.mBannerListener = listener;
    }

    public void removeBannerListener() {
        String logMessage = "removeBannerListener()";
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, logMessage, 1);
        this.mBannerListener = null;
    }

    public BannerListener getBannerListener() {
        return this.mBannerListener;
    }

    @Override
    public void onBannerAdLoaded(AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter) || this.isAdLoaded) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdLoaded() | internal | adapter: " + adapter.getProviderName(), 0);
        JSONObject providerData = IronSourceUtils.getProviderAdditionalData(adapter);
        JSONObject mediationData = IronSourceUtils.getMediationAdditionalData();
        try {
            int bannerSizeData = this.getSize().getValue();
            providerData.put("status", (Object)"true");
            mediationData.put("status", (Object)"true");
            providerData.put("bannerAdSize", bannerSizeData);
            mediationData.put("bannerAdSize", bannerSizeData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData providerEvent = new EventData(407, providerData);
        EventData mediationEvent = new EventData(407, mediationData);
        InterstitialEventsManager.getInstance().log(providerEvent);
        InterstitialEventsManager.getInstance().log(mediationEvent);
        this.isAdLoaded = true;
        if (this.isShown()) {
            this.reportBannerImpression();
        }
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdLoaded(adapter);
        }
        if (this.mBannerListener != null) {
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdLoaded()", 1);
            this.mBannerListener.onBannerAdLoaded();
        }
    }

    @Override
    public void onBannerAdLoadFailed(IronSourceError error, AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter)) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdLoadFailed() | internal | adapter: " + adapter.getProviderName(), 0);
        this.mAdapter = null;
        try {
            if (this.mBannerView != null) {
                this.removeView(this.mBannerView);
                this.mBannerView = null;
            }
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            int bannerSizeData = this.getSize().getValue();
            data.put("status", (Object)"false");
            data.put("errorCode", error.getErrorCode());
            data.put("bannerAdSize", bannerSizeData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData event = new EventData(407, data);
        InterstitialEventsManager.getInstance().log(event);
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdLoadFailed(error, adapter);
        }
    }

    @Override
    public void onBannerAdClicked(AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter)) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdClicked() | internal | adapter: " + adapter.getProviderName(), 0);
        JSONObject data = IronSourceUtils.getProviderAdditionalData(adapter);
        try {
            int bannerSizeData = this.getSize().getValue();
            data.put("bannerAdSize", bannerSizeData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EventData event = new EventData(408, data);
        InterstitialEventsManager.getInstance().log(event);
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdClicked(adapter);
        }
        if (this.mBannerListener != null) {
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdClicked()", 1);
            this.mBannerListener.onBannerAdClicked();
        }
    }

    @Override
    public void onBannerAdScreenPresented(AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter)) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdScreenPresented() | internal | adapter: " + adapter.getProviderName(), 0);
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdScreenPresented(adapter);
        }
        if (this.mBannerListener != null) {
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdScreenPresented()", 1);
            this.mBannerListener.onBannerAdScreenPresented();
        }
    }

    @Override
    public void onBannerAdScreenDismissed(AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter)) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdScreenDismissed() | internal | adapter: " + adapter.getProviderName(), 0);
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdScreenDismissed(adapter);
        }
        if (this.mBannerListener != null) {
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdScreenDismissed()", 1);
            this.mBannerListener.onBannerAdScreenDismissed();
        }
    }

    @Override
    public void onBannerAdLeftApplication(AbstractAdapter adapter) {
        if (this.shoudIgnoreThisCallback(adapter)) {
            return;
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "onBannerAdLeftApplication() | internal | adapter: " + adapter.getProviderName(), 0);
        if (this.mBannerManager != null) {
            this.mBannerManager.onBannerAdLeftApplication(adapter);
        }
        if (this.mBannerListener != null) {
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onBannerAdLeftApplication()", 1);
            this.mBannerListener.onBannerAdLeftApplication();
        }
    }

    private boolean shoudIgnoreThisCallback(AbstractAdapter adapter) {
        return this.mAdapter == null || adapter == null || !this.mAdapter.getProviderName().equals(adapter.getProviderName());
    }

    private synchronized void resetBannerImpression() {
        this.isImpressionReported = false;
        this.isAdLoaded = false;
    }

    private synchronized void reportBannerImpression() {
        if (!this.isImpressionReported) {
            this.isImpressionReported = true;
            CappingManager.incrementShowCounter((Context)this.mActivity, this.mPlacementName);
            if (this.mBannerManager != null) {
                this.mBannerManager.onBannerImpression(this.mAdapter, this);
            }
        }
    }

}

