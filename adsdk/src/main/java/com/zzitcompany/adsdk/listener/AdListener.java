package com.zzitcompany.adsdk.listener;

import com.zzitcompany.adsdk.model.AdData;

/**
 * 基础广告监听器
 */
public interface AdListener {
    /**
     * 广告加载开始
     */
    default void onAdLoadStart() {}
    
    /**
     * 广告加载成功
     * @param adData 广告数据
     */
    void onAdLoaded(AdData adData);
    
    /**
     * 广告加载失败
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    void onAdLoadFailed(int errorCode, String errorMsg);
    
    /**
     * 广告展示
     */
    default void onAdShown() {}
    
    /**
     * 广告曝光
     */
    default void onAdImpression() {}
    
    /**
     * 广告点击
     */
    default void onAdClicked() {}
    
    /**
     * 广告关闭
     */
    default void onAdClosed() {}
    
    /**
     * 广告跳过
     */
    default void onAdSkipped() {}
}
