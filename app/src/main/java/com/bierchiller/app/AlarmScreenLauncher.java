package com.bierchiller.app;

import android.content.Context;
import android.content.Intent;

final class AlarmScreenLauncher {
    private AlarmScreenLauncher() {
    }

    static Intent alarmActivityIntent(Context context) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    static void showAlarmScreen(Context context) {
        try {
            context.startActivity(alarmActivityIntent(context));
        } catch (RuntimeException ignored) {
            // Full-screen notification remains the fallback on devices that block direct launch.
        }
    }
}
