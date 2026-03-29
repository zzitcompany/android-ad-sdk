package com.zzitcompany.adsdk.loader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;
import com.zzitcompany.adsdk.view.SplashAdView;

/**
 * 开屏广告加载器 - 全屏展示，自动倒计时关闭
 */
public class SplashAdLoader extends BaseAdLoader {
    
    private SplashAdView splashAdView;
    private Activity currentActivity;
    
    public SplashAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public SplashAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.SPLASH, config);
    }
    
    /**
     * 展示开屏广告（全屏覆盖Activity）
     * @param activity Activity用于展示广告
     */
    public void showAd(Activity activity) {
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
        
        currentActivity = activity;
        
        // 设置Activity为全屏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // 创建并显示开屏广告视图
        if (splashAdView != null) {
            splashAdView.destroy();
        }
        
        splashAdView = new SplashAdView(activity);
        splashAdView.setAdData(currentAd);
        splashAdView.setAdListener(new com.zzitcompany.adsdk.listener.AdListener() {
            @Override
            public void onAdLoaded(com.zzitcompany.adsdk.model.AdData adData) {
                if (adListener != null) {
                    adListener.onAdLoaded(adData);
                }
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                if (adListener != null) {
                    adListener.onAdLoadFailed(errorCode, errorMsg);
                }
            }
            
            @Override
            public void onAdImpression() {
                notifyAdImpression();
            }
            
            @Override
            public void onAdClicked() {
                notifyAdClicked();
            }
            
            @Override
            public void onAdClosed() {
                // 恢复Activity全屏状态
                if (currentActivity != null) {
                    currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                notifyAdClosed();
            }
        });
        
        // 将广告视图添加到Activity的DecorView
        activity.addContentView(splashAdView, new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        
        notifyAdShown();
    }
    
    /**
     * 设置倒计时秒数
     */
    public void setCountdownSeconds(int seconds) {
        if (splashAdView != null) {
            splashAdView.setCountdownSeconds(seconds);
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        if (splashAdView != null) {
            splashAdView.destroy();
            splashAdView = null;
        }
        
        if (currentActivity != null) {
            currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            currentActivity = null;
        }
    }
}
