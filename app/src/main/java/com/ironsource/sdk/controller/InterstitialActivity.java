/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.ironsource.sdk.controller;

import android.os.Bundle;
import com.ironsource.sdk.controller.ControllerActivity;
import com.ironsource.sdk.utils.Logger;

public class InterstitialActivity
extends ControllerActivity {
    private static final String TAG = ControllerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause");
    }
}

