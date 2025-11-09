package com.android.settings.factory.controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryCarLogoController extends AbstractPreferenceController implements LifecycleObserver {
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "carlogo";
    }

    public FactoryCarLogoController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isAppInstalled(this.mContext, "com.syu.carmark");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception unused) {
        }
        return packageInfo != null;
    }

    public void startCarLogo() {
        Intent intent = new Intent();
        intent.setAction("com.syu.carmark");
        intent.setFlags(268435456);
        intent.setFlags(2097152);
        this.mContext.startActivity(intent);
    }
}
