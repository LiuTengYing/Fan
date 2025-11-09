package com.abupdate.iot_libs.engine.network.request;

import android.content.Context;
import com.abupdate.http_libs.response.Response;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.data.remote.SotaReportInfo;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.network.base.PostJsonRequest;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SotaReportRequest extends PostJsonRequest<CommonResponse<Void>, Void> {
    private List<SotaReportInfo> infos;

    @Override // com.abupdate.iot_libs.engine.network.base.PostRequest
    public Void parseSuccessResult(Response response) {
        return null;
    }

    public SotaReportRequest(Context context, CommonResponse<Void> commonResponse) {
        super(context, commonResponse);
        this.infos = new ArrayList();
    }

    public SotaReportRequest addReportInfo(List<SotaReportInfo> list) {
        if (list != null) {
            this.infos.addAll(list);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.abupdate.iot_libs.engine.network.base.Request
    public Response doRequest() {
        try {
            OtaEntity mainEntity = DataManager.getInstance().getMainEntity();
            mainEntity.getProductInfo();
            mainEntity.getRegisterInfo();
            throw null;
        } catch (FotaException e) {
            e.printStackTrace();
            ((CommonResponse) this.baseResponse).setErrorCode(e.getReasonCode());
            return null;
        }
    }
}
