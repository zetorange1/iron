/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.os.CountDownTimer
 *  android.os.Handler
 *  android.os.HandlerThread
 *  android.os.Looper
 *  android.text.TextUtils
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import com.ironsource.environment.DeviceStatus;
import com.ironsource.environment.NetworkStateReceiver;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.sdk.GeneralProperties;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediationInitializer
implements NetworkStateReceiver.NetworkStateReceiverListener {
    private static MediationInitializer sInstance;
    private final String GENERAL_PROPERTIES_USER_ID = "userId";
    private final String GENERAL_PROPERTIES_APP_KEY = "appKey";
    private final String TAG;
    private int mRetryDelay;
    private int mRetryCounter;
    private int mRetryLimit;
    private int mRetryGrowLimit;
    private int mRetryAvailabilityLimit;
    private boolean mIsRevived;
    private boolean mDidReportInitialAvailability;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private boolean mListenForInit;
    private AtomicBoolean mAtomicShouldPerformInit;
    private NetworkStateReceiver mNetworkStateReceiver;
    private CountDownTimer mCountDownTimer;
    private List<OnMediationInitializationListener> mOnMediationInitializationListeners;
    private Activity mActivity;
    private String mUserId;
    private String mAppKey;
    private ServerResponseWrapper mServerResponseWrapper;
    private EInitStatus mInitStatus;
    private String mUserIdType;
    private InitRunnable initRunnable;

    public static synchronized MediationInitializer getInstance() {
        if (sInstance == null) {
            sInstance = new MediationInitializer();
        }
        return sInstance;
    }

    private MediationInitializer() {
        this.TAG = this.getClass().getSimpleName();
        this.mDidReportInitialAvailability = false;
        this.mHandlerThread = null;
        this.mListenForInit = false;
        this.mOnMediationInitializationListeners = new ArrayList<OnMediationInitializationListener>();
        this.initRunnable = new InitRunnable(){

            @Override
            public void run() {
                try {
                    IronSourceObject ironSourceObject = IronSourceObject.getInstance();
                    ConfigValidationResult validationResult = MediationInitializer.this.validateUserId(MediationInitializer.this.mUserId);
                    if (validationResult.isValid()) {
                        MediationInitializer.this.mUserIdType = "userGenerated";
                    } else {
                        MediationInitializer.this.mUserId = ironSourceObject.getAdvertiserId((Context)MediationInitializer.this.mActivity);
                        if (!TextUtils.isEmpty((CharSequence)MediationInitializer.this.mUserId)) {
                            MediationInitializer.this.mUserIdType = "GAID";
                        } else {
                            MediationInitializer.this.mUserId = DeviceStatus.getOrGenerateOnceUniqueIdentifier((Context)MediationInitializer.this.mActivity);
                            if (!TextUtils.isEmpty((CharSequence)MediationInitializer.this.mUserId)) {
                                MediationInitializer.this.mUserIdType = "UUID";
                            } else {
                                MediationInitializer.this.mUserId = "";
                            }
                        }
                        ironSourceObject.setIronSourceUserId(MediationInitializer.this.mUserId);
                    }
                    GeneralProperties.getProperties().putKey("userIdType", MediationInitializer.this.mUserIdType);
                    if (!TextUtils.isEmpty((CharSequence)MediationInitializer.this.mUserId)) {
                        GeneralProperties.getProperties().putKey("userId", MediationInitializer.this.mUserId);
                    }
                    if (!TextUtils.isEmpty((CharSequence)MediationInitializer.this.mAppKey)) {
                        GeneralProperties.getProperties().putKey("appKey", MediationInitializer.this.mAppKey);
                    }
                    MediationInitializer.this.mServerResponseWrapper = ironSourceObject.getServerResponse((Context)MediationInitializer.this.mActivity, MediationInitializer.this.mUserId, this.listener);
                    if (MediationInitializer.this.mServerResponseWrapper != null) {
                        MediationInitializer.this.mHandler.removeCallbacks((Runnable)this);
                        if (MediationInitializer.this.mServerResponseWrapper.isValidResponse()) {
                            MediationInitializer.this.setInitStatus(EInitStatus.INITIATED);
                            List<IronSource.AD_UNIT> adUnits = MediationInitializer.this.mServerResponseWrapper.getInitiatedAdUnits();
                            for (OnMediationInitializationListener listener : MediationInitializer.this.mOnMediationInitializationListeners) {
                                listener.onInitSuccess(adUnits, MediationInitializer.this.wasInitRevived());
                            }
                        } else if (!MediationInitializer.this.mDidReportInitialAvailability) {
                            MediationInitializer.this.setInitStatus(EInitStatus.INIT_FAILED);
                            MediationInitializer.this.mDidReportInitialAvailability = true;
                            for (OnMediationInitializationListener listener : MediationInitializer.this.mOnMediationInitializationListeners) {
                                listener.onInitFailed("serverResponseIsNotValid");
                            }
                        }
                    } else {
                        if (this.isRecoverable && MediationInitializer.this.mRetryCounter < MediationInitializer.this.mRetryLimit) {
                            MediationInitializer.this.mIsRevived = true;
                            MediationInitializer.this.mHandler.postDelayed((Runnable)this, (long)(MediationInitializer.this.mRetryDelay * 1000));
                            if (MediationInitializer.this.mRetryCounter < MediationInitializer.this.mRetryGrowLimit) {
                                MediationInitializer.this.mRetryDelay = MediationInitializer.this.mRetryDelay * 2;
                            }
                        }
                        if (!(this.isRecoverable && MediationInitializer.this.mRetryCounter != MediationInitializer.this.mRetryAvailabilityLimit || MediationInitializer.this.mDidReportInitialAvailability)) {
                            MediationInitializer.this.mDidReportInitialAvailability = true;
                            if (TextUtils.isEmpty((CharSequence)this.reason)) {
                                this.reason = "noServerResponse";
                            }
                            for (OnMediationInitializationListener listener : MediationInitializer.this.mOnMediationInitializationListeners) {
                                listener.onInitFailed(this.reason);
                            }
                            MediationInitializer.this.setInitStatus(EInitStatus.INIT_FAILED);
                            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, "Mediation availability false reason: No server response", 1);
                        }
                        MediationInitializer.this.mRetryCounter++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.mInitStatus = EInitStatus.NOT_INIT;
        this.mHandlerThread = new HandlerThread("IronSourceInitiatorHandler");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mRetryDelay = 1;
        this.mRetryCounter = 0;
        this.mRetryLimit = 62;
        this.mRetryGrowLimit = 12;
        this.mRetryAvailabilityLimit = 5;
        this.mAtomicShouldPerformInit = new AtomicBoolean(true);
        this.mIsRevived = false;
    }

    private synchronized void setInitStatus(EInitStatus status) {
        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.INTERNAL, "setInitStatus(old status: " + (Object)((Object)this.mInitStatus) + ", new status: " + (Object)((Object)status) + ")", 0);
        this.mInitStatus = status;
    }

    public synchronized /* varargs */ void init(Activity activity, String appKey, String userId, IronSource.AD_UNIT ... adUnits) {
        try {
            if (this.mAtomicShouldPerformInit != null && this.mAtomicShouldPerformInit.compareAndSet(true, false)) {
                this.setInitStatus(EInitStatus.INIT_IN_PROGRESS);
                this.mActivity = activity;
                this.mUserId = userId;
                this.mAppKey = appKey;
                if (IronSourceUtils.isNetworkConnected((Context)activity)) {
                    this.mHandler.post((Runnable)this.initRunnable);
                } else {
                    this.mListenForInit = true;
                    if (this.mNetworkStateReceiver == null) {
                        this.mNetworkStateReceiver = new NetworkStateReceiver((Context)activity, this);
                    }
                    activity.getApplicationContext().registerReceiver((BroadcastReceiver)this.mNetworkStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                    new Handler(Looper.getMainLooper()).post(new Runnable(){

                        @Override
                        public void run() {
                            MediationInitializer.this.mCountDownTimer = new CountDownTimer(60000, 60000){

                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    if (!MediationInitializer.this.mDidReportInitialAvailability) {
                                        MediationInitializer.this.mDidReportInitialAvailability = true;
                                        for (OnMediationInitializationListener listener : MediationInitializer.this.mOnMediationInitializationListeners) {
                                            listener.onInitFailed("noInternetConnection");
                                        }
                                        MediationInitializer.this.setInitStatus(EInitStatus.INIT_FAILED);
                                        IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, "Mediation availability false reason: No internet connection", 1);
                                    }
                                }
                            }.start();
                        }

                    });
                }
            } else {
                IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.API, this.TAG + ": Multiple calls to init are not allowed", 2);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkAvailabilityChanged(boolean connected) {
        if (this.mListenForInit && connected) {
            if (this.mCountDownTimer != null) {
                this.mCountDownTimer.cancel();
            }
            this.mListenForInit = false;
            this.mIsRevived = true;
            this.mHandler.post((Runnable)this.initRunnable);
        }
    }

    private boolean wasInitRevived() {
        return this.mIsRevived;
    }

    public synchronized EInitStatus getCurrentInitStatus() {
        return this.mInitStatus;
    }

    public void addMediationInitializationListener(OnMediationInitializationListener listener) {
        if (listener == null) {
            return;
        }
        this.mOnMediationInitializationListeners.add(listener);
    }

    public void removeMediationInitializationListener(OnMediationInitializationListener listener) {
        if (listener == null || this.mOnMediationInitializationListeners.size() == 0) {
            return;
        }
        this.mOnMediationInitializationListeners.remove(listener);
    }

    private ConfigValidationResult validateUserId(String userId) {
        ConfigValidationResult result = new ConfigValidationResult();
        if (userId != null) {
            if (!this.validateLength(userId, 1, 64)) {
                IronSourceError error = ErrorBuilder.buildInvalidCredentialsError("userId", userId, null);
                result.setInvalid(error);
            }
        } else {
            IronSourceError error = ErrorBuilder.buildInvalidCredentialsError("userId", userId, "it's missing");
            result.setInvalid(error);
        }
        return result;
    }

    private boolean validateLength(String key, int minLength, int maxLength) {
        if (key == null) {
            return false;
        }
        return key.length() >= minLength && key.length() <= maxLength;
    }

    public abstract class InitRunnable
    implements Runnable {
        protected boolean isRecoverable;
        protected String reason;
        protected IronSourceObject.IResponseListener listener;

        public InitRunnable() {
            this.isRecoverable = true;
            this.listener = new IronSourceObject.IResponseListener(){

                @Override
                public void onUnrecoverableError(String errorMessage) {
                    InitRunnable.this.isRecoverable = false;
                    InitRunnable.this.reason = errorMessage;
                }
            };
        }

    }

    public static interface OnMediationInitializationListener {
        public void onInitSuccess(List<IronSource.AD_UNIT> var1, boolean var2);

        public void onInitFailed(String var1);
    }

    public static enum EInitStatus {
        NOT_INIT,
        INIT_IN_PROGRESS,
        INIT_FAILED,
        INITIATED;
        

        private EInitStatus() {
        }
    }

}

