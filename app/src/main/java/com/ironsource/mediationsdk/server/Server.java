/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  org.json.JSONObject
 */
package com.ironsource.mediationsdk.server;

import android.util.Log;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.IronSourceLoggerManager;
import com.ironsource.mediationsdk.logger.ThreadExceptionHandler;
import com.ironsource.mediationsdk.server.HttpFunctions;
import com.ironsource.mediationsdk.server.ServerURL;
import org.json.JSONObject;

public class Server {
    private static void callRequestURL(String requestUrl, boolean hit, int placementId) {
        try {
            String url = ServerURL.getRequestURL(requestUrl, hit, placementId);
            String json = HttpFunctions.getStringFromURL(url);
            JSONObject obj = new JSONObject(json);
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NETWORK, "callRequestURL(reqUrl:" + requestUrl + ", " + "hit:" + hit + ")", 1);
        }
        catch (Throwable e) {
            StringBuilder builder = new StringBuilder("callRequestURL(reqUrl:");
            if (requestUrl == null) {
                builder.append("null");
            } else {
                builder.append(requestUrl);
            }
            builder.append(", hit:").append(hit).append(")");
            IronSourceLoggerManager.getLogger().log(IronSourceLogger.IronSourceTag.NETWORK, builder.toString() + ", e:" + Log.getStackTraceString((Throwable)e), 0);
        }
    }

    public static void callAsyncRequestURL(final String requestUrl, final boolean hit, final int placementId) {
        Thread asyncRequestURL = new Thread(new Runnable(){

            @Override
            public void run() {
                Server.callRequestURL(requestUrl, hit, placementId);
            }
        }, "callAsyncRequestURL");
        asyncRequestURL.setUncaughtExceptionHandler(new ThreadExceptionHandler());
        asyncRequestURL.start();
    }

}

