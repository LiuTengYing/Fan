package com.abupdate.iot_libs.engine.network.request;

import android.content.Context;
import com.abupdate.http_libs.response.Response;
import com.abupdate.iot_libs.data.local.DownParamInfo;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.network.base.PostJsonRequest;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.interact.response.CommonResponse;
/* loaded from: classes.dex */
public class ReportDownloadResultRequest extends PostJsonRequest<CommonResponse, Void> {
    private DownParamInfo downParamInfo;

    @Override // com.abupdate.iot_libs.engine.network.base.PostRequest
    public Void parseSuccessResult(Response response) {
        return null;
    }

    public ReportDownloadResultRequest(String str, Context context, CommonResponse commonResponse) {
        super(context, commonResponse);
        setProductId(str);
    }

    public void setDownParamInfo(DownParamInfo downParamInfo) {
        this.downParamInfo = downParamInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.abupdate.iot_libs.engine.network.base.Request
    public Response doRequest() {
        if (this.downParamInfo == null) {
            ((CommonResponse) this.baseResponse).setErrorCode(1003);
            return null;
        }
        try {
            DataManager.getInstance().getEntityByProduct(this.downParamInfo.productId).getRegisterInfo();
            throw null;
        } catch (FotaException e) {
            e.printStackTrace();
            ((CommonResponse) this.baseResponse).setErrorCode(e.getReasonCode());
            return null;
        }
    }
}
