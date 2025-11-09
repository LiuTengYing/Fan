package com.android.settings.factory.controller;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.factory.dialog.FactoryLauncherSetDialogFragment;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class FactoryLauncherSetController extends BasePreferenceController {
    private static final String KEY = "launch_sel";
    private static final String TAG = "FactoryLauncherSetController";
    private RestrictedPreference mPreference;

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

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY;
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

    public FactoryLauncherSetController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return super.handlePreferenceTreeClick(preference);
    }

    public void showFragment(Fragment fragment, String str) {
        FactoryLauncherSetDialogFragment.show(fragment, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void updateState() {
        String str = "";
        String str2 = SystemProperties.get("persist.lsec.launcher", "");
        if (TextUtils.isEmpty(str2)) {
            str2 = SystemProperties.get("ro.fyt.launcher", "");
        }
        if (str2.startsWith("com.android")) {
            str = str2.length() > 20 ? str2.replace("com.android.launcher", "Launcher") : "Launcher";
        } else if (str2.startsWith("com.syu")) {
            str = str2.length() > 16 ? str2.replace("com.syu.launcher", "CarLauncher") : "CarLauncher";
        }
        Log.d(TAG, "updateState: " + str);
        this.mPreference.setState(str);
    }
}
