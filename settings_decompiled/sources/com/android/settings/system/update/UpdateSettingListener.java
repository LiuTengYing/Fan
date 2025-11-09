package com.android.settings.system.update;
/* loaded from: classes.dex */
public interface UpdateSettingListener {
    void clearData();

    int getCheckTime();

    boolean getSilentDownload();

    boolean getWifiDownload();

    void onWifiDownload(boolean z);

    void setCheckTime(int i);

    void slientDownload(boolean z);
}
