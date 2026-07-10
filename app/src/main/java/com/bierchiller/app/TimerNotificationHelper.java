package com.bierchiller.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public final class TimerNotificationHelper {
    static final String ACTION_STOP_TIMER = "com.bierchiller.app.STOP_TIMER";
    static final String ACTION_START_TIMER = "com.bierchiller.app.START_TIMER";
    static final String EXTRA_END_TIME_MILLIS = "extra_end_time_millis";
    static final String EXTRA_TOTAL_DURATION_MILLIS = "extra_total_duration_millis";
    static final String EXTRA_START_TEMP_C = "extra_start_temp_c";
    static final String EXTRA_TARGET_TEMP_C = "extra_target_temp_c";
    static final String EXTRA_DEVICE_TEMP_C = "extra_device_temp_c";
    static final String EXTRA_DISPLAY_FAHRENHEIT = "extra_display_fahrenheit";

    private static final String CHANNEL_ID = "timer_channel_v3";
    static final int NOTIFICATION_ID = 43;
    private static final int ANDROID_16_API = 36;
    private static final double CONVECTION_EXPONENT = 0.15;
    private TimerNotificationHelper() {
    }

    static void show(Context context, long endTimeMillis, long totalDurationMillis) {
        show(
                context,
                endTimeMillis,
                totalDurationMillis,
                MainActivity.DEFAULT_START_TEMP,
                MainActivity.DEFAULT_TARGET_TEMP,
                MainActivity.DEFAULT_DEVICE_TEMP,
                MainActivity.localeUsesFahrenheit(Locale.getDefault())
        );
    }

    static void show(Context context, long endTimeMillis, long totalDurationMillis,
                     double startTempC, double targetTempC, double deviceTempC,
                     boolean displayFahrenheit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        createChannel(context);
        postDirectly(
                context,
                endTimeMillis,
                totalDurationMillis,
                startTempC,
                targetTempC,
                deviceTempC,
                displayFahrenheit
        );

        Intent serviceIntent = new Intent(context, TimerForegroundService.class)
                .setAction(ACTION_START_TIMER)
                .putExtra(EXTRA_END_TIME_MILLIS, endTimeMillis)
                .putExtra(EXTRA_TOTAL_DURATION_MILLIS, totalDurationMillis)
                .putExtra(EXTRA_START_TEMP_C, startTempC)
                .putExtra(EXTRA_TARGET_TEMP_C, targetTempC)
                .putExtra(EXTRA_DEVICE_TEMP_C, deviceTempC)
                .putExtra(EXTRA_DISPLAY_FAHRENHEIT, displayFahrenheit);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (RuntimeException ignored) {
            // The immediate fallback notification was already posted above.
        }
    }

    static Notification build(Context context, long endTimeMillis, long totalDurationMillis) {
        return build(
                context,
                endTimeMillis,
                totalDurationMillis,
                MainActivity.DEFAULT_START_TEMP,
                MainActivity.DEFAULT_TARGET_TEMP,
                MainActivity.DEFAULT_DEVICE_TEMP,
                MainActivity.localeUsesFahrenheit(Locale.getDefault())
        );
    }

    static Notification build(Context context, long endTimeMillis, long totalDurationMillis,
                              double startTempC, double targetTempC, double deviceTempC,
                              boolean displayFahrenheit) {
        return buildInternal(
                context,
                endTimeMillis,
                totalDurationMillis,
                startTempC,
                targetTempC,
                deviceTempC,
                displayFahrenheit
        );
    }

    private static Notification buildInternal(Context context, long endTimeMillis, long totalDurationMillis,
                                              double startTempC, double targetTempC, double deviceTempC,
                                              boolean displayFahrenheit) {
        return buildInternal(
                context,
                endTimeMillis,
                totalDurationMillis,
                startTempC,
                targetTempC,
                deviceTempC,
                displayFahrenheit,
                supportsPromotedOngoing()
        );
    }

    private static Notification buildInternal(Context context, long endTimeMillis, long totalDurationMillis,
                                              double startTempC, double targetTempC, double deviceTempC,
                                              boolean displayFahrenheit,
                                              boolean usePromotedOngoing) {
        Intent openIntent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPendingIntent = PendingIntent.getActivity(
                context,
                3001,
                openIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        Intent stopIntent = new Intent(context, TimerActionReceiver.class)
                .setAction(ACTION_STOP_TIMER)
                .putExtra(EXTRA_END_TIME_MILLIS, endTimeMillis)
                .putExtra(EXTRA_TOTAL_DURATION_MILLIS, totalDurationMillis);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context,
                3002,
                stopIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        long remainingMillis = Math.max(0L, endTimeMillis - System.currentTimeMillis());
        String remainingText = formatRemainingClock(remainingMillis);
        String currentTempText = formatTemperatureDecimal(
                context,
                calculateCurrentBeerTemperature(
                        startTempC,
                        targetTempC,
                        deviceTempC,
                        endTimeMillis,
                        totalDurationMillis
                ),
                displayFahrenheit
        );
        String targetTempText = formatTemperatureDecimal(context, targetTempC, displayFahrenheit);
        Notification publicVersion = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_beer_mug_button)
                .setColor(resolveAppAccentColor(context))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(statusText(remainingText, currentTempText))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        if (usePromotedOngoing) {
            return new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_beer_mug_button)
                    .setColor(resolveAppAccentColor(context))
                    .setContentTitle(context.getString(R.string.running))
                    .setContentText("⏱ " + remainingText + "  🌡 " + currentTempText + " → " + targetTempText)

                    .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPublicVersion(publicVersion)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setShortCriticalText(remainingText)
                    .setContentIntent(openPendingIntent)
                    .addAction(
                            R.drawable.ic_beer_mug_button,
                            context.getString(R.string.stop_timer),
                            stopPendingIntent
                    )
                    .setRequestPromotedOngoing(true)
                    .setTimeoutAfter(remainingMillis)
                    .build();
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_beer_mug_button)
                .setColor(resolveAppAccentColor(context))
                .setContentTitle(context.getString(R.string.running))
                .setContentText(statusText(remainingText, currentTempText) + " → " + targetTempText)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPublicVersion(publicVersion)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setContentIntent(openPendingIntent)
                .addAction(
                        R.drawable.ic_beer_mug_button,
                        context.getString(R.string.stop_timer),
                        stopPendingIntent
                )
                .setTimeoutAfter(remainingMillis)
                .build();
    }

    private static int resolveAppAccentColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_accent);
    }

    private static String statusText(String remainingText, String currentTempText) {
        return "⏱" + remainingText + "🌡 " + currentTempText;
    }

    static void postDirectly(Context context, long endTimeMillis, long totalDurationMillis) {
        postDirectly(
                context,
                endTimeMillis,
                totalDurationMillis,
                MainActivity.DEFAULT_START_TEMP,
                MainActivity.DEFAULT_TARGET_TEMP,
                MainActivity.DEFAULT_DEVICE_TEMP,
                MainActivity.localeUsesFahrenheit(Locale.getDefault())
        );
    }

    static void postDirectly(Context context, long endTimeMillis, long totalDurationMillis,
                             double startTempC, double targetTempC, double deviceTempC,
                             boolean displayFahrenheit) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            try {
                manager.notify(NOTIFICATION_ID, build(
                        context,
                        endTimeMillis,
                        totalDurationMillis,
                        startTempC,
                        targetTempC,
                        deviceTempC,
                        displayFahrenheit
                ));
            } catch (RuntimeException ignored) {
                try {
                    manager.notify(NOTIFICATION_ID, buildInternal(
                            context,
                            endTimeMillis,
                            totalDurationMillis,
                            startTempC,
                            targetTempC,
                            deviceTempC,
                            displayFahrenheit,
                            false
                    ));
                } catch (RuntimeException ignoredAgain) {
                    // Keep the timer running even if this platform rejects the notification.
                }
            }
        }
    }

    static void cancel(Context context) {
        try {
            context.stopService(new Intent(context, TimerForegroundService.class));
        } catch (RuntimeException ignored) {
            // The timer notification is also cancelled below for devices without the service.
        }
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
    }

    static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(context.getString(R.string.timer_channel_description));
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    private static int pendingIntentFlags(int baseFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return baseFlags | PendingIntent.FLAG_IMMUTABLE;
        }
        return baseFlags;
    }

    static void stopTimer(Context context) {
        TimerAlarmScheduler.cancel(context);
        context.stopService(new Intent(context, AlarmService.class));
        context.getSharedPreferences("bierchiller", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(MainActivity.KEY_ALARM_DISMISSED, true)
                .remove("endTimeMillis")
                .remove("totalDurationMillis")
                .apply();
        cancel(context);
    }

    private static boolean supportsPromotedOngoing() {
        return Build.VERSION.SDK_INT >= ANDROID_16_API;
    }

    private static String formatRemainingClock(long remainingMillis) {
        long totalSeconds = Math.max(0L, (remainingMillis + 999L) / 1000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        if (minutes >= 100L) {
            long hours = minutes / 60L;
            long remainingMinutes = minutes % 60L;
            return String.format(Locale.US, "%d:%02d h", hours, remainingMinutes);
        }
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private static double calculateCurrentBeerTemperature(double startTempC, double targetTempC,
                                                          double deviceTempC, long endTimeMillis,
                                                          long totalDurationMillis) {
        if (totalDurationMillis <= 0L || deviceTempC <= -273.15 || targetTempC <= deviceTempC) {
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

        double elapsedMillis = Math.max(0L, totalDurationMillis - (endTimeMillis - System.currentTimeMillis()));
        double elapsedSeconds = Math.min(totalDurationMillis, elapsedMillis) / 1000.0;
        double tauEffectiveSeconds = (totalDurationMillis / 1000.0) / temperatureTerm;
        double theta = Math.pow(
                1.0 + CONVECTION_EXPONENT * elapsedSeconds / tauEffectiveSeconds,
                -1.0 / CONVECTION_EXPONENT
        );
        double temperature = deviceTempC + (startTempC - deviceTempC) * theta;
        return Math.max(targetTempC, Math.min(startTempC, temperature));
    }

    private static String formatTemperatureDecimal(Context context, double celsius,
                                                   boolean displayFahrenheit) {
        if (displayFahrenheit) {
            return context.getString(
                    R.string.degrees_fahrenheit_decimal,
                    (float) (celsius * 9.0 / 5.0 + 32.0)
            );
        }
        return context.getString(R.string.degrees_celsius_decimal, (float) celsius);
    }
}
