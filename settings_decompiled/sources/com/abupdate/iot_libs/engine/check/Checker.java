package com.abupdate.iot_libs.engine.check;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.local.FotaParamController;
import com.abupdate.iot_libs.data.remote.VersionInfo;
import com.abupdate.iot_libs.engine.network.base.BaseRequestStack;
import com.abupdate.iot_libs.engine.otaStatus.OtaStatus;
import com.abupdate.iot_libs.engine.otaStatus.OtaStatusMgr;
import com.abupdate.iot_libs.engine.report.ReportManager;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.engine.trigger.CheckPeriod;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.trace.Trace;
import java.util.List;
/* loaded from: classes.dex */
public class Checker extends BaseRequestStack {
    private static Checker mInstance;
    private OtaStatusMgr otaStatusMgr = OtaStatusMgr.getInstance();

    public static Checker getInstance() {
        if (mInstance == null) {
            synchronized (Checker.class) {
                if (mInstance == null) {
                    mInstance = new Checker();
                }
            }
        }
        return mInstance;
    }

    private Checker() {
    }

    public CommonResponse<List<VersionInfo>> checkVersionTask() {
        CommonResponse<List<VersionInfo>> handleCheckResponse = handleCheckResponse(new CommonResponse<>());
        handleCheckVersionResponse(handleCheckResponse);
        return handleCheckResponse;
    }

    private CommonResponse<List<VersionInfo>> handleCheckResponse(CommonResponse<List<VersionInfo>> commonResponse) {
        if (FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism) {
            if (!this.otaStatusMgr.isIdle() && !this.otaStatusMgr.isCheckNewVersion()) {
                Trace.w("Checker", "checkVersionTask() 发起检测失败，当前ota status:" + this.otaStatusMgr.getCurStatus().name());
                commonResponse.setErrorCode(1111);
                return commonResponse;
            }
            this.otaStatusMgr.refreshOtaStatus(OtaStatus.CHECKING);
        }
        CommonResponse<List<VersionInfo>> executed = CheckAllProjectTask.newInstance().executed();
        if (executed.isOK) {
            List<VersionInfo> result = executed.getResult();
            if (result.size() > 0) {
                executed.setResult(result);
                if (FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism) {
                    this.otaStatusMgr.refreshOtaStatus(OtaStatus.CHECK_NEW_VERSION);
                }
                try {
                    OtaAgentPolicy.getOtaEntityController().getMainEntity().getPolicyManager();
                    throw null;
                } catch (FotaException e) {
                    e.printStackTrace();
                }
            } else {
                Trace.e("Checker", "checkVersionTask() failed,versionInfos size = 0");
                executed.setErrorCode(8003);
                if (FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism) {
                    this.otaStatusMgr.refreshOtaStatus(OtaStatus.IDLE);
                }
            }
        } else {
            executed.setErrorCode(executed.errorCode);
            if (FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism) {
                this.otaStatusMgr.refreshOtaStatus(OtaStatus.IDLE);
            }
        }
        if (OtaAgentPolicy.getParamsController().getParams().useDefaultClientTrigger) {
            CheckPeriod.resetPeriod();
        }
        return executed;
    }

    private void handleCheckVersionResponse(CommonResponse commonResponse) {
        int i = 2003;
        int i2 = 0;
        if (commonResponse.isOK) {
            i = 2002;
        } else {
            int i3 = commonResponse.errorCode;
            if (i3 == 1001) {
                i2 = 20501;
            } else if (i3 == 1111) {
                i2 = 20801;
            } else if (i3 == 3003) {
                i2 = 20301;
            }
            i = 2001;
        }
        ReportManager.getInstance().saveEvent(i, i2);
    }
}
