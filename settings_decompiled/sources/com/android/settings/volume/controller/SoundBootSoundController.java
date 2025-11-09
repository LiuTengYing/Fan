package com.android.settings.volume.controller;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.ipc.IpcObj;
import com.android.settings.widget.view.TextSeekBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class SoundBootSoundController extends AbstractPreferenceController implements LifecycleObserver {
    private LinearLayout bootSeekLayout;
    private TextSeekBar bootSoundSeek;
    private TextView bootVolume;
    private Switch bootVolumeSwitch;
    private boolean isChecked;
    private boolean isFromUser;
    private LayoutPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bootsound";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public SoundBootSoundController(Context context, Lifecycle lifecycle) {
        super(context);
        this.isFromUser = true;
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = layoutPreference;
        if (layoutPreference == null) {
            return;
        }
        initViews();
    }

    private void initViews() {
        this.bootSoundSeek = (TextSeekBar) this.mPreference.findViewById(R$id.boot_sound_seek);
        this.bootSeekLayout = (LinearLayout) this.mPreference.findViewById(R$id.boot_sound_seek_layout);
        this.bootVolumeSwitch = (Switch) this.mPreference.findViewById(R$id.default_volume_switch);
        this.bootVolume = (TextView) this.mPreference.findViewById(R$id.sound_boot_max);
        this.bootVolumeSwitch.setChecked(this.isChecked);
        this.bootSeekLayout.setVisibility(this.isChecked ? 0 : 8);
        this.bootVolumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.volume.controller.SoundBootSoundController.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SoundBootSoundController.this.setChecked(z);
                IpcObj.getInstance().setCmd(4, 12, z ? 1 : 0);
                if (SoundBootSoundController.this.bootSeekLayout != null) {
                    SoundBootSoundController.this.bootSeekLayout.setVisibility(z ? 0 : 8);
                }
            }
        });
        this.bootSoundSeek.setOnSeekBarChangeListener(new TextSeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.volume.controller.SoundBootSoundController.2
            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBar textSeekBar, int i, boolean z) {
                Log.d("SoundBootSoundController", "onProgressChanged: " + z);
                if (z) {
                    IpcObj.getInstance().setCmd(4, 14, i);
                }
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBar textSeekBar) {
                SoundBootSoundController.this.isFromUser = false;
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBar textSeekBar) {
                SoundBootSoundController.this.isFromUser = true;
            }
        });
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        Switch r0 = this.bootVolumeSwitch;
        if (r0 != null) {
            r0.setChecked(z);
        }
        LinearLayout linearLayout = this.bootSeekLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(this.isChecked ? 0 : 8);
        }
    }

    public void updateProgress(int i) {
        TextSeekBar textSeekBar = this.bootSoundSeek;
        if (textSeekBar != null) {
            textSeekBar.setProgress(i);
            TextView textView = this.bootVolume;
            textView.setText(i + "");
        }
    }
}
