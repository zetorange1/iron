/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Activity
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.MutableContextWrapper
 *  android.content.pm.ApplicationInfo
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.graphics.Bitmap
 *  android.graphics.Color
 *  android.location.Location
 *  android.net.Uri
 *  android.os.AsyncTask
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.CountDownTimer
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.Message
 *  android.text.TextUtils
 *  android.util.Log
 *  android.view.KeyEvent
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnTouchListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.webkit.ConsoleMessage
 *  android.webkit.DownloadListener
 *  android.webkit.JavascriptInterface
 *  android.webkit.ValueCallback
 *  android.webkit.WebBackForwardList
 *  android.webkit.WebChromeClient
 *  android.webkit.WebChromeClient$CustomViewCallback
 *  android.webkit.WebResourceResponse
 *  android.webkit.WebSettings
 *  android.webkit.WebView
 *  android.webkit.WebView$WebViewTransport
 *  android.webkit.WebViewClient
 *  android.widget.FrameLayout
 *  android.widget.FrameLayout$LayoutParams
 *  android.widget.Toast
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.sdk.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.MutableContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.ironsource.environment.ApplicationContext;
import com.ironsource.environment.ConnectivityService;
import com.ironsource.environment.DeviceStatus;
import com.ironsource.environment.LocationService;
import com.ironsource.environment.UrlHandler;
import com.ironsource.ironsourcesdkdemo.VideoActivity;
import com.ironsource.sdk.agent.IronSourceAdsPublisherAgent;
import com.ironsource.sdk.controller.ControllerActivity;
import com.ironsource.sdk.controller.ControllerView;
import com.ironsource.sdk.controller.InterstitialActivity;
import com.ironsource.sdk.controller.OpenUrlActivity;
import com.ironsource.sdk.controller.VideoEventsListener;
import com.ironsource.sdk.data.AdUnitsReady;
import com.ironsource.sdk.data.AdUnitsState;
import com.ironsource.sdk.data.DemandSource;
import com.ironsource.sdk.data.SSABCParameters;
import com.ironsource.sdk.data.SSAEnums;
import com.ironsource.sdk.data.SSAFile;
import com.ironsource.sdk.data.SSAObj;
import com.ironsource.sdk.listeners.DSRewardedVideoListener;
import com.ironsource.sdk.listeners.OnGenericFunctionListener;
import com.ironsource.sdk.listeners.OnInterstitialListener;
import com.ironsource.sdk.listeners.OnOfferWallListener;
import com.ironsource.sdk.listeners.OnWebViewChangeListener;
import com.ironsource.sdk.precache.DownloadManager;
import com.ironsource.sdk.utils.DeviceProperties;
import com.ironsource.sdk.utils.IronSourceAsyncHttpRequestTask;
import com.ironsource.sdk.utils.IronSourceSharedPrefHelper;
import com.ironsource.sdk.utils.IronSourceStorageUtils;
import com.ironsource.sdk.utils.Logger;
import com.ironsource.sdk.utils.SDKUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IronSourceWebView
extends WebView
implements DownloadManager.OnPreCacheCompletion,
DownloadListener {
    private String TAG = IronSourceWebView.class.getSimpleName();
    private String PUB_TAG = "IronSource";
    public static int mDebugMode = 0;
    private final String GENERIC_MESSAGE = "We're sorry, some error occurred. we will investigate it";
    private String mRVAppKey;
    private String mRVUserId;
    private String mOWAppKey;
    private String mOWUserId;
    private Map<String, String> mOWExtraParameters;
    private Boolean mIsInterstitialAvailable = null;
    private String mISAppKey;
    private String mISUserId;
    private Map<String, String> mISExtraParameters;
    private String mOWCreditsAppKey;
    private String mOWCreditsUserId;
    public static String IS_STORE = "is_store";
    public static String IS_STORE_CLOSE = "is_store_close";
    public static String WEBVIEW_TYPE = "webview_type";
    public static String EXTERNAL_URL = "external_url";
    public static String SECONDARY_WEB_VIEW = "secondary_web_view";
    public static int DISPLAY_WEB_VIEW_INTENT = 0;
    public static int OPEN_URL_INTENT = 1;
    public static String APP_IDS = "appIds";
    public static String REQUEST_ID = "requestId";
    public static String IS_INSTALLED = "isInstalled";
    public static String RESULT = "result";
    private DownloadManager downloadManager;
    private boolean mISmiss;
    private boolean mOWmiss;
    private boolean mOWCreditsMiss;
    private boolean mGlobalControllerTimeFinish;
    private boolean isRemoveCloseEventHandler;
    private Uri mUri;
    private String mRequestParameters;
    private String mControllerKeyPressed = "interrupt";
    private CountDownTimer mCloseEventTimer;
    private CountDownTimer mLoadControllerTimer;
    private CountDownTimer mGlobalControllerTimer;
    private int mHiddenForceCloseWidth = 50;
    private int mHiddenForceCloseHeight = 50;
    private String mHiddenForceCloseLocation = "top-right";
    private ChromeClient mWebChromeClient;
    private View mCustomView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private FrameLayout mControllerLayout;
    private State mState;
    private String mOrientationState;
    private DSRewardedVideoListener mOnRewardedVideoListener;
    private OnGenericFunctionListener mOnGenericFunctionListener;
    private OnInterstitialListener mOnInitInterstitialListener;
    private OnOfferWallListener mOnOfferWallListener;
    private SSAEnums.ControllerState mControllerState = SSAEnums.ControllerState.None;
    private Boolean isKitkatAndAbove = null;
    private String mCacheDirectory;
    private VideoEventsListener mVideoEventsListener;
    private AdUnitsState mSavedState;
    private Object mSavedStateLocker = new Object();
    Context mCurrentActivityContext;
    Handler mUiHandler;
    private boolean mIsImmersive = false;
    private boolean mIsActivityThemeTranslucent = false;
    private static String JSON_KEY_SUCCESS = "success";
    private static String JSON_KEY_FAIL = "fail";
    private BroadcastReceiver mConnectionReceiver;
    private OnWebViewChangeListener mChangeListener;

    public IronSourceWebView(Context context) {
        super(context.getApplicationContext());
        this.mConnectionReceiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent) {
                if (IronSourceWebView.this.mControllerState == SSAEnums.ControllerState.Ready) {
                    String networkType = "none";
                    if (ConnectivityService.isConnectedWifi(context)) {
                        networkType = "wifi";
                    } else if (ConnectivityService.isConnectedMobile(context)) {
                        networkType = "3g";
                    }
                    IronSourceWebView.this.deviceStatusChanged(networkType);
                }
            }
        };
        Logger.i(this.TAG, "C'tor");
        this.mCacheDirectory = this.initializeCacheDirectory(context.getApplicationContext());
        this.mCurrentActivityContext = context;
        this.initLayout(this.mCurrentActivityContext);
        this.mSavedState = new AdUnitsState();
        this.downloadManager = this.getDownloadManager();
        this.downloadManager.setOnPreCacheCompletion(this);
        this.mWebChromeClient = new ChromeClient();
        this.setWebViewClient((WebViewClient)new ViewClient());
        this.setWebChromeClient((WebChromeClient)this.mWebChromeClient);
        this.setWebViewSettings();
        this.addJavascriptInterface((Object)this.createJSInterface(context), "Android");
        this.setDownloadListener((DownloadListener)this);
        this.setOnTouchListener((View.OnTouchListener)new SupersonicWebViewTouchListener());
        this.mUiHandler = this.createMainThreadHandler();
    }

    JSInterface createJSInterface(Context context) {
        return new JSInterface(context);
    }

    Handler createMainThreadHandler() {
        return new Handler(Looper.getMainLooper());
    }

    DownloadManager getDownloadManager() {
        return DownloadManager.getInstance(this.mCacheDirectory);
    }

    String initializeCacheDirectory(Context context) {
        return IronSourceStorageUtils.initializeCacheDirectory(context.getApplicationContext());
    }

    private void initLayout(Context context) {
        FrameLayout.LayoutParams coverScreenParams = new FrameLayout.LayoutParams(-1, -1);
        this.mControllerLayout = new FrameLayout(context);
        this.mCustomViewContainer = new FrameLayout(context);
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(-1, -1);
        this.mCustomViewContainer.setLayoutParams((ViewGroup.LayoutParams)fp);
        this.mCustomViewContainer.setVisibility(8);
        FrameLayout mContentView = new FrameLayout(context);
        FrameLayout.LayoutParams lpChild2 = new FrameLayout.LayoutParams(-1, -1);
        mContentView.setLayoutParams((ViewGroup.LayoutParams)lpChild2);
        mContentView.addView((View)this);
        this.mControllerLayout.addView((View)this.mCustomViewContainer, (ViewGroup.LayoutParams)coverScreenParams);
        this.mControllerLayout.addView((View)mContentView);
    }

    private void setWebViewSettings() {
        WebSettings s = this.getSettings();
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        s.setBuiltInZoomControls(false);
        s.setJavaScriptEnabled(true);
        s.setSupportMultipleWindows(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setGeolocationEnabled(true);
        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        s.setDomStorageEnabled(true);
        try {
            this.setDisplayZoomControls(s);
            this.setMediaPlaybackJellyBean(s);
        }
        catch (Throwable e) {
            Logger.e(this.TAG, "setWebSettings - " + e.toString());
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=setWebViewSettings"});
        }
    }

    private void setDisplayZoomControls(WebSettings s) {
        if (Build.VERSION.SDK_INT > 11) {
            s.setDisplayZoomControls(false);
        }
    }

    public WebBackForwardList saveState(Bundle outState) {
        return super.saveState(outState);
    }

    @SuppressLint(value={"NewApi"})
    private void setMediaPlaybackJellyBean(WebSettings s) {
        if (Build.VERSION.SDK_INT >= 17) {
            s.setMediaPlaybackRequiresUserGesture(false);
        }
    }

    @SuppressLint(value={"NewApi"})
    private void setWebDebuggingEnabled() {
        if (Build.VERSION.SDK_INT >= 19) {
            IronSourceWebView.setWebContentsDebuggingEnabled((boolean)true);
        }
    }

    public void downloadController() {
        IronSourceStorageUtils.deleteFile(this.mCacheDirectory, "", "mobileController.html");
        String controllerPath = "";
        String controllerUrl = SDKUtils.getControllerUrl();
        SSAFile indexHtml = new SSAFile(controllerUrl, controllerPath);
        this.mGlobalControllerTimer = new CountDownTimer(40000, 1000){

            public void onTick(long millisUntilFinished) {
                Logger.i(IronSourceWebView.this.TAG, "Global Controller Timer Tick " + millisUntilFinished);
            }

            public void onFinish() {
                Logger.i(IronSourceWebView.this.TAG, "Global Controller Timer Finish");
                IronSourceWebView.this.mGlobalControllerTimeFinish = true;
            }
        }.start();
        if (!this.downloadManager.isMobileControllerThreadLive()) {
            Logger.i(this.TAG, "Download Mobile Controller: " + controllerUrl);
            this.downloadManager.downloadMobileControllerFile(indexHtml);
        } else {
            Logger.i(this.TAG, "Download Mobile Controller: already alive");
        }
    }

    public void setDebugMode(int debugMode) {
        mDebugMode = debugMode;
    }

    public int getDebugMode() {
        return mDebugMode;
    }

    private boolean shouldNotifyDeveloper(String product) {
        boolean shouldNotify = false;
        if (TextUtils.isEmpty((CharSequence)product)) {
            Logger.d(this.TAG, "Trying to trigger a listener - no product was found");
            return false;
        }
        if (product.equalsIgnoreCase(SSAEnums.ProductType.Interstitial.toString())) {
            shouldNotify = this.mOnInitInterstitialListener != null;
        } else if (product.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
            shouldNotify = this.mOnRewardedVideoListener != null;
        } else if (product.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString()) || product.equalsIgnoreCase(SSAEnums.ProductType.OfferWallCredits.toString())) {
            boolean bl = shouldNotify = this.mOnOfferWallListener != null;
        }
        if (!shouldNotify) {
            Logger.d(this.TAG, "Trying to trigger a listener - no listener was found for product " + product);
        }
        return shouldNotify;
    }

    public void setOrientationState(String orientation) {
        this.mOrientationState = orientation;
    }

    public String getOrientationState() {
        return this.mOrientationState;
    }

    public static void setEXTERNAL_URL(String EXTERNAL_URL) {
        IronSourceWebView.EXTERNAL_URL = EXTERNAL_URL;
    }

    public void setVideoEventsListener(VideoEventsListener listener) {
        this.mVideoEventsListener = listener;
    }

    public void removeVideoEventsListener() {
        this.mVideoEventsListener = null;
    }

    private void setWebviewBackground(String value) {
        SSAObj ssaObj = new SSAObj(value);
        String keyColor = ssaObj.getString("color");
        int bgColor = 0;
        if (!"transparent".equalsIgnoreCase(keyColor)) {
            bgColor = Color.parseColor((String)keyColor);
        }
        this.setBackgroundColor(bgColor);
    }

    public void load(final int loadAttemp) {
        try {
            this.loadUrl("about:blank");
        }
        catch (Throwable e) {
            Logger.e(this.TAG, "WebViewController:: load: " + e.toString());
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=webviewLoadBlank"});
        }
        String controllerPath = "file://" + this.mCacheDirectory + File.separator + "mobileController.html";
        File file = new File(this.mCacheDirectory + File.separator + "mobileController.html");
        if (file.exists()) {
            this.mRequestParameters = this.getRequestParameters();
            String controllerPathWithParams = controllerPath + "?" + this.mRequestParameters;
            this.mLoadControllerTimer = new CountDownTimer(10000, 1000){

                public void onTick(long millisUntilFinished) {
                    Logger.i(IronSourceWebView.this.TAG, "Loading Controller Timer Tick " + millisUntilFinished);
                }

                public void onFinish() {
                    Logger.i(IronSourceWebView.this.TAG, "Loading Controller Timer Finish");
                    if (loadAttemp == 2) {
                        IronSourceWebView.this.mGlobalControllerTimer.cancel();
                        Collection<DemandSource> demandSources = IronSourceAdsPublisherAgent.getInstance((Activity)IronSourceWebView.this.getCurrentActivityContext()).getDemandSources();
                        for (DemandSource demandSource : demandSources) {
                            if (demandSource.getDemandSourceInitState() != 1) continue;
                            IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.RewardedVideo, demandSource.getDemandSourceName());
                        }
                        if (IronSourceWebView.this.mISmiss) {
                            IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.Interstitial, null);
                        }
                        if (IronSourceWebView.this.mOWmiss) {
                            IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.OfferWall, null);
                        }
                        if (IronSourceWebView.this.mOWCreditsMiss) {
                            IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.OfferWallCredits, null);
                        }
                    } else {
                        IronSourceWebView.this.load(2);
                    }
                }
            }.start();
            try {
                this.loadUrl(controllerPathWithParams);
            }
            catch (Throwable e) {
                Logger.e(this.TAG, "WebViewController:: load: " + e.toString());
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=webviewLoadWithPath"});
            }
            Logger.i(this.TAG, "load(): " + controllerPathWithParams);
        } else {
            Logger.i(this.TAG, "load(): Mobile Controller HTML Does not exist");
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=htmlControllerDoesNotExistOnFileSystem"});
        }
    }

    private void initProduct(String applicationKey, String userId, SSAEnums.ProductType type, String action, String demandSourceName) {
        if (TextUtils.isEmpty((CharSequence)userId) || TextUtils.isEmpty((CharSequence)applicationKey)) {
            this.triggerOnControllerInitProductFail("User id or Application key are missing", type, demandSourceName);
            return;
        }
        if (this.mControllerState == SSAEnums.ControllerState.Ready) {
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().setApplicationKey(applicationKey, type);
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().setUserID(userId, type);
            this.createInitProductJSMethod(type, demandSourceName);
        } else {
            this.setMissProduct(type, demandSourceName);
            if (this.mControllerState == SSAEnums.ControllerState.Failed) {
                this.triggerOnControllerInitProductFail(SDKUtils.createErrorMessage(action, "Initiating Controller"), type, demandSourceName);
            } else if (this.mGlobalControllerTimeFinish) {
                this.downloadController();
            }
        }
    }

    public void initRewardedVideo(String applicationKey, String userId, String demandSourceName, DSRewardedVideoListener listener) {
        this.mRVAppKey = applicationKey;
        this.mRVUserId = userId;
        this.mOnRewardedVideoListener = listener;
        this.mSavedState.setRVAppKey(applicationKey);
        this.mSavedState.setRVUserId(userId);
        this.initProduct(applicationKey, userId, SSAEnums.ProductType.RewardedVideo, "Init RV", demandSourceName);
    }

    public void initInterstitial(String applicationKey, String userId, Map<String, String> extraParameters, OnInterstitialListener listener) {
        this.mISAppKey = applicationKey;
        this.mISUserId = userId;
        this.mISExtraParameters = extraParameters;
        this.mOnInitInterstitialListener = listener;
        this.mSavedState.setInterstitialAppKey(this.mISAppKey);
        this.mSavedState.setInterstitialUserId(this.mISUserId);
        this.mSavedState.setInterstitialExtraParams(this.mISExtraParameters);
        this.mSavedState.setReportInitInterstitial(true);
        this.initProduct(this.mISAppKey, this.mISUserId, SSAEnums.ProductType.Interstitial, "Init IS", null);
    }

    public void loadInterstitial() {
        if (!this.isInterstitialAdAvailable()) {
            this.mSavedState.setReportLoadInterstitial(true);
            String script = this.generateJSToInject("loadInterstitial", "onLoadInterstitialSuccess", "onLoadInterstitialFail");
            this.injectJavascript(script);
        } else if (this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
            this.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialLoadSuccess();
                }
            });
        }
    }

    public boolean isInterstitialAdAvailable() {
        return this.mIsInterstitialAvailable == null ? false : this.mIsInterstitialAvailable;
    }

    public void showInterstitial() {
        String script = this.generateJSToInject("showInterstitial", "onShowInterstitialSuccess", "onShowInterstitialFail");
        this.injectJavascript(script);
    }

    public void forceShowInterstitial() {
        String script = this.generateJSToInject("forceShowInterstitial", "onShowInterstitialSuccess", "onShowInterstitialFail");
        this.injectJavascript(script);
    }

    public void initOfferWall(String applicationKey, String userId, Map<String, String> extraParameters, OnOfferWallListener listener) {
        this.mOWAppKey = applicationKey;
        this.mOWUserId = userId;
        this.mOWExtraParameters = extraParameters;
        this.mOnOfferWallListener = listener;
        this.mSavedState.setOfferWallExtraParams(this.mOWExtraParameters);
        this.mSavedState.setOfferwallReportInit(true);
        this.initProduct(this.mOWAppKey, this.mOWUserId, SSAEnums.ProductType.OfferWall, "Init OW", null);
    }

    public void showOfferWall(Map<String, String> extraParameters) {
        this.mOWExtraParameters = extraParameters;
        String script = this.generateJSToInject("showOfferWall", "onShowOfferWallSuccess", "onShowOfferWallFail");
        this.injectJavascript(script);
    }

    public void getOfferWallCredits(String applicationKey, String userId, OnOfferWallListener listener) {
        this.mOWCreditsAppKey = applicationKey;
        this.mOWCreditsUserId = userId;
        this.mOnOfferWallListener = listener;
        this.initProduct(this.mOWCreditsAppKey, this.mOWCreditsUserId, SSAEnums.ProductType.OfferWallCredits, "Show OW Credits", null);
    }

    private void createInitProductJSMethod(SSAEnums.ProductType type, String demandSourceName) {
        String script = null;
        if (type == SSAEnums.ProductType.RewardedVideo) {
            DemandSource demandSource = IronSourceAdsPublisherAgent.getInstance((Activity)this.getCurrentActivityContext()).getDemandSourceByName(demandSourceName);
            HashMap<String, String> rvParamsMap = new HashMap<String, String>();
            rvParamsMap.put("applicationKey", this.mRVAppKey);
            rvParamsMap.put("applicationUserId", this.mRVUserId);
            if (demandSource != null) {
                if (demandSource.getExtraParams() != null) {
                    rvParamsMap.putAll(demandSource.getExtraParams());
                }
                if (!TextUtils.isEmpty((CharSequence)demandSourceName)) {
                    rvParamsMap.put("demandSourceName", demandSourceName);
                }
            }
            String params = this.flatMapToJsonAsString(rvParamsMap);
            script = this.generateJSToInject("initRewardedVideo", params, "onInitRewardedVideoSuccess", "onInitRewardedVideoFail");
        } else if (type == SSAEnums.ProductType.Interstitial) {
            HashMap<String, String> interstitialParamsMap = new HashMap<String, String>();
            interstitialParamsMap.put("applicationKey", this.mISAppKey);
            interstitialParamsMap.put("applicationUserId", this.mISUserId);
            if (this.mISExtraParameters != null) {
                interstitialParamsMap.putAll(this.mISExtraParameters);
            }
            String params = this.flatMapToJsonAsString(interstitialParamsMap);
            script = this.generateJSToInject("initInterstitial", params, "onInitInterstitialSuccess", "onInitInterstitialFail");
        } else if (type == SSAEnums.ProductType.OfferWall) {
            HashMap<String, String> offerwallParamsMap = new HashMap<String, String>();
            offerwallParamsMap.put("applicationKey", this.mOWAppKey);
            offerwallParamsMap.put("applicationUserId", this.mOWUserId);
            if (this.mOWExtraParameters != null) {
                offerwallParamsMap.putAll(this.mOWExtraParameters);
            }
            String params = this.flatMapToJsonAsString(offerwallParamsMap);
            script = this.generateJSToInject("initOfferWall", params, "onInitOfferWallSuccess", "onInitOfferWallFail");
        } else if (type == SSAEnums.ProductType.OfferWallCredits) {
            String params = this.parseToJson("productType", "OfferWall", "applicationKey", this.mOWCreditsAppKey, "applicationUserId", this.mOWCreditsUserId, null, null, null, false);
            script = this.generateJSToInject("getUserCredits", params, "null", "onGetUserCreditsFail");
        }
        if (script != null) {
            this.injectJavascript(script);
        }
    }

    private String flatMapToJsonAsString(Map<String, String> params) {
        JSONObject jsObj = new JSONObject();
        if (params != null) {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = it.next();
                try {
                    jsObj.putOpt(pairs.getKey(), (Object)SDKUtils.encodeString(pairs.getValue()));
                }
                catch (JSONException e) {
                    Logger.i(this.TAG, "flatMapToJsonAsStringfailed " + e.toString());
                }
                it.remove();
            }
        }
        return jsObj.toString();
    }

    void setMissProduct(SSAEnums.ProductType type, String demandSourceName) {
        if (type == SSAEnums.ProductType.RewardedVideo) {
            DemandSource demandSource = IronSourceAdsPublisherAgent.getInstance((Activity)this.getCurrentActivityContext()).getDemandSourceByName(demandSourceName);
            if (demandSource != null) {
                demandSource.setDemandSourceInitState(1);
            }
        } else if (type == SSAEnums.ProductType.Interstitial) {
            this.mISmiss = true;
        } else if (type == SSAEnums.ProductType.OfferWall) {
            this.mOWmiss = true;
        } else if (type == SSAEnums.ProductType.OfferWallCredits) {
            this.mOWCreditsMiss = true;
        }
        Logger.i(this.TAG, "setMissProduct(" + (Object)((Object)type) + ")");
    }

    private void triggerOnControllerInitProductFail(final String message, final SSAEnums.ProductType type, final String demandSourceName) {
        if (this.shouldNotifyDeveloper(type.toString())) {
            this.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    if (SSAEnums.ProductType.RewardedVideo == type) {
                        Log.d((String)IronSourceWebView.this.TAG, (String)("onRVInitFail(message:" + message + ")"));
                        IronSourceWebView.this.mOnRewardedVideoListener.onRVInitFail(message, demandSourceName);
                    } else if (SSAEnums.ProductType.Interstitial == type) {
                        IronSourceWebView.this.mSavedState.setInterstitialInitSuccess(false);
                        if (IronSourceWebView.this.mSavedState.reportInitInterstitial()) {
                            Log.d((String)IronSourceWebView.this.TAG, (String)("onInterstitialInitFail(message:" + message + ")"));
                            IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialInitFailed(message);
                            IronSourceWebView.this.mSavedState.setReportInitInterstitial(false);
                        }
                    } else if (SSAEnums.ProductType.OfferWall == type) {
                        IronSourceWebView.this.mOnOfferWallListener.onOfferwallInitFail(message);
                    } else if (SSAEnums.ProductType.OfferWallCredits == type) {
                        IronSourceWebView.this.mOnOfferWallListener.onGetOWCreditsFailed(message);
                    }
                }
            });
        }
    }

    public void showRewardedVideo(String demandSourceName) {
        HashMap<String, String> rvParamsMap = new HashMap<String, String>();
        if (!TextUtils.isEmpty((CharSequence)demandSourceName)) {
            rvParamsMap.put("demandSourceName", demandSourceName);
        }
        String params = this.flatMapToJsonAsString(rvParamsMap);
        String script = this.generateJSToInject("showRewardedVideo", params, "onShowRewardedVideoSuccess", "onShowRewardedVideoFail");
        this.injectJavascript(script);
    }

    public void assetCached(String file, String path) {
        String params = this.parseToJson("file", file, "path", path, null, null, null, null, null, false);
        String script = this.generateJSToInject("assetCached", params);
        this.injectJavascript(script);
    }

    public void assetCachedFailed(String file, String path, String errorMsg) {
        String params = this.parseToJson("file", file, "path", path, "errMsg", errorMsg, null, null, null, false);
        String script = this.generateJSToInject("assetCachedFailed", params);
        this.injectJavascript(script);
    }

    public void enterBackground() {
        if (this.mControllerState == SSAEnums.ControllerState.Ready) {
            String script = this.generateJSToInject("enterBackground");
            this.injectJavascript(script);
        }
    }

    public void enterForeground() {
        if (this.mControllerState == SSAEnums.ControllerState.Ready) {
            String script = this.generateJSToInject("enterForeground");
            this.injectJavascript(script);
        }
    }

    public void viewableChange(boolean visibility, String webview) {
        String params = this.parseToJson("webview", webview, null, null, null, null, null, null, "isViewable", visibility);
        String script = this.generateJSToInject("viewableChange", params);
        this.injectJavascript(script);
    }

    public void nativeNavigationPressed(String action) {
        String params = this.parseToJson("action", action, null, null, null, null, null, null, null, false);
        String script = this.generateJSToInject("nativeNavigationPressed", params);
        this.injectJavascript(script);
    }

    public void pageFinished() {
        String script = this.generateJSToInject("pageFinished");
        this.injectJavascript(script);
    }

    public void interceptedUrlToStore() {
        String script = this.generateJSToInject("interceptedUrlToStore");
        this.injectJavascript(script);
    }

    private void injectJavascript(String script) {
        String catchClosure = "empty";
        if (this.getDebugMode() == SSAEnums.DebugMode.MODE_0.getValue()) {
            catchClosure = "console.log(\"JS exeption: \" + JSON.stringify(e));";
        } else if (this.getDebugMode() >= SSAEnums.DebugMode.MODE_1.getValue() && this.getDebugMode() <= SSAEnums.DebugMode.MODE_3.getValue()) {
            catchClosure = "console.log(\"JS exeption: \" + JSON.stringify(e));";
        }
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("try{").append(script).append("}catch(e){").append(catchClosure).append("}");
        final String url = "javascript:" + scriptBuilder.toString();
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                Logger.i(IronSourceWebView.this.TAG, url);
                try {
                    if (IronSourceWebView.this.isKitkatAndAbove != null) {
                        if (IronSourceWebView.this.isKitkatAndAbove.booleanValue()) {
                            IronSourceWebView.this.evaluateJavascriptKitKat(scriptBuilder.toString());
                        } else {
                            IronSourceWebView.this.loadUrl(url);
                        }
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        try {
                            IronSourceWebView.this.evaluateJavascriptKitKat(scriptBuilder.toString());
                            IronSourceWebView.this.isKitkatAndAbove = true;
                        }
                        catch (NoSuchMethodError e) {
                            Logger.e(IronSourceWebView.this.TAG, "evaluateJavascrip NoSuchMethodError: SDK version=" + Build.VERSION.SDK_INT + " " + e);
                            IronSourceWebView.this.loadUrl(url);
                            IronSourceWebView.this.isKitkatAndAbove = false;
                        }
                        catch (Throwable e) {
                            Logger.e(IronSourceWebView.this.TAG, "evaluateJavascrip Exception: SDK version=" + Build.VERSION.SDK_INT + " " + e);
                            IronSourceWebView.this.loadUrl(url);
                            IronSourceWebView.this.isKitkatAndAbove = false;
                        }
                    } else {
                        IronSourceWebView.this.loadUrl(url);
                        IronSourceWebView.this.isKitkatAndAbove = false;
                    }
                }
                catch (Throwable t) {
                    Logger.e(IronSourceWebView.this.TAG, "injectJavascript: " + t.toString());
                    new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=injectJavaScript"});
                }
            }
        });
    }

    @SuppressLint(value={"NewApi"})
    private void evaluateJavascriptKitKat(String script) {
        Log.w("AAAAAA", script);
        this.evaluateJavascript(script, null);
    }

    public Context getCurrentActivityContext() {
        MutableContextWrapper mctx = (MutableContextWrapper)this.mCurrentActivityContext;
        return mctx.getBaseContext();
    }

    private String getRequestParameters() {
        Uri downloadUri;
        String osType;
        String serverControllerUrl;
        DeviceProperties properties = DeviceProperties.getInstance(this.getContext());
        StringBuilder builder = new StringBuilder();
        String sdkVer = DeviceProperties.getSupersonicSdkVersion();
        if (!TextUtils.isEmpty((CharSequence)sdkVer)) {
            builder.append("SDKVersion").append("=").append(sdkVer).append("&");
        }
        if (!TextUtils.isEmpty((CharSequence)(osType = properties.getDeviceOsType()))) {
            builder.append("deviceOs").append("=").append(osType);
        }
        if ((downloadUri = Uri.parse((String)(serverControllerUrl = SDKUtils.getControllerUrl()))) != null) {
            String scheme = downloadUri.getScheme() + ":";
            String host = downloadUri.getHost();
            int port = downloadUri.getPort();
            if (port != -1) {
                host = host + ":" + port;
            }
            builder.append("&").append("protocol").append("=").append(scheme);
            builder.append("&").append("domain").append("=").append(host);
            String config = SDKUtils.getControllerConfig();
            if (!TextUtils.isEmpty((CharSequence)config)) {
                builder.append("&").append("controllerConfig").append("=").append(config);
            }
            builder.append("&").append("debug").append("=").append(this.getDebugMode());
        }
        return builder.toString();
    }

    private void closeWebView() {
        if (this.mChangeListener != null) {
            this.mChangeListener.onCloseRequested();
        }
    }

    private void responseBack(String value, boolean result, String errorMessage, String errorCode) {
        SSAObj ssaObj = new SSAObj(value);
        String success = ssaObj.getString(JSON_KEY_SUCCESS);
        String fail = ssaObj.getString(JSON_KEY_FAIL);
        String funToCall = null;
        if (result) {
            if (!TextUtils.isEmpty((CharSequence)success)) {
                funToCall = success;
            }
        } else if (!TextUtils.isEmpty((CharSequence)fail)) {
            funToCall = fail;
        }
        if (!TextUtils.isEmpty((CharSequence)funToCall)) {
            if (!TextUtils.isEmpty((CharSequence)errorMessage)) {
                try {
                    JSONObject jsObj = new JSONObject(value);
                    value = jsObj.put("errMsg", (Object)errorMessage).toString();
                }
                catch (JSONException jsObj) {
                    // empty catch block
                }
            }
            if (!TextUtils.isEmpty((CharSequence)errorCode)) {
                try {
                    JSONObject jsObj = new JSONObject(value);
                    value = jsObj.put("errCode", (Object)errorCode).toString();
                }
                catch (JSONException jsObj) {
                    // empty catch block
                }
            }
            String script = this.generateJSToInject(funToCall, value);
            this.injectJavascript(script);
        }
    }

    private String extractSuccessFunctionToCall(String jsonStr) {
        SSAObj ssaObj = new SSAObj(jsonStr);
        String funToCall = ssaObj.getString(JSON_KEY_SUCCESS);
        return funToCall;
    }

    private String extractFailFunctionToCall(String jsonStr) {
        SSAObj ssaObj = new SSAObj(jsonStr);
        String funToCall = ssaObj.getString(JSON_KEY_FAIL);
        return funToCall;
    }

    private String parseToJson(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4, String key5, boolean value5) {
        JSONObject jsObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty((CharSequence)key1) && !TextUtils.isEmpty((CharSequence)value1)) {
                jsObj.put(key1, (Object)SDKUtils.encodeString(value1));
            }
            if (!TextUtils.isEmpty((CharSequence)key2) && !TextUtils.isEmpty((CharSequence)value2)) {
                jsObj.put(key2, (Object)SDKUtils.encodeString(value2));
            }
            if (!TextUtils.isEmpty((CharSequence)key3) && !TextUtils.isEmpty((CharSequence)value3)) {
                jsObj.put(key3, (Object)SDKUtils.encodeString(value3));
            }
            if (!TextUtils.isEmpty((CharSequence)key4) && !TextUtils.isEmpty((CharSequence)value4)) {
                jsObj.put(key4, (Object)SDKUtils.encodeString(value4));
            }
            if (!TextUtils.isEmpty((CharSequence)key5)) {
                jsObj.put(key5, value5);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=" + e.getStackTrace()[0].getMethodName()});
        }
        return jsObj.toString();
    }

    private String mapToJson(Map<String, String> map) {
        JSONObject jsObj = new JSONObject();
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                try {
                    jsObj.put(key, (Object)SDKUtils.encodeString(value));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsObj.toString();
    }

    private Object[] getDeviceParams(Context context) {
        boolean fail = false;
        DeviceProperties deviceProperties = DeviceProperties.getInstance(context);
        JSONObject jsObj = new JSONObject();
        try {
            String connectionType;
            String scaleStr;
            boolean isRoot;
            float deviceScale;
            String rootStr;
            String deviceOSType;
            String deviceModel;
            jsObj.put("appOrientation", (Object)SDKUtils.translateRequestedOrientation(DeviceStatus.getActivityRequestedOrientation(this.getCurrentActivityContext())));
            String deviceOem = deviceProperties.getDeviceOem();
            if (deviceOem != null) {
                jsObj.put(SDKUtils.encodeString("deviceOEM"), (Object)SDKUtils.encodeString(deviceOem));
            }
            if ((deviceModel = deviceProperties.getDeviceModel()) != null) {
                jsObj.put(SDKUtils.encodeString("deviceModel"), (Object)SDKUtils.encodeString(deviceModel));
            } else {
                fail = true;
            }
            SDKUtils.loadGoogleAdvertiserInfo(context);
            String advertiserId = SDKUtils.getAdvertiserId();
            Boolean isLAT = SDKUtils.isLimitAdTrackingEnabled();
            if (!TextUtils.isEmpty((CharSequence)advertiserId)) {
                Logger.i(this.TAG, "add AID and LAT");
                jsObj.put("isLimitAdTrackingEnabled", (Object)isLAT);
                StringBuilder aid = new StringBuilder().append("deviceIds").append("[").append("AID").append("]");
                jsObj.put(aid.toString(), (Object)SDKUtils.encodeString(advertiserId));
            }
            if ((deviceOSType = deviceProperties.getDeviceOsType()) != null) {
                jsObj.put(SDKUtils.encodeString("deviceOs"), (Object)SDKUtils.encodeString(deviceOSType));
            } else {
                fail = true;
            }
            String deviceOSVersion = Integer.toString(deviceProperties.getDeviceOsVersion());
            if (deviceOSVersion != null) {
                jsObj.put(SDKUtils.encodeString("deviceOSVersion"), (Object)deviceOSVersion);
            } else {
                fail = true;
            }
            String ssaSDKVersion = DeviceProperties.getSupersonicSdkVersion();
            if (ssaSDKVersion != null) {
                jsObj.put(SDKUtils.encodeString("SDKVersion"), (Object)SDKUtils.encodeString(ssaSDKVersion));
            }
            if (deviceProperties.getDeviceCarrier() != null && deviceProperties.getDeviceCarrier().length() > 0) {
                jsObj.put(SDKUtils.encodeString("mobileCarrier"), (Object)SDKUtils.encodeString(deviceProperties.getDeviceCarrier()));
            }
            if (!TextUtils.isEmpty((CharSequence)(connectionType = ConnectivityService.getConnectionType(context)))) {
                jsObj.put(SDKUtils.encodeString("connectionType"), (Object)SDKUtils.encodeString(connectionType));
            } else {
                fail = true;
            }
            String deviceLanguage = context.getResources().getConfiguration().locale.getLanguage();
            if (!TextUtils.isEmpty((CharSequence)deviceLanguage)) {
                jsObj.put(SDKUtils.encodeString("deviceLanguage"), (Object)SDKUtils.encodeString(deviceLanguage.toUpperCase()));
            }
            if (SDKUtils.isExternalStorageAvailable()) {
                long freeDiskSize = DeviceStatus.getAvailableMemorySizeInMegaBytes(this.mCacheDirectory);
                jsObj.put(SDKUtils.encodeString("diskFreeSize"), (Object)SDKUtils.encodeString(String.valueOf(freeDiskSize)));
            } else {
                fail = true;
            }
            int deviceWidth = DeviceStatus.getDeviceWidth();
            String width = String.valueOf(deviceWidth);
            if (!TextUtils.isEmpty((CharSequence)width)) {
                StringBuilder key = new StringBuilder();
                key.append(SDKUtils.encodeString("deviceScreenSize")).append("[").append(SDKUtils.encodeString("width")).append("]");
                jsObj.put(key.toString(), (Object)SDKUtils.encodeString(width));
            } else {
                fail = true;
            }
            int deviceHeigh = DeviceStatus.getDeviceHeight();
            String height = String.valueOf(deviceHeigh);
            StringBuilder key = new StringBuilder();
            key.append(SDKUtils.encodeString("deviceScreenSize")).append("[").append(SDKUtils.encodeString("height")).append("]");
            jsObj.put(key.toString(), (Object)SDKUtils.encodeString(height));
            String packageName = ApplicationContext.getPackageName(this.getContext());
            if (!TextUtils.isEmpty((CharSequence)packageName)) {
                jsObj.put(SDKUtils.encodeString("bundleId"), (Object)SDKUtils.encodeString(packageName));
            }
            if (!TextUtils.isEmpty((CharSequence)(scaleStr = String.valueOf(deviceScale = DeviceStatus.getDeviceDensity())))) {
                jsObj.put(SDKUtils.encodeString("deviceScreenScale"), (Object)SDKUtils.encodeString(scaleStr));
            }
            if (!TextUtils.isEmpty((CharSequence)(rootStr = String.valueOf(isRoot = DeviceStatus.isRootedDevice())))) {
                jsObj.put(SDKUtils.encodeString("unLocked"), (Object)SDKUtils.encodeString(rootStr));
            }
            float deviceVolume = DeviceProperties.getInstance(context).getDeviceVolume(context);
            if (!TextUtils.isEmpty((CharSequence)rootStr)) {
                jsObj.put(SDKUtils.encodeString("deviceVolume"), (double)deviceVolume);
            }
            Context ctx = this.getCurrentActivityContext();
            if (Build.VERSION.SDK_INT >= 19 && ctx instanceof Activity) {
                jsObj.put(SDKUtils.encodeString("immersiveMode"), DeviceStatus.isImmersiveSupported((Activity)ctx));
            }
            jsObj.put(SDKUtils.encodeString("batteryLevel"), DeviceStatus.getBatteryLevel(ctx));
            jsObj.put(SDKUtils.encodeString("mcc"), ConnectivityService.getNetworkMCC(ctx));
            jsObj.put(SDKUtils.encodeString("mnc"), ConnectivityService.getNetworkMNC(ctx));
            jsObj.put(SDKUtils.encodeString("phoneType"), ConnectivityService.getPhoneType(ctx));
            jsObj.put(SDKUtils.encodeString("simOperator"), (Object)SDKUtils.encodeString(ConnectivityService.getSimOperator(ctx)));
            jsObj.put(SDKUtils.encodeString("lastUpdateTime"), ApplicationContext.getLastUpdateTime(ctx));
            jsObj.put(SDKUtils.encodeString("firstInstallTime"), ApplicationContext.getFirstInstallTime(ctx));
            jsObj.put(SDKUtils.encodeString("appVersion"), (Object)SDKUtils.encodeString(ApplicationContext.getApplicationVersionName(ctx)));
        }
        catch (JSONException e) {
            e.printStackTrace();
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=" + e.getStackTrace()[0].getMethodName()});
        }
        Object[] result = new Object[]{jsObj.toString(), fail};
        return result;
    }

    private Object[] getApplicationParams(String productType, String demandSourceName) {
        boolean fail = false;
        JSONObject jsObj = new JSONObject();
        String appKey = "";
        String userId = "";
        Map<String, String> productExtraParams = null;
        if (!TextUtils.isEmpty((CharSequence)productType)) {
            if (productType.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
                appKey = this.mRVAppKey;
                userId = this.mRVUserId;
                DemandSource demandSource = IronSourceAdsPublisherAgent.getInstance((Activity)this.getCurrentActivityContext()).getDemandSourceByName(demandSourceName);
                if (demandSource != null) {
                    productExtraParams = demandSource.getExtraParams();
                }
            } else if (productType.equalsIgnoreCase(SSAEnums.ProductType.Interstitial.toString())) {
                appKey = this.mISAppKey;
                userId = this.mISUserId;
                productExtraParams = this.mISExtraParameters;
            } else if (productType.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString())) {
                appKey = this.mOWAppKey;
                userId = this.mOWUserId;
                productExtraParams = this.mOWExtraParameters;
            }
            try {
                jsObj.put("productType", (Object)productType);
            }
            catch (JSONException e) {
                e.printStackTrace();
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=noProductType"});
            }
        } else {
            fail = true;
        }
        if (!TextUtils.isEmpty((CharSequence)userId)) {
            try {
                jsObj.put(SDKUtils.encodeString("applicationUserId"), (Object)SDKUtils.encodeString(userId));
            }
            catch (JSONException e) {
                e.printStackTrace();
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=encodeAppUserId"});
            }
        } else {
            fail = true;
        }
        if (!TextUtils.isEmpty((CharSequence)appKey)) {
            try {
                jsObj.put(SDKUtils.encodeString("applicationKey"), (Object)SDKUtils.encodeString(appKey));
            }
            catch (JSONException e) {
                e.printStackTrace();
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=encodeAppKey"});
            }
        } else {
            fail = true;
        }
        if (productExtraParams != null && !productExtraParams.isEmpty()) {
            for (Map.Entry<String, String> entry : productExtraParams.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("sdkWebViewCache")) {
                    this.setWebviewCache(entry.getValue());
                }
                try {
                    jsObj.put(SDKUtils.encodeString(entry.getKey()), (Object)SDKUtils.encodeString(entry.getValue()));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=extraParametersToJson"});
                }
            }
        }
        Object[] result = new Object[]{jsObj.toString(), fail};
        return result;
    }

    private Object[] getAppsStatus(String appIds, String requestId) {
        boolean fail = false;
        JSONObject result = new JSONObject();
        try {
            if (!TextUtils.isEmpty((CharSequence)appIds) && !appIds.equalsIgnoreCase("null")) {
                if (!TextUtils.isEmpty((CharSequence)requestId) && !requestId.equalsIgnoreCase("null")) {
                    Context ctx = this.getContext();
                    List<ApplicationInfo> packages = DeviceStatus.getInstalledApplications(ctx);
                    JSONArray appIdsArray = new JSONArray(appIds);
                    JSONObject bundleIds = new JSONObject();
                    for (int i = 0; i < appIdsArray.length(); ++i) {
                        String appId = appIdsArray.getString(i).trim();
                        if (TextUtils.isEmpty((CharSequence)appId)) continue;
                        JSONObject isInstalled = new JSONObject();
                        boolean found = false;
                        for (ApplicationInfo packageInfo : packages) {
                            if (!appId.equalsIgnoreCase(packageInfo.packageName)) continue;
                            isInstalled.put(IS_INSTALLED, true);
                            bundleIds.put(appId, (Object)isInstalled);
                            found = true;
                            break;
                        }
                        if (found) continue;
                        isInstalled.put(IS_INSTALLED, false);
                        bundleIds.put(appId, (Object)isInstalled);
                    }
                    result.put(RESULT, (Object)bundleIds);
                    result.put(REQUEST_ID, (Object)requestId);
                } else {
                    fail = true;
                    result.put("error", (Object)"requestId is null or empty");
                }
            } else {
                fail = true;
                result.put("error", (Object)"appIds is null or empty");
            }
        }
        catch (Exception e) {
            fail = true;
        }
        Object[] finalResult = new Object[]{result.toString(), fail};
        return finalResult;
    }

    @Override
    public void onFileDownloadSuccess(SSAFile file) {
        if (file.getFile().contains("mobileController.html")) {
            this.load(1);
        } else {
            this.assetCached(file.getFile(), file.getPath());
        }
    }

    @Override
    public void onFileDownloadFail(SSAFile file) {
        if (file.getFile().contains("mobileController.html")) {
            this.mGlobalControllerTimer.cancel();
            Collection<DemandSource> demandSources = IronSourceAdsPublisherAgent.getInstance((Activity)this.getCurrentActivityContext()).getDemandSources();
            for (DemandSource demandSource : demandSources) {
                if (demandSource.getDemandSourceInitState() != 1) continue;
                this.sendProductErrorMessage(SSAEnums.ProductType.RewardedVideo, demandSource.getDemandSourceName());
            }
            if (this.mISmiss) {
                this.sendProductErrorMessage(SSAEnums.ProductType.Interstitial, null);
            }
            if (this.mOWmiss) {
                this.sendProductErrorMessage(SSAEnums.ProductType.OfferWall, null);
            }
            if (this.mOWCreditsMiss) {
                this.sendProductErrorMessage(SSAEnums.ProductType.OfferWallCredits, null);
            }
        } else {
            this.assetCachedFailed(file.getFile(), file.getPath(), file.getErrMsg());
        }
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Logger.i(this.TAG, url + " " + mimetype);
    }

    private void toastingErrMsg(final String methodName, String value) {
        SSAObj ssaObj = new SSAObj(value);
        final String message = ssaObj.getString("errMsg");
        if (!TextUtils.isEmpty((CharSequence)message)) {
            this.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    if (IronSourceWebView.this.getDebugMode() == SSAEnums.DebugMode.MODE_3.getValue()) {
                        Toast.makeText((Context)IronSourceWebView.this.getCurrentActivityContext(), (CharSequence)(methodName + " : " + message), (int)1).show();
                    }
                }
            });
        }
    }

    public void setControllerKeyPressed(String value) {
        this.mControllerKeyPressed = value;
    }

    public String getControllerKeyPressed() {
        String keyPressed = this.mControllerKeyPressed;
        this.setControllerKeyPressed("interrupt");
        return keyPressed;
    }

    public void runGenericFunction(String method, Map<String, String> keyValPairs, OnGenericFunctionListener listener) {
        this.mOnGenericFunctionListener = listener;
        if ("initRewardedVideo".equalsIgnoreCase(method)) {
            String demandSourceName = keyValPairs.get("demandSourceName");
            this.initRewardedVideo(keyValPairs.get("applicationUserId"), keyValPairs.get("applicationKey"), demandSourceName, this.mOnRewardedVideoListener);
        } else if ("showRewardedVideo".equalsIgnoreCase(method)) {
            this.showRewardedVideo(keyValPairs.get("demandSourceName"));
        } else {
            String script = this.generateJSToInject(method, this.mapToJson(keyValPairs), "onGenericFunctionSuccess", "onGenericFunctionFail");
            this.injectJavascript(script);
        }
    }

    public void deviceStatusChanged(String networkType) {
        String params = this.parseToJson("connectionType", networkType, null, null, null, null, null, null, null, false);
        String script = this.generateJSToInject("deviceStatusChanged", params);
        this.injectJavascript(script);
    }

    public void engageEnd(String action) {
        if (action.equals("forceClose")) {
            this.closeWebView();
        }
        String params = this.parseToJson("action", action, null, null, null, null, null, null, null, false);
        String script = this.generateJSToInject("engageEnd", params);
        this.injectJavascript(script);
    }

    public void registerConnectionReceiver(Context context) {
        context.registerReceiver(this.mConnectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void unregisterConnectionReceiver(Context context) {
        try {
            context.unregisterReceiver(this.mConnectionReceiver);
        }
        catch (IllegalArgumentException var2_2) {
        }
        catch (Exception e1) {
            Log.e((String)this.TAG, (String)("unregisterConnectionReceiver - " + e1));
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=" + e1.getStackTrace()[0].getMethodName()});
        }
    }

    public void pause() {
        if (Build.VERSION.SDK_INT > 10) {
            try {
                this.onPause();
            }
            catch (Throwable e) {
                Logger.i(this.TAG, "WebViewController: pause() - " + e);
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=webviewPause"});
            }
        }
    }

    public void resume() {
        if (Build.VERSION.SDK_INT > 10) {
            try {
                this.onResume();
            }
            catch (Throwable e) {
                Logger.i(this.TAG, "WebViewController: onResume() - " + e);
                new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=webviewResume"});
            }
        }
    }

    public void setOnWebViewControllerChangeListener(OnWebViewChangeListener listener) {
        this.mChangeListener = listener;
    }

    public FrameLayout getLayout() {
        return this.mControllerLayout;
    }

    public boolean inCustomView() {
        return this.mCustomView != null;
    }

    public void hideCustomView() {
        this.mWebChromeClient.onHideCustomView();
    }

    private void setWebviewCache(String value) {
        if (value.equalsIgnoreCase("0")) {
            this.getSettings().setCacheMode(2);
        } else {
            this.getSettings().setCacheMode(-1);
        }
    }

    public boolean handleSearchKeysURLs(String url) throws Exception {
        List<String> searchKeys = IronSourceSharedPrefHelper.getSupersonicPrefHelper().getSearchKeys();
        try {
            if (searchKeys != null && !searchKeys.isEmpty()) {
                for (String key : searchKeys) {
                    if (!url.contains(key)) continue;
                    UrlHandler.openUrl(this.getCurrentActivityContext(), url);
                    return true;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setState(State state) {
        this.mState = state;
    }

    public State getState() {
        return this.mState;
    }

    private void sendProductErrorMessage(SSAEnums.ProductType type, String demnadSourceName) {
        String action = "";
        switch (type) {
            case RewardedVideo: {
                action = "Init RV";
                break;
            }
            case Interstitial: {
                action = "Init IS";
                break;
            }
            case OfferWall: {
                action = "Init OW";
                break;
            }
            case OfferWallCredits: {
                action = "Show OW Credits";
            }
        }
        this.triggerOnControllerInitProductFail(SDKUtils.createErrorMessage(action, "Initiating Controller"), type, demnadSourceName);
    }

    public void destroy() {
        super.destroy();
        if (this.downloadManager != null) {
            this.downloadManager.release();
        }
        if (this.mConnectionReceiver != null) {
            this.mConnectionReceiver = null;
        }
        this.mUiHandler = null;
        this.mCurrentActivityContext = null;
    }

    private String generateJSToInject(String funToCall) {
        StringBuilder script = new StringBuilder();
        script.append("SSA_CORE.SDKController.runFunction('").append(funToCall).append("');");
        return script.toString();
    }

    private String generateJSToInject(String funToCall, String parameters) {
        StringBuilder script = new StringBuilder();
        script.append("SSA_CORE.SDKController.runFunction('").append(funToCall).append("?parameters=").append(parameters).append("');");
        return script.toString();
    }

    private String generateJSToInject(String funToCall, String successFunc, String failFunc) {
        StringBuilder script = new StringBuilder();
        script.append("SSA_CORE.SDKController.runFunction('").append(funToCall).append("','").append(successFunc).append("','").append(failFunc).append("');");
        return script.toString();
    }

    private String generateJSToInject(String funToCall, String parameters, String successFunc, String failFunc) {
        StringBuilder script = new StringBuilder();
        script.append("SSA_CORE.SDKController.runFunction('").append(funToCall).append("?parameters=").append(parameters).append("','").append(successFunc).append("','").append(failFunc).append("');");
        return script.toString();
    }

    public AdUnitsState getSavedState() {
        return this.mSavedState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restoreState(AdUnitsState state) {
        Object object = this.mSavedStateLocker;
        synchronized (object) {
            if (state.shouldRestore() && this.mControllerState.equals((Object)SSAEnums.ControllerState.Ready)) {
                String appKey;
                String userId;
                Log.d((String)this.TAG, (String)("restoreState(state:" + state + ")"));
                int lastAd = state.getDisplayedProduct();
                if (lastAd != -1) {
                    if (lastAd == SSAEnums.ProductType.RewardedVideo.ordinal()) {
                        Log.d((String)this.TAG, (String)"onRVAdClosed()");
                        String demandSourceName = state.getDisplayedDemandSourceName();
                        if (this.mOnRewardedVideoListener != null && !TextUtils.isEmpty((CharSequence)demandSourceName)) {
                            this.mOnRewardedVideoListener.onRVAdClosed(demandSourceName);
                        }
                    } else if (lastAd == SSAEnums.ProductType.Interstitial.ordinal()) {
                        Log.d((String)this.TAG, (String)"onInterstitialAdClosed()");
                        if (this.mOnInitInterstitialListener != null) {
                            this.mOnInitInterstitialListener.onInterstitialClose();
                        }
                    } else if (lastAd == SSAEnums.ProductType.OfferWall.ordinal()) {
                        Log.d((String)this.TAG, (String)"onOWAdClosed()");
                        if (this.mOnOfferWallListener != null) {
                            this.mOnOfferWallListener.onOWAdClosed();
                        }
                    }
                    state.adOpened(-1);
                    state.setDisplayedDemandSourceName(null);
                } else {
                    Log.d((String)this.TAG, (String)"No ad was opened");
                }
                if (state.isInterstitialInitSuccess()) {
                    Log.d((String)this.TAG, (String)"onInterstitialAvailability(false)");
                    if (this.mOnInitInterstitialListener != null) {
                        // empty if block
                    }
                    appKey = state.getInterstitialAppKey();
                    userId = state.getInterstitialUserId();
                    Map<String, String> extraParams = state.getInterstitialExtraParams();
                    Log.d((String)this.TAG, (String)("initInterstitial(appKey:" + appKey + ", userId:" + userId + ", extraParam:" + extraParams + ")"));
                    this.initInterstitial(appKey, userId, extraParams, this.mOnInitInterstitialListener);
                }
                appKey = state.getRVAppKey();
                userId = state.getRVUserId();
                Collection<DemandSource> demandSources = IronSourceAdsPublisherAgent.getInstance((Activity)this.getCurrentActivityContext()).getDemandSources();
                for (DemandSource demandSource : demandSources) {
                    if (demandSource.getDemandSourceInitState() != 2) continue;
                    String demandSourceName = demandSource.getDemandSourceName();
                    Log.d((String)this.TAG, (String)"onRVNoMoreOffers()");
                    this.mOnRewardedVideoListener.onRVNoMoreOffers(demandSourceName);
                    this.initRewardedVideo(appKey, userId, demandSourceName, this.mOnRewardedVideoListener);
                }
                state.setShouldRestore(false);
            }
            this.mSavedState = state;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (!this.mChangeListener.onBackButtonPressed()) {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void runOnUiThread(Runnable task) {
        this.mUiHandler.post(task);
    }

    public static enum State {
        Display,
        Gone;
        

        private State() {
        }
    }

    public class JSInterface {
        volatile int udiaResults;

        public JSInterface(Context context) {
            this.udiaResults = 0;
        }

        @JavascriptInterface
        public void initController(String value) {
            Logger.i(IronSourceWebView.this.TAG, "initController(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (ssaObj.containsKey("stage")) {
                String stage = ssaObj.getString("stage");
                if ("ready".equalsIgnoreCase(stage)) {
                    IronSourceWebView.this.mControllerState = SSAEnums.ControllerState.Ready;
                    IronSourceWebView.this.mGlobalControllerTimer.cancel();
                    IronSourceWebView.this.mLoadControllerTimer.cancel();
                    Collection<DemandSource> demandSources = IronSourceAdsPublisherAgent.getInstance((Activity)IronSourceWebView.this.getCurrentActivityContext()).getDemandSources();
                    for (DemandSource demandSource : demandSources) {
                        if (demandSource.getDemandSourceInitState() != 1) continue;
                        IronSourceWebView.this.initRewardedVideo(IronSourceWebView.this.mRVAppKey, IronSourceWebView.this.mRVUserId, demandSource.getDemandSourceName(), IronSourceWebView.this.mOnRewardedVideoListener);
                    }
                    if (IronSourceWebView.this.mISmiss) {
                        IronSourceWebView.this.initInterstitial(IronSourceWebView.this.mISAppKey, IronSourceWebView.this.mISUserId, IronSourceWebView.this.mISExtraParameters, IronSourceWebView.this.mOnInitInterstitialListener);
                    }
                    if (IronSourceWebView.this.mOWmiss) {
                        IronSourceWebView.this.initOfferWall(IronSourceWebView.this.mOWAppKey, IronSourceWebView.this.mOWUserId, IronSourceWebView.this.mOWExtraParameters, IronSourceWebView.this.mOnOfferWallListener);
                    }
                    if (IronSourceWebView.this.mOWCreditsMiss) {
                        IronSourceWebView.this.getOfferWallCredits(IronSourceWebView.this.mOWCreditsAppKey, IronSourceWebView.this.mOWCreditsUserId, IronSourceWebView.this.mOnOfferWallListener);
                    }
                    IronSourceWebView.this.restoreState(IronSourceWebView.this.mSavedState);
                } else if ("loaded".equalsIgnoreCase(stage)) {
                    IronSourceWebView.this.mControllerState = SSAEnums.ControllerState.Loaded;
                } else if ("failed".equalsIgnoreCase(stage)) {
                    IronSourceWebView.this.mControllerState = SSAEnums.ControllerState.Failed;
                    Collection<DemandSource> demandSources = IronSourceAdsPublisherAgent.getInstance((Activity)IronSourceWebView.this.getCurrentActivityContext()).getDemandSources();
                    for (DemandSource demandSource : demandSources) {
                        if (demandSource.getDemandSourceInitState() != 1) continue;
                        IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.RewardedVideo, demandSource.getDemandSourceName());
                    }
                    if (IronSourceWebView.this.mISmiss) {
                        IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.Interstitial, null);
                    }
                    if (IronSourceWebView.this.mOWmiss) {
                        IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.OfferWall, null);
                    }
                    if (IronSourceWebView.this.mOWCreditsMiss) {
                        IronSourceWebView.this.sendProductErrorMessage(SSAEnums.ProductType.OfferWallCredits, null);
                    }
                } else {
                    Logger.i(IronSourceWebView.this.TAG, "No STAGE mentioned! Should not get here!");
                }
            }
        }

        @JavascriptInterface
        public void alert(String message) {
        }

        @JavascriptInterface
        public void getDeviceStatus(String value) {
            Logger.i(IronSourceWebView.this.TAG, "getDeviceStatus(" + value + ")");
            String successFunToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            String failFunToCall = IronSourceWebView.this.extractFailFunctionToCall(value);
            Object[] resultArr = new Object[2];
            resultArr = IronSourceWebView.this.getDeviceParams(IronSourceWebView.this.getContext());
            String params = (String)resultArr[0];
            boolean failed = (Boolean)resultArr[1];
            String funToCall = null;
            if (failed) {
                if (!TextUtils.isEmpty((CharSequence)failFunToCall)) {
                    funToCall = failFunToCall;
                }
            } else if (!TextUtils.isEmpty((CharSequence)successFunToCall)) {
                funToCall = successFunToCall;
            }
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String script = IronSourceWebView.this.generateJSToInject(funToCall, params, "onGetDeviceStatusSuccess", "onGetDeviceStatusFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void getApplicationInfo(String value) {
            Logger.i(IronSourceWebView.this.TAG, "getApplicationInfo(" + value + ")");
            String successFunToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            String failFunToCall = IronSourceWebView.this.extractFailFunctionToCall(value);
            SSAObj ssaObj = new SSAObj(value);
            String product = ssaObj.getString("productType");
            String demandSourceName = ssaObj.getString("demandSourceName");
            String funToCall = null;
            Object[] resultArr = new Object[2];
            resultArr = IronSourceWebView.this.getApplicationParams(product, demandSourceName);
            String params = (String)resultArr[0];
            boolean failed = (Boolean)resultArr[1];
            if (failed) {
                if (!TextUtils.isEmpty((CharSequence)failFunToCall)) {
                    funToCall = failFunToCall;
                }
            } else if (!TextUtils.isEmpty((CharSequence)successFunToCall)) {
                funToCall = successFunToCall;
            }
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String script = IronSourceWebView.this.generateJSToInject(funToCall, params, "onGetApplicationInfoSuccess", "onGetApplicationInfoFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void checkInstalledApps(String value) {
            Logger.i(IronSourceWebView.this.TAG, "checkInstalledApps(" + value + ")");
            String successFunToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            String failFunToCall = IronSourceWebView.this.extractFailFunctionToCall(value);
            String funToCall = null;
            SSAObj ssaObj = new SSAObj(value);
            String appIdsString = ssaObj.getString(IronSourceWebView.APP_IDS);
            String requestIdString = ssaObj.getString(IronSourceWebView.REQUEST_ID);
            Object[] resultArr = IronSourceWebView.this.getAppsStatus(appIdsString, requestIdString);
            String params = (String)resultArr[0];
            boolean failed = (Boolean)resultArr[1];
            if (failed) {
                if (!TextUtils.isEmpty((CharSequence)failFunToCall)) {
                    funToCall = failFunToCall;
                }
            } else if (!TextUtils.isEmpty((CharSequence)successFunToCall)) {
                funToCall = successFunToCall;
            }
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String script = IronSourceWebView.this.generateJSToInject(funToCall, params, "onCheckInstalledAppsSuccess", "onCheckInstalledAppsFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void saveFile(String value) {
            String lastUpdateTimeStr;
            Logger.i(IronSourceWebView.this.TAG, "saveFile(" + value + ")");
            SSAFile ssaFile = new SSAFile(value);
            if (DeviceStatus.getAvailableMemorySizeInMegaBytes(IronSourceWebView.this.mCacheDirectory) <= 0) {
                IronSourceWebView.this.responseBack(value, false, "no_disk_space", null);
                return;
            }
            if (!SDKUtils.isExternalStorageAvailable()) {
                IronSourceWebView.this.responseBack(value, false, "sotrage_unavailable", null);
                return;
            }
            if (IronSourceStorageUtils.isFileCached(IronSourceWebView.this.mCacheDirectory, ssaFile)) {
                IronSourceWebView.this.responseBack(value, false, "file_already_exist", null);
                return;
            }
            if (!ConnectivityService.isConnected(IronSourceWebView.this.getContext())) {
                IronSourceWebView.this.responseBack(value, false, "no_network_connection", null);
                return;
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            String lastUpdateTimeObj = ssaFile.getLastUpdateTime();
            if (lastUpdateTimeObj != null && !TextUtils.isEmpty((CharSequence)(lastUpdateTimeStr = String.valueOf(lastUpdateTimeObj)))) {
                String folder;
                String path = ssaFile.getPath();
                if (path.contains("/")) {
                    String[] splitArr = ssaFile.getPath().split("/");
                    folder = splitArr[splitArr.length - 1];
                } else {
                    folder = path;
                }
                IronSourceSharedPrefHelper.getSupersonicPrefHelper().setCampaignLastUpdate(folder, lastUpdateTimeStr);
            }
            IronSourceWebView.this.downloadManager.downloadFile(ssaFile);
        }

        @JavascriptInterface
        public void adUnitsReady(String value) {
            Logger.i(IronSourceWebView.this.TAG, "adUnitsReady(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String demandSourceName = ssaObj.getString("demandSourceName");
            final AdUnitsReady adUnitsReady = new AdUnitsReady(value);
            if (!adUnitsReady.isNumOfAdUnitsExist()) {
                IronSourceWebView.this.responseBack(value, false, "Num Of Ad Units Do Not Exist", null);
                return;
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            final String product = adUnitsReady.getProductType();
            if (IronSourceWebView.this.shouldNotifyDeveloper(product)) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        int adUnits = Integer.parseInt(adUnitsReady.getNumOfAdUnits());
                        boolean fireSuccess = adUnits > 0;
                        if (product.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
                            if (fireSuccess) {
                                Log.d((String)IronSourceWebView.this.TAG, (String)"onRVInitSuccess()");
                                IronSourceWebView.this.mOnRewardedVideoListener.onRVInitSuccess(adUnitsReady, demandSourceName);
                            } else {
                                IronSourceWebView.this.mOnRewardedVideoListener.onRVNoMoreOffers(demandSourceName);
                            }
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void deleteFolder(String value) {
            Logger.i(IronSourceWebView.this.TAG, "deleteFolder(" + value + ")");
            SSAFile file = new SSAFile(value);
            if (!IronSourceStorageUtils.isPathExist(IronSourceWebView.this.mCacheDirectory, file.getPath())) {
                IronSourceWebView.this.responseBack(value, false, "Folder not exist", "1");
            } else {
                boolean result = IronSourceStorageUtils.deleteFolder(IronSourceWebView.this.mCacheDirectory, file.getPath());
                IronSourceWebView.this.responseBack(value, result, null, null);
            }
        }

        @JavascriptInterface
        public void deleteFile(String value) {
            Logger.i(IronSourceWebView.this.TAG, "deleteFile(" + value + ")");
            SSAFile file = new SSAFile(value);
            if (!IronSourceStorageUtils.isPathExist(IronSourceWebView.this.mCacheDirectory, file.getPath())) {
                IronSourceWebView.this.responseBack(value, false, "File not exist", "1");
            } else {
                boolean result = IronSourceStorageUtils.deleteFile(IronSourceWebView.this.mCacheDirectory, file.getPath(), file.getFile());
                IronSourceWebView.this.responseBack(value, result, null, null);
            }
        }

        @JavascriptInterface
        public void displayWebView(String value) {
            Logger.i(IronSourceWebView.this.TAG, "displayWebView(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            SSAObj ssaObj = new SSAObj(value);
            boolean display = (Boolean)ssaObj.get("display");
            String productType = ssaObj.getString("productType");
            boolean isStandaloneView = ssaObj.getBoolean("standaloneView");
            String demandSourceName = ssaObj.getString("demandSourceName");
            boolean isRewardedVideo = false;
            if (display) {
                IronSourceWebView.this.mIsImmersive = ssaObj.getBoolean("immersive");
                IronSourceWebView.this.mIsActivityThemeTranslucent = ssaObj.getBoolean("activityThemeTranslucent");
                if (IronSourceWebView.this.getState() != State.Display) {
                    IronSourceWebView.this.setState(State.Display);
                    Logger.i(IronSourceWebView.this.TAG, "State: " + (Object)((Object)IronSourceWebView.this.mState));
                    Context context = IronSourceWebView.this.getCurrentActivityContext();
                    String orientation = IronSourceWebView.this.getOrientationState();
                    int rotation = DeviceStatus.getApplicationRotation(context);
                    if (isStandaloneView) {
                        ControllerView controllerView = new ControllerView(context);
                        controllerView.addView((View)IronSourceWebView.this.mControllerLayout);
                        controllerView.showInterstitial(IronSourceWebView.this);
                    } else {
                        Intent intent = IronSourceWebView.this.mIsActivityThemeTranslucent ? new Intent(context, InterstitialActivity.class) : new Intent(context, VideoActivity.class);
                        if (SSAEnums.ProductType.RewardedVideo.toString().equalsIgnoreCase(productType)) {
                            if ("application".equals(orientation)) {
                                orientation = SDKUtils.translateRequestedOrientation(DeviceStatus.getActivityRequestedOrientation(IronSourceWebView.this.getCurrentActivityContext()));
                            }
                            isRewardedVideo = true;
                            intent.putExtra("productType", SSAEnums.ProductType.RewardedVideo.toString());
                            IronSourceWebView.this.mSavedState.adOpened(SSAEnums.ProductType.RewardedVideo.ordinal());
                            IronSourceWebView.this.mSavedState.setDisplayedDemandSourceName(demandSourceName);
                        } else if (SSAEnums.ProductType.OfferWall.toString().equalsIgnoreCase(productType)) {
                            intent.putExtra("productType", SSAEnums.ProductType.OfferWall.toString());
                            IronSourceWebView.this.mSavedState.adOpened(SSAEnums.ProductType.OfferWall.ordinal());
                        }
                        if (isRewardedVideo && IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.RewardedVideo.toString())) {
                            IronSourceWebView.this.mOnRewardedVideoListener.onRVAdOpened(demandSourceName);
                        }
                        intent.setFlags(536870912);
                        intent.putExtra("immersive", IronSourceWebView.this.mIsImmersive);
                        intent.putExtra("orientation_set_flag", orientation);
                        intent.putExtra("rotation_set_flag", rotation);
                        context.startActivity(intent);
                    }
                } else {
                    Logger.i(IronSourceWebView.this.TAG, "State: " + (Object)((Object)IronSourceWebView.this.mState));
                }
            } else {
                IronSourceWebView.this.setState(State.Gone);
                IronSourceWebView.this.closeWebView();
            }
        }

        @JavascriptInterface
        public void getOrientation(String value) {
            String funToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            String params = SDKUtils.getOrientation(IronSourceWebView.this.getCurrentActivityContext()).toString();
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String script = IronSourceWebView.this.generateJSToInject(funToCall, params, "onGetOrientationSuccess", "onGetOrientationFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void setOrientation(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setOrientation(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            String orientation = ssaObj.getString("orientation");
            IronSourceWebView.this.setOrientationState(orientation);
            int rotation = DeviceStatus.getApplicationRotation(IronSourceWebView.this.getCurrentActivityContext());
            if (IronSourceWebView.this.mChangeListener != null) {
                IronSourceWebView.this.mChangeListener.onOrientationChanged(orientation, rotation);
            }
        }

        @JavascriptInterface
        public void getCachedFilesMap(String value) {
            Logger.i(IronSourceWebView.this.TAG, "getCachedFilesMap(" + value + ")");
            String funToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                SSAObj ssaObj = new SSAObj(value);
                if (!ssaObj.containsKey("path")) {
                    IronSourceWebView.this.responseBack(value, false, "path key does not exist", null);
                    return;
                }
                String mapPath = (String)ssaObj.get("path");
                if (!IronSourceStorageUtils.isPathExist(IronSourceWebView.this.mCacheDirectory, mapPath)) {
                    IronSourceWebView.this.responseBack(value, false, "path file does not exist on disk", null);
                    return;
                }
                String fileSystmeMap = IronSourceStorageUtils.getCachedFilesMap(IronSourceWebView.this.mCacheDirectory, mapPath);
                String script = IronSourceWebView.this.generateJSToInject(funToCall, fileSystmeMap, "onGetCachedFilesMapSuccess", "onGetCachedFilesMapFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void adCredited(final String value) {
            final String appKey;
            final String userId;
            Log.d((String)IronSourceWebView.this.PUB_TAG, (String)("adCredited(" + value + ")"));
            SSAObj ssaObj = new SSAObj(value);
            String creditsStr = ssaObj.getString("credits");
            final int credits = creditsStr != null ? Integer.parseInt(creditsStr) : 0;
            String totalCreditsStr = ssaObj.getString("total");
            final int totalCredits = totalCreditsStr != null ? Integer.parseInt(totalCreditsStr) : 0;
            final String demandSourceName = ssaObj.getString("demandSourceName");
            final String product = ssaObj.getString("productType");
            boolean isExternalPoll = ssaObj.getBoolean("externalPoll");
            boolean totalCreditsFlag = false;
            String latestCompeltionsTime = null;
            boolean md5Signature = false;
            if (isExternalPoll) {
                appKey = IronSourceWebView.this.mOWCreditsAppKey;
                userId = IronSourceWebView.this.mOWCreditsUserId;
            } else {
                appKey = IronSourceWebView.this.mOWAppKey;
                userId = IronSourceWebView.this.mOWUserId;
            }
            if (product.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString())) {
                StringBuilder sig;
                String localMD5;
                if (ssaObj.isNull("signature") || ssaObj.isNull("timestamp") || ssaObj.isNull("totalCreditsFlag")) {
                    IronSourceWebView.this.responseBack(value, false, "One of the keys are missing: signature/timestamp/totalCreditsFlag", null);
                    return;
                }
                String controllerMD5 = ssaObj.getString("signature");
                if (controllerMD5.equalsIgnoreCase(localMD5 = SDKUtils.getMD5((sig = new StringBuilder().append(totalCreditsStr).append(appKey).append(userId)).toString()))) {
                    md5Signature = true;
                } else {
                    IronSourceWebView.this.responseBack(value, false, "Controller signature is not equal to SDK signature", null);
                }
                totalCreditsFlag = ssaObj.getBoolean("totalCreditsFlag");
                latestCompeltionsTime = ssaObj.getString("timestamp");
            }
            if (IronSourceWebView.this.shouldNotifyDeveloper(product)) {
                final boolean mTotalCreditsFlag = totalCreditsFlag;
                final String mlatestCompeltionsTime = latestCompeltionsTime;
                final boolean mMd5Signature = md5Signature;
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if (product.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
                            IronSourceWebView.this.mOnRewardedVideoListener.onRVAdCredited(credits, demandSourceName);
                        } else if (product.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString()) && mMd5Signature && IronSourceWebView.this.mOnOfferWallListener.onOWAdCredited(credits, totalCredits, mTotalCreditsFlag) && !TextUtils.isEmpty((CharSequence)mlatestCompeltionsTime)) {
                            boolean result = IronSourceSharedPrefHelper.getSupersonicPrefHelper().setLatestCompeltionsTime(mlatestCompeltionsTime, appKey, userId);
                            if (result) {
                                IronSourceWebView.this.responseBack(value, true, null, null);
                            } else {
                                IronSourceWebView.this.responseBack(value, false, "Time Stamp could not be stored", null);
                            }
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void removeCloseEventHandler(String value) {
            Logger.i(IronSourceWebView.this.TAG, "removeCloseEventHandler(" + value + ")");
            if (IronSourceWebView.this.mCloseEventTimer != null) {
                IronSourceWebView.this.mCloseEventTimer.cancel();
            }
            IronSourceWebView.this.isRemoveCloseEventHandler = true;
        }

        @JavascriptInterface
        public void onGetDeviceStatusSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetDeviceStatusSuccess(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetDeviceStatusSuccess", value);
        }

        @JavascriptInterface
        public void onGetDeviceStatusFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetDeviceStatusFail(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetDeviceStatusFail", value);
        }

        @JavascriptInterface
        public void onInitRewardedVideoSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onInitRewardedVideoSuccess(" + value + ")");
            SSABCParameters ssaBCParameters = new SSABCParameters(value);
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().setSSABCParameters(ssaBCParameters);
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onInitRewardedVideoSuccess", value);
        }

        @JavascriptInterface
        public void onInitRewardedVideoFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onInitRewardedVideoFail(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            final String demandSourceName = ssaObj.getString("demandSourceName");
            DemandSource demandSource = IronSourceAdsPublisherAgent.getInstance((Activity)IronSourceWebView.this.getCurrentActivityContext()).getDemandSourceByName(demandSourceName);
            if (demandSource != null) {
                demandSource.setDemandSourceInitState(3);
            }
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.RewardedVideo.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        Log.d((String)IronSourceWebView.this.TAG, (String)("onRVInitFail(message:" + message + ")"));
                        IronSourceWebView.this.mOnRewardedVideoListener.onRVInitFail(toSend, demandSourceName);
                    }
                });
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onInitRewardedVideoFail", value);
        }

        @JavascriptInterface
        public void onGetApplicationInfoSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetApplicationInfoSuccess(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetApplicationInfoSuccess", value);
        }

        @JavascriptInterface
        public void onGetApplicationInfoFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetApplicationInfoFail(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetApplicationInfoFail", value);
        }

        @JavascriptInterface
        public void onShowRewardedVideoSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowRewardedVideoSuccess(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onShowRewardedVideoSuccess", value);
        }

        @JavascriptInterface
        public void onShowRewardedVideoFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowRewardedVideoFail(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            final String demandSourceName = ssaObj.getString("demandSourceName");
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.RewardedVideo.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        Log.d((String)IronSourceWebView.this.TAG, (String)("onRVShowFail(message:" + message + ")"));
                        IronSourceWebView.this.mOnRewardedVideoListener.onRVShowFail(toSend, demandSourceName);
                    }
                });
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onShowRewardedVideoFail", value);
        }

        @JavascriptInterface
        public void onGetCachedFilesMapSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetCachedFilesMapSuccess(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetCachedFilesMapSuccess", value);
        }

        @JavascriptInterface
        public void onGetCachedFilesMapFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetCachedFilesMapFail(" + value + ")");
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetCachedFilesMapFail", value);
        }

        @JavascriptInterface
        public void onShowOfferWallSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowOfferWallSuccess(" + value + ")");
            IronSourceWebView.this.mSavedState.adOpened(SSAEnums.ProductType.OfferWall.ordinal());
            final String placementId = SDKUtils.getValueFromJsonObject(value, "placementId");
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        IronSourceWebView.this.mOnOfferWallListener.onOWShowSuccess(placementId);
                    }
                });
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onShowOfferWallSuccess", value);
        }

        @JavascriptInterface
        public void onShowOfferWallFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowOfferWallFail(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        IronSourceWebView.this.mOnOfferWallListener.onOWShowFail(toSend);
                    }
                });
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onShowOfferWallFail", value);
        }

        @JavascriptInterface
        public void onInitInterstitialSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onInitInterstitialSuccess()");
            IronSourceWebView.this.toastingErrMsg("onInitInterstitialSuccess", "true");
            IronSourceWebView.this.mSavedState.setInterstitialInitSuccess(true);
            if (IronSourceWebView.this.mSavedState.reportInitInterstitial()) {
                IronSourceWebView.this.mSavedState.setReportInitInterstitial(false);
                if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                    IronSourceWebView.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d((String)IronSourceWebView.this.TAG, (String)"onInterstitialInitSuccess()");
                            IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialInitSuccess();
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void onInitInterstitialFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onInitInterstitialFail(" + value + ")");
            IronSourceWebView.this.mSavedState.setInterstitialInitSuccess(false);
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            if (IronSourceWebView.this.mSavedState.reportInitInterstitial()) {
                IronSourceWebView.this.mSavedState.setReportInitInterstitial(false);
                if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                    IronSourceWebView.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            String toSend = message;
                            if (toSend == null) {
                                toSend = "We're sorry, some error occurred. we will investigate it";
                            }
                            Log.d((String)IronSourceWebView.this.TAG, (String)("onInterstitialInitFail(message:" + toSend + ")"));
                            IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialInitFailed(toSend);
                        }
                    });
                }
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onInitInterstitialFail", value);
        }

        private void setInterstitialAvailability(boolean isAvailable) {
            IronSourceWebView.this.mIsInterstitialAvailable = isAvailable;
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.toastingErrMsg("onInterstitialAvailability", String.valueOf(IronSourceWebView.this.mIsInterstitialAvailable));
            }
        }

        @JavascriptInterface
        public void adClicked(String value) {
            Logger.i(IronSourceWebView.this.TAG, "adClicked(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            String productType = ssaObj.getString("productType");
            if (productType.equalsIgnoreCase(SSAEnums.ProductType.Interstitial.toString()) && IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialClick();
                    }
                });
            } else if (productType.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString()) && IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.RewardedVideo.toString())) {
                final String demandSourceName = ssaObj.getString("demandSourceName");
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        IronSourceWebView.this.mOnRewardedVideoListener.onRVAdClicked(demandSourceName);
                    }
                });
            }
        }

        @JavascriptInterface
        public void onShowInterstitialSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowInterstitialSuccess(" + value + ")");
            IronSourceWebView.this.mSavedState.adOpened(SSAEnums.ProductType.Interstitial.ordinal());
            IronSourceWebView.this.responseBack(value, true, null, null);
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialOpen();
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialShowSuccess();
                    }
                });
                IronSourceWebView.this.toastingErrMsg("onShowInterstitialSuccess", value);
            }
            this.setInterstitialAvailability(false);
        }

        @JavascriptInterface
        public void onInitOfferWallSuccess(String value) {
            IronSourceWebView.this.toastingErrMsg("onInitOfferWallSuccess", "true");
            IronSourceWebView.this.mSavedState.setOfferwallInitSuccess(true);
            if (IronSourceWebView.this.mSavedState.reportInitOfferwall()) {
                IronSourceWebView.this.mSavedState.setOfferwallReportInit(false);
                if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                    IronSourceWebView.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Log.d((String)IronSourceWebView.this.TAG, (String)"onOfferWallInitSuccess()");
                            IronSourceWebView.this.mOnOfferWallListener.onOfferwallInitSuccess();
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void onInitOfferWallFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onInitOfferWallFail(" + value + ")");
            IronSourceWebView.this.mSavedState.setOfferwallInitSuccess(false);
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            if (IronSourceWebView.this.mSavedState.reportInitOfferwall()) {
                IronSourceWebView.this.mSavedState.setOfferwallReportInit(false);
                if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                    IronSourceWebView.this.runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            String toSend = message;
                            if (toSend == null) {
                                toSend = "We're sorry, some error occurred. we will investigate it";
                            }
                            Log.d((String)IronSourceWebView.this.TAG, (String)("onOfferWallInitFail(message:" + toSend + ")"));
                            IronSourceWebView.this.mOnOfferWallListener.onOfferwallInitFail(toSend);
                        }
                    });
                }
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onInitOfferWallFail", value);
        }

        @JavascriptInterface
        public void onLoadInterstitialSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onLoadInterstitialSuccess(" + value + ")");
            this.setInterstitialAvailability(true);
            IronSourceWebView.this.responseBack(value, true, null, null);
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialLoadSuccess();
                    }
                });
            }
            IronSourceWebView.this.toastingErrMsg("onLoadInterstitialSuccess", "true");
        }

        @JavascriptInterface
        public void onLoadInterstitialFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onLoadInterstitialFail(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            IronSourceWebView.this.responseBack(value, true, null, null);
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialLoadFailed(toSend);
                    }
                });
            }
            IronSourceWebView.this.toastingErrMsg("onLoadInterstitialFail", "true");
        }

        @JavascriptInterface
        public void onShowInterstitialFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onShowInterstitialFail(" + value + ")");
            this.setInterstitialAvailability(false);
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            IronSourceWebView.this.responseBack(value, true, null, null);
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.Interstitial.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialShowFailed(toSend);
                    }
                });
            }
            IronSourceWebView.this.toastingErrMsg("onShowInterstitialFail", value);
        }

        @JavascriptInterface
        public void onGenericFunctionSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGenericFunctionSuccess(" + value + ")");
            if (IronSourceWebView.this.mOnGenericFunctionListener == null) {
                Logger.d(IronSourceWebView.this.TAG, "genericFunctionListener was not found");
                return;
            }
            IronSourceWebView.this.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    IronSourceWebView.this.mOnGenericFunctionListener.onGFSuccess();
                }
            });
            IronSourceWebView.this.responseBack(value, true, null, null);
        }

        @JavascriptInterface
        public void onGenericFunctionFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGenericFunctionFail(" + value + ")");
            if (IronSourceWebView.this.mOnGenericFunctionListener == null) {
                Logger.d(IronSourceWebView.this.TAG, "genericFunctionListener was not found");
                return;
            }
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            IronSourceWebView.this.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    IronSourceWebView.this.mOnGenericFunctionListener.onGFFail(message);
                }
            });
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGenericFunctionFail", value);
        }

        @JavascriptInterface
        public void createCalendarEvent(String value) {
            Logger.i(IronSourceWebView.this.TAG, "createCalendarEvent(" + value + ")");
            try {
                JSONObject jsObj = new JSONObject();
                JSONObject jsRecurrence = new JSONObject();
                jsRecurrence.put("frequency", (Object)"weekly");
                jsObj.put("id", (Object)"testevent723GDf84");
                jsObj.put("description", (Object)"Watch this crazy showInterstitial on cannel 5!");
                jsObj.put("start", (Object)"2014-02-01T20:00:00-8:00");
                jsObj.put("end", (Object)"2014-06-30T20:00:00-8:00");
                jsObj.put("status", (Object)"pending");
                jsObj.put("recurrence", (Object)jsRecurrence.toString());
                jsObj.put("reminder", (Object)"2014-02-01T19:50:00-8:00");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openUrl(String value) {
            Logger.i(IronSourceWebView.this.TAG, "openUrl(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            String url = ssaObj.getString("url");
            String method = ssaObj.getString("method");
            Context context = IronSourceWebView.this.getCurrentActivityContext();
            try {
                if (method.equalsIgnoreCase("external_browser")) {
                    UrlHandler.openUrl(context, url);
                } else if (method.equalsIgnoreCase("webview")) {
                    Intent intent = new Intent(context, OpenUrlActivity.class);
                    intent.putExtra(IronSourceWebView.EXTERNAL_URL, url);
                    intent.putExtra(IronSourceWebView.SECONDARY_WEB_VIEW, true);
                    intent.putExtra("immersive", IronSourceWebView.this.mIsImmersive);
                    context.startActivity(intent);
                } else if (method.equalsIgnoreCase("store")) {
                    Intent intent = new Intent(context, OpenUrlActivity.class);
                    intent.putExtra(IronSourceWebView.EXTERNAL_URL, url);
                    intent.putExtra(IronSourceWebView.IS_STORE, true);
                    intent.putExtra(IronSourceWebView.SECONDARY_WEB_VIEW, true);
                    context.startActivity(intent);
                }
            }
            catch (Exception ex) {
                IronSourceWebView.this.responseBack(value, false, ex.getMessage(), null);
                ex.printStackTrace();
            }
        }

        @JavascriptInterface
        public void setForceClose(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setForceClose(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            String width = ssaObj.getString("width");
            String hight = ssaObj.getString("height");
            IronSourceWebView.this.mHiddenForceCloseWidth = Integer.parseInt(width);
            IronSourceWebView.this.mHiddenForceCloseHeight = Integer.parseInt(hight);
            IronSourceWebView.this.mHiddenForceCloseLocation = ssaObj.getString("position");
        }

        @JavascriptInterface
        public void setBackButtonState(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setBackButtonState(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            String state = ssaObj.getString("state");
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().setBackButtonState(state);
        }

        @JavascriptInterface
        public void setStoreSearchKeys(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setStoreSearchKeys(" + value + ")");
            IronSourceSharedPrefHelper.getSupersonicPrefHelper().setSearchKeys(value);
        }

        @JavascriptInterface
        public void setWebviewBackgroundColor(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setWebviewBackgroundColor(" + value + ")");
            IronSourceWebView.this.setWebviewBackground(value);
        }

        @JavascriptInterface
        public void toggleUDIA(String value) {
            Logger.i(IronSourceWebView.this.TAG, "toggleUDIA(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("toggle")) {
                IronSourceWebView.this.responseBack(value, false, "toggle key does not exist", null);
                return;
            }
            String toggleStr = ssaObj.getString("toggle");
            int toggle = Integer.parseInt(toggleStr);
            if (toggle == 0) {
                return;
            }
            String binaryToggle = Integer.toBinaryString(toggle);
            if (TextUtils.isEmpty((CharSequence)binaryToggle)) {
                IronSourceWebView.this.responseBack(value, false, "fialed to convert toggle", null);
                return;
            }
            char[] binaryToggleArr = binaryToggle.toCharArray();
            if (binaryToggleArr[3] == '0') {
                IronSourceSharedPrefHelper.getSupersonicPrefHelper().setShouldRegisterSessions(true);
            } else {
                IronSourceSharedPrefHelper.getSupersonicPrefHelper().setShouldRegisterSessions(false);
            }
        }

        @JavascriptInterface
        public void getUDIA(String value) {
            this.udiaResults = 0;
            Logger.i(IronSourceWebView.this.TAG, "getUDIA(" + value + ")");
            String funToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("getByFlag")) {
                IronSourceWebView.this.responseBack(value, false, "getByFlag key does not exist", null);
                return;
            }
            String getByFlagStr = ssaObj.getString("getByFlag");
            int getByFlag = Integer.parseInt(getByFlagStr);
            if (getByFlag == 0) {
                return;
            }
            String binaryToggle = Integer.toBinaryString(getByFlag);
            if (TextUtils.isEmpty((CharSequence)binaryToggle)) {
                IronSourceWebView.this.responseBack(value, false, "fialed to convert getByFlag", null);
                return;
            }
            StringBuilder strBld = new StringBuilder(binaryToggle).reverse();
            binaryToggle = strBld.toString();
            char[] binaryToggleArr = binaryToggle.toCharArray();
            JSONArray jsArr = new JSONArray();
            if (binaryToggleArr[3] == '0') {
                JSONObject jsObj = new JSONObject();
                try {
                    jsObj.put("sessions", (Object)IronSourceSharedPrefHelper.getSupersonicPrefHelper().getSessions());
                    IronSourceSharedPrefHelper.getSupersonicPrefHelper().deleteSessions();
                    jsArr.put((Object)jsObj);
                }
                catch (JSONException var11_11) {
                    // empty catch block
                }
            }
            if (binaryToggleArr[2] == '1') {
                ++this.udiaResults;
                Location location = LocationService.getLastLocation(IronSourceWebView.this.getContext());
                if (location != null) {
                    JSONObject jsObj = new JSONObject();
                    try {
                        jsObj.put("latitude", location.getLatitude());
                        jsObj.put("longitude", location.getLongitude());
                        jsArr.put((Object)jsObj);
                        --this.udiaResults;
                        this.sendResults(funToCall, jsArr);
                        Logger.i(IronSourceWebView.this.TAG, "done location");
                    }
                    catch (JSONException var12_13) {}
                } else {
                    --this.udiaResults;
                }
            }
        }

        private void sendResults(String funToCall, JSONArray jsArr) {
            Logger.i(IronSourceWebView.this.TAG, "sendResults: " + this.udiaResults);
            if (this.udiaResults <= 0) {
                this.injectGetUDIA(funToCall, jsArr);
            }
        }

        @JavascriptInterface
        public void onUDIASuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onUDIASuccess(" + value + ")");
        }

        @JavascriptInterface
        public void onUDIAFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onUDIAFail(" + value + ")");
        }

        @JavascriptInterface
        public void onGetUDIASuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetUDIASuccess(" + value + ")");
        }

        @JavascriptInterface
        public void onGetUDIAFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetUDIAFail(" + value + ")");
        }

        @JavascriptInterface
        public void setUserUniqueId(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setUserUniqueId(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("userUniqueId") || !ssaObj.containsKey("productType")) {
                IronSourceWebView.this.responseBack(value, false, "uniqueId or productType does not exist", null);
                return;
            }
            String uniqueId = ssaObj.getString("userUniqueId");
            String productType = ssaObj.getString("productType");
            boolean result = IronSourceSharedPrefHelper.getSupersonicPrefHelper().setUniqueId(uniqueId, productType);
            if (result) {
                IronSourceWebView.this.responseBack(value, true, null, null);
            } else {
                IronSourceWebView.this.responseBack(value, false, "setUserUniqueId failed", null);
            }
        }

        @JavascriptInterface
        public void getUserUniqueId(String value) {
            Logger.i(IronSourceWebView.this.TAG, "getUserUniqueId(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("productType")) {
                IronSourceWebView.this.responseBack(value, false, "productType does not exist", null);
                return;
            }
            String funToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String productType = ssaObj.getString("productType");
                String id = IronSourceSharedPrefHelper.getSupersonicPrefHelper().getUniqueId(productType);
                String params = IronSourceWebView.this.parseToJson("userUniqueId", id, "productType", productType, null, null, null, null, null, false);
                String script = IronSourceWebView.this.generateJSToInject(funToCall, params, "onGetUserUniqueIdSuccess", "onGetUserUniqueIdFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void onGetUserUniqueIdSuccess(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetUserUniqueIdSuccess(" + value + ")");
        }

        @JavascriptInterface
        public void onGetUserUniqueIdFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetUserUniqueIdFail(" + value + ")");
        }

        private void injectGetUDIA(String funToCall, JSONArray jsonArr) {
            if (!TextUtils.isEmpty((CharSequence)funToCall)) {
                String script = IronSourceWebView.this.generateJSToInject(funToCall, jsonArr.toString(), "onGetUDIASuccess", "onGetUDIAFail");
                IronSourceWebView.this.injectJavascript(script);
            }
        }

        @JavascriptInterface
        public void onOfferWallGeneric(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onOfferWallGeneric(" + value + ")");
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                IronSourceWebView.this.mOnOfferWallListener.onOWGeneric("", "");
            }
        }

        @JavascriptInterface
        public void setUserData(String value) {
            Logger.i(IronSourceWebView.this.TAG, "setUserData(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("key")) {
                IronSourceWebView.this.responseBack(value, false, "key does not exist", null);
                return;
            }
            if (!ssaObj.containsKey("value")) {
                IronSourceWebView.this.responseBack(value, false, "value does not exist", null);
                return;
            }
            String mKey = ssaObj.getString("key");
            String mValue = ssaObj.getString("value");
            boolean result = IronSourceSharedPrefHelper.getSupersonicPrefHelper().setUserData(mKey, mValue);
            if (result) {
                String successFunToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
                String params = IronSourceWebView.this.parseToJson(mKey, mValue, null, null, null, null, null, null, null, false);
                String script = IronSourceWebView.this.generateJSToInject(successFunToCall, params);
                IronSourceWebView.this.injectJavascript(script);
            } else {
                IronSourceWebView.this.responseBack(value, false, "SetUserData failed writing to shared preferences", null);
            }
        }

        @JavascriptInterface
        public void getUserData(String value) {
            Logger.i(IronSourceWebView.this.TAG, "getUserData(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            if (!ssaObj.containsKey("key")) {
                IronSourceWebView.this.responseBack(value, false, "key does not exist", null);
                return;
            }
            String failFunToCall = IronSourceWebView.this.extractSuccessFunctionToCall(value);
            String mKey = ssaObj.getString("key");
            String mValue = IronSourceSharedPrefHelper.getSupersonicPrefHelper().getUserData(mKey);
            String params = IronSourceWebView.this.parseToJson(mKey, mValue, null, null, null, null, null, null, null, false);
            String script = IronSourceWebView.this.generateJSToInject(failFunToCall, params);
            IronSourceWebView.this.injectJavascript(script);
        }

        @JavascriptInterface
        public void onGetUserCreditsFail(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onGetUserCreditsFail(" + value + ")");
            SSAObj ssaObj = new SSAObj(value);
            final String message = ssaObj.getString("errMsg");
            if (IronSourceWebView.this.shouldNotifyDeveloper(SSAEnums.ProductType.OfferWall.toString())) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        String toSend = message;
                        if (toSend == null) {
                            toSend = "We're sorry, some error occurred. we will investigate it";
                        }
                        IronSourceWebView.this.mOnOfferWallListener.onGetOWCreditsFailed(toSend);
                    }
                });
            }
            IronSourceWebView.this.responseBack(value, true, null, null);
            IronSourceWebView.this.toastingErrMsg("onGetUserCreditsFail", value);
        }

        @JavascriptInterface
        public void onAdWindowsClosed(String value) {
            Logger.i(IronSourceWebView.this.TAG, "onAdWindowsClosed(" + value + ")");
            IronSourceWebView.this.mSavedState.adClosed();
            IronSourceWebView.this.mSavedState.setDisplayedDemandSourceName(null);
            SSAObj ssaObj = new SSAObj(value);
            final String product = ssaObj.getString("productType");
            final String demandSourceName = ssaObj.getString("demandSourceName");
            if (product.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
                Log.d((String)IronSourceWebView.this.PUB_TAG, (String)"onRVAdClosed()");
            } else if (product.equalsIgnoreCase(SSAEnums.ProductType.Interstitial.toString())) {
                Log.d((String)IronSourceWebView.this.PUB_TAG, (String)"onISAdClosed()");
            } else if (product.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString())) {
                Log.d((String)IronSourceWebView.this.PUB_TAG, (String)"onOWAdClosed()");
            }
            if (IronSourceWebView.this.shouldNotifyDeveloper(product) && product != null) {
                IronSourceWebView.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if (product.equalsIgnoreCase(SSAEnums.ProductType.RewardedVideo.toString())) {
                            IronSourceWebView.this.mOnRewardedVideoListener.onRVAdClosed(demandSourceName);
                        } else if (product.equalsIgnoreCase(SSAEnums.ProductType.Interstitial.toString())) {
                            IronSourceWebView.this.mOnInitInterstitialListener.onInterstitialClose();
                        } else if (product.equalsIgnoreCase(SSAEnums.ProductType.OfferWall.toString())) {
                            IronSourceWebView.this.mOnOfferWallListener.onOWAdClosed();
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void onVideoStatusChanged(String value) {
            Log.d((String)IronSourceWebView.this.TAG, (String)("onVideoStatusChanged(" + value + ")"));
            SSAObj ssaObj = new SSAObj(value);
            String product = ssaObj.getString("productType");
            if (IronSourceWebView.this.mVideoEventsListener != null && !TextUtils.isEmpty((CharSequence)product) && SSAEnums.ProductType.RewardedVideo.toString().equalsIgnoreCase(product)) {
                String status = ssaObj.getString("status");
                if ("started".equalsIgnoreCase(status)) {
                    IronSourceWebView.this.mVideoEventsListener.onVideoStarted();
                } else if ("paused".equalsIgnoreCase(status)) {
                    IronSourceWebView.this.mVideoEventsListener.onVideoPaused();
                } else if ("playing".equalsIgnoreCase(status)) {
                    IronSourceWebView.this.mVideoEventsListener.onVideoResumed();
                } else if ("ended".equalsIgnoreCase(status)) {
                    IronSourceWebView.this.mVideoEventsListener.onVideoEnded();
                } else if ("stopped".equalsIgnoreCase(status)) {
                    IronSourceWebView.this.mVideoEventsListener.onVideoStopped();
                } else {
                    Logger.i(IronSourceWebView.this.TAG, "onVideoStatusChanged: unknown status: " + status);
                }
            }
        }

    }

    private class FrameBustWebViewClient
    extends WebViewClient {
        private FrameBustWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Context ctx = IronSourceWebView.this.getCurrentActivityContext();
            Intent intent = new Intent(ctx, OpenUrlActivity.class);
            intent.putExtra(IronSourceWebView.EXTERNAL_URL, url);
            intent.putExtra(IronSourceWebView.SECONDARY_WEB_VIEW, false);
            ctx.startActivity(intent);
            return true;
        }
    }

    private class ChromeClient
    extends WebChromeClient {
        private ChromeClient() {
        }

        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView childView = new WebView(view.getContext());
            childView.setWebChromeClient((WebChromeClient)this);
            childView.setWebViewClient((WebViewClient)new FrameBustWebViewClient());
            WebView.WebViewTransport transport = (WebView.WebViewTransport)resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();
            Logger.i("onCreateWindow", "onCreateWindow");
            return true;
        }

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Logger.i("MyApplication", consoleMessage.message() + " -- From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
            return true;
        }

        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            Logger.i("Test", "onShowCustomView");
            IronSourceWebView.this.setVisibility(8);
            if (IronSourceWebView.this.mCustomView != null) {
                Logger.i("Test", "mCustomView != null");
                callback.onCustomViewHidden();
                return;
            }
            Logger.i("Test", "mCustomView == null");
            IronSourceWebView.this.mCustomViewContainer.addView(view);
            IronSourceWebView.this.mCustomView = view;
            IronSourceWebView.this.mCustomViewCallback = callback;
            IronSourceWebView.this.mCustomViewContainer.setVisibility(0);
        }

        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(IronSourceWebView.this.getCurrentActivityContext());
            frameLayout.setLayoutParams((ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
            return frameLayout;
        }

        public void onHideCustomView() {
            Logger.i("Test", "onHideCustomView");
            if (IronSourceWebView.this.mCustomView == null) {
                return;
            }
            IronSourceWebView.this.mCustomView.setVisibility(8);
            IronSourceWebView.this.mCustomViewContainer.removeView(IronSourceWebView.this.mCustomView);
            IronSourceWebView.this.mCustomView = null;
            IronSourceWebView.this.mCustomViewContainer.setVisibility(8);
            IronSourceWebView.this.mCustomViewCallback.onCustomViewHidden();
            IronSourceWebView.this.setVisibility(0);
        }
    }

    private class ViewClient
    extends WebViewClient {
        private ViewClient() {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.i("onPageStarted", url);
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            Logger.i("onPageFinished", url);
            if (url.contains("adUnit") || url.contains("index.html")) {
                IronSourceWebView.this.pageFinished();
            }
            super.onPageFinished(view, url);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Logger.i("onReceivedError", failingUrl + " " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.i("shouldOverrideUrlLoading", url);
            try {
                if (IronSourceWebView.this.handleSearchKeysURLs(url)) {
                    IronSourceWebView.this.interceptedUrlToStore();
                    return true;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Logger.i("shouldInterceptRequest", url);
            boolean mraidCall = false;
            try {
                URL mUrl = new URL(url);
                String file = mUrl.getFile();
                if (file.contains("mraid.js")) {
                    mraidCall = true;
                }
            }
            catch (MalformedURLException mUrl) {
                // empty catch block
            }
            if (mraidCall) {
                String filePath = "file://" + IronSourceWebView.this.mCacheDirectory + File.separator + "mraid.js";
                File mraidFile = new File(filePath);
                try {
                    FileInputStream fis = new FileInputStream(mraidFile);
                    return new WebResourceResponse("text/javascript", "UTF-8", this.getClass().getResourceAsStream(filePath));
                }
                catch (FileNotFoundException fis) {
                    // empty catch block
                }
            }
            return super.shouldInterceptRequest(view, url);
        }
    }

    private class SupersonicWebViewTouchListener
    implements View.OnTouchListener {
        private SupersonicWebViewTouchListener() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 1) {
                float xTouch = event.getX();
                float yTouch = event.getY();
                Logger.i(IronSourceWebView.this.TAG, "X:" + (int)xTouch + " Y:" + (int)yTouch);
                int width = DeviceStatus.getDeviceWidth();
                int height = DeviceStatus.getDeviceHeight();
                Logger.i(IronSourceWebView.this.TAG, "Width:" + width + " Height:" + height);
                int boundsTouchAreaX = SDKUtils.dpToPx(IronSourceWebView.this.mHiddenForceCloseWidth);
                int boundsTouchAreaY = SDKUtils.dpToPx(IronSourceWebView.this.mHiddenForceCloseHeight);
                int actualTouchX = 0;
                int actualTouchY = 0;
                if ("top-right".equalsIgnoreCase(IronSourceWebView.this.mHiddenForceCloseLocation)) {
                    actualTouchX = width - (int)xTouch;
                    actualTouchY = (int)yTouch;
                } else if ("top-left".equalsIgnoreCase(IronSourceWebView.this.mHiddenForceCloseLocation)) {
                    actualTouchX = (int)xTouch;
                    actualTouchY = (int)yTouch;
                } else if ("bottom-right".equalsIgnoreCase(IronSourceWebView.this.mHiddenForceCloseLocation)) {
                    actualTouchX = width - (int)xTouch;
                    actualTouchY = height - (int)yTouch;
                } else if ("bottom-left".equalsIgnoreCase(IronSourceWebView.this.mHiddenForceCloseLocation)) {
                    actualTouchX = (int)xTouch;
                    actualTouchY = height - (int)yTouch;
                }
                if (actualTouchX <= boundsTouchAreaX && actualTouchY <= boundsTouchAreaY) {
                    IronSourceWebView.this.isRemoveCloseEventHandler = false;
                    if (IronSourceWebView.this.mCloseEventTimer != null) {
                        IronSourceWebView.this.mCloseEventTimer.cancel();
                    }
                    IronSourceWebView.this.mCloseEventTimer = new CountDownTimer(2000, 500){

                        public void onTick(long millisUntilFinished) {
                            Logger.i(IronSourceWebView.this.TAG, "Close Event Timer Tick " + millisUntilFinished);
                        }

                        public void onFinish() {
                            Logger.i(IronSourceWebView.this.TAG, "Close Event Timer Finish");
                            if (IronSourceWebView.this.isRemoveCloseEventHandler) {
                                IronSourceWebView.this.isRemoveCloseEventHandler = false;
                            } else {
                                IronSourceWebView.this.engageEnd("forceClose");
                            }
                        }
                    }.start();
                }
            }
            return false;
        }

    }

}

