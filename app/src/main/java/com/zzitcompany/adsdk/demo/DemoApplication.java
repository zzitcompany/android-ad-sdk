package com.zzitcompany.adsdk.demo;

import android.app.Application;

import com.zzitcompany.adsdk.AdSdk;
import com.zzitcompany.adsdk.config.AdSdkConfig;

/**
 * Demo Application
 */
public class DemoApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化广告SDK
        AdSdkConfig config = new AdSdkConfig.Builder()
                .setDebugMode(true)
                .setAutoPreload(true)
                .setPreloadCount(3)
                .setFrequencyCapEnabled(true)
                .build();
        
        AdSdk.init(this, config);
    }
}
