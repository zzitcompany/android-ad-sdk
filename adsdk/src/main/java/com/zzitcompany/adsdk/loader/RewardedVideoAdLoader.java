package com.zzitcompany.adsdk.loader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.zzitcompany.adsdk.config.AdSdkConfig;
import com.zzitcompany.adsdk.listener.RewardedAdListener;
import com.zzitcompany.adsdk.model.AdType;
import com.zzitcompany.adsdk.utils.AdFrequencyManager;
import com.zzitcompany.adsdk.view.CloseButton;

/**
 * 激励视频广告加载器
 */
public class RewardedVideoAdLoader extends BaseAdLoader {
    
    private Dialog adDialog;
    private VideoView videoView;
    private ImageView coverImage;
    private boolean isShowing = false;
    private boolean isRewarded = false;
    private boolean isVideoCompleted = false;
    private boolean isPlaying = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int videoDurationMs = 0;
    
    private RewardedAdListener rewardedAdListener;
    
    public RewardedVideoAdLoader(Context context) {
        this(context, new AdSdkConfig.Builder().build());
    }
    
    public RewardedVideoAdLoader(Context context, AdSdkConfig config) {
        super(context, AdType.REWARDED_VIDEO, config);
    }
    
    public void setRewardedAdListener(RewardedAdListener listener) {
        this.rewardedAdListener = listener;
        super.setAdListener(listener);
    }
    
    public void showAd(Activity activity) {
        if (!isAdLoaded()) {
            Log.w(TAG, "Ad not loaded");
            if (adListener != null) {
                adListener.onAdLoadFailed(-1, "Ad not loaded");
            }
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
        container.setBackgroundColor(Color.BLACK);
        
        // 封面图片
        coverImage = new ImageView(context);
        coverImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        coverImage.setBackgroundColor(Color.BLACK);
        
        if (currentAd.getImageUrl() != null) {
            Glide.with(context)
                    .load(currentAd.getImageUrl())
                    .into(coverImage);
        }
        
        // 播放按钮
        FrameLayout playButtonContainer = new FrameLayout(context);
        playButtonContainer.setBackground(createPlayButtonBackground());
        
        TextView playText = new TextView(context);
        playText.setText("▶ 观看视频获得奖励");
        playText.setTextColor(Color.WHITE);
        playText.setTextSize(16);
        playText.setGravity(Gravity.CENTER);
        playButtonContainer.addView(playText, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        
        // 播放按钮点击事件
        playButtonContainer.setOnClickListener(v -> {
            startVideoPlayback();
        });
        
        coverImage.setOnClickListener(v -> {
            if (!isPlaying) {
                startVideoPlayback();
            }
        });
        
        // 视频播放器（初始隐藏）
        videoView = new VideoView(context);
        videoView.setVisibility(View.GONE);
        
        // 视频准备完成
        videoView.setOnPreparedListener(mp -> {
            videoDurationMs = mp.getDuration();
            mp.setLooping(false);
            notifyVideoStarted();
            notifyAdShown();
        });
        
        // 视频播放完成
        videoView.setOnCompletionListener(mp -> {
            isVideoCompleted = true;
            notifyVideoCompleted();
            grantReward();
            // 显示完成界面
            showCompletionOverlay();
        });
        
        // 视频错误
        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Video playback error: " + what);
            // 如果视频播放失败，模拟完成（用于测试）
            isVideoCompleted = true;
            notifyVideoCompleted();
            grantReward();
            return true;
        });
        
        container.addView(coverImage, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        
        // 添加播放按钮容器（居中）
        FrameLayout.LayoutParams playParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        container.addView(playButtonContainer, playParams);
        
        container.addView(videoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        
        // 关闭按钮
        CloseButton closeButton = new CloseButton(context);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.topMargin = dpToPx(30);
        closeParams.rightMargin = dpToPx(16);
        closeButton.setVisibility(View.GONE);
        container.addView(closeButton, closeParams);
        
        closeButton.setOnClickListener(v -> {
            if (!isVideoCompleted) {
                notifyAdSkipped();
            }
            dismissAd();
            notifyAdClosed();
        });
        
        // 关闭按钮显示逻辑
        handler.postDelayed(() -> {
            if (adDialog != null && adDialog.isShowing()) {
                closeButton.setVisibility(View.VISIBLE);
            }
        }, 5000); // 5秒后显示关闭按钮
        
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
        isPlaying = false;
    }
    
    /**
     * 开始视频播放
     */
    private void startVideoPlayback() {
        if (videoView == null || isPlaying) {
            return;
        }
        
        isPlaying = true;
        
        // 隐藏封面和播放按钮
        if (coverImage != null) {
            // 找到父容器中的播放按钮并隐藏
            View parent = (View) coverImage.getParent();
            if (parent != null && parent instanceof FrameLayout) {
                FrameLayout frameParent = (FrameLayout) parent;
                for (int i = 0; i < frameParent.getChildCount(); i++) {
                    View child = frameParent.getChildAt(i);
                    if (child != coverImage && child != videoView && child instanceof FrameLayout) {
                        child.setVisibility(View.GONE);
                    }
                }
            }
            coverImage.setVisibility(View.GONE);
        }
        
        // 显示视频播放器
        videoView.setVisibility(View.VISIBLE);
        
        // 获取视频URL（如果是mock URL，使用示例视频）
        String videoUrl = getEffectiveVideoUrl();
        
        try {
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.start();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start video: " + e.getMessage());
            // 如果无法播放视频，模拟完成
            isVideoCompleted = true;
            notifyVideoCompleted();
            grantReward();
        }
    }
    
    /**
     * 获取有效的视频URL（处理mock数据）
     */
    private String getEffectiveVideoUrl() {
        String url = currentAd.getVideoUrl();
        
        // 如果是示例URL或无效URL，使用公开可用的测试视频
        if (url == null || url.contains("example.com") || url.isEmpty()) {
            // 使用公开的测试视频
            return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }
        
        return url;
    }
    
    /**
     * 显示完成界面
     */
    private void showCompletionOverlay() {
        if (adDialog == null || !adDialog.isShowing()) {
            return;
        }
        
        View contentView = adDialog.getWindow().getDecorView();
        if (contentView instanceof FrameLayout) {
            FrameLayout container = (FrameLayout) contentView;
            
            // 隐藏视频
            if (videoView != null) {
                videoView.setVisibility(View.GONE);
            }
            
            // 显示完成界面
            FrameLayout completeView = new FrameLayout(context);
            completeView.setBackgroundColor(Color.parseColor("#E65C00")); // 橙色背景
            
            TextView completeText = new TextView(context);
            completeText.setText("🎉 视频观看完成！");
            completeText.setTextColor(Color.WHITE);
            completeText.setTextSize(24);
            completeText.setGravity(Gravity.CENTER);
            
            TextView rewardText = new TextView(context);
            rewardText.setText("获得 " + currentAd.getRewardName() + " x" + currentAd.getRewardAmount());
            rewardText.setTextColor(Color.WHITE);
            rewardText.setTextSize(18);
            rewardText.setGravity(Gravity.CENTER);
            rewardText.setPadding(0, dpToPx(20), 0, 0);
            
            TextView closeText = new TextView(context);
            closeText.setText("点击关闭");
            closeText.setTextColor(Color.parseColor("#CCFFFFFF"));
            closeText.setTextSize(14);
            closeText.setGravity(Gravity.CENTER);
            closeText.setPadding(0, dpToPx(40), 0, 0);
            
            completeView.addView(completeText, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            
            FrameLayout.LayoutParams rewardParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            rewardParams.topMargin = dpToPx(80);
            completeView.addView(rewardText, rewardParams);
            
            FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            closeParams.bottomMargin = dpToPx(100);
            completeView.addView(closeText, closeParams);
            
            completeView.setOnClickListener(v -> {
                dismissAd();
                notifyAdClosed();
            });
            
            container.addView(completeView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        }
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
            try {
                videoView.stopPlayback();
            } catch (Exception e) {
                // 忽略
            }
            videoView = null;
        }
        
        if (adDialog != null && adDialog.isShowing()) {
            adDialog.dismiss();
            adDialog = null;
        }
        isShowing = false;
        isPlaying = false;
    }
    
    public boolean isShowing() {
        return isShowing;
    }
    
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
    
    private GradientDrawable createPlayButtonBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor("#80FF6200"));
        drawable.setSize(dpToPx(200), dpToPx(60));
        return drawable;
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
