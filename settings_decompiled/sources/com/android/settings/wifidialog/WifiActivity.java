package com.android.settings.wifidialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.wifidialog.WifiReceiver;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class WifiActivity extends Activity {
    private static String TAG = "WifiDialogActivity";
    WifiAdapter adapter;
    private int dialogX;
    private LinearLayout mContentView;
    private TextView mMore;
    private LinearLayout mRootView;
    private ImageView mScanBtn;
    private Button mSwitch;
    private LinearLayout mTitleLy;
    private LinearLayout mView;
    private LinearLayout mWifiConnectedRoot;
    private TextView mWifiName;
    private WifiManager manager;
    private WifiPasswordDialog passwordDialog;
    private ListView wifiListView;
    private List<ScanResult> scanResults = new ArrayList();
    private boolean onSwitchChanged = false;
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.wifidialog.WifiActivity.13
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 9999 && WifiActivity.this.mHandler != null && WifiActivity.this.mHandler.hasMessages(9999)) {
                WifiActivity.this.mHandler.removeMessages(9999);
                WifiActivity.this.mHandler.sendMessageDelayed(WifiActivity.this.getMeaasge(9999), 20000L);
                WifiActivity.this.updateList();
                WifiActivity.this.updateConnectedWifi();
            }
            super.handleMessage(message);
        }
    };
    private WifiReceiver.WifiStateListener listener = new WifiReceiver.WifiStateListener() { // from class: com.android.settings.wifidialog.WifiActivity.14
        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void connecting() {
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void onWifiOpen() {
            if (WifiActivity.this.mSwitch != null) {
                WifiActivity.this.mSwitch.setEnabled(true);
                WifiActivity.this.onSwitchChanged = true;
            }
            WifiActivity.this.updateSwitchState(true);
            WifiUtils.getInstance().startScanWifi();
            WifiActivity.this.updateConnectedWifi();
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void onWifiClose() {
            if (WifiActivity.this.mSwitch != null) {
                WifiActivity.this.mSwitch.setEnabled(true);
            }
            WifiActivity.this.updateSwitchState(false);
            WifiActivity.this.updateConnectedWifi();
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void connectSuccessful() {
            WifiActivity.this.updateConnectedWifi();
            WifiActivity.this.updateList();
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void connectError() {
            WifiActivity.this.updateConnectedWifi();
            WifiActivity.this.updateList();
            Toast.makeText(WifiActivity.this, "error", 0).show();
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void scanComplete() {
            if (WifiActivity.this.onSwitchChanged) {
                WifiActivity.this.updateConnectedWifi();
                WifiActivity.this.updateList();
                WifiActivity.this.onSwitchChanged = false;
            }
        }

        @Override // com.android.settings.wifidialog.WifiReceiver.WifiStateListener
        public void onHomeClick() {
            WifiActivity.this.mHandler = null;
            WifiUtils.getInstance().unregisterWifiBroadcast();
            WifiActivity.this.finish();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.layout_activity_wifi);
        this.dialogX = getIntent().getIntExtra("pointX", 500);
        initViews();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.dialogX = intent.getIntExtra("pointX", 500);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        initData();
        WifiUtils.getInstance().registerWifiBroadcast(this.listener);
    }

    private void initData() {
        this.manager = WifiUtils.getInstance().getWifiManager();
        WifiUtils.getInstance().startScanWifi();
        updateConnectedWifi();
        updateList();
        this.mHandler.sendMessageDelayed(getMeaasge(9999), 20000L);
    }

    private void initViews() {
        this.mView = (LinearLayout) findViewById(R$id.wifi_dialog_root);
        this.mRootView = (LinearLayout) findViewById(R$id.root_view);
        this.wifiListView = (ListView) findViewById(R$id.wifi_list_view);
        this.mContentView = (LinearLayout) findViewById(R$id.root_content);
        this.mSwitch = (Button) findViewById(R$id.wifi_switch);
        this.mWifiName = (TextView) findViewById(R$id.wifi_name_ac);
        this.mScanBtn = (ImageView) findViewById(R$id.wifi_scan_btn);
        this.mMore = (TextView) findViewById(R$id.wifi_dialog_more);
        this.mWifiConnectedRoot = (LinearLayout) findViewById(R$id.wifi_connected_root);
        this.mTitleLy = (LinearLayout) findViewById(R$id.title_layout);
        float abs = Math.abs(-5) / 25.0f;
        Bitmap takeScreenShot = takeScreenShot(abs);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("initViews: ");
        sb.append(takeScreenShot == null);
        sb.append("   ");
        sb.append(abs);
        Log.d(str, sb.toString());
        if (takeScreenShot != null) {
            this.mView.setBackground(new BitmapDrawable(rsBlur(this, takeScreenShot, 25, 1.0f / abs)));
        }
        int i = SettingsApplication.mHeightFix;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) ((i - 100) * 0.8d * 0.8d), (int) ((i - 100) * 0.8d));
        layoutParams.leftMargin = this.dialogX;
        String str2 = TAG;
        Log.d(str2, "initViews: " + this.dialogX);
        this.mRootView.setLayoutParams(layoutParams);
        double d = (double) ((int) (((double) (SettingsApplication.mHeightFix + (-100))) * 0.8d));
        this.mTitleLy.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (0.23d * d)));
        this.wifiListView.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (0.62d * d)));
        this.mMore.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (d * 0.15d)));
        WifiUtils.getInstance().init(this);
        if (WifiUtils.getInstance().isWifiEnable()) {
            this.mSwitch.setBackgroundResource(R$drawable.switch_open);
        } else {
            this.mSwitch.setBackgroundResource(R$drawable.switch_close);
        }
        this.mSwitch.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WifiUtils.getInstance().isWifiEnable()) {
                    WifiActivity.this.setOnSwitchEnable(false);
                } else {
                    WifiActivity.this.setOnSwitchEnable(true);
                }
            }
        });
        this.mContentView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.finish();
            }
        });
        this.mWifiConnectedRoot.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.showDisconnectDialog();
            }
        });
        this.mScanBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.updateList();
                WifiActivity.this.updateConnectedWifi();
            }
        });
        this.mMore.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.startWifi();
            }
        });
        this.mView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDisconnectDialog() {
        if (this.mHandler.hasMessages(9999)) {
            this.mHandler.removeMessages(9999);
        }
        final ConfirmDialog confirmDialog = new ConfirmDialog(this, R$style.Dialog_Transparent);
        confirmDialog.show();
        confirmDialog.setTitle("断开连接");
        confirmDialog.setOnLeftButtonClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiUtils.getInstance().removeConnectedWifi();
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setOnRightButtonClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                confirmDialog.dismiss();
                WifiActivity.this.mHandler.sendMessageDelayed(WifiActivity.this.getMeaasge(9999), 1000L);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList() {
        this.scanResults.clear();
        WifiUtils.getInstance().startScanWifi();
        List<ScanResult> wifiList = WifiUtils.getInstance().getWifiList();
        this.wifiListView.setVisibility(0);
        for (ScanResult scanResult : wifiList) {
            if (!TextUtils.isEmpty(scanResult.SSID) && !WifiUtils.getInstance().isConnectedTargetSsid(scanResult.SSID)) {
                this.scanResults.add(scanResult);
            }
        }
        WifiInfo connectionInfo = this.manager.getConnectionInfo();
        this.adapter = new WifiAdapter(this, this.scanResults);
        if (connectionInfo != null && !connectionInfo.getSSID().contains("<unknown ssid>")) {
            this.adapter.setConnectInfo(connectionInfo);
        }
        this.wifiListView.setAdapter((ListAdapter) this.adapter);
        this.wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.9
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (WifiActivity.this.mHandler.hasMessages(9999)) {
                    WifiActivity.this.mHandler.removeMessages(9999);
                }
                WifiInfo connectionInfo2 = WifiActivity.this.manager.getConnectionInfo();
                ScanResult item = WifiActivity.this.adapter.getItem(i);
                WifiInfo connectInfo = WifiActivity.this.adapter.getConnectInfo();
                if (connectionInfo2 != null && connectInfo != null) {
                    Log.d("fangli", "connectionInfo ===" + connectionInfo2.getSSID() + "    result ===" + item.SSID + i);
                    if (i == 0 && connectionInfo2.getSSID().contains(connectInfo.getSSID())) {
                        WifiActivity.this.showDisconnectDialog();
                        return;
                    }
                }
                if (WifiUtils.getInstance().isConnectedTargetSsid(item.SSID)) {
                    return;
                }
                WifiActivity.this.connect(item);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifi() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setFlags(268435456);
        intent.setClassName("com.android.settings", "com.android.settings.Settings$NetworkDashboardActivity");
        startActivity(intent);
        finish();
    }

    private void closeWifi() {
        this.scanResults.clear();
        this.wifiListView.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connect(ScanResult scanResult) {
        if (getSecurity(scanResult) == 0) {
            WifiUtils.getInstance().connectWifi(scanResult.SSID, "");
        } else {
            showPasswordDialog(scanResult);
        }
    }

    private void showPasswordDialog(final ScanResult scanResult) {
        final String str = scanResult.SSID;
        WifiPasswordDialog wifiPasswordDialog = new WifiPasswordDialog(this, R$style.Dialog_Transparent);
        this.passwordDialog = wifiPasswordDialog;
        wifiPasswordDialog.show();
        this.passwordDialog.setTitle(str);
        this.passwordDialog.setOnLeftButtonClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiUtils.getInstance().connectWifi(str, WifiActivity.this.passwordDialog.getEditText());
                WifiActivity.this.scanResults.remove(scanResult);
                WifiActivity.this.passwordDialog.dismiss();
            }
        });
        this.passwordDialog.setOnRightButtonClickListener(new View.OnClickListener() { // from class: com.android.settings.wifidialog.WifiActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WifiActivity.this.passwordDialog.dismiss();
                WifiActivity.this.mHandler.sendMessageDelayed(WifiActivity.this.getMeaasge(9999), 1000L);
            }
        });
        this.passwordDialog.setOnCheckBoxChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.wifidialog.WifiActivity.12
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                WifiActivity.this.passwordDialog.setPasswordShow(z);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [int] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v5 */
    static int getSecurity(ScanResult scanResult) {
        ?? r0 = scanResult.capabilities.contains("WEP");
        if (scanResult.capabilities.contains("PSK")) {
            r0 = 2;
        }
        if (scanResult.capabilities.contains("EAP")) {
            return 3;
        }
        return r0;
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        this.mHandler = null;
        WifiUtils.getInstance().unregisterWifiBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Message getMeaasge(int i) {
        Message message = new Message();
        message.what = i;
        return message;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnSwitchEnable(boolean z) {
        updateSwitchState(z);
        if (z) {
            WifiUtils.getInstance().openWifi();
            this.mSwitch.setEnabled(false);
            return;
        }
        this.mSwitch.setEnabled(false);
        WifiUtils.getInstance().closeWifi();
        closeWifi();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSwitchState(boolean z) {
        if (z) {
            this.mSwitch.setBackgroundResource(R$drawable.switch_open);
        } else {
            this.mSwitch.setBackgroundResource(R$drawable.switch_close);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectedWifi() {
        WifiInfo connectionInfo = this.manager.getConnectionInfo();
        if (connectionInfo == null || connectionInfo.getSSID().contains("<unknown ssid>")) {
            return;
        }
        this.mWifiName.setText(connectionInfo.getSSID().replace("\"", ""));
    }

    private Bitmap takeScreenShot(float f) {
        int i = SettingsApplication.mWidthFix;
        int i2 = SettingsApplication.mHeightFix;
        SurfaceControl.ScreenshotHardwareBuffer captureDisplay = SurfaceControl.captureDisplay(new SurfaceControl.DisplayCaptureArgs.Builder(SurfaceControl.getPhysicalDisplayToken(((DisplayManager) SettingsApplication.mApplication.getSystemService(DisplayManager.class)).getDisplay(0).getAddress().getPhysicalDisplayId())).setSourceCrop(new Rect(0, 0, 0, 0)).setSize(i, i2).build());
        if (captureDisplay == null) {
            return null;
        }
        Bitmap asBitmap = captureDisplay.asBitmap();
        Matrix matrix = new Matrix();
        matrix.postScale(f, f);
        return Bitmap.createBitmap(asBitmap, 0, 0, i, i2, matrix, true).copy(Bitmap.Config.ARGB_8888, true);
    }

    public Bitmap rsBlur(Context context, Bitmap bitmap, int i, float f) {
        RenderScript create = RenderScript.create(context);
        Allocation createFromBitmap = Allocation.createFromBitmap(create, bitmap);
        Allocation createTyped = Allocation.createTyped(create, createFromBitmap.getType());
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        create2.setInput(createFromBitmap);
        create2.setRadius(i);
        create2.forEach(createTyped);
        createTyped.copyTo(bitmap);
        create.destroy();
        Matrix matrix = new Matrix();
        matrix.postScale(f, f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
