package com.abupdate.iot_libs.interact.callback;

import android.content.Context;
import com.abupdate.iot_libs.OtaAgentPolicy;
/* loaded from: classes.dex */
public class MessageListener {
    private static MessageListener messageListener;
    private boolean timeout = true;
    private State currentState = State.Null;
    private Context mCx = OtaAgentPolicy.sCx;

    /* loaded from: classes.dex */
    enum State {
        Null,
        Login,
        Logout,
        ReportDeviceInfo
    }

    private MessageListener() {
    }

    public static MessageListener getInstance() {
        if (messageListener == null) {
            synchronized (MessageListener.class) {
                if (messageListener == null) {
                    messageListener = new MessageListener();
                }
            }
        }
        return messageListener;
    }
}
