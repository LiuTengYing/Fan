package com.android.settings.display;

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.display.ColorPickerView;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.unisoc.settings.utils.ToastManager;
/* loaded from: classes.dex */
public class ColorTemperatureActivity extends CollapsingToolbarBaseActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioButton mAutomaticButton;
    private int mCheckButtonId;
    private ColorPickerView mColorPickerView;
    private RadioGroup mColorRadioGroup;
    private int mContrastMode;
    private RadioGroup mContrastRadioGroup;
    private RadioButton mCoolColorTButton;
    private int mCurrentUser;
    private final Handler mHandler;
    private RadioButton mIncreasesButton;
    private RadioButton mNormalColorButton;
    private final SettingsObserver mSettingsObserver;
    private RadioButton mStandardButton;
    private boolean mVersionForS;
    private RadioButton mWarmColorButton;

    public ColorTemperatureActivity() {
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mCurrentUser = -10000;
        this.mVersionForS = SystemProperties.getInt("ro.product.vndk.version", 0) == 31;
    }

    @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.color_temperature_ajusting);
        View decorView = getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(isInDarkMode() ? systemUiVisibility & (-17) : systemUiVisibility | 16);
        setTitle(R$string.colors_contrast_title);
        RadioGroup radioGroup = (RadioGroup) findViewById(R$id.radio_group_contrast);
        this.mContrastRadioGroup = radioGroup;
        radioGroup.setOnCheckedChangeListener(this);
        this.mAutomaticButton = (RadioButton) findViewById(R$id.automatic_button);
        this.mIncreasesButton = (RadioButton) findViewById(R$id.increased_button);
        this.mStandardButton = (RadioButton) findViewById(R$id.standard_button);
        RadioGroup radioGroup2 = (RadioGroup) findViewById(R$id.radio_group_color);
        this.mColorRadioGroup = radioGroup2;
        radioGroup2.setOnCheckedChangeListener(this);
        this.mWarmColorButton = (RadioButton) findViewById(R$id.color_warm);
        this.mNormalColorButton = (RadioButton) findViewById(R$id.color_normal);
        this.mCoolColorTButton = (RadioButton) findViewById(R$id.color_cool);
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R$id.main_colorPickerView);
        this.mColorPickerView = colorPickerView;
        colorPickerView.setOnColorPickerViewTouchListener(new onColorPickerViewTouchListener());
        this.mColorPickerView.setOnColorPickerViewSizeChangedListener(new onColorPickerViewSizeChangedListener());
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (getResources().getConfiguration().orientation == 2) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags |= 256;
            getWindow().setAttributes(attributes);
            getWindow().addFlags(256);
        } else if (getResources().getConfiguration().orientation == 1) {
            WindowManager.LayoutParams attributes2 = getWindow().getAttributes();
            attributes2.flags &= -257;
            getWindow().setAttributes(attributes2);
            getWindow().clearFlags(256);
            recreate();
        }
    }

    public boolean isInDarkMode() {
        return ((UiModeManager) getSystemService(UiModeManager.class)).getNightMode() == 2 || ((PowerManager) getSystemService(PowerManager.class)).isPowerSaveMode();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        this.mSettingsObserver.setListening(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mContrastMode = getContrastMode();
        Log.d("ColorTemperatureActivity", "mContrastMode: " + this.mContrastMode + " colorValue: " + getColorValue());
        int contrastModeToCheckedId = contrastModeToCheckedId(this.mContrastMode);
        this.mCheckButtonId = contrastModeToCheckedId;
        this.mContrastRadioGroup.check(contrastModeToCheckedId);
        if (isNightDisplayActivated()) {
            setColorTemperatureEnabled(false);
            this.mAutomaticButton.setEnabled(false);
            this.mIncreasesButton.setEnabled(false);
            this.mStandardButton.setEnabled(false);
            Toast makeText = Toast.makeText(this, getString(R$string.color_temperature_disabled_toast), 0);
            ToastManager.setToast(makeText);
            makeText.show();
        } else {
            setColorTemperatureEnabled(true);
            this.mAutomaticButton.setEnabled(true);
            this.mIncreasesButton.setEnabled(true);
            this.mStandardButton.setEnabled(true);
        }
        getColorValue();
        if (this.mContrastMode != 1) {
            setColorTemperatureEnabled(false);
        }
        updateColorTemperatureButton();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        this.mSettingsObserver.setListening(false);
    }

    @Override // android.widget.RadioGroup.OnCheckedChangeListener
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Log.d("ColorTemperatureActivity", "onCheckedChanged checkedId = " + i);
        if (radioGroup == this.mContrastRadioGroup) {
            if (i == R$id.automatic_button) {
                this.mContrastMode = 1;
                setColorTemperatureEnabled(true);
                updateColorTemperatureButton();
            } else if (i == R$id.increased_button) {
                this.mContrastMode = 2;
                setColorTemperatureEnabled(false);
            } else if (i == R$id.standard_button) {
                this.mContrastMode = 3;
                setColorTemperatureEnabled(false);
            } else {
                this.mContrastMode = 1;
                setColorTemperatureEnabled(true);
            }
            if (this.mVersionForS && isNightDisplayActivated()) {
                this.mContrastMode = 0;
            }
            setContrastMode(this.mContrastMode);
        } else if (radioGroup == this.mColorRadioGroup) {
            float[] circleCenter = this.mColorPickerView.getCircleCenter();
            int i2 = -16777214;
            if (i == R$id.color_warm) {
                circleCenter = calculateColorCoordinate(-16777215);
                i2 = -16777215;
            } else {
                if (i == R$id.color_normal) {
                    circleCenter = this.mColorPickerView.getCircleCenter();
                } else if (i == R$id.color_cool) {
                    circleCenter = calculateColorCoordinate(-16777214);
                }
                i2 = -16777216;
            }
            if (circleCenter != null) {
                if (this.mWarmColorButton.isChecked() || this.mNormalColorButton.isChecked() || this.mCoolColorTButton.isChecked()) {
                    setColorValue(i2);
                    this.mColorPickerView.setMarkerPoint(circleCenter[0], circleCenter[1]);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setColorTemperatureEnabled(boolean z) {
        this.mWarmColorButton.setEnabled(z);
        this.mNormalColorButton.setEnabled(z);
        this.mCoolColorTButton.setEnabled(z);
        this.mColorPickerView.setEnabled(z);
        this.mColorPickerView.setTouchable(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColorTemperatureButton() {
        int colorValueToCheckedId = colorValueToCheckedId(getColorValue());
        if (colorValueToCheckedId == -1) {
            this.mColorRadioGroup.clearCheck();
        } else {
            this.mColorRadioGroup.check(colorValueToCheckedId);
        }
    }

    public int contrastModeToCheckedId(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    return R$id.standard_button;
                }
                return R$id.automatic_button;
            }
            return R$id.increased_button;
        }
        return R$id.automatic_button;
    }

    public int colorValueToCheckedId(int i) {
        switch (i) {
            case -16777216:
                return R$id.color_normal;
            case -16777215:
                return R$id.color_warm;
            case -16777214:
                return R$id.color_cool;
            default:
                return -1;
        }
    }

    /* loaded from: classes.dex */
    public class onColorPickerViewTouchListener implements ColorPickerView.OnColorPickerViewTouchListener {
        public onColorPickerViewTouchListener() {
        }

        @Override // com.android.settings.display.ColorPickerView.OnColorPickerViewTouchListener
        public void onTouchActionDown(int i, float f, float f2) {
            updateColor(i, f, f2);
        }

        @Override // com.android.settings.display.ColorPickerView.OnColorPickerViewTouchListener
        public void onTouchActionMove(int i, float f, float f2) {
            updateColor(i, f, f2);
        }

        private void updateColor(int i, float f, float f2) {
            Log.d("ColorTemperatureActivity", " updateColor,  colorValue= " + i + ", " + Integer.toHexString(i));
            int colorValue = ColorTemperatureActivity.this.getColorValue();
            ColorTemperatureActivity.this.setColorValue(i & 16777215);
            ColorTemperatureActivity.this.setColorCoordinate(f, f2);
            if (ColorTemperatureActivity.this.colorValueToCheckedId(colorValue) != -1) {
                if (ColorTemperatureActivity.this.mWarmColorButton.isChecked() || ColorTemperatureActivity.this.mNormalColorButton.isChecked() || ColorTemperatureActivity.this.mCoolColorTButton.isChecked()) {
                    ColorTemperatureActivity.this.updateColorTemperatureButton();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class onColorPickerViewSizeChangedListener implements ColorPickerView.OnColorPickerViewSizeChangedListener {
        public onColorPickerViewSizeChangedListener() {
        }

        @Override // com.android.settings.display.ColorPickerView.OnColorPickerViewSizeChangedListener
        public boolean onSizeChanged(int i, int i2) {
            float[] colorCoordinate = ColorTemperatureActivity.this.getColorCoordinate(i, i2);
            if (colorCoordinate == null) {
                return false;
            }
            ColorTemperatureActivity.this.mColorPickerView.setMarkerPoint(colorCoordinate[0], colorCoordinate[1]);
            return true;
        }
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        private final Uri NIGHT_DISPLAY_ACTIVATED;

        public SettingsObserver(Handler handler) {
            super(handler);
            this.NIGHT_DISPLAY_ACTIVATED = Settings.Secure.getUriFor("night_display_activated");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (this.NIGHT_DISPLAY_ACTIVATED.equals(uri)) {
                if (ColorTemperatureActivity.this.isNightDisplayActivated()) {
                    ColorTemperatureActivity.this.setColorTemperatureEnabled(false);
                    ColorTemperatureActivity.this.mAutomaticButton.setEnabled(false);
                    ColorTemperatureActivity.this.mIncreasesButton.setEnabled(false);
                    ColorTemperatureActivity.this.mStandardButton.setEnabled(false);
                    ColorTemperatureActivity colorTemperatureActivity = ColorTemperatureActivity.this;
                    Toast makeText = Toast.makeText(colorTemperatureActivity, colorTemperatureActivity.getString(R$string.color_temperature_disabled_toast), 0);
                    ToastManager.setToast(makeText);
                    makeText.show();
                    return;
                }
                ColorTemperatureActivity colorTemperatureActivity2 = ColorTemperatureActivity.this;
                colorTemperatureActivity2.mContrastMode = colorTemperatureActivity2.getContrastMode();
                if (ColorTemperatureActivity.this.mContrastMode == 1) {
                    ColorTemperatureActivity.this.setColorTemperatureEnabled(true);
                }
                ColorTemperatureActivity.this.mAutomaticButton.setEnabled(true);
                ColorTemperatureActivity.this.mIncreasesButton.setEnabled(true);
                ColorTemperatureActivity.this.mStandardButton.setEnabled(true);
            }
        }

        public void setListening(boolean z) {
            ContentResolver contentResolver = ColorTemperatureActivity.this.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.NIGHT_DISPLAY_ACTIVATED, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }
    }

    public void setColorValue(int i) {
        Settings.System.putInt(getContentResolver(), "sprd_display_color_temperature_auto_mode_value", i);
    }

    public int getColorValue() {
        return Settings.System.getInt(getContentResolver(), "sprd_display_color_temperature_auto_mode_value", -16777216);
    }

    public void setColorCoordinate(float f, float f2) {
        ColorPickerView colorPickerView = this.mColorPickerView;
        if (colorPickerView == null) {
            return;
        }
        int[] pickerViewSize = colorPickerView.getPickerViewSize();
        Settings.System.putString(getContentResolver(), "sprd_display_color_temperature_point_coordinate", "" + (f / pickerViewSize[0]) + "," + (f2 / pickerViewSize[1]));
    }

    public float[] getColorCoordinate(int i, int i2) {
        String string;
        float[] fArr = new float[2];
        if (getColorValue() == -16777215) {
            return calculateColorCoordinate(-16777215);
        }
        if (getColorValue() == -16777214) {
            return calculateColorCoordinate(-16777214);
        }
        if (getColorValue() == -16777216 || (string = Settings.System.getString(getContentResolver(), "sprd_display_color_temperature_point_coordinate")) == null) {
            return null;
        }
        String[] split = string.split(",");
        fArr[0] = Float.parseFloat(split[0]) * i;
        fArr[1] = Float.parseFloat(split[1]) * i2;
        return fArr;
    }

    public void setContrastMode(int i) {
        Settings.System.putInt(getContentResolver(), "sprd_display_color_temperature_mode", i);
    }

    public int getContrastMode() {
        return Settings.System.getInt(getContentResolver(), "sprd_display_color_temperature_mode", 1);
    }

    public boolean isNightDisplayActivated() {
        return Settings.Secure.getInt(getContentResolver(), "night_display_activated", 0) == 1;
    }

    private float[] calculateColorCoordinate(int i) {
        float[] fArr = {0.0f, 0.0f};
        ColorPickerView colorPickerView = this.mColorPickerView;
        if (colorPickerView == null) {
            return fArr;
        }
        float[] circleCenter = colorPickerView.getCircleCenter();
        float circleRadius = this.mColorPickerView.getCircleRadius();
        if (-16777215 == i) {
            double d = circleRadius;
            circleCenter[0] = (float) (circleCenter[0] - ((Math.cos(Math.toRadians(60.0d)) * d) * 0.6d));
            circleCenter[1] = (float) (circleCenter[1] - ((Math.sin(Math.toRadians(60.0d)) * d) * 0.6d));
        } else if (-16777214 == i) {
            double d2 = circleRadius;
            circleCenter[0] = (float) (circleCenter[0] + (Math.cos(Math.toRadians(60.0d)) * d2 * 0.6d));
            circleCenter[1] = (float) (circleCenter[1] + (Math.sin(Math.toRadians(60.0d)) * d2 * 0.6d));
        }
        return circleCenter;
    }
}
