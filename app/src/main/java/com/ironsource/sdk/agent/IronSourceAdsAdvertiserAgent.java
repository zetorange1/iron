/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.text.TextUtils
 */
package com.ironsource.sdk.agent;

import android.content.Context;
import android.text.TextUtils;
import com.ironsource.environment.ApplicationContext;
import com.ironsource.sdk.SSAAdvertiserTest;
import com.ironsource.sdk.data.SSAObj;
import com.ironsource.sdk.utils.IronSourceSharedPrefHelper;
import com.ironsource.sdk.utils.Logger;
import com.ironsource.sdk.utils.SDKUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class IronSourceAdsAdvertiserAgent
implements SSAAdvertiserTest {
    private static final String TAG = "IronSourceAdsAdvertiserAgent";
    private static String SERVICE_PROTOCOL = "https";
    private static String SERVICE_HOST_NAME = "www.supersonicads.com";
    private static int SERVICE_PORT = 443;
    private static String TIME_API = "https://www.supersonicads.com/timestamp.php";
    private static String PACKAGE_NAME = null;
    private static final String DOMAIN = "/campaigns/onLoad?";
    private static final String BUNDLE_ID = "bundleId";
    private static final String DEVICE_IDS = "deviceIds";
    private static final String SIGNATURE = "signature";
    public static IronSourceAdsAdvertiserAgent sInstance;

    private IronSourceAdsAdvertiserAgent() {
    }

    public static synchronized IronSourceAdsAdvertiserAgent getInstance() {
        Logger.i("IronSourceAdsAdvertiserAgent", "getInstance()");
        if (sInstance == null) {
            sInstance = new IronSourceAdsAdvertiserAgent();
        }
        return sInstance;
    }

    @Override
    public void reportAppStarted(final Context context) {
        if (IronSourceSharedPrefHelper.getSupersonicPrefHelper(context).getReportAppStarted()) {
            return;
        }
        new Thread(new Runnable(){

            @Override
            public void run() {
                String reqParams = IronSourceAdsAdvertiserAgent.this.getRequestParameters(context);
                String file = "/campaigns/onLoad?" + reqParams;
                try {
                    URL requestURL = new URL(SERVICE_PROTOCOL, SERVICE_HOST_NAME, SERVICE_PORT, file);
                    Result result = IronSourceAdsAdvertiserAgent.this.performRequest(requestURL, context);
                    if (result.getResponseCode() == 200) {
                        IronSourceSharedPrefHelper.getSupersonicPrefHelper(context).setReportAppStarted(true);
                    }
                }
                catch (MalformedURLException result) {
                    // empty catch block
                }
            }
        }).start();
    }

    @Override
    public void setDomain(String protocol, String host, int port) {
        SERVICE_PROTOCOL = protocol;
        SERVICE_HOST_NAME = host;
        SERVICE_PORT = port;
    }

    @Override
    public void setTimeAPI(String url) {
        TIME_API = url;
    }

    @Override
    public void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
    }

    @Override
    public void clearReportApp(Context context) {
        IronSourceSharedPrefHelper.getSupersonicPrefHelper(context).setReportAppStarted(false);
    }

    private String getRequestParameters(Context context) {
        StringBuilder parameters = new StringBuilder();
        String pckName = TextUtils.isEmpty((CharSequence)PACKAGE_NAME) ? ApplicationContext.getPackageName(context) : PACKAGE_NAME;
        if (!TextUtils.isEmpty((CharSequence)pckName)) {
            parameters.append("&").append("bundleId").append("=").append(SDKUtils.encodeString(pckName));
        }
        SDKUtils.loadGoogleAdvertiserInfo(context);
        String advertiserId = SDKUtils.getAdvertiserId();
        boolean isLAT = SDKUtils.isLimitAdTrackingEnabled();
        if (!TextUtils.isEmpty((CharSequence)advertiserId)) {
            parameters.append("&").append("deviceIds").append(SDKUtils.encodeString("[")).append(SDKUtils.encodeString("AID")).append(SDKUtils.encodeString("]")).append("=").append(SDKUtils.encodeString(advertiserId));
            parameters.append("&").append(SDKUtils.encodeString("isLimitAdTrackingEnabled")).append("=").append(SDKUtils.encodeString(Boolean.toString(isLAT)));
        } else {
            advertiserId = "";
        }
        StringBuilder signature = new StringBuilder();
        signature.append(pckName);
        signature.append(advertiserId);
        int timeStamp = this.getUTCTimeStamp(context);
        signature.append(timeStamp);
        String md5 = SDKUtils.getMD5(signature.toString());
        parameters.append("&").append("signature").append("=").append(md5);
        return parameters.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Result performRequest(URL url, Context context) {
        Result requestResult;
        requestResult = new Result(this);
        HttpURLConnection connection = null;
        int responseCode = 0;
        InputStream is = null;
        StringBuilder builder = null;
        try {
            url.toURI();
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            responseCode = connection.getResponseCode();
            is = connection.getInputStream();
            byte[] buffer5 = new byte[102400];
            boolean bytesRead = false;
            boolean totalBytesRead = false;
            builder = new StringBuilder();
            String line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            if (!totalBytesRead) {
                // empty if block
            }
        }
        catch (MalformedURLException buffer5) {}
        catch (URISyntaxException buffer5) {}
        catch (SocketTimeoutException buffer5) {}
        catch (FileNotFoundException buffer5) {}
        catch (IOException buffer5) {}
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException buffer5) {
                // empty catch block
            }
            if (responseCode != 200) {
                Logger.i("IronSourceAdsAdvertiserAgent", " RESPONSE CODE: " + responseCode + " URL: " + url);
            }
            if (connection != null) {
                connection.disconnect();
            }
            requestResult.setResponseCode(responseCode);
            if (builder == null) {
                requestResult.setResponseString("empty");
            } else {
                requestResult.setResponseString(builder.toString());
            }
        }
        return requestResult;
    }

    private int getUTCTimeStamp(Context context) {
        try {
            String json;
            SSAObj ssaObj;
            URL url = new URL(TIME_API);
            Result result = this.performRequest(url, context);
            if (result.getResponseCode() == 200 && (ssaObj = new SSAObj(json = result.getResponseString())).containsKey("timestamp")) {
                String timestamp = ssaObj.getString("timestamp");
                int time = Integer.parseInt(timestamp);
                int utc = time - time % 60;
                return utc;
            }
        }
        catch (MalformedURLException result) {
            // empty catch block
        }
        return 0;
    }

    private class Result {
        private int mResponseCode;
        private String mResponseString;
        final /* synthetic */ IronSourceAdsAdvertiserAgent this$0;

        public Result(IronSourceAdsAdvertiserAgent ironSourceAdsAdvertiserAgent) {
            this.this$0 = ironSourceAdsAdvertiserAgent;
        }

        public Result(IronSourceAdsAdvertiserAgent ironSourceAdsAdvertiserAgent, int responseCode, String responseString) {
            this.this$0 = ironSourceAdsAdvertiserAgent;
            this.setResponseCode(responseCode);
            this.setResponseString(responseString);
        }

        public int getResponseCode() {
            return this.mResponseCode;
        }

        public void setResponseCode(int responseCode) {
            this.mResponseCode = responseCode;
        }

        public String getResponseString() {
            return this.mResponseString;
        }

        public void setResponseString(String responseString) {
            this.mResponseString = responseString;
        }
    }

    public static final class SuperSonicAdsAdvertiserException
    extends RuntimeException {
        private static final long serialVersionUID = 8169178234844720921L;

        public SuperSonicAdsAdvertiserException(Throwable t) {
            super(t);
        }
    }

}

