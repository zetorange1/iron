/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.text.TextUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import com.ironsource.eventsmodule.EventData;
import com.ironsource.mediationsdk.AbstractAdUnitManager;
import com.ironsource.mediationsdk.AbstractAdapter;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.events.RewardedVideoEventsManager;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.model.Configurations;
import com.ironsource.mediationsdk.model.OfferwallConfigurations;
import com.ironsource.mediationsdk.model.OfferwallPlacement;
import com.ironsource.mediationsdk.model.ProviderSettings;
import com.ironsource.mediationsdk.model.ProviderSettingsHolder;
import com.ironsource.mediationsdk.sdk.InternalOfferwallApi;
import com.ironsource.mediationsdk.sdk.InternalOfferwallListener;
import com.ironsource.mediationsdk.sdk.OfferwallApi;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.utils.ErrorBuilder;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.ironsource.mediationsdk.utils.ServerResponseWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;

class OfferwallManager
extends AbstractAdUnitManager
implements InternalOfferwallApi,
InternalOfferwallListener {
    private final String TAG;
    private final String GENERAL_PROPERTIES_USER_ID = "userId";
    private OfferwallApi mAdapter;
    private InternalOfferwallListener mListenersWrapper;
    private IronSourceLoggerManager mLoggerManager;
    private AtomicBoolean mAtomicShouldPerformInit;
    private AtomicBoolean mIsOfferwallAvailable;
    private ServerResponseWrapper mServerResponseWrapper;
    private String mCurrentPlacementName;

    public OfferwallManager() {
        this.TAG = this.getClass().getName();
        this.mAtomicShouldPerformInit = new AtomicBoolean(true);
        this.mIsOfferwallAvailable = new AtomicBoolean(false);
        this.mLoggerManager = IronSourceLoggerManager.getLogger();
    }

    @Override
    void shouldTrackNetworkState(Context context, boolean track) {
    }

    @Override
    boolean isPremiumAdapter(String providerName) {
        return false;
    }

    @Override
    boolean isBackFillAvailable() {
        return false;
    }

    @Override
    public synchronized void initOfferwall(Activity activity, String appKey, String userId) {
        ArrayList<AbstractAdapter> startedAdapters;
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.NATIVE, this.TAG + ":initOfferwall(appKey: " + appKey + ", userId: " + userId + ")", 1);
        this.mAppKey = appKey;
        this.mUserId = userId;
        this.mActivity = activity;
        this.mServerResponseWrapper = IronSourceObject.getInstance().getCurrentServerResponse();
        if (this.mServerResponseWrapper != null && ((startedAdapters = this.startAdapters(activity, userId, this.mServerResponseWrapper)) == null || startedAdapters.isEmpty())) {
            IronSourceError initFailedError = ErrorBuilder.buildInitFailedError("Please check configurations for Offerwall adapters", "Offerwall");
            this.reportInitFail(initFailedError);
        }
    }

    private synchronized void reportInitFail(IronSourceError error) {
        if (this.mIsOfferwallAvailable != null) {
            this.mIsOfferwallAvailable.set(false);
        }
        if (this.mAtomicShouldPerformInit != null) {
            this.mAtomicShouldPerformInit.set(true);
        }
        if (this.mListenersWrapper != null) {
            this.mListenersWrapper.onOfferwallAvailable(false, error);
        }
    }

    private ArrayList<AbstractAdapter> startAdapters(Activity activity, String userId, ServerResponseWrapper serverResponseWrapper) {
        ArrayList<AbstractAdapter> adapterList = new ArrayList<AbstractAdapter>();
        ProviderSettingsHolder holder = serverResponseWrapper.getProviderSettingsHolder();
        ProviderSettings settings = holder.getProviderSettings("SupersonicAds");
        String providerName = "SupersonicAds";
        String requestUrl = settings.getRewardedVideoSettings().optString("requestUrl");
        try {
            Class mAdapterClass;
            Method startAdapterMethod;
            IronSourceObject sso = IronSourceObject.getInstance();
            AbstractAdapter providerAdapter = sso.getExistingAdapter(providerName);
            if (providerAdapter == null && (providerAdapter = (AbstractAdapter)(startAdapterMethod = (mAdapterClass = Class.forName("com.ironsource.adapters." + providerName.toLowerCase() + "." + providerName + "Adapter")).getMethod("startAdapter", String.class, String.class)).invoke(mAdapterClass, providerName, requestUrl)) != null) {
                sso.addToAdaptersList(providerAdapter);
            }
            this.setCustomParams(providerAdapter);
            providerAdapter.setLogListener(this.mLoggerManager);
            ((InternalOfferwallApi)((Object)providerAdapter)).setInternalOfferwallListener(this);
            this.addOfferwallAdapter((OfferwallApi)((Object)providerAdapter));
            String appKey = IronSourceObject.getInstance().getIronSourceAppKey();
            ((OfferwallApi)((Object)providerAdapter)).initOfferwall(activity, appKey, userId);
            adapterList.add(providerAdapter);
        }
        catch (Throwable e) {
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.API, providerName + " initialization failed - please verify that required dependencies are in you build path.", 2);
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.API, this.TAG + ":startAdapter", e);
        }
        return adapterList;
    }

    private void addOfferwallAdapter(OfferwallApi adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void onResume(Activity activity) {
    }

    @Override
    public void onPause(Activity activity) {
    }

    @Override
    public void setAge(int age) {
    }

    @Override
    public void setGender(String gender) {
    }

    @Override
    public void setMediationSegment(String segment) {
    }

    @Override
    public void showOfferwall() {
    }

    @Override
    public void showOfferwall(String placementName) {
        String logMessage = "OWManager:showOfferwall(" + placementName + ")";
        try {
            this.mCurrentPlacementName = placementName;
            OfferwallPlacement placement = this.mServerResponseWrapper.getConfigurations().getOfferwallConfigurations().getOfferwallPlacement(placementName);
            if (placement == null) {
                String noPlacementMessage = "Placement is not valid, please make sure you are using the right placements, using the default placement.";
                this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, noPlacementMessage, 3);
                placement = this.mServerResponseWrapper.getConfigurations().getOfferwallConfigurations().getDefaultOfferwallPlacement();
                if (placement == null) {
                    String noDefaultPlacement = "Default placement was not found, please make sure you are using the right placements.";
                    this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, noDefaultPlacement, 3);
                    return;
                }
            }
            this.mLoggerManager.log(IronSourceLogger.IronSourceTag.INTERNAL, logMessage, 1);
            if (this.mIsOfferwallAvailable != null && this.mIsOfferwallAvailable.get() && this.mAdapter != null) {
                this.mAdapter.showOfferwall(String.valueOf(placement.getPlacementId()));
            }
        }
        catch (Exception e) {
            this.mLoggerManager.logException(IronSourceLogger.IronSourceTag.INTERNAL, logMessage, e);
        }
    }

    @Override
    public synchronized boolean isOfferwallAvailable() {
        boolean result = false;
        if (this.mIsOfferwallAvailable != null) {
            result = this.mIsOfferwallAvailable.get();
        }
        return result;
    }

    @Override
    public void getOfferwallCredits() {
        if (this.mAdapter != null) {
            this.mAdapter.getOfferwallCredits();
        }
    }

    @Override
    public void setOfferwallListener(OfferwallListener offerwallListener) {
    }

    @Override
    public void setInternalOfferwallListener(InternalOfferwallListener listener) {
        this.mListenersWrapper = listener;
    }

    @Override
    public void onOfferwallAvailable(boolean isAvailable) {
        this.onOfferwallAvailable(isAvailable, null);
    }

    @Override
    public void onOfferwallAvailable(boolean isAvailable, IronSourceError error) {
        String logString = "onOfferwallAvailable(isAvailable: " + isAvailable + ")";
        if (error != null) {
            logString = logString + ", error: " + error.getErrorMessage();
        }
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, logString, 1);
        if (isAvailable) {
            this.mIsOfferwallAvailable.set(true);
            this.mListenersWrapper.onOfferwallAvailable(isAvailable);
        } else {
            this.reportInitFail(error);
        }
    }

    @Override
    public void onOfferwallOpened() {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onOfferwallOpened()", 1);
        JSONObject data = IronSourceUtils.getMediationAdditionalData();
        try {
            if (!TextUtils.isEmpty((CharSequence)this.mCurrentPlacementName)) {
                data.put("placement", (Object)this.mCurrentPlacementName);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        EventData event = new EventData(305, data);
        RewardedVideoEventsManager.getInstance().log(event);
        this.mListenersWrapper.onOfferwallOpened();
    }

    @Override
    public void onOfferwallShowFailed(IronSourceError error) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onOfferwallShowFailed(" + error + ")", 1);
        this.mListenersWrapper.onOfferwallShowFailed(error);
    }

    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        return this.mListenersWrapper.onOfferwallAdCredited(credits, totalCredits, totalCreditsFlag);
    }

    @Override
    public void onGetOfferwallCreditsFailed(IronSourceError error) {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onGetOfferwallCreditsFailed(" + error + ")", 1);
        this.mListenersWrapper.onGetOfferwallCreditsFailed(error);
    }

    @Override
    public void onOfferwallClosed() {
        this.mLoggerManager.log(IronSourceLogger.IronSourceTag.ADAPTER_CALLBACK, "onOfferwallClosed()", 1);
        this.mListenersWrapper.onOfferwallClosed();
    }
}

