# Android Ad SDK

一个完整的安卓广告SDK，支持多种广告类型展示。

## 功能特性

- ✅ **开屏广告 (Splash Ad)** - 应用启动时展示的全屏广告
- ✅ **横幅广告 (Banner Ad)** - 固定位置的条状广告
- ✅ **信息流广告 (Feed Ad)** - 嵌入列表内容中的广告
- ✅ **插屏广告 (Interstitial Ad)** - 全屏弹窗广告
- ✅ **激励视频广告 (Rewarded Video Ad)** - 观看视频获得奖励

## 核心特性

- 🚀 广告预加载与缓存
- 📊 广告展示统计
- ⏱️ 频率控制
- 🎯 Mock数据支持
- 📱 纯客户端实现

## 项目结构

```
android-ad-sdk/
├── adsdk/                  # SDK模块
│   └── src/main/java/com/zzitcompany/adsdk/
│       ├── AdSdk.java              # SDK入口
│       ├── config/                 # 配置
│       ├── listener/               # 监听器接口
│       ├── loader/                 # 广告加载器
│       ├── model/                  # 数据模型
│       ├── mock/                   # Mock数据
│       ├── utils/                  # 工具类
│       └── view/                   # 广告视图
├── app/                    # Demo应用
└── README.md
```

## 快速开始

### 1. 初始化SDK

```java
// 在Application中初始化
AdSdkConfig config = new AdSdkConfig.Builder()
    .setDebugMode(true)
    .setAutoPreload(true)
    .setPreloadCount(3)
    .build();

AdSdk.init(context, config);
```

### 2. 展示横幅广告

```java
BannerAdLoader loader = AdSdk.getInstance().getBannerAdLoader();
loader.setAdListener(new AdListener() {
    @Override
    public void onAdLoaded(AdData adData) {
        loader.showBanner(container);
    }
});
loader.loadAd();
```

### 3. 展示插屏广告

```java
InterstitialAdLoader loader = AdSdk.getInstance().getInterstitialAdLoader();
loader.setAdListener(new AdListener() {
    @Override
    public void onAdLoaded(AdData adData) {
        loader.showAd(activity);
    }
});
loader.loadAd();
```

### 4. 展示激励视频

```java
RewardedVideoAdLoader loader = AdSdk.getInstance().getRewardedVideoAdLoader();
loader.setRewardedAdListener(new RewardedAdListener() {
    @Override
    public void onAdLoaded(AdData adData) {
        loader.showAd(activity);
    }
    
    @Override
    public void onReward(String rewardName, int rewardAmount) {
        // 发放奖励
    }
});
loader.loadAd();
```

## SDK配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| debugMode | 调试模式 | false |
| preloadCount | 预加载数量 | 3 |
| autoPreload | 自动预加载 | true |
| frequencyCapEnabled | 频控开关 | true |
| frequencyIntervalMs | 频控间隔 | 60000ms |
| cacheExpireTimeMs | 缓存过期时间 | 30分钟 |

## 技术要求

- Android SDK 21+
- Java 11+
- AndroidX

## 依赖

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

## License

MIT License
