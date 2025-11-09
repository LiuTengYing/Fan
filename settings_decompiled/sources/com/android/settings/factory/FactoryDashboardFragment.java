package com.android.settings.factory;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.factory.controller.Factory360EnableController;
import com.android.settings.factory.controller.FactoryAhdController;
import com.android.settings.factory.controller.FactoryAnteenOnController;
import com.android.settings.factory.controller.FactoryBackupCmaeraSelectController;
import com.android.settings.factory.controller.FactoryBackupTestController;
import com.android.settings.factory.controller.FactoryBlCurrentAdjustController;
import com.android.settings.factory.controller.FactoryCameraPowerController;
import com.android.settings.factory.controller.FactoryCarLogoController;
import com.android.settings.factory.controller.FactoryCarProtocolController;
import com.android.settings.factory.controller.FactoryDoorLockController;
import com.android.settings.factory.controller.FactoryExtraMicController;
import com.android.settings.factory.controller.FactoryFrontViewController;
import com.android.settings.factory.controller.FactoryHandBrakeController;
import com.android.settings.factory.controller.FactoryImageSettingsController;
import com.android.settings.factory.controller.FactoryLauncherSetController;
import com.android.settings.factory.controller.FactoryLocalResverController;
import com.android.settings.factory.controller.FactoryMcuPanelKeyController;
import com.android.settings.factory.controller.FactoryMcuUpgradeController;
import com.android.settings.factory.controller.FactoryOneKeyOutputController;
import com.android.settings.factory.controller.FactoryRadarFrontController;
import com.android.settings.factory.controller.FactoryRadarRecoderController;
import com.android.settings.factory.controller.FactoryReverseAntiShakingController;
import com.android.settings.factory.controller.FactorySeriaDeviceController;
import com.android.settings.factory.controller.FactorySleepModeController;
import com.android.settings.factory.controller.FactoryTouchKeyStudyController;
import com.android.settings.factory.controller.FactoryTouchScreenKeyStudyController;
import com.android.settings.factory.controller.FactoryTouchScreenTestController;
import com.android.settings.factory.controller.FactoryTvTypeController;
import com.android.settings.factory.controller.FactoryUnkonwResourcesController;
import com.android.settings.factory.controller.FactoryUsbErrorController;
import com.android.settings.factory.controller.FactoryUsbProtocolController;
import com.android.settings.homepage.StateChangeListener;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.UpdateStateChange;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class FactoryDashboardFragment extends DashboardFragment implements IpcNotify, StateChangeListener {
    private static String BACKUPCAMERA = "select_backup";
    private static String CARLOGO = "carlogo";
    private static String IMAGESETTINGS = "vcom_avdd";
    private static String LAUNCHERSET = "launch_sel";
    private static String MCUUPDATE = "mcuupgrade";
    private static String ONEKEYOUTPUT = "str_text_config_out";
    private static String SERIADEVICE = "serial_device";
    private static String SYNCANR = "sync_anr";
    private static String TVTYPE = "tvtype";
    private static int UPDATEMAIN = 503;
    private static int UPDATERADIO = 504;
    private static int UPDATETV = 505;
    private static String USBPROTOCOL = "usbprotocol";
    private static String factoryCarProtocol = "car_protocol";
    private static String factoryTouchScreenTest = "touch_test";
    private static String touchScreenPanelStudy = "touchkeystudy";
    private static String touchScreenStudy = "touchscreenstudy";
    private Factory360EnableController factory360EnableController;
    private FactoryAhdController factoryAhdController;
    private FactoryAnteenOnController factoryAnteenOnController;
    private FactoryBackupCmaeraSelectController factoryBackupCmaeraSelectController;
    private FactoryBackupTestController factoryBackupTestController;
    private FactoryBlCurrentAdjustController factoryBlCurrentAdjustController;
    private FactoryCameraPowerController factoryCameraPowerController;
    private FactoryCarLogoController factoryCarLogoController;
    private FactoryCarProtocolController factoryCarProtocolController;
    private FactoryDoorLockController factoryDoorLockController;
    private FactoryExtraMicController factoryExtraMicController;
    private FactoryFrontViewController factoryFrontViewController;
    private FactoryHandBrakeController factoryHandBrakeController;
    private FactoryImageSettingsController factoryImageSettingsController;
    private FactoryLauncherSetController factoryLauncherSetController;
    private FactoryLocalResverController factoryLocalResverController;
    private FactoryMcuPanelKeyController factoryMcuPanelKeyController;
    private FactoryMcuUpgradeController factoryMcuUpgradeController;
    private FactoryOneKeyOutputController factoryOneKeyOutputController;
    private FactoryRadarFrontController factoryRadarFrontController;
    private FactoryRadarRecoderController factoryRadarRecoderController;
    private FactoryReverseAntiShakingController factoryReverseAntiShakingController;
    private FactorySeriaDeviceController factorySeriaDeviceController;
    private FactorySleepModeController factorySleepModeController;
    private FactoryTouchKeyStudyController factoryTouchKeyStudyController;
    private FactoryTouchScreenKeyStudyController factoryTouchScreenKeyStudyController;
    private FactoryTouchScreenTestController factoryTouchScreenTestController;
    private FactoryTvTypeController factoryTvTypeController;
    private FactoryUnkonwResourcesController factoryUnkonwResourcesController;
    private FactoryUsbErrorController factoryUsbErrorController;
    private FactoryUsbProtocolController factoryUsbProtocolController;
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.factory.FactoryDashboardFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == FactoryDashboardFragment.UPDATEMAIN) {
                FactoryDashboardFragment.this.updateMainState((com.syu.remote.Message) message.obj);
            } else if (message.what == FactoryDashboardFragment.UPDATERADIO) {
                FactoryDashboardFragment.this.updateRadioState((com.syu.remote.Message) message.obj);
            } else if (message.what == FactoryDashboardFragment.UPDATETV) {
                FactoryDashboardFragment.this.updateTvMsg((com.syu.remote.Message) message.obj);
            }
            super.handleMessage(message);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "FactoryDashboardFragment";
    }

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
    public void notifyCanbus(com.syu.remote.Message message) {
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
    public void notifySensor(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(com.syu.remote.Message message) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.factory_settings_layout;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setNotify(getPrefContext());
        UpdateStateChange.getInstance().setStateChangeListener(this);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        UpdateStateChange.getInstance().removeStateChangeListener();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        if (this.factory360EnableController == null) {
            this.factory360EnableController = new Factory360EnableController(context, lifecycle);
        }
        if (this.factoryAnteenOnController == null) {
            this.factoryAnteenOnController = new FactoryAnteenOnController(context, lifecycle);
        }
        if (this.factoryAhdController == null) {
            this.factoryAhdController = new FactoryAhdController(context, lifecycle);
        }
        if (this.factoryBackupTestController == null) {
            this.factoryBackupTestController = new FactoryBackupTestController(context, lifecycle);
        }
        if (this.factoryCameraPowerController == null) {
            this.factoryCameraPowerController = new FactoryCameraPowerController(context, lifecycle);
        }
        if (this.factoryDoorLockController == null) {
            this.factoryDoorLockController = new FactoryDoorLockController(context, lifecycle);
        }
        if (this.factoryExtraMicController == null) {
            this.factoryExtraMicController = new FactoryExtraMicController(context, lifecycle);
        }
        if (this.factoryFrontViewController == null) {
            this.factoryFrontViewController = new FactoryFrontViewController(context, lifecycle);
        }
        if (this.factoryHandBrakeController == null) {
            this.factoryHandBrakeController = new FactoryHandBrakeController(context, lifecycle);
        }
        if (this.factoryLocalResverController == null) {
            this.factoryLocalResverController = new FactoryLocalResverController(context, lifecycle);
        }
        if (this.factoryMcuPanelKeyController == null) {
            this.factoryMcuPanelKeyController = new FactoryMcuPanelKeyController(context, lifecycle);
        }
        if (this.factoryRadarFrontController == null) {
            this.factoryRadarFrontController = new FactoryRadarFrontController(context, lifecycle);
        }
        if (this.factorySleepModeController == null) {
            this.factorySleepModeController = new FactorySleepModeController(context, lifecycle);
        }
        if (this.factoryUsbErrorController == null) {
            this.factoryUsbErrorController = new FactoryUsbErrorController(context, lifecycle);
        }
        if (this.factoryTouchScreenTestController == null) {
            this.factoryTouchScreenTestController = new FactoryTouchScreenTestController(context, lifecycle);
        }
        if (this.factoryCarProtocolController == null) {
            this.factoryCarProtocolController = new FactoryCarProtocolController(context, lifecycle);
        }
        if (this.factoryLauncherSetController == null) {
            this.factoryLauncherSetController = new FactoryLauncherSetController(context, LAUNCHERSET);
        }
        if (this.factoryBackupCmaeraSelectController == null) {
            this.factoryBackupCmaeraSelectController = new FactoryBackupCmaeraSelectController(context, lifecycle);
        }
        if (this.factorySeriaDeviceController == null) {
            this.factorySeriaDeviceController = new FactorySeriaDeviceController(context, lifecycle);
        }
        if (this.factoryUsbProtocolController == null) {
            this.factoryUsbProtocolController = new FactoryUsbProtocolController(context, lifecycle);
        }
        if (this.factoryTvTypeController == null) {
            this.factoryTvTypeController = new FactoryTvTypeController(context, lifecycle);
        }
        if (this.factoryMcuUpgradeController == null) {
            this.factoryMcuUpgradeController = new FactoryMcuUpgradeController(context, lifecycle);
        }
        if (this.factoryBlCurrentAdjustController == null) {
            this.factoryBlCurrentAdjustController = new FactoryBlCurrentAdjustController(context, lifecycle, this);
        }
        if (this.factoryImageSettingsController == null) {
            this.factoryImageSettingsController = new FactoryImageSettingsController(context, lifecycle);
        }
        if (this.factoryTouchKeyStudyController == null) {
            this.factoryTouchKeyStudyController = new FactoryTouchKeyStudyController(context, lifecycle);
        }
        if (this.factoryTouchScreenKeyStudyController == null) {
            this.factoryTouchScreenKeyStudyController = new FactoryTouchScreenKeyStudyController(context, lifecycle);
        }
        if (this.factoryOneKeyOutputController == null) {
            this.factoryOneKeyOutputController = new FactoryOneKeyOutputController(context, lifecycle);
        }
        if (this.factoryUnkonwResourcesController == null) {
            this.factoryUnkonwResourcesController = new FactoryUnkonwResourcesController(context, lifecycle);
        }
        if (this.factoryReverseAntiShakingController == null) {
            this.factoryReverseAntiShakingController = new FactoryReverseAntiShakingController(context, lifecycle);
        }
        if (this.factoryCarLogoController == null) {
            this.factoryCarLogoController = new FactoryCarLogoController(context, lifecycle);
        }
        if (this.factoryRadarRecoderController == null) {
            this.factoryRadarRecoderController = new FactoryRadarRecoderController(context, lifecycle);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.factory360EnableController);
        arrayList.add(this.factoryAnteenOnController);
        arrayList.add(this.factoryAhdController);
        arrayList.add(this.factoryBackupTestController);
        arrayList.add(this.factoryCameraPowerController);
        arrayList.add(this.factoryDoorLockController);
        arrayList.add(this.factoryExtraMicController);
        arrayList.add(this.factoryFrontViewController);
        arrayList.add(this.factoryHandBrakeController);
        arrayList.add(this.factoryLocalResverController);
        arrayList.add(this.factoryMcuPanelKeyController);
        arrayList.add(this.factoryRadarFrontController);
        arrayList.add(this.factorySleepModeController);
        arrayList.add(this.factoryUsbErrorController);
        arrayList.add(this.factoryTouchScreenTestController);
        arrayList.add(this.factoryCarProtocolController);
        arrayList.add(this.factoryLauncherSetController);
        arrayList.add(this.factoryBackupCmaeraSelectController);
        arrayList.add(this.factorySeriaDeviceController);
        arrayList.add(this.factoryUsbProtocolController);
        arrayList.add(this.factoryTvTypeController);
        arrayList.add(this.factoryMcuUpgradeController);
        arrayList.add(this.factoryBlCurrentAdjustController);
        arrayList.add(this.factoryImageSettingsController);
        arrayList.add(this.factoryTouchKeyStudyController);
        arrayList.add(this.factoryTouchScreenKeyStudyController);
        arrayList.add(this.factoryOneKeyOutputController);
        arrayList.add(this.factoryUnkonwResourcesController);
        arrayList.add(this.factoryReverseAntiShakingController);
        arrayList.add(this.factoryCarLogoController);
        arrayList.add(this.factoryRadarRecoderController);
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        onPreferenceClick(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    private void onPreferenceClick(String str) {
        if (str.contains(factoryTouchScreenTest)) {
            this.factoryTouchScreenTestController.showDialog(this, "settings");
        } else if (str.contains(factoryCarProtocol)) {
            this.factoryCarProtocolController.showDialog(this, "steer_settings");
        } else if (str.contains(touchScreenStudy)) {
            startTouchScreenStudy();
        } else if (str.contains(touchScreenPanelStudy)) {
            startTouchScreenPanelStudy();
        } else if (str.contains(CARLOGO)) {
            this.factoryCarLogoController.startCarLogo();
        } else if (LAUNCHERSET.contains(str)) {
            this.factoryLauncherSetController.showFragment(this, getPrefContext().getString(R$string.factory_launch_sel));
        } else if (BACKUPCAMERA.contains(str)) {
            this.factoryBackupCmaeraSelectController.showDialog(this, getPrefContext().getString(R$string.factory_select_backup));
        } else if (SERIADEVICE.contains(str)) {
            this.factorySeriaDeviceController.showDialog(this, getPrefContext().getString(R$string.factory_serial_device));
        } else if (USBPROTOCOL.contains(str)) {
            this.factoryUsbProtocolController.showDialog(this, getPrefContext().getString(R$string.factory_usbprotocol));
        } else if (TVTYPE.contains(str)) {
            this.factoryTvTypeController.showDialog(this, getPrefContext().getString(R$string.factory_tvtype));
        } else if (MCUUPDATE.contains(str)) {
            this.factoryMcuUpgradeController.showDialog(this, getPrefContext().getString(R$string.factory_mcuupgrade));
        } else if (IMAGESETTINGS.contains(str)) {
            this.factoryImageSettingsController.showDialog(this, "");
        } else if (ONEKEYOUTPUT.contains(str)) {
            this.factoryOneKeyOutputController.saveConfig();
        } else if (SYNCANR.contains(str)) {
            SystemProperties.set("sys.fyt.setparama", "copyanrs");
            Toast.makeText(getPrefContext(), getPrefContext().getResources().getString(R$string.factory_sync_anr_msg), 0).show();
        }
    }

    private void startTouchScreenStudy() {
        Intent intent = new Intent();
        intent.setPackage("com.syu.calibration");
        intent.setAction("com.syu.screen.cali");
        intent.setFlags(268435456);
        intent.setFlags(2097152);
        getPrefContext().startService(intent);
    }

    private void startTouchScreenPanelStudy() {
        Intent intent = new Intent();
        intent.setAction("com.syu.key.study");
        intent.setFlags(268435456);
        intent.setFlags(2097152);
        getPrefContext().startActivity(intent);
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 172, 121, 8, 65, 82, 66, 125, 100, 38, 81, 116, 109, 163, 180, 31);
        IpcObj.getInstance().setObserverMoudle(1, 25);
        IpcObj.getInstance().setObserverMoudle(6, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMainState(com.syu.remote.Message message) {
        FactoryMcuPanelKeyController factoryMcuPanelKeyController;
        Log.d("FactoryDashboardFragment", "updateMainState: " + message.code + Arrays.toString(message.ints));
        int i = message.code;
        if (i == 8) {
            FactoryHandBrakeController factoryHandBrakeController = this.factoryHandBrakeController;
            if (factoryHandBrakeController != null) {
                factoryHandBrakeController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 31) {
            FactoryBlCurrentAdjustController factoryBlCurrentAdjustController = this.factoryBlCurrentAdjustController;
            if (factoryBlCurrentAdjustController != null) {
                factoryBlCurrentAdjustController.setBrightness(message.ints[0]);
            }
        } else if (i == 38) {
            FactorySleepModeController factorySleepModeController = this.factorySleepModeController;
            if (factorySleepModeController != null) {
                factorySleepModeController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 109) {
            FactoryImageSettingsController factoryImageSettingsController = this.factoryImageSettingsController;
            if (factoryImageSettingsController != null) {
                factoryImageSettingsController.setmVcom(message.ints[0]);
            }
        } else if (i == 116) {
            FactoryImageSettingsController factoryImageSettingsController2 = this.factoryImageSettingsController;
            if (factoryImageSettingsController2 != null) {
                factoryImageSettingsController2.setmAvdd(message.ints[0]);
            }
        } else if (i == 121) {
            FactoryAhdController factoryAhdController = this.factoryAhdController;
            if (factoryAhdController != null) {
                factoryAhdController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 125) {
            FactoryCameraPowerController factoryCameraPowerController = this.factoryCameraPowerController;
            if (factoryCameraPowerController != null) {
                factoryCameraPowerController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 163) {
            FactorySeriaDeviceController factorySeriaDeviceController = this.factorySeriaDeviceController;
            if (factorySeriaDeviceController != null) {
                factorySeriaDeviceController.setSelectDevice(message.strs[0]);
            }
        } else if (i == 172) {
            FactoryExtraMicController factoryExtraMicController = this.factoryExtraMicController;
            if (factoryExtraMicController != null) {
                factoryExtraMicController.setChecked(message.ints[0] == 0);
            }
        } else if (i == 180) {
            FactoryReverseAntiShakingController factoryReverseAntiShakingController = this.factoryReverseAntiShakingController;
            if (factoryReverseAntiShakingController != null) {
                factoryReverseAntiShakingController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 65) {
            FactoryLocalResverController factoryLocalResverController = this.factoryLocalResverController;
            if (factoryLocalResverController != null) {
                factoryLocalResverController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 66) {
            FactoryDoorLockController factoryDoorLockController = this.factoryDoorLockController;
            if (factoryDoorLockController != null) {
                factoryDoorLockController.setCheced(message.ints[0] == 1);
            }
        } else if (i == 81) {
            Factory360EnableController factory360EnableController = this.factory360EnableController;
            if (factory360EnableController != null) {
                factory360EnableController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 82 && (factoryMcuPanelKeyController = this.factoryMcuPanelKeyController) != null) {
            factoryMcuPanelKeyController.setChecked(message.ints[0] == 1);
        }
        updateState();
    }

    private void updateState() {
        FactoryBackupCmaeraSelectController factoryBackupCmaeraSelectController = this.factoryBackupCmaeraSelectController;
        if (factoryBackupCmaeraSelectController != null) {
            factoryBackupCmaeraSelectController.updateCameraSelect();
        }
        FactoryLauncherSetController factoryLauncherSetController = this.factoryLauncherSetController;
        if (factoryLauncherSetController != null) {
            factoryLauncherSetController.updateState();
        }
    }

    public void updateRadioState(com.syu.remote.Message message) {
        FactoryAnteenOnController factoryAnteenOnController;
        if (message.code == 25 && (factoryAnteenOnController = this.factoryAnteenOnController) != null) {
            factoryAnteenOnController.setChecked(message.ints[0] == 1);
        }
    }

    public void updateTvMsg(com.syu.remote.Message message) {
        if (message.code != 0) {
            return;
        }
        Log.d("FactoryDashboardFragment", "updateTvMsg: " + message.code + Arrays.toString(message.ints));
        FactoryTvTypeController factoryTvTypeController = this.factoryTvTypeController;
        if (factoryTvTypeController != null) {
            factoryTvTypeController.setItemSelect(message.ints[0]);
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
    public void notifyRadio(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATERADIO;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATETV;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
    }

    @Override // com.android.settings.homepage.StateChangeListener
    public void stateChange(String str, String str2) {
        FactoryBackupCmaeraSelectController factoryBackupCmaeraSelectController = this.factoryBackupCmaeraSelectController;
        if (factoryBackupCmaeraSelectController != null) {
            factoryBackupCmaeraSelectController.updateCameraSelect();
        }
        FactoryLauncherSetController factoryLauncherSetController = this.factoryLauncherSetController;
        if (factoryLauncherSetController != null) {
            factoryLauncherSetController.updateState();
        }
        FactoryRadarFrontController factoryRadarFrontController = this.factoryRadarFrontController;
        if (factoryRadarFrontController != null) {
            factoryRadarFrontController.updateRadarTofront();
        }
        FactoryRadarRecoderController factoryRadarRecoderController = this.factoryRadarRecoderController;
        if (factoryRadarRecoderController != null) {
            factoryRadarRecoderController.updateRecorder();
        }
    }
}
