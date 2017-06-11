/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.graphics.Bitmap
 *  android.net.Uri
 *  android.os.AsyncTask
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Handler
 *  android.view.ContextThemeWrapper
 *  android.view.KeyEvent
 *  android.view.View
 *  android.view.View$OnSystemUiVisibilityChangeListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.ViewParent
 *  android.view.Window
 *  android.webkit.WebSettings
 *  android.webkit.WebView
 *  android.webkit.WebViewClient
 *  android.widget.ProgressBar
 *  android.widget.RelativeLayout
 *  android.widget.RelativeLayout$LayoutParams
 */
package com.ironsource.sdk.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.ironsource.sdk.agent.IronSourceAdsPublisherAgent;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.utils.IronSourceAsyncHttpRequestTask;
import com.ironsource.sdk.utils.IronSourceSharedPrefHelper;
import com.ironsource.sdk.utils.Logger;
import com.ironsource.sdk.utils.SDKUtils;
import java.util.List;

public class OpenUrlActivity
extends Activity {
    private static final String TAG = "OpenUrlActivity";
    private static final int WEB_VIEW_VIEW_ID = SDKUtils.generateViewId();
    private static final int PROGRESS_BAR_VIEW_ID = SDKUtils.generateViewId();
    private WebView webView = null;
    private IronSourceWebView mWebViewController;
    private ProgressBar mProgressBar;
    boolean isSecondaryWebview;
    private RelativeLayout mainLayout;
    private String mUrl;
    private Handler mUiThreadHandler = new Handler();
    private boolean mIsImmersive = false;
    private final Runnable decorViewSettings;

    public OpenUrlActivity() {
        this.decorViewSettings = new Runnable(){

            @Override
            public void run() {
                OpenUrlActivity.this.getWindow().getDecorView().setSystemUiVisibility(SDKUtils.getActivityUIFlags(OpenUrlActivity.this.mIsImmersive));
            }
        };
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("OpenUrlActivity", "onCreate()");
        IronSourceAdsPublisherAgent ssaPubAgt = IronSourceAdsPublisherAgent.getInstance(this);
        this.mWebViewController = ssaPubAgt.getWebViewController();
        this.hideActivityTitle();
        this.hideActivtiyStatusBar();
        Bundle bundle = this.getIntent().getExtras();
        this.mUrl = bundle.getString(IronSourceWebView.EXTERNAL_URL);
        this.isSecondaryWebview = bundle.getBoolean(IronSourceWebView.SECONDARY_WEB_VIEW);
        Intent intent = this.getIntent();
        this.mIsImmersive = intent.getBooleanExtra("immersive", false);
        if (this.mIsImmersive) {
            this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){

                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & 4098) == 0) {
                        OpenUrlActivity.this.mUiThreadHandler.removeCallbacks(OpenUrlActivity.this.decorViewSettings);
                        OpenUrlActivity.this.mUiThreadHandler.postDelayed(OpenUrlActivity.this.decorViewSettings, 500);
                    }
                }
            });
            this.runOnUiThread(this.decorViewSettings);
        }
        this.mainLayout = new RelativeLayout((Context)this);
        this.setContentView((View)this.mainLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    protected void onResume() {
        super.onResume();
        this.createWebView();
    }

    private void createWebView() {
        if (this.webView == null) {
            this.webView = new WebView(this.getApplicationContext());
            this.webView.setId(WEB_VIEW_VIEW_ID);
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.setWebViewClient((WebViewClient)new Client());
            this.loadUrl(this.mUrl);
        }
        if (this.findViewById(WEB_VIEW_VIEW_ID) == null) {
            RelativeLayout.LayoutParams webViewLayoutParams = new RelativeLayout.LayoutParams(-1, -1);
            this.mainLayout.addView((View)this.webView, (ViewGroup.LayoutParams)webViewLayoutParams);
        }
        this.createProgressBarForWebView();
        if (this.mWebViewController != null) {
            this.mWebViewController.viewableChange(true, "secondary");
        }
    }

    private void createProgressBarForWebView() {
        if (this.mProgressBar == null) {
            this.mProgressBar = Build.VERSION.SDK_INT >= 11 ? new ProgressBar((Context)new ContextThemeWrapper((Context)this, 16973939)) : new ProgressBar((Context)this);
            this.mProgressBar.setId(PROGRESS_BAR_VIEW_ID);
        }
        if (this.findViewById(PROGRESS_BAR_VIEW_ID) == null) {
            RelativeLayout.LayoutParams progressBarLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
            progressBarLayoutParams.addRule(13);
            this.mProgressBar.setLayoutParams((ViewGroup.LayoutParams)progressBarLayoutParams);
            this.mProgressBar.setVisibility(4);
            this.mainLayout.addView((View)this.mProgressBar);
        }
    }

    private void removeWebViewFromLayout() {
        if (this.mWebViewController != null) {
            ViewGroup parent;
            this.mWebViewController.viewableChange(false, "secondary");
            if (this.mainLayout != null && (parent = (ViewGroup)this.webView.getParent()) != null) {
                if (parent.findViewById(WEB_VIEW_VIEW_ID) != null) {
                    parent.removeView((View)this.webView);
                }
                if (parent.findViewById(PROGRESS_BAR_VIEW_ID) != null) {
                    parent.removeView((View)this.mProgressBar);
                }
            }
        }
    }

    private void destroyWebView() {
        if (this.webView != null) {
            this.webView.destroy();
        }
    }

    protected void onPause() {
        super.onPause();
        this.removeWebViewFromLayout();
    }

    public void loadUrl(String url) {
        this.webView.stopLoading();
        this.webView.clearHistory();
        try {
            this.webView.loadUrl(url);
        }
        catch (Throwable e) {
            Logger.e("OpenUrlActivity", "OpenUrlActivity:: loadUrl: " + e.toString());
            new IronSourceAsyncHttpRequestTask().execute(new String[]{"https://www.supersonicads.com/mobile/sdk5/log?method=" + e.getStackTrace()[0].getMethodName()});
        }
    }

    private void hideActivityTitle() {
        this.requestWindowFeature(1);
    }

    private void hideActivtiyStatusBar() {
        this.getWindow().setFlags(1024, 1024);
    }

    private void disableTouch() {
        this.getWindow().addFlags(16);
    }

    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.destroyWebView();
    }

    public void finish() {
        if (this.isSecondaryWebview) {
            this.mWebViewController.engageEnd("secondaryClose");
        }
        super.finish();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (this.mIsImmersive && hasFocus) {
            this.runOnUiThread(this.decorViewSettings);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mIsImmersive && (keyCode == 25 || keyCode == 24)) {
            this.mUiThreadHandler.postDelayed(this.decorViewSettings, 500);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class Client
    extends WebViewClient {
        private Client() {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            OpenUrlActivity.this.mProgressBar.setVisibility(0);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            OpenUrlActivity.this.mProgressBar.setVisibility(4);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            List<String> searchKeys = IronSourceSharedPrefHelper.getSupersonicPrefHelper().getSearchKeys();
            if (searchKeys != null && !searchKeys.isEmpty()) {
                for (String key : searchKeys) {
                    if (!url.contains(key)) continue;
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((String)url));
                    OpenUrlActivity.this.startActivity(intent);
                    OpenUrlActivity.this.mWebViewController.interceptedUrlToStore();
                    OpenUrlActivity.this.finish();
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}

