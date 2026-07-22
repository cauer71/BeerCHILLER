package com.bierchiller.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public final class TimerForegroundService extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long endTimeMillis;
    private long totalDurationMillis;
    private double startTempC;
    private double targetTempC;
    private double deviceTempC;
    private boolean displayFahrenheit;
    private boolean foregroundStarted;
    private long lastWidgetUpdateMillis;

    private final Runnable updateNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            if (endTimeMillis <= System.currentTimeMillis()) {
                stopTimerService();
                return;
            }

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(TimerNotificationHelper.NOTIFICATION_ID, buildTimerNotification());
            }
            long now = System.currentTimeMillis();
            if (now - lastWidgetUpdateMillis >= 30_000L) {
                BeerChillerWidgetProvider.updateAll(TimerForegroundService.this);
                lastWidgetUpdateMillis = now;
            }
            handler.postDelayed(this, 1000L);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TimerNotificationHelper.createChannel(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && TimerNotificationHelper.ACTION_STOP_TIMER.equals(intent.getAction())) {
            SharedPreferences preferences = getSharedPreferences("bierchiller", MODE_PRIVATE);
            preferences.edit()
                    .remove("endTimeMillis")
                    .remove("totalDurationMillis")
                    .apply();
            TimerAlarmScheduler.cancel(this);
            BeerChillerWidgetProvider.updateAll(this);
            stopTimerService();
            return START_NOT_STICKY;
        }

        endTimeMillis = 0L;
        totalDurationMillis = 0L;
        startTempC = MainActivity.DEFAULT_START_TEMP;
        targetTempC = MainActivity.DEFAULT_TARGET_TEMP;
        deviceTempC = MainActivity.DEFAULT_DEVICE_TEMP;
        displayFahrenheit = MainActivity.localeUsesFahrenheit(java.util.Locale.getDefault());
        if (intent != null) {
            endTimeMillis = intent.getLongExtra(TimerNotificationHelper.EXTRA_END_TIME_MILLIS, 0L);
            totalDurationMillis = intent.getLongExtra(TimerNotificationHelper.EXTRA_TOTAL_DURATION_MILLIS, 0L);
            startTempC = intent.getDoubleExtra(TimerNotificationHelper.EXTRA_START_TEMP_C, startTempC);
            targetTempC = intent.getDoubleExtra(TimerNotificationHelper.EXTRA_TARGET_TEMP_C, targetTempC);
            deviceTempC = intent.getDoubleExtra(TimerNotificationHelper.EXTRA_DEVICE_TEMP_C, deviceTempC);
            displayFahrenheit = intent.getBooleanExtra(
                    TimerNotificationHelper.EXTRA_DISPLAY_FAHRENHEIT,
                    displayFahrenheit
            );
        }

        if (endTimeMillis <= System.currentTimeMillis()) {
            stopTimerService();
            return START_NOT_STICKY;
        }

        try {
            Notification notification = buildTimerNotification();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                        TimerNotificationHelper.NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                );
            } else {
                startForeground(TimerNotificationHelper.NOTIFICATION_ID, notification);
            }
            foregroundStarted = true;
            lastWidgetUpdateMillis = System.currentTimeMillis();
            BeerChillerWidgetProvider.updateAll(this);
            handler.removeCallbacks(updateNotificationRunnable);
            handler.postDelayed(updateNotificationRunnable, 1000L);
        } catch (RuntimeException ignored) {
            TimerNotificationHelper.postDirectly(
                    this,
                    endTimeMillis,
                    totalDurationMillis,
                    startTempC,
                    targetTempC,
                    deviceTempC,
                    displayFahrenheit
            );
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateNotificationRunnable);
        removeForegroundNotification();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTimerService() {
        BeerChillerWidgetProvider.updateAll(this);
        removeForegroundNotification();
        stopSelf();
    }

    private void removeForegroundNotification() {
        if (!foregroundStarted) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        foregroundStarted = false;
    }

    private Notification buildTimerNotification() {
        return TimerNotificationHelper.build(
                this,
                endTimeMillis,
                totalDurationMillis,
                startTempC,
                targetTempC,
                deviceTempC,
                displayFahrenheit
        );
    }
}
