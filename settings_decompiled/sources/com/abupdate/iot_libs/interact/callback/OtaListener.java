package com.abupdate.iot_libs.interact.callback;

import com.abupdate.iot_libs.engine.network.OtaTools;
import com.abupdate.mqtt_libs.connect.MqttManager;
import com.abupdate.mqtt_libs.mqttv3.IMqttActionListener;
import com.abupdate.mqtt_libs.mqttv3.MqttException;
import com.abupdate.trace.Trace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class OtaListener implements IMqttActionListener {
    private static OtaListener mInstance;
    Object mLock = new Object();
    private boolean subSuccess = true;
    private int reconnectTime = 3;
    private Map<Enum, List<IListener>> listeners = new HashMap();

    /* loaded from: classes.dex */
    public enum Action {
        CONNECT,
        DISCONNECT,
        PUB_LOGIN,
        PUB_LOGOUT,
        SUB
    }

    public static OtaListener getInstance() {
        if (mInstance == null) {
            synchronized (OtaListener.class) {
                if (mInstance == null) {
                    mInstance = new OtaListener();
                }
            }
        }
        return mInstance;
    }

    private OtaListener() {
    }

    private List<IListener> getListener(Action action) {
        return this.listeners.get(action);
    }

    public void connect(Throwable th) {
        OtaTools.getInstance().setState(OtaTools.State.Disconnected);
        List<IListener> listener = getListener(Action.CONNECT);
        if (listener != null) {
            Iterator<IListener> it = listener.iterator();
            while (it.hasNext()) {
                ((IStatusListener) it.next()).onError(getError(th));
            }
        }
        if (th != null) {
            th.printStackTrace();
            Trace.e("OtaListener", "connect() " + th.toString());
        }
        if (MqttManager.getInstance().isConnected() || OtaTools.getInstance().getState() == OtaTools.State.Connecting || 5 == getError(th) || 4 == getError(th) || this.reconnectTime <= 0) {
            return;
        }
        MqttManager.getInstance().keepConnect(1800000L, System.currentTimeMillis() + 600000);
        this.reconnectTime--;
    }

    private int getError(Throwable th) {
        if (th instanceof MqttException) {
            return ((MqttException) th).getReasonCode();
        }
        return 6;
    }
}
