package com.android.settings.factory.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.widget.view.TextSeekBarCenter;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class ProtocolTrackMaxAngelController extends AbstractPreferenceController implements LifecycleObserver {
    private static String TAG = "ProtocolTrackMaxAngelController";
    private TextSeekBarCenter angelSeekbar;
    private TextView mCurrentValue;
    private LayoutPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "back_car_max_angel";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public void showDialog(Fragment fragment, String str) {
    }

    public ProtocolTrackMaxAngelController(Context context, Lifecycle lifecycle) {
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
        int i = SystemProperties.getInt("persist.fyt.track_angle_max", 40);
        String str = TAG;
        Log.d(str, "init: " + i);
        this.angelSeekbar = (TextSeekBarCenter) this.mPreference.findViewById(R$id.protocol_reverse_max_angel);
        this.mCurrentValue = (TextView) this.mPreference.findViewById(R$id.protocol_reverse_max_angel_max);
        this.angelSeekbar.setMin(20);
        this.angelSeekbar.setMax(50);
        this.angelSeekbar.setProgress(i - 20);
        TextView textView = this.mCurrentValue;
        textView.setText(i + "");
        this.angelSeekbar.setOnSeekBarChangeListener(new TextSeekBarCenter.OnSeekBarChangeListener() { // from class: com.android.settings.factory.controller.ProtocolTrackMaxAngelController.1
            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStartTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onStopTrackingTouch(TextSeekBarCenter textSeekBarCenter) {
            }

            @Override // com.android.settings.widget.view.TextSeekBarCenter.OnSeekBarChangeListener
            public void onProgressChanged(TextSeekBarCenter textSeekBarCenter, int i2, boolean z) {
                StringBuilder sb = new StringBuilder();
                int i3 = i2 + 20;
                sb.append(i3);
                sb.append("");
                SystemProperties.set("persist.fyt.track_angle_max", sb.toString());
                TextView textView2 = ProtocolTrackMaxAngelController.this.mCurrentValue;
                textView2.setText(i3 + "");
                String str2 = ProtocolTrackMaxAngelController.TAG;
                Log.d(str2, "onProgressChanged: " + i2);
            }
        });
    }
}
