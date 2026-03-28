package com.zzitcompany.adsdk.model;

/**
 * 广告加载状态
 */
public enum AdLoadState {
    /**
     * 空闲状态
     */
    IDLE,
    
    /**
     * 加载中
     */
    LOADING,
    
    /**
     * 加载成功
     */
    LOADED,
    
    /**
     * 加载失败
     */
    FAILED,
    
    /**
     * 已展示
     */
    SHOWN,
    
    /**
     * 已关闭
     */
    CLOSED
}
