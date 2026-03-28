package com.zzitcompany.adsdk.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.model.AdData;

/**
 * 开屏广告视图
 */
public class SplashAdView extends FrameLayout {
    
    private ImageView imageView;
    private TextView adLabel;
    private CloseButton closeButton;
    private TextView skipText;
    private AdData adData;
    private AdListener adListener;
    private Handler handler;
    private Runnable skipRunnable;
    private long remainingTime;
    
    public SplashAdView(Context context) {
        super(context);
        init(context);
    }
    
    public SplashAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    private void init(Context context) {
        handler = new Handler(Looper.getMainLooper());
        
        // 背景色
        setBackgroundColor(0xFFFFFFFF);
        
        // 图片
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> handleAdClick());
        
        addView(imageView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        
        // 广告标签
        adLabel = new TextView(context);
        adLabel.setText("广告");
        adLabel.setTextSize(10);
        adLabel.setTextColor(0xFF666666);
        adLabel.setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2));
        adLabel.setBackgroundColor(0x33999999);
        
        LayoutParams labelParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        labelParams.gravity = Gravity.BOTTOM | Gravity.START;
        labelParams.setMargins(dpToPx(8), 0, 0, dpToPx(8));
        addView(adLabel, labelParams);
        
        // 关闭按钮
        closeButton = new CloseButton(context);
        LayoutParams closeParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.setMargins(0, dpToPx(30), dpToPx(16), 0);
        addView(closeButton, closeParams);
        
        // 跳过文字
        skipText = new TextView(context);
        skipText.setText("跳过");
        skipText.setTextSize(12);
        skipText.setTextColor(0xFFFFFFFF);
        skipText.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));
        skipText.setBackgroundColor(0x80000000);
        skipText.setClickable(true);
        
        LayoutParams skipParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        skipParams.gravity = Gravity.BOTTOM | Gravity.END;
        skipParams.setMargins(0, 0, dpToPx(16), dpToPx(30));
        addView(skipText, skipParams);
        
        // 初始隐藏关闭按钮和跳过
        closeButton.setVisibility(View.GONE);
        skipText.setVisibility(View.GONE);
        
        closeButton.setOnClickListener(v -> closeAd());
        skipText.setOnClickListener(v -> closeAd());
    }
    
    public void setAdData(AdData adData) {
        this.adData = adData;
        
        if (adData != null && adData.getImageUrl() != null) {
            Glide.with(getContext())
                    .load(adData.getImageUrl())
                    .into(imageView);
            
            // 启动倒计时
            startCountDown(adData.getCloseDelayMs());
        }
    }
    
    public void setAdListener(AdListener listener) {
        this.adListener = listener;
    }
    
    private void startCountDown(long delayMs) {
        remainingTime = delayMs / 1000;
        
        // 延迟显示关闭按钮
        handler.postDelayed(() -> {
            closeButton.setVisibility(View.VISIBLE);
            skipText.setVisibility(View.VISIBLE);
            
            if (adListener != null) {
                adListener.onAdImpression();
            }
        }, delayMs);
        
        // 更新跳过按钮文字
        skipText.setText(remainingTime + "s | 跳过");
        
        skipRunnable = new Runnable() {
            @Override
            public void run() {
                remainingTime--;
                if (remainingTime > 0) {
                    skipText.setText(remainingTime + "s | 跳过");
                    handler.postDelayed(this, 1000);
                } else {
                    skipText.setText("跳过");
                }
            }
        };
        handler.postDelayed(skipRunnable, 1000);
    }
    
    private void handleAdClick() {
        if (adListener != null) {
            adListener.onAdClicked();
        }
        
        if (adData != null && adData.getClickUrl() != null) {
            try {
                android.content.Intent intent = new android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(adData.getClickUrl()));
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (Exception e) {
                // 忽略
            }
        }
    }
    
    private void closeAd() {
        if (skipRunnable != null) {
            handler.removeCallbacks(skipRunnable);
        }
        
        if (adListener != null) {
            adListener.onAdClosed();
        }
    }
    
    public void destroy() {
        if (skipRunnable != null) {
            handler.removeCallbacks(skipRunnable);
        }
        
        if (imageView != null) {
            Glide.with(getContext()).clear(imageView);
        }
        
        adData = null;
        adListener = null;
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
