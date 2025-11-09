package com.android.settings.volume.controller;

import android.content.Context;
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
public class SoundScaleController extends AbstractPreferenceController implements LifecycleObserver {
    private LayoutPreference mPreference;
    private TextView mTextValue;
    private TextSeekBar soundScaleSeek;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "soundscale";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public SoundScaleController(Context context, Lifecycle lifecycle) {
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
        this.soundScaleSeek = (TextSeekBar) this.mPreference.findViewById(R$id.sound_scale_seek);
        this.mTextValue = (TextView) this.mPreference.findViewById(R$id.sound_scale_max);
        this.soundScaleSeek.setOnSeekBarChangeListener(new TextSeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.volume.controller.SoundScaleController.1
            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBar textSeekBar) {
            }

            @Override // com.android.settings.widget.view.TextSeekBar.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBar textSeekBar, int i, boolean z) {
                if (z) {
                    IpcObj.getInstance().setCmd(4, 52, i);
                }
            }
        });
    }

    public void setMaxValue(int i) {
        TextSeekBar textSeekBar = this.soundScaleSeek;
        if (textSeekBar != null) {
            textSeekBar.setMax(i);
        }
    }

    public void setMinValue(int i) {
        TextSeekBar textSeekBar = this.soundScaleSeek;
        if (textSeekBar != null) {
            textSeekBar.setMin(i);
        }
    }

    public void setProgress(int i) {
        TextSeekBar textSeekBar = this.soundScaleSeek;
        if (textSeekBar != null) {
            textSeekBar.setProgress(i);
        }
        TextView textView = this.mTextValue;
        if (textView != null) {
            textView.setText(i + "");
        }
    }
}
