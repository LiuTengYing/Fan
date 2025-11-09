package com.android.settings.ipc;

import android.content.Context;
import android.util.Log;
import com.syu.remote.Message;
import com.syu.remote.MessageObserver;
import com.syu.remote.ModuleManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class IpcObj {
    private static IpcObj mIpcObj;
    private AmpObserver amp;
    private BtObserver bt;
    private CanUpObserver canbox;
    private CanbusObserver canbus;
    private DvdObserver dvd;
    private DVRObserver dvr;
    private GestureObserver gesture;
    private GsensorObserver gsensor;
    private IpodObserver ipod;
    private List<IpcNotify> listeners = new ArrayList();
    private MainObserver main;
    private ModuleManager manager;
    private RadioMObserver radio;
    private SensorObserver sensor;
    private SoundObserver sound;
    private TPMSObserver tpms;
    private TVObserver tv;

    public static IpcObj getInstance() {
        if (mIpcObj == null) {
            mIpcObj = new IpcObj();
        }
        return mIpcObj;
    }

    public void setManager(Context context) {
        if (this.manager == null) {
            this.manager = new ModuleManager(context);
        }
    }

    public void init(Context context) {
        if (this.sound == null) {
            this.sound = new SoundObserver();
        }
        if (this.main == null) {
            this.main = new MainObserver();
        }
        if (this.radio == null) {
            this.radio = new RadioMObserver();
        }
        if (this.dvd == null) {
            this.dvd = new DvdObserver();
        }
        if (this.dvr == null) {
            this.dvr = new DVRObserver();
        }
        if (this.tv == null) {
            this.tv = new TVObserver();
        }
        if (this.ipod == null) {
            this.ipod = new IpodObserver();
        }
        if (this.canbus == null) {
            this.canbus = new CanbusObserver();
        }
        if (this.amp == null) {
            this.amp = new AmpObserver();
        }
        if (this.bt == null) {
            this.bt = new BtObserver();
        }
        if (this.gsensor == null) {
            this.gsensor = new GsensorObserver();
        }
        if (this.gesture == null) {
            this.gesture = new GestureObserver();
        }
        if (this.sensor == null) {
            this.sensor = new SensorObserver();
        }
        if (this.tpms == null) {
            this.tpms = new TPMSObserver();
        }
        if (this.canbox == null) {
            this.canbox = new CanUpObserver();
        }
        this.manager.addObserver(this.sound);
        this.manager.addObserver(this.main);
        this.manager.addObserver(this.radio);
        this.manager.addObserver(this.dvd);
        this.manager.addObserver(this.dvr);
        this.manager.addObserver(this.tv);
        this.manager.addObserver(this.ipod);
        this.manager.addObserver(this.canbus);
        this.manager.addObserver(this.amp);
        this.manager.addObserver(this.bt);
        this.manager.addObserver(this.gsensor);
        this.manager.addObserver(this.gesture);
        this.manager.addObserver(this.sensor);
        this.manager.addObserver(this.tpms);
        this.manager.addObserver(this.canbox);
    }

    public void setCmd(int i, int i2, int... iArr) {
        this.manager.sendCmd(i, i2, iArr);
        Log.d("fangli", "setCmd: modleid ===" + i + "        code ====" + i2 + "     params ints =====" + Arrays.toString(iArr));
    }

    public void setNotify(IpcNotify ipcNotify) {
        if (this.listeners != null) {
            Log.d("fangli", "setNotify: " + this.listeners.size());
            this.listeners.add(ipcNotify);
        }
    }

    public boolean isConnect() {
        ModuleManager moduleManager = this.manager;
        return moduleManager != null && moduleManager.isConnected();
    }

    public void setCmd(int i, int i2, int[] iArr, float[] fArr, String[] strArr) {
        this.manager.sendCmd(i, i2, iArr, fArr, strArr);
    }

    public void setCmd(int i, int i2, String str) {
        this.manager.sendCmd(i, i2, null, null, new String[]{str});
        Log.d("fangli", "setCmd: modleid ===" + i + "        code ====" + i2 + "     params strs=====" + str);
    }

    public void setObserverMoudle(int i, int... iArr) {
        this.manager.observeModule(i, iArr);
    }

    public void removeAllObserver() {
        ModuleManager moduleManager = this.manager;
        if (moduleManager == null) {
            return;
        }
        SoundObserver soundObserver = this.sound;
        if (soundObserver != null) {
            moduleManager.removeObserver(soundObserver);
        }
        MainObserver mainObserver = this.main;
        if (mainObserver != null) {
            this.manager.removeObserver(mainObserver);
        }
        RadioMObserver radioMObserver = this.radio;
        if (radioMObserver != null) {
            this.manager.removeObserver(radioMObserver);
        }
        DvdObserver dvdObserver = this.dvd;
        if (dvdObserver != null) {
            this.manager.removeObserver(dvdObserver);
        }
        DVRObserver dVRObserver = this.dvr;
        if (dVRObserver != null) {
            this.manager.removeObserver(dVRObserver);
        }
        TVObserver tVObserver = this.tv;
        if (tVObserver != null) {
            this.manager.removeObserver(tVObserver);
        }
        IpodObserver ipodObserver = this.ipod;
        if (ipodObserver != null) {
            this.manager.removeObserver(ipodObserver);
        }
        CanbusObserver canbusObserver = this.canbus;
        if (canbusObserver != null) {
            this.manager.removeObserver(canbusObserver);
        }
        AmpObserver ampObserver = this.amp;
        if (ampObserver != null) {
            this.manager.removeObserver(ampObserver);
        }
        BtObserver btObserver = this.bt;
        if (btObserver != null) {
            this.manager.removeObserver(btObserver);
        }
        GsensorObserver gsensorObserver = this.gsensor;
        if (gsensorObserver != null) {
            this.manager.removeObserver(gsensorObserver);
        }
        GestureObserver gestureObserver = this.gesture;
        if (gestureObserver != null) {
            this.manager.removeObserver(gestureObserver);
        }
        SensorObserver sensorObserver = this.sensor;
        if (sensorObserver != null) {
            this.manager.removeObserver(sensorObserver);
        }
        TPMSObserver tPMSObserver = this.tpms;
        if (tPMSObserver != null) {
            this.manager.removeObserver(tPMSObserver);
        }
    }

    /* loaded from: classes.dex */
    class SoundObserver extends MessageObserver {
        public SoundObserver() {
            super(4);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifySound(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class MainObserver extends MessageObserver {
        public MainObserver() {
            super(0);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyMain(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class RadioMObserver extends MessageObserver {
        public RadioMObserver() {
            super(1);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyRadio(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class DvdObserver extends MessageObserver {
        public DvdObserver() {
            super(3);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyDvd(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class DVRObserver extends MessageObserver {
        public DVRObserver() {
            super(9);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyDvr(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class TVObserver extends MessageObserver {
        public TVObserver() {
            super(6);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyTv(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class IpodObserver extends MessageObserver {
        public IpodObserver() {
            super(5);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyIpod(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class CanbusObserver extends MessageObserver {
        public CanbusObserver() {
            super(7);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyCanbus(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class AmpObserver extends MessageObserver {
        public AmpObserver() {
            super(15);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyAmp(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class BtObserver extends MessageObserver {
        public BtObserver() {
            super(2);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyBt(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class GsensorObserver extends MessageObserver {
        public GsensorObserver() {
            super(17);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyGsensor(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class GestureObserver extends MessageObserver {
        public GestureObserver() {
            super(18);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyGesture(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class SensorObserver extends MessageObserver {
        public SensorObserver() {
            super(19);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifySensor(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class TPMSObserver extends MessageObserver {
        public TPMSObserver() {
            super(8);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyTpms(message);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class CanUpObserver extends MessageObserver {
        public CanUpObserver() {
            super(14);
        }

        @Override // com.syu.remote.MessageObserver
        public void onReceiver(Message message) {
            if (message != null) {
                for (IpcNotify ipcNotify : IpcObj.this.listeners) {
                    ipcNotify.notifyCanbox(message);
                }
            }
        }
    }

    public void removeNotify(IpcNotify ipcNotify) {
        List<IpcNotify> list = this.listeners;
        if (list == null || list.size() <= 0) {
            return;
        }
        this.listeners.remove(ipcNotify);
    }
}
