package com.zzitcompany.adsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzitcompany.adsdk.R;
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.model.AdData;

/**
 * Banner广告视图
 */
public class BannerAdView extends FrameLayout {
    
    private ImageView imageView;
    private TextView adLabel;
    private CloseButton closeButton;
    private AdData adData;
    private AdListener adListener;
    
    public BannerAdView(Context context) {
        super(context);
        init(context);
    }
    
    public BannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public BannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        // 创建容器
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        
        // 广告标签
        adLabel = new TextView(context);
        adLabel.setText("广告");
        adLabel.setTextSize(10);
        adLabel.setTextColor(0xFF666666);
        adLabel.setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2));
        adLabel.setBackgroundColor(0x33999999);
        
        // 图片视图
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> handleAdClick());
        
        LayoutParams imageParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                dpToPx(100));
        
        container.addView(imageView, imageParams);
        
        // 添加广告标签
        LayoutParams labelParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        labelParams.gravity = Gravity.BOTTOM | Gravity.START;
        
        addView(container, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        addView(adLabel, labelParams);
        
        // 关闭按钮
        closeButton = new CloseButton(context);
        LayoutParams closeParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        addView(closeButton, closeParams);
        
        closeButton.setOnClickListener(v -> {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            setVisibility(GONE);
        });
    }
    
    /**
     * 设置广告数据
     */
    public void setAdData(AdData adData) {
        this.adData = adData;
        
        if (adData != null && adData.getImageUrl() != null) {
            Glide.with(getContext())
                    .load(adData.getImageUrl())
                    .into(imageView);
        }
    }
    
    /**
     * 设置广告监听器
     */
    public void setAdListener(AdListener listener) {
        this.adListener = listener;
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
                // 忽略错误
            }
        }
    }
    
    /**
     * 销毁视图
     */
    public void destroy() {
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
