package com.android.settings.notification.zen;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import com.android.settings.R$id;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.SettingsEnableZenModeDialog;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class ZenModeButtonPreferenceController extends AbstractZenModePreferenceController {
    private final FragmentManager mFragment;
    private boolean mRefocusButton;
    private Button mZenButtonOff;
    private Button mZenButtonOn;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_toggle";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeButtonPreferenceController(Context context, Lifecycle lifecycle, FragmentManager fragmentManager) {
        super(context, "zen_mode_toggle", lifecycle);
        this.mRefocusButton = false;
        this.mFragment = fragmentManager;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        super.updateState(preference);
        if (this.mZenButtonOn == null) {
            this.mZenButtonOn = (Button) ((LayoutPreference) preference).findViewById(R$id.zen_mode_settings_turn_on_button);
            updateZenButtonOnClickListener(preference);
        }
        if (this.mZenButtonOff == null) {
            Button button = (Button) ((LayoutPreference) preference).findViewById(R$id.zen_mode_settings_turn_off_button);
            this.mZenButtonOff = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModeButtonPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ZenModeButtonPreferenceController.this.lambda$updateState$0(preference, view);
                }
            });
        }
        updatePreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(Preference preference, View view) {
        this.mRefocusButton = true;
        writeMetrics(preference, false);
        this.mBackend.setZenMode(0);
    }

    private void updatePreference(Preference preference) {
        int zenMode = getZenMode();
        if (zenMode == 1 || zenMode == 2 || zenMode == 3) {
            this.mZenButtonOff.setVisibility(0);
            this.mZenButtonOn.setVisibility(8);
            if (this.mRefocusButton) {
                this.mRefocusButton = false;
                this.mZenButtonOff.sendAccessibilityEvent(8);
                return;
            }
            return;
        }
        this.mZenButtonOff.setVisibility(8);
        updateZenButtonOnClickListener(preference);
        this.mZenButtonOn.setVisibility(0);
        if (this.mRefocusButton) {
            this.mRefocusButton = false;
            this.mZenButtonOn.sendAccessibilityEvent(8);
        }
    }

    private void updateZenButtonOnClickListener(final Preference preference) {
        this.mZenButtonOn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModeButtonPreferenceController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ZenModeButtonPreferenceController.this.lambda$updateZenButtonOnClickListener$1(preference, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateZenButtonOnClickListener$1(Preference preference, View view) {
        this.mRefocusButton = true;
        writeMetrics(preference, true);
        int zenDuration = getZenDuration();
        if (zenDuration == -1) {
            new SettingsEnableZenModeDialog().show(this.mFragment, "EnableZenModeButton");
        } else if (zenDuration == 0) {
            this.mBackend.setZenMode(1);
        } else {
            this.mBackend.setZenModeForDuration(zenDuration);
        }
    }

    private void writeMetrics(Preference preference, boolean z) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt(DashboardFragment.CATEGORY));
        this.mMetricsFeatureProvider.action(this.mContext, 1268, z);
    }
}
