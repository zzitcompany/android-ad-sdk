package com.zzitcompany.adsdk.model;

/**
 * 广告类型枚举
 */
public enum AdType {
    /**
     * 开屏广告
     */
    SPLASH("splash"),
    
    /**
     * 横幅广告
     */
    BANNER("banner"),
    
    /**
     * 信息流广告
     */
    FEED("feed"),
    
    /**
     * 插屏广告
     */
    INTERSTITIAL("interstitial"),
    
    /**
     * 激励视频广告
     */
    REWARDED_VIDEO("rewarded_video");

    private final String value;

    AdType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AdType fromValue(String value) {
        for (AdType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ad type: " + value);
    }
}
