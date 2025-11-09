package com.android.settings.ipc;

import com.syu.remote.Message;
/* loaded from: classes.dex */
public interface IpcNotify {
    void notifyAmp(Message message);

    void notifyBt(Message message);

    void notifyCanbox(Message message);

    void notifyCanbus(Message message);

    void notifyDvd(Message message);

    void notifyDvr(Message message);

    void notifyGesture(Message message);

    void notifyGsensor(Message message);

    void notifyIpod(Message message);

    void notifyMain(Message message);

    void notifyRadio(Message message);

    void notifySensor(Message message);

    void notifySound(Message message);

    void notifyTpms(Message message);

    void notifyTv(Message message);
}
