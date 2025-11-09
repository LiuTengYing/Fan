package com.abupdate.iot_libs;

import android.text.TextUtils;
import com.abupdate.iot_libs.engine.network.OtaTools;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.engine.thread.RealCall;
import com.abupdate.iot_libs.interact.callback.OtaListener;
import com.abupdate.iot_libs.utils.SPFTool;
import com.abupdate.mqtt_libs.connect.MqttManager;
import com.abupdate.mqtt_libs.mqttv3.MqttException;
import com.abupdate.trace.Trace;
import java.util.Calendar;
/* loaded from: classes.dex */
public class MqttAgentPolicy {
    public static void connect() {
        Trace.d("MqttAgentPolicy", "%s%s%s", "--------------------------", "connect", "--------------------------");
        if (MqttManager.getInstance().isConnected()) {
            Trace.d("MqttAgentPolicy", "connect() is connected");
            OtaListener.getInstance().connect(new MqttException(new Throwable("is connected")));
            return;
        }
        OtaTools.State state = OtaTools.getInstance().getState();
        OtaTools.State state2 = OtaTools.State.Connecting;
        if (state == state2) {
            Trace.d("MqttAgentPolicy", "connect() is connecting");
            OtaListener.getInstance().connect(new MqttException(new Throwable("is connecting")));
            return;
        }
        OtaTools.getInstance().setState(state2);
        Dispatcher.getDispatcher().enqueue(RealCall.getInstance().genMqttConnectAsy());
    }

    public static boolean isConnected() {
        return MqttManager.getInstance().isConnected();
    }

    public static boolean isMqttTime() {
        String string = SPFTool.getString("sp_mqtt_policy", "");
        if (!TextUtils.isEmpty(string)) {
            string = string.replace(" ", "");
        }
        boolean z = true;
        if (TextUtils.isEmpty(string) || "null".equals(string) || "0024".equals(string)) {
            Trace.d("MqttAgentPolicy", "policy is null, mqtt should connect all time");
            return true;
        }
        int i = Calendar.getInstance().get(11);
        if (string.length() == 4) {
            int parseInt = Integer.parseInt(string.substring(0, 2));
            int parseInt2 = Integer.parseInt(string.substring(2, 4));
            if (i < parseInt || i >= parseInt2) {
                z = false;
            }
        } else {
            boolean z2 = false;
            for (String str : string.split(",")) {
                if (i == Integer.parseInt(str) - 1) {
                    z2 = true;
                }
            }
            z = z2;
        }
        Trace.d("MqttAgentPolicy", "is  mqtt time: " + z);
        return z;
    }
}
