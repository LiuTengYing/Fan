package com.android.settings.common.controller;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.common.dialog.CommonPoweroffDelayDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class CommonPoweroffDelayController extends AbstractPreferenceController implements LifecycleObserver {
    private static int mTime;
    private Context mContext;
    private RestrictedPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "poweroff_delay";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public CommonPoweroffDelayController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mContext = context;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void showDialog(Fragment fragment, String str) {
        CommonPoweroffDelayDialogFragment.show(fragment, str, mTime);
    }

    public void setTime(int i) {
        mTime = i;
        this.mPreference.setState(getCurrentPosition(i));
    }

    private String getCurrentPosition(int i) {
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.power_delay_time);
        char c = 3;
        if (i != 0) {
            if (i == 3) {
                c = 1;
            } else if (i == 10) {
                c = 2;
            } else if (i != 180) {
                if (i == 300) {
                    c = 4;
                } else if (i == 600) {
                    c = 5;
                } else if (i == 900) {
                    c = 6;
                }
            }
            return stringArray[c];
        }
        c = 0;
        return stringArray[c];
    }
}
