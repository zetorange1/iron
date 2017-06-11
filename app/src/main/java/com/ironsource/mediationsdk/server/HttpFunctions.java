/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 */
package com.ironsource.mediationsdk.server;

import android.text.TextUtils;
import com.ironsource.mediationsdk.IronSourceObject;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpFunctions {
    private static final int SERVER_REQUEST_TIMEOUT = 15000;
    private static final String SERVER_REQUEST_GET_METHOD = "GET";
    private static final String SERVER_REQUEST_POST_METHOD = "POST";
    private static final String SERVER_REQUEST_ENCODING = "UTF-8";
    public static final String ERROR_PREFIX = "ERROR:";
    private static final String SERVER_BAD_REQUEST_ERROR = "Bad Request - 400";

    public static String getStringFromURL(String link) throws Exception {
        return HttpFunctions.getStringFromURL(link, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getStringFromURL(String link, IronSourceObject.IResponseListener listener) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            String line;
            URL requestURL = new URL(link);
            conn = (HttpURLConnection)requestURL.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                if (listener != null) {
                    listener.onUnrecoverableError("Bad Request - 400");
                }
                String string = null;
                return string;
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String result = stringBuilder.toString();
            if (TextUtils.isEmpty((CharSequence)result)) {
                String string = null;
                return string;
            }
            String string = result;
            return string;
        }
        catch (Exception e) {
            String responseCode = null;
            return responseCode;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean getStringFromPostWithAutho(String url, String json, String userName, String password) {
        OutputStream os = null;
        HttpURLConnection conn = null;
        try {
            URL requestURL = new URL(url);
            String authorizationString = IronSourceUtils.getBase64Auth(userName, password);
            conn = (HttpURLConnection)requestURL.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", authorizationString);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            int responseCode = conn.getResponseCode();
            boolean bl = responseCode == 200;
            return bl;
        }
        catch (Exception e) {
            boolean authorizationString = false;
            return authorizationString;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}

