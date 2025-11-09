package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.syudisplay.BacklightLevelContrlooer;
import com.android.settings.display.syudisplay.ScreenSaverController;
import com.android.settings.display.syudisplay.SettingsThemeController;
import com.android.settings.display.syudisplay.TimePickerContrlooer;
import com.android.settings.display.syudisplay.WlanSpeedController;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.syu.remote.Message;
import com.unisoc.settings.superresolution.OnFocusListenable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class DisplaySettings extends DashboardFragment implements OnFocusListenable, IpcNotify {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.display_settings) { // from class: com.android.settings.DisplaySettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return null;
        }
    };
    private static int brightnessControlState = -1;
    private static boolean isSupportSuperResolution;
    private static ScreenSaverController screenSaverController;
    private static TimePickerContrlooer timePickerContrlooer;
    private BacklightLevelContrlooer backlightLevelContrlooer;
    private SettingsThemeController settingsThemeController;
    private WlanSpeedController wlanSpeedController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DisplaySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 46;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvr(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGesture(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyGsensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyIpod(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyRadio(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySensor(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifySound(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTpms(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyTv(Message message) {
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
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        SettingsApplication.mApplication.getHomeActivity().setHomeBackground(SettingsApplication.mApplication.getThemeMode(), true, -1);
    }

    private void updateTitle() {
        TextView textView;
        FragmentActivity activity = getActivity();
        if (!(activity instanceof SettingsBaseActivity) || (textView = (TextView) ((SettingsBaseActivity) activity).findViewById(R$id.collapsing_title)) == null) {
            return;
        }
        textView.setText(R$string.display_settings);
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 29, 32, 33, 177, 178);
    }

    private void updateMainMsg(Message message) {
        SettingsThemeController settingsThemeController;
        Log.d("DisplaySettings", "updateMainMsg: " + message.code + "  " + Arrays.toString(message.ints));
        int i = message.code;
        if (i == 29) {
            int[] iArr = message.ints;
            if (iArr != null) {
                int i2 = iArr[0];
                brightnessControlState = i2;
                timePickerContrlooer.setTimePickerState(i2);
                updatePreferenceStates();
            }
        } else if (i == 32) {
            BacklightLevelContrlooer backlightLevelContrlooer = this.backlightLevelContrlooer;
            if (backlightLevelContrlooer != null) {
                backlightLevelContrlooer.setBrightnessDay(message.ints[0]);
            }
        } else if (i == 33) {
            BacklightLevelContrlooer backlightLevelContrlooer2 = this.backlightLevelContrlooer;
            if (backlightLevelContrlooer2 != null) {
                backlightLevelContrlooer2.setBrightnessNight(message.ints[0]);
            }
        } else if (i != 177) {
            if (i == 178 && (settingsThemeController = this.settingsThemeController) != null) {
                settingsThemeController.updateTheme(message.ints[0]);
            }
        } else {
            ScreenSaverController screenSaverController2 = screenSaverController;
            if (screenSaverController2 != null) {
                screenSaverController2.setScreensaverTime(message.ints[0]);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.unisoc.settings.superresolution.OnFocusListenable
    public void onWindowFocusChanged(boolean z) {
        Log.d("DisplaySettings", "onWindowFocusChanged isSupportSuperResolution :" + isSupportSuperResolution + "    hasWindowFocus : " + z);
    }

    @Override // androidx.fragment.app.Fragment
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.display_settings;
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
        timePickerContrlooer = new TimePickerContrlooer(context, "backlight_time_control");
        screenSaverController = new ScreenSaverController(this, context, "screensaver_preview");
        this.backlightLevelContrlooer = new BacklightLevelContrlooer(context, "brightness_day_night");
        this.wlanSpeedController = new WlanSpeedController(context, "wireless_control_switch");
        this.settingsThemeController = new SettingsThemeController(context, "display_theme");
        arrayList.add(timePickerContrlooer);
        arrayList.add(screenSaverController);
        arrayList.add(this.backlightLevelContrlooer);
        arrayList.add(this.wlanSpeedController);
        arrayList.add(this.settingsThemeController);
        return arrayList;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(Message message) {
        updateMainMsg(message);
    }
}
