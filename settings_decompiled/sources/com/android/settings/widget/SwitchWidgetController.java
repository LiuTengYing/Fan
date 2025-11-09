package com.android.settings.widget;

import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public abstract class SwitchWidgetController {
    protected OnSwitchChangeListener mListener;

    /* loaded from: classes.dex */
    public interface OnSwitchChangeListener {
        boolean onSwitchToggled(boolean z);
    }

    public abstract boolean isChecked();

    public abstract void setChecked(boolean z);

    public abstract void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin);

    public abstract void setEnabled(boolean z);

    public abstract void setTitle(String str);

    public void setupView() {
    }

    public abstract void startListening();

    public abstract void stopListening();

    public void teardownView() {
    }

    public void setListener(OnSwitchChangeListener onSwitchChangeListener) {
        this.mListener = onSwitchChangeListener;
    }
}
