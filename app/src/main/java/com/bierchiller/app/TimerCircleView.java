package com.bierchiller.app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class TimerCircleView extends View {
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String mainText = "-- min";
    private String labelText = "K\u00fchlzeit";
    private String detailText = "";
    private String temperatureText = "";
    private String temperatureLabelText = "";
    private float progress = 1f;
    private boolean running;
    private boolean valid = true;
    private boolean backgroundVisible;
    private int visualMode;

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
        mainText = getResources().getString(R.string.no_timer);
        labelText = getResources().getString(R.string.cooling_time);

        glowPaint.setStyle(Paint.Style.FILL);

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#FFFFFFFF"));
        fillPaint.setShadowLayer(dp(12), 0, dp(4), Color.parseColor("#26000000"));

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(dp(12));
        trackPaint.setColor(Color.parseColor("#E7F2F7"));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(dp(12));

        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeCap(Paint.Cap.ROUND);
        highlightPaint.setStrokeWidth(dp(2));
        highlightPaint.setColor(Color.parseColor("#99FFE066"));

        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeCap(Paint.Cap.ROUND);
        tickPaint.setStrokeWidth(dp(2));

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
        setTimerState(mainText, labelText, detailText, "", "", progress, running, valid);
    }

    public void setTimerState(String mainText, String labelText, String detailText,
                              String temperatureText, String temperatureLabelText,
                              float progress, boolean running, boolean valid) {
        this.mainText = mainText;
        this.labelText = labelText;
        this.detailText = detailText;
        this.temperatureText = temperatureText;
        this.temperatureLabelText = temperatureLabelText;
        this.progress = Math.max(0f, Math.min(1f, progress));
        this.running = running;
        this.valid = valid;
        invalidate();
    }

    public void setBackgroundVisible(boolean backgroundVisible) {
        this.backgroundVisible = backgroundVisible;
        this.visualMode = backgroundVisible ? 1 : 0;
        invalidate();
    }

    public void setVisualMode(int visualMode) {
        this.visualMode = visualMode;
        this.backgroundVisible = visualMode != 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float stroke = dp(12);
        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean vr2 = visualMode == 1;
        float sizeScale = landscape ? (vr2 ? 1.0f : 0.80f) : (vr2 ? 0.96f : 0.88f);
        float size = (Math.min(getWidth(), getHeight()) - stroke - dp(4)) * sizeScale;
        if (size <= 0f) {
            return;
        }

        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;
        RectF oval = new RectF(left, top, left + size, top + size);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f + (vr2 ? dp(14) : -dp(8));
        oval.offset(0, centerY - getHeight() / 2f);
        float radius = size / 2f;

        if (backgroundVisible && !vr2) {
            float glowRadius = radius * 1.28f;
            glowPaint.setShader(new RadialGradient(
                    centerX,
                    centerY,
                    glowRadius,
                    Color.parseColor("#66FFF3C7"),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
            ));
            glowPaint.setShadowLayer(dp(34), 0, 0, Color.parseColor("#66FFE066"));
            canvas.drawCircle(centerX, centerY, glowRadius, glowPaint);
            glowPaint.clearShadowLayer();
            glowPaint.setShader(null);
        }

        if (!vr2) {
            fillPaint.setColor(backgroundVisible
                    ? Color.parseColor("#CCFFF6DA")
                    : Color.parseColor("#FFFFFFFF"));
            fillPaint.setShadowLayer(
                    dp(12),
                    0,
                    dp(4),
                    backgroundVisible ? Color.parseColor("#30000000") : Color.parseColor("#26000000")
            );
            canvas.drawCircle(centerX, centerY, radius - stroke / 2f, fillPaint);
        }
        if (vr2) {
            drawTicks(canvas, centerX, centerY, radius);
        } else {
            trackPaint.setColor(backgroundVisible ? Color.parseColor("#F5E8A8") : Color.parseColor("#E7F2F7"));
            canvas.drawArc(oval, 0, 360, false, trackPaint);
        }
        progressPaint.setColor(valid
                ? (vr2 ? Color.parseColor("#FFFFFFFF") : (running ? Color.parseColor("#E8B923") : Color.parseColor("#123B4A")))
                : Color.parseColor("#D56B5D"));
        RectF progressOval = oval;
        float progressRadius = radius;
        if (vr2) {
            float progressInset = dp(17);
            progressOval = new RectF(
                    oval.left + progressInset,
                    oval.top + progressInset,
                    oval.right - progressInset,
                    oval.bottom - progressInset
            );
            progressRadius = radius - progressInset;
        }
        if (progress >= 0.999f) {
            canvas.drawCircle(centerX, centerY, progressRadius, progressPaint);
        } else if (progress > 0.001f) {
            canvas.drawArc(progressOval, -90, 360f * progress, false, progressPaint);
        }
        if (backgroundVisible && valid && !vr2) {
            RectF highlightOval = new RectF(
                    oval.left + stroke * 0.95f,
                    oval.top + stroke * 0.95f,
                    oval.right - stroke * 0.95f,
                    oval.bottom - stroke * 0.95f
            );
            canvas.drawArc(highlightOval, 205, 82, false, highlightPaint);
        }

        float textMaxWidth = size * 0.56f;
        mainTextPaint.setColor(vr2 ? Color.WHITE : Color.parseColor("#123B4A"));
        if (vr2) {
            mainTextPaint.setShadowLayer(dp(3), 0, dp(2), Color.parseColor("#99000000"));
            labelTextPaint.setShadowLayer(dp(2), 0, dp(1), Color.parseColor("#88000000"));
        } else {
            mainTextPaint.clearShadowLayer();
            labelTextPaint.clearShadowLayer();
        }
        mainTextPaint.setTextSize(fitTextSize(mainTextPaint, mainText, Math.min(sp(52), size * 0.21f), sp(24), textMaxWidth));
        labelTextPaint.setColor(vr2
                ? Color.WHITE
                : (backgroundVisible ? Color.parseColor("#123B4A") : Color.parseColor("#5F767B")));
        String visibleLabelText = vr2 ? labelText.toUpperCase(Locale.getDefault()) : labelText;
        labelTextPaint.setTextSize(fitTextSize(labelTextPaint, visibleLabelText, Math.min(sp(17), size * 0.08f), sp(9), textMaxWidth));
        Paint detailTextPaint = labelTextPaint;
        Paint temperatureTextPaint = mainTextPaint;
        Paint temperatureLabelPaint = labelTextPaint;

        Paint.FontMetrics mainMetrics = mainTextPaint.getFontMetrics();
        Paint.FontMetrics labelMetrics = labelTextPaint.getFontMetrics();
        boolean showTemperature = temperatureText != null && !temperatureText.isEmpty();
        float mainBaseline = centerY - Math.min(dp(showTemperature ? 30 : 2), size * (showTemperature ? 0.10f : 0.01f))
                - (mainMetrics.ascent + mainMetrics.descent) / 2f;
        float labelBaseline = centerY - Math.min(dp(showTemperature ? 90 : 78), size * (showTemperature ? 0.30f : 0.26f))
                - (labelMetrics.ascent + labelMetrics.descent) / 2f;
        float detailBaseline = centerY + Math.min(dp(showTemperature ? 24 : 43), size * (showTemperature ? 0.08f : 0.16f))
                - (labelMetrics.ascent + labelMetrics.descent) / 2f;

        canvas.drawText(mainText, centerX, mainBaseline, mainTextPaint);
        canvas.drawText(visibleLabelText, centerX, labelBaseline, labelTextPaint);
        if (detailText != null && !detailText.isEmpty()) {
            detailTextPaint.setTextSize(fitTextSize(detailTextPaint, detailText, Math.min(sp(16), size * 0.075f), sp(9), textMaxWidth));
            canvas.drawText(detailText, centerX, detailBaseline, detailTextPaint);
        }
        if (showTemperature) {
            temperatureTextPaint.setTextSize(fitTextSize(temperatureTextPaint, temperatureText, Math.min(sp(26), size * 0.105f), sp(15), textMaxWidth));
            Paint.FontMetrics tempMetrics = temperatureTextPaint.getFontMetrics();
            float tempBaseline = centerY + Math.min(dp(76), size * 0.24f)
                    - (tempMetrics.ascent + tempMetrics.descent) / 2f;
            canvas.drawText(temperatureText, centerX, tempBaseline, temperatureTextPaint);

            if (temperatureLabelText != null && !temperatureLabelText.isEmpty()) {
                String visibleTemperatureLabel = compactTemperatureLabel(temperatureLabelText);
                temperatureLabelPaint.setTextSize(fitTextSize(temperatureLabelPaint, visibleTemperatureLabel, Math.min(sp(11), size * 0.048f), sp(7), textMaxWidth));
                Paint.FontMetrics tempLabelMetrics = temperatureLabelPaint.getFontMetrics();
                float tempLabelBaseline = centerY + Math.min(dp(101), size * 0.32f)
                        - (tempLabelMetrics.ascent + tempLabelMetrics.descent) / 2f;
                canvas.drawText(visibleTemperatureLabel, centerX, tempLabelBaseline, temperatureLabelPaint);
            }
        }
    }

    private String compactTemperatureLabel(String text) {
        if (visualMode == 1 && text.toLowerCase(Locale.getDefault()).contains("temperatur")) {
            return "Temperatur";
        }
        return text;
    }

    private void drawTicks(Canvas canvas, float centerX, float centerY, float radius) {
        tickPaint.setColor(Color.parseColor("#DFF6F3"));
        float inner = radius - dp(24);
        float outer = radius - dp(11);
        for (int i = 0; i < 132; i++) {
            double angle = Math.toRadians(-90 + i * (360.0 / 132.0));
            canvas.drawLine(
                    centerX + (float) Math.cos(angle) * inner,
                    centerY + (float) Math.sin(angle) * inner,
                    centerX + (float) Math.cos(angle) * outer,
                    centerY + (float) Math.sin(angle) * outer,
                    tickPaint
            );
        }
    }

    private float fitTextSize(Paint paint, String text, float maxSize, float minSize, float maxWidth) {
        float textSize = maxSize;
        paint.setTextSize(textSize);
        while (textSize > minSize && paint.measureText(text) > maxWidth) {
            textSize -= sp(1);
            paint.setTextSize(textSize);
        }
        return textSize;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
