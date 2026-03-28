package com.zzitcompany.adsdk.model;

/**
 * 广告统计数据
 */
public class AdStatistics {
    private String adId;
    private AdType adType;
    private long impressionTime;
    private long clickTime;
    private long closeTime;
    private boolean isClicked;
    private boolean isCompleted;
    private long viewDurationMs;

    public AdStatistics() {
    }

    public AdStatistics(String adId, AdType adType) {
        this.adId = adId;
        this.adType = adType;
    }

    // Getters and Setters
    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public AdType getAdType() {
        return adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public long getImpressionTime() {
        return impressionTime;
    }

    public void setImpressionTime(long impressionTime) {
        this.impressionTime = impressionTime;
    }

    public long getClickTime() {
        return clickTime;
    }

    public void setClickTime(long clickTime) {
        this.clickTime = clickTime;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getViewDurationMs() {
        return viewDurationMs;
    }

    public void setViewDurationMs(long viewDurationMs) {
        this.viewDurationMs = viewDurationMs;
    }
}
