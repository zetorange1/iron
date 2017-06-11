/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.res.Resources
 *  android.graphics.Rect
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.Window
 *  android.view.WindowManager
 *  android.view.WindowManager$LayoutParams
 *  android.widget.FrameLayout
 */
package com.ironsource.sdk.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.ironsource.environment.DeviceStatus;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.handlers.BackButtonHandler;
import com.ironsource.sdk.listeners.OnWebViewChangeListener;

public class ControllerView
extends FrameLayout
implements OnWebViewChangeListener {
    private Context mContext;
    private IronSourceWebView mWebViewController;

    public ControllerView(Context context) {
        super(context);
        this.mContext = context;
        this.setClickable(true);
    }

    public void showInterstitial(IronSourceWebView webView) {
        this.mWebViewController = webView;
        this.mWebViewController.setOnWebViewControllerChangeListener(this);
        this.mWebViewController.requestFocus();
        this.mContext = this.mWebViewController.getCurrentActivityContext();
        this.setPaddingByOrientation(this.getStatusBarPadding(), this.getNavigationBarPadding());
        this.addViewToWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWebViewController.resume();
        this.mWebViewController.viewableChange(true, "main");
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWebViewController.pause();
        this.mWebViewController.viewableChange(false, "main");
        if (this.mWebViewController != null) {
            this.mWebViewController.setState(IronSourceWebView.State.Gone);
            this.mWebViewController.removeVideoEventsListener();
        }
        this.removeAllViews();
    }

    private void addViewToWindow() {
        Activity activity = (Activity)this.mContext;
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                ViewGroup decorView = ControllerView.this.getWindowDecorViewGroup();
                if (decorView != null) {
                    decorView.addView((View)ControllerView.this);
                }
            }
        });
    }

    private void removeViewFromWindow() {
        Activity activity = (Activity)this.mContext;
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                ViewGroup decorView = ControllerView.this.getWindowDecorViewGroup();
                if (decorView != null) {
                    decorView.removeView((View)ControllerView.this);
                }
            }
        });
    }

    private ViewGroup getWindowDecorViewGroup() {
        Activity activity = (Activity)this.mContext;
        if (activity != null) {
            return (ViewGroup)activity.getWindow().getDecorView();
        }
        return null;
    }

    private void setPaddingByOrientation(int statusBarHeight, int navigationBarSize) {
        try {
            if (this.mContext != null) {
                int orientation = DeviceStatus.getDeviceOrientation(this.mContext);
                if (orientation == 1) {
                    this.setPadding(0, statusBarHeight, 0, navigationBarSize);
                } else if (orientation == 2) {
                    this.setPadding(0, statusBarHeight, navigationBarSize, 0);
                }
            }
        }
        catch (Exception orientation) {
            // empty catch block
        }
    }

    private int getStatusBarPadding() {
        boolean isFullScreen;
        Activity activity = (Activity)this.mContext;
        boolean bl = isFullScreen = (activity.getWindow().getAttributes().flags & 1024) != 0;
        if (isFullScreen) {
            return 0;
        }
        int top = this.getStatusBarHeight();
        return top > 0 ? top : 0;
    }

    private int getStatusBarHeight() {
        int result = 0;
        try {
            int resourceId;
            if (this.mContext != null && (resourceId = this.mContext.getResources().getIdentifier("status_bar_height", "dimen", "android")) > 0) {
                result = this.mContext.getResources().getDimensionPixelSize(resourceId);
            }
        }
        catch (Exception resourceId) {
            // empty catch block
        }
        return result;
    }

    private int getNavigationBarPadding() {
        Activity activity = (Activity)this.mContext;
        try {
            if (Build.VERSION.SDK_INT > 9) {
                Rect screenRect = new Rect();
                activity.getWindow().getDecorView().getDrawingRect(screenRect);
                Rect visibleFrame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
                int orientation = DeviceStatus.getDeviceOrientation((Context)activity);
                if (orientation == 1) {
                    return screenRect.bottom - visibleFrame.bottom > 0 ? screenRect.bottom - visibleFrame.bottom : 0;
                }
                return screenRect.right - visibleFrame.right > 0 ? screenRect.right - visibleFrame.right : 0;
            }
        }
        catch (Exception screenRect) {
            // empty catch block
        }
        return 0;
    }

    @Override
    public void onCloseRequested() {
        this.removeViewFromWindow();
    }

    @Override
    public void onOrientationChanged(String orientation, int rotation) {
    }

    @Override
    public boolean onBackButtonPressed() {
        return BackButtonHandler.getInstance().handleBackButton((Activity)this.mContext);
    }

}

