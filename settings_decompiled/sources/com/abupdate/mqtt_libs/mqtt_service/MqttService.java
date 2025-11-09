package com.abupdate.mqtt_libs.mqtt_service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import com.abupdate.mqtt_libs.connect.MqttManager;
import com.abupdate.mqtt_libs.connect.NetUtils;
import com.abupdate.trace.Trace;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@SuppressLint({"Registered"})
/* loaded from: classes.dex */
public class MqttService extends Service implements MqttTraceHandler {
    private c keepConnectReceiver;
    private long keepConnectRepeatTime;
    private AlarmManager mAm;
    com.abupdate.mqtt_libs.mqtt_service.c messageStore;
    private e mqttServiceBinder;
    private d networkConnectionMonitor;
    private PendingIntent operation;
    private String traceCallbackId;
    private boolean traceEnabled = false;
    private volatile boolean backgroundDataEnabled = true;
    private Map<String, com.abupdate.mqtt_libs.mqtt_service.d> connections = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class c extends BroadcastReceiver {
        public c() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("ACTION_ALARM_KEEP_CONNECT")) {
                Trace.d("MqttService", "onReceive() receive alarm_connect_engine's message");
                if (!MqttManager.getInstance().isConnected()) {
                    if (MqttService.this.mAm != null && MqttService.this.operation != null) {
                        MqttService.this.mAm.setWindow(0, System.currentTimeMillis() + MqttService.this.keepConnectRepeatTime, 5000L, MqttService.this.operation);
                    }
                    if (NetUtils.isNetWorkAvailable()) {
                        Trace.d("MqttService", "onReceive() try to reconnect");
                        MqttService.this.reconnect();
                        return;
                    }
                    Trace.d("MqttService", "onReceive() The current network is not available");
                    return;
                }
                Trace.d("MqttService", "onReceive() socket have connected");
                MqttService.this.stopKeepConnect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class d extends BroadcastReceiver {
        private d() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MqttService.this.traceDebug("MqttService", "Internal network status receive.");
            PowerManager.WakeLock newWakeLock = ((PowerManager) MqttService.this.getSystemService("power")).newWakeLock(1, "MQTT");
            newWakeLock.acquire();
            MqttService.this.traceDebug("MqttService", "Reconnect for Network recovery.");
            if (!MqttService.this.isOnline()) {
                MqttService.this.notifyClientsOffline();
            } else {
                MqttService.this.traceDebug("MqttService", "Online,reconnect.");
                MqttService.this.reconnect();
            }
            newWakeLock.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyClientsOffline() {
        for (com.abupdate.mqtt_libs.mqtt_service.d dVar : this.connections.values()) {
            dVar.i();
        }
    }

    private void registerBroadcastReceivers() {
        if (this.networkConnectionMonitor == null) {
            d dVar = new d();
            this.networkConnectionMonitor = dVar;
            registerReceiver(dVar, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
        if (this.keepConnectReceiver == null) {
            c cVar = new c();
            this.keepConnectReceiver = cVar;
            registerReceiver(cVar, new IntentFilter("ACTION_ALARM_KEEP_CONNECT"));
        }
    }

    private void traceCallback(String str, String str2, String str3) {
        if (this.traceCallbackId == null || !this.traceEnabled) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("MqttService.callbackAction", "trace");
        bundle.putString("MqttService.traceSeverity", str);
        bundle.putString("MqttService.traceTag", str2);
        bundle.putString("MqttService.errorMessage", str3);
        callbackToActivity(this.traceCallbackId, f.ERROR, bundle);
    }

    private void unregisterBroadcastReceivers() {
        d dVar = this.networkConnectionMonitor;
        if (dVar != null) {
            unregisterReceiver(dVar);
            this.networkConnectionMonitor = null;
        }
        c cVar = this.keepConnectReceiver;
        if (cVar != null) {
            unregisterReceiver(cVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void callbackToActivity(String str, f fVar, Bundle bundle) {
        Intent intent = new Intent("MqttService.callbackToActivity.v0");
        if (str != null) {
            intent.putExtra("MqttService.clientHandle", str);
        }
        intent.putExtra("MqttService.callbackStatus", fVar);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        sendBroadcast(intent);
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected() && this.backgroundDataEnabled;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        this.mqttServiceBinder.a(intent.getStringExtra("MqttService.activityToken"));
        return this.mqttServiceBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mqttServiceBinder = new e(this);
        this.messageStore = new b(this, this);
    }

    @Override // android.app.Service
    public void onDestroy() {
        for (com.abupdate.mqtt_libs.mqtt_service.d dVar : this.connections.values()) {
            dVar.a((String) null, (String) null);
        }
        if (this.mqttServiceBinder != null) {
            this.mqttServiceBinder = null;
        }
        unregisterBroadcastReceivers();
        com.abupdate.mqtt_libs.mqtt_service.c cVar = this.messageStore;
        if (cVar != null) {
            cVar.close();
        }
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        registerBroadcastReceivers();
        return 1;
    }

    void reconnect() {
        traceDebug("MqttService", "Reconnect to server, client size=" + this.connections.size());
        for (com.abupdate.mqtt_libs.mqtt_service.d dVar : this.connections.values()) {
            traceDebug("Reconnect Client:", dVar.e() + '/' + dVar.g());
            if (isOnline()) {
                dVar.j();
            }
        }
    }

    public void stopKeepConnect() {
        PendingIntent pendingIntent;
        AlarmManager alarmManager = this.mAm;
        if (alarmManager == null || (pendingIntent = this.operation) == null) {
            return;
        }
        alarmManager.cancel(pendingIntent);
    }

    @Override // com.abupdate.mqtt_libs.mqtt_service.MqttTraceHandler
    public void traceDebug(String str, String str2) {
        traceCallback("debug", str, str2);
    }

    public void traceError(String str, String str2) {
        traceCallback("error", str, str2);
    }

    @Override // com.abupdate.mqtt_libs.mqtt_service.MqttTraceHandler
    public void traceException(String str, String str2, Exception exc) {
        if (this.traceCallbackId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("MqttService.callbackAction", "trace");
            bundle.putString("MqttService.traceSeverity", "exception");
            bundle.putString("MqttService.errorMessage", str2);
            bundle.putSerializable("MqttService.exception", exc);
            bundle.putString("MqttService.traceTag", str);
            callbackToActivity(this.traceCallbackId, f.ERROR, bundle);
        }
    }
}
