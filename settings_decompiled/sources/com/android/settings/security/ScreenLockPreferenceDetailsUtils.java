package com.android.settings.security;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.security.screenlock.ScreenLockSettings;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class ScreenLockPreferenceDetailsUtils {
    private final Context mContext;
    private final LockPatternUtils mLockPatternUtils;
    private final int mProfileChallengeUserId;
    private final UserManager mUm;
    private final int mUserId;

    public ScreenLockPreferenceDetailsUtils(Context context) {
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        this.mContext = context;
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        this.mUm = userManager;
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, myUserId);
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_unlock_set_or_change);
    }

    public String getSummary(int i) {
        Integer summaryResId = getSummaryResId(i);
        if (summaryResId != null) {
            return this.mContext.getResources().getString(summaryResId.intValue());
        }
        return null;
    }

    public boolean isPasswordQualityManaged(int i, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        return enforcedAdmin != null && ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getPasswordQuality(enforcedAdmin.component, i) == 524288;
    }

    public boolean isLockPatternSecure() {
        return this.mLockPatternUtils.isSecure(this.mUserId);
    }

    public boolean shouldShowGearMenu() {
        return isLockPatternSecure();
    }

    public void openScreenLockSettings(int i) {
        this.mContext.startActivity(getLaunchScreenLockSettingsIntent(i));
    }

    public Intent getLaunchScreenLockSettingsIntent(int i) {
        return new SubSettingLauncher(this.mContext).setDestination(ScreenLockSettings.class.getName()).setSourceMetricsCategory(i).toIntent();
    }

    public boolean openChooseLockGenericFragment(int i) {
        Intent quietModeDialogIntent = getQuietModeDialogIntent();
        if (quietModeDialogIntent != null) {
            this.mContext.startActivity(quietModeDialogIntent);
            return false;
        }
        this.mContext.startActivity(getChooseLockGenericFragmentIntent(i));
        return true;
    }

    public Intent getLaunchChooseLockGenericFragmentIntent(int i) {
        Intent quietModeDialogIntent = getQuietModeDialogIntent();
        return quietModeDialogIntent != null ? quietModeDialogIntent : getChooseLockGenericFragmentIntent(i);
    }

    private Intent getQuietModeDialogIntent() {
        int i = this.mProfileChallengeUserId;
        if (i == -10000 || this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i) || !StorageManager.isFileEncryptedNativeOnly() || !this.mUm.isQuietModeEnabled(UserHandle.of(this.mProfileChallengeUserId))) {
            return null;
        }
        return UnlaunchableAppActivity.createInQuietModeDialogIntent(this.mProfileChallengeUserId);
    }

    private Intent getChooseLockGenericFragmentIntent(int i) {
        return new SubSettingLauncher(this.mContext).setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName()).setSourceMetricsCategory(i).setTransitionType(1).toIntent();
    }

    private Integer getSummaryResId(int i) {
        if (!this.mLockPatternUtils.isSecure(i)) {
            if (i == this.mProfileChallengeUserId || this.mLockPatternUtils.isLockScreenDisabled(i)) {
                return Integer.valueOf(R$string.unlock_set_unlock_mode_off);
            }
            return Integer.valueOf(R$string.unlock_set_unlock_mode_none);
        }
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i);
        if (keyguardStoredPasswordQuality != 65536) {
            if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                return Integer.valueOf(R$string.unlock_set_unlock_mode_pin);
            }
            if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                return Integer.valueOf(R$string.unlock_set_unlock_mode_password);
            }
            return null;
        }
        return Integer.valueOf(R$string.unlock_set_unlock_mode_pattern);
    }
}
