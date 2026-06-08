package com.example.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class TimerCircleView extends View {
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String mainText = "-- min";
    private String labelText = "K\u00fchlzeit";
    private String detailText = "";
    private float progress = 1f;
    private boolean running;
    private boolean valid = true;
    private boolean backgroundVisible;

    public TimerCircleView(Context context) {
        super(context);
        init();
    }

    public TimerCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        glowPaint.setStyle(Paint.Style.FILL);

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#FFFFFFFF"));
        fillPaint.setShadowLayer(dp(12), 0, dp(4), Color.parseColor("#26000000"));

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(dp(16));
        trackPaint.setColor(Color.parseColor("#E7F2F7"));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(dp(16));

        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeCap(Paint.Cap.ROUND);
        highlightPaint.setStrokeWidth(dp(2));
        highlightPaint.setColor(Color.parseColor("#99FFE066"));

        mainTextPaint.setColor(Color.parseColor("#123B4A"));
        mainTextPaint.setTextAlign(Paint.Align.CENTER);
        mainTextPaint.setFakeBoldText(true);

        labelTextPaint.setColor(Color.parseColor("#123B4A"));
        labelTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setTimerState(String mainText, String labelText, float progress, boolean running, boolean valid) {
        setTimerState(mainText, labelText, "", progress, running, valid);
    }

    public void setTimerState(String mainText, String labelText, String detailText,
                              float progress, boolean running, boolean valid) {
        this.mainText = mainText;
        this.labelText = labelText;
        this.detailText = detailText;
        this.progress = Math.max(0f, Math.min(1f, progress));
        this.running = running;
        this.valid = valid;
        invalidate();
    }

    public void setBackgroundVisible(boolean backgroundVisible) {
        this.backgroundVisible = backgroundVisible;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float stroke = dp(16);
        float size = (Math.min(getWidth(), getHeight()) - stroke - dp(4)) * 0.86f;
        if (size <= 0f) {
            return;
        }

        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;
        RectF oval = new RectF(left, top, left + size, top + size);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = size / 2f;

        if (backgroundVisible) {
            float glowRadius = radius * 1.28f;
            glowPaint.setShader(new RadialGradient(
                    centerX,
                    centerY,
                    glowRadius,
                    Color.parseColor("#A6FFE066"),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
            ));
            glowPaint.setShadowLayer(dp(34), 0, 0, Color.parseColor("#66FFE066"));
            canvas.drawCircle(centerX, centerY, glowRadius, glowPaint);
            glowPaint.clearShadowLayer();
            glowPaint.setShader(null);
        }

        fillPaint.setColor(backgroundVisible
                ? Color.parseColor("#80FFEEB9")
                : Color.parseColor("#FFFFFFFF"));
        fillPaint.setShadowLayer(
                backgroundVisible ? dp(16) : dp(12),
                0,
                backgroundVisible ? dp(5) : dp(4),
                backgroundVisible ? Color.parseColor("#40000000") : Color.parseColor("#26000000")
        );
        canvas.drawCircle(centerX, centerY, radius - stroke / 2f, fillPaint);
        trackPaint.setColor(backgroundVisible
                ? Color.parseColor("#F1EBD1")
                : Color.parseColor("#E7F2F7"));
        canvas.drawArc(oval, 0, 360, false, trackPaint);
        progressPaint.setColor(valid
                ? (running ? Color.parseColor("#E8B923") : Color.parseColor("#123B4A"))
                : Color.parseColor("#D56B5D"));
        if (progress >= 0.999f) {
            canvas.drawCircle(centerX, centerY, radius, progressPaint);
        } else {
            canvas.drawArc(oval, -90, 360f * progress, false, progressPaint);
        }
        if (backgroundVisible && valid) {
            RectF highlightOval = new RectF(
                    left + stroke * 0.75f,
                    top + stroke * 0.75f,
                    left + size - stroke * 0.75f,
                    top + size - stroke * 0.75f
            );
            canvas.drawArc(highlightOval, 205, 82, false, highlightPaint);
        }

        mainTextPaint.setTextSize(sp(58));
        labelTextPaint.setColor(backgroundVisible
                ? Color.parseColor("#123B4A")
                : Color.parseColor("#5F767B"));
        labelTextPaint.setTextSize(sp(18));
        Paint detailTextPaint = labelTextPaint;

        Paint.FontMetrics mainMetrics = mainTextPaint.getFontMetrics();
        Paint.FontMetrics labelMetrics = labelTextPaint.getFontMetrics();
        float mainBaseline = centerY - dp(2) - (mainMetrics.ascent + mainMetrics.descent) / 2f;
        float labelBaseline = centerY - dp(78) - (labelMetrics.ascent + labelMetrics.descent) / 2f;
        float detailBaseline = centerY + dp(43) - (labelMetrics.ascent + labelMetrics.descent) / 2f;

        canvas.drawText(mainText, centerX, mainBaseline, mainTextPaint);
        canvas.drawText(labelText, centerX, labelBaseline, labelTextPaint);
        if (detailText != null && !detailText.isEmpty()) {
            detailTextPaint.setTextSize(sp(18));
            canvas.drawText(detailText, centerX, detailBaseline, detailTextPaint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
