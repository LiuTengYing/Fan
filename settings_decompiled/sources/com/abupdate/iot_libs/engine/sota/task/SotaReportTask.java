package com.abupdate.iot_libs.engine.sota.task;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.remote.SotaReportInfo;
import com.abupdate.iot_libs.engine.report.SotaReportDBManager;
import com.abupdate.iot_libs.engine.sota.RequestStack;
import com.abupdate.iot_libs.engine.thread.RealCall;
import java.util.List;
/* loaded from: classes.dex */
public class SotaReportTask {
    private static SotaReportTask mInstance;
    private SotaReportDBManager mDbManager = new SotaReportDBManager(OtaAgentPolicy.sCx);

    public static SotaReportTask getInstance() {
        if (mInstance == null) {
            synchronized (SotaReportTask.class) {
                if (mInstance == null) {
                    mInstance = new SotaReportTask();
                }
            }
        }
        return mInstance;
    }

    public void delete(SotaReportInfo sotaReportInfo) {
        this.mDbManager.delete(sotaReportInfo);
    }

    public int querySize() {
        return this.mDbManager.query().size();
    }

    public int queryAndReport() {
        List<SotaReportInfo> query = this.mDbManager.query();
        int size = query == null ? 0 : query.size();
        if (query != null && query.size() > 0) {
            RequestStack.getInstance().getDispatcher().enqueue(RealCall.getInstance().genSotaReportAsy().addInfos(query));
        }
        return size;
    }
}
