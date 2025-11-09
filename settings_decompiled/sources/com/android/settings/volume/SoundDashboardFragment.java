package com.android.settings.volume;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.utils.AppPackageName;
import com.android.settings.volume.controller.SoundAmpController;
import com.android.settings.volume.controller.SoundBalanceSettingsController;
import com.android.settings.volume.controller.SoundBootSoundController;
import com.android.settings.volume.controller.SoundDefaultSoundSwitchController;
import com.android.settings.volume.controller.SoundFiberThroughController;
import com.android.settings.volume.controller.SoundGpsMixController;
import com.android.settings.volume.controller.SoundGpxMixScaleController;
import com.android.settings.volume.controller.SoundLoudController;
import com.android.settings.volume.controller.SoundReductionSwitchController;
import com.android.settings.volume.controller.SoundReserverReduceController;
import com.android.settings.volume.controller.SoundReverseMuteController;
import com.android.settings.volume.controller.SoundScaleController;
import com.android.settings.volume.controller.SoundSpeedVolumeController;
import com.android.settings.volume.controller.SoundTouchScreenController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SoundDashboardFragment extends DashboardFragment implements IpcNotify {
    private static int UPDATEMSG = 803;
    private SoundAmpController soundAmpController;
    private SoundBalanceSettingsController soundBalanceSettingsController;
    private SoundBootSoundController soundBootSoundController;
    private SoundDefaultSoundSwitchController soundDefaultSoundSwitchController;
    private SoundFiberThroughController soundFiberThroughController;
    private SoundGpsMixController soundGpsMixController;
    private SoundGpxMixScaleController soundGpxMixScaleController;
    private SoundLoudController soundLoudController;
    private SoundReductionSwitchController soundReductionSwitchController;
    private SoundReserverReduceController soundReserverReduceController;
    private SoundReverseMuteController soundReverseMuteController;
    private SoundScaleController soundScaleController;
    private SoundSpeedVolumeController soundSpeedVolumeController;
    private SoundTouchScreenController soundTouchScreenController;
    private String mSoundModelId = "";
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.volume.SoundDashboardFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == SoundDashboardFragment.UPDATEMSG) {
                SoundDashboardFragment.this.updateSoundMsg((com.syu.remote.Message) message.obj);
            }
            super.handleMessage(message);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SoundDashboardFragment";
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
    public void notifyMain(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(com.syu.remote.Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(com.syu.remote.Message message) {
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
        return R$xml.sound_settings_layout;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setNotify(getPrefContext());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.handler.removeMessages(UPDATEMSG);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        this.soundTouchScreenController = new SoundTouchScreenController(context, lifecycle);
        this.soundLoudController = new SoundLoudController(context, lifecycle);
        this.soundAmpController = new SoundAmpController(context, lifecycle);
        this.soundReductionSwitchController = new SoundReductionSwitchController(context, lifecycle);
        this.soundBalanceSettingsController = new SoundBalanceSettingsController(context, "volbalance");
        this.soundBootSoundController = new SoundBootSoundController(context, lifecycle);
        this.soundScaleController = new SoundScaleController(context, lifecycle);
        this.soundSpeedVolumeController = new SoundSpeedVolumeController(context, lifecycle);
        this.soundGpxMixScaleController = new SoundGpxMixScaleController(context, lifecycle);
        this.soundReserverReduceController = new SoundReserverReduceController(context, lifecycle);
        this.soundFiberThroughController = new SoundFiberThroughController(context, lifecycle);
        arrayList.add(this.soundTouchScreenController);
        arrayList.add(this.soundLoudController);
        arrayList.add(this.soundAmpController);
        arrayList.add(this.soundReductionSwitchController);
        arrayList.add(this.soundBalanceSettingsController);
        arrayList.add(this.soundBootSoundController);
        arrayList.add(this.soundScaleController);
        arrayList.add(this.soundSpeedVolumeController);
        arrayList.add(this.soundGpxMixScaleController);
        arrayList.add(this.soundReserverReduceController);
        arrayList.add(this.soundFiberThroughController);
        return arrayList;
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(4, 4, 11, 13, 7, 5, 14, 6, 51, 52, 57, 15, 23, 12, 81, 85);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        preferenceClick(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    private void preferenceClick(String str) {
        if ("equalizer".contains(str)) {
            startEq();
        } else if ("volbalance".contains(str)) {
            this.soundBalanceSettingsController.showfragment(this, getPrefContext().getString(R$string.factory_volbalance));
        } else if ("sound_by_speed".contains(str)) {
            this.soundSpeedVolumeController.showDialog(this, getPrefContext().getString(R$string.sound_by_speed));
        } else if ("subwoof".contains(str)) {
            startSubwoof();
        }
    }

    private void startEq() {
        Intent intent = new Intent();
        intent.setClassName(AppPackageName.EQPACKAGE, AppPackageName.EQCLASS);
        intent.putExtra("jump_to", 29);
        getPrefContext().startActivity(intent);
    }

    private void startSubwoof() {
        Intent intent = new Intent();
        intent.setClassName(AppPackageName.EQPACKAGE, AppPackageName.EQCLASS);
        intent.putExtra("jump_to", 34);
        getPrefContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSoundMsg(com.syu.remote.Message message) {
        Log.d("SoundDashboardFragment", "updateSoundMsg: " + message + "  " + message.ints[0]);
        int i = message.code;
        if (i == 4) {
            SoundTouchScreenController soundTouchScreenController = this.soundTouchScreenController;
            if (soundTouchScreenController != null) {
                soundTouchScreenController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 5) {
            SoundDefaultSoundSwitchController soundDefaultSoundSwitchController = this.soundDefaultSoundSwitchController;
            if (soundDefaultSoundSwitchController != null) {
                soundDefaultSoundSwitchController.setChecked(message.ints[0] == 1);
            }
            SoundBootSoundController soundBootSoundController = this.soundBootSoundController;
            if (soundBootSoundController != null) {
                soundBootSoundController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 6) {
            SoundBootSoundController soundBootSoundController2 = this.soundBootSoundController;
            if (soundBootSoundController2 != null) {
                soundBootSoundController2.updateProgress(message.ints[0]);
            }
        } else if (i == 7) {
            SoundReverseMuteController soundReverseMuteController = this.soundReverseMuteController;
            if (soundReverseMuteController != null) {
                soundReverseMuteController.setChecked(message.ints[0] == 1);
            }
            SoundReserverReduceController soundReserverReduceController = this.soundReserverReduceController;
            if (soundReserverReduceController != null) {
                soundReserverReduceController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 23) {
            SoundReserverReduceController soundReserverReduceController2 = this.soundReserverReduceController;
            if (soundReserverReduceController2 != null) {
                soundReserverReduceController2.setProgress(message.ints[0]);
            }
        } else if (i == 57) {
            SoundSpeedVolumeController soundSpeedVolumeController = this.soundSpeedVolumeController;
            if (soundSpeedVolumeController != null) {
                soundSpeedVolumeController.setSpeedSound(message.ints[0]);
            }
        } else if (i == 81) {
            SoundReductionSwitchController soundReductionSwitchController = this.soundReductionSwitchController;
            if (soundReductionSwitchController != null) {
                soundReductionSwitchController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 85) {
            SoundFiberThroughController soundFiberThroughController = this.soundFiberThroughController;
            if (soundFiberThroughController != null) {
                soundFiberThroughController.setChecked(message.ints[0] == 1);
            }
        } else if (i == 51) {
            SoundScaleController soundScaleController = this.soundScaleController;
            if (soundScaleController != null) {
                soundScaleController.setMaxValue(message.ints[1]);
                this.soundScaleController.setMinValue(message.ints[0]);
            }
        } else if (i != 52) {
            switch (i) {
                case 11:
                    SoundLoudController soundLoudController = this.soundLoudController;
                    if (soundLoudController != null) {
                        soundLoudController.setChecked(message.ints[0] == 1);
                        return;
                    }
                    return;
                case 12:
                    SoundBalanceSettingsController soundBalanceSettingsController = this.soundBalanceSettingsController;
                    if (soundBalanceSettingsController != null) {
                        int[] iArr = message.ints;
                        soundBalanceSettingsController.setSoundValues(iArr[0], iArr[1]);
                        return;
                    }
                    return;
                case 13:
                    SoundAmpController soundAmpController = this.soundAmpController;
                    if (soundAmpController != null) {
                        soundAmpController.setChecked(message.ints[0] == 1);
                        return;
                    }
                    return;
                case 14:
                    SoundGpsMixController soundGpsMixController = this.soundGpsMixController;
                    if (soundGpsMixController != null) {
                        soundGpsMixController.setChecked(message.ints[0] == 1);
                    }
                    SoundGpxMixScaleController soundGpxMixScaleController = this.soundGpxMixScaleController;
                    if (soundGpxMixScaleController != null) {
                        soundGpxMixScaleController.setChecked(message.ints[0] == 1);
                        return;
                    }
                    return;
                case 15:
                    SoundGpxMixScaleController soundGpxMixScaleController2 = this.soundGpxMixScaleController;
                    if (soundGpxMixScaleController2 != null) {
                        soundGpxMixScaleController2.setProgress(message.ints[0]);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else {
            SoundScaleController soundScaleController2 = this.soundScaleController;
            if (soundScaleController2 != null) {
                soundScaleController2.setProgress(message.ints[0]);
            }
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(com.syu.remote.Message message) {
        Message message2 = new Message();
        message2.what = UPDATEMSG;
        message2.obj = message;
        Handler handler = this.handler;
        if (handler != null) {
            handler.sendMessage(message2);
        }
        Log.d("SoundDashboardFragment", "notifySound: " + message.toString());
    }
}
