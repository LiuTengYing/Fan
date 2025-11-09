package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.face.FaceManager;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.biometrics.ParentalControlsUtils;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class FaceStatusUtils {
    private final Context mContext;
    private final FaceManager mFaceManager;
    private final int mUserId;

    public FaceStatusUtils(Context context, FaceManager faceManager, int i) {
        this.mContext = context;
        this.mFaceManager = faceManager;
        this.mUserId = i;
    }

    public boolean isAvailable() {
        return !Utils.isMultipleBiometricsSupported(this.mContext) && Utils.hasFaceHardware(this.mContext);
    }

    public RestrictedLockUtils.EnforcedAdmin getDisablingAdmin() {
        return ParentalControlsUtils.parentConsentRequired(this.mContext, 8);
    }

    public String getSummary() {
        int i;
        Resources resources = this.mContext.getResources();
        if (hasEnrolled()) {
            i = R$string.security_settings_face_preference_summary;
        } else {
            i = R$string.security_settings_face_preference_summary_none;
        }
        return resources.getString(i);
    }

    public String getSettingsClassName() {
        return hasEnrolled() ? Settings.FaceSettingsInternalActivity.class.getName() : FaceEnrollIntroductionInternal.class.getName();
    }

    public boolean hasEnrolled() {
        return this.mFaceManager.hasEnrolledTemplates(this.mUserId);
    }
}
