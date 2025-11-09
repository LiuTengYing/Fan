package com.abupdate.mqtt_libs.mqtt_service;

import android.os.Binder;
/* loaded from: classes.dex */
class e extends Binder {

    /* renamed from: a  reason: collision with root package name */
    private MqttService f9a;
    private String b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public e(MqttService mqttService) {
        this.f9a = mqttService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void a(String str) {
        this.b = str;
    }
}
