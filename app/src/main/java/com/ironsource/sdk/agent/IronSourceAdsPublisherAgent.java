/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.MutableContextWrapper
 *  android.os.AsyncTask
 *  android.os.Handler
 *  android.os.Looper
 *  android.text.TextUtils
 */
package com.ironsource.sdk.agent;

import android.app.Activity;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.ironsource.sdk.SSAPublisher;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.data.AdUnitsReady;
import com.ironsource.sdk.data.DemandSource;
import com.ironsource.sdk.data.SSASession;
import com.ironsource.sdk.listeners.DSRewardedVideoListener;
import com.ironsource.sdk.listeners.OnGenericFunctionListener;
import com.ironsource.sdk.listeners.OnInterstitialListener;
import com.ironsource.sdk.listeners.OnOfferWallListener;
import com.ironsource.sdk.listeners.OnRewardedVideoListener;
import com.ironsource.sdk.utils.DeviceProperties;
import com.ironsource.sdk.utils.IronSourceAsyncHttpRequestTask;
import com.ironsource.sdk.utils.IronSourceSharedPrefHelper;
import com.ironsource.sdk.utils.Logger;
import com.ironsource.sdk.utils.SDKUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class IronSourceAdsPublisherAgent
implements SSAPublisher,
DSRewardedVideoListener {
    private static final String TAG = "IronSourceAdsPublisherAgent";
    private static IronSourceAdsPublisherAgent sInstance;
    private IronSourceWebView wvc;
    private SSASession session;
    private static MutableContextWrapper mutableContextWrapper;
    private Map<String, DemandSource> mDemandSourceMap;

    private IronSourceAdsPublisherAgent(final Activity activity, int debugMode) {
        IronSourceSharedPrefHelper.getSupersonicPrefHelper((Context)activity);
        this.mDemandSourceMap = new HashMap<String, DemandSource>();
        Logger.enableLogging(SDKUtils.getDebugMode());
        Logger.i("IronSourceAdsPublisherAgent", "C'tor");
        mutableContextWrapper = new MutableContextWrapper((Context)activity);
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                IronSourceAdsPublisherAgent.this.wvc = new IronSourceWebView((Context)mutableContextWrapper);
                IronSourceAdsPublisherAgent.this.wvc.registerConnectionReceiver((Context)activity);
                IronSourceAdsPublisherAgent.this.wvc.setDebugMode(SDKUtils.getDebugMode());
                IronSourceAdsPublisherAgent.this.wvc.downloadController();
            }
        });
        this.startSession((Context)activity);
    }

    public static synchronized IronSourceAdsPublisherAgent getInstance(Activity activity) {
        return IronSourceAdsPublisherAgent.getInstance(activity, 0);
    }

    public static synchronized IronSourceAdsPublisherAgent getInstance(Activity activity, int debugMode) {
        Logger.i("IronSourceAdsPublisherAgent", "getInstance()");
        if (sInstance == null) {
            sInstance = new IronSourceAdsPublisherAgent(activity, debugMode);
        } else {
            mutableContextWrapper.setBaseContext((Context)activity);
        }
        return sInstance;
    }

    public IronSourceWebView getWebViewController() {
        return this.wvc;
    }

    private void startSession(Context context) {
        this.session = new SSASession(context, SSASession.SessionType.launched);
    }

    public void resumeSession(Context context) {
        this.session = new SSASession(context, SSASession.SessionType.backFromBG);
    }

    private void endSession() {
        if (this.session != null) {
            this.session.endSession();
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().addSession(this.session);
            this.session = null;
        }
    }

    @Override
    public void initRewardedVideo(String applicationKey, String userId, String demandSourceName, Map<String, String> extraParameters, OnRewardedVideoListener listener) {
        DemandSource demandSource = new DemandSource(demandSourceName, extraParameters, listener);
        this.mDemandSourceMap.put(demandSourceName, demandSource);
        this.wvc.initRewardedVideo(applicationKey, userId, demandSourceName, this);
    }

    @Override
    public void showRewardedVideo(String demandSourceName) {
        this.wvc.showRewardedVideo(demandSourceName);
    }

    @Override
    public void initOfferWall(String applicationKey, String userId, Map<String, String> extraParameters, OnOfferWallListener listener) {
        this.wvc.initOfferWall(applicationKey, userId, extraParameters, listener);
    }

    @Override
    public void showOfferWall(Map<String, String> extraParameters) {
        this.wvc.showOfferWall(extraParameters);
    }

    @Override
    public void getOfferWallCredits(String applicationKey, String userId, OnOfferWallListener listener) {
        this.wvc.getOfferWallCredits(applicationKey, userId, listener);
    }

    @Override
    public void initInterstitial(String applicationKey, String userId, Map<String, String> extraParameters, OnInterstitialListener listener) {
        this.wvc.initInterstitial(applicationKey, userId, extraParameters, listener);
    }

    @Override
    public void loadInterstitial() {
        this.wvc.loadInterstitial();
    }

    @Override
    public boolean isInterstitialAdAvailable() {
        return this.wvc.isInterstitialAdAvailable();
    }

    @Override
    public void showInterstitial() {
        this.wvc.showInterstitial();
    }

    @Override
    public void forceShowInterstitial() {
        this.wvc.forceShowInterstitial();
    }

    @Override
    public void onResume(Activity activity) {
        mutableContextWrapper.setBaseContext((Context)activity);
        this.wvc.enterForeground();
        this.wvc.registerConnectionReceiver((Context)activity);
        if (this.session == null) {
            this.resumeSession((Context)activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        try {
            this.wvc.enterBackground();
            this.wvc.unregisterConnectionReceiver((Context)activity);
            this.endSession();
        }
        catch (Exception e) {
            e.printStackTrace();
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=" + e.getStackTrace()[0].getMethodName()});
        }
    }

    @Override
    public void release(Activity activity) {
        try {
            Logger.i("IronSourceAdsPublisherAgent", "release()");
            DeviceProperties.release();
            this.wvc.unregisterConnectionReceiver((Context)activity);
            if (Looper.getMainLooper().equals((Object)Looper.myLooper())) {
                this.wvc.destroy();
                this.wvc = null;
            } else {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable uiRunnable = new Runnable(){

                    @Override
                    public void run() {
                        IronSourceAdsPublisherAgent.this.wvc.destroy();
                        IronSourceAdsPublisherAgent.this.wvc = null;
                    }
                };
                uiHandler.post(uiRunnable);
            }
        }
        catch (Exception uiHandler) {
            // empty catch block
        }
        sInstance = null;
        this.endSession();
    }

    @Override
    public void runGenericFunction(String method, Map<String, String> keyValPairs, OnGenericFunctionListener listener) {
        this.wvc.runGenericFunction(method, keyValPairs, listener);
    }

    @Override
    public void onRVInitSuccess(AdUnitsReady adUnitsReady, String demandSourceName) {
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null) {
            demandSource.setDemandSourceInitState(2);
            OnRewardedVideoListener listener = demandSource.getListener();
            if (listener != null) {
                listener.onRVInitSuccess(adUnitsReady);
            }
        }
    }

    @Override
    public void onRVInitFail(String description, String demandSourceName) {
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null) {
            demandSource.setDemandSourceInitState(3);
            OnRewardedVideoListener listener = demandSource.getListener();
            if (listener != null) {
                listener.onRVInitFail(description);
            }
        }
    }

    @Override
    public void onRVNoMoreOffers(String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVNoMoreOffers();
        }
    }

    @Override
    public void onRVAdCredited(int credits, String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVAdCredited(credits);
        }
    }

    @Override
    public void onRVAdClosed(String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVAdClosed();
        }
    }

    @Override
    public void onRVAdOpened(String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVAdOpened();
        }
    }

    @Override
    public void onRVShowFail(String description, String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVShowFail(description);
        }
    }

    @Override
    public void onRVAdClicked(String demandSourceName) {
        OnRewardedVideoListener listener;
        DemandSource demandSource = this.getDemandSourceByName(demandSourceName);
        if (demandSource != null && (listener = demandSource.getListener()) != null) {
            listener.onRVAdClicked();
        }
    }

    public Collection<DemandSource> getDemandSources() {
        return this.mDemandSourceMap.values();
    }

    public DemandSource getDemandSourceByName(String demandSourceName) {
        if (TextUtils.isEmpty((CharSequence)demandSourceName)) {
            return null;
        }
        return this.mDemandSourceMap.get(demandSourceName);
    }

}

