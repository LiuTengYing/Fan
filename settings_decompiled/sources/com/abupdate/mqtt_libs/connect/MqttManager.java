package com.abupdate.mqtt_libs.connect;

import android.content.Context;
/* loaded from: classes.dex */
public class MqttManager {
    private static MqttManager instance;
    public static Context sCx;
    private boolean alarmIsAvailable = true;
    private ConnectCommand mConnect;

    private MqttManager() {
    }

    public static synchronized MqttManager getInstance() {
        MqttManager mqttManager;
        synchronized (MqttManager.class) {
            if (instance == null) {
                synchronized (MqttManager.class) {
                    if (instance == null) {
                        instance = new MqttManager();
                    }
                }
            }
            mqttManager = instance;
        }
        return mqttManager;
    }

    public ConnectCommand getConnect() {
        return this.mConnect;
    }

    public boolean isConnected() {
        if (getConnect() == null || getConnect().getClient() == null) {
            return false;
        }
        try {
            return getConnect().getClient().isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void keepConnect(long j, long j2) {
        if (getConnect() == null || getConnect().getClient() == null) {
            return;
        }
        try {
            getConnect().getClient().startKeepConnect(j, j2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
