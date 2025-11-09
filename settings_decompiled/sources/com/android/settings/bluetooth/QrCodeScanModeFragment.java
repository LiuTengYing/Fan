package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;
import com.android.settings.core.InstrumentedFragment;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import com.android.settingslib.qrcode.QrCamera;
/* loaded from: classes.dex */
public class QrCodeScanModeFragment extends InstrumentedFragment implements TextureView.SurfaceTextureListener, QrCamera.ScannerCallback {
    private String mBroadcastMetadata;
    private QrCamera mCamera;
    private Context mContext;
    private QrCodeScanModeController mController;
    private int mCornerRadius;
    private TextView mErrorMessage;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.bluetooth.QrCodeScanModeFragment.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                QrCodeScanModeFragment.this.mErrorMessage.setVisibility(4);
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                QrCodeScanModeFragment.this.mController.addSource(QrCodeScanModeFragment.this.mSink, QrCodeScanModeFragment.this.mBroadcastMetadata, QrCodeScanModeFragment.this.mIsGroupOp);
                QrCodeScanModeFragment.this.updateSummary();
                QrCodeScanModeFragment.this.mSummary.sendAccessibilityEvent(32);
            } else {
                QrCodeScanModeFragment.this.mErrorMessage.setVisibility(0);
                QrCodeScanModeFragment.this.mErrorMessage.setText((String) message.obj);
                QrCodeScanModeFragment.this.mErrorMessage.sendAccessibilityEvent(32);
                removeMessages(1);
                sendEmptyMessageDelayed(1, 10000L);
            }
        }
    };
    private boolean mIsGroupOp;
    private BluetoothDevice mSink;
    private TextView mSummary;
    private TextureView mTextureView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1926;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public QrCodeScanModeFragment(boolean z, BluetoothDevice bluetoothDevice) {
        this.mIsGroupOp = z;
        this.mSink = bluetoothDevice;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mContext = context;
        this.mController = new QrCodeScanModeController(context);
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R$layout.qrcode_scanner_fragment, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mTextureView = (TextureView) view.findViewById(R$id.preview_view);
        this.mCornerRadius = this.mContext.getResources().getDimensionPixelSize(R$dimen.qrcode_preview_radius);
        this.mTextureView.setSurfaceTextureListener(this);
        this.mTextureView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.bluetooth.QrCodeScanModeFragment.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), QrCodeScanModeFragment.this.mCornerRadius);
            }
        });
        this.mTextureView.setClipToOutline(true);
        this.mErrorMessage = (TextView) view.findViewById(R$id.error_message);
    }

    private void initCamera(SurfaceTexture surfaceTexture) {
        if (this.mCamera == null) {
            QrCamera qrCamera = new QrCamera(this.mContext, this);
            this.mCamera = qrCamera;
            qrCamera.start(surfaceTexture);
        }
    }

    private void destroyCamera() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
            this.mCamera = null;
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera(surfaceTexture);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        destroyCamera();
        return true;
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public void handleSuccessfulResult(String str) {
        Log.d("QrCodeScanModeFragment", "handleSuccessfulResult(), get the qr code string.");
        this.mBroadcastMetadata = str;
        handleBtLeAudioScanner();
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public void handleCameraFailure() {
        destroyCamera();
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public Size getViewSize() {
        return new Size(this.mTextureView.getWidth(), this.mTextureView.getHeight());
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public Rect getFramePosition(Size size, int i) {
        return new Rect(0, 0, size.getHeight(), size.getHeight());
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public void setTransform(Matrix matrix) {
        this.mTextureView.setTransform(matrix);
    }

    @Override // com.android.settingslib.qrcode.QrCamera.ScannerCallback
    public boolean isValid(String str) {
        if (str.startsWith("BT:")) {
            return true;
        }
        showErrorMessage(R$string.bt_le_audio_qr_code_is_not_valid_format);
        return false;
    }

    private void showErrorMessage(int i) {
        this.mHandler.obtainMessage(2, getString(i)).sendToTarget();
    }

    private void handleBtLeAudioScanner() {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3), 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSummary() {
        this.mSummary.setText(getString(R$string.bt_le_audio_scan_qr_code_scanner, null));
    }
}
