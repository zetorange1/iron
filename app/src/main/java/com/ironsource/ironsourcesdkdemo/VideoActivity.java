package com.ironsource.ironsourcesdkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ironsource.environment.DeviceStatus;
import com.ironsource.sdk.agent.IronSourceAdsPublisherAgent;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.controller.VideoEventsListener;
import com.ironsource.sdk.data.AdUnitsState;
import com.ironsource.sdk.data.SSAEnums;
import com.ironsource.sdk.handlers.BackButtonHandler;
import com.ironsource.sdk.listeners.OnWebViewChangeListener;
import com.ironsource.sdk.utils.Logger;
import com.ironsource.sdk.utils.SDKUtils;


public class VideoActivity extends Activity implements OnWebViewChangeListener, VideoEventsListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final int WEB_VIEW_VIEW_ID = 1;
    public int currentRequestedRotation = -1;
    private IronSourceWebView mWebViewController;
    private RelativeLayout mContainer;
    private TextView mStatus;
    private Button mStop;
    private FrameLayout mWebViewFrameContainer;
    private boolean mIsImmersive = false;
    private Handler mUiThreadHandler = new Handler();
    private final Runnable decorViewSettings;
    final RelativeLayout.LayoutParams MATCH_PARENT_LAYOUT_PARAMS;
    private boolean calledFromOnCreate;
    private String mProductType;
    private AdUnitsState mState;

    public VideoActivity() {
        this.decorViewSettings = new Runnable(){

            @Override
            public void run() {
                VideoActivity.this.getWindow().getDecorView().setSystemUiVisibility(SDKUtils.getActivityUIFlags(VideoActivity.this.mIsImmersive));
            }
        };
        this.MATCH_PARENT_LAYOUT_PARAMS = new RelativeLayout.LayoutParams(500, 500);
        this.calledFromOnCreate = false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
        this.hideActivityTitle();
        this.hideActivtiyStatusBar();
        this.setContentView(R.layout.activity_video);
        this.mContainer = (RelativeLayout) findViewById(R.id.container);
        this.mStatus = (TextView) findViewById(R.id.va_status);
        this.mStop = (Button) findViewById(R.id.va_stop);
        this.mStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DemoActivity.mHidden.setText("1");
                finish();
            }
        });
        IronSourceAdsPublisherAgent ssaPubAgt = IronSourceAdsPublisherAgent.getInstance(this);
        this.mWebViewController = ssaPubAgt.getWebViewController();
        this.mWebViewController.setId(R.id.webview);
        this.mWebViewController.setOnWebViewControllerChangeListener(this);
        this.mWebViewController.setVideoEventsListener(this);
        Intent intent = this.getIntent();
        this.mProductType = intent.getStringExtra("productType");
        this.mIsImmersive = intent.getBooleanExtra("immersive", false);
        if (this.mIsImmersive) {
            this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){

                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & 4098) == 0) {
                        VideoActivity.this.mUiThreadHandler.removeCallbacks(VideoActivity.this.decorViewSettings);
                        VideoActivity.this.mUiThreadHandler.postDelayed(VideoActivity.this.decorViewSettings, 500);
                    }
                }
            });
            this.runOnUiThread(this.decorViewSettings);
        }
        if (!TextUtils.isEmpty((CharSequence)this.mProductType) && SSAEnums.ProductType.OfferWall.toString().equalsIgnoreCase(this.mProductType)) {
            if (savedInstanceState != null) {
                AdUnitsState state = (AdUnitsState)savedInstanceState.getParcelable("state");
                if (state != null) {
                    this.mState = state;
                    this.mWebViewController.restoreState(state);
                }
                this.finish();
            } else {
                this.mState = this.mWebViewController.getSavedState();
            }
        }
        this.mWebViewFrameContainer = this.mWebViewController.getLayout();
        View view = this.mContainer.findViewById(R.id.webview);
        if (view == null && this.mWebViewFrameContainer.getParent() != null) {
            this.calledFromOnCreate = true;
            this.finish();
        }
        this.initOrientationState();
    }

    private void initOrientationState() {
        Intent intent = this.getIntent();
        String orientation = intent.getStringExtra("orientation_set_flag");
        int rotation = intent.getIntExtra("rotation_set_flag", 0);
//        this.handleOrientationState(orientation, rotation);
    }

    private void handleOrientationState(String orientation, int rotation) {
        if (orientation != null) {
            if ("landscape".equalsIgnoreCase(orientation)) {
                this.setInitiateLandscapeOrientation();
            } else if ("portrait".equalsIgnoreCase(orientation)) {
                this.setInitiatePortraitOrientation();
            } else if ("device".equalsIgnoreCase(orientation)) {
                if (DeviceStatus.isDeviceOrientationLocked((Context)this)) {
                    this.setRequestedOrientation(1);
                }
            } else if (this.getRequestedOrientation() == -1) {
                this.setRequestedOrientation(4);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty((CharSequence)this.mProductType) && SSAEnums.ProductType.OfferWall.toString().equalsIgnoreCase(this.mProductType)) {
            this.mState.setShouldRestore(true);
            outState.putParcelable("state", (Parcelable)this.mState);
        }
    }

    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume");
        this.MATCH_PARENT_LAYOUT_PARAMS.addRule(RelativeLayout.BELOW, R.id.va_status);
        this.MATCH_PARENT_LAYOUT_PARAMS.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.mContainer.addView((View)this.mWebViewFrameContainer, (ViewGroup.LayoutParams)this.MATCH_PARENT_LAYOUT_PARAMS);
        if (this.mWebViewController != null) {
            this.mWebViewController.registerConnectionReceiver((Context)this);
            this.mWebViewController.resume();
            this.mWebViewController.viewableChange(true, "main");
        }
        ((AudioManager)this.getSystemService("audio")).requestAudioFocus(null, 3, 2);
    }

    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause");
        ((AudioManager)this.getSystemService("audio")).abandonAudioFocus(null);
        if (this.mWebViewController != null) {
            this.mWebViewController.unregisterConnectionReceiver((Context)this);
            this.mWebViewController.pause();
            this.mWebViewController.viewableChange(false, "main");
        }
        this.removeWebViewContainerView();
    }

    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy");
        if (this.calledFromOnCreate) {
            this.removeWebViewContainerView();
        }
        if (this.mWebViewController != null) {
            this.mWebViewController.setState(IronSourceWebView.State.Gone);
            this.mWebViewController.removeVideoEventsListener();
        }
    }

    private void removeWebViewContainerView() {
        ViewGroup parent;
        View view;
        if (this.mContainer != null && (view = (parent = (ViewGroup)this.mWebViewFrameContainer.getParent()).findViewById(R.id.webview)) != null) {
            parent.removeView((View)this.mWebViewFrameContainer);
        }
    }

    @Override
    public void onCloseRequested() {
        this.finish();
    }

    @Override
    public void onOrientationChanged(String orientation, int rotation) {
        this.handleOrientationState(orientation, rotation);
    }

    @Override
    public boolean onBackButtonPressed() {
        this.onBackPressed();
        return true;
    }

    public void onBackPressed() {
        Logger.i(TAG, "onBackPressed");
        boolean isHandled = BackButtonHandler.getInstance().handleBackButton(this);
        if (!isHandled) {
            super.onBackPressed();
        }
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Logger.i(TAG, "onUserLeaveHint");
    }

    private void hideActivityTitle() {
        this.requestWindowFeature(1);
    }

    private void hideActivtiyStatusBar() {
        this.getWindow().setFlags(1024, 1024);
    }

    private void keepScreenOn() {
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                VideoActivity.this.getWindow().addFlags(128);
            }
        });
    }

    private void cancelScreenOn() {
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                VideoActivity.this.getWindow().clearFlags(128);
            }
        });
    }

    private void setInitiateLandscapeOrientation() {
        int rotation = DeviceStatus.getApplicationRotation((Context)this);
        Logger.i(TAG, "setInitiateLandscapeOrientation");
        if (rotation == 0) {
            Logger.i(TAG, "ROTATION_0");
            this.setRequestedOrientation(0);
        } else if (rotation == 2) {
            Logger.i(TAG, "ROTATION_180");
            this.setRequestedOrientation(8);
        } else if (rotation == 3) {
            Logger.i(TAG, "ROTATION_270 Right Landscape");
            this.setRequestedOrientation(8);
        } else if (rotation == 1) {
            Logger.i(TAG, "ROTATION_90 Left Landscape");
            this.setRequestedOrientation(0);
        } else {
            Logger.i(TAG, "No Rotation");
        }
    }

    private void setInitiatePortraitOrientation() {
        int rotation = DeviceStatus.getApplicationRotation((Context)this);
        Logger.i(TAG, "setInitiatePortraitOrientation");
        if (rotation == 0) {
            Logger.i(TAG, "ROTATION_0");
            this.setRequestedOrientation(1);
        } else if (rotation == 2) {
            Logger.i(TAG, "ROTATION_180");
            this.setRequestedOrientation(9);
        } else if (rotation == 1) {
            Logger.i(TAG, "ROTATION_270 Right Landscape");
            this.setRequestedOrientation(1);
        } else if (rotation == 3) {
            Logger.i(TAG, "ROTATION_90 Left Landscape");
            this.setRequestedOrientation(1);
        } else {
            Logger.i(TAG, "No Rotation");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.mWebViewController.inCustomView()) {
            this.mWebViewController.hideCustomView();
            return true;
        }
        if (this.mIsImmersive && (keyCode == 25 || keyCode == 24)) {
            this.mUiThreadHandler.removeCallbacks(this.decorViewSettings);
            this.mUiThreadHandler.postDelayed(this.decorViewSettings, 500);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (this.currentRequestedRotation != requestedOrientation) {
            Logger.i(TAG, "Rotation: Req = " + requestedOrientation + " Curr = " + this.currentRequestedRotation);
            this.currentRequestedRotation = requestedOrientation;
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (this.mIsImmersive && hasFocus) {
            this.runOnUiThread(this.decorViewSettings);
        }
    }

    @Override
    public void onVideoStarted() {
        this.toggleKeepScreen(true);
    }

    @Override
    public void onVideoPaused() {
        this.toggleKeepScreen(false);
    }

    @Override
    public void onVideoResumed() {
        this.toggleKeepScreen(true);
    }

    @Override
    public void onVideoEnded()
    {
        this.toggleKeepScreen(false);
        finish();
    }

    @Override
    public void onVideoStopped() {
        this.toggleKeepScreen(false);
    }

    public void toggleKeepScreen(boolean screenOn) {
        if (screenOn) {
            this.keepScreenOn();
        } else {
            this.cancelScreenOn();
        }
    }
}
