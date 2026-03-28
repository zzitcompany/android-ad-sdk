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
import com.zzitcompany.adsdk.listener.AdListener;
import com.zzitcompany.adsdk.model.AdData;

/**
 * 信息流广告视图
 */
public class FeedAdView extends FrameLayout {
    
    private ImageView imageView;
    private TextView titleView;
    private TextView descView;
    private TextView adLabel;
    private CloseButton closeButton;
    private AdData adData;
    private AdListener adListener;
    
    public FeedAdView(Context context) {
        super(context);
        init(context);
    }
    
    public FeedAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public FeedAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        // 主容器
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundColor(0xFFFFFFFF);
        container.setElevation(dpToPx(2));
        
        // 图片
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> handleAdClick());
        
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(180));
        container.addView(imageView, imageParams);
        
        // 文字容器
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        
        // 标题
        titleView = new TextView(context);
        titleView.setTextSize(14);
        titleView.setTextColor(0xFF333333);
        titleView.setMaxLines(1);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        
        textContainer.addView(titleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        // 描述
        descView = new TextView(context);
        descView.setTextSize(12);
        descView.setTextColor(0xFF666666);
        descView.setMaxLines(2);
        descView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        descView.setPadding(0, dpToPx(4), 0, 0);
        
        textContainer.addView(descView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        container.addView(textContainer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        addView(container, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        
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
        labelParams.gravity = Gravity.TOP | Gravity.START;
        labelParams.setMargins(dpToPx(8), dpToPx(8), 0, 0);
        addView(adLabel, labelParams);
        
        // 关闭按钮
        closeButton = new CloseButton(context);
        LayoutParams closeParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.setMargins(0, dpToPx(8), dpToPx(8), 0);
        addView(closeButton, closeParams);
        
        closeButton.setOnClickListener(v -> {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            setVisibility(GONE);
        });
    }
    
    public void setAdData(AdData adData) {
        this.adData = adData;
        
        if (adData != null) {
            titleView.setText(adData.getTitle());
            descView.setText(adData.getDescription());
            
            if (adData.getImageUrl() != null) {
                Glide.with(getContext())
                        .load(adData.getImageUrl())
                        .into(imageView);
            }
        }
    }
    
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
                // 忽略
            }
        }
    }
    
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
