package com.android.settings.biometrics;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import androidx.fragment.app.FragmentActivity;
/* loaded from: classes.dex */
public class MultiBiometricEnrollHelper {
    private final FragmentActivity mActivity;
    private final long mGkPwHandle;
    private final boolean mRequestEnrollFace;
    private final boolean mRequestEnrollFingerprint;
    private final int mUserId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MultiBiometricEnrollHelper(FragmentActivity fragmentActivity, int i, boolean z, boolean z2, long j) {
        this.mActivity = fragmentActivity;
        this.mUserId = i;
        this.mGkPwHandle = j;
        this.mRequestEnrollFace = z;
        this.mRequestEnrollFingerprint = z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startNextStep() {
        if (this.mRequestEnrollFace) {
            launchFaceEnroll();
        } else if (this.mRequestEnrollFingerprint) {
            launchFingerprintEnroll();
        } else {
            this.mActivity.setResult(2);
            this.mActivity.finish();
        }
    }

    private void launchFaceEnroll() {
        ((FaceManager) this.mActivity.getSystemService(FaceManager.class)).generateChallenge(this.mUserId, new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.MultiBiometricEnrollHelper$$ExternalSyntheticLambda1
            public final void onGenerateChallengeResult(int i, int i2, long j) {
                MultiBiometricEnrollHelper.this.lambda$launchFaceEnroll$0(i, i2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchFaceEnroll$0(int i, int i2, long j) {
        byte[] requestGatekeeperHat = BiometricUtils.requestGatekeeperHat(this.mActivity, this.mGkPwHandle, this.mUserId, j);
        FragmentActivity fragmentActivity = this.mActivity;
        Intent faceIntroIntent = BiometricUtils.getFaceIntroIntent(fragmentActivity, fragmentActivity.getIntent());
        faceIntroIntent.putExtra("sensor_id", i);
        faceIntroIntent.putExtra("challenge", j);
        if (this.mRequestEnrollFingerprint) {
            FragmentActivity fragmentActivity2 = this.mActivity;
            Intent fingerprintIntroIntent = BiometricUtils.getFingerprintIntroIntent(fragmentActivity2, fragmentActivity2.getIntent());
            fingerprintIntroIntent.putExtra("gk_pw_handle", this.mGkPwHandle);
            faceIntroIntent.putExtra("enroll_after_face", PendingIntent.getActivity(this.mActivity, 0, fingerprintIntroIntent, 201326592));
        }
        BiometricUtils.launchEnrollForResult(this.mActivity, faceIntroIntent, 3000, requestGatekeeperHat, Long.valueOf(this.mGkPwHandle), this.mUserId);
    }

    private void launchFingerprintEnroll() {
        ((FingerprintManager) this.mActivity.getSystemService(FingerprintManager.class)).generateChallenge(this.mUserId, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.MultiBiometricEnrollHelper$$ExternalSyntheticLambda0
            public final void onChallengeGenerated(int i, int i2, long j) {
                MultiBiometricEnrollHelper.this.lambda$launchFingerprintEnroll$1(i, i2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchFingerprintEnroll$1(int i, int i2, long j) {
        byte[] requestGatekeeperHat = BiometricUtils.requestGatekeeperHat(this.mActivity, this.mGkPwHandle, this.mUserId, j);
        FragmentActivity fragmentActivity = this.mActivity;
        Intent fingerprintIntroIntent = BiometricUtils.getFingerprintIntroIntent(fragmentActivity, fragmentActivity.getIntent());
        fingerprintIntroIntent.putExtra("sensor_id", i);
        fingerprintIntroIntent.putExtra("challenge", j);
        BiometricUtils.launchEnrollForResult(this.mActivity, fingerprintIntroIntent, 3001, requestGatekeeperHat, Long.valueOf(this.mGkPwHandle), this.mUserId);
    }
}
