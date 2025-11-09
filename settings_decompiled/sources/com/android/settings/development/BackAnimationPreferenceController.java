package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.Objects;
/* loaded from: classes.dex */
public class BackAnimationPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final DevelopmentSettingsDashboardFragment mFragment;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "back_navigation_animation";
    }

    @VisibleForTesting
    BackAnimationPreferenceController(Context context) {
        super(context);
        this.mFragment = null;
    }

    public BackAnimationPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        Objects.requireNonNull(developmentSettingsDashboardFragment);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "enable_back_animation", booleanValue ? 1 : 0);
        DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment = this.mFragment;
        if (developmentSettingsDashboardFragment == null || !booleanValue) {
            return true;
        }
        BackAnimationPreferenceDialog.show(developmentSettingsDashboardFragment);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getInt(this.mContext.getContentResolver(), "enable_back_animation", 0) != 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "enable_back_animation", 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }
}
