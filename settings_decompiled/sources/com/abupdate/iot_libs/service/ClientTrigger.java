package com.abupdate.iot_libs.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.local.FotaParamController;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.data.remote.VersionInfo;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.otaStatus.OtaStatusMgr;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.engine.thread.NamedRunnable;
import com.abupdate.iot_libs.engine.trigger.CheckPeriod;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.iot_libs.utils.NetUtils;
import com.abupdate.trace.Trace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/* loaded from: classes.dex */
public class ClientTrigger extends Service {
    public static boolean isInit;
    private AlarmCheckReceiver alarmCheckReceiver;
    private boolean checkVersionIsRun = false;
    private long lastNotifyTime;
    private NetworkReceiver networkReceiver;
    private PushReceiver pushReceiver;
    private RebootCheckReceiver rebootCheckReceiver;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        registerBroadcastReceivers();
        Trace.d("ClientTrigger", "ClientTrigger onStartCommand");
        isInit = true;
        return 1;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Trace.d("ClientTrigger", "ClientTrigger onCreate");
    }

    @Override // android.app.Service
    public void onDestroy() {
        unRegisterBroadcastReceivers();
        Trace.d("ClientTrigger", "ClientTrigger onDestroy");
        isInit = false;
        super.onDestroy();
    }

    private void registerBroadcastReceivers() {
        if (this.alarmCheckReceiver == null) {
            this.alarmCheckReceiver = new AlarmCheckReceiver();
        }
        if (this.pushReceiver == null) {
            this.pushReceiver = new PushReceiver();
        }
        if (this.networkReceiver == null) {
            this.networkReceiver = new NetworkReceiver();
        }
        if (this.rebootCheckReceiver == null) {
            this.rebootCheckReceiver = new RebootCheckReceiver();
        }
        registerReceiver(this.alarmCheckReceiver, new IntentFilter("ACTION_COM_ABUPDATE_ALARM_CHECK_VERSION"), 4);
        registerReceiver(this.alarmCheckReceiver, new IntentFilter("ACTION_COM_ABUPDATE_ALARM_REMIND_INSTALL"), 4);
        registerReceiver(this.networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"), 4);
        registerReceiver(this.pushReceiver, new IntentFilter("action_fota_notify"), 4);
        registerReceiver(this.rebootCheckReceiver, new IntentFilter("action_fota_check"), 4);
    }

    private void unRegisterBroadcastReceivers() {
        AlarmCheckReceiver alarmCheckReceiver = this.alarmCheckReceiver;
        if (alarmCheckReceiver != null) {
            unregisterReceiver(alarmCheckReceiver);
        }
        PushReceiver pushReceiver = this.pushReceiver;
        if (pushReceiver != null) {
            unregisterReceiver(pushReceiver);
        }
        NetworkReceiver networkReceiver = this.networkReceiver;
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
        RebootCheckReceiver rebootCheckReceiver = this.rebootCheckReceiver;
        if (rebootCheckReceiver != null) {
            unregisterReceiver(rebootCheckReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AlarmCheckReceiver extends BroadcastReceiver {
        private AlarmCheckReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("ACTION_COM_ABUPDATE_ALARM_REMIND_INSTALL".equals(action)) {
                ClientTrigger.this.remindInstallTask();
            } else if ("ACTION_COM_ABUPDATE_ALARM_CHECK_VERSION".equals(action)) {
                if (!OtaAgentPolicy.getParamsController().getParams().rebootCheckTrigger) {
                    Trace.d("ClientTrigger", "Alarm chech not start:rebootCheckTrigger:" + OtaAgentPolicy.getParamsController().getParams().rebootCheckTrigger);
                    return;
                }
                Trace.d("ClientTrigger", "AlarmCheckReceiver receiver alarm-cycle-check-task ");
                if (FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism && OtaStatusMgr.getInstance().isUpgrading()) {
                    Trace.d("ClientTrigger", "Now is upgrading, will ignore");
                } else if (NetUtils.isNetWorkAvailable()) {
                    CheckPeriod.resetPeriod();
                    ClientTrigger.this.checkVersion();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NetworkReceiver extends BroadcastReceiver {
        private NetworkReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && CheckPeriod.isArrived() && NetUtils.isNetWorkAvailable()) {
                ClientTrigger.this.checkVersion();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PushReceiver extends BroadcastReceiver {
        private PushReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("action_fota_notify".equals(intent.getAction())) {
                Trace.d("ClientTrigger", "AlarmCheckReceiver receiver push-check-task ");
                ClientTrigger.this.checkVersion();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RebootCheckReceiver extends BroadcastReceiver {
        private RebootCheckReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("action_fota_check".equals(intent.getAction())) {
                Trace.d("ClientTrigger", "开机执行一次检测");
                ClientTrigger.this.checkVersion();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkVersion() {
        if (this.checkVersionIsRun) {
            Trace.e("ClientTrigger", "checkVersion is run ");
        } else if (NetUtils.isNetWorkAvailable()) {
            Dispatcher.getDispatcher().enqueue(new NamedRunnable("Trigger CheckVersion Task", new Object[0]) { // from class: com.abupdate.iot_libs.service.ClientTrigger.1
                @Override // com.abupdate.iot_libs.engine.thread.NamedRunnable
                protected void execute() {
                    ClientTrigger.this.checkVersionIsRun = true;
                    try {
                        if (!FotaParamController.getInstance().getParams().useDefaultClientStatusMechanism) {
                            ClientTrigger.this.checkAndNotify();
                        } else {
                            OtaStatusMgr otaStatusMgr = OtaStatusMgr.getInstance();
                            if (!otaStatusMgr.isIdle() && !otaStatusMgr.isCheckNewVersion()) {
                                if (otaStatusMgr.isNeedReboot()) {
                                    ClientTrigger.this.notifyReboot();
                                } else if (!otaStatusMgr.isChecking() && !otaStatusMgr.isDownloading() && !otaStatusMgr.isUpgrading()) {
                                    CheckPeriod.resetPeriod();
                                    ClientTrigger.this.notifyNewVersion();
                                } else {
                                    Trace.i("ClientTrigger", "check notify task is stopped,because OTA task is running: " + otaStatusMgr.getCurStatus().name());
                                }
                            }
                            ClientTrigger.this.checkAndNotify();
                        }
                    } finally {
                        ClientTrigger.this.checkVersionIsRun = false;
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndNotify() {
        List<VersionInfo> list;
        Trace.d("ClientTrigger", "checkAndNotify() start check and notify new version");
        CommonResponse<List<VersionInfo>> executed = OtaAgentPolicy.checkVersion().executed();
        Trace.d("ClientTrigger", "check version result:" + executed.isOK);
        if (!executed.isOK || (list = executed.result) == null || list.size() <= 0) {
            return;
        }
        FotaParamController.getInstance().getClass();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyReboot() {
        Trace.d("ClientTrigger", "notifyNewVersion() start notify reboot");
        CheckPeriod.resetPeriod();
        ArrayList<OtaEntity> arrayList = new ArrayList();
        try {
            arrayList.add(DataManager.getInstance().getMainEntity());
        } catch (FotaException e) {
            e.printStackTrace();
        }
        if (arrayList.size() <= 0) {
            Trace.d("ClientTrigger", "not found need reboot entity");
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        for (OtaEntity otaEntity : arrayList) {
            arrayList2.add(otaEntity.getVersionInfo());
        }
        FotaParamController.getInstance().getClass();
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNewVersion() {
        Trace.d("ClientTrigger", "notifyNewVersion() start notify new version");
        long time = new Date().getTime();
        if (time - this.lastNotifyTime < 300000) {
            Trace.d("ClientTrigger", "notifyNewVersion() last notify time is less than 5 minute");
            return;
        }
        this.lastNotifyTime = time;
        DataManager.getInstance().getNeedNotifyNewVersionEntity();
        FotaParamController.getInstance().getClass();
        Trace.d("ClientTrigger", "notifyNewVersion() is end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void remindInstallTask() {
        Trace.d("ClientTrigger", "remindInstallTask() start remind task");
        if (!OtaStatusMgr.getInstance().isDownloadFinished()) {
            Trace.d("ClientTrigger", "remindInstallTask() is not download finish");
            return;
        }
        DataManager.getInstance().getNeedNotifyNewVersionEntity();
        FotaParamController.getInstance().getClass();
        Trace.d("ClientTrigger", "remindInstallTask() there is not version info");
    }
}
