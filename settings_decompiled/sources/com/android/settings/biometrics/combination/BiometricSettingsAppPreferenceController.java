package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import com.android.settings.Utils;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* loaded from: classes.dex */
public class BiometricSettingsAppPreferenceController extends TogglePreferenceController {
    private static final int DEFAULT = 1;
    private static final int OFF = 0;
    private static final int ON = 1;
    private FaceManager mFaceManager;
    private FingerprintManager mFingerprintManager;
    private int mUserId;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public final boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BiometricSettingsAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
    }

    protected RestrictedLockUtils.EnforcedAdmin getRestrictingAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this.mContext, 416, this.mUserId);
    }

    public void setUserId(int i) {
        this.mUserId = i;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "biometric_app_enabled", 1, this.mUserId) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "biometric_app_enabled", z ? 1 : 0, this.mUserId);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (Utils.isMultipleBiometricsSupported(this.mContext)) {
            FaceManager faceManager = this.mFaceManager;
            if (faceManager == null || this.mFingerprintManager == null) {
                return 1;
            }
            return (faceManager.hasEnrolledTemplates(this.mUserId) || this.mFingerprintManager.hasEnrolledTemplates(this.mUserId)) ? 0 : 1;
        }
        return 3;
    }
}
