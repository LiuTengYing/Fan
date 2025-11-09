package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class FaceSettingsLockscreenBypassPreferenceController extends FaceSettingsPreferenceController {
    @VisibleForTesting
    protected FaceManager mFaceManager;
    private UserManager mUserManager;

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsLockscreenBypassPreferenceController(Context context, String str) {
        super(context, str);
        if (context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.mFaceManager = (FaceManager) context.getSystemService(FaceManager.class);
        }
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (FaceSettings.isFaceHardwareDetected(this.mContext) && getRestrictingAdmin() == null) {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_dismisses_keyguard", this.mContext.getResources().getBoolean(17891660) ? 1 : 0, getUserHandle()) != 0;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "face_unlock_dismisses_keyguard", z ? 1 : 0, getUserHandle());
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            preference.setEnabled(false);
            return;
        }
        RestrictedLockUtils.EnforcedAdmin restrictingAdmin = getRestrictingAdmin();
        if (restrictingAdmin != null) {
            ((RestrictedSwitchPreference) preference).setDisabledByAdmin(restrictingAdmin);
        } else if (!this.mFaceManager.hasEnrolledTemplates(getUserId())) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        FaceManager faceManager;
        if (Utils.isMultipleBiometricsSupported(this.mContext) || this.mUserManager.isManagedProfile(getUserId()) || (faceManager = this.mFaceManager) == null || !faceManager.isHardwareDetected()) {
            return 3;
        }
        return this.mFaceManager.hasEnrolledTemplates(getUserId()) ? 0 : 5;
    }

    private int getUserHandle() {
        return UserHandle.of(getUserId()).getIdentifier();
    }
}
