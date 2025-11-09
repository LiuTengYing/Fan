package com.android.settings.network.helper;

import com.android.settings.widget.RestrictedSwitchPreferences;
/* loaded from: classes.dex */
public class OnSwitchItemClickLInstener {
    private static OnSwitchItemClickLInstener mInstance;
    private RestrictedSwitchPreferences.onItemClickListener listener;

    public static OnSwitchItemClickLInstener getInstance() {
        if (mInstance == null) {
            mInstance = new OnSwitchItemClickLInstener();
        }
        return mInstance;
    }

    public void setOnWifiSwitchClick() {
        RestrictedSwitchPreferences.onItemClickListener onitemclicklistener = this.listener;
        if (onitemclicklistener != null) {
            onitemclicklistener.onItemClick();
        }
    }

    public void setOnWifiSwitchCheck(boolean z) {
        RestrictedSwitchPreferences.onItemClickListener onitemclicklistener = this.listener;
        if (onitemclicklistener != null) {
            onitemclicklistener.onSwitchCheck(z);
        }
    }

    public void setListener(RestrictedSwitchPreferences.onItemClickListener onitemclicklistener) {
        this.listener = onitemclicklistener;
    }

    public void removeListener() {
        this.listener = null;
    }
}
