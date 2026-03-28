package com.zzitcompany.adsdk.loader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.view.CloseButton;

/**
 * 插屏广告加载器
 */
public class InterstitialAdLoader extends BaseAdLoader {
    
    private Dialog adDialog;
    private boolean isShowing = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    public InterstitialAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public InterstitialAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.INTERSTITIAL, config);
    }
    
    /**
     * 展示插屏广告
     * @param activity Activity
     */
    public void showAd(Activity activity) {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            return;
        }
        
        if (isShowing) {
            Log.w(TAG, "Ad is already showing");
            return;
        }
        
        if (!AdFrequencyManager.getInstance().canShowAd(currentAd)) {
            Log.w(TAG, "Ad frequency cap reached");
            notifyAdSkipped();
            return;
        }
        
        createAndShowDialog(activity);
    }
    
    private void createAndShowDialog(Activity activity) {
        adDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        FrameLayout container = new FrameLayout(context);
        
        // 广告图片
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .load(currentAd.getImageUrl())
                .into(imageView);
        
        imageView.setOnClickListener(v -> {
            notifyAdClicked();
            // 处理点击跳转
            handleAdClick();
        });
        
        container.addView(imageView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        
        // 关闭按钮
        CloseButton closeButton = new CloseButton(context);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        closeParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        closeParams.topMargin = dpToPx(30);
        closeParams.rightMargin = dpToPx(16);
        container.addView(closeButton, closeParams);
        
        closeButton.setOnClickListener(v -> {
            dismissAd();
            notifyAdClosed();
        });
        
        // 延迟显示关闭按钮
        closeButton.setVisibility(View.GONE);
        handler.postDelayed(() -> {
            if (adDialog != null && adDialog.isShowing()) {
                closeButton.setVisibility(View.VISIBLE);
            }
        }, currentAd.getCloseDelayMs());
        
        adDialog.setContentView(container);
        adDialog.setCancelable(false);
        adDialog.setCanceledOnTouchOutside(false);
        
        // 设置窗口属性
        Window window = adDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        
        adDialog.show();
        isShowing = true;
        
        notifyAdShown();
        notifyAdImpression();
    }
    
    /**
     * 关闭广告
     */
    public void dismissAd() {
        if (adDialog != null && adDialog.isShowing()) {
            adDialog.dismiss();
            adDialog = null;
            isShowing = false;
        }
    }
    
    /**
     * 广告是否正在展示
     */
    public boolean isShowing() {
        return isShowing;
    }
    
    private void handleAdClick() {
        if (config.isClickRedirectEnabled() && currentAd.getClickUrl() != null) {
            // 打开浏览器或WebView
            try {
                android.content.Intent intent = new android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(currentAd.getClickUrl()));
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Failed to open click URL", e);
            }
        }
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        dismissAd();
        handler.removeCallbacksAndMessages(null);
    }
}
