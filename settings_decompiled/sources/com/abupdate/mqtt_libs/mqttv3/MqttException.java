package com.abupdate.mqtt_libs.mqttv3;

import a.m;
/* loaded from: classes.dex */
public class MqttException extends Exception {
    private static final long serialVersionUID = 300;
    private Throwable cause;
    private int reasonCode;

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return m.b(this.reasonCode);
    }

    public int getReasonCode() {
        return this.reasonCode;
    }

    @Override // java.lang.Throwable
    public String toString() {
        String str = getMessage() + " (" + this.reasonCode + ")";
        if (this.cause != null) {
            return str + " - " + this.cause.toString();
        }
        return str;
    }

    public MqttException(Throwable th) {
        this.reasonCode = 0;
        this.cause = th;
    }

    public MqttException(int i, Throwable th) {
        this.reasonCode = i;
        this.cause = th;
    }
}
