/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.AsyncTask
 */
package com.ironsource.sdk.utils;

import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class IronSourceAsyncHttpRequestTask
extends AsyncTask<String, Integer, Integer> {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected /* varargs */ Integer doInBackground(String ... urls) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.getInputStream();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return 1;
    }
}

