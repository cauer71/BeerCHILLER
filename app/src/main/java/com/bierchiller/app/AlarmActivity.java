package com.bierchiller.app;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class AlarmActivity extends Activity {
    private static final String PREFS = "bierchiller";
    private static final String KEY_END_TIME = "endTimeMillis";
    private static final String KEY_TOTAL_DURATION = "totalDurationMillis";
    private static final String KEY_ALARM_DISMISSED = "alarmDismissed";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLockScreenAlarmWindow();
        enableFullscreen();
        setContentView(R.layout.activity_alarm);

        TextView title = findViewById(R.id.alarmTitle);
        Button stopButton = findViewById(R.id.stopAlarmButton);
        title.setText(getString(R.string.alarm_ringing));

        stopButton.setOnClickListener(v -> stopAlarm());
    }

    private void stopAlarm() {
        markAlarmDismissed();
        Intent stopIntent = new Intent(this, AlarmService.class);
        stopIntent.setAction(AlarmService.ACTION_STOP);
        startService(stopIntent);
        finish();
    }

    private void markAlarmDismissed() {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putBoolean(KEY_ALARM_DISMISSED, true)
                .remove(KEY_END_TIME)
                .remove(KEY_TOTAL_DURATION)
                .apply();
        TimerNotificationHelper.cancel(this);
    }

    @Override
    public void onBackPressed() {
        stopAlarm();
    }

    private void enableFullscreen() {
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void configureLockScreenAlarmWindow() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableFullscreen();
        }
    }
}
