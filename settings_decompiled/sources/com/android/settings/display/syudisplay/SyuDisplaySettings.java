package com.android.settings.display.syudisplay;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsApplication;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class SyuDisplaySettings extends DashboardFragment implements IpcNotify {
    private static int UPDATEMSG = 703;
    private BacklightLevelContrlooer backlightLevelContrlooer;
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.display.syudisplay.SyuDisplaySettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == SyuDisplaySettings.UPDATEMSG) {
                SyuDisplaySettings.this.updateMainMsg((com.syu.remote.Message) message.obj);
            }
            super.handleMessage(message);
        }
    };
    private ScreenSaverController screenSaverController;
    private ScreenSaverTitleController screenSaverTitleController;
    private SettingsThemeController settingsThemeController;
    private TimePickerContrlooer timePickerContrlooer;
    private WlanSpeedController wlanSpeedController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SyuDisplaySettings";
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

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setNotify(getPrefContext());
        updatePreferenceStates();
        updateTitle();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        SettingsApplication.mApplication.getHomeActivity().setHomeBackground(SettingsApplication.mApplication.getThemeMode(), true, -1);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
    }

    private void updateTitle() {
        Toolbar toolbar;
        FragmentActivity activity = getActivity();
        if (!(activity instanceof SettingsBaseActivity) || (toolbar = (Toolbar) ((SettingsBaseActivity) activity).findViewById(R$id.action_bar)) == null) {
            return;
        }
        toolbar.setTitle(getPrefContext().getResources().getString(R$string.display_settings));
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 29, 32, 33, 177, 178, 136, 4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMainMsg(com.syu.remote.Message message) {
        SettingsThemeController settingsThemeController;
        Log.d("SyuDisplaySettings", "updateMainMsg: " + message.code + "  " + Arrays.toString(message.ints));
        int i = message.code;
        if (i == 4) {
            BacklightLevelContrlooer backlightLevelContrlooer = this.backlightLevelContrlooer;
            if (backlightLevelContrlooer != null) {
                backlightLevelContrlooer.setDayNightSelect(message.ints[0]);
            }
            SettingsThemeController settingsThemeController2 = this.settingsThemeController;
            if (settingsThemeController2 != null) {
                settingsThemeController2.setDayNightState(message.ints[0]);
            }
        } else if (i == 29) {
            int[] iArr = message.ints;
            if (iArr != null) {
                this.timePickerContrlooer.setTimePickerState(iArr[0]);
                updatePreferenceStates();
            }
        } else if (i == 32) {
            BacklightLevelContrlooer backlightLevelContrlooer2 = this.backlightLevelContrlooer;
            if (backlightLevelContrlooer2 != null) {
                backlightLevelContrlooer2.setBrightnessDay(message.ints[0]);
            }
        } else if (i == 33) {
            BacklightLevelContrlooer backlightLevelContrlooer3 = this.backlightLevelContrlooer;
            if (backlightLevelContrlooer3 != null) {
                backlightLevelContrlooer3.setBrightnessNight(message.ints[0]);
            }
        } else if (i != 177) {
            if (i == 178 && (settingsThemeController = this.settingsThemeController) != null) {
                settingsThemeController.updateTheme(message.ints[0]);
            }
        } else {
            ScreenSaverController screenSaverController = this.screenSaverController;
            if (screenSaverController != null) {
                int[] iArr2 = message.ints;
                int i2 = iArr2[0];
                if (i2 == 2) {
                    screenSaverController.setScreensaverState(iArr2[1] == 1);
                } else if (i2 == 1) {
                    screenSaverController.initScreensaverTime(iArr2[1]);
                }
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            return R$xml.display_settings;
        }
        return R$xml.display_settings_h;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_display;
    }

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        this.timePickerContrlooer = new TimePickerContrlooer(context, "backlight_time_control");
        this.screenSaverController = new ScreenSaverController(this, context, "screensaver_preview");
        this.backlightLevelContrlooer = new BacklightLevelContrlooer(context, "brightness_day_night");
        this.wlanSpeedController = new WlanSpeedController(context, "wireless_control_switch");
        this.settingsThemeController = new SettingsThemeController(context, "display_theme");
        this.screenSaverTitleController = new ScreenSaverTitleController(this, context, "screensaver_title");
        arrayList.add(this.timePickerContrlooer);
        arrayList.add(this.screenSaverController);
        arrayList.add(this.backlightLevelContrlooer);
        arrayList.add(this.wlanSpeedController);
        arrayList.add(this.settingsThemeController);
        arrayList.add(this.screenSaverTitleController);
        return arrayList;
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
}
