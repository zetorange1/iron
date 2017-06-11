/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.controller;

public interface VideoEventsListener {
    public void onVideoStarted();

    public void onVideoPaused();

    public void onVideoResumed();

    public void onVideoEnded();

    public void onVideoStopped();
}

