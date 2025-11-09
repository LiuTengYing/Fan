package com.abupdate.iot_libs.engine.network.request;

import android.content.Context;
import com.abupdate.http_libs.response.Response;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.network.base.PostJsonRequest;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.interact.response.CommonResponse;
/* loaded from: classes.dex */
public class MessageArriveRequest extends PostJsonRequest<CommonResponse, Void> {
    private String msgId;

    @Override // com.abupdate.iot_libs.engine.network.base.PostRequest
    public Void parseSuccessResult(Response response) {
        return null;
    }

    public MessageArriveRequest(Context context, CommonResponse commonResponse) {
        super(context, commonResponse);
    }

    public MessageArriveRequest setMessageId(String str) {
        this.msgId = str;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.abupdate.iot_libs.engine.network.base.Request
    public Response doRequest() {
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
