package com.zzitcompany.adsdk.mock;

import com.zzitcompany.adsdk.model.AdData;
import com.zzitcompany.adsdk.model.AdType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Mock广告数据提供器
 */
public class MockAdProvider {
    
    private static final Random random = new Random();
    
    // Mock图片URL（使用公开的占位图片服务）
    private static final String[] MOCK_IMAGE_URLS = {
        "https://picsum.photos/800/600?random=1",
        "https://picsum.photos/800/600?random=2",
        "https://picsum.photos/800/600?random=3",
        "https://picsum.photos/1080/1920?random=4", // 开屏竖图
        "https://picsum.photos/1080/1920?random=5",
        "https://picsum.photos/320/100?random=6", // Banner
        "https://picsum.photos/320/100?random=7",
        "https://picsum.photos/640/320?random=8", // 信息流
        "https://picsum.photos/640/320?random=9",
    };
    
    // Mock广告标题
    private static final String[] MOCK_TITLES = {
        "限时特惠，新品首发",
        "618大促，全场5折起",
        "新人专享，立减100元",
        "爆款推荐，品质之选",
        "今日特价，手慢无",
        "品牌直降，正品保障"
    };
    
    // Mock广告描述
    private static final String[] MOCK_DESCRIPTIONS = {
        "点击查看详情，惊喜等你发现",
        "海量商品任你挑选，品质保证",
        "限时优惠，错过再等一年",
        "精选好物，物超所值",
        "立即抢购，库存有限"
    };
    
    // Mock点击URL
    private static final String MOCK_CLICK_URL = "https://www.example.com/ad/landing";
    
    /**
     * 获取开屏广告
     */
    public static AdData getSplashAd() {
        AdData ad = createBaseAd(AdType.SPLASH);
        ad.setImageUrl(MOCK_IMAGE_URLS[random.nextInt(2) + 3]); // 竖图
        ad.setCloseDelayMs(5000); // 5秒后显示关闭按钮
        ad.setDisplayDurationMs(5000);
        return ad;
    }
    
    /**
     * 获取横幅广告
     */
    public static AdData getBannerAd() {
        AdData ad = createBaseAd(AdType.BANNER);
        ad.setImageUrl(MOCK_IMAGE_URLS[random.nextInt(2) + 5]); // Banner图
        ad.setDisplayDurationMs(0); // 永久展示
        return ad;
    }
    
    /**
     * 获取信息流广告
     */
    public static AdData getFeedAd() {
        AdData ad = createBaseAd(AdType.FEED);
        ad.setImageUrl(MOCK_IMAGE_URLS[random.nextInt(2) + 7]); // 信息流图
        ad.setDisplayDurationMs(0);
        return ad;
    }
    
    /**
     * 获取插屏广告
     */
    public static AdData getInterstitialAd() {
        AdData ad = createBaseAd(AdType.INTERSTITIAL);
        ad.setImageUrl(MOCK_IMAGE_URLS[random.nextInt(3)]);
        ad.setCloseDelayMs(3000); // 3秒后显示关闭按钮
        return ad;
    }
    
    /**
     * 获取激励视频广告
     */
    public static AdData getRewardedVideoAd() {
        AdData ad = createBaseAd(AdType.REWARDED_VIDEO);
        ad.setVideoUrl("https://www.example.com/video/sample.mp4");
        ad.setImageUrl(MOCK_IMAGE_URLS[random.nextInt(3)]); // 视频封面
        ad.setRewardName("金币");
        ad.setRewardAmount(50 + random.nextInt(50));
        ad.setDisplayDurationMs(30000); // 30秒视频
        ad.setCloseDelayMs(0);
        return ad;
    }
    
    /**
     * 获取指定类型的广告
     */
    public static AdData getAdByType(AdType adType) {
        switch (adType) {
            case SPLASH:
                return getSplashAd();
            case BANNER:
                return getBannerAd();
            case FEED:
                return getFeedAd();
            case INTERSTITIAL:
                return getInterstitialAd();
            case REWARDED_VIDEO:
                return getRewardedVideoAd();
            default:
                return getSplashAd();
        }
    }
    
    /**
     * 获取广告列表
     */
    public static List<AdData> getAdList(AdType adType, int count) {
        List<AdData> ads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ads.add(getAdByType(adType));
        }
        return ads;
    }
    
    /**
     * 创建基础广告数据
     */
    private static AdData createBaseAd(AdType adType) {
        AdData ad = new AdData();
        ad.setAdId(adType.getValue() + "_" + System.currentTimeMillis() + "_" + random.nextInt(10000));
        ad.setAdType(adType);
        ad.setTitle(MOCK_TITLES[random.nextInt(MOCK_TITLES.length)]);
        ad.setDescription(MOCK_DESCRIPTIONS[random.nextInt(MOCK_DESCRIPTIONS.length)]);
        ad.setClickUrl(MOCK_CLICK_URL);
        ad.setTargetTags(Arrays.asList("default", "all"));
        return ad;
    }
}
