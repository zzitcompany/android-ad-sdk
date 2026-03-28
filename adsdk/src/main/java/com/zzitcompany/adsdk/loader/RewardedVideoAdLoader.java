package com.zzitcompany.adsdk.loader;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.listener.RewardedAdListener;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.view.CloseButton;

/**
 * 激励视频广告加载器
 */
public class RewardedVideoAdLoader extends BaseAdLoader {
    
    private android.app.Dialog adDialog;
    private VideoView videoView;
    private boolean isShowing = false;
    private boolean isRewarded = false;
    private boolean isVideoCompleted = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    private RewardedAdListener rewardedAdListener;
    
    public RewardedVideoAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public RewardedVideoAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.REWARDED_VIDEO, config);
    }
    
    /**
     * 设置激励视频广告监听器
     */
    public void setRewardedAdListener(RewardedAdListener listener) {
        this.rewardedAdListener = listener;
        super.setAdListener(listener);
    }
    
    /**
     * 展示激励视频广告
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
        adDialog = new android.app.Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        FrameLayout container = new FrameLayout(context);
        
        // 视频播放器
        videoView = new VideoView(context);
        videoView.setVideoURI(Uri.parse(currentAd.getVideoUrl()));
        
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            videoView.start();
            notifyVideoStarted();
            notifyAdShown();
        });
        
        videoView.setOnCompletionListener(mp -> {
            isVideoCompleted = true;
            notifyVideoCompleted();
            grantReward();
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Video playback error: " + what);
            notifyAdLoadFailed(-1, "Video playback error");
            dismissAd();
            return true;
        });
        
        container.addView(videoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        
        // 关闭按钮（视频完成后才显示）
        CloseButton closeButton = new CloseButton(context);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        closeParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        closeParams.topMargin = dpToPx(30);
        closeParams.rightMargin = dpToPx(16);
        container.addView(closeButton, closeParams);
        
        closeButton.setOnClickListener(v -> {
            if (!isVideoCompleted) {
                notifyAdSkipped();
            }
            dismissAd();
            notifyAdClosed();
        });
        
        // 初始隐藏关闭按钮，视频完成后显示
        closeButton.setVisibility(View.GONE);
        
        adDialog.setContentView(container);
        adDialog.setCancelable(false);
        adDialog.setCanceledOnTouchOutside(false);
        
        Window window = adDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        
        adDialog.show();
        isShowing = true;
        isRewarded = false;
        isVideoCompleted = false;
    }
    
    /**
     * 发放奖励
     */
    private void grantReward() {
        if (!isRewarded && currentAd != null) {
            isRewarded = true;
            if (rewardedAdListener != null) {
                rewardedAdListener.onReward(
                        currentAd.getRewardName(),
                        currentAd.getRewardAmount());
            }
        }
    }
    
    /**
     * 关闭广告
     */
    public void dismissAd() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        
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
    
    /**
     * 是否已获得奖励
     */
    public boolean isRewarded() {
        return isRewarded;
    }
    
    private void notifyVideoStarted() {
        if (rewardedAdListener != null) {
            rewardedAdListener.onVideoStarted();
        }
    }
    
    private void notifyVideoCompleted() {
        if (rewardedAdListener != null) {
            rewardedAdListener.onVideoCompleted();
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
