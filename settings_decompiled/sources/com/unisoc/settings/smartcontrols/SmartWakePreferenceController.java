package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.SmartSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class SmartWakePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final Fragment mHost;
    public SmartSwitchPreference mSmartWakePreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "smart_wake";
    }

    public SmartWakePreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isSmartWakeAvailable(this.mContext) && isWakeGestureAvailable(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            SmartSwitchPreference smartSwitchPreference = (SmartSwitchPreference) preferenceScreen.findPreference("smart_wake");
            this.mSmartWakePreference = smartSwitchPreference;
            smartSwitchPreference.setOnViewClickedListener(new SmartSwitchPreference.OnViewClickedListener() { // from class: com.unisoc.settings.smartcontrols.SmartWakePreferenceController.1
                @Override // com.android.settings.widget.SmartSwitchPreference.OnViewClickedListener
                public void OnViewClicked(View view) {
                    SmartWakePreferenceController.this.showSmartWakeAnimation();
                }
            });
            this.mSmartWakePreference.setOnPreferenceSwitchCheckedListener(new SmartSwitchPreference.OnPreferenceSwitchChangeListener() { // from class: com.unisoc.settings.smartcontrols.SmartWakePreferenceController.2
                @Override // com.android.settings.widget.SmartSwitchPreference.OnPreferenceSwitchChangeListener
                public void onPreferenceSwitchChanged(boolean z) {
                    Settings.Secure.putInt(((AbstractPreferenceController) SmartWakePreferenceController.this).mContext.getContentResolver(), "wake_gesture_enabled", z ? 1 : 0);
                }
            });
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mSmartWakePreference.setChecked(Settings.Secure.getInt(this.mContext.getContentResolver(), "wake_gesture_enabled", 0) == 1);
    }

    public static boolean isSmartWakeAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_smartWake);
    }

    public static boolean isWakeGestureAvailable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        return (sensorManager == null || sensorManager.getDefaultSensor(23) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSmartWakeAnimation() {
        FragmentTransaction beginTransaction = this.mHost.getFragmentManager().beginTransaction();
        this.mHost.getFragmentManager().executePendingTransactions();
        Fragment findFragmentByTag = this.mHost.getFragmentManager().findFragmentByTag("smart_wake_dialog");
        if (findFragmentByTag != null) {
            if (findFragmentByTag.isAdded()) {
                return;
            }
            beginTransaction.remove(findFragmentByTag);
        }
        SmartWakeAnimation newInstance = SmartWakeAnimation.newInstance(this.mSmartWakePreference);
        if (newInstance == null || !this.mHost.getActivity().isResumed() || newInstance.isAdded()) {
            return;
        }
        newInstance.show(beginTransaction, "smart_wake_dialog");
    }
}
