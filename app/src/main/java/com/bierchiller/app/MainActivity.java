package com.bierchiller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "bierchiller";
    private static final String KEY_END_TIME = "endTimeMillis";
    private static final String KEY_TOTAL_DURATION = "totalDurationMillis";
    private static final String KEY_VISUAL_MODE = "visualMode";
    private static final String KEY_START_TEMP = "startTemp";
    private static final String KEY_TARGET_TEMP = "targetTemp";
    private static final String KEY_DEVICE_TEMP = "deviceTemp";
    private static final String KEY_DEVICE_MODE = "deviceMode";
    private static final String KEY_VOLUME_INDEX = "volumeIndex";
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final int SHOW_REQUEST_CODE = 1002;
    private static final int VISUAL_CLASSIC = 0;
    private static final int VISUAL_VR2 = 1;
    private static final int DEVICE_FREEZER = 0;
    private static final int DEVICE_FRIDGE = 1;
    private static final int FREEZER_TEMP = -14;
    private static final int FRIDGE_TEMP = 4;
    private static final double COOLING_RATE = 0.028;
    private static final double CONVECTION_EXPONENT = 0.25;
    private static final String[] LANGUAGE_CODES = new String[]{
            LocaleHelper.SYSTEM_LANGUAGE, "de", "en", "it", "fr", "es", "pt", "nl", "pl", "cs", "hr"
    };

    private final double[] volumeFactors = new double[]{0.82, 1.0, 1.7};

    private TimerCircleView timerCircle;
    private ImageView backgroundImage;
    private View backgroundOverlay;
    private View controlPanel;
    private TextView startTempValue;
    private TextView targetTempValue;
    private TextView deviceTempValue;
    private TextView startTempLabel;
    private TextView targetTempLabel;
    private TextView deviceTempLabel;
    private Button volumeSmallButton;
    private Button volumeMediumButton;
    private Button volumeLargeButton;
    private Button freezerButton;
    private Button fridgeButton;
    private View startButton;
    private ImageView startButtonIcon;
    private TextView startButtonText;
    private ImageView headerLogoIcon;
    private TextView headerBeerText;
    private TextView headerChillerText;
    private ImageButton menuButton;
    private ImageView startTempIcon;
    private ImageView targetTempIcon;
    private ImageView deviceTempIcon;
    private Button stopButton;
    private Button startMinusButton;
    private Button startPlusButton;
    private Button targetMinusButton;
    private Button targetPlusButton;
    private Button deviceMinusButton;
    private Button devicePlusButton;

    private CountDownTimer countDownTimer;
    private long endTimeMillis;
    private long totalDurationMillis;
    private int startTemp = 22;
    private int targetTemp = 6;
    private int deviceTemp = FREEZER_TEMP;
    private int volumeIndex = 1;
    private int deviceMode = DEVICE_FREEZER;
    private boolean running;
    private int visualMode;
    private AlarmManager alarmManager;
    private SharedPreferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFullscreen();
        setContentView(R.layout.activity_main);

        timerCircle = findViewById(R.id.timerCircle);
        backgroundImage = findViewById(R.id.backgroundImage);
        backgroundOverlay = findViewById(R.id.backgroundOverlay);
        controlPanel = findViewById(R.id.controlPanel);
        startTempValue = findViewById(R.id.startTempValue);
        targetTempValue = findViewById(R.id.targetTempValue);
        deviceTempValue = findViewById(R.id.deviceTempValue);
        startTempLabel = findViewById(R.id.startTempLabel);
        targetTempLabel = findViewById(R.id.targetTempLabel);
        deviceTempLabel = findViewById(R.id.deviceTempLabel);
        volumeSmallButton = findViewById(R.id.volumeSmallButton);
        volumeMediumButton = findViewById(R.id.volumeMediumButton);
        volumeLargeButton = findViewById(R.id.volumeLargeButton);
        freezerButton = findViewById(R.id.freezerButton);
        fridgeButton = findViewById(R.id.fridgeButton);
        startButton = findViewById(R.id.startButton);
        startButtonIcon = findViewById(R.id.startButtonIcon);
        startButtonText = findViewById(R.id.startButtonText);
        headerLogoIcon = findViewById(R.id.headerLogoIcon);
        headerBeerText = findViewById(R.id.headerBeerText);
        headerChillerText = findViewById(R.id.headerChillerText);
        menuButton = findViewById(R.id.menuButton);
        startTempIcon = findViewById(R.id.startTempIcon);
        targetTempIcon = findViewById(R.id.targetTempIcon);
        deviceTempIcon = findViewById(R.id.deviceTempIcon);
        stopButton = findViewById(R.id.stopButton);
        startMinusButton = findViewById(R.id.startMinusButton);
        startPlusButton = findViewById(R.id.startPlusButton);
        targetMinusButton = findViewById(R.id.targetMinusButton);
        targetPlusButton = findViewById(R.id.targetPlusButton);
        deviceMinusButton = findViewById(R.id.deviceMinusButton);
        devicePlusButton = findViewById(R.id.devicePlusButton);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        restoreInputPreferences();
        updateHeaderBrand();
        visualMode = readVisualModePreference();
        applyVisualMode();

        wireControls();
        wireMenu();
        requestNotificationPermission();
        handleNotificationAction(getIntent());
        updateIdleDisplay();
        restoreRunningAlarm();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationAction(intent);
    }

    private void handleNotificationAction(Intent intent) {
        if (intent != null && TimerNotificationHelper.ACTION_STOP_TIMER.equals(intent.getAction())) {
            stopTimer();
        }
    }

    private void wireControls() {
        volumeSmallButton.setOnClickListener(v -> setVolumeIndex(0));
        volumeMediumButton.setOnClickListener(v -> setVolumeIndex(1));
        volumeLargeButton.setOnClickListener(v -> setVolumeIndex(2));
        freezerButton.setOnClickListener(v -> setDeviceMode(DEVICE_FREEZER));
        fridgeButton.setOnClickListener(v -> setDeviceMode(DEVICE_FRIDGE));

        startMinusButton.setOnClickListener(v -> changeStartTemp(-1));
        startPlusButton.setOnClickListener(v -> changeStartTemp(1));
        targetMinusButton.setOnClickListener(v -> changeTargetTemp(-1));
        targetPlusButton.setOnClickListener(v -> changeTargetTemp(1));
        deviceMinusButton.setOnClickListener(v -> changeDeviceTemp(-1));
        devicePlusButton.setOnClickListener(v -> changeDeviceTemp(1));

        startButton.setOnClickListener(v -> startTimerFromInputs());
        stopButton.setOnClickListener(v -> stopTimer());
    }

    private void wireMenu() {
        menuButton.setOnClickListener(v -> {
        PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenu().add(0, 1, 0, R.string.menu_classic_ui)
                    .setCheckable(true)
                    .setChecked(visualMode == VISUAL_CLASSIC);
            popupMenu.getMenu().add(0, 2, 1, R.string.menu_vr2_mode)
                    .setCheckable(true)
                    .setChecked(visualMode == VISUAL_VR2);
            popupMenu.getMenu().add(0, 3, 2, R.string.menu_language);
            popupMenu.getMenu().add(0, 4, 3, R.string.menu_info);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    setVisualMode(visualMode == VISUAL_CLASSIC ? VISUAL_VR2 : VISUAL_CLASSIC);
                    return true;
                }
                if (item.getItemId() == 2) {
                    setVisualMode(visualMode == VISUAL_VR2 ? VISUAL_CLASSIC : VISUAL_VR2);
                    return true;
                }
                if (item.getItemId() == 3) {
                    showLanguageDialog();
                    return true;
                }
                if (item.getItemId() == 4) {
                    showInfoDialog();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void setVisualMode(int mode) {
        visualMode = mode;
        preferences.edit().putInt(KEY_VISUAL_MODE, mode).apply();
        applyVisualMode();
        updateVolumeButtons();
        updateDeviceModeButtons();
        setControlsForRunningState();
        if (!running) {
            updateIdleDisplay();
        } else if (endTimeMillis > System.currentTimeMillis()) {
            updateRunningTimerCircle(endTimeMillis - System.currentTimeMillis());
        }
    }

    private void applyVisualMode() {
        boolean visualBackground = visualMode != VISUAL_CLASSIC;
        boolean vr2 = visualMode == VISUAL_VR2;
        backgroundImage.setVisibility(visualBackground ? View.VISIBLE : View.GONE);
        applyBackgroundScale();
        backgroundOverlay.setVisibility(visualBackground && !vr2 ? View.VISIBLE : View.GONE);
        if (!vr2) {
            backgroundOverlay.setBackgroundResource(R.drawable.bg_beer_overlay);
        }
        timerCircle.setVisualMode(visualMode);
        headerLogoIcon.setImageResource(vr2 ? R.drawable.ic_hops_vr2 : R.drawable.ic_snowflake);
        headerBeerText.setTextColor(Color.parseColor(vr2 ? "#4A2509" : "#E8B923"));
        headerChillerText.setTextColor(Color.parseColor(vr2 ? "#D99C00" : "#123B4A"));
        menuButton.setColorFilter(Color.parseColor(vr2 ? "#4A2509" : "#123B4A"), PorterDuff.Mode.SRC_IN);
        controlPanel.setBackgroundResource(vr2 ? R.drawable.bg_control_panel_vr2 : R.drawable.bg_control_panel);
        startButton.setBackgroundResource(vr2 ? R.drawable.bg_primary_button_vr2 : R.drawable.bg_primary_button);
        stopButton.setBackgroundResource(vr2 ? R.drawable.bg_secondary_button_vr2 : R.drawable.bg_secondary_button);
        int iconVisibility = vr2 ? View.VISIBLE : View.GONE;
        startTempIcon.setVisibility(iconVisibility);
        targetTempIcon.setVisibility(iconVisibility);
        deviceTempIcon.setVisibility(iconVisibility);
        tintStartButton(visualBackground ? Color.WHITE : Color.parseColor("#102A33"));
        styleTemperatureControls(vr2);
    }

    private int readVisualModePreference() {
        Object stored = preferences.getAll().get(KEY_VISUAL_MODE);
        if (stored instanceof Integer) {
            int mode = (Integer) stored;
            return mode == VISUAL_CLASSIC ? VISUAL_CLASSIC : VISUAL_VR2;
        }
        if (stored instanceof Boolean) {
            return (Boolean) stored ? VISUAL_VR2 : VISUAL_CLASSIC;
        }
        return VISUAL_VR2;
    }

    private void styleTemperatureControls(boolean vr2) {
        int buttonBackground = vr2 ? R.drawable.bg_step_button_vr2 : R.drawable.bg_step_button;
        int valueBackground = vr2 ? R.drawable.bg_value_chip_vr2 : R.drawable.bg_value_chip;
        Button[] stepButtons = new Button[]{
                startMinusButton, startPlusButton, targetMinusButton, targetPlusButton,
                deviceMinusButton, devicePlusButton
        };
        for (Button button : stepButtons) {
            button.setBackgroundResource(buttonBackground);
            button.setTextColor(Color.parseColor(vr2 ? "#4A2509" : "#123B4A"));
        }
        TextView[] valueChips = new TextView[]{startTempValue, targetTempValue, deviceTempValue};
        for (TextView valueChip : valueChips) {
            valueChip.setBackgroundResource(valueBackground);
            valueChip.setTextColor(Color.parseColor(vr2 ? "#4A2509" : "#123B4A"));
        }
        TextView[] labels = new TextView[]{startTempLabel, targetTempLabel, deviceTempLabel};
        for (TextView label : labels) {
            label.setTextColor(Color.parseColor(vr2 ? "#4A2509" : "#123B4A"));
            label.setSingleLine(false);
            label.setMaxLines(2);
            label.setGravity(android.view.Gravity.CENTER_VERTICAL);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, vr2 ? 9.5f : 12f);
        }
    }

    private void applyBackgroundScale() {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return;
        }
        backgroundImage.post(() -> {
            Drawable drawable = backgroundImage.getDrawable();
            int viewWidth = backgroundImage.getWidth();
            int viewHeight = backgroundImage.getHeight();
            if (drawable == null || viewWidth <= 0 || viewHeight <= 0) {
                return;
            }
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            if (drawableWidth <= 0 || drawableHeight <= 0) {
                return;
            }
            float scale = Math.max((float) viewWidth / drawableWidth, (float) viewHeight / drawableHeight);
            float dx = (viewWidth - drawableWidth * scale) * 0.5f;
            float dy = -viewHeight * 0.29f;
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            backgroundImage.setScaleType(ImageView.ScaleType.MATRIX);
            backgroundImage.setImageMatrix(matrix);
        });
    }

    private void showInfoDialog() {
        String version = getString(R.string.app_version, BuildConfig.VERSION_NAME);
        new AlertDialog.Builder(this)
                .setTitle(R.string.info_title)
                .setMessage(getString(R.string.app_info_message, getString(R.string.app_name), version))
                .setPositiveButton(R.string.close, null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languageNames = new String[]{
                getString(R.string.language_system),
                "Deutsch",
                "English",
                "Italiano",
                "Fran\u00e7ais",
                "Espa\u00f1ol",
                "Portugu\u00eas",
                "Nederlands",
                "Polski",
                "\u010ce\u0161tina",
                "Hrvatski"
        };
        int currentSelection = LocaleHelper.currentSelectionIndex(this, LANGUAGE_CODES);
        new AlertDialog.Builder(this)
                .setTitle(R.string.language_title)
                .setSingleChoiceItems(languageNames, currentSelection, (dialog, which) -> {
                    LocaleHelper.setStoredLanguage(this, LANGUAGE_CODES[which]);
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton(R.string.close, null)
                .show();
    }

    private void updateHeaderBrand() {
        String appName = getString(R.string.app_name);
        if (appName.endsWith("CHILLER")) {
            headerBeerText.setText(appName.substring(0, appName.length() - "CHILLER".length()));
            headerChillerText.setText("CHILLER");
        } else {
            headerBeerText.setText(appName);
            headerChillerText.setText("");
        }
    }

    private void setVolumeIndex(int index) {
        if (running) {
            return;
        }
        volumeIndex = Math.max(0, Math.min(index, volumeFactors.length - 1));
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void changeStartTemp(int delta) {
        if (running) {
            return;
        }
        startTemp = clamp(startTemp + delta, -5, 40);
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void changeTargetTemp(int delta) {
        if (running) {
            return;
        }
        targetTemp = clamp(targetTemp + delta, -5, 20);
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void changeDeviceTemp(int delta) {
        if (running) {
            return;
        }
        deviceTemp = clamp(deviceTemp + delta, -30, 5);
        if (deviceTemp == FRIDGE_TEMP) {
            deviceMode = DEVICE_FRIDGE;
        } else if (deviceTemp == FREEZER_TEMP) {
            deviceMode = DEVICE_FREEZER;
        }
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void setDeviceMode(int mode) {
        if (running) {
            return;
        }
        deviceMode = mode == DEVICE_FRIDGE ? DEVICE_FRIDGE : DEVICE_FREEZER;
        deviceTemp = deviceMode == DEVICE_FRIDGE ? FRIDGE_TEMP : FREEZER_TEMP;
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void restoreInputPreferences() {
        startTemp = preferences.getInt(KEY_START_TEMP, startTemp);
        targetTemp = preferences.getInt(KEY_TARGET_TEMP, targetTemp);
        deviceTemp = preferences.getInt(KEY_DEVICE_TEMP, deviceTemp);
        deviceMode = preferences.getInt(KEY_DEVICE_MODE, deviceTemp == FRIDGE_TEMP ? DEVICE_FRIDGE : DEVICE_FREEZER);
        volumeIndex = preferences.getInt(KEY_VOLUME_INDEX, volumeIndex);

        startTemp = clamp(startTemp, -5, 40);
        targetTemp = clamp(targetTemp, -5, 20);
        deviceTemp = clamp(deviceTemp, -30, 5);
        deviceMode = deviceMode == DEVICE_FRIDGE ? DEVICE_FRIDGE : DEVICE_FREEZER;
        volumeIndex = Math.max(0, Math.min(volumeIndex, volumeFactors.length - 1));
    }

    private void saveInputPreferences() {
        preferences.edit()
                .putInt(KEY_START_TEMP, startTemp)
                .putInt(KEY_TARGET_TEMP, targetTemp)
                .putInt(KEY_DEVICE_TEMP, deviceTemp)
                .putInt(KEY_DEVICE_MODE, deviceMode)
                .putInt(KEY_VOLUME_INDEX, volumeIndex)
                .apply();
    }

    private void startTimerFromInputs() {
        int minutes = calculateCoolingMinutes();
        if (minutes < 1) {
            showInvalidInputs();
            return;
        }

        stopTimer(false);
        totalDurationMillis = minutes * 60_000L;
        endTimeMillis = System.currentTimeMillis() + totalDurationMillis;
        preferences.edit()
                .putLong(KEY_END_TIME, endTimeMillis)
                .putLong(KEY_TOTAL_DURATION, totalDurationMillis)
                .apply();

        running = true;
        setControlsForRunningState();
        setStatus(getString(R.string.running));
        startUiCountdown(endTimeMillis);
        scheduleExactAlarm(endTimeMillis);
        TimerNotificationHelper.show(this, endTimeMillis, totalDurationMillis);
    }

    private void scheduleExactAlarm(long triggerAtMillis) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                ALARM_REQUEST_CODE,
                intent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        Intent showIntent = new Intent(this, AlarmActivity.class);
        PendingIntent showPendingIntent = PendingIntent.getActivity(
                this,
                SHOW_REQUEST_CODE,
                showIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        if (alarmManager != null) {
            AlarmManager.AlarmClockInfo alarmClockInfo =
                    new AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }
    }

    private void stopTimer() {
        stopTimer(true);
    }

    private void stopTimer(boolean resetUi) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (alarmManager != null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    ALARM_REQUEST_CODE,
                    new Intent(this, AlarmReceiver.class),
                    pendingIntentFlags(PendingIntent.FLAG_NO_CREATE)
            );
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }

        stopService(new Intent(this, AlarmService.class));
        TimerNotificationHelper.cancel(this);
        endTimeMillis = 0;
        totalDurationMillis = 0;
        running = false;
        preferences.edit().remove(KEY_END_TIME).remove(KEY_TOTAL_DURATION).apply();

        if (resetUi) {
            updateIdleDisplay();
        }
    }

    private void restoreRunningAlarm() {
        long storedEndTime = preferences.getLong(KEY_END_TIME, 0);
        if (storedEndTime > System.currentTimeMillis()) {
            endTimeMillis = storedEndTime;
            totalDurationMillis = preferences.getLong(
                    KEY_TOTAL_DURATION,
                    Math.max(1, storedEndTime - System.currentTimeMillis())
            );
            running = true;
            setControlsForRunningState();
            setStatus(getString(R.string.running));
            startUiCountdown(endTimeMillis);
            TimerNotificationHelper.show(this, endTimeMillis, totalDurationMillis);
        } else {
            preferences.edit().remove(KEY_END_TIME).remove(KEY_TOTAL_DURATION).apply();
            TimerNotificationHelper.cancel(this);
        }
    }

    private void startUiCountdown(long targetTimeMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long remaining = Math.max(0, targetTimeMillis - System.currentTimeMillis());
        updateRunningTimerCircle(remaining);

        if (remaining <= 0) {
            finishTimerUi();
            return;
        }

        countDownTimer = new CountDownTimer(remaining, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateRunningTimerCircle(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                finishTimerUi();
            }
        }.start();
    }

    private void finishTimerUi() {
        timerCircle.setTimerState(
                "00:00",
                getString(R.string.remaining_time),
                formatEndTime(endTimeMillis),
                getString(R.string.degrees_celsius_decimal, (float) targetTemp),
                getString(R.string.current_temperature),
                visualMode == VISUAL_VR2 ? 1f : 0f,
                true,
                true
        );
        setStatus(getString(R.string.alarm_ringing));
        preferences.edit().remove(KEY_END_TIME).remove(KEY_TOTAL_DURATION).apply();
        TimerNotificationHelper.cancel(this);
    }

    private void updateRunningTimerCircle(long remainingMillis) {
        float progress = totalDurationMillis > 0
                ? (float) remainingMillis / (float) totalDurationMillis
                : 0f;
        float visibleProgress = 1f - Math.max(0f, Math.min(1f, progress));
        float elapsedProgress = totalDurationMillis > 0
                ? visibleProgress
                : 0f;
        double currentBeerTemp = calculateCurrentBeerTemperature(elapsedProgress);
        timerCircle.setTimerState(
                formatDuration(remainingMillis),
                getString(R.string.remaining_time),
                formatEndTime(endTimeMillis),
                getString(R.string.degrees_celsius_decimal, (float) currentBeerTemp),
                getString(R.string.current_temperature),
                visibleProgress,
                true,
                true
        );
    }

    private void updateIdleDisplay() {
        updateTemperatureValues();
        updateVolumeButtons();
        updateDeviceModeButtons();
        setControlsForRunningState();

        int minutes = calculateCoolingMinutes();
        if (minutes < 1) {
            showInvalidInputs();
            return;
        }

        setStatus(getString(R.string.ready));
        long estimatedEndTime = System.currentTimeMillis() + minutes * 60_000L;
        timerCircle.setTimerState(
                getString(R.string.minutes_short, minutes),
                getString(R.string.cooling_time),
                formatEndTime(estimatedEndTime),
                "",
                "",
                visualMode == VISUAL_VR2 ? 0f : 1f,
                false,
                true
        );
    }

    private void showInvalidInputs() {
        setStatus(getString(R.string.check_inputs_short));
        timerCircle.setTimerState("--", getString(R.string.check_inputs), 1f, false, false);
    }

    private void setStatus(String text) {
    }

    private void updateTemperatureValues() {
        startTempValue.setText(getString(R.string.degrees_celsius, startTemp));
        targetTempValue.setText(getString(R.string.degrees_celsius, targetTemp));
        deviceTempValue.setText(getString(R.string.degrees_celsius, deviceTemp));
    }

    private void updateVolumeButtons() {
        styleVolumeButton(volumeSmallButton, volumeIndex == 0);
        styleVolumeButton(volumeMediumButton, volumeIndex == 1);
        styleVolumeButton(volumeLargeButton, volumeIndex == 2);
    }

    private void updateDeviceModeButtons() {
        boolean freezerSelected = deviceTemp == FREEZER_TEMP && deviceMode == DEVICE_FREEZER;
        boolean fridgeSelected = deviceTemp == FRIDGE_TEMP && deviceMode == DEVICE_FRIDGE;
        styleVolumeButton(freezerButton, freezerSelected);
        styleVolumeButton(fridgeButton, fridgeSelected);
    }

    private void styleVolumeButton(Button button, boolean selected) {
        button.setBackgroundResource(selected
                ? (visualMode == VISUAL_VR2 ? R.drawable.bg_segment_selected_vr2 : R.drawable.bg_segment_selected)
                : (visualMode == VISUAL_VR2 ? R.drawable.bg_segment_unselected_vr2 : R.drawable.bg_segment_unselected));
        button.setTextColor(selected
                ? Color.WHITE
                : Color.parseColor(visualMode == VISUAL_VR2 ? "#4A2509" : "#123B4A"));
    }

    private void setControlsForRunningState() {
        boolean editable = !running;
        Button[] editButtons = new Button[]{
                volumeSmallButton, volumeMediumButton, volumeLargeButton,
                freezerButton, fridgeButton,
                startMinusButton, startPlusButton, targetMinusButton, targetPlusButton,
                deviceMinusButton, devicePlusButton
        };

        for (Button button : editButtons) {
            button.setEnabled(editable);
            button.setAlpha(editable ? 1f : 0.55f);
        }

        startButton.setEnabled(editable);
        startButton.setAlpha(editable ? 1f : 0.55f);
        stopButton.setEnabled(running);
        stopButton.setAlpha(1f);
        stopButton.setTextColor(running
                ? Color.parseColor(visualMode == VISUAL_VR2 ? "#4A2509" : "#123B4A")
                : Color.parseColor("#7D9092"));
    }

    private void tintStartButton(int color) {
        startButtonText.setTextColor(color);
        startButtonIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private int calculateCoolingMinutes() {
        if (targetTemp >= startTemp || deviceTemp >= targetTemp) {
            return -1;
        }

        double thetaTarget = (targetTemp - deviceTemp) / (double) (startTemp - deviceTemp);
        double dimensionlessTime = 4.0 * (Math.pow(thetaTarget, -CONVECTION_EXPONENT) - 1.0);
        double baseMinutes = dimensionlessTime / COOLING_RATE;
        return Math.max(1, (int) Math.ceil(baseMinutes * volumeFactors[volumeIndex]));
    }

    private double calculateCurrentBeerTemperature(float elapsedProgress) {
        double totalMinutes = totalDurationMillis / 60000.0;
        double elapsedMinutes = Math.max(0.0, totalMinutes * Math.max(0f, Math.min(1f, elapsedProgress)));
        double dimensionlessTime = COOLING_RATE * elapsedMinutes / volumeFactors[volumeIndex];
        double theta = Math.pow(4.0 / (dimensionlessTime + 4.0), 4.0);
        double temperature = deviceTemp + (startTemp - deviceTemp) * theta;
        return Math.max(targetTemp, Math.min(startTemp, temperature));
    }

    private String formatDuration(long millis) {
        long totalSeconds = (long) Math.ceil(millis / 1000.0);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (minutes < 100) {
            return String.format(Locale.GERMANY, "%02d:%02d", minutes, seconds);
        }
        return String.format(Locale.GERMANY, "%d:%02d", minutes, seconds);
    }

    private String formatEndTime(long targetTimeMillis) {
        return getString(R.string.ends_at, formatTime(this, targetTimeMillis));
    }

    static String formatTime(Context context, long targetTimeMillis) {
        return new SimpleDateFormat("HH:mm", Locale.GERMANY)
                .format(new Date(targetTimeMillis));
    }

    private int pendingIntentFlags(int baseFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return baseFlags | PendingIntent.FLAG_IMMUTABLE;
        }
        return baseFlags;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 2001);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        enableFullscreen();
        if (running && endTimeMillis > System.currentTimeMillis()) {
            startUiCountdown(endTimeMillis);
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
