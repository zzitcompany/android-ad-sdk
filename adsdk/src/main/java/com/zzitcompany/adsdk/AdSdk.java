package com.zzitcompany.adsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.loader.BannerAdLoader;
import com.zzitcompany.adsdk.loader.FeedAdLoader;
import com.zzitcompany.adsdk.loader.InterstitialAdLoader;
import com.zzitcompany.adsdk.loader.RewardedVideoAdLoader;
import com.zzitcompany.adsdk.loader.SplashAdLoader;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdCacheManager;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;

/**
 * SDK入口类
 */
public class AdSdk {
    
    private static final String TAG = "AdSdk";
    
    private static volatile AdSdk instance;
    private static boolean isInitialized = false;
    
    private final Context context;
    private final AdSdkConfig config;
    private final Handler mainHandler;
    
    private SplashAdLoader splashAdLoader;
    private BannerAdLoader bannerAdLoader;
    private FeedAdLoader feedAdLoader;
    private InterstitialAdLoader interstitialAdLoader;
    private RewardedVideoAdLoader rewardedVideoAdLoader;
    
    private AdSdk(Context context, AdSdkConfig config) {
        this.context = context.getApplicationContext();
        this.config = config;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * 初始化SDK
     * @param context Application Context
     * @param config SDK配置
     */
    public static synchronized void init(Context context, AdSdkConfig config) {
        if (isInitialized) {
            Log.w(TAG, "AdSdk already initialized");
            return;
        }
        
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        
        instance = new AdSdk(context, config);
        isInitialized = true;
        
        // 初始化缓存管理器
        AdCacheManager.init(config);
        
        // 初始化频控管理器
        AdFrequencyManager.init(config);
        
        Log.i(TAG, "AdSdk initialized successfully");
    }
    
    /**
     * 初始化SDK（使用默认配置）
     */
    public static synchronized void init(Context context) {
        init(context, new AdSdkConfig.Builder().build());
    }
    
    /**
     * 获取SDK实例
     */
    public static AdSdk getInstance() {
        if (!isInitialized) {
            throw new IllegalStateException("AdSdk not initialized. Call init() first.");
        }
        return instance;
    }
    
    /**
     * 检查SDK是否已初始化
     */
    public static boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * 获取开屏广告加载器
     */
    public SplashAdLoader getSplashAdLoader() {
        if (splashAdLoader == null) {
            splashAdLoader = new SplashAdLoader(context, config);
        }
        return splashAdLoader;
    }
    
    /**
     * 获取横幅广告加载器
     */
    public BannerAdLoader getBannerAdLoader() {
        if (bannerAdLoader == null) {
            bannerAdLoader = new BannerAdLoader(context, config);
        }
        return bannerAdLoader;
    }
    
    /**
     * 获取信息流广告加载器
     */
    public FeedAdLoader getFeedAdLoader() {
        if (feedAdLoader == null) {
            feedAdLoader = new FeedAdLoader(context, config);
        }
        return feedAdLoader;
    }
    
    /**
     * 获取插屏广告加载器
     */
    public InterstitialAdLoader getInterstitialAdLoader() {
        if (interstitialAdLoader == null) {
            interstitialAdLoader = new InterstitialAdLoader(context, config);
        }
        return interstitialAdLoader;
    }
    
    /**
     * 获取激励视频广告加载器
     */
    public RewardedVideoAdLoader getRewardedVideoAdLoader() {
        if (rewardedVideoAdLoader == null) {
            rewardedVideoAdLoader = new RewardedVideoAdLoader(context, config);
        }
        return rewardedVideoAdLoader;
    }
    
    /**
     * 预加载所有类型广告
     */
    public void preloadAllAds() {
        preloadAd(AdType.SPLASH);
        preloadAd(AdType.BANNER);
        preloadAd(AdType.FEED);
        preloadAd(AdType.INTERSTITIAL);
        preloadAd(AdType.REWARDED_VIDEO);
    }
    
    /**
     * 预加载指定类型广告
     */
    public void preloadAd(AdType adType) {
        switch (adType) {
            case SPLASH:
                getSplashAdLoader().preloadAd();
                break;
            case BANNER:
                getBannerAdLoader().preloadAd();
                break;
            case FEED:
                getFeedAdLoader().preloadAd();
                break;
            case INTERSTITIAL:
                getInterstitialAdLoader().preloadAd();
                break;
            case REWARDED_VIDEO:
                getRewardedVideoAdLoader().preloadAd();
                break;
        }
    }
    
    /**
     * 清除所有广告缓存
     */
    public void clearAllCache() {
        AdCacheManager.getInstance().clearAllCache();
    }
    
    /**
     * 清除频控记录
     */
    public void clearFrequencyRecords() {
        AdFrequencyManager.getInstance().clearFrequencyRecords();
    }
    
    /**
     * 获取SDK配置
     */
    public AdSdkConfig getConfig() {
        return config;
    }
    
    /**
     * 销毁所有广告
     */
    public void destroy() {
        if (splashAdLoader != null) {
            splashAdLoader.destroy();
            splashAdLoader = null;
        }
        if (bannerAdLoader != null) {
            bannerAdLoader.destroy();
            bannerAdLoader = null;
        }
        if (feedAdLoader != null) {
            feedAdLoader.destroy();
            feedAdLoader = null;
        }
        if (interstitialAdLoader != null) {
            interstitialAdLoader.destroy();
            interstitialAdLoader = null;
        }
        if (rewardedVideoAdLoader != null) {
            rewardedVideoAdLoader.destroy();
            rewardedVideoAdLoader = null;
        }
        
        AdCacheManager.getInstance().clearAllCache();
        
        Log.i(TAG, "AdSdk destroyed");
    }
}
