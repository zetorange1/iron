/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.os.Handler
 *  android.os.Looper
 *  android.view.View
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.ironsource.mediationsdk.BannerAdaptersListener;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.config.AbstractAdapterConfig;
import com.ironsource.mediationsdk.config.ConfigValidationResult;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.logger.LoggingApi;
import com.ironsource.mediationsdk.model.BannerConfigurations;
import com.ironsource.mediationsdk.model.InterstitialConfigurations;
import com.ironsource.mediationsdk.model.RewardedVideoConfigurations;
import com.ironsource.mediationsdk.sdk.BannerManagerListener;
import com.ironsource.mediationsdk.sdk.BaseBannerApi;
import com.ironsource.mediationsdk.sdk.InterstitialAdapterApi;
import com.ironsource.mediationsdk.sdk.InterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialAdapterApi;
import com.ironsource.mediationsdk.sdk.RewardedInterstitialManagerListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoAdapterApi;
import com.ironsource.mediationsdk.sdk.RewardedVideoManagerListener;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractAdapter
implements InterstitialAdapterApi,
RewardedVideoAdapterApi,
LoggingApi,
RewardedInterstitialAdapterApi,
BaseBannerApi {
    private String mProviderName;
    private String mProviderUrl;
    private IronSourceLoggerManager mLoggerManager = IronSourceLoggerManager.getLogger();
    private int mInterstitialTimeout;
    private int mRewardedVideoTimeout;
    private long mBannerTimeout;
    private int mNumberOfAdsPlayed;
    private int mNumberOfVideosPlayed;
    private int mNumberOfBannersShowed;
    protected View mCurrentAdNetworkBanner;
    private int mInterstitialPriority = -1;
    private int mRewardedVideoPriority = -1;
    private int mBannerPriority = -1;
    private InterstitialConfigurations mInterstitialConfig;
    protected RewardedVideoConfigurations mRewardedVideoConfig;
    private BannerConfigurations mBannerConfig;
    private TimerTask mRVTimerTask;
    private TimerTask mISInitTimerTask;
    private TimerTask mISLoadTimerTask;
    private TimerTask mBannerInitTimerTask;
    private TimerTask mBannerLoadTimerTask;
    private String mPluginType;
    private String mPluginFrameworkVersion;
    protected RewardedInterstitialManagerListener mRewardedInterstitialManager;
    protected BannerManagerListener mBannerManager;
    protected IronSourceBannerLayout mIronSourceBanner;

    public AbstractAdapter(String providerName, String providerUrl) {
        if (providerName == null) {
            providerName = "";
        }
        if (providerUrl == null) {
            providerUrl = "";
        }
        this.mProviderName = providerName;
        this.mProviderUrl = providerUrl;
        this.mNumberOfVideosPlayed = 0;
        this.mNumberOfAdsPlayed = 0;
        this.mNumberOfBannersShowed = 0;
    }

    public int getNumberOfAdsPlayed() {
        return this.mNumberOfAdsPlayed;
    }

    public void increaseNumberOfAdsPlayed() {
        ++this.mNumberOfAdsPlayed;
    }

    public void resetNumberOfAdsPlayed() {
        this.mNumberOfAdsPlayed = 0;
    }

    public int getNumberOfVideosPlayed() {
        return this.mNumberOfVideosPlayed;
    }

    public void increaseNumberOfVideosPlayed() {
        ++this.mNumberOfVideosPlayed;
    }

    public void resetNumberOfVideosPlayed() {
        this.mNumberOfVideosPlayed = 0;
    }

    public abstract int getMaxRVAdsPerIteration();

    public abstract int getMaxISAdsPerIteration();

    public void setInterstitialTimeout(int timeout) {
        this.mInterstitialTimeout = timeout;
    }

    public void setBannerTimeout(long timeout) {
        this.mBannerTimeout = timeout;
    }

    public void setInterstitialPriority(int priority) {
        this.mInterstitialPriority = priority;
    }

    public void setBannerPriority(int priority) {
        this.mBannerPriority = priority;
    }

    public int getInterstitialPriority() {
        return this.mInterstitialPriority;
    }

    public int getBannerPriority() {
        return this.mBannerPriority;
    }

    public void setRewardedVideoTimeout(int timeout) {
        this.mRewardedVideoTimeout = timeout;
    }

    public void setRewardedVideoPriority(int priority) {
        this.mRewardedVideoPriority = priority;
    }

    public int getRewardedVideoPriority() {
        return this.mRewardedVideoPriority;
    }

    public void setInterstitialConfigurations(InterstitialConfigurations interstitialConfigurations) {
        this.mInterstitialConfig = interstitialConfigurations;
    }

    public void setBannerConfigurations(BannerConfigurations bannerConfigurations) {
        this.mBannerConfig = bannerConfigurations;
    }

    public void setRewardedVideoConfigurations(RewardedVideoConfigurations rewardedVideoConfigurations) {
        this.mRewardedVideoConfig = rewardedVideoConfigurations;
    }

    void setPluginData(String pluginType, String pluginFrameworkVersion) {
        this.mPluginType = pluginType;
        this.mPluginFrameworkVersion = pluginFrameworkVersion;
    }

    public String getPluginType() {
        return this.mPluginType;
    }

    public String getPluginFrameworkVersion() {
        return this.mPluginFrameworkVersion;
    }

    public String getProviderName() {
        return this.mProviderName;
    }

    protected void log(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
        this.mLoggerManager.onLog(tag, message, logLevel);
    }

    String getUrl() {
        return this.mProviderUrl;
    }

    protected ConfigValidationResult validateConfigBeforeInitAndCallInitFailForInvalid(AbstractAdapterConfig config, InterstitialManagerListener manager) {
        ConfigValidationResult validationResult = config.isISConfigValid();
        if (!validationResult.isValid()) {
            IronSourceError sse = validationResult.getIronSourceError();
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + sse.getErrorMessage(), 2);
            if (manager != null) {
                manager.onInterstitialInitFailed(sse, this);
            }
        }
        return validationResult;
    }

    protected ConfigValidationResult validateConfigBeforeInitAndCallAvailabilityChangedForInvalid(AbstractAdapterConfig config, RewardedVideoManagerListener manager) {
        ConfigValidationResult validationResult = config.isRVConfigValid();
        if (!validationResult.isValid()) {
            IronSourceError sse = validationResult.getIronSourceError();
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + sse.getErrorMessage(), 2);
            if (manager != null) {
                manager.onRewardedVideoAvailabilityChanged(false, this);
            }
        }
        return validationResult;
    }

    protected ConfigValidationResult validateBannerConfigBeforeInit(AbstractAdapterConfig config, BannerManagerListener manager) {
        ConfigValidationResult validationResult = config.isBannerConfigValid();
        if (!validationResult.isValid()) {
            IronSourceError sse = validationResult.getIronSourceError();
            this.log(IronSourceLogger.IronSourceTag.ADAPTER_API, this.getProviderName() + sse.getErrorMessage(), 2);
            if (manager != null) {
                manager.onBannerInitFailed(sse, this);
            }
        }
        return validationResult;
    }

    public boolean equals(Object other) {
        if (other != null && other instanceof AbstractAdapter) {
            AbstractAdapter otherAdapter = (AbstractAdapter)other;
            return this.getProviderName().equals(otherAdapter.getProviderName());
        }
        return false;
    }

    public abstract String getVersion();

    public abstract String getCoreSDKVersion();

    protected void startISInitTimer(final InterstitialManagerListener listener) {
        try {
            this.mISInitTimerTask = new TimerTask(){

                @Override
                public void run() {
                    listener.onInterstitialInitFailed(ErrorBuilder.buildInitFailedError("Timeout", "Interstitial"), AbstractAdapter.this);
                }
            };
            Timer timer = new Timer();
            if (this.mISInitTimerTask != null) {
                timer.schedule(this.mISInitTimerTask, this.mInterstitialTimeout * 1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelISInitTimer() {
        try {
            if (this.mISInitTimerTask != null) {
                this.mISInitTimerTask.cancel();
                this.mISInitTimerTask = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startISLoadTimer(final InterstitialManagerListener listener) {
        try {
            this.mISLoadTimerTask = new TimerTask(){

                @Override
                public void run() {
                    String errorString = "Interstitial Load Fail, " + AbstractAdapter.this.getProviderName() + " - " + "Timeout";
                    IronSourceError error = ErrorBuilder.buildLoadFailedError(errorString);
                    listener.onInterstitialAdLoadFailed(error, AbstractAdapter.this);
                }
            };
            Timer timer = new Timer();
            if (this.mISLoadTimerTask != null) {
                timer.schedule(this.mISLoadTimerTask, this.mInterstitialTimeout * 1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelISLoadTimer() {
        try {
            if (this.mISLoadTimerTask != null) {
                this.mISLoadTimerTask.cancel();
                this.mISLoadTimerTask = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startBannerLoadTimer(final BannerAdaptersListener listener) {
        try {
            this.mBannerLoadTimerTask = new TimerTask(){

                @Override
                public void run() {
                    String errorString = "Banner Load Fail, " + AbstractAdapter.this.getProviderName() + " - " + "Timeout";
                    IronSourceError error = ErrorBuilder.buildLoadFailedError(errorString);
                    AbstractAdapter.this.removeBannerViews();
                    listener.onBannerAdLoadFailed(error, AbstractAdapter.this);
                }
            };
            Timer timer = new Timer();
            if (this.mBannerLoadTimerTask != null) {
                timer.schedule(this.mBannerLoadTimerTask, this.mBannerTimeout);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelBannerLoadTimer() {
        try {
            if (this.mBannerLoadTimerTask != null) {
                this.mBannerLoadTimerTask.cancel();
                this.mBannerLoadTimerTask = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startRVTimer(final RewardedVideoManagerListener listener) {
        try {
            this.mRVTimerTask = new TimerTask(){

                @Override
                public void run() {
                    listener.onRewardedVideoAvailabilityChanged(false, AbstractAdapter.this);
                }
            };
            Timer rvtimer = new Timer();
            if (this.mRVTimerTask != null) {
                rvtimer.schedule(this.mRVTimerTask, this.mRewardedVideoTimeout * 1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelRVTimer() {
        try {
            if (this.mRVTimerTask != null) {
                this.mRVTimerTask.cancel();
                this.mRVTimerTask = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startBannerInitTimer(final BannerManagerListener listener) {
        try {
            this.mBannerInitTimerTask = new TimerTask(){

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onBannerInitFailed(ErrorBuilder.buildInitFailedError("Timeout", "Banner"), AbstractAdapter.this);
                    }
                }
            };
            Timer timer = new Timer();
            if (this.mBannerInitTimerTask != null) {
                timer.schedule(this.mBannerInitTimerTask, this.mBannerTimeout);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelBannerInitTimer() {
        try {
            if (this.mBannerInitTimerTask != null) {
                this.mBannerInitTimerTask.cancel();
                this.mBannerInitTimerTask = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void removeBannerViews() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){

            @Override
            public void run() {
                if (AbstractAdapter.this.mIronSourceBanner != null && AbstractAdapter.this.mCurrentAdNetworkBanner != null) {
                    AbstractAdapter.this.mIronSourceBanner.removeView(AbstractAdapter.this.mCurrentAdNetworkBanner);
                }
            }
        });
    }

    @Override
    public void setLogListener(LogListener logListener) {
    }

    @Override
    public void setRewardedInterstitialListener(RewardedInterstitialManagerListener listener) {
        this.mRewardedInterstitialManager = listener;
    }

    protected boolean isAdaptersDebugEnabled() {
        return this.mLoggerManager.isDebugEnabled();
    }

    public void initBanners(Activity activity, String appKey, String userId) {
    }

    @Override
    public IronSourceBannerLayout createBanner(Activity activity, EBannerSize size) {
        return null;
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner) {
    }

    @Override
    public void loadBanner(IronSourceBannerLayout banner, String placementName) {
    }

    @Override
    public void destroyBanner(IronSourceBannerLayout banner) {
    }

    public void setBannerListener(BannerManagerListener manager) {
    }

}

