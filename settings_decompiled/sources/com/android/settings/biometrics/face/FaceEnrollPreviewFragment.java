package com.android.settings.biometrics.face;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.face.FaceEnrollEnrolling;
import com.android.settings.biometrics.face.ParticleCollection;
import com.android.settings.core.InstrumentedPreferenceFragment;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FaceEnrollPreviewFragment extends InstrumentedPreferenceFragment implements BiometricEnrollSidecar.Listener {
    private static final int HEIGHT;
    private static final int WIDTH;
    private FaceEnrollAnimationDrawable mAnimationDrawable;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private ImageView mCircleView;
    private ParticleCollection.Listener mListener;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Size mPreviewSize;
    private FaceEnrollEnrolling.StartedListener mStartedListener;
    private FaceEnrollEnrolling.StopedListener mStopedListener;
    private FaceSquareTextureView mTextureView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final ParticleCollection.Listener mAnimationListener = new ParticleCollection.Listener() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.1
        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            FaceEnrollPreviewFragment.this.mListener.onEnrolled();
        }
    };
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.2
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            Log.d("FaceEnrollPreviewFragment", "onSurfaceTextureAvailable");
            FaceEnrollPreviewFragment faceEnrollPreviewFragment = FaceEnrollPreviewFragment.this;
            faceEnrollPreviewFragment.configureTransform(faceEnrollPreviewFragment.mTextureView.getWidth(), FaceEnrollPreviewFragment.this.mTextureView.getHeight());
            FaceEnrollPreviewFragment.this.mStartedListener.onEnrollStarted();
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            FaceEnrollPreviewFragment.this.configureTransform(i, i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.d("FaceEnrollPreviewFragment", "onSurfaceTextureDestroyed");
            FaceEnrollPreviewFragment.this.mStopedListener.onEnrollStoped();
            return true;
        }
    };
    private final CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.3
        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onOpened(CameraDevice cameraDevice) {
            FaceEnrollPreviewFragment.this.mCameraDevice = cameraDevice;
            try {
                SurfaceTexture surfaceTexture = FaceEnrollPreviewFragment.this.mTextureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(FaceEnrollPreviewFragment.this.mPreviewSize.getWidth(), FaceEnrollPreviewFragment.this.mPreviewSize.getHeight());
                Surface surface = new Surface(surfaceTexture);
                FaceEnrollPreviewFragment faceEnrollPreviewFragment = FaceEnrollPreviewFragment.this;
                faceEnrollPreviewFragment.mPreviewRequestBuilder = faceEnrollPreviewFragment.mCameraDevice.createCaptureRequest(1);
                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.addTarget(surface);
                FaceEnrollPreviewFragment.this.mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.3.1
                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        if (FaceEnrollPreviewFragment.this.mCameraDevice == null) {
                            return;
                        }
                        FaceEnrollPreviewFragment.this.mCaptureSession = cameraCaptureSession;
                        try {
                            FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 4);
                            FaceEnrollPreviewFragment faceEnrollPreviewFragment2 = FaceEnrollPreviewFragment.this;
                            faceEnrollPreviewFragment2.mPreviewRequest = faceEnrollPreviewFragment2.mPreviewRequestBuilder.build();
                            FaceEnrollPreviewFragment.this.mCaptureSession.setRepeatingRequest(FaceEnrollPreviewFragment.this.mPreviewRequest, null, FaceEnrollPreviewFragment.this.mHandler);
                        } catch (CameraAccessException e) {
                            Log.e("FaceEnrollPreviewFragment", "Unable to access camera", e);
                        }
                    }

                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        Log.e("FaceEnrollPreviewFragment", "Unable to configure camera");
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onError(CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1554;
    }

    static {
        WIDTH = 3 == SystemProperties.getInt("persist.sys.cam.faceid.version", 1) ? 1440 : 960;
        HEIGHT = 3 == SystemProperties.getInt("persist.sys.cam.faceid.version", 1) ? 1080 : 720;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Surface getPreviewSurface() {
        Log.d("FaceEnrollPreviewFragment", "getPreviewSurface begin");
        try {
            SurfaceTexture surfaceTexture = this.mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(WIDTH, HEIGHT);
            return new Surface(surfaceTexture);
        } catch (Exception e) {
            Log.d("FaceEnrollPreviewFragment", "getPreviewSurface Exception", e);
            return null;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        this.mTextureView = (FaceSquareTextureView) activity.findViewById(R$id.texture_view);
        ImageView imageView = (ImageView) activity.findViewById(R$id.circle_view);
        this.mCircleView = imageView;
        if (imageView == null || this.mTextureView == null) {
            activity.finish();
            return;
        }
        imageView.setLayerType(1, null);
        FaceEnrollAnimationDrawable faceEnrollAnimationDrawable = new FaceEnrollAnimationDrawable(getContext(), this.mAnimationListener);
        this.mAnimationDrawable = faceEnrollAnimationDrawable;
        this.mCircleView.setImageDrawable(faceEnrollAnimationDrawable);
        this.mCameraManager = (CameraManager) getContext().getSystemService("camera");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FaceSquareTextureView faceSquareTextureView = this.mTextureView;
        if (faceSquareTextureView == null) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        } else if (faceSquareTextureView.isAvailable()) {
            this.mStartedListener.onEnrollStarted();
        } else {
            this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentError(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mAnimationDrawable.onEnrollmentProgressChange(i, i2);
    }

    public void setListener(ParticleCollection.Listener listener) {
        this.mListener = listener;
    }

    public void setStartedListener(FaceEnrollEnrolling.StartedListener startedListener) {
        this.mStartedListener = startedListener;
    }

    public void setStopedListener(FaceEnrollEnrolling.StopedListener stopedListener) {
        this.mStopedListener = stopedListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configureTransform(int i, int i2) {
        if (this.mTextureView == null) {
            return;
        }
        float f = i / WIDTH;
        float f2 = i2 / HEIGHT;
        float min = Math.min(f, f2);
        float f3 = f / min;
        float f4 = f2 / min;
        TypedValue typedValue = new TypedValue();
        TypedValue typedValue2 = new TypedValue();
        TypedValue typedValue3 = new TypedValue();
        getResources().getValue(R$dimen.face_preview_translate_x, typedValue, true);
        getResources().getValue(R$dimen.face_preview_translate_y, typedValue2, true);
        getResources().getValue(R$dimen.face_preview_scale, typedValue3, true);
        Matrix matrix = new Matrix();
        this.mTextureView.getTransform(matrix);
        matrix.setScale(f3 * typedValue3.getFloat(), f4 * typedValue3.getFloat());
        matrix.postTranslate(typedValue.getFloat(), typedValue2.getFloat());
        this.mTextureView.setTransform(matrix);
    }

    private void closeCamera() {
        CameraCaptureSession cameraCaptureSession = this.mCaptureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.mCaptureSession = null;
        }
        CameraDevice cameraDevice = this.mCameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.mCameraDevice = null;
        }
    }
}
