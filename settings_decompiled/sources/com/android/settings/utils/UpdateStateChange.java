package com.android.settings.utils;

import com.android.settings.homepage.StateChangeListener;
/* loaded from: classes.dex */
public class UpdateStateChange {
    private static UpdateStateChange mInstance;
    private StateChangeListener listener;

    public static UpdateStateChange getInstance() {
        if (mInstance == null) {
            mInstance = new UpdateStateChange();
        }
        return mInstance;
    }

    public void updateChoice(String str, String str2) {
        StateChangeListener stateChangeListener = this.listener;
        if (stateChangeListener != null) {
            stateChangeListener.stateChange(str, str2);
        }
    }

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.listener = stateChangeListener;
    }

    public void removeStateChangeListener() {
        this.listener = null;
    }
}
