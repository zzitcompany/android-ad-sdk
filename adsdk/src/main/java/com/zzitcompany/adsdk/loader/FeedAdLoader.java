package com.zzitcompany.adsdk.loader;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;
import com.zzitcompany.adsdk.view.FeedAdView;

/**
 * 信息流广告加载器
 */
public class FeedAdLoader extends BaseAdLoader {
    
    private FeedAdView feedAdView;
    
    public FeedAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public FeedAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.FEED, config);
    }
    
    /**
     * 获取信息流广告视图
     * @return 广告视图
     */
    public FeedAdView getAdView() {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            return null;
        }
        
        if (!AdFrequencyManager.getInstance().canShowAd(currentAd)) {
            Log.w(TAG, "Ad frequency cap reached");
            return null;
        }
        
        feedAdView = new FeedAdView(context);
        feedAdView.setAdData(currentAd);
        feedAdView.setAdListener(adListener);
        
        notifyAdShown();
        
        return feedAdView;
    }
    
    /**
     * 展示信息流广告到指定容器
     */
    public void showAd(ViewGroup parent, int index) {
        FeedAdView view = getAdView();
        if (view != null && parent != null) {
            if (index >= 0 && index < parent.getChildCount()) {
                parent.addView(view, index);
            } else {
                parent.addView(view);
            }
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (feedAdView != null) {
            feedAdView.destroy();
            feedAdView = null;
        }
    }
}
