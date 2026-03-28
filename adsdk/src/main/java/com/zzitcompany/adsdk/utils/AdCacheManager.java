package com.zzitcompany.adsdk.utils;

import android.util.Log;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdData;
import com.zzitcompany.adsdk.model.AdType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告缓存管理器
 */
public class AdCacheManager {
    
    private static final String TAG = "AdCacheManager";
    
    private final AdSdkConfig config;
    private final Map<AdType, Queue<AdData>> adCache;
    private final Map<AdType, Long> lastLoadTime;
    
    private static volatile AdCacheManager instance;
    
    private AdCacheManager(AdSdkConfig config) {
        this.config = config;
        this.adCache = new ConcurrentHashMap<>();
        this.lastLoadTime = new ConcurrentHashMap<>();
        
        // 初始化各类型广告的缓存队列
        for (AdType type : AdType.values()) {
            adCache.put(type, new LinkedList<>());
        }
    }
    
    public static AdCacheManager getInstance() {
        if (instance == null) {
            synchronized (AdCacheManager.class) {
                if (instance == null) {
                    instance = new AdCacheManager(AdSdkConfig.Builder::new);
                }
            }
        }
        return instance;
    }
    
    public static void init(AdSdkConfig config) {
        if (instance == null) {
            synchronized (AdCacheManager.class) {
                if (instance == null) {
                    instance = new AdCacheManager(config);
                }
            }
        }
    }
    
    /**
     * 缓存广告
     */
    public void cacheAd(AdData adData) {
        if (adData == null || adData.getAdType() == null) {
            return;
        }
        
        Queue<AdData> queue = adCache.get(adData.getAdType());
        if (queue != null) {
            synchronized (queue) {
                // 检查缓存数量限制
                if (queue.size() >= config.getPreloadCount()) {
                    queue.poll(); // 移除最旧的
                }
                queue.offer(adData);
                lastLoadTime.put(adData.getAdType(), System.currentTimeMillis());
                Log.d(TAG, "Cached ad: " + adData.getAdId() + ", type: " + adData.getAdType());
            }
        }
    }
    
    /**
     * 获取缓存的广告
     */
    public AdData getCachedAd(AdType adType) {
        Queue<AdData> queue = adCache.get(adType);
        if (queue != null) {
            synchronized (queue) {
                AdData ad = queue.poll();
                if (ad != null && !isExpired(ad)) {
                    Log.d(TAG, "Got cached ad: " + ad.getAdId());
                    return ad;
                }
            }
        }
        return null;
    }
    
    /**
     * 检查是否有缓存广告
     */
    public boolean hasCachedAd(AdType adType) {
        Queue<AdData> queue = adCache.get(adType);
        if (queue != null) {
            synchronized (queue) {
                for (AdData ad : queue) {
                    if (!isExpired(ad)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 获取缓存数量
     */
    public int getCachedCount(AdType adType) {
        Queue<AdData> queue = adCache.get(adType);
        if (queue != null) {
            synchronized (queue) {
                return queue.size();
            }
        }
        return 0;
    }
    
    /**
     * 清除指定类型广告缓存
     */
    public void clearCache(AdType adType) {
        Queue<AdData> queue = adCache.get(adType);
        if (queue != null) {
            synchronized (queue) {
                queue.clear();
            }
        }
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        for (Queue<AdData> queue : adCache.values()) {
            synchronized (queue) {
                queue.clear();
            }
        }
    }
    
    /**
     * 检查广告是否过期
     */
    private boolean isExpired(AdData ad) {
        Long loadTime = lastLoadTime.get(ad.getAdType());
        if (loadTime == null) {
            return false;
        }
        return System.currentTimeMillis() - loadTime > config.getCacheExpireTimeMs();
    }
    
    /**
     * 获取所有缓存状态
     */
    public List<String> getCacheStatus() {
        List<String> status = new ArrayList<>();
        for (AdType type : AdType.values()) {
            status.add(type.getValue() + ": " + getCachedCount(type));
        }
        return status;
    }
}
