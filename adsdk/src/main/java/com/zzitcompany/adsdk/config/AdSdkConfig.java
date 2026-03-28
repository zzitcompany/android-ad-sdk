package com.zzitcompany.adsdk.config;

/**
 * SDK配置
 */
public class AdSdkConfig {
    /**
     * 是否开启调试模式
     */
    private boolean debugMode = false;
    
    /**
     * 广告预加载数量
     */
    private int preloadCount = 3;
    
    /**
     * 广告缓存过期时间（毫秒）
     */
    private long cacheExpireTimeMs = 30 * 60 * 1000; // 30分钟
    
    /**
     * 是否自动预加载
     */
    private boolean autoPreload = true;
    
    /**
     * 是否开启频控
     */
    private boolean frequencyCapEnabled = true;
    
    /**
     * 同一广告展示间隔（毫秒）
     */
    private long frequencyIntervalMs = 60 * 1000; // 1分钟
    
    /**
     * 网络超时时间（毫秒）
     */
    private long networkTimeoutMs = 10 * 1000; // 10秒
    
    /**
     * 是否启用点击跳转
     */
    private boolean clickRedirectEnabled = true;

    private AdSdkConfig() {
    }

    public static class Builder {
        private final AdSdkConfig config;

        public Builder() {
            config = new AdSdkConfig();
        }

        public Builder setDebugMode(boolean debugMode) {
            config.debugMode = debugMode;
            return this;
        }

        public Builder setPreloadCount(int preloadCount) {
            config.preloadCount = preloadCount;
            return this;
        }

        public Builder setCacheExpireTimeMs(long cacheExpireTimeMs) {
            config.cacheExpireTimeMs = cacheExpireTimeMs;
            return this;
        }

        public Builder setAutoPreload(boolean autoPreload) {
            config.autoPreload = autoPreload;
            return this;
        }

        public Builder setFrequencyCapEnabled(boolean enabled) {
            config.frequencyCapEnabled = enabled;
            return this;
        }

        public Builder setFrequencyIntervalMs(long intervalMs) {
            config.frequencyIntervalMs = intervalMs;
            return this;
        }

        public Builder setNetworkTimeoutMs(long timeoutMs) {
            config.networkTimeoutMs = timeoutMs;
            return this;
        }

        public Builder setClickRedirectEnabled(boolean enabled) {
            config.clickRedirectEnabled = enabled;
            return this;
        }

        public AdSdkConfig build() {
            return config;
        }
    }

    // Getters
    public boolean isDebugMode() {
        return debugMode;
    }

    public int getPreloadCount() {
        return preloadCount;
    }

    public long getCacheExpireTimeMs() {
        return cacheExpireTimeMs;
    }

    public boolean isAutoPreload() {
        return autoPreload;
    }

    public boolean isFrequencyCapEnabled() {
        return frequencyCapEnabled;
    }

    public long getFrequencyIntervalMs() {
        return frequencyIntervalMs;
    }

    public long getNetworkTimeoutMs() {
        return networkTimeoutMs;
    }

    public boolean isClickRedirectEnabled() {
        return clickRedirectEnabled;
    }
}
