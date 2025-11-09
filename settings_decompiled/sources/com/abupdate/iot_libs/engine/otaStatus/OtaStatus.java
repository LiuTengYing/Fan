package com.abupdate.iot_libs.engine.otaStatus;
/* loaded from: classes.dex */
public enum OtaStatus {
    IDLE,
    CHECKING,
    CHECK_NEW_VERSION,
    DOWNLOADING,
    DOWNLOAD_FINISHED,
    DOWNLOAD_FAILED,
    DOWNLOAD_PAUSE,
    UPGRADING,
    UPGRADE_PAUSE,
    UPGRADE_FAIL,
    UPGRADE_SUCCESS,
    NEED_REBOOT
}
