package com.bierchiller.app;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

final class TimerAlarmScheduler {
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final int SHOW_REQUEST_CODE = 1002;

    private TimerAlarmScheduler() {
    }

    @SuppressLint("ScheduleExactAlarm")
    static void schedule(Context context, long triggerAtMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                new Intent(context, AlarmReceiver.class),
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );
        PendingIntent showPendingIntent = PendingIntent.getActivity(
                context,
                SHOW_REQUEST_CODE,
                new Intent(context, AlarmActivity.class),
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                    || alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent),
                        alarmPendingIntent
                );
            } else {
                scheduleInexact(alarmManager, triggerAtMillis, alarmPendingIntent);
            }
        } catch (SecurityException ignored) {
            scheduleInexact(alarmManager, triggerAtMillis, alarmPendingIntent);
        }
    }

    static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                new Intent(context, AlarmReceiver.class),
                pendingIntentFlags(PendingIntent.FLAG_NO_CREATE)
        );
        if (alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
            alarmPendingIntent.cancel();
        }
    }

    private static void scheduleInexact(AlarmManager alarmManager, long triggerAtMillis,
                                        PendingIntent alarmPendingIntent) {
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, alarmPendingIntent);
        } catch (RuntimeException ignored) {
            // The visible timer can continue if a device rejects background alarm scheduling.
        }
    }

    private static int pendingIntentFlags(int baseFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return baseFlags | PendingIntent.FLAG_IMMUTABLE;
        }
        return baseFlags;
    }
}
