package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class BiometricLockscreenBypassPreferenceController extends FaceSettingsLockscreenBypassPreferenceController {
    @Override // com.android.settings.biometrics.face.FaceSettingsLockscreenBypassPreferenceController, com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsLockscreenBypassPreferenceController, com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsLockscreenBypassPreferenceController, com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsLockscreenBypassPreferenceController, com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BiometricLockscreenBypassPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsLockscreenBypassPreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Utils.isMultipleBiometricsSupported(this.mContext) ? 0 : 3;
    }
}
