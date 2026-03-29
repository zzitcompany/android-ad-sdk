package com.zzitcompany.adsdk.loader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;
import com.zzitcompany.adsdk.view.SplashAdView;

/**
 * 开屏广告加载器
 */
public class SplashAdLoader extends BaseAdLoader {
    
    private SplashAdView splashAdView;
    
    public SplashAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public SplashAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.SPLASH, config);
    }
    
    /**
     * 展示开屏广告
     * @param activity Activity用于展示广告
     * @param container 容器视图
     */
    public void showAd(Activity activity, ViewGroup container) {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            if (adListener != null) {
                adListener.onAdLoadFailed(-1, "Ad not loaded");
            }
            return;
        }
        
        if (!AdFrequencyManager.getInstance().canShowAd(currentAd)) {
            Log.w(TAG, "Ad frequency cap reached");
            notifyAdSkipped();
            return;
        }
        
        // 创建并显示开屏广告视图
        if (splashAdView != null) {
            splashAdView.destroy();
        }
        
        splashAdView = new SplashAdView(context);
        splashAdView.setAdData(currentAd);
        splashAdView.setAdListener(adListener);
        
        container.removeAllViews();
        container.addView(splashAdView);
        
        notifyAdShown();
        notifyAdImpression();
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (splashAdView != null) {
            splashAdView.destroy();
            splashAdView = null;
        }
    }
}
