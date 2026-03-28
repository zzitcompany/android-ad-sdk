package com.zzitcompany.adsdk.model;

import java.util.List;

/**
 * 广告数据模型
 */
public class AdData {
    /**
     * 广告ID
     */
    private String adId;
    
    /**
     * 广告类型
     */
    private AdType adType;
    
    /**
     * 广告标题
     */
    private String title;
    
    /**
     * 广告描述
     */
    private String description;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 视频URL（激励视频使用）
     */
    private String videoUrl;
    
    /**
     * 点击跳转URL
     */
    private String clickUrl;
    
    /**
     * 关闭按钮显示延迟（毫秒）
     */
    private long closeDelayMs;
    
    /**
     * 广告展示时长（毫秒）
     */
    private long displayDurationMs;
    
    /**
     * 激励奖励名称
     */
    private String rewardName;
    
    /**
     * 激励奖励数量
     */
    private int rewardAmount;
    
    /**
     * 用户标签（用于定向投放）
     */
    private List<String> targetTags;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public long getCloseDelayMs() {
        return closeDelayMs;
    }

    public void setCloseDelayMs(long closeDelayMs) {
        this.closeDelayMs = closeDelayMs;
    }

    public long getDisplayDurationMs() {
        return displayDurationMs;
    }

    public void setDisplayDurationMs(long displayDurationMs) {
        this.displayDurationMs = displayDurationMs;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public List<String> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<String> targetTags) {
        this.targetTags = targetTags;
    }
}
