package com.android.settings.factory.controller;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryTouchScreenTestController extends AbstractPreferenceController implements LifecycleObserver {
    private static String className = "com.syu.touchscreentest.MainActivity";
    private static String packageName = "com.syu.touchscreentest";
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "touch_test";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryTouchScreenTestController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    public void showDialog(Fragment fragment, String str) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        intent.setFlags(268435456);
        this.mContext.getApplicationContext().startActivity(intent);
    }
}
