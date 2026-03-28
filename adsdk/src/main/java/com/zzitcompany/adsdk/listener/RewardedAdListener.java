package com.zzitcompany.adsdk.listener;

/**
 * 激励视频广告监听器
 */
public interface RewardedAdListener extends AdListener {
    /**
     * 视频开始播放
     */
    default void onVideoStarted() {}
    
    /**
     * 视频播放完成
     */
    default void onVideoCompleted() {}
    
    /**
     * 激励奖励发放
     * @param rewardName 奖励名称
     * @param rewardAmount 奖励数量
     */
    void onReward(String rewardName, int rewardAmount);
    
    /**
     * 激励发放失败
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    default void onRewardFailed(int errorCode, String errorMsg) {}
}
