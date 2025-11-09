package com.android.settings.widget;

import androidx.preference.Preference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class GenericSwitchController extends SwitchWidgetController implements Preference.OnPreferenceChangeListener {
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private Preference mPreference;

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setTitle(String str) {
    }

    public GenericSwitchController(PrimarySwitchPreference primarySwitchPreference) {
        setPreference(primarySwitchPreference);
    }

    public GenericSwitchController(RestrictedSwitchPreference restrictedSwitchPreference) {
        setPreference(restrictedSwitchPreference);
    }

    private void setPreference(Preference preference) {
        this.mPreference = preference;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(preference.getContext()).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void startListening() {
        this.mPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void stopListening() {
        this.mPreference.setOnPreferenceChangeListener(null);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setChecked(boolean z) {
        Preference preference = this.mPreference;
        if (preference instanceof PrimarySwitchPreference) {
            ((PrimarySwitchPreference) preference).setChecked(z);
        } else if (preference instanceof RestrictedSwitchPreference) {
            ((RestrictedSwitchPreference) preference).setChecked(z);
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public boolean isChecked() {
        Preference preference = this.mPreference;
        if (preference instanceof PrimarySwitchPreference) {
            return ((PrimarySwitchPreference) preference).isChecked();
        }
        if (preference instanceof RestrictedSwitchPreference) {
            return ((RestrictedSwitchPreference) preference).isChecked();
        }
        return false;
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setEnabled(boolean z) {
        Preference preference = this.mPreference;
        if (preference instanceof PrimarySwitchPreference) {
            ((PrimarySwitchPreference) preference).setSwitchEnabled(z);
        } else if (preference instanceof RestrictedSwitchPreference) {
            ((RestrictedSwitchPreference) preference).setEnabled(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mListener;
        if (onSwitchChangeListener != null) {
            boolean onSwitchToggled = onSwitchChangeListener.onSwitchToggled(((Boolean) obj).booleanValue());
            if (onSwitchToggled) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt(DashboardFragment.CATEGORY));
            }
            return onSwitchToggled;
        }
        return false;
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        Preference preference = this.mPreference;
        if (preference instanceof PrimarySwitchPreference) {
            ((PrimarySwitchPreference) preference).setDisabledByAdmin(enforcedAdmin);
        } else if (preference instanceof RestrictedSwitchPreference) {
            ((RestrictedSwitchPreference) preference).setDisabledByAdmin(enforcedAdmin);
        }
    }
}
