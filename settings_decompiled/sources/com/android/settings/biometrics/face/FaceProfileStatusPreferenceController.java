package com.android.settings.biometrics.face;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import com.android.settings.R$string;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceProfileStatusPreferenceController extends FaceStatusPreferenceController {
    private static final String KEY_FACE_SETTINGS = "face_settings_profile";
    private final DevicePolicyManager mDevicePolicyManager;

    private boolean isAtLeastStrength(int i, int i2) {
        int i3 = i & 32767;
        if (((~i2) & i3) != 0) {
            return false;
        }
        for (int i4 = 1; i4 <= i2; i4 = (i4 << 1) | 1) {
            if (i4 == i3) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceProfileStatusPreferenceController(Context context) {
        super(context, KEY_FACE_SETTINGS);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    public FaceProfileStatusPreferenceController(Context context, String str) {
        super(context, str);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    public FaceProfileStatusPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_FACE_SETTINGS, lifecycle);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    public FaceProfileStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        int availabilityStatus = super.getAvailabilityStatus();
        if (canAuthenticate(8)) {
            if (availabilityStatus != 0) {
                return availabilityStatus;
            }
            return 1;
        }
        return 3;
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

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setTitle(this.mDevicePolicyManager.getResources().getString("Settings.FACE_SETTINGS_FOR_WORK_TITLE", new Supplier() { // from class: com.android.settings.biometrics.face.FaceProfileStatusPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$updateState$0;
                lambda$updateState$0 = FaceProfileStatusPreferenceController.this.lambda$updateState$0();
                return lambda$updateState$0;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() {
        return this.mContext.getResources().getString(R$string.security_settings_face_profile_preference_title);
    }

    private boolean canAuthenticate(int i) {
        for (String str : this.mContext.getResources().getStringArray(17236004)) {
            String[] split = str.split(":");
            if (Integer.parseInt(split[1]) == i) {
                return isAtLeastStrength(Integer.parseInt(split[2]), 255);
            }
        }
        return false;
    }
}
