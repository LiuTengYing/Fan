package com.android.settings.biometrics.face;

import android.app.Activity;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.util.Log;
import android.view.Surface;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FaceEnrollSidecar extends BiometricEnrollSidecar {
    private final int[] mDisabledFeatures;
    private FaceManager.EnrollmentCallback mEnrollmentCallback = new FaceManager.EnrollmentCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollSidecar.1
        public void onEnrollmentProgress(int i) {
            FaceEnrollSidecar.super.onEnrollmentProgress(i);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            FaceEnrollSidecar.super.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            FaceEnrollSidecar.super.onEnrollmentError(i, charSequence);
        }
    };
    private FaceUpdater mFaceUpdater;
    private Surface mSurfaceTexture;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1509;
    }

    public FaceEnrollSidecar(int[] iArr) {
        this.mDisabledFeatures = Arrays.copyOf(iArr, iArr.length);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar, androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mFaceUpdater = new FaceUpdater(activity);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar
    public void startEnrollment() {
        Log.d("FaceEnrollSidecar", "startEnrollment");
        super.startEnrollment();
        byte[] bArr = this.mToken;
        if (bArr == null) {
            FragmentActivity activity = getActivity();
            Intent intent = new Intent(activity, FaceEnrollIntroduction.class);
            intent.putExtra("reenroll_token", true);
            intent.addFlags(67108864);
            activity.startActivity(intent);
            activity.finish();
        } else if (this.mSurfaceTexture != null) {
            Log.d("FaceEnrollSidecar", "enroll with surface texture");
            this.mFaceUpdater.enroll(this.mUserId, this.mToken, this.mEnrollmentCancel, this.mEnrollmentCallback, this.mDisabledFeatures, this.mSurfaceTexture, false);
        } else {
            this.mFaceUpdater.enroll(this.mUserId, bArr, this.mEnrollmentCancel, this.mEnrollmentCallback, this.mDisabledFeatures);
        }
    }

    public void setSurfaceTexture(Surface surface) {
        this.mSurfaceTexture = surface;
    }
}
