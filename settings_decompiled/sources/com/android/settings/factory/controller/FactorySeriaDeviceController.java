package com.android.settings.factory.controller;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.factory.dialog.FactorySeriaDeviceDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactorySeriaDeviceController extends AbstractPreferenceController implements LifecycleObserver {
    private static String selectDevice = "";
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "serial_device";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactorySeriaDeviceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void showDialog(Fragment fragment, String str) {
        FactorySeriaDeviceDialogFragment.show(fragment, str, selectDevice);
    }

    public void setSelectDevice(String str) {
        selectDevice = str;
        Log.d("FactorySeriaDeviceController", "setSelectDevice: " + selectDevice);
        updateState();
    }

    public void updateState() {
        Resources resources = this.mContext.getResources();
        int i = R$string.set_serial_device_null;
        String string = resources.getString(i);
        if (selectDevice.contains("null")) {
            string = this.mContext.getResources().getString(i);
        }
        if (selectDevice.contains("RadarXiaoFD#9600")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_RadarXiaoFD_9600);
        }
        if (selectDevice.contains("Accelerator#9600")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_accelerator_9600);
        }
        if (selectDevice.contains("Accelerator3B#9600")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_accelerator3b_9600);
        }
        if (selectDevice.contains("OroTpms#9600")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_orotpms_9600);
        }
        if (selectDevice.contains("RadarTianCheng#9600")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_radartiancheng_9600);
        }
        if (selectDevice.contains("TrackPlugin#19200")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_trackplugin_19200);
        }
        if (selectDevice.contains("PluginWLLeds#19200")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_pluginwlleds_19200);
        }
        if (selectDevice.contains("Common_XZMGuiji#19200")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_xzguiji_38400);
        }
        if (selectDevice.contains("Common_WCGuiji#38400")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_wcguiji_38400);
        }
        if (selectDevice.contains("LeiShenDsp#38400")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_yltdsp_38400);
        }
        if (selectDevice.contains("RaiseLeds#38400")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_rzclight_38400);
        }
        if (selectDevice.contains("SnnavLed#115200")) {
            string = this.mContext.getResources().getString(R$string.set_serial_device_SnnavLed_115200);
        }
        this.mPreference.setState(string);
    }
}
