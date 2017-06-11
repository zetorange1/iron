/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.net.ConnectivityManager
 *  android.net.NetworkInfo
 *  android.os.Bundle
 */
package com.ironsource.environment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class NetworkStateReceiver
extends BroadcastReceiver {
    private ConnectivityManager mManager;
    private NetworkStateReceiverListener mListener;
    private boolean mConnected;

    public NetworkStateReceiver(Context context, NetworkStateReceiverListener listener) {
        this.mListener = listener;
        this.mManager = (ConnectivityManager)context.getSystemService("connectivity");
        this.checkAndSetState();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        if (this.checkAndSetState()) {
            this.notifyState();
        }
    }

    private boolean checkAndSetState() {
        boolean prev = this.mConnected;
        NetworkInfo activeNetwork = this.mManager.getActiveNetworkInfo();
        this.mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != this.mConnected;
    }

    private void notifyState() {
        if (this.mListener != null) {
            if (this.mConnected) {
                this.mListener.onNetworkAvailabilityChanged(true);
            } else {
                this.mListener.onNetworkAvailabilityChanged(false);
            }
        }
    }

    public static interface NetworkStateReceiverListener {
        public void onNetworkAvailabilityChanged(boolean var1);
    }

}

