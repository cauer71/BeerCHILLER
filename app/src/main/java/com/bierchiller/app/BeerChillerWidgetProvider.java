package com.bierchiller.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
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
    private static final String KEY_TARGET_TEMP = "targetTemp";
    private static final String KEY_TEMPERATURE_UNIT = "temperatureUnit";
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

        if (running) {
            long remainingMillis = Math.max(0L, endTimeMillis - now);
            long chronometerBase = SystemClock.elapsedRealtime() + remainingMillis;
            views.setViewVisibility(R.id.widgetCountdown, View.VISIBLE);
            views.setViewVisibility(R.id.widgetIdleTime, View.GONE);
            views.setTextViewText(R.id.widgetStateLabel,
                    localizedContext.getString(R.string.remaining_time));
            views.setChronometer(R.id.widgetCountdown, chronometerBase, null, true);
            views.setTextViewText(R.id.widgetEndTime,
                    localizedContext.getString(
                            R.string.ends_at,
                            DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(endTimeMillis))
                    ));
            views.setTextViewText(R.id.widgetTarget,
                    localizedContext.getString(
                            R.string.widget_target,
                            formatTargetTemperature(localizedContext, preferences)
                    ));
            views.setTextViewText(R.id.widgetAction, localizedContext.getString(R.string.stop));
            views.setOnClickPendingIntent(
                    R.id.widgetAction,
                    PendingIntent.getBroadcast(
                            context,
                            7000 + appWidgetId,
                            new Intent(context, BeerChillerWidgetProvider.class)
                                    .setAction(ACTION_STOP_TIMER),
                            pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
                    )
            );
        } else {
            views.setViewVisibility(R.id.widgetCountdown, View.GONE);
            views.setViewVisibility(R.id.widgetIdleTime, View.VISIBLE);
            views.setTextViewText(R.id.widgetIdleTime, finished ? "00:00" : "--:--");
            views.setTextViewText(
                    R.id.widgetStateLabel,
                    localizedContext.getString(finished ? R.string.alarm_ringing : R.string.status_ready)
            );
            views.setTextViewText(
                    R.id.widgetEndTime,
                    localizedContext.getString(finished ? R.string.alarm_detail : R.string.widget_tap_to_start)
            );
            views.setTextViewText(R.id.widgetTarget, localizedContext.getString(R.string.app_name));
            views.setTextViewText(R.id.widgetAction, localizedContext.getString(R.string.widget_open_app));
            views.setOnClickPendingIntent(R.id.widgetAction, openAppPendingIntent(context, appWidgetId));
        }

        return views;
    }

    private static void applyVisualStyle(RemoteViews views, boolean classic) {
        int primaryText = classic ? 0xFF123B4A : 0xFF4A2509;
        int accentText = classic ? 0xFF1F5872 : 0xFFD99C00;
        views.setInt(
                R.id.widgetRoot,
                "setBackgroundResource",
                classic ? R.drawable.widget_bg_classic : R.drawable.widget_bg_beer
        );
        views.setImageViewResource(
                R.id.widgetLogo,
                classic ? R.drawable.ic_snowflake : R.drawable.ic_hops_vr2
        );
        views.setTextColor(R.id.widgetBeerText, primaryText);
        views.setTextColor(R.id.widgetChillerText, accentText);
        views.setTextColor(R.id.widgetStateLabel, primaryText);
        views.setTextColor(R.id.widgetCountdown, primaryText);
        views.setTextColor(R.id.widgetIdleTime, primaryText);
        views.setTextColor(R.id.widgetEndTime, primaryText);
        views.setTextColor(R.id.widgetTarget, primaryText);
        views.setTextColor(R.id.widgetAction, 0xFFFFFFFF);
        views.setInt(
                R.id.widgetAction,
                "setBackgroundResource",
                classic ? R.drawable.widget_action_classic : R.drawable.widget_action_beer
        );
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
        int unit = preferences.getInt(KEY_TEMPERATURE_UNIT, 0);
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = context.getResources().getConfiguration().locale;
        }
        boolean fahrenheit = unit == UNIT_FAHRENHEIT
                || (unit == 0 && MainActivity.localeUsesFahrenheit(locale));
        if (fahrenheit) {
            int converted = (int) Math.round(celsius * 9.0 / 5.0 + 32.0);
            return context.getString(R.string.degrees_fahrenheit, converted);
        }
        return context.getString(R.string.degrees_celsius, celsius);
    }

    private static int pendingIntentFlags(int baseFlags) {
        return baseFlags | PendingIntent.FLAG_IMMUTABLE;
    }
}
