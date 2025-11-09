package com.android.settings.widget;

import android.widget.Switch;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class MainSwitchBarController extends SwitchWidgetController implements OnMainSwitchChangeListener {
    private final SettingsMainSwitchBar mMainSwitch;

    public MainSwitchBarController(SettingsMainSwitchBar settingsMainSwitchBar) {
        this.mMainSwitch = settingsMainSwitchBar;
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setupView() {
        this.mMainSwitch.show();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void teardownView() {
        this.mMainSwitch.hide();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setTitle(String str) {
        this.mMainSwitch.setTitle(str);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void startListening() {
        this.mMainSwitch.addOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void stopListening() {
        this.mMainSwitch.removeOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setChecked(boolean z) {
        this.mMainSwitch.setChecked(z);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public boolean isChecked() {
        return this.mMainSwitch.isChecked();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setEnabled(boolean z) {
        this.mMainSwitch.setEnabled(z);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mListener;
        if (onSwitchChangeListener != null) {
            onSwitchChangeListener.onSwitchToggled(z);
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mMainSwitch.setDisabledByAdmin(enforcedAdmin);
    }
}
