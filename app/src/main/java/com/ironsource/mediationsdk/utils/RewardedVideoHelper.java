/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.mediationsdk.utils;

public class RewardedVideoHelper {
    private Boolean mVideoAvailability;
    private int mCurrentVideosPresented;
    private int mMaxVideosPerSession;
    private String mPlacementName;

    public RewardedVideoHelper() {
        this.initState();
    }

    private void initState() {
        this.mVideoAvailability = null;
        this.mCurrentVideosPresented = 0;
        this.mMaxVideosPerSession = 0;
        this.mPlacementName = "";
    }

    public String getPlacementName() {
        return this.mPlacementName;
    }

    public void setPlacementName(String placementName) {
        this.mPlacementName = placementName;
    }

    public void reset() {
        this.initState();
    }

    public void setMaxVideo(int maxVideo) {
        this.mMaxVideosPerSession = maxVideo;
    }

    public synchronized boolean increaseCurrentVideo() {
        ++this.mCurrentVideosPresented;
        boolean canShowMore = this.canShowVideoInCurrentSession();
        return this.setVideoAvailability(canShowMore && this.isVideoAvailable());
    }

    public synchronized boolean isVideoAvailable() {
        boolean result = false;
        if (this.mVideoAvailability != null) {
            result = this.mVideoAvailability;
        }
        return result;
    }

    public synchronized boolean setVideoAvailability(boolean availability) {
        boolean shouldNotify = false;
        boolean bl = availability = availability && this.canShowVideoInCurrentSession();
        if (this.mVideoAvailability == null || this.mVideoAvailability != availability) {
            this.mVideoAvailability = availability;
            shouldNotify = true;
        }
        return shouldNotify;
    }

    public boolean canShowVideoInCurrentSession() {
        return this.mCurrentVideosPresented < this.mMaxVideosPerSession;
    }
}

