package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class DesktopModePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin, RebootConfirmationDialogHost {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;
    private final DevelopmentSettingsDashboardFragment mFragment;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "force_desktop_mode_on_external_displays";
    }

    public DesktopModePreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "force_desktop_mode_on_external_displays", booleanValue ? 1 : 0);
        if (booleanValue) {
            RebootConfirmationDialogFragment.show(this.mFragment, R$string.reboot_dialog_force_desktop_mode, this);
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getInt(this.mContext.getContentResolver(), "force_desktop_mode_on_external_displays", 0) != 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "force_desktop_mode_on_external_displays", 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // com.android.settings.development.RebootConfirmationDialogHost
    public void onRebootConfirmed() {
        this.mContext.startActivity(new Intent("android.intent.action.REBOOT"));
    }

    String getBuildType() {
        return Build.TYPE;
    }
}
