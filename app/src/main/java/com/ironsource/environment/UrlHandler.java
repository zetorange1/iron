/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.Intent
 *  android.net.Uri
 */
package com.ironsource.environment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlHandler {
    public static void openUrl(Context context, String url) throws Exception {
        if (url == null) {
            throw new Exception("url is null");
        }
        context.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse((String)url)));
    }
}

