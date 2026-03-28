package com.zzitcompany.adsdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 关闭按钮视图
 */
public class CloseButton extends View {
    
    private Paint paint;
    private Paint strokePaint;
    private int size = 24; // dp
    private int padding = 4; // dp
    
    public CloseButton(Context context) {
        super(context);
        init();
    }
    
    public CloseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public CloseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#80000000"));
        paint.setStyle(Paint.Style.FILL);
        
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(dpToPx(2));
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        
        setClickable(true);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizePx = dpToPx(size + padding * 2);
        setMeasuredDimension(sizePx, sizePx);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        
        // 绘制圆形背景
        canvas.drawCircle(centerX, centerY, dpToPx(size / 2f), paint);
        
        // 绘制X图标
        int iconSize = dpToPx(6);
        canvas.drawLine(centerX - iconSize, centerY - iconSize,
                       centerX + iconSize, centerY + iconSize, strokePaint);
        canvas.drawLine(centerX + iconSize, centerY - iconSize,
                       centerX - iconSize, centerY + iconSize, strokePaint);
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
