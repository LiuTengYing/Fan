package com.unisoc.settings.superresolution;

import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes2.dex */
public class SuperResolutionSettings extends SettingsPreferenceFragment {
    public static PhoneStateListener mPhoneStateListener;
    public static TelephonyManager mTelephonyManager;
    private Context mContext;
    private String mCurrentSelectedMode;
    private String mLastedSelectedMode;
    public int mModeListNum;
    private Uri mOneHandedModeUri;
    private PreferenceScreen mPreferenceScreen;
    private Uri mWifiDeviceAcessUri;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsEnabledForProjectionScreen = false;
    private boolean mIsEnabledForWifiDeviceAccess = false;
    private boolean mIsEnabledForOneHand = false;
    private boolean mIsTelephoneSetateIdel = true;
    public List<String[]> mResolutionModeList = new ArrayList();
    private OneHandedModeObserver mObserver = new OneHandedModeObserver();
    private final Uri MULTIDISPLAY_INFO_URI = Settings.Global.getUriFor("multidisplay_info");

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 46;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.super_resolution_settings);
        this.mContext = getActivity();
        this.mOneHandedModeUri = Settings.Global.getUriFor("one_handed_mode_state");
        this.mWifiDeviceAcessUri = Settings.Global.getUriFor("wifi_device_access");
        this.mPreferenceScreen = getPreferenceScreen();
        List<String[]> resolutionMode = getResolutionMode();
        this.mResolutionModeList = resolutionMode;
        if (resolutionMode != null) {
            this.mModeListNum = resolutionMode.size();
        } else {
            this.mModeListNum = 0;
        }
        for (int i = 0; i < this.mModeListNum; i++) {
            String[] strArr = this.mResolutionModeList.get(i);
            if (strArr != null && strArr.length == 4) {
                String str = strArr[0];
                String str2 = strArr[1];
                String str3 = strArr[2];
                String str4 = strArr[3];
                Log.d("SuperResolutionSettings", "resolution index = " + str + " width = " + str2 + " height = " + str3 + " name = " + str4);
                RadioButtonPreference radioButtonPreference = new RadioButtonPreference(this.mContext);
                radioButtonPreference.setChecked(false);
                radioButtonPreference.setTitle(str4);
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(" × ");
                sb.append(str3);
                radioButtonPreference.setSummary(sb.toString());
                radioButtonPreference.setKey(str);
                this.mPreferenceScreen.addPreference(radioButtonPreference);
            }
        }
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("need_search_icon_in_action_bar", false);
        setArguments(bundle2);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        mTelephonyManager = (TelephonyManager) getSystemService("phone");
        PhoneStateListener phoneStateListener = new PhoneStateListener() { // from class: com.unisoc.settings.superresolution.SuperResolutionSettings.1
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str) {
                super.onCallStateChanged(i, str);
                boolean z = false;
                if (i == 0) {
                    SuperResolutionSettings.this.mIsTelephoneSetateIdel = true;
                    Log.d("SuperResolutionSettings", "PhoneStateListener CALL_STATE_IDLE set preference enable");
                } else if (i == 1) {
                    SuperResolutionSettings.this.mIsTelephoneSetateIdel = false;
                    Log.d("SuperResolutionSettings", "PhoneStateListener CALL_STATE_RINGING set preference disable");
                } else if (i != 2) {
                    return;
                } else {
                    SuperResolutionSettings.this.mIsTelephoneSetateIdel = false;
                    Log.d("SuperResolutionSettings", "PhoneStateListener CALL_STATE_OFFHOOK set preference disable");
                }
                boolean z2 = (SuperResolutionSettings.this.mIsEnabledForProjectionScreen || SuperResolutionSettings.this.mIsEnabledForOneHand || SuperResolutionSettings.this.mIsEnabledForWifiDeviceAccess) ? false : true;
                SuperResolutionSettings superResolutionSettings = SuperResolutionSettings.this;
                if (z2 && superResolutionSettings.mIsTelephoneSetateIdel) {
                    z = true;
                }
                superResolutionSettings.setPreferenceViewEnable(z);
            }
        };
        mPhoneStateListener = phoneStateListener;
        mTelephonyManager.listen(phoneStateListener, 32);
        this.mObserver.register();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        TelephonyManager telephonyManager = mTelephonyManager;
        if (telephonyManager != null) {
            telephonyManager.listen(mPhoneStateListener, 0);
        }
        this.mObserver.unregister();
    }

    private void updateSelectedState(String str) {
        String str2 = this.mCurrentSelectedMode;
        this.mLastedSelectedMode = str2;
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) findPreference(str2);
        if (radioButtonPreference != null) {
            radioButtonPreference.setChecked(false);
            Log.d("SuperResolutionSettings", "lasted pref mode = " + radioButtonPreference.getKey());
        }
        this.mCurrentSelectedMode = str;
        RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) findPreference(str);
        if (radioButtonPreference2 != null) {
            radioButtonPreference2.setChecked(true);
            Log.d("SuperResolutionSettings", "current pref mode = " + radioButtonPreference2.getKey());
        }
    }

    private String getDefalutResolutionModeIndex() {
        String str;
        Point point = new Point();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRealSize(point);
        String valueOf = String.valueOf(point.x);
        String valueOf2 = String.valueOf(point.y);
        int i = 0;
        while (true) {
            if (i >= this.mModeListNum) {
                str = "-1";
                break;
            }
            String[] strArr = this.mResolutionModeList.get(i);
            String str2 = strArr[1];
            String str3 = strArr[2];
            if (getResources().getConfiguration().orientation == 1) {
                if (valueOf.equals(str2) && valueOf2.equals(str3)) {
                    str = strArr[0];
                    break;
                }
                i++;
            } else {
                if (valueOf.equals(str3) && valueOf2.equals(str2)) {
                    str = strArr[0];
                    break;
                }
                i++;
            }
        }
        if (str.equals("-1")) {
            Log.e("SuperResolutionSettings", "getDefalutResolutionModeIndex is error");
            return "0";
        }
        return str;
    }

    private void refreshUI() {
        int preferenceCount = this.mPreferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = this.mPreferenceScreen.getPreference(i);
            if (preference != null && (preference instanceof RadioButtonPreference)) {
                ((RadioButtonPreference) preference).setChecked(false);
            }
        }
        String modeIndex = getModeIndex();
        this.mCurrentSelectedMode = modeIndex;
        updateSelectedState(modeIndex);
        updateBaseOnSingleHandedMode();
    }

    public List<String[]> getResolutionMode() {
        try {
            List<String[]> resolutions = WindowManagerGlobal.getWindowManagerService().getResolutions();
            this.mResolutionModeList = resolutions;
            if (resolutions != null) {
                Collections.sort(resolutions, new Comparator<String[]>() { // from class: com.unisoc.settings.superresolution.SuperResolutionSettings.2
                    @Override // java.util.Comparator
                    public int compare(String[] strArr, String[] strArr2) {
                        return strArr[1].compareTo(strArr2[1]);
                    }
                });
                return this.mResolutionModeList;
            }
            return null;
        } catch (RemoteException unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreferenceViewEnable(boolean z) {
        int preferenceCount = this.mPreferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            this.mPreferenceScreen.getPreference(i).setEnabled(z);
        }
    }

    public void setModeIndex(String str) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "unisoc.action.change_display_config", Integer.valueOf(str).intValue());
    }

    public String getModeIndex() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "unisoc.action.change_display_config", -1);
        if (i == -1) {
            return getDefalutResolutionModeIndex();
        }
        return Integer.toString(i);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        final String key = preference.getKey();
        Log.d("SuperResolutionSettings", "onPreferenceTreeClick pref = " + preference + "   key = " + key);
        if (preference instanceof RadioButtonPreference) {
            updateSelectedState(key);
            if (!this.mCurrentSelectedMode.equals(this.mLastedSelectedMode)) {
                boolean putInt = Settings.Global.putInt(this.mContext.getContentResolver(), "sprd.action.super_resolution_state", 1);
                Log.d("SuperResolutionSettings", "set ACTION_SUPER_RESOLUTION_STATE SUPER_RESOLUTION_STATE_ON = " + putInt);
                setPreferenceViewEnable(false);
                killIncompatibledApps();
                this.mHandler.postDelayed(new Runnable() { // from class: com.unisoc.settings.superresolution.SuperResolutionSettings.3
                    @Override // java.lang.Runnable
                    public void run() {
                        SuperResolutionSettings.this.setModeIndex(key);
                    }
                }, 50L);
            }
            return true;
        }
        return false;
    }

    private void killIncompatibledApps() {
        List<ApplicationInfo> installedApplications = this.mContext.getPackageManager().getInstalledApplications(0);
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.incompatibled_system_app);
        String liveWallpaperPackageName = getLiveWallpaperPackageName();
        HashSet hashSet = new HashSet(Arrays.asList(stringArray));
        List<InputMethodInfo> inputMethodList = ((InputMethodManager) this.mContext.getSystemService("input_method")).getInputMethodList();
        ArrayList arrayList = new ArrayList();
        for (InputMethodInfo inputMethodInfo : inputMethodList) {
            arrayList.add(inputMethodInfo.getPackageName());
        }
        if (installedApplications == null || runningAppProcesses == null) {
            return;
        }
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (applicationInfo != null && !arrayList.contains(applicationInfo.processName)) {
                if (liveWallpaperPackageName != null && applicationInfo.processName.equals(liveWallpaperPackageName)) {
                    Log.d("SuperResolutionSettings", "not kill liveWallpaperApp : " + liveWallpaperPackageName);
                } else if (!applicationInfo.isSystemApp() || hashSet.contains(applicationInfo.processName)) {
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                        if (runningAppProcessInfo != null && applicationInfo.processName.equals(runningAppProcessInfo.processName)) {
                            Log.d("SuperResolutionSettings", " kill app info : " + applicationInfo.packageName);
                            activityManager.forceStopPackage(applicationInfo.packageName);
                        }
                    }
                }
            }
        }
    }

    private String getLiveWallpaperPackageName() {
        WallpaperManager wallpaperManager = (WallpaperManager) this.mContext.getSystemService("wallpaper");
        if (wallpaperManager == null) {
            Log.d("SuperResolutionSettings", "WallpaperManager not available");
            return null;
        }
        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo != null) {
            return wallpaperInfo.getPackageName();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBaseOnSingleHandedMode() {
        String string = Settings.Global.getString(getContentResolver(), "multidisplay_info");
        boolean z = false;
        int i = Settings.Global.getInt(getContentResolver(), "one_handed_mode_state", 0);
        int i2 = Settings.Global.getInt(getContentResolver(), "wifi_device_access", 0);
        boolean z2 = i == 1;
        if (i2 != 1) {
            this.mIsEnabledForWifiDeviceAccess = false;
        } else {
            this.mIsEnabledForWifiDeviceAccess = true;
        }
        if (!TextUtils.isEmpty(string)) {
            this.mIsEnabledForProjectionScreen = true;
        } else {
            this.mIsEnabledForProjectionScreen = false;
        }
        if (!z2) {
            this.mIsEnabledForOneHand = false;
        } else {
            this.mIsEnabledForOneHand = true;
        }
        if (((this.mIsEnabledForProjectionScreen || this.mIsEnabledForOneHand || this.mIsEnabledForWifiDeviceAccess) ? false : true) && this.mIsTelephoneSetateIdel) {
            z = true;
        }
        setPreferenceViewEnable(z);
    }

    /* loaded from: classes2.dex */
    private final class OneHandedModeObserver extends ContentObserver {
        private OneHandedModeObserver() {
            super(SuperResolutionSettings.this.mHandler);
        }

        public void register() {
            SuperResolutionSettings.this.getContentResolver().registerContentObserver(SuperResolutionSettings.this.MULTIDISPLAY_INFO_URI, false, this);
            SuperResolutionSettings.this.getContentResolver().registerContentObserver(SuperResolutionSettings.this.mWifiDeviceAcessUri, false, this);
            SuperResolutionSettings.this.getContentResolver().registerContentObserver(SuperResolutionSettings.this.mOneHandedModeUri, false, this);
        }

        public void unregister() {
            SuperResolutionSettings.this.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            SuperResolutionSettings.this.updateBaseOnSingleHandedMode();
        }
    }
}
