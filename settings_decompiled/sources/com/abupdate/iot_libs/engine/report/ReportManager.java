package com.abupdate.iot_libs.engine.report;

import android.content.Context;
import android.text.TextUtils;
import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.local.DownParamInfo;
import com.abupdate.iot_libs.data.local.ErrorFileParamInfo;
import com.abupdate.iot_libs.data.local.EventTrackParamInfo;
import com.abupdate.iot_libs.data.local.UpgradeParamInfo;
import com.abupdate.iot_libs.data.remote.PushMessageInfo;
import com.abupdate.iot_libs.engine.network.base.BaseRequestStack;
import com.abupdate.iot_libs.engine.network.request.MessageArriveRequest;
import com.abupdate.iot_libs.engine.network.request.ReportDownloadResultRequest;
import com.abupdate.iot_libs.engine.network.request.ReportErrorLogRequest;
import com.abupdate.iot_libs.engine.network.request.ReportUpdateResultRequest;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.engine.thread.RealCall;
import com.abupdate.iot_libs.interact.callback.IReportResultCallback;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.trace.Trace;
import java.io.File;
import java.util.List;
/* loaded from: classes.dex */
public class ReportManager extends BaseRequestStack {
    private static String TAG = "ReportManager";
    private static ReportManager m_instance;
    private final Context m_context;
    private final ReportDBManager m_dbManager;

    public void saveEvent(int i, int i2) {
    }

    private ReportManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.m_context = applicationContext;
        this.m_dbManager = new ReportDBManager(applicationContext);
    }

    public static ReportManager getInstance() {
        if (m_instance == null) {
            synchronized (ReportManager.class) {
                if (m_instance == null) {
                    m_instance = new ReportManager(OtaAgentPolicy.sCx);
                }
            }
        }
        return m_instance;
    }

    public int queryReport() {
        List<UpgradeParamInfo> queryUpgrade = this.m_dbManager.queryUpgrade();
        List<EventTrackParamInfo> queryEventTrack = this.m_dbManager.queryEventTrack();
        return queryUpgrade.size() + this.m_dbManager.queryDown().size() + this.m_dbManager.queryPushData().size() + this.m_dbManager.queryErrorLogData().size() + queryEventTrack.size();
    }

    public void reportAsy() {
        if (getInstance().queryReport() == 0) {
            Trace.d(TAG, "report() do not have data to be reported!");
            return;
        }
        Trace.d(TAG, "reportAsy() start");
        Dispatcher.getDispatcher().enqueue(RealCall.getInstance().genReportAsy());
    }

    public synchronized void report() {
        List<DownParamInfo> queryDown = this.m_dbManager.queryDown();
        int size = queryDown.size();
        if (size > 0) {
            String str = TAG;
            Trace.d(str, "check the local report download: " + size);
        }
        for (final DownParamInfo downParamInfo : queryDown) {
            reportDown(downParamInfo, new IReportResultCallback() { // from class: com.abupdate.iot_libs.engine.report.ReportManager.1
                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportSuccess() {
                    Trace.d(ReportManager.TAG, "onReportSuccess() report down.");
                    ReportManager.this.m_dbManager.delete(downParamInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportFail() {
                    Trace.d(ReportManager.TAG, "onReportFail() report down.");
                    ReportManager.this.m_dbManager.delete(downParamInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportNetFail() {
                    Trace.d(ReportManager.TAG, "onReportNetFail() report down.");
                }
            });
        }
        List<UpgradeParamInfo> queryUpgrade = this.m_dbManager.queryUpgrade();
        int size2 = queryUpgrade.size();
        if (size2 > 0) {
            String str2 = TAG;
            Trace.d(str2, "check the local report upgrade: " + size2);
        }
        for (final UpgradeParamInfo upgradeParamInfo : queryUpgrade) {
            reportUpgrade(upgradeParamInfo, new IReportResultCallback() { // from class: com.abupdate.iot_libs.engine.report.ReportManager.2
                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportSuccess() {
                    Trace.d(ReportManager.TAG, "onReportSuccess() upgrade");
                    ReportManager.this.m_dbManager.delete(upgradeParamInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportFail() {
                    Trace.d(ReportManager.TAG, "onReportFail() upgrade.");
                    ReportManager.this.m_dbManager.delete(upgradeParamInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportNetFail() {
                    Trace.d(ReportManager.TAG, "onReportNetFail() upgrade.");
                }
            });
        }
        this.m_dbManager.queryEventTrack();
        List<PushMessageInfo> queryPushData = this.m_dbManager.queryPushData();
        int size3 = queryPushData.size();
        if (size3 > 0) {
            String str3 = TAG;
            Trace.d(str3, "check push message data:" + size3);
        }
        for (final PushMessageInfo pushMessageInfo : queryPushData) {
            reportPushData(pushMessageInfo, new IReportResultCallback() { // from class: com.abupdate.iot_libs.engine.report.ReportManager.3
                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportSuccess() {
                    Trace.d(ReportManager.TAG, "onReportSuccess() push");
                    ReportManager.this.m_dbManager.delete(pushMessageInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportFail() {
                    Trace.d(ReportManager.TAG, "onReportFail() push");
                    ReportManager.this.m_dbManager.delete(pushMessageInfo);
                }

                @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                public void onReportNetFail() {
                    Trace.d(ReportManager.TAG, "onReportNetFail() push");
                }
            });
        }
        List<ErrorFileParamInfo> queryErrorLogData = this.m_dbManager.queryErrorLogData();
        int size4 = queryErrorLogData.size();
        if (size4 > 0) {
            String str4 = TAG;
            Trace.d(str4, "check error log report data:" + size4);
            for (final ErrorFileParamInfo errorFileParamInfo : queryErrorLogData) {
                reportErrorLog(errorFileParamInfo, new IReportResultCallback() { // from class: com.abupdate.iot_libs.engine.report.ReportManager.4
                    @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                    public void onReportSuccess() {
                        Trace.d(ReportManager.TAG, "onReportSuccess() error log");
                        ReportManager.this.m_dbManager.delete(errorFileParamInfo);
                        String str5 = ReportManager.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("delete file path: ");
                        sb.append(errorFileParamInfo.uploadFile);
                        sb.append(", result: ");
                        sb.append(new File(errorFileParamInfo.uploadFile).delete() ? "success" : "failed");
                        Trace.d(str5, sb.toString());
                    }

                    @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                    public void onReportFail() {
                        Trace.d(ReportManager.TAG, "onReportFail() error log");
                        ReportManager.this.m_dbManager.delete(errorFileParamInfo);
                        String str5 = ReportManager.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("delete file path: ");
                        sb.append(errorFileParamInfo.uploadFile);
                        sb.append(", result: ");
                        sb.append(new File(errorFileParamInfo.uploadFile).delete() ? "success" : "failed");
                        Trace.d(str5, sb.toString());
                    }

                    @Override // com.abupdate.iot_libs.interact.callback.IReportResultCallback
                    public void onReportNetFail() {
                        Trace.d(ReportManager.TAG, "onReportNetFail() error log");
                        if (TextUtils.isEmpty(errorFileParamInfo.deltaID)) {
                            ReportManager.this.m_dbManager.delete(errorFileParamInfo);
                            String str5 = ReportManager.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("delete file path: ");
                            sb.append(errorFileParamInfo.uploadFile);
                            sb.append(", result: ");
                            sb.append(new File(errorFileParamInfo.uploadFile).delete() ? "success" : "failed");
                            Trace.d(str5, sb.toString());
                        }
                    }
                });
            }
        }
    }

    private void reportErrorLog(ErrorFileParamInfo errorFileParamInfo, IReportResultCallback iReportResultCallback) {
        ReportErrorLogRequest reportErrorLogRequest = new ReportErrorLogRequest(OtaAgentPolicy.sCx, new CommonResponse());
        reportErrorLogRequest.setErrorFileParamInfo(errorFileParamInfo);
        CommonResponse commonResponse = (CommonResponse) doRequest(reportErrorLogRequest);
        if (commonResponse.isOK) {
            iReportResultCallback.onReportSuccess();
        } else if (commonResponse.isNetError()) {
            iReportResultCallback.onReportNetFail();
        } else {
            iReportResultCallback.onReportFail();
        }
    }

    public void reportDown(DownParamInfo downParamInfo, IReportResultCallback iReportResultCallback) {
        ReportDownloadResultRequest reportDownloadResultRequest = new ReportDownloadResultRequest(downParamInfo.productId, OtaAgentPolicy.sCx, new CommonResponse());
        reportDownloadResultRequest.setDownParamInfo(downParamInfo);
        CommonResponse commonResponse = (CommonResponse) doRequest(reportDownloadResultRequest);
        if (commonResponse.isOK) {
            iReportResultCallback.onReportSuccess();
        } else if (commonResponse.isNetError()) {
            iReportResultCallback.onReportNetFail();
        } else {
            iReportResultCallback.onReportFail();
        }
    }

    public void reportUpgrade(UpgradeParamInfo upgradeParamInfo, IReportResultCallback iReportResultCallback) {
        ReportUpdateResultRequest reportUpdateResultRequest = new ReportUpdateResultRequest(upgradeParamInfo.productId, OtaAgentPolicy.sCx, new CommonResponse());
        reportUpdateResultRequest.setUpgradeParamInfo(upgradeParamInfo);
        CommonResponse commonResponse = (CommonResponse) doRequest(reportUpdateResultRequest);
        if (commonResponse.isOK) {
            iReportResultCallback.onReportSuccess();
        } else if (commonResponse.isNetError()) {
            iReportResultCallback.onReportNetFail();
        } else {
            iReportResultCallback.onReportFail();
        }
    }

    public void reportPushData(PushMessageInfo pushMessageInfo, IReportResultCallback iReportResultCallback) {
        MessageArriveRequest messageArriveRequest = new MessageArriveRequest(OtaAgentPolicy.sCx, new CommonResponse());
        messageArriveRequest.setMessageId(pushMessageInfo.msgId);
        CommonResponse commonResponse = (CommonResponse) doRequest(messageArriveRequest);
        if (commonResponse.isOK) {
            iReportResultCallback.onReportSuccess();
        } else if (commonResponse.isNetError()) {
            iReportResultCallback.onReportNetFail();
        } else {
            iReportResultCallback.onReportFail();
        }
    }
}
