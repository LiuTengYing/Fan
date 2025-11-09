package com.android.settings.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.common.controller.CommonAnyKeyStartSwitchController;
import com.android.settings.common.controller.CommonAutoNaviController;
import com.android.settings.common.controller.CommonAutoStartAppController;
import com.android.settings.common.controller.CommonBackCarMirrorController;
import com.android.settings.common.controller.CommonFanSetController;
import com.android.settings.common.controller.CommonHicarSettingsController;
import com.android.settings.common.controller.CommonKeyCustomController;
import com.android.settings.common.controller.CommonLanternController;
import com.android.settings.common.controller.CommonLauncherAppController;
import com.android.settings.common.controller.CommonNaviAppController;
import com.android.settings.common.controller.CommonOSDTimeController;
import com.android.settings.common.controller.CommonOneinstallController;
import com.android.settings.common.controller.CommonPoweroffDelayController;
import com.android.settings.common.controller.CommonSteerSettingsController;
import com.android.settings.common.controller.CommonUsbDeviceStartController;
import com.android.settings.common.controller.CommonVoiceAppController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.homepage.StateChangeListener;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.AppUtils;
import com.android.settings.utils.UpdateStateChange;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.syu.util.IpcUtil;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CommonDashboardFragment extends DashboardFragment implements IpcNotify, StateChangeListener {
    private static String COMMONAUTOSTART = "automatic_start_settings";
    private static String COMMONFANSET = "fan_set";
    private static String COMMONHAICARSET = "huawei_hicar_settings";
    private static String COMMONONEKEYINSTALL = "onekey_install";
    private static String COMMONPOWEROFFDELAY = "poweroff_delay";
    private static String COMMONUSBDEVICESTART = "usb_device_start";
    private static String COMMONVOICEAPP = "voice_app";
    private static String CUSTOMKEY = "custom_key";
    private static String CommonLauncherApp = "launcher_application_setting";
    private static String CommonNaviApp = "navi_app";
    private static String CommonSevenLight = "seven_light";
    private static String CommonSteerSettings = "steer";
    private static int UPDATEMSG = 903;
    private CommonAnyKeyStartSwitchController anyKeyStartSwitchController;
    private CommonAutoNaviController commonAutoNaviController;
    private CommonAutoStartAppController commonAutoStartAppController;
    private CommonBackCarMirrorController commonBackCarMirrorController;
    private CommonFanSetController commonFanSetController;
    private CommonHicarSettingsController commonHicarSettingsController;
    private CommonKeyCustomController commonKeyCustomController;
    private CommonLanternController commonLanternController;
    private CommonLauncherAppController commonLauncherAppController;
    private CommonNaviAppController commonNaviAppController;
    private CommonOSDTimeController commonOSDTimeController;
    private CommonOneinstallController commonOneinstallController;
    private CommonPoweroffDelayController commonPoweroffDelayController;
    private CommonSteerSettingsController commonSteerSettingsController;
    private CommonUsbDeviceStartController commonUsbDeviceStartController;
    private CommonVoiceAppController commonVoiceAppController;
    private int isFanOpen;
    private boolean isNaviPath = false;
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.common.CommonDashboardFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == CommonDashboardFragment.UPDATEMSG) {
                CommonDashboardFragment.this.updateMain((com.syu.remote.Message) message.obj);
            }
            super.handleMessage(message);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "CommonDashboardFragment";
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

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        CommonNaviAppController commonNaviAppController;
        super.onResume();
        Log.d("CommonDashboardFragment", "onResume: ");
        setNotify(getPrefContext());
        UpdateStateChange.getInstance().setStateChangeListener(this);
        CommonUsbDeviceStartController commonUsbDeviceStartController = this.commonUsbDeviceStartController;
        if (commonUsbDeviceStartController != null) {
            commonUsbDeviceStartController.updateState();
        }
        if (!this.isNaviPath || (commonNaviAppController = this.commonNaviAppController) == null) {
            return;
        }
        commonNaviAppController.showDialog(this, getPrefContext().getString(R$string.common_navi_app));
        this.isNaviPath = false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        UpdateStateChange.getInstance().removeStateChangeListener();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.isNaviPath = getActivity().getIntent().getBooleanExtra("NaviPath", false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.common_settings_layout;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        if (this.anyKeyStartSwitchController == null) {
            this.anyKeyStartSwitchController = new CommonAnyKeyStartSwitchController(context, lifecycle);
        }
        if (this.commonAutoNaviController == null) {
            this.commonAutoNaviController = new CommonAutoNaviController(context, lifecycle);
        }
        if (this.commonOSDTimeController == null) {
            this.commonOSDTimeController = new CommonOSDTimeController(context, lifecycle);
        }
        if (this.commonBackCarMirrorController == null) {
            this.commonBackCarMirrorController = new CommonBackCarMirrorController(context, lifecycle);
        }
        if (this.commonLanternController == null) {
            this.commonLanternController = new CommonLanternController(context, lifecycle);
        }
        if (this.commonSteerSettingsController == null) {
            this.commonSteerSettingsController = new CommonSteerSettingsController(context, lifecycle);
        }
        if (this.commonNaviAppController == null) {
            this.commonNaviAppController = new CommonNaviAppController(context, lifecycle);
        }
        if (this.commonUsbDeviceStartController == null) {
            this.commonUsbDeviceStartController = new CommonUsbDeviceStartController(context, lifecycle);
        }
        if (this.commonAutoStartAppController == null) {
            this.commonAutoStartAppController = new CommonAutoStartAppController(context, lifecycle);
        }
        if (this.commonFanSetController == null) {
            this.commonFanSetController = new CommonFanSetController(context, lifecycle);
        }
        if (this.commonPoweroffDelayController == null) {
            this.commonPoweroffDelayController = new CommonPoweroffDelayController(context, lifecycle);
        }
        if (this.commonVoiceAppController == null) {
            this.commonVoiceAppController = new CommonVoiceAppController(context, lifecycle);
        }
        if (this.commonOneinstallController == null) {
            this.commonOneinstallController = new CommonOneinstallController(context, lifecycle);
        }
        if (this.commonLauncherAppController == null) {
            this.commonLauncherAppController = new CommonLauncherAppController(context, lifecycle);
        }
        if (this.commonHicarSettingsController == null) {
            this.commonHicarSettingsController = new CommonHicarSettingsController(context, lifecycle);
        }
        if (this.commonKeyCustomController == null) {
            this.commonKeyCustomController = new CommonKeyCustomController(context, lifecycle);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.anyKeyStartSwitchController);
        arrayList.add(this.commonAutoNaviController);
        arrayList.add(this.commonOSDTimeController);
        arrayList.add(this.commonBackCarMirrorController);
        arrayList.add(this.commonLanternController);
        arrayList.add(this.commonSteerSettingsController);
        arrayList.add(this.commonNaviAppController);
        arrayList.add(this.commonUsbDeviceStartController);
        arrayList.add(this.commonAutoStartAppController);
        arrayList.add(this.commonFanSetController);
        arrayList.add(this.commonPoweroffDelayController);
        arrayList.add(this.commonVoiceAppController);
        arrayList.add(this.commonOneinstallController);
        arrayList.add(this.commonLauncherAppController);
        arrayList.add(this.commonHicarSettingsController);
        arrayList.add(this.commonKeyCustomController);
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        onPreferenceClick(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    private void onPreferenceClick(String str) {
        if (str.contains(CommonSevenLight)) {
            this.commonLanternController.showDialog(this, getPrefContext().getString(R$string.common_seven_light));
        } else if (str.contains(CommonSteerSettings)) {
            startSteerSettings();
        } else if (str.contains(CommonNaviApp)) {
            this.commonNaviAppController.showDialog(this, getPrefContext().getString(R$string.common_navi_app));
        } else if (str.contains(COMMONUSBDEVICESTART)) {
            this.commonUsbDeviceStartController.showDialog(this, getPrefContext().getString(R$string.common_usb_device_start));
        } else if (COMMONAUTOSTART.contains(str)) {
            this.commonAutoStartAppController.showDialog(this, getPrefContext().getString(R$string.common_automatic_start_settings));
        } else if (COMMONFANSET.contains(str)) {
            this.commonFanSetController.showDialog(this, getPrefContext().getString(R$string.common_fan_set));
        } else if (COMMONPOWEROFFDELAY.contains(str)) {
            this.commonPoweroffDelayController.showDialog(this, getPrefContext().getString(R$string.set_Poweroff_Delay));
        } else if (COMMONVOICEAPP.contains(str)) {
            this.commonVoiceAppController.showDialog(this, getPrefContext().getString(R$string.common_voice_app));
        } else if (COMMONONEKEYINSTALL.contains(str)) {
            this.commonOneinstallController.showDialog(this, getPrefContext().getString(R$string.common_one_key_installation));
        } else if (CommonLauncherApp.contains(str)) {
            this.commonLauncherAppController.showDialog(this, getPrefContext().getString(R$string.common_launcher_windows_app));
        } else if (COMMONHAICARSET.contains(str)) {
            startHicar();
        } else if (CUSTOMKEY.contains(str)) {
            this.commonKeyCustomController.showDialog(this, getPrefContext().getString(R$string.common_custom_key));
        }
    }

    private void startHicar() {
        Intent intent = new Intent();
        intent.setFlags(268435456);
        intent.setComponent(new ComponentName("com.syu.hicar", "com.syu.hicar.act.SettingsAct"));
        intent.putExtra("packagename", "com.syu.settings");
        getPrefContext().startActivity(intent);
    }

    private void startSteerSettings() {
        Intent intent = new Intent();
        intent.setAction("com.syu.steer");
        intent.setFlags(268435456);
        getPrefContext().startActivity(intent);
    }

    public void updateMain(com.syu.remote.Message message) {
        CommonFanSetController commonFanSetController;
        Log.d("CommonDashboardFragment", "updateMain: " + message.code + message.toString());
        updateState();
        int i = message.code;
        if (i == 5) {
            this.anyKeyStartSwitchController.setChecked(message.ints[0] == 1);
        } else if (i == 6) {
            this.commonAutoNaviController.setChecked(message.ints[0] == 1);
        } else if (i == 24) {
            this.commonBackCarMirrorController.setChecked(message.ints[0] == 1);
        } else if (i == 26) {
            this.commonOSDTimeController.setChecked(message.ints[0] == 1);
        } else if (i == 28) {
            if (IpcUtil.strsOk(message.strs, 1) && AppUtils.isAppInstalled(getPrefContext(), message.strs[0])) {
                this.commonNaviAppController.setPackageName(message.strs[0]);
            }
        } else if (i == 56) {
            CommonFanSetController commonFanSetController2 = this.commonFanSetController;
            if (commonFanSetController2 != null) {
                commonFanSetController2.setMode(message.ints[0] == 1);
            }
            this.isFanOpen = message.ints[0];
        } else if (i == 142) {
            CommonPoweroffDelayController commonPoweroffDelayController = this.commonPoweroffDelayController;
            if (commonPoweroffDelayController != null) {
                commonPoweroffDelayController.setTime(message.ints[0]);
            }
        } else if (i == 164) {
            if (this.commonVoiceAppController != null) {
                if (IpcUtil.strsOk(message.strs, 1)) {
                    if (AppUtils.isAppInstalled(getPrefContext(), message.strs[0])) {
                        this.commonVoiceAppController.setPackageName(message.strs[0]);
                        return;
                    }
                    return;
                }
                this.commonVoiceAppController.setPackageName("");
            }
        } else if (i == 185) {
            if (this.commonKeyCustomController != null) {
                if (IpcUtil.strsOk(message.strs, 1)) {
                    if (AppUtils.isAppInstalled(getPrefContext(), message.strs[0])) {
                        this.commonKeyCustomController.setPackageName(message.strs[0]);
                        return;
                    }
                    return;
                }
                this.commonKeyCustomController.setPackageName("");
            }
        } else if (i != 144) {
            if (i == 145 && (commonFanSetController = this.commonFanSetController) != null) {
                commonFanSetController.setTemp(message.ints[1]);
                this.commonFanSetController.updateState();
            }
        } else {
            CommonFanSetController commonFanSetController3 = this.commonFanSetController;
            if (commonFanSetController3 != null) {
                commonFanSetController3.setMode(this.isFanOpen == 1);
                this.commonFanSetController.setIsAuto(message.ints[0] == 1);
            }
        }
    }

    private void updateState() {
        CommonUsbDeviceStartController commonUsbDeviceStartController = this.commonUsbDeviceStartController;
        if (commonUsbDeviceStartController != null) {
            commonUsbDeviceStartController.updateState();
        }
        CommonLanternController commonLanternController = this.commonLanternController;
        if (commonLanternController != null) {
            commonLanternController.updateColor();
        }
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 2, 3, 5, 6, 8, 26, 29, 28, 145, 56, 144, 142, 123, 164, 24, 185);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATEMSG;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
    }

    @Override // com.android.settings.homepage.StateChangeListener
    public void stateChange(String str, String str2) {
        Log.d("CommonDashboardFragment", "stateChange: 111");
        updateState();
    }
}
