package com.abupdate.iot_libs.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.SystemClock;
import com.abupdate.iot_libs.MqttAgentPolicy;
import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.network.OtaTools;
import com.abupdate.iot_libs.engine.report.ReportManager;
import com.abupdate.iot_libs.engine.sota.task.SotaReportTask;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.engine.thread.NamedRunnable;
import com.abupdate.iot_libs.utils.SPFTool;
import com.abupdate.trace.Trace;
/* loaded from: classes.dex */
public class JobSchedulerService extends JobService {
    public static final String TAG = JobSchedulerService.class.getSimpleName();

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Trace.d(TAG, "onCreate() ");
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        String str = TAG;
        Trace.d(str, "onStartJob() ");
        if (OtaAgentPolicy.sCx == null) {
            return true;
        }
        if (DataManager.getInstance().shouldUsePush()) {
            Trace.d(str, "onStartJob() should establish mqtt connect");
            if (!MqttAgentPolicy.isConnected() && OtaTools.getInstance().getState() != OtaTools.State.Connecting) {
                MqttAgentPolicy.connect();
            }
        }
        if (queryReport() > 0) {
            ReportManager.getInstance().reportAsy();
        }
        if (OtaAgentPolicy.getParamsController().getParams().useSota) {
            SotaReportTask.getInstance().queryAndReport();
        }
        Dispatcher.getDispatcher().enqueue(new NamedRunnable("JobService finish task ", new Object[0]) { // from class: com.abupdate.iot_libs.service.JobSchedulerService.1
            @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
            protected void execute() {
                SystemClock.sleep(30000L);
                boolean z = true;
                if (DataManager.getInstance().shouldUsePush() && !MqttAgentPolicy.isConnected()) {
                    JobSchedulerService.this.jobFinished(jobParameters, true);
                }
                int queryReport = JobSchedulerService.this.queryReport();
                int querySize = OtaAgentPolicy.getParamsController().getParams().useSota ? SotaReportTask.getInstance().querySize() : 0;
                if (queryReport <= 0 && querySize <= 0) {
                    z = false;
                }
                JobSchedulerService.this.jobFinished(jobParameters, z);
            }
        });
        if (System.currentTimeMillis() - SPFTool.getLong("spf_static_check_version_cycle", -1L) >= 259200000) {
            SPFTool.putLong("spf_static_check_version_cycle", System.currentTimeMillis());
            for (final OtaEntity otaEntity : DataManager.getInstance().getOtaEntities()) {
                Dispatcher.getDispatcher().enqueue(new NamedRunnable("Daily Active Statistics", new Object[0]) { // from class: com.abupdate.iot_libs.service.JobSchedulerService.2
                    @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
                    protected void execute() {
                        otaEntity.getProductInfo();
                    }
                });
            }
        }
        Trace.d(TAG, "每次开机是否执行");
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        Trace.d(TAG, "onStopJob() ");
        if (OtaAgentPolicy.sCx == null) {
            return false;
        }
        if (queryReport() > 0) {
            return true;
        }
        return OtaAgentPolicy.getParamsController().getParams().useSota && SotaReportTask.getInstance().querySize() > 0;
    }

    public int queryReport() {
        return ReportManager.getInstance().queryReport();
    }
}
