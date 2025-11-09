package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import android.view.CrossWindowBlurListeners;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public final class EnableBlursPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final boolean mBlurSupported;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_blurs_on_windows";
    }

    public EnableBlursPreferenceController(Context context) {
        this(context, CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED);
    }

    public EnableBlursPreferenceController(Context context, boolean z) {
        super(context);
        this.mBlurSupported = z;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "disable_window_blurs", !((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mBlurSupported;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getInt(this.mContext.getContentResolver(), "disable_window_blurs", 0) == 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "disable_window_blurs", 0);
        updateState(null);
    }
}
