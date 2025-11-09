package com.abupdate.iot_libs.engine.sota;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.remote.SotaReportInfo;
import com.abupdate.iot_libs.engine.network.base.BaseRequestStack;
import com.abupdate.iot_libs.engine.network.request.SotaReportRequest;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import java.util.List;
/* loaded from: classes.dex */
public class RequestStack extends BaseRequestStack {
    private static RequestStack mInstance;

    public static RequestStack getInstance() {
        if (mInstance == null) {
            synchronized (RequestStack.class) {
                if (mInstance == null) {
                    mInstance = new RequestStack();
                }
            }
        }
        return mInstance;
    }

    public Dispatcher getDispatcher() {
        return Dispatcher.getDispatcher();
    }

    public CommonResponse<Void> report(List<SotaReportInfo> list) {
        return (CommonResponse) doRequest(new SotaReportRequest(OtaAgentPolicy.sCx, new CommonResponse()).addReportInfo(list));
    }
}
