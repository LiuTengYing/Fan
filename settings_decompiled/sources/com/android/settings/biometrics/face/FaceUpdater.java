package com.android.settings.biometrics.face;

import android.content.Context;
import android.hardware.face.Face;
import android.hardware.face.FaceEnrollCell;
import android.hardware.face.FaceManager;
import android.os.CancellationSignal;
import android.view.Surface;
import com.android.settings.Utils;
import com.android.settings.safetycenter.BiometricsSafetySource;
/* loaded from: classes.dex */
public class FaceUpdater {
    private final Context mContext;
    private final FaceManager mFaceManager;

    public FaceUpdater(Context context) {
        this.mContext = context;
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    public FaceUpdater(Context context, FaceManager faceManager) {
        this.mContext = context;
        this.mFaceManager = faceManager;
    }

    public void enroll(int i, byte[] bArr, CancellationSignal cancellationSignal, FaceManager.EnrollmentCallback enrollmentCallback, int[] iArr) {
        this.mFaceManager.enroll(i, bArr, cancellationSignal, new NotifyingEnrollmentCallback(this.mContext, enrollmentCallback), iArr);
    }

    public void enroll(int i, byte[] bArr, CancellationSignal cancellationSignal, FaceManager.EnrollmentCallback enrollmentCallback, int[] iArr, Surface surface, boolean z) {
        this.mFaceManager.enroll(i, bArr, cancellationSignal, new NotifyingEnrollmentCallback(this.mContext, enrollmentCallback), iArr, surface, z);
    }

    public void remove(Face face, int i, FaceManager.RemovalCallback removalCallback) {
        this.mFaceManager.remove(face, i, new NotifyingRemovalCallback(this.mContext, removalCallback));
    }

    /* loaded from: classes.dex */
    private static class NotifyingEnrollmentCallback extends FaceManager.EnrollmentCallback {
        private final FaceManager.EnrollmentCallback mCallback;
        private final Context mContext;

        NotifyingEnrollmentCallback(Context context, FaceManager.EnrollmentCallback enrollmentCallback) {
            this.mContext = context;
            this.mCallback = enrollmentCallback;
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            this.mCallback.onEnrollmentError(i, charSequence);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            this.mCallback.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentFrame(int i, CharSequence charSequence, FaceEnrollCell faceEnrollCell, int i2, float f, float f2, float f3) {
            this.mCallback.onEnrollmentFrame(i, charSequence, faceEnrollCell, i2, f, f2, f3);
        }

        public void onEnrollmentProgress(int i) {
            this.mCallback.onEnrollmentProgress(i);
            if (i == 0) {
                BiometricsSafetySource.onBiometricsChanged(this.mContext);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class NotifyingRemovalCallback extends FaceManager.RemovalCallback {
        private final FaceManager.RemovalCallback mCallback;
        private final Context mContext;

        NotifyingRemovalCallback(Context context, FaceManager.RemovalCallback removalCallback) {
            this.mContext = context;
            this.mCallback = removalCallback;
        }

        public void onRemovalError(Face face, int i, CharSequence charSequence) {
            this.mCallback.onRemovalError(face, i, charSequence);
        }

        public void onRemovalSucceeded(Face face, int i) {
            this.mCallback.onRemovalSucceeded(face, i);
            BiometricsSafetySource.onBiometricsChanged(this.mContext);
        }
    }
}
