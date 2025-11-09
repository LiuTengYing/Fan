package com.abupdate.iot_libs.engine.otaStatus;

import com.abupdate.iot_libs.utils.SPFTool;
import com.abupdate.trace.Trace;
/* loaded from: classes.dex */
public class OtaStatusMgr {
    private static OtaStatusMgr mInstance;
    private OtaStatus curStatus = getCurStatus();

    private OtaStatusMgr() {
    }

    public static OtaStatusMgr getInstance() {
        if (mInstance == null) {
            mInstance = new OtaStatusMgr();
        }
        return mInstance;
    }

    public OtaStatus getCurStatus() {
        if (this.curStatus == null) {
            this.curStatus = OtaStatus.valueOf(SPFTool.getString("OtaStatusMgr:KEY_SP_CUR_OTA_STATUS", OtaStatus.IDLE.name()));
        }
        return this.curStatus;
    }

    public void refreshOtaStatus(OtaStatus otaStatus) {
        if (otaStatus == null) {
            throw new IllegalArgumentException("dest ota status not be null.");
        }
        Trace.d("OtaStatusMgr", "refreshOtaStatus() %s -> %s", this.curStatus.name(), otaStatus.name());
        if (this.curStatus != otaStatus) {
            this.curStatus = otaStatus;
            SPFTool.putString("OtaStatusMgr:KEY_SP_CUR_OTA_STATUS", otaStatus.name());
        }
    }

    public boolean isIdle() {
        return getCurStatus() == OtaStatus.IDLE;
    }

    public boolean isChecking() {
        return getCurStatus() == OtaStatus.CHECKING;
    }

    public boolean isCheckNewVersion() {
        return getCurStatus() == OtaStatus.CHECK_NEW_VERSION;
    }

    public boolean isDownloading() {
        return getCurStatus() == OtaStatus.DOWNLOADING;
    }

    public boolean isDownloadFinished() {
        return getCurStatus() == OtaStatus.DOWNLOAD_FINISHED;
    }

    public boolean isUpgrading() {
        return getCurStatus() == OtaStatus.UPGRADING;
    }

    public boolean isNeedReboot() {
        return getCurStatus() == OtaStatus.NEED_REBOOT;
    }
}
