package com.android.settings.safetycenter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.safetycenter.SafetyEvent;
import android.safetycenter.SafetySourceData;
import android.safetycenter.SafetySourceStatus;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricNavigationUtils;
import com.android.settings.biometrics.combination.CombinedBiometricStatusUtils;
import com.android.settings.biometrics.face.FaceStatusUtils;
import com.android.settings.biometrics.fingerprint.FingerprintStatusUtils;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public final class BiometricsSafetySource {
    public static void setSafetySourceData(Context context, SafetyEvent safetyEvent) {
        if (SafetyCenterManagerWrapper.get().isEnabled(context)) {
            int myUserId = UserHandle.myUserId();
            BiometricNavigationUtils biometricNavigationUtils = new BiometricNavigationUtils(myUserId);
            CombinedBiometricStatusUtils combinedBiometricStatusUtils = new CombinedBiometricStatusUtils(context, myUserId);
            if (combinedBiometricStatusUtils.isAvailable()) {
                RestrictedLockUtils.EnforcedAdmin disablingAdmin = combinedBiometricStatusUtils.getDisablingAdmin();
                setBiometricSafetySourceData(context, context.getString(R$string.security_settings_biometric_preference_title), combinedBiometricStatusUtils.getSummary(), biometricNavigationUtils.getBiometricSettingsIntent(context, combinedBiometricStatusUtils.getSettingsClassName(), disablingAdmin, Bundle.EMPTY), disablingAdmin == null, combinedBiometricStatusUtils.hasEnrolled(), safetyEvent);
                return;
            }
            FaceStatusUtils faceStatusUtils = new FaceStatusUtils(context, Utils.getFaceManagerOrNull(context), myUserId);
            if (faceStatusUtils.isAvailable()) {
                RestrictedLockUtils.EnforcedAdmin disablingAdmin2 = faceStatusUtils.getDisablingAdmin();
                setBiometricSafetySourceData(context, context.getString(R$string.security_settings_face_preference_title), faceStatusUtils.getSummary(), biometricNavigationUtils.getBiometricSettingsIntent(context, faceStatusUtils.getSettingsClassName(), disablingAdmin2, Bundle.EMPTY), disablingAdmin2 == null, faceStatusUtils.hasEnrolled(), safetyEvent);
                return;
            }
            FingerprintStatusUtils fingerprintStatusUtils = new FingerprintStatusUtils(context, Utils.getFingerprintManagerOrNull(context), myUserId);
            if (fingerprintStatusUtils.isAvailable()) {
                RestrictedLockUtils.EnforcedAdmin disablingAdmin3 = fingerprintStatusUtils.getDisablingAdmin();
                setBiometricSafetySourceData(context, context.getString(R$string.security_settings_fingerprint_preference_title), fingerprintStatusUtils.getSummary(), biometricNavigationUtils.getBiometricSettingsIntent(context, fingerprintStatusUtils.getSettingsClassName(), disablingAdmin3, Bundle.EMPTY), disablingAdmin3 == null, fingerprintStatusUtils.hasEnrolled(), safetyEvent);
            }
        }
    }

    public static void onBiometricsChanged(Context context) {
        setSafetySourceData(context, new SafetyEvent.Builder(100).build());
    }

    private static void setBiometricSafetySourceData(Context context, String str, String str2, Intent intent, boolean z, boolean z2, SafetyEvent safetyEvent) {
        SafetyCenterManagerWrapper.get().setSafetySourceData(context, "AndroidBiometrics", new SafetySourceData.Builder().setStatus(new SafetySourceStatus.Builder(str, str2, (z && z2) ? 200 : 100).setPendingIntent(createPendingIntent(context, intent)).setEnabled(z).build()).build(), safetyEvent);
    }

    private static PendingIntent createPendingIntent(Context context, Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, 67108864);
    }
}
