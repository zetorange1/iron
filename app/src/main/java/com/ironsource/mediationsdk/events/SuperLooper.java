/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.Handler
 *  android.os.HandlerThread
 *  android.os.Looper
 */
package com.ironsource.mediationsdk.events;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.ironsource.mediationsdk.logger.ThreadExceptionHandler;

public class SuperLooper
extends Thread {
    private SupersonicSdkThread mSdkThread;
    private static SuperLooper mInstance;

    private SuperLooper() {
        this.mSdkThread = new SupersonicSdkThread(this.getClass().getSimpleName());
        this.mSdkThread.start();
        this.mSdkThread.prepareHandler();
    }

    public static synchronized SuperLooper getLooper() {
        if (mInstance == null) {
            mInstance = new SuperLooper();
        }
        return mInstance;
    }

    public synchronized void post(Runnable runnable) {
        if (this.mSdkThread == null) {
            return;
        }
        Handler callbackHandler = this.mSdkThread.getCallbackHandler();
        if (callbackHandler != null) {
            callbackHandler.post(runnable);
        }
    }

    private class SupersonicSdkThread
    extends HandlerThread {
        private Handler mHandler;

        public SupersonicSdkThread(String name) {
            super(name);
            this.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new ThreadExceptionHandler());
        }

        public void prepareHandler() {
            this.mHandler = new Handler(this.getLooper());
        }

        public Handler getCallbackHandler() {
            return this.mHandler;
        }
    }

}

