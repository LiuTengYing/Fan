package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.SmartSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class SmartPickUpPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final Fragment mHost;
    public SmartSwitchPreference mSmartPickUpPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "smart_pick_up";
    }

    public SmartPickUpPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isSmartPickUpAvailable(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            SmartSwitchPreference smartSwitchPreference = (SmartSwitchPreference) preferenceScreen.findPreference("smart_pick_up");
            this.mSmartPickUpPreference = smartSwitchPreference;
            smartSwitchPreference.setOnViewClickedListener(new SmartSwitchPreference.OnViewClickedListener() { // from class: com.unisoc.settings.smartcontrols.SmartPickUpPreferenceController.1
                @Override // com.android.settings.widget.SmartSwitchPreference.OnViewClickedListener
                public void OnViewClicked(View view) {
                    SmartPickUpPreferenceController.this.showSmartPickUpAnimation();
                }
            });
            this.mSmartPickUpPreference.setOnPreferenceSwitchCheckedListener(new SmartSwitchPreference.OnPreferenceSwitchChangeListener() { // from class: com.unisoc.settings.smartcontrols.SmartPickUpPreferenceController.2
                @Override // com.android.settings.widget.SmartSwitchPreference.OnPreferenceSwitchChangeListener
                public void onPreferenceSwitchChanged(boolean z) {
                    Settings.Secure.putInt(((AbstractPreferenceController) SmartPickUpPreferenceController.this).mContext.getContentResolver(), "doze_pulse_on_pick_up", z ? 1 : 0);
                }
            });
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mSmartPickUpPreference.setChecked(isSmartPickUpEnabled(this.mContext));
    }

    public static boolean isSmartPickUpAvailable(Context context) {
        return new AmbientDisplayConfiguration(context).dozePickupSensorAvailable();
    }

    private static boolean isSmartPickUpEnabled(Context context) {
        return new AmbientDisplayConfiguration(context).pickupGestureEnabled(UserHandle.myUserId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSmartPickUpAnimation() {
        FragmentTransaction beginTransaction = this.mHost.getFragmentManager().beginTransaction();
        this.mHost.getFragmentManager().executePendingTransactions();
        Fragment findFragmentByTag = this.mHost.getFragmentManager().findFragmentByTag("smart_pick_up_dialog");
        if (findFragmentByTag != null) {
            if (findFragmentByTag.isAdded()) {
                return;
            }
            beginTransaction.remove(findFragmentByTag);
        }
        SmartPickUpAnimation newInstance = SmartPickUpAnimation.newInstance(this.mSmartPickUpPreference);
        if (newInstance == null || !this.mHost.getActivity().isResumed() || newInstance.isAdded()) {
            return;
        }
        newInstance.show(beginTransaction, "smart_pick_up_dialog");
    }
}
