package com.zzitcompany.adsdk.loader;

import android.content.Context;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdType;

/**
 * 开屏广告加载器
 */
public class SplashAdLoader extends BaseAdLoader {
    
    public SplashAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public SplashAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.SPLASH, config);
    }
    
    /**
     * 展示开屏广告
     * @param activity Activity用于展示广告
     * @param containerId 容器ID
     */
    public void showAd(android.app.Activity activity, int containerId) {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            return;
        }
        
        if (!AdFrequencyManager.getInstance().canShowAd(currentAd)) {
            Log.w(TAG, "Ad frequency cap reached");
            notifyAdSkipped();
            return;
        }
        
        notifyAdShown();
        // 具体展示逻辑在SplashAdView中实现
    }
}
