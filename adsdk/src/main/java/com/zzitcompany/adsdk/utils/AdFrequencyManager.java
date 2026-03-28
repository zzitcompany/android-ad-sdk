package com.zzitcompany.adsdk.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告频控管理器
 */
public class AdFrequencyManager {
    
    private static final String KEY_PREFIX = "ad_freq_";
    private static final String KEY_GLOBAL_PREFIX = "ad_global_freq_";
    
    private final AdSdkConfig config;
    private final Map<String, Long> lastShowTimeMap;
    
    private static volatile AdFrequencyManager instance;
    
    private AdFrequencyManager(AdSdkConfig config) {
        this.config = config;
        this.lastShowTimeMap = new ConcurrentHashMap<>();
    }
    
    public static AdFrequencyManager getInstance() {
        if (instance == null) {
            synchronized (AdFrequencyManager.class) {
                if (instance == null) {
                    instance = new AdFrequencyManager(new AdSdkConfig.Builder().build());
                }
            }
        }
        return instance;
    }
    
    public static void init(AdSdkConfig config) {
        if (instance == null) {
            synchronized (AdFrequencyManager.class) {
                if (instance == null) {
                    instance = new AdFrequencyManager(config);
                }
            }
        }
    }
    
    /**
     * 检查广告是否可以展示（频控检查）
     */
    public boolean canShowAd(AdData adData) {
        if (!config.isFrequencyCapEnabled()) {
            return true;
        }
        
        // 检查同一广告ID的频控
        if (!checkAdIdFrequency(adData.getAdId())) {
            return false;
        }
        
        // 检查同一广告类型的全局频控
        if (!checkAdTypeFrequency(adData.getAdType().getValue())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 记录广告展示
     */
    public void recordAdShow(AdData adData) {
        long currentTime = System.currentTimeMillis();
        String adId = adData.getAdId();
        String adType = adData.getAdType().getValue();
        
        // 记录广告ID展示时间
        lastShowTimeMap.put(KEY_PREFIX + adId, currentTime);
        
        // 记录广告类型全局展示时间
        lastShowTimeMap.put(KEY_GLOBAL_PREFIX + adType, currentTime);
    }
    
    /**
     * 检查特定广告ID的频控
     */
    private boolean checkAdIdFrequency(String adId) {
        String key = KEY_PREFIX + adId;
        Long lastShowTime = lastShowTimeMap.get(key);
        
        if (lastShowTime == null) {
            return true;
        }
        
        long elapsed = System.currentTimeMillis() - lastShowTime;
        return elapsed >= config.getFrequencyIntervalMs();
    }
    
    /**
     * 检查广告类型的全局频控
     */
    private boolean checkAdTypeFrequency(String adType) {
        String key = KEY_GLOBAL_PREFIX + adType;
        Long lastShowTime = lastShowTimeMap.get(key);
        
        if (lastShowTime == null) {
            return true;
        }
        
        long elapsed = System.currentTimeMillis() - lastShowTime;
        // 同类型广告展示间隔为配置时间的一半
        return elapsed >= config.getFrequencyIntervalMs() / 2;
    }
    
    /**
     * 清除频控记录
     */
    public void clearFrequencyRecords() {
        lastShowTimeMap.clear();
    }
    
    /**
     * 清除特定广告的频控记录
     */
    public void clearAdFrequencyRecord(String adId) {
        lastShowTimeMap.remove(KEY_PREFIX + adId);
    }
}
