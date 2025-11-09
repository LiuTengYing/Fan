package com.abupdate.iot_libs.engine.thread;

import android.os.Handler;
import android.os.Looper;
import com.abupdate.iot_libs.data.remote.SotaReportInfo;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.report.ReportManager;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.engine.sota.RequestStack;
import com.abupdate.iot_libs.engine.sota.task.SotaReportTask;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.iot_libs.utils.NetUtils;
import com.abupdate.trace.Trace;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RealCall {
    public static RealCall mInstance;
    private Handler handler;

    private RealCall() {
        setCallbcakToMain(new Handler(Looper.getMainLooper()));
    }

    public static RealCall getInstance() {
        if (mInstance == null) {
            synchronized (RealCall.class) {
                if (mInstance == null) {
                    mInstance = new RealCall();
                }
            }
        }
        return mInstance;
    }

    public RealCall setCallbcakToMain(Handler handler) {
        this.handler = handler;
        return this;
    }

    public ReportAsy genReportAsy() {
        return new ReportAsy();
    }

    public ConnectMqttAsy genMqttConnectAsy() {
        return new ConnectMqttAsy();
    }

    /* loaded from: classes.dex */
    public class ReportAsy extends NamedRunnable {
        public ReportAsy() {
            super("FOTA %s", "CheckAllApp");
        }

        @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
        protected void execute() {
            if (NetUtils.isNetWorkAvailable()) {
                ReportManager.getInstance().report();
            }
        }
    }

    /* loaded from: classes.dex */
    public class ConnectMqttAsy extends NamedRunnable {
        public ConnectMqttAsy() {
            super("FOTA %s", "ConnectMqtt");
        }

        @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
        protected void execute() {
            try {
                DataManager.getInstance().getMainEntity().getRegisterInfo();
                throw null;
            } catch (FotaException e) {
                e.printStackTrace();
                Trace.e("RealCall", e);
            }
        }
    }

    public SotaReportAsy genSotaReportAsy() {
        return new SotaReportAsy();
    }

    /* loaded from: classes.dex */
    public class SotaReportAsy extends NamedRunnable {
        private List<SotaReportInfo> infos;

        public SotaReportAsy() {
            super("SOTA %s", "Report Task");
            this.infos = new ArrayList();
        }

        public SotaReportAsy addInfos(List<SotaReportInfo> list) {
            if (list != null) {
                this.infos.addAll(list);
            }
            return this;
        }

        @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
        protected void execute() {
            CommonResponse<Void> report = RequestStack.getInstance().report(this.infos);
            if (report == null || !report.isOK) {
                return;
            }
            for (SotaReportInfo sotaReportInfo : this.infos) {
                SotaReportTask.getInstance().delete(sotaReportInfo);
            }
        }
    }
}
