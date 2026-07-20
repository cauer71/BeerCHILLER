package com.bierchiller.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

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
    private static final String KEY_TEMPERATURE_UNIT = "temperatureUnit";
    private static final String KEY_FREEZER_DEFAULT_MIGRATED = "freezerDefaultMigrated";
    static final String KEY_ALARM_DISMISSED = "alarmDismissed";
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final int SHOW_REQUEST_CODE = 1002;
    private static final int APP_UPDATE_REQUEST_CODE = 3001;
    private static final int VISUAL_CLASSIC = 0;
    private static final int VISUAL_BEER = 1;
    private static final int DEVICE_FREEZER = 0;
    private static final int DEVICE_FRIDGE = 1;
    private static final int CONTAINER_BOTTLE = 0;
    private static final int CONTAINER_CAN = 1;
    private static final int ORIENTATION_LYING = 0;
    private static final int ORIENTATION_STANDING = 1;
    private static final int FREEZER_TEMP = -18;
    private static final int LEGACY_FREEZER_TEMP = -14;
    private static final int FRIDGE_TEMP = 4;
    static final int DEFAULT_START_TEMP = 22;
    static final int DEFAULT_TARGET_TEMP = 8;
    static final int DEFAULT_DEVICE_TEMP = FREEZER_TEMP;
    static final int MIN_START_TEMP = -5;
    static final int MAX_START_TEMP = 40;
    static final int MIN_TARGET_TEMP = -5;
    static final int MAX_TARGET_TEMP = 20;
    static final int MIN_DEVICE_TEMP = -30;
    static final int MAX_DEVICE_TEMP = 5;
    private static final int UNIT_SYSTEM = 0;
    private static final int UNIT_CELSIUS = 1;
    private static final int UNIT_FAHRENHEIT = 2;
    private static final int VOLUME_SMALL = 0;
    private static final int VOLUME_MEDIUM = 1;
    private static final int VOLUME_LARGE = 2;
    private static final double CONVECTION_EXPONENT = 0.15;
    private static final double DELTA_REF_C = 25.0;
    private static final double DEVICE_FACTOR_FRIDGE = 1.0;
    private static final double DEVICE_FACTOR_FREEZER = 0.84;
    private static final double BOTTLE_POSITION_FACTOR_STANDING = 1.0;
    private static final double BOTTLE_POSITION_FACTOR_LYING = 0.95;
    private static final double CAN_POSITION_FACTOR_STANDING = 1.0;
    private static final double CAN_POSITION_FACTOR_LYING = 0.92;
    private static final double COLD_START_FULL_CORRECTION_C = 16.0;
    private static final double COLD_START_NO_CORRECTION_C = 24.0;
    private static final double COLD_START_MAX_EXTRA_FACTOR = 0.70;
    private static final String[] LANGUAGE_CODES = new String[]{
            LocaleHelper.SYSTEM_LANGUAGE, "de", "en", "it", "fr", "es", "pt", "nl", "pl", "cs", "hr"
    };

    private TimerCircleView timerCircle;
    private ImageView backgroundImage;
    private View backgroundOverlay;
    private View controlPanel;
    private View selectionTopRow;
    private View containerTypeGroup;
    private View orientationGroup;
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
    private int startTemp = DEFAULT_START_TEMP;
    private int targetTemp = DEFAULT_TARGET_TEMP;
    private int deviceTemp = DEFAULT_DEVICE_TEMP;
    private int volumeIndex = VOLUME_MEDIUM;
    private int deviceMode = DEVICE_FREEZER;
    private int containerType = CONTAINER_BOTTLE;
    private int orientation = ORIENTATION_LYING;
    private int temperatureUnit = UNIT_SYSTEM;
    private boolean running;
    private boolean beerControlsCollapsed;
    private ValueAnimator beerAnimator;
    private int beerSelectionHeight;
    private int beerVolumeHeight;
    private int beerControlHeight;
    private int beerBottomRowHeight;
    private int beerTimerHeight;
    private float beerStartWeight;
    private float beerStopWeight;
    private int beerStopLeftMargin;
    private Button selectedContainerButton;
    private Button selectedOrientationButton;
    private Button selectedVolumeButton;
    private Button selectedDeviceButton;
    private int visualMode;
    private AlarmManager alarmManager;
    private AppUpdateManager appUpdateManager;
    private SharedPreferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(lockUiFontScale(LocaleHelper.wrap(newBase)));
    }

    private static Context lockUiFontScale(Context context) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.fontScale = 1.0f;
        return context.createConfigurationContext(configuration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();
        setContentView(R.layout.activity_main);
        installHeaderStatusBarOffset();
        installPhonePortraitNavigationBarInset();
        installTabletSystemBarInsets();
        installPhoneLandscapeNavigationBarLayout();

        timerCircle = findViewById(R.id.timerCircle);
        timerCircle.setTranslationY(-18f);
        backgroundImage = findViewById(R.id.backgroundImage);
        backgroundOverlay = findViewById(R.id.backgroundOverlay);
        controlPanel = findViewById(R.id.controlPanel);
        selectionTopRow = findViewById(R.id.selectionTopRow);
        containerTypeGroup = findViewById(R.id.containerTypeGroup);
        orientationGroup = findViewById(R.id.orientationGroup);
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

        lockCompactControlText();
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        restoreInputPreferences();
        updateHeaderBrand();
        visualMode = readVisualModePreference();
        applyVisualMode();

        wireControls();
        wireMenu();
        requestNotificationPermission();
        requestFullScreenIntentPermissionIfNeeded();
        handleNotificationAction(getIntent());
        updateIdleDisplay();
        clearTimerStateOnAppStart();
        checkForAppUpdate();
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
            popupMenu.getMenu().add(0, 3, 0, R.string.menu_calculation_model);
            popupMenu.getMenu().add(0, 4, 1, R.string.menu_settings);
            popupMenu.getMenu().add(0, 5, 2, R.string.menu_info);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 3) {
                    startActivity(new Intent(this, HelpActivity.class));
                    return true;
                }
                if (item.getItemId() == 4) {
                    showSettingsDialog();
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
        applySelectorGroupBackgrounds(vrStyle);
        startButton.setBackgroundResource(vrStyle ? R.drawable.bg_primary_button_vr2 : R.drawable.bg_primary_button);
        stopButton.setBackgroundResource(vrStyle ? R.drawable.bg_secondary_button_vr2 : R.drawable.bg_secondary_button);
        int iconVisibility = vrStyle ? View.VISIBLE : View.GONE;
        startTempIcon.setVisibility(iconVisibility);
        targetTempIcon.setVisibility(iconVisibility);
        deviceTempIcon.setVisibility(iconVisibility);
        tintStartButton(visualBackground ? Color.WHITE : Color.parseColor("#102A33"));
        styleTemperatureControls(vrStyle);
        applyBeerRunningLayout(false);
    }

    private void applySelectorGroupBackgrounds(boolean vrStyle) {
        View[] groups = new View[]{containerTypeGroup, orientationGroup, volumeRow};
        if (!vrStyle) {
            controlPanel.setBackgroundResource(R.drawable.bg_control_panel);
            for (View group : groups) {
                group.setBackgroundResource(R.drawable.bg_segment_group);
            }
            return;
        }

        int alpha = Math.round(255f * 0.80f);
        int fill = Color.argb(alpha, 0xFB, 0xF5, 0xE7);
        int stroke = Color.argb(Math.min(255, alpha + 14), 0xF4, 0xDD, 0xAA);
        controlPanel.setBackground(createRoundedPanelBackground(fill, stroke, 30));
        for (View group : groups) {
            group.setBackground(createRoundedPanelBackground(fill, stroke, 22));
        }
    }

    private GradientDrawable createRoundedPanelBackground(int fill, int stroke, float radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(fill);
        background.setStroke(dpInt(1), stroke);
        background.setCornerRadius(dp(radiusDp));
        return background;
    }

    private int readVisualModePreference() {
        Object stored = preferences.getAll().get(KEY_VISUAL_MODE);
        if (stored instanceof Integer) {
            int mode = (Integer) stored;
            return mode == VISUAL_CLASSIC ? VISUAL_CLASSIC : VISUAL_BEER;
        }
        if (stored instanceof Boolean) {
            return (Boolean) stored ? VISUAL_BEER : VISUAL_CLASSIC;
        }
        return VISUAL_BEER;
    }

    private boolean isVrStyle() {
        return visualMode == VISUAL_BEER;
    }

    private void lockCompactControlText() {
        keepOneLine(headerBeerText, 28f, android.view.Gravity.CENTER_VERTICAL);
        keepOneLine(headerChillerText, 28f, android.view.Gravity.CENTER_VERTICAL);
        keepOneLine(startButtonText, 18f, android.view.Gravity.CENTER);
        keepOneLine(stopButton, 18f, android.view.Gravity.CENTER);

        Button[] selectorButtons = new Button[]{bottleButton, canButton, lyingButton, standingButton};
        for (Button button : selectorButtons) {
            keepOneLine(button, 15f, android.view.Gravity.CENTER);
            button.setPadding(dpInt(2), 0, dpInt(2), 0);
        }

        Button[] volumeButtons = new Button[]{volumeSmallButton, volumeMediumButton, volumeLargeButton};
        for (Button button : volumeButtons) {
            keepOneLine(button, 17f, android.view.Gravity.CENTER);
            button.setPadding(dpInt(2), 0, dpInt(2), 0);
        }

        Button[] deviceButtons = new Button[]{freezerButton, fridgeButton};
        for (Button button : deviceButtons) {
            keepOneLine(button, 15f, android.view.Gravity.CENTER);
            button.setPadding(dpInt(2), 0, dpInt(2), 0);
        }

        TextView[] labels = new TextView[]{startTempLabel, targetTempLabel, deviceTempLabel};
        for (TextView label : labels) {
            keepFixedText(label, 15f, 2, android.view.Gravity.CENTER_VERTICAL);
            label.setPadding(0, 0, dpInt(8), 0);
        }

        TextView[] values = new TextView[]{startTempValue, targetTempValue, deviceTempValue};
        for (TextView value : values) {
            keepOneLine(value, 19f, android.view.Gravity.CENTER);
        }

        Button[] stepButtons = new Button[]{
                startMinusButton, startPlusButton, targetMinusButton, targetPlusButton,
                deviceMinusButton, devicePlusButton
        };
        for (Button button : stepButtons) {
            keepOneLine(button, 25f, android.view.Gravity.CENTER);
            button.setPadding(0, 0, 0, 0);
        }
    }

    private void keepOneLine(TextView view, float textSizeDp, int gravity) {
        keepFixedText(view, textSizeDp, 1, gravity);
    }

    private void keepFixedText(TextView view, float textSizeDp, int maxLines, int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        }
        view.setSingleLine(maxLines == 1);
        view.setMaxLines(maxLines);
        view.setEllipsize(maxLines == 1 ? TextUtils.TruncateAt.END : null);
        view.setIncludeFontPadding(false);
        view.setGravity(gravity);
        view.setLineSpacing(0f, 0.94f);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDp);
    }

    private void lockTemperatureValueText() {
        TextView[] values = new TextView[]{startTempValue, targetTempValue, deviceTempValue};
        for (TextView value : values) {
            keepOneLine(value, 19f, android.view.Gravity.CENTER);
            value.setHorizontallyScrolling(false);
            value.setMinWidth(dpInt(78));
        }
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
            keepFixedText(label, 15f, 2, android.view.Gravity.CENTER_VERTICAL);
            label.setPadding(0, 0, dpInt(8), 0);
        }
    }

    private void applyBackgroundScale() {
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
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
                float scale = Math.max((float) viewWidth / drawableWidth,
                        (float) viewHeight / drawableHeight);
                float dx = (viewWidth - drawableWidth * scale) * 0.5f;
                float foamBoundary = drawableHeight * 0.105f;
                float targetFoamY = headerBeerText.getBottom() + dp(52);
                float dy = targetFoamY - foamBoundary * scale;
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                backgroundImage.setScaleType(ImageView.ScaleType.MATRIX);
                backgroundImage.setImageMatrix(matrix);
            });
            return;
        }
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

    private void showTemperatureUnitDialog() {
        String[] unitNames = new String[]{
                getString(R.string.temperature_unit_system),
                getString(R.string.temperature_unit_celsius),
                getString(R.string.temperature_unit_fahrenheit)
        };
        int currentSelection = Math.max(UNIT_SYSTEM, Math.min(temperatureUnit, UNIT_FAHRENHEIT));
        new AlertDialog.Builder(this)
                .setTitle(R.string.temperature_unit_title)
                .setSingleChoiceItems(unitNames, currentSelection, (dialog, which) -> {
                    temperatureUnit = Math.max(UNIT_SYSTEM, Math.min(which, UNIT_FAHRENHEIT));
                    preferences.edit().putInt(KEY_TEMPERATURE_UNIT, temperatureUnit).apply();
                    dialog.dismiss();
                    if (!running) {
                        updateIdleDisplay();
                    } else if (endTimeMillis > System.currentTimeMillis()) {
                        updateRunningTimerCircle(endTimeMillis - System.currentTimeMillis());
                    }
                })
                .setNegativeButton(R.string.close, null)
                .show();
    }

    private void showSettingsDialog() {
        String[] languageNames = languageNames();
        String[] unitNames = temperatureUnitNames();
        String[] visualModeNames = new String[]{
                getString(R.string.menu_classic_ui),
                getString(R.string.menu_beer_ui)
        };
        int currentLanguageSelection = LocaleHelper.currentSelectionIndex(this, LANGUAGE_CODES);
        int currentUnitSelection = Math.max(UNIT_SYSTEM, Math.min(temperatureUnit, UNIT_FAHRENHEIT));
        int currentVisualModeSelection = visualMode == VISUAL_CLASSIC ? 0 : 1;

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int horizontalPadding = dpInt(24);
        int verticalPadding = dpInt(8);
        content.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        scrollView.addView(content);

        TextView languageLabel = settingsSectionLabel(R.string.language_title);
        RadioGroup languageGroup = settingsRadioGroup(languageNames, currentLanguageSelection);
        TextView unitLabel = settingsSectionLabel(R.string.temperature_unit_title);
        RadioGroup unitGroup = settingsRadioGroup(unitNames, currentUnitSelection);
        TextView visualModeLabel = settingsSectionLabel(R.string.visual_style_title);
        RadioGroup visualModeGroup = settingsRadioGroup(visualModeNames, currentVisualModeSelection);

        content.addView(visualModeLabel);
        content.addView(visualModeGroup);
        content.addView(languageLabel);
        content.addView(languageGroup);
        content.addView(unitLabel);
        content.addView(unitGroup);

        new AlertDialog.Builder(this)
                .setTitle(R.string.settings_title)
                .setView(scrollView)
                .setPositiveButton(R.string.close, (dialog, which) -> {
                    int selectedLanguageIndex = languageGroup.indexOfChild(languageGroup.findViewById(languageGroup.getCheckedRadioButtonId()));
                    int selectedUnitIndex = unitGroup.indexOfChild(unitGroup.findViewById(unitGroup.getCheckedRadioButtonId()));
                    int selectedVisualModeIndex = visualModeGroup.indexOfChild(
                            visualModeGroup.findViewById(visualModeGroup.getCheckedRadioButtonId()));

                    boolean languageChanged = selectedLanguageIndex >= 0
                            && selectedLanguageIndex != currentLanguageSelection;
                    boolean unitChanged = selectedUnitIndex >= 0
                            && selectedUnitIndex != currentUnitSelection;

                    if (unitChanged) {
                        temperatureUnit = Math.max(UNIT_SYSTEM, Math.min(selectedUnitIndex, UNIT_FAHRENHEIT));
                        preferences.edit().putInt(KEY_TEMPERATURE_UNIT, temperatureUnit).apply();
                    }
                    if (selectedVisualModeIndex >= 0 && selectedVisualModeIndex != currentVisualModeSelection) {
                        setVisualMode(selectedVisualModeIndex == 0 ? VISUAL_CLASSIC : VISUAL_BEER);
                    }
                    if (languageChanged) {
                        LocaleHelper.setStoredLanguage(this, LANGUAGE_CODES[selectedLanguageIndex]);
                        recreate();
                    } else if (unitChanged) {
                        if (!running) {
                            updateIdleDisplay();
                        } else if (endTimeMillis > System.currentTimeMillis()) {
                            updateRunningTimerCircle(endTimeMillis - System.currentTimeMillis());
                        }
                    }
                })
                .show();
    }

    private String[] languageNames() {
        return new String[]{
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
    }

    private String[] temperatureUnitNames() {
        return new String[]{
                getString(R.string.temperature_unit_system),
                getString(R.string.temperature_unit_celsius),
                getString(R.string.temperature_unit_fahrenheit)
        };
    }

    private TextView settingsSectionLabel(int titleRes) {
        TextView label = new TextView(this);
        label.setText(titleRes);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        label.setTextColor(Color.parseColor("#5F767B"));
        label.setPadding(0, dpInt(16), 0, dpInt(4));
        return label;
    }

    private RadioGroup settingsRadioGroup(String[] labels, int checkedIndex) {
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < labels.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(labels[i]);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            radioButton.setPadding(0, dpInt(8), 0, dpInt(8));
            group.addView(radioButton);
            if (i == checkedIndex) {
                group.check(radioButton.getId());
            }
        }
        return group;
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
        startTemp = clamp(startTemp + delta, MIN_START_TEMP, MAX_START_TEMP);
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void changeTargetTemp(int delta) {
        if (running) {
            return;
        }
        targetTemp = clamp(targetTemp + delta, MIN_TARGET_TEMP, MAX_TARGET_TEMP);
        saveInputPreferences();
        updateIdleDisplay();
    }

    private void changeDeviceTemp(int delta) {
        if (running) {
            return;
        }
        deviceTemp = clamp(deviceTemp + delta, MIN_DEVICE_TEMP, MAX_DEVICE_TEMP);
        syncDeviceModeWithTemperature();
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

    private void syncDeviceModeWithTemperature() {
        deviceMode = deviceTemp < 0 ? DEVICE_FREEZER : DEVICE_FRIDGE;
    }

    private void restoreInputPreferences() {
        startTemp = restoreTemperaturePreference(
                preferences.contains(KEY_START_TEMP),
                preferences.getInt(KEY_START_TEMP, DEFAULT_START_TEMP),
                DEFAULT_START_TEMP,
                MIN_START_TEMP,
                MAX_START_TEMP
        );
        targetTemp = restoreTemperaturePreference(
                preferences.contains(KEY_TARGET_TEMP),
                preferences.getInt(KEY_TARGET_TEMP, DEFAULT_TARGET_TEMP),
                DEFAULT_TARGET_TEMP,
                MIN_TARGET_TEMP,
                MAX_TARGET_TEMP
        );
        deviceTemp = restoreTemperaturePreference(
                preferences.contains(KEY_DEVICE_TEMP),
                preferences.getInt(KEY_DEVICE_TEMP, DEFAULT_DEVICE_TEMP),
                DEFAULT_DEVICE_TEMP,
                MIN_DEVICE_TEMP,
                MAX_DEVICE_TEMP
        );
        deviceMode = preferences.getInt(KEY_DEVICE_MODE, deviceTemp == FRIDGE_TEMP ? DEVICE_FRIDGE : DEVICE_FREEZER);
        migrateLegacyFreezerDefault();
        volumeIndex = preferences.getInt(KEY_VOLUME_INDEX, volumeIndex);
        containerType = preferences.getInt(KEY_CONTAINER_TYPE, containerType);
        orientation = preferences.getInt(KEY_ORIENTATION, orientation);
        temperatureUnit = preferences.getInt(KEY_TEMPERATURE_UNIT, UNIT_SYSTEM);

        syncDeviceModeWithTemperature();
        containerType = containerType == CONTAINER_CAN ? CONTAINER_CAN : CONTAINER_BOTTLE;
        orientation = orientation == ORIENTATION_STANDING ? ORIENTATION_STANDING : ORIENTATION_LYING;
        temperatureUnit = temperatureUnit >= UNIT_SYSTEM && temperatureUnit <= UNIT_FAHRENHEIT
                ? temperatureUnit
                : UNIT_SYSTEM;
        volumeIndex = Math.max(VOLUME_SMALL, Math.min(volumeIndex, VOLUME_LARGE));
        if (containerType == CONTAINER_CAN && volumeIndex == VOLUME_LARGE) {
            volumeIndex = VOLUME_MEDIUM;
        }
    }

    static int restoreTemperaturePreference(boolean hasSavedValue, int savedValue,
                                            int defaultValue, int min, int max) {
        return clamp(hasSavedValue ? savedValue : defaultValue, min, max);
    }

    private void migrateLegacyFreezerDefault() {
        if (preferences.getBoolean(KEY_FREEZER_DEFAULT_MIGRATED, false)) {
            return;
        }
        if (deviceMode == DEVICE_FREEZER && deviceTemp == LEGACY_FREEZER_TEMP) {
            deviceTemp = FREEZER_TEMP;
            preferences.edit()
                    .putInt(KEY_DEVICE_TEMP, deviceTemp)
                    .putBoolean(KEY_FREEZER_DEFAULT_MIGRATED, true)
                    .apply();
            return;
        }
        preferences.edit().putBoolean(KEY_FREEZER_DEFAULT_MIGRATED, true).apply();
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
        TimerNotificationHelper.show(
                this,
                endTimeMillis,
                totalDurationMillis,
                startTemp,
                targetTemp,
                deviceTemp,
                shouldDisplayFahrenheit()
        );
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
            try {
                if (canScheduleExactTimerAlarm()) {
                    AlarmManager.AlarmClockInfo alarmClockInfo =
                            new AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                } else {
                    scheduleInexactTimerAlarm(triggerAtMillis, pendingIntent);
                }
            } catch (SecurityException ignored) {
                scheduleInexactTimerAlarm(triggerAtMillis, pendingIntent);
            }
        }
    }

    private boolean canScheduleExactTimerAlarm() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || alarmManager == null
                || alarmManager.canScheduleExactAlarms();
    }

    private void scheduleInexactTimerAlarm(long triggerAtMillis, PendingIntent pendingIntent) {
        if (alarmManager == null) {
            return;
        }
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } catch (RuntimeException ignored) {
            // The foreground timer continues running even if this platform rejects alarm scheduling.
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

    private void clearTimerStateOnAppStart() {
        TimerAlarmScheduler.cancel(this);
        stopService(new Intent(this, AlarmService.class));
        TimerNotificationHelper.cancel(this);
        endTimeMillis = 0;
        totalDurationMillis = 0;
        running = false;
        preferences.edit().remove(KEY_END_TIME).remove(KEY_TOTAL_DURATION).apply();
    }

    private AppUpdateManager getAppUpdateManager() {
        if (appUpdateManager == null) {
            appUpdateManager = AppUpdateManagerFactory.create(this);
        }
        return appUpdateManager;
    }

    private void checkForAppUpdate() {
        getAppUpdateManager().getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startImmediateAppUpdate(appUpdateInfo);
                    }
                });
    }

    private void resumeAppUpdateIfInProgress() {
        getAppUpdateManager().getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startImmediateAppUpdate(appUpdateInfo);
                    }
                });
    }

    @SuppressWarnings("deprecation")
    private void startImmediateAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            getAppUpdateManager().startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    APP_UPDATE_REQUEST_CODE
            );
        } catch (IntentSender.SendIntentException | RuntimeException ignored) {
            // Google Play handles supported devices. If the flow cannot start, keep the app usable.
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
                formatTemperatureDecimal(targetTemp),
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
                formatTemperatureDecimal(currentBeerTemp),
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
        startTempValue.setText(formatTemperature(startTemp));
        targetTempValue.setText(formatTemperature(targetTemp));
        deviceTempValue.setText(formatTemperature(deviceTemp));
        lockTemperatureValueText();
    }

    private String formatTemperature(double celsius) {
        if (shouldDisplayFahrenheit()) {
            return getString(R.string.degrees_fahrenheit, Math.round(celsiusToFahrenheit(celsius)));
        }
        return getString(R.string.degrees_celsius, Math.round(celsius));
    }

    private String formatTemperatureDecimal(double celsius) {
        if (shouldDisplayFahrenheit()) {
            return getString(R.string.degrees_fahrenheit_decimal, (float) celsiusToFahrenheit(celsius));
        }
        return getString(R.string.degrees_celsius_decimal, (float) celsius);
    }

    private boolean shouldDisplayFahrenheit() {
        if (temperatureUnit == UNIT_FAHRENHEIT) {
            return true;
        }
        if (temperatureUnit == UNIT_CELSIUS) {
            return false;
        }
        return localeUsesFahrenheit(Locale.getDefault());
    }

    static boolean localeUsesFahrenheit(Locale locale) {
        if (locale == null) {
            return false;
        }
        String country = locale.getCountry();
        return "US".equals(country)
                || "BS".equals(country)
                || "BZ".equals(country)
                || "KY".equals(country)
                || "PW".equals(country);
    }

    private static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32.0;
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
        Button newDeviceButton = deviceMode == DEVICE_FRIDGE ? fridgeButton : freezerButton;
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
        applyBeerRunningLayout(true);
    }

    // Portrait running animation shared by Classic and Beer UI.
    private void applyBeerRunningLayout(boolean animate) {
        boolean portrait = getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
        if (!portrait) {
            if (beerControlsCollapsed) {
                captureBeerOriginalSizes();
                applyBeerProgress(0f);
                beerControlsCollapsed = false;
            }
            return;
        }
        boolean collapse = running;
        if (!collapse && !beerControlsCollapsed) {
            return;
        }
        if (beerControlsCollapsed == collapse
                && timerCircle.getScaleX() == (collapse ? 1.18f : 1f)
                && viewHeight(controlPanel) == (collapse ? 0 : originalBeerHeight(controlPanel))) {
            return;
        }

        captureBeerOriginalSizes();
        if (beerAnimator != null) {
            beerAnimator.cancel();
        }

        float startProgress = currentBeerCollapseProgress();
        float endProgress = collapse ? 1f : 0f;
        beerControlsCollapsed = collapse;
        if (!animate) {
            applyBeerProgress(endProgress);
            applyBeerFinalLayout(collapse);
            return;
        }

        beerAnimator = ValueAnimator.ofFloat(startProgress, endProgress);
        beerAnimator.setDuration(600L);
        beerAnimator.setInterpolator(new PathInterpolator(0.2f, 0f, 0f, 1f));
        beerAnimator.addUpdateListener(animation -> applyBeerProgress((float) animation.getAnimatedValue()));
        beerAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                applyBeerProgress(endProgress);
                applyBeerFinalLayout(collapse);
            }
        });
        beerAnimator.start();
    }

    private void captureBeerOriginalSizes() {
        if (beerSelectionHeight <= 0) {
            beerSelectionHeight = Math.max(selectionTopRow.getHeight(), dpInt(42));
        }
        if (beerVolumeHeight <= 0) {
            beerVolumeHeight = Math.max(volumeRow.getHeight(), dpInt(44));
        }
        if (beerControlHeight <= 0) {
            beerControlHeight = Math.max(controlPanel.getHeight(), dpInt(226));
        }
        if (beerBottomRowHeight <= 0) {
            beerBottomRowHeight = Math.max(bottomButtonRow.getHeight(), dpInt(62));
        }
        if (beerTimerHeight <= 0) {
            beerTimerHeight = Math.max(timerCircle.getHeight(), dpInt(420));
        }
        if (beerStartWeight <= 0f) {
            beerStartWeight = ((LinearLayout.LayoutParams) startButton.getLayoutParams()).weight;
        }
        if (beerStopWeight <= 0f) {
            LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();
            beerStopWeight = stopParams.weight;
            beerStopLeftMargin = stopParams.leftMargin;
        }
    }

    private float currentBeerCollapseProgress() {
        if (timerCircle.getScaleX() > 1f) {
            return Math.max(0f, Math.min(1f, (timerCircle.getScaleX() - 1f) / 0.18f));
        }
        return beerControlsCollapsed ? 1f : 0f;
    }

    private int originalBeerHeight(View view) {
        if (view == selectionTopRow) {
            return beerSelectionHeight;
        }
        if (view == volumeRow) {
            return beerVolumeHeight;
        }
        if (view == controlPanel) {
            return beerControlHeight;
        }
        if (view == bottomButtonRow) {
            return beerBottomRowHeight;
        }
        return view.getHeight();
    }

    private int viewHeight(View view) {
        return view.getLayoutParams() != null ? view.getLayoutParams().height : view.getHeight();
    }

    private void applyBeerProgress(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));

        setViewHeight(selectionTopRow, Math.round(beerSelectionHeight * (1f - clamped)));
        setViewHeight(volumeRow, Math.round(beerVolumeHeight * (1f - clamped)));
        setViewHeight(controlPanel, Math.round(beerControlHeight * (1f - clamped)));
        setViewHeight(bottomButtonRow, beerBottomRowHeight);
        if (isPortraitTablet()) {
            int expandedHeight = expandedTabletTimerHeight();
            setViewHeight(timerCircle, Math.round(beerTimerHeight
                    + (expandedHeight - beerTimerHeight) * clamped));
        }

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
        startParams.weight = beerStartWeight * (1f - clamped);
        stopParams.weight = beerStopWeight + beerStartWeight * clamped;
        stopParams.leftMargin = Math.round(beerStopLeftMargin * (1f - clamped));
        startButton.setLayoutParams(startParams);
        stopButton.setLayoutParams(stopParams);

        timerCircle.setScaleX(1f + 0.18f * clamped);
        timerCircle.setScaleY(1f + 0.18f * clamped);
        timerCircle.setTranslationY(dp(16) * clamped);
    }

    private void applyBeerFinalLayout(boolean collapsed) {
        setViewHeight(selectionTopRow, collapsed ? 0 : beerSelectionHeight);
        setViewHeight(volumeRow, collapsed ? 0 : beerVolumeHeight);
        setViewHeight(controlPanel, collapsed ? 0 : beerControlHeight);
        setViewHeight(bottomButtonRow, beerBottomRowHeight);
        if (isPortraitTablet()) {
            setViewHeight(timerCircle, collapsed ? expandedTabletTimerHeight() : beerTimerHeight);
        }

        LinearLayout.LayoutParams startParams = (LinearLayout.LayoutParams) startButton.getLayoutParams();
        LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();
        startParams.weight = collapsed ? 0f : beerStartWeight;
        stopParams.weight = collapsed ? beerStopWeight + beerStartWeight : beerStopWeight;
        stopParams.leftMargin = collapsed ? 0 : beerStopLeftMargin;
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

    private boolean isPortraitTablet() {
        return getResources().getConfiguration().smallestScreenWidthDp >= 600
                && getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
    }

    private int expandedTabletTimerHeight() {
        int rootHeight = timerCircle.getRootView().getHeight();
        int fixedBottomSpace = dpInt(12 + 12 + 16 + 22 + 78 + 48);
        return Math.max(beerTimerHeight, rootHeight - timerCircle.getTop() - fixedBottomSpace);
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
        double temperatureTerm = (Math.pow(result.thetaTarget, -CONVECTION_EXPONENT) - 1.0)
                / CONVECTION_EXPONENT;
        if (!Double.isFinite(temperatureTerm) || temperatureTerm <= 0.0) {
            return targetTemp;
        }
        double tauEffectiveSeconds = result.seconds / temperatureTerm;
        double theta = Math.pow(
                1.0 + CONVECTION_EXPONENT * elapsedSeconds / tauEffectiveSeconds,
                -1.0 / CONVECTION_EXPONENT
        );
        double temperature = deviceTemp + (startTemp - deviceTemp) * theta;
        return Math.max(targetTemp, Math.min(startTemp, temperature));
    }

    private CoolingResult calculateCoolingResult() {
        OrientationPreset orientationPreset = selectedOrientationPreset();
        if (deviceTemp <= -273.15 || targetTemp <= deviceTemp) {
            return CoolingResult.invalid();
        }
        if (targetTemp >= startTemp) {
            return new CoolingResult(true, 0.0, 1.0,
                    orientationPreset.orientation, orientationPreset.factor);
        }

        ContainerPreset preset = selectedContainerPreset();
        if (!preset.isValid()) {
            return CoolingResult.invalid();
        }

        CoolingModel model = calculateCoolingModelSeconds(
                startTemp,
                targetTemp,
                deviceTemp,
                preset,
                deviceMode,
                orientationPreset.orientation
        );
        if (!model.valid) {
            return CoolingResult.invalid();
        }

        if (!Double.isFinite(model.seconds) || model.seconds < 0.0
                || !Double.isFinite(orientationPreset.factor)
                || orientationPreset.factor <= 0.0
                || !Double.isFinite(model.thetaTarget)) {
            return CoolingResult.invalid();
        }
        return new CoolingResult(
                true,
                model.seconds,
                model.thetaTarget,
                orientationPreset.orientation,
                orientationPreset.factor
        );
    }

    static double orientationFactorFor(int requestedOrientation) {
        return positionFactorFor(CONTAINER_BOTTLE, requestedOrientation);
    }

    static double applyCalibrationFactors(double modelSeconds,
                                          double environmentCalibrationFactor,
                                          double orientationFactor) {
        return modelSeconds * environmentCalibrationFactor * orientationFactor;
    }

    static double applyCalibrationFactors(double modelSeconds,
                                          double environmentCalibrationFactor,
                                          double orientationFactor,
                                          double containerCoolingFactor) {
        return modelSeconds * environmentCalibrationFactor * orientationFactor * containerCoolingFactor;
    }

    static CoolingModel calculateCoolingModelSeconds(double startTempC, double targetTempC,
                                                     double deviceTempC, ContainerPreset preset) {
        return calculateCoolingModelSeconds(
                startTempC,
                targetTempC,
                deviceTempC,
                preset,
                DEVICE_FRIDGE,
                ORIENTATION_STANDING
        );
    }

    static CoolingModel calculateCoolingModelSeconds(double startTempC, double targetTempC,
                                                     double deviceTempC, ContainerPreset preset,
                                                     int requestedDeviceMode, int requestedOrientation) {
        if (deviceTempC <= -273.15 || targetTempC <= deviceTempC || preset == null || !preset.isValid()) {
            return CoolingModel.invalid();
        }
        if (targetTempC >= startTempC) {
            return new CoolingModel(true, 0.0, 1.0);
        }

        double delta0 = startTempC - deviceTempC;
        if (!Double.isFinite(delta0) || delta0 <= 0.0) {
            return CoolingModel.invalid();
        }

        double thetaTarget = (targetTempC - deviceTempC) / delta0;
        if (!Double.isFinite(thetaTarget) || thetaTarget <= 0.0 || thetaTarget >= 1.0) {
            return CoolingModel.invalid();
        }

        double deltaCorrection = Math.pow(DELTA_REF_C / delta0, CONVECTION_EXPONENT);
        double temperatureTerm = (Math.pow(thetaTarget, -CONVECTION_EXPONENT) - 1.0)
                / CONVECTION_EXPONENT;
        double coldStartFactor = coldBottleFreezerStartFactor(
                preset.containerType,
                requestedDeviceMode,
                startTempC
        );
        double minutes = preset.baseTauMinutes
                * deviceFactorFor(requestedDeviceMode)
                * positionFactorFor(preset.containerType, requestedOrientation)
                * deltaCorrection
                * coldStartFactor
                * temperatureTerm;
        double seconds = minutes * 60.0;
        if (!Double.isFinite(seconds) || seconds < 0.0) {
            return CoolingModel.invalid();
        }
        return new CoolingModel(true, seconds, thetaTarget);
    }

    static double coldBottleFreezerStartFactor(int requestedContainerType,
                                               int requestedDeviceMode,
                                               double startTempC) {
        if (requestedContainerType != CONTAINER_BOTTLE || requestedDeviceMode != DEVICE_FREEZER) {
            return 1.0;
        }
        double x = (COLD_START_NO_CORRECTION_C - startTempC)
                / (COLD_START_NO_CORRECTION_C - COLD_START_FULL_CORRECTION_C);
        double correctionStrength = smoothstep01(x);
        return 1.0 + COLD_START_MAX_EXTRA_FACTOR * correctionStrength;
    }

    private static double smoothstep01(double value) {
        double x = clamp01(value);
        return x * x * (3.0 - 2.0 * x);
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    static double surfaceAreaFor(ContainerPreset preset) {
        double sideArea = Math.PI * preset.diameterMeters * preset.lengthMeters;
        if (!preset.includeEndFaces) {
            return sideArea;
        }
        return sideArea + Math.PI * preset.diameterMeters * preset.diameterMeters / 2.0;
    }

    static double containerCoolingFactorFor(int requestedContainerType) {
        return 1.0;
    }

    static double deviceFactorFor(int requestedDeviceMode) {
        return requestedDeviceMode == DEVICE_FREEZER
                ? DEVICE_FACTOR_FREEZER
                : DEVICE_FACTOR_FRIDGE;
    }

    static double positionFactorFor(int requestedContainerType, int requestedOrientation) {
        if (requestedContainerType == CONTAINER_CAN) {
            return requestedOrientation == ORIENTATION_LYING
                    ? CAN_POSITION_FACTOR_LYING
                    : CAN_POSITION_FACTOR_STANDING;
        }
        return requestedOrientation == ORIENTATION_LYING
                ? BOTTLE_POSITION_FACTOR_LYING
                : BOTTLE_POSITION_FACTOR_STANDING;
    }

    private ContainerPreset selectedContainerPreset() {
        return containerPresetFor(containerType, volumeIndex);
    }

    static ContainerPreset containerPresetFor(int requestedContainerType, int requestedVolumeIndex) {
        if (requestedContainerType == CONTAINER_CAN) {
            if (requestedVolumeIndex == VOLUME_SMALL) {
                return new ContainerPreset(CONTAINER_CAN, 0.33, 85.0,
                        0.33, 0.015, 900.0, 0.066, 0.115, true);
            }
            if (requestedVolumeIndex == VOLUME_MEDIUM) {
                return new ContainerPreset(CONTAINER_CAN, 0.5, 105.0,
                        0.5, 0.018, 900.0, 0.066, 0.168, true);
            }
            return ContainerPreset.invalid();
        }

        if (requestedVolumeIndex == VOLUME_SMALL) {
            return referenceBottlePreset();
        }
        if (requestedVolumeIndex == VOLUME_LARGE) {
            return new ContainerPreset(CONTAINER_BOTTLE, 1.0, 155.0,
                    1.0, 0.65, 840.0, 0.085, 0.29, false);
        }
        return new ContainerPreset(CONTAINER_BOTTLE, 0.5, 110.0,
                0.5, 0.3, 840.0, 0.07, 0.21, false);
    }

    static ContainerPreset referenceBottlePreset() {
        return new ContainerPreset(CONTAINER_BOTTLE, 0.33, 87.0,
                0.33, 0.214, 840.0, 0.061, 0.235, false);
    }

    private OrientationPreset selectedOrientationPreset() {
        int selectedOrientation = orientation == ORIENTATION_STANDING ? ORIENTATION_STANDING : ORIENTATION_LYING;
        return new OrientationPreset(selectedOrientation, orientationFactorFor(selectedOrientation));
    }

    static class ContainerPreset {
        final int containerType;
        final double volumeLiters;
        final double baseTauMinutes;
        final double beerMassKg;
        final double containerMassKg;
        final double containerHeatCapacity;
        final double diameterMeters;
        final double lengthMeters;
        final boolean includeEndFaces;

        ContainerPreset(int containerType, double volumeLiters, double baseTauMinutes, double beerMassKg,
                        double containerMassKg, double containerHeatCapacity,
                        double diameterMeters, double lengthMeters, boolean includeEndFaces) {
            this.containerType = containerType;
            this.volumeLiters = volumeLiters;
            this.baseTauMinutes = baseTauMinutes;
            this.beerMassKg = beerMassKg;
            this.containerMassKg = containerMassKg;
            this.containerHeatCapacity = containerHeatCapacity;
            this.diameterMeters = diameterMeters;
            this.lengthMeters = lengthMeters;
            this.includeEndFaces = includeEndFaces;
        }

        static ContainerPreset invalid() {
            return new ContainerPreset(-1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false);
        }

        boolean isValid() {
            return volumeLiters > 0.0
                    && baseTauMinutes > 0.0
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
        final double thetaTarget;
        final int orientation;
        final double orientationFactor;

        CoolingResult(boolean valid, double seconds, double thetaTarget,
                      int orientation, double orientationFactor) {
            this.valid = valid;
            this.seconds = seconds;
            this.thetaTarget = thetaTarget;
            this.orientation = orientation;
            this.orientationFactor = orientationFactor;
        }

        static CoolingResult invalid() {
            return new CoolingResult(false, 0.0, 0.0,
                    ORIENTATION_LYING, BOTTLE_POSITION_FACTOR_LYING);
        }
    }

    private static class OrientationPreset {
        final int orientation;
        final double factor;

        OrientationPreset(int orientation, double factor) {
            this.orientation = orientation;
            this.factor = factor;
        }
    }

    static class CoolingModel {
        final boolean valid;
        final double seconds;
        final double thetaTarget;

        CoolingModel(boolean valid, double seconds, double thetaTarget) {
            this.valid = valid;
            this.seconds = seconds;
            this.thetaTarget = thetaTarget;
        }

        static CoolingModel invalid() {
            return new CoolingModel(false, 0.0, 0.0);
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

    static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 2001);
        }
    }

    private void requestFullScreenIntentPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || isFinishing()) {
            return;
        }
        android.app.NotificationManager notificationManager =
                getSystemService(android.app.NotificationManager.class);
        if (notificationManager == null || notificationManager.canUseFullScreenIntent()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.full_screen_intent_title)
                .setMessage(R.string.full_screen_intent_message)
                .setPositiveButton(R.string.open_settings, (dialog, which) -> openFullScreenIntentSettings())
                .setNegativeButton(R.string.close, null)
                .show();
    }

    private void openFullScreenIntentSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                .setData(Uri.parse("package:" + getPackageName()));
        try {
            startActivity(intent);
        } catch (RuntimeException ignored) {
            Intent fallbackIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(fallbackIntent);
        }
    }

    private void configureSystemBars() {
        Window window = getWindow();
        WindowCompat.enableEdgeToEdge(window);
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        boolean phoneLandscape = getResources().getConfiguration().smallestScreenWidthDp < 600
                && getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
        }
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.show(WindowInsetsCompat.Type.statusBars()
                | WindowInsetsCompat.Type.navigationBars());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(!phoneLandscape);
    }

    private void installHeaderStatusBarOffset() {
        View header = findViewById(R.id.headerRow);
        if (header == null) {
            header = findViewById(R.id.headerTitleGroup);
        }
        if (header == null) {
            return;
        }
        final View titleHeader = header;
        boolean tablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        if (tablet) {
            titleHeader.setTranslationY(0f);
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(titleHeader, (view, insets) -> {
            int statusBarHeight = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars()
                            | WindowInsetsCompat.Type.displayCutout()).top;
            titleHeader.setTranslationY(statusBarHeight / 2);
            return insets;
        });
        ViewCompat.requestApplyInsets(titleHeader);
    }

    private void installPhoneLandscapeNavigationBarLayout() {
        View navigationBarBackground = findViewById(R.id.phoneLandscapeNavBarBackground);
        View mainContent = findViewById(R.id.mainContent);
        if (navigationBarBackground == null || mainContent == null) {
            return;
        }

        FrameLayout.LayoutParams baseContentParams =
                (FrameLayout.LayoutParams) mainContent.getLayoutParams();
        final int baseLeftMargin = baseContentParams.leftMargin;
        final int baseRightMargin = baseContentParams.rightMargin;
        View decorView = getWindow().getDecorView();
        ViewCompat.setOnApplyWindowInsetsListener(decorView, (view, insets) -> {
            androidx.core.graphics.Insets navigationBars = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars());
            androidx.core.graphics.Insets safeDrawing = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            FrameLayout.LayoutParams contentParams =
                    (FrameLayout.LayoutParams) mainContent.getLayoutParams();
            contentParams.leftMargin = baseLeftMargin + safeDrawing.left;
            contentParams.rightMargin = baseRightMargin + safeDrawing.right;
            mainContent.setLayoutParams(contentParams);

            FrameLayout.LayoutParams backgroundParams =
                    (FrameLayout.LayoutParams) navigationBarBackground.getLayoutParams();
            backgroundParams.width = navigationBars.left > 0
                    ? navigationBars.left : navigationBars.right;
            backgroundParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            backgroundParams.leftMargin = 0;
            backgroundParams.rightMargin = 0;
            backgroundParams.gravity = navigationBars.left > 0 ? Gravity.START : Gravity.END;
            navigationBarBackground.setLayoutParams(backgroundParams);
            navigationBarBackground.setVisibility(backgroundParams.width > 0
                    ? View.VISIBLE : View.GONE);
            return insets;
        });
        ViewCompat.requestApplyInsets(decorView);
    }

    private void installPhonePortraitNavigationBarInset() {
        View phonePortraitContent = findViewById(R.id.phonePortraitContent);
        if (phonePortraitContent == null) {
            return;
        }

        final int basePaddingBottom = phonePortraitContent.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(phonePortraitContent, (view, insets) -> {
            androidx.core.graphics.Insets safeDrawing = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            phonePortraitContent.setPadding(
                    phonePortraitContent.getPaddingLeft(),
                    phonePortraitContent.getPaddingTop(),
                    phonePortraitContent.getPaddingRight(),
                    Math.max(basePaddingBottom, safeDrawing.bottom));
            return insets;
        });
        ViewCompat.requestApplyInsets(phonePortraitContent);
    }

    private void installTabletSystemBarInsets() {
        View tabletContent = findViewById(R.id.tabletContent);
        if (tabletContent == null) {
            return;
        }

        final int basePaddingLeft = tabletContent.getPaddingLeft();
        final int basePaddingTop = tabletContent.getPaddingTop();
        final int basePaddingRight = tabletContent.getPaddingRight();
        final int basePaddingBottom = tabletContent.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(tabletContent, (view, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            tabletContent.setPadding(
                    basePaddingLeft + systemBars.left,
                    basePaddingTop + systemBars.top,
                    basePaddingRight + systemBars.right,
                    basePaddingBottom + systemBars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(tabletContent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureSystemBars();
        resumeAppUpdateIfInProgress();
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
            configureSystemBars();
        }
    }
}
