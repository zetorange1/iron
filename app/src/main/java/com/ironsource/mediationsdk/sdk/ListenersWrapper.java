/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.Handler
 *  android.os.Looper
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.sdk;

import android.os.Handler;
import android.os.Looper;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.events.InterstitialEventsManager;
import com.ironsource.mediationsdk.events.RewardedVideoEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InternalOfferwallListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ListenersWrapper
implements RewardedVideoListener,
InterstitialListener,
InternalOfferwallListener,
RewardedInterstitialListener {
    private RewardedVideoListener mRewardedVideoListener;
    private InterstitialListener mInterstitialListener;
    private OfferwallListener mOfferwallListener;
    private RewardedInterstitialListener mRewardedInterstitialListener;
    private CallbackHandlerThread mCallbackHandlerThread;

    public ListenersWrapper() {
        this.mCallbackHandlerThread = new CallbackHandlerThread();
        this.mCallbackHandlerThread.start();
    }

    private boolean canSendCallback(Object productListener) {
        return productListener != null && this.mCallbackHandlerThread != null;
    }

    private void sendCallback(Runnable callbackRunnable) {
        if (this.mCallbackHandlerThread == null) {
            return;
        }
        Handler callbackHandler = this.mCallbackHandlerThread.getCallbackHandler();
        if (callbackHandler != null) {
            callbackHandler.post(callbackRunnable);
        }
    }

    public void setRewardedVideoListener(RewardedVideoListener rewardedVideoListener) {
        this.mRewardedVideoListener = rewardedVideoListener;
    }

    public void setInterstitialListener(InterstitialListener interstitialListener) {
        this.mInterstitialListener = interstitialListener;
    }

    public void setOfferwallListener(OfferwallListener offerwallListener) {
        this.mOfferwallListener = offerwallListener;
    }

    public void setRewardedInterstitialListener(RewardedInterstitialListener rewardedInterstitialListener) {
        this.mRewardedInterstitialListener = rewardedInterstitialListener;
    }

    @Override
    public void onRewardedVideoAdOpened() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdOpened()", 1);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdOpened();
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAdClosed() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdClosed()", 1);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdClosed();
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(final boolean available) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAvailabilityChanged(available:" + available + ")", 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("status", (Object)String.valueOf(available));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(7, data);
        RewardedVideoEventsManager.getInstance().log(event);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAvailabilityChanged(available);
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAdStarted() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdStarted()", 1);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdStarted();
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAdEnded() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdEnded()", 1);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdEnded();
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAdRewarded(final Placement placement) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdRewarded(" + placement.toString() + ")", 1);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdRewarded(placement);
                }
            });
        }
    }

    @Override
    public void onRewardedVideoAdShowFailed(final IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onRewardedVideoAdShowFailed(" + error.toString() + ")", 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("status", (Object)"false");
            if (error.getErrorCode() == 524) {
                data.put("reason", 1);
            }
            data.put("errorCode", error.getErrorCode());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(17, data);
        RewardedVideoEventsManager.getInstance().log(event);
        if (this.canSendCallback(this.mRewardedVideoListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedVideoListener.onRewardedVideoAdShowFailed(error);
                }
            });
        }
    }

    @Override
    public void onInterstitialAdReady() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdReady()", 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("status", (Object)"true");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(27, data);
        InterstitialEventsManager.getInstance().log(event);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdReady();
                }
            });
        }
    }

    @Override
    public void onInterstitialAdLoadFailed(final IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdLoadFailed(" + error + ")", 1);
        if (error != null && 520 != error.getErrorCode()) {
            JSONObject data = IronSourceUtils.getMediationAdditionalData();
            try {
                data.put("status", (Object)"false");
                data.put("errorCode", error.getErrorCode());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            EventData event = new EventData(27, data);
            InterstitialEventsManager.getInstance().log(event);
        }
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdLoadFailed(error);
                }
            });
        }
    }

    @Override
    public void onInterstitialAdOpened() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdOpened()", 1);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdOpened();
                }
            });
        }
    }

    @Override
    public void onInterstitialAdShowSucceeded() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdShowSucceeded()", 1);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdShowSucceeded();
                }
            });
        }
    }

    @Override
    public void onInterstitialAdShowFailed(final IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdShowFailed(" + error + ")", 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            if (error.getErrorCode() == 524) {
                data.put("reason", 1);
            }
            data.put("errorCode", error.getErrorCode());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(29, data);
        InterstitialEventsManager.getInstance().log(event);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdShowFailed(error);
                }
            });
        }
    }

    @Override
    public void onInterstitialAdClicked() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdClicked()", 1);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdClicked();
                }
            });
        }
    }

    @Override
    public void onInterstitialAdClosed() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdClosed()", 1);
        if (this.canSendCallback(this.mInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mInterstitialListener.onInterstitialAdClosed();
                }
            });
        }
    }

    @Override
    public void onOfferwallOpened() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onOfferwallOpened()", 1);
        if (this.canSendCallback(this.mOfferwallListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mOfferwallListener.onOfferwallOpened();
                }
            });
        }
    }

    @Override
    public void onOfferwallShowFailed(final IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onOfferwallShowFailed(" + error + ")", 1);
        if (this.canSendCallback(this.mOfferwallListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mOfferwallListener.onOfferwallShowFailed(error);
                }
            });
        }
    }

    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        boolean result = false;
        if (this.mOfferwallListener != null) {
            result = this.mOfferwallListener.onOfferwallAdCredited(credits, totalCredits, totalCreditsFlag);
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onOfferwallAdCredited(credits:" + credits + ", " + "totalCredits:" + totalCredits + ", " + "totalCreditsFlag:" + totalCreditsFlag + "):" + result, 1);
        return result;
    }

    @Override
    public void onGetOfferwallCreditsFailed(final IronSourceError error) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onGetOfferwallCreditsFailed(" + error + ")", 1);
        if (this.canSendCallback(this.mOfferwallListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mOfferwallListener.onGetOfferwallCreditsFailed(error);
                }
            });
        }
    }

    @Override
    public void onOfferwallClosed() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onOfferwallClosed()", 1);
        if (this.canSendCallback(this.mOfferwallListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mOfferwallListener.onOfferwallClosed();
                }
            });
        }
    }

    @Override
    public void onOfferwallAvailable(boolean isAvailable) {
        this.onOfferwallAvailable(isAvailable, null);
    }

    @Override
    public void onOfferwallAvailable(final boolean isAvailable, IronSourceError error) {
        String logString = "onOfferwallAvailable(isAvailable: " + isAvailable + ")";
        if (error != null) {
            logString = logString + ", error: " + error.getErrorMessage();
        }
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, logString, 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            data.put("status", (Object)String.valueOf(isAvailable));
            if (error != null) {
                data.put("errorCode", error.getErrorCode());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(302, data);
        RewardedVideoEventsManager.getInstance().log(event);
        if (this.canSendCallback(this.mOfferwallListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mOfferwallListener.onOfferwallAvailable(isAvailable);
                }
            });
        }
    }

    @Override
    public void onInterstitialAdRewarded() {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.CALLBACK, "onInterstitialAdRewarded()", 1);
        if (this.canSendCallback(this.mRewardedInterstitialListener)) {
            this.sendCallback(new Runnable(){

                @Override
                public void run() {
                    ListenersWrapper.this.mRewardedInterstitialListener.onInterstitialAdRewarded();
                }
            });
        }
    }

    private class CallbackHandlerThread
    extends Thread {
        private Handler mCallbackHandler;

        private CallbackHandlerThread() {
        }

        @Override
        public void run() {
            Looper.prepare();
            this.mCallbackHandler = new Handler();
            Looper.loop();
        }

        public Handler getCallbackHandler() {
            return this.mCallbackHandler;
        }
    }

}

