package com.bierchiller.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Locale;

public final class TimerBootReceiver extends BroadcastReceiver {
    private static final String PREFS = "bierchiller";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            return;
        }

        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long endTimeMillis = preferences.getLong("endTimeMillis", 0L);
        if (endTimeMillis <= 0L) {
            return;
        }

        long now = System.currentTimeMillis();
        if (endTimeMillis <= now) {
            TimerAlarmScheduler.schedule(context, now + 1_000L);
            return;
        }

        long totalDurationMillis = preferences.getLong(
                "totalDurationMillis",
                Math.max(1L, endTimeMillis - now)
        );
        int temperatureUnit = preferences.getInt("temperatureUnit", 0);
        boolean displayFahrenheit = temperatureUnit == 2
                || (temperatureUnit == 0 && MainActivity.localeUsesFahrenheit(Locale.getDefault()));

        TimerAlarmScheduler.schedule(context, endTimeMillis);
        TimerNotificationHelper.show(
                context,
                endTimeMillis,
                totalDurationMillis,
                preferences.getInt("startTemp", MainActivity.DEFAULT_START_TEMP),
                preferences.getInt("targetTemp", MainActivity.DEFAULT_TARGET_TEMP),
                preferences.getInt("deviceTemp", MainActivity.DEFAULT_DEVICE_TEMP),
                displayFahrenheit
        );
    }
}
