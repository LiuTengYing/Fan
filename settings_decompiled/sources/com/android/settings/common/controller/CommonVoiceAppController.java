package com.android.settings.common.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsApplication;
import com.android.settings.common.dialog.CommonVoiceAppDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonVoiceAppController extends AbstractPreferenceController implements LifecycleObserver {
    private static String packageName = "";
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "voice_app";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonVoiceAppController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void showDialog(Fragment fragment, String str) {
        CommonVoiceAppDialogFragment.show(fragment, str, packageName);
    }

    public void setPackageName(String str) {
        packageName = str;
        if (TextUtils.isEmpty(str)) {
            packageName = SystemProperties.get("ro.syu.voice_def", "");
        }
        Log.d("CommonVoiceAppController", "displayPreference: " + packageName);
        if (TextUtils.isEmpty(packageName)) {
            this.mPreference.setState("");
            return;
        }
        this.mPreference.setState(SettingsApplication.mApplication.getAppName(packageName));
    }
}
