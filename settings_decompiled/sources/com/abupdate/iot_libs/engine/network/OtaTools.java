package com.abupdate.iot_libs.engine.network;

import com.abupdate.iot_libs.interact.callback.MessageListener;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class OtaTools {
    private static OtaTools mqttTools;
    public Map<Integer, String> reply = new HashMap();
    private MessageListener messageListener = MessageListener.getInstance();
    private State currentState = State.Null;

    /* loaded from: classes.dex */
    public enum State {
        Null,
        Connecting,
        Connected,
        Login,
        Logout,
        Disconnecting,
        Disconnected
    }

    private OtaTools() {
    }

    public static OtaTools getInstance() {
        if (mqttTools == null) {
            synchronized (OtaTools.class) {
                if (mqttTools == null) {
                    mqttTools = new OtaTools();
                }
            }
        }
        return mqttTools;
    }

    public State getState() {
        return this.currentState;
    }

    public void setState(State state) {
        this.currentState = state;
    }
}
