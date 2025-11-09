package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public final class UsbTetherPreferenceController extends TetherBasePreferenceController {
    private static final String TAG = "UsbTetherPrefController";
    private boolean mMassStorageActive;
    final BroadcastReceiver mUsbChangeReceiver;
    private boolean mUsbConnected;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 1;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UsbTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mUsbChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.UsbTetherPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (TextUtils.equals("android.intent.action.MEDIA_SHARED", action)) {
                    UsbTetherPreferenceController.this.mMassStorageActive = true;
                } else if (TextUtils.equals("android.intent.action.MEDIA_UNSHARED", action)) {
                    UsbTetherPreferenceController.this.mMassStorageActive = false;
                } else if (TextUtils.equals("android.hardware.usb.action.USB_STATE", action)) {
                    UsbTetherPreferenceController.this.mUsbConnected = intent.getBooleanExtra("connected", false);
                }
                UsbTetherPreferenceController usbTetherPreferenceController = UsbTetherPreferenceController.this;
                usbTetherPreferenceController.updateState(usbTetherPreferenceController.mPreference);
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNSHARED");
        this.mContext.registerReceiver(this.mUsbChangeReceiver, intentFilter);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mContext.unregisterReceiver(this.mUsbChangeReceiver);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        return this.mUsbConnected && !this.mMassStorageActive;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        String[] tetherableUsbRegexs = this.mTm.getTetherableUsbRegexs();
        return (tetherableUsbRegexs == null || tetherableUsbRegexs.length == 0 || Utils.isMonkeyRunning()) ? false : true;
    }
}
