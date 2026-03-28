package com.zzitcompany.adsdk.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zzitcompany.adsdk.AdSdk;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.listener.RewardedAdListener;
import com.zzitcompany.adsdk.loader.BannerAdLoader;
import com.zzitcompany.adsdk.loader.FeedAdLoader;
import com.zzitcompany.adsdk.loader.InterstitialAdLoader;
import com.zzitcompany.adsdk.loader.RewardedVideoAdLoader;
import com.zzitcompany.adsdk.model.AdData;

/**
 * Demo主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BannerAdLoader bannerAdLoader;
    private FeedAdLoader feedAdLoader;
    private InterstitialAdLoader interstitialAdLoader;
    private RewardedVideoAdLoader rewardedVideoAdLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initAdLoaders();
    }

    private void initViews() {
        findViewById(R.id.btn_show_banner).setOnClickListener(this);
        findViewById(R.id.btn_show_feed).setOnClickListener(this);
        findViewById(R.id.btn_show_interstitial).setOnClickListener(this);
        findViewById(R.id.btn_show_rewarded).setOnClickListener(this);
        findViewById(R.id.btn_preload_all).setOnClickListener(this);
    }

    private void initAdLoaders() {
        bannerAdLoader = AdSdk.getInstance().getBannerAdLoader();
        feedAdLoader = AdSdk.getInstance().getFeedAdLoader();
        interstitialAdLoader = AdSdk.getInstance().getInterstitialAdLoader();
        rewardedVideoAdLoader = AdSdk.getInstance().getRewardedVideoAdLoader();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.btn_show_banner) {
            showBannerAd();
        } else if (id == R.id.btn_show_feed) {
            showFeedAd();
        } else if (id == R.id.btn_show_interstitial) {
            showInterstitialAd();
        } else if (id == R.id.btn_show_rewarded) {
            showRewardedVideoAd();
        } else if (id == R.id.btn_preload_all) {
            preloadAllAds();
        }
    }

    private void showBannerAd() {
        bannerAdLoader.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(AdData adData) {
                Toast.makeText(MainActivity.this, "Banner广告加载成功", Toast.LENGTH_SHORT).show();
                bannerAdLoader.showBanner(findViewById(R.id.banner_container));
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                Toast.makeText(MainActivity.this, "Banner广告加载失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        bannerAdLoader.loadAd();
    }

    private void showFeedAd() {
        feedAdLoader.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(AdData adData) {
                Toast.makeText(MainActivity.this, "信息流广告加载成功", Toast.LENGTH_SHORT).show();
                feedAdLoader.showAd(findViewById(R.id.feed_container), 0);
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                Toast.makeText(MainActivity.this, "信息流广告加载失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        feedAdLoader.loadAd();
    }

    private void showInterstitialAd() {
        interstitialAdLoader.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(AdData adData) {
                Toast.makeText(MainActivity.this, "插屏广告加载成功", Toast.LENGTH_SHORT).show();
                interstitialAdLoader.showAd(MainActivity.this);
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                Toast.makeText(MainActivity.this, "插屏广告加载失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(MainActivity.this, "插屏广告关闭", Toast.LENGTH_SHORT).show();
            }
        });
        interstitialAdLoader.loadAd();
    }

    private void showRewardedVideoAd() {
        rewardedVideoAdLoader.setRewardedAdListener(new RewardedAdListener() {
            @Override
            public void onAdLoaded(AdData adData) {
                Toast.makeText(MainActivity.this, "激励视频加载成功", Toast.LENGTH_SHORT).show();
                rewardedVideoAdLoader.showAd(MainActivity.this);
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMsg) {
                Toast.makeText(MainActivity.this, "激励视频加载失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReward(String rewardName, int rewardAmount) {
                Toast.makeText(MainActivity.this, 
                        "获得奖励: " + rewardName + " x" + rewardAmount, 
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(MainActivity.this, "激励视频关闭", Toast.LENGTH_SHORT).show();
            }
        });
        rewardedVideoAdLoader.loadAd();
    }

    private void preloadAllAds() {
        AdSdk.getInstance().preloadAllAds();
        Toast.makeText(this, "开始预加载所有广告", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (bannerAdLoader != null) {
            bannerAdLoader.destroy();
        }
        if (feedAdLoader != null) {
            feedAdLoader.destroy();
        }
        if (interstitialAdLoader != null) {
            interstitialAdLoader.destroy();
        }
        if (rewardedVideoAdLoader != null) {
            rewardedVideoAdLoader.destroy();
        }
    }
}
