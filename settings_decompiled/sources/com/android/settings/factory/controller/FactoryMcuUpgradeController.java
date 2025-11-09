package com.android.settings.factory.controller;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import com.android.settings.factory.dialog.UpdateProgressDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryMcuUpgradeController extends AbstractPreferenceController implements LifecycleObserver {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mcuupgrade";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public FactoryMcuUpgradeController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    public void showDialog(Fragment fragment, String str) {
        UpdateProgressDialogFragment.show(fragment, str);
    }
}
