package com.android.settings.factory.controller;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsApplication;
import com.android.settingslib.core.AbstractPreferenceController;
import com.syu.util.FuncUtils;
import java.io.File;
/* loaded from: classes.dex */
public class FactoryTouchScreenKeyStudyController extends AbstractPreferenceController implements LifecycleObserver {
    private int canStudy;
    private Context mContext;
    private String touchPath;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "touchscreenstudy";
    }

    public FactoryTouchScreenKeyStudyController(Context context, Lifecycle lifecycle) {
        super(context);
        this.canStudy = 0;
        this.touchPath = "/proc/gtsql_config";
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (new File(this.touchPath).exists()) {
            this.canStudy = 1;
        } else {
            this.canStudy = 0;
        }
        Log.d("fangli", "isAvailable: " + this.canStudy);
        return (this.canStudy == 0 || (SettingsApplication.mWidthFix == 2000 && SettingsApplication.mHeightFix == 1200) || !FuncUtils.isAppInstalled(this.mContext, "com.syu.calibration") || (SettingsApplication.mWidthFix == 1920 && SettingsApplication.mHeightFix == 1200)) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }
}
