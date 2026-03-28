package com.zzitcompany.adsdk.loader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.listener.RewardedAdListener;
import com.zzitcompany.adsdk.mock.MockAdProvider;
import com.zzitcompany.adsdk.model.AdData;
import com.zzitcompany.adsdk.model.AdLoadState;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdCacheManager;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 广告加载器基类
 */
public abstract class BaseAdLoader {
    
    protected final String TAG = this.getClass().getSimpleName();
    
    protected final Context context;
    protected final AdSdkConfig config;
    protected final AdType adType;
    protected final ExecutorService executorService;
    protected final Handler mainHandler;
    
    protected AdLoadState loadState = AdLoadState.IDLE;
    protected AdData currentAd;
    protected AdListener adListener;
    
    protected BaseAdLoader(Context context, AdType adType, AdSdkConfig config) {
        this.context = context.getApplicationContext();
        this.adType = adType;
        this.config = config;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * 设置广告监听器
     */
    public void setAdListener(AdListener listener) {
        this.adListener = listener;
    }
    
    /**
     * 加载广告
     */
    public void loadAd() {
        loadAd(null);
    }
    
    /**
     * 加载广告（带广告ID）
     */
    public void loadAd(String adId) {
        if (loadState == AdLoadState.LOADING) {
            Log.w(TAG, "Ad is already loading");
            return;
        }
        
        // 先检查缓存
        if (config.isAutoPreload()) {
            AdData cachedAd = AdCacheManager.getInstance().getCachedAd(adType);
            if (cachedAd != null) {
                onAdLoadedInternal(cachedAd);
                return;
            }
        }
        
        loadState = AdLoadState.LOADING;
        notifyLoadStart();
        
        executorService.execute(() -> {
            try {
                // 模拟网络延迟
                Thread.sleep(200 + (long) (Math.random() * 300));
                
                // 从Mock提供器获取广告数据
                AdData adData = MockAdProvider.getAdByType(adType);
                
                mainHandler.post(() -> {
                    if (adData != null) {
                        onAdLoadedInternal(adData);
                    } else {
                        onAdLoadFailedInternal(-1, "Failed to load ad");
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading ad", e);
                mainHandler.post(() -> onAdLoadFailedInternal(-1, e.getMessage()));
            }
        });
    }
    
    /**
     * 预加载广告
     */
    public void preloadAd() {
        executorService.execute(() -> {
            try {
                AdData adData = MockAdProvider.getAdByType(adType);
                if (adData != null) {
                    AdCacheManager.getInstance().cacheAd(adData);
                    Log.d(TAG, "Preloaded ad: " + adData.getAdId());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error preloading ad", e);
            }
        });
    }
    
    /**
     * 检查广告是否已加载
     */
    public boolean isAdLoaded() {
        return loadState == AdLoadState.LOADED && currentAd != null;
    }
    
    /**
     * 获取当前加载状态
     */
    public AdLoadState getLoadState() {
        return loadState;
    }
    
    /**
     * 获取当前广告数据
     */
    public AdData getCurrentAd() {
        return currentAd;
    }
    
    /**
     * 销毁广告
     */
    public void destroy() {
        loadState = AdLoadState.IDLE;
        currentAd = null;
        adListener = null;
    }
    
    /**
     * 广告加载成功内部处理
     */
    protected void onAdLoadedInternal(AdData adData) {
        loadState = AdLoadState.LOADED;
        currentAd = adData;
        
        if (adListener != null) {
            adListener.onAdLoaded(adData);
        }
        
        // 自动预加载下一个
        if (config.isAutoPreload()) {
            preloadAd();
        }
    }
    
    /**
     * 广告加载失败内部处理
     */
    protected void onAdLoadFailedInternal(int errorCode, String errorMsg) {
        loadState = AdLoadState.FAILED;
        
        if (adListener != null) {
            adListener.onAdLoadFailed(errorCode, errorMsg);
        }
    }
    
    /**
     * 通知广告开始加载
     */
    protected void notifyLoadStart() {
        if (adListener != null) {
            adListener.onAdLoadStart();
        }
    }
    
    /**
     * 通知广告展示
     */
    protected void notifyAdShown() {
        if (currentAd != null) {
            AdFrequencyManager.getInstance().recordAdShow(currentAd);
        }
        
        if (adListener != null) {
            adListener.onAdShown();
        }
    }
    
    /**
     * 通知广告曝光
     */
    protected void notifyAdImpression() {
        if (adListener != null) {
            adListener.onAdImpression();
        }
    }
    
    /**
     * 通知广告点击
     */
    protected void notifyAdClicked() {
        if (adListener != null) {
            adListener.onAdClicked();
        }
    }
    
    /**
     * 通知广告关闭
     */
    protected void notifyAdClosed() {
        loadState = AdLoadState.CLOSED;
        
        if (adListener != null) {
            adListener.onAdClosed();
        }
    }
    
    /**
     * 通知广告跳过
     */
    protected void notifyAdSkipped() {
        if (adListener != null) {
            adListener.onAdSkipped();
        }
    }
}
