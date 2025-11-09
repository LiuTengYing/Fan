package com.android.settings.common.controller;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.common.dialog.CommonAutoStartDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonAutoStartAppController extends AbstractPreferenceController implements LifecycleObserver {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "automatic_start_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonAutoStartAppController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    public void showDialog(Fragment fragment, String str) {
        CommonAutoStartDialogFragment.show(fragment, str);
    }
}
