package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import com.android.settings.Utils;
import com.android.settings.safetycenter.BiometricsSafetySource;
/* loaded from: classes.dex */
public class FingerprintUpdater {
    private final Context mContext;
    private final FingerprintManager mFingerprintManager;

    public FingerprintUpdater(Context context) {
        this.mContext = context;
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
    }

    public FingerprintUpdater(Context context, FingerprintManager fingerprintManager) {
        this.mContext = context;
        this.mFingerprintManager = fingerprintManager;
    }

    public void enroll(byte[] bArr, CancellationSignal cancellationSignal, int i, FingerprintManager.EnrollmentCallback enrollmentCallback, int i2) {
        this.mFingerprintManager.enroll(bArr, cancellationSignal, i, new NotifyingEnrollmentCallback(this.mContext, enrollmentCallback), i2);
    }

    public void remove(Fingerprint fingerprint, int i, FingerprintManager.RemovalCallback removalCallback) {
        this.mFingerprintManager.remove(fingerprint, i, new NotifyingRemovalCallback(this.mContext, removalCallback));
    }

    /* loaded from: classes.dex */
    private static class NotifyingEnrollmentCallback extends FingerprintManager.EnrollmentCallback {
        private final FingerprintManager.EnrollmentCallback mCallback;
        private final Context mContext;

        NotifyingEnrollmentCallback(Context context, FingerprintManager.EnrollmentCallback enrollmentCallback) {
            this.mContext = context;
            this.mCallback = enrollmentCallback;
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            this.mCallback.onEnrollmentError(i, charSequence);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            this.mCallback.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentProgress(int i) {
            this.mCallback.onEnrollmentProgress(i);
            if (i == 0) {
                BiometricsSafetySource.onBiometricsChanged(this.mContext);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class NotifyingRemovalCallback extends FingerprintManager.RemovalCallback {
        private final FingerprintManager.RemovalCallback mCallback;
        private final Context mContext;

        NotifyingRemovalCallback(Context context, FingerprintManager.RemovalCallback removalCallback) {
            this.mContext = context;
            this.mCallback = removalCallback;
        }

        public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
            this.mCallback.onRemovalError(fingerprint, i, charSequence);
        }

        public void onRemovalSucceeded(Fingerprint fingerprint, int i) {
            this.mCallback.onRemovalSucceeded(fingerprint, i);
            BiometricsSafetySource.onBiometricsChanged(this.mContext);
        }
    }
}
