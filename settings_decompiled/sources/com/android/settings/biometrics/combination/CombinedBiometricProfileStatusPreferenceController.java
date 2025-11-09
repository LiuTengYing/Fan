package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class CombinedBiometricProfileStatusPreferenceController extends CombinedBiometricStatusPreferenceController {
    private static final String KEY_BIOMETRIC_SETTINGS = "biometric_settings_profile";

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CombinedBiometricProfileStatusPreferenceController(Context context) {
        super(context, KEY_BIOMETRIC_SETTINGS);
    }

    public CombinedBiometricProfileStatusPreferenceController(Context context, String str) {
        super(context, str);
    }

    public CombinedBiometricProfileStatusPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_BIOMETRIC_SETTINGS, lifecycle);
    }

    public CombinedBiometricProfileStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected boolean isUserSupported() {
        int i = this.mProfileChallengeUserId;
        return i != -10000 && this.mUm.isManagedProfile(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public int getUserId() {
        return this.mProfileChallengeUserId;
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSettingsClassName() {
        return this.mCombinedBiometricStatusUtils.getProfileSettingsClassName();
    }
}
