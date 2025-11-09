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
public class SoundGpxMixScaleController extends AbstractPreferenceController implements LifecycleObserver {
    private TextSeekBar gpsMixSeek;
    private boolean isChecked;
    private Switch mGpsMixSwitch;
    private LinearLayout mGpsScaleLayout;
    private LayoutPreference mPreference;
    private TextView mTextValue;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mixsound_scale";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public SoundGpxMixScaleController(Context context, Lifecycle lifecycle) {
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
        this.gpsMixSeek = (TextSeekBar) this.mPreference.findViewById(R$id.gps_mix_seek);
        this.mGpsMixSwitch = (Switch) this.mPreference.findViewById(R$id.gps_mix_scale_switch);
        this.mTextValue = (TextView) this.mPreference.findViewById(R$id.gps_mix_max);
        LinearLayout linearLayout = (LinearLayout) this.mPreference.findViewById(R$id.gps_mix_scale_layout);
        this.mGpsScaleLayout = linearLayout;
        linearLayout.setVisibility(this.isChecked ? 0 : 8);
        this.mGpsMixSwitch.setChecked(this.isChecked);
        this.gpsMixSeek.setOnSeekBarChangeListener(new TextSeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.volume.controller.SoundGpxMixScaleController.1
            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBar textSeekBar, int i, boolean z) {
                if (z) {
                    IpcObj.getInstance().setCmd(4, 10, i);
                }
                if (SoundGpxMixScaleController.this.mTextValue != null) {
                    TextView textView = SoundGpxMixScaleController.this.mTextValue;
                    textView.setText(i + "");
                }
            }
        });
        this.mGpsMixSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.volume.controller.SoundGpxMixScaleController.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SoundGpxMixScaleController.this.setChecked(z);
                if (SoundGpxMixScaleController.this.mGpsScaleLayout != null) {
                    SoundGpxMixScaleController.this.mGpsScaleLayout.setVisibility(z ? 0 : 8);
                }
                IpcObj.getInstance().setCmd(4, 9, z ? 1 : 0);
            }
        });
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        Switch r0 = this.mGpsMixSwitch;
        if (r0 != null) {
            r0.setChecked(z);
        }
        LinearLayout linearLayout = this.mGpsScaleLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(this.isChecked ? 0 : 8);
        }
    }

    public void setProgress(int i) {
        TextSeekBar textSeekBar = this.gpsMixSeek;
        if (textSeekBar != null) {
            textSeekBar.setProgress(i);
        }
        TextView textView = this.mTextValue;
        if (textView != null) {
            textView.setText(i + "");
        }
    }
}
