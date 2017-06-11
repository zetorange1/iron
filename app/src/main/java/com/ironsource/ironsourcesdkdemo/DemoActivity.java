package com.ironsource.ironsourcesdkdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.sdk.SSAFactory;
import com.ironsource.sdk.agent.IronSourceAdsPublisherAgent;
import com.ironsource.sdk.controller.IronSourceWebView;
import com.ironsource.sdk.controller.VideoEventsListener;
import com.ironsource.sdk.listeners.OnWebViewChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DemoActivity extends Activity implements RewardedVideoListener {

    private final String TAG = "DemoActivity";
    private final String APP_KEY = "4f7e8e5d";
    private final String userID = "176797418"; // 175764341  176797418
    // private String userID = "";
    private final String FALLBACK_USER_ID = "userId";
    private Button mVideoButton;
    private EditText mUsername;
    private EditText mPassword;
    private TextView mStatus;
    public static TextView mHidden;
    private String mStrUsername = "";
    private String mStrPassword = "";
    private boolean mIsCreate;

    private Placement mPlacement;

    private FrameLayout mBannerParentLayout;
    private IronSourceBannerLayout mIronSourceBannerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        //The integrationHelper is used to validate the integration. Remove the integrationHelper before going live!
        IntegrationHelper.validateIntegration(this);
        initUIElements();
        startIronSourceInitTask();
        // mIsCreate = true;
        mIsCreate = false;

    }
    private void startIronSourceInitTask(){

        // getting advertiser id should be done on a background thread
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return IronSource.getAdvertiserId(DemoActivity.this);
            }
            @Override
            protected void onPostExecute(String advertisingId) {
                if (TextUtils.isEmpty(advertisingId)) {
                    advertisingId = FALLBACK_USER_ID;
                }
                // we're using an advertisingId as the 'userId'
                initIronSource(APP_KEY, userID);
            }
        };
        task.execute();
    }

    private void initIronSource(String appKey, String userId) {
        // IronSource Advertiser SDK call
        SSAFactory.getAdvertiserInstance().reportAppStarted(this);
        // Be sure to set a listener to each product that is being initiated
        // set the IronSource rewarded video listener
        IronSource.setRewardedVideoListener(this);
        // set client side callbacks for the offerwall
        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);

        // set the IronSource user id
        IronSource.setUserId(userId);
        // init the IronSource SDK
        IronSource.init(this, appKey);

        updateButtonsState();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // call the IronSource onResume method
        IronSource.onResume(this);
        updateButtonsState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call the IronSource onPause method
        IronSource.onPause(this);
        updateButtonsState();
    }

    /**
     * Handle the button state according to the status of the IronSource producs
     */
    private void updateButtonsState() {
            handleVideoButtonState(IronSource.isRewardedVideoAvailable());

    }



    /**
     * initialize the UI elements of the activity
     */
    private void initUIElements() {
        mUsername = (EditText) findViewById(R.id.rv_username);
        mPassword = (EditText) findViewById(R.id.rv_password);
        mStatus = (TextView) findViewById(R.id.status);
        mHidden = (TextView) findViewById(R.id.hidden);
        mVideoButton = (Button) findViewById(R.id.rv_button);
        // mVideoButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         mHidden.setText("");
        //         final String username = mUsername.getText().toString();
        //         final String password = mPassword.getText().toString();
        //         if (username.isEmpty() || password.isEmpty()) {
        //             mStatus.setText("Login Failed!");
        //             return;
        //         }
        //         mStatus.setText("");

        //         new AsyncTask<Void, Void, String>() {
        //             @Override
        //             protected String doInBackground(Void... urls) {
        //                 StringBuilder sb = new StringBuilder();;
        //                 try {
        //                     if (mStrUsername.equals(username) && mStrPassword.equals(password)) {
        //                         return "same";
        //                     }
        //                     URL url;
        //                     url = new URL("https://api.imvu.com/login");
        //                     HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        //                     urlConn.setDoInput(true);
        //                     urlConn.setDoOutput(true);
        //                     urlConn.setRequestProperty("Content-Type", "application/json");
        //                     urlConn.setRequestProperty("Host", "api.imvu.com");
        //                     urlConn.setRequestMethod("POST");
        //                     JSONObject jsonParam = new JSONObject();
        //                     jsonParam.put("username", username);
        //                     jsonParam.put("password", password);
        //                     OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
        //                     wr.write(jsonParam.toString());
        //                     wr.flush();

        //                     BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));
        //                     String line = null;
        //                     while ((line = br.readLine()) != null) {
        //                         sb.append(line + "\n");
        //                     }
        //                     br.close();
        //                     wr.close();
        //                     mStrUsername = username;
        //                     mStrPassword = password;
        //                 } catch (Exception e) {
        //                     e.printStackTrace();
        //                     return "";
        //                 }
        //                 return sb.toString();
        //             }
        //             @Override
        //             protected void onPostExecute(String response) {
        //                 if (response.isEmpty()) {
        //                     mStatus.setText("Login Failed!");
        //                     return;
        //                 }
        //                 if (!response.equals("same")) {
        //                     JSONObject js;
        //                     try {
        //                         js = new JSONObject(response).getJSONObject("denormalized");
        //                         userID = js.getJSONObject(js.keys().next()).getJSONObject("data").getJSONObject("user").getString("id").replace("https://api.imvu.com/users/cid/", "");
        //                         Log.w("AAAAAA", userID);
        //                         startIronSourceInitTask();
        //                     } catch (JSONException e) {
        //                         e.printStackTrace();
        //                         mStatus.setText("Login Failed!");
        //                         return;
        //                     }
        //                 }
        //                 if (mIsCreate) {
        //                     startIronSourceInitTask();
        //                     mIsCreate = false;
        //                 }
        //                 if (!userID.isEmpty() && IronSource.isRewardedVideoAvailable())
        //                     IronSource.showRewardedVideo();
        //             }
        //         }.execute();
        //     }
        // });

        mBannerParentLayout = (FrameLayout) findViewById(R.id.banner_footer);

        // In order to work with IronSourceBanners you need to add Providers who support banner ad unit and uncomment next line
        // createAndloadBanner();
    }


    /**
     * Creates and loads IronSource Banner
     *
     */
    private void createAndloadBanner() {
        // choose banner size
        EBannerSize size = EBannerSize.BANNER;

        // instantiate IronSourceBanner object, using the IronSource.createBanner API
        mIronSourceBannerLayout = IronSource.createBanner(this, size);

        // add IronSourceBanner to your container
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mBannerParentLayout.addView(mIronSourceBannerLayout, 0, layoutParams);

        if (mIronSourceBannerLayout != null) {
            // set the banner listener
            mIronSourceBannerLayout.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    Log.d(TAG, "onBannerAdLoaded");
                    // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                    mBannerParentLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {
                    Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                }

                @Override
                public void onBannerAdClicked() {
                    Log.d(TAG, "onBannerAdClicked");
                }

                @Override
                public void onBannerAdScreenPresented() {
                    Log.d(TAG, "onBannerAdScreenPresented");
                }

                @Override
                public void onBannerAdScreenDismissed() {
                    Log.d(TAG, "onBannerAdScreenDismissed");
                }

                @Override
                public void onBannerAdLeftApplication() {
                    Log.d(TAG, "onBannerAdLeftApplication");
                }
            });

            // load ad into the created banner
            IronSource.loadBanner(mIronSourceBannerLayout);
        } else {
            Toast.makeText(DemoActivity.this, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Destroys IronSource Banner and removes it from the container
     *
     */
    private void destroyAndDetachBanner() {
        IronSource.destroyBanner(mIronSourceBannerLayout);
        if (mBannerParentLayout != null) {
            mBannerParentLayout.removeView(mIronSourceBannerLayout);
        }
    }

    /**
     * Set the Rewareded Video button state according to the product's state
     *
     * @param available if the video is available
     */
    public void handleVideoButtonState(final boolean available) {
        final String text;
        final int color;
        if (available || mIsCreate) {
            color = Color.BLUE;
            text = getResources().getString(R.string.show);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.rv);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoButton.setTextColor(color);
                mVideoButton.setText(text);
                mVideoButton.setEnabled(available || mIsCreate);

            }
        });
    }


    // --------- IronSource Rewarded Video Listener ---------

    @Override
    public void onRewardedVideoAdOpened() {
        // called when the video is opened
        Log.d(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // called when the video is closed
        Log.d(TAG, "onRewardedVideoAdClosed");
        // here we show a dialog to the user if he was rewarded
        if (mPlacement != null) {
            // if the user was rewarded
//            showRewardDialog(mPlacement);
            mPlacement = null;
        }
        if (IronSource.isRewardedVideoAvailable() && mHidden.getText().toString().isEmpty())
            IronSource.showRewardedVideo();
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
        // called when the video availbility has changed
        Log.d(TAG, "onRewardedVideoAvailabilityChanged" + " " + b);
        handleVideoButtonState(b);
        if (b && mHidden.getText().toString().isEmpty())
            IronSource.showRewardedVideo();
    }

    @Override
    public void onRewardedVideoAdStarted() {
        // called when the video has started
        Log.d(TAG, "onRewardedVideoAdStarted");
    }

    @Override
    public void onRewardedVideoAdEnded() {
        // called when the video has ended
        Log.d(TAG, "onRewardedVideoAdEnded");
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
        // called when the video has been rewarded and a reward can be given to the user
        Log.d(TAG, "onRewardedVideoAdRewarded" + " " + placement);
        mPlacement = placement;

    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
        // called when the video has failed to show
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onRewardedVideoAdShowFailed" + " " + ironSourceError);
    }

    public void showRewardDialog(Placement placement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DemoActivity.this);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle(getResources().getString(R.string.rewarded_dialog_header));
        builder.setMessage(getResources().getString(R.string.rewarded_dialog_message) + " " + placement.getRewardAmount() + " " + placement.getRewardName());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
