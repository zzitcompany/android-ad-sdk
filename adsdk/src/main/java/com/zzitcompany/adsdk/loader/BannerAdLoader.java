package com.zzitcompany.adsdk.loader;

import android.content.Context;
import android.view.ViewGroup;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.view.BannerAdView;

/**
 * 横幅广告加载器
 */
public class BannerAdLoader extends BaseAdLoader {
    
    private BannerAdView bannerAdView;
    
    public BannerAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public BannerAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.BANNER, config);
    }
    
    /**
     * 加载并展示横幅广告
     * @param parent 父容器
     */
    public void loadAndShow(ViewGroup parent) {
        loadAd();
        
        // 等待加载完成后展示
        setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(com.zzitcompany.adsdk.model.AdData adData) {
                showBanner(parent);
            }
            
            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                Log.e(TAG, "Failed to load banner ad: " + errorMsg);
            }
        });
    }
    
    /**
     * 展示横幅广告
     */
    public void showBanner(ViewGroup parent) {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            return;
        }
        
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
        
        bannerAdView = new BannerAdView(context);
        bannerAdView.setAdData(currentAd);
        bannerAdView.setAdListener(adListener);
        
        parent.removeAllViews();
        parent.addView(bannerAdView);
        
        notifyAdShown();
    }
    
    /**
     * 隐藏横幅广告
     */
    public void hideBanner() {
        if (bannerAdView != null) {
            bannerAdView.setVisibility(android.view.View.GONE);
        }
    }
    
    /**
     * 显示横幅广告
     */
    public void showBanner() {
        if (bannerAdView != null) {
            bannerAdView.setVisibility(android.view.View.VISIBLE);
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
    }
}
