package com.android.settings.common.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsApplication;
import com.android.settings.common.dialog.CommonNaviAppDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonNaviAppController extends AbstractPreferenceController implements LifecycleObserver {
    private static String packageName = "";
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "navi_app";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonNaviAppController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedPreference restrictedPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = restrictedPreference;
        if (restrictedPreference == null) {
            return;
        }
        String str = SystemProperties.get("persist.sys.navi.packagename", "");
        Log.d("CommonNaviAppController", "displayPreference: " + str);
        this.mPreference.setState(str);
    }

    public void showDialog(Fragment fragment, String str) {
        CommonNaviAppDialogFragment.show(fragment, str, packageName);
    }

    public void setPackageName(String str) {
        packageName = str;
        String str2 = SystemProperties.get("persist.sys.navi.packagename", "");
        Log.d("CommonNaviAppController", "displayPreference: " + str2);
        this.mPreference.setState(SettingsApplication.mApplication.getAppName(str2));
    }
}
