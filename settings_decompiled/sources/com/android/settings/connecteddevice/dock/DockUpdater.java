package com.android.settings.connecteddevice.dock;

import android.content.Context;
/* loaded from: classes.dex */
public interface DockUpdater {
    default void forceUpdate() {
    }

    default void registerCallback() {
    }

    default void setPreferenceContext(Context context) {
    }

    default void unregisterCallback() {
    }
}
