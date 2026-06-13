package com.bierchiller.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public final class TimerNotificationHelper {
    static final String ACTION_STOP_TIMER = "com.bierchiller.app.STOP_TIMER";

    private static final String CHANNEL_ID = "timer_channel_v3";
    private static final int NOTIFICATION_ID = 43;

    private TimerNotificationHelper() {
    }

    static void show(Context context, long endTimeMillis, long totalDurationMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        createChannel(context);

        Intent openIntent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPendingIntent = PendingIntent.getActivity(
                context,
                3001,
                openIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        Intent stopIntent = new Intent(context, MainActivity.class)
                .setAction(ACTION_STOP_TIMER)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent stopPendingIntent = PendingIntent.getActivity(
                context,
                3002,
                stopIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        String endText = context.getString(R.string.ends_at, MainActivity.formatTime(context, endTimeMillis));
        String shortText = MainActivity.formatTime(context, endTimeMillis);
        int progress = calculateProgress(endTimeMillis, totalDurationMillis);
        NotificationCompat.ProgressStyle progressStyle = new NotificationCompat.ProgressStyle()
                .setProgress(progress)
                .setStyledByProgress(true)
                .addProgressSegment(new NotificationCompat.ProgressStyle.Segment(1000)
                        .setColor(Color.parseColor("#EAB400")))
                .addProgressPoint(new NotificationCompat.ProgressStyle.Point(progress)
                        .setColor(Color.parseColor("#123B4A")));

        Notification publicVersion = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(endText)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(endText)
                .setSubText(context.getString(R.string.remaining_time))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPublicVersion(publicVersion)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .setWhen(endTimeMillis)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setContentIntent(openPendingIntent)
                .setStyle(progressStyle)
                .setRequestPromotedOngoing(true)
                .setShortCriticalText(shortText)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        context.getString(R.string.stop_timer),
                        stopPendingIntent)
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    static void cancel(Context context) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
    }

    private static void createChannel(Context context) {
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

    private static int calculateProgress(long endTimeMillis, long totalDurationMillis) {
        if (totalDurationMillis <= 0L) {
            return 0;
        }
        long elapsedMillis = Math.max(0L, totalDurationMillis - (endTimeMillis - System.currentTimeMillis()));
        return Math.max(0, Math.min(1000, (int) ((elapsedMillis * 1000L) / totalDurationMillis)));
    }
}
