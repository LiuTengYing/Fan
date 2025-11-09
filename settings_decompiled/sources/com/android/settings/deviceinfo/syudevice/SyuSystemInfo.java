package com.android.settings.deviceinfo.syudevice;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.BasePreferenceController;
import com.syu.jni.ControlNative;
import com.syu.jni.DataSetting;
/* loaded from: classes.dex */
public class SyuSystemInfo extends BasePreferenceController {
    private static String KEY = "settings_about_sys_info";
    private Activity mActivity;
    private DisplayMetrics mDisplayMetrics;
    private Fragment mFragment;
    private String mUiAppTime;
    private String new_resolution;
    private String old_resolution1;
    private String old_resolution2;
    private String old_resolution3;
    private String old_resolution4;
    private String old_resolution5;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SyuSystemInfo(Context context, String str) {
        super(context, str);
        this.old_resolution1 = "";
        this.old_resolution2 = "";
        this.old_resolution3 = "";
        this.old_resolution4 = "";
        this.old_resolution5 = "";
        this.new_resolution = "";
        this.mUiAppTime = "";
    }

    public void showDialog(Fragment fragment, String str) {
        SyuSysDialog.show(fragment, str);
    }

    public void setHost(Fragment fragment) {
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY.contains(preference.getKey())) {
            showDialog(this.mFragment, this.mActivity.getResources().getString(R$string.settings_about_sys_info));
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getSystemInfo();
    }

    private String getSystemInfo() {
        this.mDisplayMetrics = new DisplayMetrics();
        String systemInfoApp = getSystemInfoApp();
        String systemTime = getSystemTime();
        return systemInfoApp + "\n" + systemTime;
    }

    private String getSystemInfoApp() {
        String string = this.mActivity.getResources().getString(R$string.device_info_default);
        try {
            byte[] fyt_get_ui_time = ControlNative.INSTANCE.fyt_get_ui_time();
            if (fyt_get_ui_time.length > 5) {
                this.mUiAppTime = String.valueOf(Math.abs((int) fyt_get_ui_time[5]) + 1900) + "-" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[4])) + "-" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[3])) + " " + String.format("%02d", Byte.valueOf(fyt_get_ui_time[2])) + ":" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[1])) + ":" + String.format("%02d", Byte.valueOf(fyt_get_ui_time[0]));
                return DataSetting.ANDROID_COMPANY + getDisplayMetrics() + this.mUiAppTime;
            }
            return string;
        } catch (Exception e) {
            e.printStackTrace();
            return string;
        }
    }

    private String getDisplayMetrics() {
        int i = SystemProperties.getInt("sys.lsec.navi_bar_height", 0);
        String str = " ";
        if (this.mDisplayMetrics != null) {
            ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(this.mDisplayMetrics);
            DisplayMetrics displayMetrics = this.mDisplayMetrics;
            int i2 = displayMetrics.widthPixels;
            int i3 = displayMetrics.heightPixels;
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            sb.append(i2);
            sb.append("x");
            int i4 = i3 + i;
            sb.append(i4);
            sb.append(" ");
            str = sb.toString();
            this.old_resolution1 = i2 + "x" + i4;
            this.old_resolution2 = i2 + "X" + i4;
            this.old_resolution3 = i4 + "x" + i2;
            this.old_resolution4 = i4 + "X" + i2;
            this.old_resolution5 = "-_";
        }
        String str2 = SystemProperties.get("persist.sys.private_systeminfo", "");
        if (!"".equals(str2)) {
            if ("null".equals(str2)) {
                this.new_resolution = "";
            } else {
                this.new_resolution = str2;
            }
            str = str.replace(this.old_resolution1, this.new_resolution).replace(this.old_resolution2, this.new_resolution).replace(this.old_resolution3, this.new_resolution).replace(this.old_resolution4, this.new_resolution).replace(this.old_resolution5, "_");
        }
        Log.d("fangli", "getDisplayMetrics: " + str);
        return str;
    }

    private String getSystemTime() {
        return "System " + SystemProperties.get("ro.build.date", "");
    }
}
