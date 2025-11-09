package com.android.settings.common.controller;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.common.dialog.CommonUsbDeviceStartDialogFragment;
import com.android.settings.utils.AppUtils;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonUsbDeviceStartController extends AbstractPreferenceController implements LifecycleObserver {
    private static String mUSB_Conn_Start_Carlink = "com.syu.carlink/com.syu.carlink.MainActivity";
    private static String mUSB_Conn_Start_Easyconn = "net.easyconn/net.easyconn.ui.Fy15MainActivity";
    private static String mUSB_Conn_Start_Hicar = "com.syu.hicar/com.syu.hicar.MainActivity";
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_device_start";
    }

    public CommonUsbDeviceStartController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return getIsDeviceShow();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void showDialog(Fragment fragment, String str) {
        CommonUsbDeviceStartDialogFragment.show(fragment, str);
    }

    public void updateState() {
        String str = SystemProperties.get("persist.fyt.phonestartapp", "");
        Resources resources = SettingsApplication.mApplication.getResources();
        int i = R$string.common_default_usb_conn_none;
        String string = resources.getString(i);
        if ("".equals(str)) {
            string = SettingsApplication.mApplication.getResources().getString(i);
        } else if (mUSB_Conn_Start_Carlink.equals(str)) {
            string = SettingsApplication.mApplication.getResources().getString(R$string.common_carlink);
        } else if (mUSB_Conn_Start_Easyconn.equals(str)) {
            string = SettingsApplication.mApplication.getResources().getString(R$string.common_easyconnection);
        } else if (mUSB_Conn_Start_Hicar.equals(str)) {
            string = SettingsApplication.mApplication.getResources().getString(R$string.common_hicar);
        }
        if (this.mPreference != null) {
            Log.d("CommonUsbDeviceStartController", "updateState: " + string);
            this.mPreference.setState(string);
        }
    }

    private boolean getIsDeviceShow() {
        return AppUtils.isAppInstalled(this.mContext, "com.syu.carlink") || (AppUtils.isAppInstalled(this.mContext, "net.easyconn") && !SystemProperties.getBoolean("persist.fyt.hideecforusbconn", false)) || AppUtils.isAppInstalled(this.mContext, "com.syu.hicar");
    }
}
