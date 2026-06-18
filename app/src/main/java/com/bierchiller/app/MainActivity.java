package com.bierchiller.app;

import android.animation.ValueAnimator;
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
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

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
    private static final String KEY_CONTAINER_TYPE = "containerType";
    private static final String KEY_ORIENTATION = "orientation";
    static final String KEY_ALARM_DISMISSED = "alarmDismissed";
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final int SHOW_REQUEST_CODE = 1002;
    private static final int VISUAL_CLASSIC = 0;
    private static final int VISUAL_VR3 = 1;
    private static final int DEVICE_FREEZER = 0;
    private static final int DEVICE_FRIDGE = 1;
    private static final int CONTAINER_BOTTLE = 0;
    private static final int CONTAINER_CAN = 1;
    private static final int ORIENTATION_LYING = 0;
    private static final int ORIENTATION_STANDING = 1;
    private static final int FREEZER_TEMP = -14;
    private static final int FRIDGE_TEMP = 4;
    private static final int VOLUME_SMALL = 0;
    private static final int VOLUME_MEDIUM = 1;
    private static final int VOLUME_LARGE = 2;
    private static final double BEER_HEAT_CAPACITY = 4200.0;
    private static final double AIR_THERMAL_CONDUCTIVITY = 0.026;
    private static final double AIR_KINEMATIC_VISCOSITY = 15.1e-6;
    private static final double AIR_THERMAL_DIFFUSIVITY = 21.8e-6;
    private static final double GRAVITY = 9.81;
    private static final double CONVECTION_EXPONENT = 0.25;
    private static final double REFERENCE_START_TEMP = 39.5;
    private static final double REFERENCE_TARGET_TEMP = 8.0;
    private static final double REFERENCE_DEVICE_TEMP = -17.5;
    private static final double REFERENCE_MEASURED_SECONDS = 54.4 * 60.0;
    private static final String[] LANGUAGE_CODES = new String[]{
            LocaleHelper.SYSTEM_LANGUAGE, "de", "en", "it", "fr", "es", "pt", "nl", "pl", "cs", "hr"
    };

    private TimerCircleView timerCircle;
    private ImageView backgroundImage;
    private View backgroundOverlay;
    private View controlPanel;
    private View selectionTopRow;
    private View volumeRow;
    private View bottomButtonRow;
    private TextView startTempValue;
    private TextView targetTempValue;
    private TextView deviceTempValue;
    private TextView startTempLabel;
    private TextView targetTempLabel;
    private TextView deviceTempLabel;
    private Button bottleButton;
    private Button canButton;
    private Button volumeSmallButton;
    private Button volumeMediumButton;
    private Button volumeLargeButton;
    private Button lyingButton;
    private Button standingButton;
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
    private int volumeIndex = VOLUME_MEDIUM;
    private int deviceMode = DEVICE_FREEZER;
    private int containerType = CONTAINER_BOTTLE;
    private int orientation = ORIENTATION_LYING;
    private boolean running;
    private boolean vr3ControlsCollapsed;
    private ValueAnimator vr3Animator;
    private int vr3SelectionHeight;
    private int vr3VolumeHeight;
    private int vr3ControlHeight;
    private int vr3BottomRowHeight;
    private float vr3StartWeight;
    private float vr3StopWeight;
    private int vr3StopLeftMargin;
    private Button selectedContainerButton;
    private Button selectedOrientationButton;
    private Button selectedVolumeButton;
    private Button selectedDeviceButton;
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
        selectionTopRow = findViewById(R.id.selectionTopRow);
        volumeRow = findViewById(R.id.volumeRow);
        bottomButtonRow = findViewById(R.id.bottomButtonRow);
        startTempValue = findViewById(R.id.startTempValue);
        targetTempValue = findViewById(R.id.targetTempValue);
        deviceTempValue = findViewById(R.id.deviceTempValue);
        startTempLabel = findViewById(R.id.startTempLabel);
        targetTempLabel = findViewById(R.id.targetTempLabel);
        deviceTempLabel = findViewById(R.id.deviceTempLabel);
        bottleButton = findViewById(R.id.bottleButton);
        canButton = findViewById(R.id.canButton);
        volumeSmallButton = findViewById(R.id.volumeSmallButton);
        volumeMediumButton = findViewById(R.id.volumeMediumButton);
        volumeLargeButton = findViewById(R.id.volumeLargeButton);
        lyingButton = findViewById(R.id.lyingButton);
        standingButton = findViewById(R.id.standingButton);
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
        bottleButton.setOnClickListener(v -> setContainerType(CONTAINER_BOTTLE));
        canButton.setOnClickListener(v -> setContainerType(CONTAINER_CAN));
        volumeSmallButton.setOnClickListener(v -> setVolumeIndex(0));
        volumeMediumButton.setOnClickListener(v -> setVolumeIndex(1));
        volumeLargeButton.setOnClickListener(v -> setVolumeIndex(2));
        lyingButton.setOnClickListener(v -> setOrientation(ORIENTATION_LYING));
        standingButton.setOnClickListener(v -> setOrientation(ORIENTATION_STANDING));
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
            popupMenu.getMenu().add(0, 2, 1, R.string.menu_vr3_mode)
                    .setCheckable(true)
                    .setChecked(visualMode == VISUAL_VR3);
            popupMenu.getMenu().add(0, 3, 2, R.string.menu_calculation_model);
            popupMenu.getMenu().add(0, 4, 3, R.string.menu_language);
            popupMenu.getMenu().add(0, 5, 4, R.string.menu_info);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    setVisualMode(VISUAL_CLASSIC);
                    return true;
                }
                if (item.getItemId() == 2) {
                    setVisualMode(VISUAL_VR3);
                    return true;
                }
                if (item.getItemId() == 3) {
                    startActivity(new Intent(this, HelpActivity.class));
                    return true;
                }
                if (item.getItemId() == 4) {
                    showLanguageDialog();
                    return true;
                }
                if (item.getItemId() == 5) {
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
        updateSelectionButtons();
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
        boolean vrStyle = isVrStyle();
        backgroundImage.setVisibility(visualBackground ? View.VISIBLE : View.GONE);
        applyBackgroundScale();
        backgroundOverlay.setVisibility(visualBackground && !vrStyle ? View.VISIBLE : View.GONE);
        if (!vrStyle) {
            backgroundOverlay.setBackgroundResource(R.drawable.bg_beer_overlay);
        }
        timerCircle.setVisualMode(visualMode);
        headerLogoIcon.setImageResource(vrStyle ? R.drawable.ic_hops_vr2 : R.drawable.ic_snowflake);
        headerBeerText.setTextColor(Color.parseColor(vrStyle ? "#4A2509" : "#E8B923"));
        headerChillerText.setTextColor(Color.parseColor(vrStyle ? "#D99C00" : "#123B4A"));
        menuButton.setColorFilter(Color.parseColor(vrStyle ? "#4A2509" : "#123B4A"), PorterDuff.Mode.SRC_IN);
        controlPanel.setBackgroundResource(vrStyle ? R.drawable.bg_control_panel_vr2 : R.drawable.bg_control_panel);
        startButton.setBackgroundResource(vrStyle ? R.drawable.bg_primary_button_vr2 : R.drawable.bg_primary_button);
        stopButton.setBackgroundResource(vrStyle ? R.drawable.bg_secondary_button_vr2 : R.drawable.bg_secondary_button);
        int iconVisibility = vrStyle ? View.VISIBLE : View.GONE;
        startTempIcon.setVisibility(iconVisibility);
        targetTempIcon.setVisibility(iconVisibility);
        deviceTempIcon.setVisibility(iconVisibility);
        tintStartButton(visualBackground ? Color.WHITE : Color.parseColor("#102A33"));
        styleTemperatureControls(vrStyle);
        applyVr3RunningLayout(false);
    }

    private int readVisualModePreference() {
        Object stored = preferences.getAll().get(KEY_VISUAL_MODE);
        if (stored instanceof Integer) {
            int mode = (Integer) stored;
            return mode == VISUAL_CLASSIC ? VISUAL_CLASSIC : VISUAL_VR3;
        }
        if (stored instanceof Boolean) {
            return (Boolean) stored ? VISUAL_VR3 : VISUAL_CLASSIC;
        }
        return VISUAL_VR3;
    }

    private boolean isVrStyle() {
        return visualMode == VISUAL_VR3;
    }

    private void styleTemperatureControls(boolean vrStyle) {
        int buttonBackground = vrStyle ? R.drawable.bg_step_button_vr2 : R.drawable.bg_step_button;
        int valueBackground = vrStyle ? R.drawable.bg_value_chip_vr2 : R.drawable.bg_value_chip;
        Button[] stepButtons = new Button[]{
                startMinusButton, startPlusButton, targetMinusButton, targetPlusButton,
                deviceMinusButton, devicePlusButton
        };
        for (Button button : stepButtons) {
            button.setBackgroundResource(buttonBackground);
            button.setTextColor(Color.parseColor(vrStyle ? "#4A2509" : "#123B4A"));
        }
        TextView[] valueChips = new TextView[]{startTempValue, targetTempValue, deviceTempValue};
        for (TextView valueChip : valueChips) {
            valueChip.setBackgroundResource(valueBackground);
            valueChip.setTextColor(Color.parseColor(vrStyle ? "#4A2509" : "#123B4A"));
        }
        TextView[] labels = new TextView[]{startTempLabel, targetTempLabel, deviceTempLabel};
        for (TextView label : labels) {
            label.setTextColor(Color.parseColor(vrStyle ? "#4A2509" : "#123B4A"));
            label.setSingleLine(false);
            label.setMaxLines(2);
            label.setGravity(android.view.Gravity.CENTER_VERTICAL);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, vrStyle ? 9.5f : 12f);
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
        int requestedVolume = Math.max(VOLUME_SMALL, Math.min(index, VOLUME_LARGE));
        if (containerType == CONTAINER_CAN && requestedVolume == VOLUME_LARGE) {
            requestedVolume = VOLUME_MEDIUM;
        }
        volumeIndex = requestedVolume;
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void setContainerType(int type) {
        if (running) {
            return;
        }
        containerType = type == CONTAINER_CAN ? CONTAINER_CAN : CONTAINER_BOTTLE;
        if (containerType == CONTAINER_CAN && volumeIndex == VOLUME_LARGE) {
            volumeIndex = VOLUME_MEDIUM;
        }
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void setOrientation(int requestedOrientation) {
        if (running) {
            return;
        }
        orientation = requestedOrientation == ORIENTATION_STANDING ? ORIENTATION_STANDING : ORIENTATION_LYING;
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
        containerType = preferences.getInt(KEY_CONTAINER_TYPE, containerType);
        orientation = preferences.getInt(KEY_ORIENTATION, orientation);

        startTemp = clamp(startTemp, -5, 40);
        targetTemp = clamp(targetTemp, -5, 20);
        deviceTemp = clamp(deviceTemp, -30, 5);
        deviceMode = deviceMode == DEVICE_FRIDGE ? DEVICE_FRIDGE : DEVICE_FREEZER;
        containerType = containerType == CONTAINER_CAN ? CONTAINER_CAN : CONTAINER_BOTTLE;
        orientation = orientation == ORIENTATION_STANDING ? ORIENTATION_STANDING : ORIENTATION_LYING;
        volumeIndex = Math.max(VOLUME_SMALL, Math.min(volumeIndex, VOLUME_LARGE));
        if (containerType == CONTAINER_CAN && volumeIndex == VOLUME_LARGE) {
            volumeIndex = VOLUME_MEDIUM;
        }
    }

    private void saveInputPreferences() {
        preferences.edit()
                .putInt(KEY_START_TEMP, startTemp)
                .putInt(KEY_TARGET_TEMP, targetTemp)
                .putInt(KEY_DEVICE_TEMP, deviceTemp)
                .putInt(KEY_DEVICE_MODE, deviceMode)
                .putInt(KEY_VOLUME_INDEX, volumeIndex)
                .putInt(KEY_CONTAINER_TYPE, containerType)
                .putInt(KEY_ORIENTATION, orientation)
                .apply();
    }

    private void startTimerFromInputs() {
        int minutes = calculateCoolingMinutes();
        if (minutes < 0) {
            showInvalidInputs();
            return;
        }

        stopTimer(false);
        preferences.edit().remove(KEY_ALARM_DISMISSED).apply();
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
                getString(R.string.current_temperature_short),
                isVrStyle() ? 1f : 0f,
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
                getString(R.string.current_temperature_short),
                visibleProgress,
                true,
                true
        );
    }

    private void updateIdleDisplay() {
        updateTemperatureValues();
        updateSelectionButtons();
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
                isVrStyle() ? 0f : 1f,
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

    private void updateSelectionButtons() {
        Button newContainerButton = containerType == CONTAINER_BOTTLE ? bottleButton : canButton;
        styleSegmentGroup(new Button[]{bottleButton, canButton}, newContainerButton, selectedContainerButton);
        selectedContainerButton = newContainerButton;

        Button newVolumeButton = volumeIndex == VOLUME_SMALL
                ? volumeSmallButton
                : (volumeIndex == VOLUME_MEDIUM ? volumeMediumButton : volumeLargeButton);
        styleSegmentGroup(
                new Button[]{volumeSmallButton, volumeMediumButton, volumeLargeButton},
                newVolumeButton,
                selectedVolumeButton
        );
        selectedVolumeButton = newVolumeButton;

        Button newOrientationButton = orientation == ORIENTATION_LYING ? lyingButton : standingButton;
        styleSegmentGroup(new Button[]{lyingButton, standingButton}, newOrientationButton, selectedOrientationButton);
        selectedOrientationButton = newOrientationButton;
    }

    private void updateDeviceModeButtons() {
        boolean freezerSelected = deviceTemp == FREEZER_TEMP && deviceMode == DEVICE_FREEZER;
        boolean fridgeSelected = deviceTemp == FRIDGE_TEMP && deviceMode == DEVICE_FRIDGE;
        Button newDeviceButton = freezerSelected || !fridgeSelected ? freezerButton : fridgeButton;
        styleSegmentGroup(new Button[]{freezerButton, fridgeButton}, newDeviceButton, selectedDeviceButton);
        selectedDeviceButton = newDeviceButton;
    }

    private void styleVolumeButton(Button button, boolean selected) {
        button.setBackgroundResource(selected
                ? (isVrStyle() ? R.drawable.bg_segment_selected_vr2 : R.drawable.bg_segment_selected)
                : (isVrStyle() ? R.drawable.bg_segment_unselected_vr2 : R.drawable.bg_segment_unselected));
        button.setTextColor(selected
                ? Color.WHITE
                : Color.parseColor(isVrStyle() ? "#4A2509" : "#123B4A"));
    }

    private void styleSegmentGroup(Button[] buttons, Button selectedButton, Button previousSelectedButton) {
        for (Button button : buttons) {
            styleVolumeButton(button, button == selectedButton);
        }
        if (shouldAnimateSegmentPill() && previousSelectedButton != null && previousSelectedButton != selectedButton) {
            animateSelectedSegmentPill(previousSelectedButton, selectedButton);
        } else {
            selectedButton.setTranslationX(0f);
            selectedButton.setScaleX(1f);
            selectedButton.setScaleY(1f);
        }
    }

    private boolean shouldAnimateSegmentPill() {
        return true;
    }

    private void animateSelectedSegmentPill(Button previousButton, Button selectedButton) {
        int previousCenter = previousButton.getLeft() + previousButton.getWidth() / 2;
        int selectedCenter = selectedButton.getLeft() + selectedButton.getWidth() / 2;
        if (previousCenter == selectedCenter || previousButton.getWidth() == 0 || selectedButton.getWidth() == 0) {
            selectedButton.post(() -> animateSelectedSegmentPill(previousButton, selectedButton));
            return;
        }

        selectedButton.animate().cancel();
        selectedButton.setTranslationX(previousCenter - selectedCenter);
        selectedButton.setScaleX(0.98f);
        selectedButton.setScaleY(0.99f);

        SpringAnimation move = new SpringAnimation(selectedButton, DynamicAnimation.TRANSLATION_X, 0f);
        SpringForce moveSpring = new SpringForce(0f);
        moveSpring.setDampingRatio(0.92f);
        moveSpring.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        move.setSpring(moveSpring);
        move.start();

        SpringAnimation scaleX = new SpringAnimation(selectedButton, DynamicAnimation.SCALE_X, 1f);
        SpringAnimation scaleY = new SpringAnimation(selectedButton, DynamicAnimation.SCALE_Y, 1f);
        SpringForce scaleSpring = new SpringForce(1f);
        scaleSpring.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        scaleSpring.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        scaleX.setSpring(scaleSpring);
        scaleY.setSpring(scaleSpring);
        scaleX.start();
        scaleY.start();
    }

    private void setControlsForRunningState() {
        boolean editable = !running;
        Button[] editButtons = new Button[]{
                bottleButton, canButton,
                volumeSmallButton, volumeMediumButton, volumeLargeButton,
                lyingButton, standingButton,
                freezerButton, fridgeButton,
                startMinusButton, startPlusButton, targetMinusButton, targetPlusButton,
                deviceMinusButton, devicePlusButton
        };

        for (Button button : editButtons) {
            boolean available = button != volumeLargeButton || containerType != CONTAINER_CAN;
            button.setEnabled(editable && available);
            button.setAlpha(editable && available ? 1f : 0.55f);
        }

        startButton.setEnabled(editable);
        startButton.setAlpha(editable ? 1f : 0.55f);
        stopButton.setEnabled(running);
        stopButton.setAlpha(1f);
        stopButton.setTextColor(running
                ? Color.parseColor(isVrStyle() ? "#4A2509" : "#123B4A")
                : Color.parseColor("#7D9092"));
        applyVr3RunningLayout(true);
    }

    // Portrait running animation shared by Classic and VR3.
    private void applyVr3RunningLayout(boolean animate) {
        boolean portrait = getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
        if (!portrait) {
            if (vr3ControlsCollapsed) {
                captureVr3OriginalSizes();
                applyVr3Progress(0f);
                vr3ControlsCollapsed = false;
            }
            return;
        }
        boolean collapse = running;
        if (!collapse && !vr3ControlsCollapsed) {
            return;
        }
        if (vr3ControlsCollapsed == collapse
                && timerCircle.getScaleX() == (collapse ? 1.18f : 1f)
                && viewHeight(controlPanel) == (collapse ? 0 : originalVr3Height(controlPanel))) {
            return;
        }

        captureVr3OriginalSizes();
        if (vr3Animator != null) {
            vr3Animator.cancel();
        }

        float startProgress = currentVr3CollapseProgress();
        float endProgress = collapse ? 1f : 0f;
        vr3ControlsCollapsed = collapse;
        if (!animate) {
            applyVr3Progress(endProgress);
            applyVr3FinalLayout(collapse);
            return;
        }

        vr3Animator = ValueAnimator.ofFloat(startProgress, endProgress);
        vr3Animator.setDuration(600L);
        vr3Animator.setInterpolator(new PathInterpolator(0.2f, 0f, 0f, 1f));
        vr3Animator.addUpdateListener(animation -> applyVr3Progress((float) animation.getAnimatedValue()));
        vr3Animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                applyVr3Progress(endProgress);
                applyVr3FinalLayout(collapse);
            }
        });
        vr3Animator.start();
    }

    private void captureVr3OriginalSizes() {
        if (vr3SelectionHeight <= 0) {
            vr3SelectionHeight = Math.max(selectionTopRow.getHeight(), dpInt(42));
        }
        if (vr3VolumeHeight <= 0) {
            vr3VolumeHeight = Math.max(volumeRow.getHeight(), dpInt(44));
        }
        if (vr3ControlHeight <= 0) {
            vr3ControlHeight = Math.max(controlPanel.getHeight(), dpInt(226));
        }
        if (vr3BottomRowHeight <= 0) {
            vr3BottomRowHeight = Math.max(bottomButtonRow.getHeight(), dpInt(62));
        }
        if (vr3StartWeight <= 0f) {
            vr3StartWeight = ((LinearLayout.LayoutParams) startButton.getLayoutParams()).weight;
        }
        if (vr3StopWeight <= 0f) {
            LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();
            vr3StopWeight = stopParams.weight;
            vr3StopLeftMargin = stopParams.leftMargin;
        }
    }

    private float currentVr3CollapseProgress() {
        if (timerCircle.getScaleX() > 1f) {
            return Math.max(0f, Math.min(1f, (timerCircle.getScaleX() - 1f) / 0.18f));
        }
        return vr3ControlsCollapsed ? 1f : 0f;
    }

    private int originalVr3Height(View view) {
        if (view == selectionTopRow) {
            return vr3SelectionHeight;
        }
        if (view == volumeRow) {
            return vr3VolumeHeight;
        }
        if (view == controlPanel) {
            return vr3ControlHeight;
        }
        if (view == bottomButtonRow) {
            return vr3BottomRowHeight;
        }
        return view.getHeight();
    }

    private int viewHeight(View view) {
        return view.getLayoutParams() != null ? view.getLayoutParams().height : view.getHeight();
    }

    private void applyVr3Progress(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));

        setViewHeight(selectionTopRow, Math.round(vr3SelectionHeight * (1f - clamped)));
        setViewHeight(volumeRow, Math.round(vr3VolumeHeight * (1f - clamped)));
        setViewHeight(controlPanel, Math.round(vr3ControlHeight * (1f - clamped)));
        setViewHeight(bottomButtonRow, vr3BottomRowHeight);

        float controlsAlpha = 1f - clamped;
        selectionTopRow.setAlpha(controlsAlpha);
        volumeRow.setAlpha(controlsAlpha);
        controlPanel.setAlpha(controlsAlpha);
        selectionTopRow.setTranslationY(dp(56) * clamped);
        volumeRow.setTranslationY(dp(70) * clamped);
        controlPanel.setTranslationY(dp(86) * clamped);
        selectionTopRow.setScaleY(1f - 0.12f * clamped);
        volumeRow.setScaleY(1f - 0.12f * clamped);
        controlPanel.setScaleY(1f - 0.08f * clamped);
        startButton.setAlpha(controlsAlpha);
        startButton.setTranslationX(-dp(72) * clamped);
        startButton.setScaleX(1f - 0.18f * clamped);
        startButton.setScaleY(1f - 0.08f * clamped);

        LinearLayout.LayoutParams startParams = (LinearLayout.LayoutParams) startButton.getLayoutParams();
        LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();
        startParams.weight = vr3StartWeight * (1f - clamped);
        stopParams.weight = vr3StopWeight + vr3StartWeight * clamped;
        stopParams.leftMargin = Math.round(vr3StopLeftMargin * (1f - clamped));
        startButton.setLayoutParams(startParams);
        stopButton.setLayoutParams(stopParams);

        timerCircle.setScaleX(1f + 0.18f * clamped);
        timerCircle.setScaleY(1f + 0.18f * clamped);
        timerCircle.setTranslationY(dp(16) * clamped);
    }

    private void applyVr3FinalLayout(boolean collapsed) {
        setViewHeight(selectionTopRow, collapsed ? 0 : vr3SelectionHeight);
        setViewHeight(volumeRow, collapsed ? 0 : vr3VolumeHeight);
        setViewHeight(controlPanel, collapsed ? 0 : vr3ControlHeight);
        setViewHeight(bottomButtonRow, vr3BottomRowHeight);

        LinearLayout.LayoutParams startParams = (LinearLayout.LayoutParams) startButton.getLayoutParams();
        LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();
        startParams.weight = collapsed ? 0f : vr3StartWeight;
        stopParams.weight = collapsed ? vr3StopWeight + vr3StartWeight : vr3StopWeight;
        stopParams.leftMargin = collapsed ? 0 : vr3StopLeftMargin;
        startButton.setLayoutParams(startParams);
        stopButton.setLayoutParams(stopParams);
    }

    private void setViewHeight(View view, int height) {
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }
        params.height = Math.max(0, height);
        view.setLayoutParams(params);
    }

    private void tintStartButton(int color) {
        startButtonText.setTextColor(color);
        startButtonIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private int dpInt(float value) {
        return Math.round(dp(value));
    }

    private int calculateCoolingMinutes() {
        CoolingResult result = calculateCoolingResult();
        if (!result.valid) {
            return -1;
        }
        if (result.seconds <= 0.0) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(result.seconds / 60.0));
    }

    private double calculateCurrentBeerTemperature(float elapsedProgress) {
        CoolingResult result = calculateCoolingResult();
        if (!result.valid || result.seconds <= 0.0) {
            return targetTemp;
        }
        double elapsedSeconds = Math.max(0.0, result.seconds * Math.max(0f, Math.min(1f, elapsedProgress)));
        double modelElapsedSeconds = elapsedSeconds * result.freezerCalibrationFactor;
        double dimensionlessTime = modelElapsedSeconds * result.hMax * result.area / result.thermalCapacity;
        double theta = Math.pow(4.0 / (dimensionlessTime + 4.0), 4.0);
        double temperature = deviceTemp + (startTemp - deviceTemp) * theta;
        return Math.max(targetTemp, Math.min(startTemp, temperature));
    }

    private CoolingResult calculateCoolingResult() {
        if (deviceTemp <= -273.15 || targetTemp <= deviceTemp) {
            return CoolingResult.invalid();
        }
        if (targetTemp >= startTemp) {
            return new CoolingResult(true, 0.0, 0.0, 0.0, 0.0, 1.0, freezerCalibrationFactor());
        }

        ContainerPreset preset = selectedContainerPreset();
        if (!preset.isValid()) {
            return CoolingResult.invalid();
        }

        CoolingModel model = calculateCoolingModelSeconds(
                startTemp,
                targetTemp,
                deviceTemp,
                preset
        );
        if (!model.valid) {
            return CoolingResult.invalid();
        }

        double freezerCalibrationFactor = freezerCalibrationFactor();
        double seconds = model.seconds / freezerCalibrationFactor;
        if (!Double.isFinite(seconds) || seconds < 0.0 || !Double.isFinite(freezerCalibrationFactor)) {
            return CoolingResult.invalid();
        }
        return new CoolingResult(
                true,
                seconds,
                model.hMax,
                model.area,
                model.thermalCapacity,
                model.thetaTarget,
                freezerCalibrationFactor
        );
    }

    private CoolingModel calculateCoolingModelSeconds(double startTempC, double targetTempC,
                                                      double deviceTempC, ContainerPreset preset) {
        if (deviceTempC <= -273.15 || targetTempC <= deviceTempC || preset == null || !preset.isValid()) {
            return CoolingModel.invalid();
        }
        if (targetTempC >= startTempC) {
            return new CoolingModel(true, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        double deltaStart = startTempC - deviceTempC;
        double thetaTarget = (targetTempC - deviceTempC) / deltaStart;
        if (thetaTarget <= 0.0 || thetaTarget >= 1.0) {
            return CoolingModel.invalid();
        }

        double deviceTemperatureK = deviceTempC + 273.15;
        double characteristicLength = Math.PI * preset.diameterMeters / 2.0;
        double area = Math.PI * preset.diameterMeters * preset.lengthMeters;
        if (preset.includeEndFaces) {
            area += Math.PI * preset.diameterMeters * preset.diameterMeters / 2.0;
        }
        double thermalCapacity = preset.beerMassKg * BEER_HEAT_CAPACITY
                + preset.containerMassKg * preset.containerHeatCapacity;
        double hMax = (AIR_THERMAL_CONDUCTIVITY / characteristicLength)
                * 0.402
                * Math.pow((GRAVITY * Math.pow(characteristicLength, 3.0))
                / (deviceTemperatureK * AIR_KINEMATIC_VISCOSITY * AIR_THERMAL_DIFFUSIVITY), CONVECTION_EXPONENT)
                * Math.pow(deltaStart, CONVECTION_EXPONENT);

        double dimensionlessTime = 4.0 * (Math.pow(thetaTarget, -CONVECTION_EXPONENT) - 1.0);
        double seconds = (thermalCapacity / (hMax * area)) * dimensionlessTime;
        if (!Double.isFinite(seconds) || seconds < 0.0) {
            return CoolingModel.invalid();
        }
        return new CoolingModel(true, seconds, hMax, area, thermalCapacity, thetaTarget);
    }

    private double freezerCalibrationFactor() {
        CoolingModel reference = calculateCoolingModelSeconds(
                REFERENCE_START_TEMP,
                REFERENCE_TARGET_TEMP,
                REFERENCE_DEVICE_TEMP,
                referenceBottlePreset()
        );
        if (!reference.valid || reference.seconds <= 0.0) {
            return 1.0;
        }
        return reference.seconds / REFERENCE_MEASURED_SECONDS;
    }

    private ContainerPreset selectedContainerPreset() {
        if (containerType == CONTAINER_CAN) {
            if (volumeIndex == VOLUME_SMALL) {
                return new ContainerPreset(CONTAINER_CAN, 0.33, 0.33, 0.015, 900.0, 0.066, 0.115, true);
            }
            if (volumeIndex == VOLUME_MEDIUM) {
                return new ContainerPreset(CONTAINER_CAN, 0.5, 0.5, 0.018, 900.0, 0.066, 0.168, true);
            }
            return ContainerPreset.invalid();
        }

        if (volumeIndex == VOLUME_SMALL) {
            return referenceBottlePreset();
        }
        if (volumeIndex == VOLUME_LARGE) {
            return new ContainerPreset(CONTAINER_BOTTLE, 1.0, 1.0, 0.65, 840.0, 0.085, 0.29, false);
        }
        return new ContainerPreset(CONTAINER_BOTTLE, 0.5, 0.5, 0.3, 840.0, 0.07, 0.21, false);
    }

    private ContainerPreset referenceBottlePreset() {
        return new ContainerPreset(CONTAINER_BOTTLE, 0.33, 0.33, 0.214, 840.0, 0.061, 0.235, false);
    }

    private static class ContainerPreset {
        final int containerType;
        final double volumeLiters;
        final double beerMassKg;
        final double containerMassKg;
        final double containerHeatCapacity;
        final double diameterMeters;
        final double lengthMeters;
        final boolean includeEndFaces;

        ContainerPreset(int containerType, double volumeLiters, double beerMassKg,
                        double containerMassKg, double containerHeatCapacity,
                        double diameterMeters, double lengthMeters, boolean includeEndFaces) {
            this.containerType = containerType;
            this.volumeLiters = volumeLiters;
            this.beerMassKg = beerMassKg;
            this.containerMassKg = containerMassKg;
            this.containerHeatCapacity = containerHeatCapacity;
            this.diameterMeters = diameterMeters;
            this.lengthMeters = lengthMeters;
            this.includeEndFaces = includeEndFaces;
        }

        static ContainerPreset invalid() {
            return new ContainerPreset(-1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false);
        }

        boolean isValid() {
            return volumeLiters > 0.0
                    && beerMassKg > 0.0
                    && containerMassKg > 0.0
                    && containerHeatCapacity > 0.0
                    && diameterMeters > 0.0
                    && lengthMeters > 0.0;
        }
    }

    private static class CoolingResult {
        final boolean valid;
        final double seconds;
        final double hMax;
        final double area;
        final double thermalCapacity;
        final double thetaTarget;
        final double freezerCalibrationFactor;

        CoolingResult(boolean valid, double seconds, double hMax, double area,
                      double thermalCapacity, double thetaTarget, double freezerCalibrationFactor) {
            this.valid = valid;
            this.seconds = seconds;
            this.hMax = hMax;
            this.area = area;
            this.thermalCapacity = thermalCapacity;
            this.thetaTarget = thetaTarget;
            this.freezerCalibrationFactor = freezerCalibrationFactor;
        }

        static CoolingResult invalid() {
            return new CoolingResult(false, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
        }
    }

    private static class CoolingModel {
        final boolean valid;
        final double seconds;
        final double hMax;
        final double area;
        final double thermalCapacity;
        final double thetaTarget;

        CoolingModel(boolean valid, double seconds, double hMax, double area,
                     double thermalCapacity, double thetaTarget) {
            this.valid = valid;
            this.seconds = seconds;
            this.hMax = hMax;
            this.area = area;
            this.thermalCapacity = thermalCapacity;
            this.thetaTarget = thetaTarget;
        }

        static CoolingModel invalid() {
            return new CoolingModel(false, 0.0, 0.0, 0.0, 0.0, 0.0);
        }
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
        if (preferences.getBoolean(KEY_ALARM_DISMISSED, false)) {
            preferences.edit().remove(KEY_ALARM_DISMISSED).apply();
            stopTimer(true);
            return;
        }
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
