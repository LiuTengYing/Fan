package com.android.settings.factory;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsApplication;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.factory.controller.ProtocolAirConditionSwitchController;
import com.android.settings.factory.controller.ProtocolAutoStartStopController;
import com.android.settings.factory.controller.ProtocolCanboxUpgradeController;
import com.android.settings.factory.controller.ProtocolCarDoorSwitchController;
import com.android.settings.factory.controller.ProtocolCarTyleController;
import com.android.settings.factory.controller.ProtocolDriverLocationController;
import com.android.settings.factory.controller.ProtocolPrintController;
import com.android.settings.factory.controller.ProtocolRadarClickSwitchController;
import com.android.settings.factory.controller.ProtocolRadarSwitchController;
import com.android.settings.factory.controller.ProtocolRearDoorLeftRightController;
import com.android.settings.factory.controller.ProtocolSoundChannelController;
import com.android.settings.factory.controller.ProtocolTemperatureOutController;
import com.android.settings.factory.controller.ProtocolTemperatureReversalController;
import com.android.settings.factory.controller.ProtocolTemperatureUnitController;
import com.android.settings.factory.controller.ProtocolTrackForceCenterController;
import com.android.settings.factory.controller.ProtocolTrackMaxAngelController;
import com.android.settings.factory.controller.ProtocolTrackReserverController;
import com.android.settings.factory.controller.ProtocolTrackSwitchController;
import com.android.settings.factory.controller.ProtocolUpAndDownSongController;
import com.android.settings.factory.controller.ProtocolVolumeSwapController;
import com.android.settings.factory.listener.ProtocolNotifyListener;
import com.android.settings.factory.protocol.CarProtocol;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class FactoryCarProcotolFragment extends DashboardFragment implements IpcNotify {
    private static String CANBOXUPGRADE = "canbox_upgrade";
    private static String CARTYPE = "cartype";
    private static String TAG = "FactoryCarProcotolFragment";
    private static String TRACKMAXANGEL = "back_car_max_angel";
    private static int UPDATECANBUS = 604;
    private static int UPDATEMAIN = 603;
    private ProtocolAirConditionSwitchController protocolAirConditionSwitchController;
    private ProtocolAutoStartStopController protocolAutoStartStopController;
    private ProtocolCanboxUpgradeController protocolCanboxUpgradeController;
    private ProtocolCarDoorSwitchController protocolCarDoorSwitchController;
    private ProtocolCarTyleController protocolCarTyleController;
    private ProtocolDriverLocationController protocolDriverLocationController;
    private ProtocolPrintController protocolPrintController;
    private ProtocolRadarClickSwitchController protocolRadarClickSwitchController;
    private ProtocolRadarSwitchController protocolRadarSwitchController;
    private ProtocolRearDoorLeftRightController protocolRearDoorLeftRightController;
    private ProtocolSoundChannelController protocolSoundChannelController;
    private ProtocolTemperatureOutController protocolTemperatureOutController;
    private ProtocolTemperatureReversalController protocolTemperatureReversalController;
    private ProtocolTrackForceCenterController protocolTrackForceCenterController;
    private ProtocolTrackMaxAngelController protocolTrackMaxAngelController;
    private ProtocolTrackReserverController protocolTrackReserverController;
    private ProtocolTrackSwitchController protocolTrackSwitchController;
    private ProtocolUpAndDownSongController protocolUpAndDownSongController;
    private ProtocolVolumeSwapController protocolVolumeSwapController;
    private ProtocolTemperatureUnitController temperatureUnitController;
    private ProtocolNotifyListener protocolNotifyListener = new ProtocolNotifyListener() { // from class: com.android.settings.factory.FactoryCarProcotolFragment.1
        @Override // com.android.settings.factory.listener.ProtocolNotifyListener
        public void notifyCarType() {
            Log.d(FactoryCarProcotolFragment.TAG, "notifyCarType: ");
            if (FactoryCarProcotolFragment.this.protocolCarTyleController != null) {
                FactoryCarProcotolFragment.this.protocolCarTyleController.initCarStr();
            }
        }
    };
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.factory.FactoryCarProcotolFragment.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == FactoryCarProcotolFragment.UPDATEMAIN) {
                FactoryCarProcotolFragment.this.updateMain((com.syu.remote.Message) message.obj);
            } else if (message.what == FactoryCarProcotolFragment.UPDATECANBUS) {
                FactoryCarProcotolFragment.this.updateCanbus((com.syu.remote.Message) message.obj);
            }
            super.handleMessage(message);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(com.syu.remote.Message message) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.factory_car_protocol_layout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setNotify(getPrefContext());
        CarProtocol.getInstance(getPrefContext()).setProtocolNotifyListener(this.protocolNotifyListener);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        CarProtocol.getInstance(getPrefContext()).removeProtocolNotifyListsner(this.protocolNotifyListener);
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 88, 130, 23, 102, R.styleable.AppCompatTheme_toolbarNavigationButtonStyle, 27, 201);
        IpcObj.getInstance().setObserverMoudle(7, 1002, 1003, 1001, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (CARTYPE.contains(preference.getKey())) {
            ProtocolCarTyleController protocolCarTyleController = this.protocolCarTyleController;
            if (protocolCarTyleController != null) {
                protocolCarTyleController.showCarType(this, "");
            }
        } else if (TRACKMAXANGEL.contains(preference.getKey())) {
            this.protocolTrackMaxAngelController.showDialog(this, getPrefContext().getString(R$string.back_car_max_angel));
        } else if (CANBOXUPGRADE.contains(preference.getKey())) {
            this.protocolCanboxUpgradeController.showDialog(this, getPrefContext().getString(R$string.canbox_upgrade));
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        this.protocolAutoStartStopController = new ProtocolAutoStartStopController(context, lifecycle);
        this.protocolTrackSwitchController = new ProtocolTrackSwitchController(context, lifecycle);
        this.protocolTrackForceCenterController = new ProtocolTrackForceCenterController(context, lifecycle);
        this.protocolTrackReserverController = new ProtocolTrackReserverController(context, lifecycle);
        this.protocolCarDoorSwitchController = new ProtocolCarDoorSwitchController(context, lifecycle);
        this.protocolDriverLocationController = new ProtocolDriverLocationController(context, lifecycle);
        this.protocolAirConditionSwitchController = new ProtocolAirConditionSwitchController(context, lifecycle);
        this.protocolCarTyleController = new ProtocolCarTyleController(context, lifecycle);
        this.protocolTrackMaxAngelController = new ProtocolTrackMaxAngelController(context, lifecycle);
        this.protocolCanboxUpgradeController = new ProtocolCanboxUpgradeController(context, lifecycle);
        this.protocolTemperatureReversalController = new ProtocolTemperatureReversalController(context, lifecycle);
        this.temperatureUnitController = new ProtocolTemperatureUnitController(context, lifecycle);
        this.protocolSoundChannelController = new ProtocolSoundChannelController(context, lifecycle);
        this.protocolRadarSwitchController = new ProtocolRadarSwitchController(context, lifecycle);
        this.protocolTemperatureOutController = new ProtocolTemperatureOutController(context, lifecycle);
        this.protocolRearDoorLeftRightController = new ProtocolRearDoorLeftRightController(context, lifecycle);
        this.protocolUpAndDownSongController = new ProtocolUpAndDownSongController(context, lifecycle);
        this.protocolVolumeSwapController = new ProtocolVolumeSwapController(context, lifecycle);
        this.protocolPrintController = new ProtocolPrintController(context, lifecycle);
        this.protocolRadarClickSwitchController = new ProtocolRadarClickSwitchController(context, lifecycle);
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.protocolAutoStartStopController);
        arrayList.add(this.protocolTrackSwitchController);
        arrayList.add(this.protocolTrackForceCenterController);
        arrayList.add(this.protocolCarDoorSwitchController);
        arrayList.add(this.protocolDriverLocationController);
        arrayList.add(this.protocolAirConditionSwitchController);
        arrayList.add(this.protocolTrackReserverController);
        arrayList.add(this.protocolCarTyleController);
        arrayList.add(this.protocolTrackMaxAngelController);
        arrayList.add(this.protocolCanboxUpgradeController);
        arrayList.add(this.protocolTemperatureReversalController);
        arrayList.add(this.temperatureUnitController);
        arrayList.add(this.protocolSoundChannelController);
        arrayList.add(this.protocolRadarSwitchController);
        arrayList.add(this.protocolTemperatureOutController);
        arrayList.add(this.protocolRearDoorLeftRightController);
        arrayList.add(this.protocolUpAndDownSongController);
        arrayList.add(this.protocolVolumeSwapController);
        arrayList.add(this.protocolPrintController);
        arrayList.add(this.protocolRadarClickSwitchController);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMain(com.syu.remote.Message message) {
        String str = TAG;
        Log.d(str, "updateMain: " + message.code + Arrays.toString(message.ints));
        int i = message.code;
        if (i == 23) {
            this.protocolTrackSwitchController.setChecked(message.ints[0] > 0);
        } else if (i == 27) {
            this.protocolRadarSwitchController.setChecked(message.ints[0] > 0);
        } else if (i == 88) {
            this.protocolAutoStartStopController.setChecked(message.ints[0] > 0);
        } else if (i == 102) {
            this.protocolTrackReserverController.setChecked(message.ints[0] > 0);
        } else if (i == 112) {
            this.protocolSoundChannelController.setChecked(message.ints[0] > 0);
        } else if (i == 130) {
            this.protocolTrackForceCenterController.setChecked(message.ints[0] > 0);
        } else if (i != 201) {
        } else {
            this.protocolPrintController.setChecked(message.ints[1] > 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCanbus(com.syu.remote.Message message) {
        String str = TAG;
        Log.d(str, "updateCanbus: " + message.code + Arrays.toString(message.ints));
        switch (message.code) {
            case AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP /* 1000 */:
                this.protocolCarTyleController.setCanbusId(message.ints[0]);
                SettingsApplication.mCanbusId = message.ints[0];
                this.temperatureUnitController.updateCheck();
                this.protocolCarTyleController.initCarStr();
                return;
            case 1001:
                this.protocolAirConditionSwitchController.setChecked(message.ints[0] > 0);
                return;
            case 1002:
                this.protocolCarDoorSwitchController.setChecked(message.ints[0] > 0);
                return;
            case 1003:
                this.protocolDriverLocationController.setChecked(message.ints[0] > 0);
                return;
            default:
                return;
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATEMAIN;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATECANBUS;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
    }
}
