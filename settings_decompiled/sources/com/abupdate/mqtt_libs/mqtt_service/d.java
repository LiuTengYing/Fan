package com.abupdate.mqtt_libs.mqtt_service;

import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import com.abupdate.mqtt_libs.connect.NetUtils;
import com.abupdate.mqtt_libs.mqttv3.IMqttActionListener;
import com.abupdate.mqtt_libs.mqttv3.MqttException;
import com.abupdate.mqtt_libs.mqttv3.h;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class d {

    /* renamed from: a  reason: collision with root package name */
    private String f6a;
    private String b;
    private h d;
    private String e;
    private String f;
    private com.abupdate.mqtt_libs.mqttv3.e g;
    private com.abupdate.mqtt_libs.mqtt_service.a h;
    private MqttService i;
    private volatile boolean j;
    private boolean k;
    private volatile boolean l;
    private PowerManager.WakeLock q;

    /* loaded from: classes.dex */
    class a extends C0002d {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements IMqttActionListener {
        b() {
        }
    }

    /* loaded from: classes.dex */
    class c extends C0002d {
        final /* synthetic */ Bundle c;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        c(Bundle bundle, Bundle bundle2) {
            super(d.this, bundle, null);
            this.c = bundle2;
        }
    }

    /* renamed from: com.abupdate.mqtt_libs.mqtt_service.d$d  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    private class C0002d implements IMqttActionListener {

        /* renamed from: a  reason: collision with root package name */
        private final Bundle f8a;

        /* synthetic */ C0002d(d dVar, Bundle bundle, a aVar) {
            this(bundle);
        }

        private C0002d(Bundle bundle) {
            this.f8a = bundle;
        }
    }

    private void k() {
        PowerManager.WakeLock wakeLock = this.q;
        if (wakeLock == null || !wakeLock.isHeld()) {
            return;
        }
        this.q.release();
    }

    public void connectionLost(Throwable th) {
        MqttService mqttService = this.i;
        mqttService.traceDebug("MqttConnection", "connectionLost(" + th.getMessage() + ")");
        this.j = true;
        try {
            if (!this.d.n()) {
                this.g.a(null, new b());
            } else {
                this.h.a(100L);
            }
        } catch (Exception unused) {
        }
        Bundle bundle = new Bundle();
        bundle.putString("MqttService.callbackAction", "onConnectionLost");
        bundle.putString("MqttService.errorMessage", th.getMessage());
        if (th instanceof MqttException) {
            bundle.putSerializable("MqttService.exception", th);
        }
        bundle.putString("MqttService.exceptionStack", Log.getStackTraceString(th));
        this.i.callbackToActivity(this.e, f.OK, bundle);
        k();
    }

    public String e() {
        return this.b;
    }

    public String g() {
        return this.f6a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void i() {
        if (this.j || this.k) {
            return;
        }
        connectionLost(new Exception("Android offline"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void j() {
        if (this.g == null) {
            this.i.traceError("MqttConnection", "Reconnect myClient = null. Will not do reconnect");
        } else if (this.l) {
            this.i.traceDebug("MqttConnection", "The client is connecting. Reconnect return directly.");
        } else if (!NetUtils.isNetWorkAvailable()) {
            this.i.traceDebug("MqttConnection", "The network is not reachable. Will not do reconnect");
        } else {
            if (this.d.n()) {
                Log.i("MqttConnection", "Requesting Automatic reconnect using New Java AC");
                Bundle bundle = new Bundle();
                bundle.putString("MqttService.activityToken", this.f);
                bundle.putString("MqttService.invocationContext", null);
                bundle.putString("MqttService.callbackAction", "connect");
                try {
                    this.g.h();
                } catch (MqttException e) {
                    Log.e("MqttConnection", "Exception occurred attempting to reconnect: " + e.getMessage());
                    a(false);
                    a(bundle, e);
                }
                return;
            }
            if (this.j && !this.k) {
                this.i.traceDebug("MqttConnection", "Do Real Reconnect!");
                Bundle bundle2 = new Bundle();
                bundle2.putString("MqttService.activityToken", this.f);
                bundle2.putString("MqttService.invocationContext", null);
                bundle2.putString("MqttService.callbackAction", "connect");
                try {
                    try {
                        this.g.a(this.d, null, new c(bundle2, bundle2));
                        a(true);
                    } catch (Exception e2) {
                        MqttService mqttService = this.i;
                        mqttService.traceError("MqttConnection", "Cannot reconnect to remote server." + e2.getMessage());
                        a(false);
                        a(bundle2, new MqttException(6, e2.getCause()));
                    }
                } catch (MqttException e3) {
                    MqttService mqttService2 = this.i;
                    mqttService2.traceError("MqttConnection", "Cannot reconnect to remote server." + e3.getMessage());
                    a(false);
                    a(bundle2, e3);
                }
            }
            return;
        }
    }

    private void a(Bundle bundle, Exception exc) {
        bundle.putString("MqttService.errorMessage", exc.getLocalizedMessage());
        bundle.putSerializable("MqttService.exception", exc);
        this.i.callbackToActivity(this.e, f.ERROR, bundle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void a(String str, String str2) {
        this.i.traceDebug("MqttConnection", "disconnect()");
        this.j = true;
        Bundle bundle = new Bundle();
        bundle.putString("MqttService.activityToken", str2);
        bundle.putString("MqttService.invocationContext", str);
        bundle.putString("MqttService.callbackAction", "disconnect");
        com.abupdate.mqtt_libs.mqttv3.e eVar = this.g;
        if (eVar != null && eVar.g()) {
            try {
                this.g.a(str, new C0002d(this, bundle, null));
            } catch (Exception e) {
                a(bundle, e);
            }
        } else {
            bundle.putString("MqttService.errorMessage", "not connected");
            this.i.traceError("disconnect", "not connected");
            this.i.callbackToActivity(this.e, f.ERROR, bundle);
        }
        h hVar = this.d;
        if (hVar != null && hVar.o()) {
            this.i.messageStore.b(this.e);
        }
        k();
    }

    private synchronized void a(boolean z) {
        this.l = z;
    }
}
