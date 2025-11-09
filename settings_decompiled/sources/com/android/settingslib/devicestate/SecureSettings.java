package com.android.settingslib.devicestate;

import android.database.ContentObserver;
/* loaded from: classes2.dex */
interface SecureSettings {
    String getStringForUser(String str, int i);

    void putStringForUser(String str, String str2, int i);

    void registerContentObserver(String str, boolean z, ContentObserver contentObserver, int i);
}
