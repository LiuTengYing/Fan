package com.android.settings.biometrics.combination;

import android.content.Context;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.biometrics.ParentalControlsUtils;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class CombinedBiometricStatusUtils {
    private final Context mContext;
    FaceManager mFaceManager;
    FingerprintManager mFingerprintManager;
    private final int mUserId;

    public CombinedBiometricStatusUtils(Context context, int i) {
        this.mContext = context;
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
        this.mUserId = i;
    }

    public boolean isAvailable() {
        return Utils.hasFingerprintHardware(this.mContext) && Utils.hasFaceHardware(this.mContext);
    }

    public boolean hasEnrolled() {
        return hasEnrolledFingerprints() || hasEnrolledFace();
    }

    public RestrictedLockUtils.EnforcedAdmin getDisablingAdmin() {
        RestrictedLockUtils.EnforcedAdmin parentConsentRequired = ParentalControlsUtils.parentConsentRequired(this.mContext, 8);
        RestrictedLockUtils.EnforcedAdmin parentConsentRequired2 = ParentalControlsUtils.parentConsentRequired(this.mContext, 2);
        boolean z = parentConsentRequired != null;
        boolean z2 = parentConsentRequired2 != null;
        if (z && z2) {
            return parentConsentRequired;
        }
        return null;
    }

    public String getSummary() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        int size = fingerprintManager != null ? fingerprintManager.getEnrolledFingerprints(this.mUserId).size() : 0;
        boolean hasEnrolledFace = hasEnrolledFace();
        if (!hasEnrolledFace || size <= 1) {
            if (hasEnrolledFace && size == 1) {
                return this.mContext.getString(R$string.security_settings_biometric_preference_summary_both_fp_single);
            }
            if (hasEnrolledFace) {
                return this.mContext.getString(R$string.security_settings_face_preference_summary);
            }
            if (size > 0) {
                return this.mContext.getResources().getQuantityString(R$plurals.security_settings_fingerprint_preference_summary, size, Integer.valueOf(size));
            }
            return this.mContext.getString(R$string.security_settings_biometric_preference_summary_none_enrolled);
        }
        return this.mContext.getString(R$string.security_settings_biometric_preference_summary_both_fp_multiple);
    }

    private boolean hasEnrolledFingerprints() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        return fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints(this.mUserId);
    }

    private boolean hasEnrolledFace() {
        FaceManager faceManager = this.mFaceManager;
        return faceManager != null && faceManager.hasEnrolledTemplates(this.mUserId);
    }

    public String getSettingsClassName() {
        return Settings.CombinedBiometricSettingsActivity.class.getName();
    }

    public String getProfileSettingsClassName() {
        return Settings.CombinedBiometricProfileSettingsActivity.class.getName();
    }
}
