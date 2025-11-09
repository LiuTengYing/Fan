package com.android.settings.factory.controller;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.factory.dialog.FactoryImageSettingsDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryImageSettingsController extends AbstractPreferenceController implements LifecycleObserver {
    private int mAvdd;
    private int mVcom;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vcom_avdd";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryImageSettingsController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mAvdd = 0;
        this.mVcom = 0;
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
        FactoryImageSettingsDialogFragment.show(fragment, str, new int[]{this.mAvdd, this.mVcom});
    }

    public void setmAvdd(int i) {
        this.mAvdd = i;
    }

    public void setmVcom(int i) {
        this.mVcom = i;
    }
}
