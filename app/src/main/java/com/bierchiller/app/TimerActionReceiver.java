package com.bierchiller.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class TimerActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && TimerNotificationHelper.ACTION_STOP_TIMER.equals(intent.getAction())) {
            TimerNotificationHelper.stopTimer(context);
        }
    }
}
