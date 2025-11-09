package com.abupdate.iot_libs.engine.network.request;

import android.content.Context;
import com.abupdate.http_libs.response.Response;
import com.abupdate.iot_libs.data.local.ErrorFileParamInfo;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.network.base.PostFileRequest;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import java.io.File;
/* loaded from: classes.dex */
public class ReportErrorLogRequest extends PostFileRequest<CommonResponse, Void> {
    private ErrorFileParamInfo errorFileParamInfo;

    @Override // com.abupdate.iot_libs.engine.network.base.PostRequest
    protected void parseFailedResult(Response response) {
    }

    @Override // com.abupdate.iot_libs.engine.network.base.PostRequest
    public Void parseSuccessResult(Response response) {
        return null;
    }

    public ReportErrorLogRequest(Context context, CommonResponse commonResponse) {
        super(context, commonResponse);
    }

    public ReportErrorLogRequest setErrorFileParamInfo(ErrorFileParamInfo errorFileParamInfo) {
        this.errorFileParamInfo = errorFileParamInfo;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.abupdate.iot_libs.engine.network.base.Request
    public Response doRequest() {
        if (this.errorFileParamInfo == null) {
            ((CommonResponse) this.baseResponse).setErrorCode(1003);
            return null;
        } else if (!new File(this.errorFileParamInfo.uploadFile).exists()) {
            ((CommonResponse) this.baseResponse).setErrorCode(204);
            return null;
        } else {
            try {
                DataManager.getInstance().getMainEntity().getRegisterInfo();
                throw null;
            } catch (FotaException e) {
                e.printStackTrace();
                ((CommonResponse) this.baseResponse).setErrorCode(e.getReasonCode());
                return null;
            }
        }
    }
}
