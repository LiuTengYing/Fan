package com.android.settings.security;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockGeneric;
/* loaded from: classes.dex */
public class ChangeProfileScreenLockPreferenceController extends ChangeScreenLockPreferenceController {
    private final String mPreferenceKey;

    public ChangeProfileScreenLockPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment) {
        this(context, settingsPreferenceFragment, "unlock_set_or_change_profile");
    }

    public ChangeProfileScreenLockPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String str) {
        super(context, settingsPreferenceFragment);
        this.mPreferenceKey = str;
    }

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        int keyguardStoredPasswordQuality;
        int i = this.mProfileChallengeUserId;
        if (i == -10000 || !this.mUm.isManagedProfile(i)) {
            return false;
        }
        return !this.mLockPatternUtils.isSecure(this.mProfileChallengeUserId) || (keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId)) == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288;
    }

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey()) && !Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
            new SubSettingLauncher(this.mContext).setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName()).setSourceMetricsCategory(this.mHost.getMetricsCategory()).setArguments(bundle).setTransitionType(1).launch();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.security.ChangeScreenLockPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSummary(preference, this.mProfileChallengeUserId);
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
            this.mPreference.setSummary(this.mContext.getString(R$string.lock_settings_profile_unified_summary));
            this.mPreference.setEnabled(false);
            return;
        }
        disableIfPasswordQualityManaged(this.mProfileChallengeUserId);
    }
}
