package com.android.settings.factory.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.UpdateStateChange;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class FactoryRadarFrontController extends AbstractPreferenceController implements LifecycleObserver {
    private LayoutPreference mPreference;
    private Switch mRadarFrontSwitch;
    private Switch mRadarSwitch;
    private RelativeLayout mRadarToFrontLy;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "radartofront";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public FactoryRadarFrontController(Context context, Lifecycle lifecycle) {
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
        this.mRadarToFrontLy = (RelativeLayout) this.mPreference.findViewById(R$id.radar_to_front_ly);
        this.mRadarSwitch = (Switch) this.mPreference.findViewById(R$id.radar_switch);
        this.mRadarFrontSwitch = (Switch) this.mPreference.findViewById(R$id.radar_to_front);
        boolean z = SystemProperties.getBoolean("persist.fyt.zh_frontview_enable", false);
        this.mRadarSwitch.setChecked(z);
        this.mRadarToFrontLy.setVisibility(z ? 0 : 8);
        this.mRadarFrontSwitch.setChecked(SystemProperties.getBoolean("persist.fyt.fy_radartofront", false));
        this.mRadarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.factory.controller.FactoryRadarFrontController.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                SystemProperties.set("persist.fyt.zh_frontview_enable", z2 + "");
                FactoryRadarFrontController.this.mRadarToFrontLy.setVisibility(z2 ? 0 : 8);
                if (!z2) {
                    SystemProperties.set("persist.fyt.fy_radartofront", "false");
                } else {
                    FactoryRadarFrontController.this.mRadarFrontSwitch.setChecked(SystemProperties.getBoolean("persist.fyt.fy_radartofront", false));
                }
                IpcObj.getInstance().setCmd(0, 47, !z2 ? 1 : 0);
                IpcObj.getInstance().setCmd(0, 126, z2 ? 1 : 0);
            }
        });
        this.mRadarFrontSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.factory.controller.FactoryRadarFrontController.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                SystemProperties.set("persist.fyt.fy_radartofront", z2 + "");
                if (z2) {
                    SystemProperties.set("persist.fyt.radarToRecorder_enable", "false");
                }
                UpdateStateChange.getInstance().updateChoice("radartofront", "front");
            }
        });
    }

    public void updateRadarTofront() {
        this.mRadarSwitch.setChecked(SystemProperties.getBoolean("persist.fyt.zh_frontview_enable", false));
        this.mRadarFrontSwitch.setChecked(SystemProperties.getBoolean("persist.fyt.fy_radartofront", false));
    }
}
