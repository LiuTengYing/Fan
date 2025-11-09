package com.unisoc.settings.smartcontrols;

import android.content.Context;
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
public class MuteAlarmsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final Fragment mHost;
    public SmartSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mute_alarms";
    }

    public MuteAlarmsPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isMuteAlarmsAvailable(this.mContext) && Utils.isAppInstalled(this.mContext, "com.android.deskclock");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            SmartSwitchPreference smartSwitchPreference = (SmartSwitchPreference) preferenceScreen.findPreference("mute_alarms");
            this.mPreference = smartSwitchPreference;
            smartSwitchPreference.setOnViewClickedListener(new SmartSwitchPreference.OnViewClickedListener() { // from class: com.unisoc.settings.smartcontrols.MuteAlarmsPreferenceController.1
                @Override // com.android.settings.widget.SmartSwitchPreference.OnViewClickedListener
                public void OnViewClicked(View view) {
                    MuteAlarmsPreferenceController.this.showMuteAlarmsAnimation();
                }
            });
            this.mPreference.setOnPreferenceSwitchCheckedListener(new SmartSwitchPreference.OnPreferenceSwitchChangeListener() { // from class: com.unisoc.settings.smartcontrols.MuteAlarmsPreferenceController.2
                @Override // com.android.settings.widget.SmartSwitchPreference.OnPreferenceSwitchChangeListener
                public void onPreferenceSwitchChanged(boolean z) {
                    if (SmartMotionFragment.isSmartMotionEnabled(((AbstractPreferenceController) MuteAlarmsPreferenceController.this).mContext)) {
                        Settings.Global.putInt(((AbstractPreferenceController) MuteAlarmsPreferenceController.this).mContext.getContentResolver(), "mute_alarms", z ? 1 : 0);
                    }
                }
            });
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        if (SmartMotionFragment.isSmartMotionEnabled(this.mContext)) {
            i = Settings.Global.getInt(this.mContext.getContentResolver(), "mute_alarms", 0);
        } else {
            i = Settings.Global.getInt(this.mContext.getContentResolver(), "mute_alarms_switch", 0);
        }
        if (preference == null || !(preference instanceof SmartSwitchPreference)) {
            return;
        }
        ((SmartSwitchPreference) preference).setChecked(i == 1);
    }

    public static boolean isMuteAlarmsAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_muteAlarms) && Utils.isSupportSensor(context, 65546) && Utils.isSupportSensor(context, 65546);
    }

    public void updateOnSmartMotionChange(boolean z) {
        if (!z) {
            SmartSwitchPreference smartSwitchPreference = this.mPreference;
            if (smartSwitchPreference != null && smartSwitchPreference.isChecked()) {
                Settings.Global.putInt(this.mContext.getContentResolver(), "mute_alarms_switch", 1);
            }
            Settings.Global.putInt(this.mContext.getContentResolver(), "mute_alarms", 0);
        } else if (Settings.Global.getInt(this.mContext.getContentResolver(), "mute_alarms_switch", 0) == 1) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "mute_alarms", 1);
            Settings.Global.putInt(this.mContext.getContentResolver(), "mute_alarms_switch", 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMuteAlarmsAnimation() {
        FragmentTransaction beginTransaction = this.mHost.getFragmentManager().beginTransaction();
        this.mHost.getFragmentManager().executePendingTransactions();
        Fragment findFragmentByTag = this.mHost.getFragmentManager().findFragmentByTag("mute_alarms_dialog");
        if (findFragmentByTag != null) {
            if (findFragmentByTag.isAdded()) {
                return;
            }
            beginTransaction.remove(findFragmentByTag);
        }
        MuteAlarmsAnimation newInstance = MuteAlarmsAnimation.newInstance(this.mPreference);
        if (newInstance == null || !this.mHost.getActivity().isResumed() || newInstance.isAdded()) {
            return;
        }
        newInstance.show(beginTransaction, "mute_alarms_dialog");
    }
}
