package com.zzitcompany.adsdk.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.model.AdData;

/**
 * 开屏广告视图 - 全屏展示，自动倒计时关闭
 */
public class SplashAdView extends FrameLayout {
    
    private ImageView imageView;
    private TextView skipText;
    private AdData adData;
    private AdListener adListener;
    private Handler handler;
    private CountDownTimer countDownTimer;
    private int totalSeconds = 5;
    private Activity parentActivity;
    
    public SplashAdView(Context context) {
        super(context);
        init();
    }
    
    public SplashAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        handler = new Handler(Looper.getMainLooper());
        
        // 全屏背景
        setBackgroundColor(Color.BLACK);
        
        // 如果是Activity上下文，设置全屏
        if (getContext() instanceof Activity) {
            parentActivity = (Activity) getContext();
            parentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        // 广告图片（全屏）
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundColor(Color.BLACK);
        
        addView(imageView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        
        // 跳过按钮（右下角）
        skipText = new TextView(getContext());
        skipText.setText(totalSeconds + "s | 跳过");
        skipText.setTextSize(14);
        skipText.setTextColor(Color.WHITE);
        skipText.setGravity(Gravity.CENTER);
        skipText.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        skipText.setBackgroundColor(0x80000000);
        skipText.setClickable(true);
        
        LayoutParams skipParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        skipParams.gravity = Gravity.BOTTOM | Gravity.END;
        skipParams.bottomMargin = dpToPx(40);
        skipParams.rightMargin = dpToPx(20);
        addView(skipText, skipParams);
        
        skipText.setOnClickListener(v -> closeAd());
        
        // 广告标签（左下角）
        TextView adLabel = new TextView(getContext());
        adLabel.setText("广告");
        adLabel.setTextSize(12);
        adLabel.setTextColor(0xFFFFFFFF);
        adLabel.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        adLabel.setBackgroundColor(0x66000000);
        
        LayoutParams labelParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        labelParams.gravity = Gravity.BOTTOM | Gravity.START;
        labelParams.bottomMargin = dpToPx(40);
        labelParams.leftMargin = dpToPx(20);
        addView(adLabel, labelParams);
        
        // 图片点击
        imageView.setOnClickListener(v -> handleAdClick());
    }
    
    public void setAdData(AdData adData) {
        this.adData = adData;
        
        if (adData != null) {
            if (adData.getImageUrl() != null) {
                Glide.with(getContext())
                        .load(adData.getImageUrl())
                        .into(imageView);
            }
            
            // 使用广告设置的倒计时时间（如果有）
            if (adData.getCloseDelayMs() > 0) {
                totalSeconds = (int) (adData.getCloseDelayMs() / 1000);
            }
        }
        
        // 开始倒计时
        startCountdown();
    }
    
    private void startCountdown() {
        // 移除之前的倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // 更新倒计时显示
        updateSkipText(totalSeconds);
        
        // 开始新的倒计时
        countDownTimer = new CountDownTimer(totalSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) + 1;
                updateSkipText(seconds);
            }
            
            @Override
            public void onFinish() {
                updateSkipText(0);
                // 倒计时结束，自动关闭
                closeAd();
            }
        }.start();
        
        // 延迟通知曝光
        handler.postDelayed(() -> {
            if (adListener != null) {
                adListener.onAdImpression();
            }
        }, 500);
    }
    
    private void updateSkipText(int seconds) {
        if (seconds > 0) {
            skipText.setText(seconds + "s | 跳过");
            skipText.setClickable(false);
            skipText.setAlpha(0.7f);
        } else {
            skipText.setText("跳过");
            skipText.setClickable(true);
            skipText.setAlpha(1f);
        }
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
        // 停止倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        
        // 关闭时恢复Activity的全屏状态
        if (parentActivity != null) {
            parentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        // 通知关闭
        if (adListener != null) {
            adListener.onAdClosed();
        }
        
        // 隐藏视图
        setVisibility(View.GONE);
    }
    
    public void setAdListener(AdListener listener) {
        this.adListener = listener;
    }
    
    public void setCountdownSeconds(int seconds) {
        this.totalSeconds = seconds;
    }
    
    public void destroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        
        if (imageView != null) {
            Glide.with(getContext()).clear(imageView);
        }
        
        if (parentActivity != null) {
            parentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        adData = null;
        adListener = null;
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
