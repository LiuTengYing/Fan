package com.android.settings.volume.controller;

import android.content.Context;
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
public class SoundReserverReduceController extends AbstractPreferenceController implements LifecycleObserver {
    private boolean isChecked;
    private LayoutPreference mPreference;
    private LinearLayout mReserverScaleLayout;
    private Switch mReserverSwitch;
    private TextView mTextValue;
    private TextSeekBar reserverReduceSeek;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "reversesound_lower";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public SoundReserverReduceController(Context context, Lifecycle lifecycle) {
        super(context);
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
        this.reserverReduceSeek = (TextSeekBar) this.mPreference.findViewById(R$id.reserver_reduce_seek);
        this.mReserverSwitch = (Switch) this.mPreference.findViewById(R$id.reserver_sound_switch);
        this.mTextValue = (TextView) this.mPreference.findViewById(R$id.reserver_reduce_max);
        this.mReserverScaleLayout = (LinearLayout) this.mPreference.findViewById(R$id.reserver_sound_layout);
        this.reserverReduceSeek.setOnSeekBarChangeListener(new TextSeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.volume.controller.SoundReserverReduceController.1
            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBar textSeekBar, int i, boolean z) {
                if (z) {
                    IpcObj.getInstance().setCmd(4, 20, i);
                }
            }
        });
        this.mReserverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.volume.controller.SoundReserverReduceController.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SoundReserverReduceController.this.setChecked(z);
                IpcObj.getInstance().setCmd(4, 13, z ? 1 : 0);
            }
        });
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        Switch r0 = this.mReserverSwitch;
        if (r0 != null) {
            r0.setChecked(z);
        }
        LinearLayout linearLayout = this.mReserverScaleLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(this.isChecked ? 8 : 0);
        }
    }

    public void setProgress(int i) {
        TextSeekBar textSeekBar = this.reserverReduceSeek;
        if (textSeekBar != null) {
            textSeekBar.setProgress(i);
        }
        TextView textView = this.mTextValue;
        if (textView != null) {
            textView.setText(i + "");
        }
    }
}
