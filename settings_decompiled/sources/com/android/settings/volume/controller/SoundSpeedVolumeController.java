package com.android.settings.volume.controller;

import android.content.Context;
import android.content.res.Resources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.volume.dialog.SoundSpeedVolumeDialogFragment;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class SoundSpeedVolumeController extends AbstractPreferenceController implements LifecycleObserver {
    private Context mContext;
    private RestrictedPreference mPreference;
    private int speedSound;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sound_by_speed";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public SoundSpeedVolumeController(Context context, Lifecycle lifecycle) {
        super(context);
        this.speedSound = 0;
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
        SoundSpeedVolumeDialogFragment.show(fragment, str, this.speedSound);
    }

    public void setSpeedSound(int i) {
        this.speedSound = i;
        this.mPreference.setState(getSpeedSound(i));
    }

    private String getSpeedSound(int i) {
        Resources resources = this.mContext.getResources();
        int i2 = R$string.sound_speed_volume_close;
        resources.getString(i2);
        if (i != 3) {
            if (i != 5) {
                if (i == 7) {
                    return this.mContext.getResources().getString(R$string.sound_speed_volume_high);
                }
                return this.mContext.getResources().getString(i2);
            }
            return this.mContext.getResources().getString(R$string.sound_speed_volume_middle);
        }
        return this.mContext.getResources().getString(R$string.sound_speed_volume_low);
    }
}
