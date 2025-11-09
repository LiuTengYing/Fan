package com.android.settings.factory.controller;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.factory.dialog.FctoryBackcarCameraDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryBackupCmaeraSelectController extends AbstractPreferenceController implements LifecycleObserver {
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_backup";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryBackupCmaeraSelectController(Context context, Lifecycle lifecycle) {
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
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void showDialog(Fragment fragment, String str) {
        FctoryBackcarCameraDialogFragment.show(fragment, str);
    }

    public void updateCameraSelect() {
        int i = SystemProperties.getInt("persist.syu.camera360", 0);
        Resources resources = this.mContext.getResources();
        int i2 = R$string.sevenlight_default;
        String string = resources.getString(i2);
        if (i == 0) {
            string = this.mContext.getResources().getString(i2);
        } else if (i == 1) {
            string = this.mContext.getResources().getString(R$string.str_text_inner360);
        }
        this.mPreference.setState(string);
    }
}
