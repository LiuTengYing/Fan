package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class FaceSettingsLivenessModePreferenceController extends FaceSettingsPreferenceController {
    private static final int DEFAULT = 0;
    private static final String FACEID_LIVENESSGRADE_PROP = "persist.vendor.faceid.livenessgrade";
    static final String KEY = "security_settings_face_require_livenessmode";
    private static final int OFF = 0;
    private static final int ON = 1;
    private FaceManager mFaceManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public FaceSettingsLivenessModePreferenceController(Context context) {
        this(context, KEY);
    }

    public FaceSettingsLivenessModePreferenceController(Context context, String str) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_require_livenessmode", 0, getUserId()) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "face_unlock_require_livenessmode", z ? 1 : 0, getUserId());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            preference.setEnabled(false);
        } else if (!this.mFaceManager.hasEnrolledTemplates(getUserId())) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
        }
        updateSummary(preference);
    }

    void updateSummary(Preference preference) {
        if (!(2 == SystemProperties.getInt(FACEID_LIVENESSGRADE_PROP, 0))) {
            preference.setSummary(R$string.security_settings_face_settings_require_livenessmode_details);
        } else {
            preference.setSummary("");
        }
    }
}
