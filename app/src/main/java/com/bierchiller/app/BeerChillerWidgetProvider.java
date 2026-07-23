package com.bierchiller.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public final class BeerChillerWidgetProvider extends AppWidgetProvider {
    static final String ACTION_STOP_TIMER = "com.bierchiller.app.action.WIDGET_STOP_TIMER";

    private static final String PREFS = "bierchiller";
    private static final String KEY_END_TIME = "endTimeMillis";
    private static final String KEY_TOTAL_DURATION = "totalDurationMillis";
    private static final String KEY_VISUAL_MODE = "visualMode";
    private static final String KEY_START_TEMP = "startTemp";
    private static final String KEY_TARGET_TEMP = "targetTemp";
    private static final String KEY_DEVICE_TEMP = "deviceTemp";
    private static final String KEY_TEMPERATURE_UNIT = "temperatureUnit";
    private static final double CONVECTION_EXPONENT = 0.15;
    private static final int VISUAL_CLASSIC = 0;
    private static final int UNIT_FAHRENHEIT = 2;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildRemoteViews(context, appWidgetId));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if (ACTION_STOP_TIMER.equals(action)) {
            stopTimer(context);
            updateAll(context);
            return;
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            clearStoredTimer(context);
            updateAll(context);
            return;
        }
        super.onReceive(context, intent);
        if (Intent.ACTION_LOCALE_CHANGED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            updateAll(context);
        }
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        android.content.ComponentName provider =
                new android.content.ComponentName(context, BeerChillerWidgetProvider.class);
        int[] appWidgetIds = manager.getAppWidgetIds(provider);
        for (int appWidgetId : appWidgetIds) {
            manager.updateAppWidget(appWidgetId, buildRemoteViews(context, appWidgetId));
        }
    }

    private static RemoteViews buildRemoteViews(Context context, int appWidgetId) {
        Context localizedContext = LocaleHelper.wrap(context);
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean classic = readVisualMode(preferences) == VISUAL_CLASSIC;
        long now = System.currentTimeMillis();
        long endTimeMillis = preferences.getLong(KEY_END_TIME, 0L);
        long totalDurationMillis = preferences.getLong(KEY_TOTAL_DURATION, 0L);
        boolean running = totalDurationMillis > 0L && endTimeMillis > now;
        boolean finished = totalDurationMillis > 0L && endTimeMillis > 0L && endTimeMillis <= now;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_timer);
        applyVisualStyle(views, classic);
        bindOpenApp(views, context, appWidgetId);
        float ringProgress = running
                ? Math.max(0f, Math.min(1f, (endTimeMillis - now) / (float) totalDurationMillis))
                : 0f;
        views.setImageViewBitmap(R.id.widgetRing, createRingBitmap(context, classic, ringProgress));

        if (running) {
            long remainingMillis = Math.max(0L, endTimeMillis - now);
            long chronometerBase = SystemClock.elapsedRealtime() + remainingMillis;
            float countdownTextSize = totalDurationMillis >= 100L * 60L * 1000L ? 17f : 23f;
            views.setViewVisibility(R.id.widgetCountdown, View.VISIBLE);
            views.setViewVisibility(R.id.widgetIdleTime, View.GONE);
            views.setViewVisibility(R.id.widgetIdleMug, View.GONE);
            views.setViewVisibility(R.id.widgetBottomRow, View.VISIBLE);
            views.setTextViewTextSize(
                    R.id.widgetCountdown,
                    TypedValue.COMPLEX_UNIT_SP,
                    countdownTextSize
            );
            views.setChronometer(R.id.widgetCountdown, chronometerBase, null, true);
            views.setTextViewText(
                    R.id.widgetEndTime,
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(endTimeMillis))
            );
            views.setTextViewText(R.id.widgetTarget,
                    formatCurrentTemperature(
                            localizedContext,
                            preferences,
                            endTimeMillis,
                            totalDurationMillis
                    ));
        } else {
            views.setViewVisibility(R.id.widgetCountdown, View.GONE);
            views.setViewVisibility(R.id.widgetIdleTime, View.GONE);
            views.setViewVisibility(R.id.widgetIdleMug, View.VISIBLE);
            views.setViewVisibility(R.id.widgetBottomRow, View.GONE);
            views.setTextViewTextSize(
                    R.id.widgetIdleTime,
                    TypedValue.COMPLEX_UNIT_SP,
                    23f
            );
            views.setTextViewText(R.id.widgetIdleTime, finished ? "00:00" : "--:--");
            views.setTextViewText(R.id.widgetEndTime, "--:--");
        }

        return views;
    }

    private static void applyVisualStyle(RemoteViews views, boolean classic) {
        int primaryText = classic ? 0xFF123B4A : 0xFF4A2509;
        int accentText = classic ? 0xFF1F5872 : 0xFFD99C00;
        int ringText = classic ? 0xFF123B4A : 0xFFFFFFFF;
        views.setInt(
                R.id.widgetRoot,
                "setBackgroundResource",
                classic ? R.drawable.widget_bg_classic : R.drawable.widget_bg_beer
        );
        views.setImageViewResource(
                R.id.widgetLogo,
                classic ? R.drawable.ic_snowflake : R.drawable.ic_hops_vr2
        );
        views.setImageViewResource(R.id.widgetIdleMug, R.drawable.ic_beer_mug_button);
        views.setImageViewResource(R.id.widgetTemperatureIcon, R.drawable.ic_temp_vr2);
        views.setInt(R.id.widgetIdleMug, "setColorFilter", ringText);
        views.setInt(R.id.widgetTemperatureIcon, "setColorFilter", primaryText);
        views.setTextColor(R.id.widgetBeerText, primaryText);
        views.setTextColor(R.id.widgetChillerText, accentText);
        views.setTextColor(R.id.widgetCountdown, ringText);
        views.setTextColor(R.id.widgetIdleTime, ringText);
        views.setTextColor(R.id.widgetEndTime, primaryText);
        views.setTextColor(R.id.widgetTarget, primaryText);
    }

    private static Bitmap createRingBitmap(Context context, boolean classic, float progress) {
        float density = context.getResources().getDisplayMetrics().density;
        int size = Math.max(1, Math.round(100f * density));
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmap.setDensity(context.getResources().getDisplayMetrics().densityDpi);
        Canvas canvas = new Canvas(bitmap);
        float center = size / 2f;
        float outerRadius = center - 2.5f * density;
        Paint tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeCap(Paint.Cap.ROUND);
        tickPaint.setStrokeWidth(Math.max(1f, 1.2f * density));
        tickPaint.setColor(classic ? 0xFFDCECEF : 0xFFDFF6F3);
        int tickCount = 72;
        for (int i = 0; i < tickCount; i++) {
            double angle = Math.toRadians(-90f + i * (360f / tickCount));
            float tickLength = (i % 6 == 0 ? 6f : 4f) * density;
            float innerRadius = outerRadius - tickLength;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            canvas.drawLine(
                    center + cos * innerRadius,
                    center + sin * innerRadius,
                    center + cos * outerRadius,
                    center + sin * outerRadius,
                    tickPaint
            );
        }
        if (progress > 0f) {
            Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStrokeWidth(Math.max(2f, 4f * density));
            progressPaint.setColor(classic ? 0xFF123B4A : 0xFFFFFFFF);
            float progressRadius = outerRadius - 5f * density;
            RectF progressRing = new RectF(
                    center - progressRadius,
                    center - progressRadius,
                    center + progressRadius,
                    center + progressRadius
            );
            canvas.drawArc(progressRing, -90f, 360f * progress, false, progressPaint);
        }
        return bitmap;
    }

    private static void bindOpenApp(RemoteViews views, Context context, int appWidgetId) {
        views.setOnClickPendingIntent(R.id.widgetRoot, openAppPendingIntent(context, appWidgetId));
    }

    private static PendingIntent openAppPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                context,
                6000 + appWidgetId,
                intent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    private static void stopTimer(Context context) {
        clearStoredTimer(context);
        TimerAlarmScheduler.cancel(context);
        TimerNotificationHelper.cancel(context);
        context.stopService(new Intent(context, TimerForegroundService.class));
        context.stopService(new Intent(context, AlarmService.class));
    }

    private static void clearStoredTimer(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_END_TIME)
                .remove(KEY_TOTAL_DURATION)
                .apply();
    }

    private static int readVisualMode(SharedPreferences preferences) {
        Object value = preferences.getAll().get(KEY_VISUAL_MODE);
        if (value instanceof Integer) {
            return (Integer) value == VISUAL_CLASSIC ? VISUAL_CLASSIC : 1;
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : VISUAL_CLASSIC;
        }
        return 1;
    }

    private static String formatTargetTemperature(Context context, SharedPreferences preferences) {
        int celsius = preferences.getInt(KEY_TARGET_TEMP, MainActivity.DEFAULT_TARGET_TEMP);
        return formatTemperature(context, celsius, preferences);
    }

    private static String formatCurrentTemperature(Context context, SharedPreferences preferences,
                                                   long endTimeMillis, long totalDurationMillis) {
        int startTempC = preferences.getInt(KEY_START_TEMP, MainActivity.DEFAULT_START_TEMP);
        int targetTempC = preferences.getInt(KEY_TARGET_TEMP, MainActivity.DEFAULT_TARGET_TEMP);
        int deviceTempC = preferences.getInt(KEY_DEVICE_TEMP, MainActivity.DEFAULT_DEVICE_TEMP);
        double currentTemperatureC = calculateCurrentBeerTemperature(
                startTempC,
                targetTempC,
                deviceTempC,
                endTimeMillis,
                totalDurationMillis
        );
        if (usesFahrenheit(context, preferences)) {
            return context.getString(
                    R.string.degrees_fahrenheit_decimal,
                    (float) (currentTemperatureC * 9.0 / 5.0 + 32.0)
            );
        }
        return context.getString(R.string.degrees_celsius_decimal, (float) currentTemperatureC);
    }

    private static String formatTemperature(Context context, int celsius,
                                            SharedPreferences preferences) {
        if (usesFahrenheit(context, preferences)) {
            int converted = (int) Math.round(celsius * 9.0 / 5.0 + 32.0);
            return context.getString(R.string.degrees_fahrenheit, converted);
        }
        return context.getString(R.string.degrees_celsius, celsius);
    }

    private static boolean usesFahrenheit(Context context, SharedPreferences preferences) {
        int unit = preferences.getInt(KEY_TEMPERATURE_UNIT, 0);
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = context.getResources().getConfiguration().locale;
        }
        return unit == UNIT_FAHRENHEIT
                || (unit == 0 && MainActivity.localeUsesFahrenheit(locale));
    }

    private static double calculateCurrentBeerTemperature(int startTempC, int targetTempC,
                                                          int deviceTempC, long endTimeMillis,
                                                          long totalDurationMillis) {
        if (totalDurationMillis <= 0L || deviceTempC <= -273 || targetTempC <= deviceTempC) {
            return targetTempC;
        }
        if (targetTempC >= startTempC) {
            return targetTempC;
        }

        double delta0 = startTempC - deviceTempC;
        if (delta0 <= 0.0) {
            return targetTempC;
        }
        double thetaTarget = (targetTempC - deviceTempC) / delta0;
        if (!Double.isFinite(thetaTarget) || thetaTarget <= 0.0 || thetaTarget >= 1.0) {
            return targetTempC;
        }

        double temperatureTerm = (Math.pow(thetaTarget, -CONVECTION_EXPONENT) - 1.0)
                / CONVECTION_EXPONENT;
        if (!Double.isFinite(temperatureTerm) || temperatureTerm <= 0.0) {
            return targetTempC;
        }

        double elapsedMillis = Math.max(
                0L,
                totalDurationMillis - (endTimeMillis - System.currentTimeMillis())
        );
        double elapsedSeconds = Math.min(totalDurationMillis, elapsedMillis) / 1000.0;
        double tauEffectiveSeconds = (totalDurationMillis / 1000.0) / temperatureTerm;
        double theta = Math.pow(
                1.0 + CONVECTION_EXPONENT * elapsedSeconds / tauEffectiveSeconds,
                -1.0 / CONVECTION_EXPONENT
        );
        double temperature = deviceTempC + (startTempC - deviceTempC) * theta;
        return Math.max(targetTempC, Math.min(startTempC, temperature));
    }

    private static int pendingIntentFlags(int baseFlags) {
        return baseFlags | PendingIntent.FLAG_IMMUTABLE;
    }
}
