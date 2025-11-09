package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.SearchIndexableData;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.HardwareRevisionPreferenceController;
import com.android.settings.deviceinfo.SoftwareRevisionPreferenceController;
import com.android.settings.deviceinfo.syudevice.CpuInfoController;
import com.android.settings.deviceinfo.syudevice.QrCodeController;
import com.android.settings.deviceinfo.syudevice.SnCodeController;
import com.android.settings.deviceinfo.syudevice.SyuSystemInfo;
import com.android.settings.deviceinfo.syudevice.YlogUploadDialogController;
import com.android.settings.ipc.IpcNotify;
import com.android.settings.ipc.IpcObj;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexableRaw;
import com.syu.remote.Message;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MyDeviceInfoFragment extends DashboardFragment implements DeviceNamePreferenceController.DeviceNamePreferenceHost, IpcNotify {
    private static String KEY_RECOVERY_SYSTEM_UPDATE = "RecoverySystemUpdate";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.my_device_info) { // from class: com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            Resources resources = context.getResources();
            if (resources.getBoolean(R$bool.config_support_otaupdate) && UserHandle.myUserId() == 0) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw).key = MyDeviceInfoFragment.KEY_RECOVERY_SYSTEM_UPDATE;
                searchIndexableRaw.title = resources.getString(R$string.recovery_update_title);
                searchIndexableRaw.screenTitle = resources.getString(R$string.about_settings);
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return MyDeviceInfoFragment.buildPreferenceControllers(context, null, null);
        }
    };
    private BuildNumberPreferenceController mBuildNumberPreferenceController;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment.2
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            Log.d("MyDeviceInfoFragment", "onSubscriptionsChanged updatePreferenceStates");
            MyDeviceInfoFragment.this.updatePreferenceStates();
        }
    };
    private SyuSystemInfo syuSystemInfoController;

    private void removeSavedWifiMacAddress() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MyDeviceInfoFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 40;
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyAmp(Message message) {
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbox(Message message) {
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

    public void onSetDeviceNameConfirm(boolean z) {
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_about;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        BuildNumberPreferenceController buildNumberPreferenceController = (BuildNumberPreferenceController) use(BuildNumberPreferenceController.class);
        this.mBuildNumberPreferenceController = buildNumberPreferenceController;
        buildNumberPreferenceController.setHost(this);
        SyuSystemInfo syuSystemInfo = (SyuSystemInfo) use(SyuSystemInfo.class);
        this.syuSystemInfoController = syuSystemInfo;
        syuSystemInfo.setHost(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        removeSavedWifiMacAddress();
        SubscriptionManager.from(getContext()).addOnSubscriptionsChangedListener(this.mSubscriptionListener);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        IpcObj.getInstance().removeAllObserver();
        IpcObj.getInstance().removeNotify(this);
        SubscriptionManager.from(getContext()).removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.my_device_info;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setNotify(getPrefContext());
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, MyDeviceInfoFragment myDeviceInfoFragment, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SoftwareRevisionPreferenceController(context));
        arrayList.add(new HardwareRevisionPreferenceController(context));
        arrayList.add(new QrCodeController(myDeviceInfoFragment, context, "quick_response_code"));
        arrayList.add(new CpuInfoController(context, "syu_cpu_info"));
        arrayList.add(new SnCodeController(myDeviceInfoFragment, context, "machine_serial_number"));
        arrayList.add(new YlogUploadDialogController(myDeviceInfoFragment, context, "ylog_upload"));
        return arrayList;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (this.mBuildNumberPreferenceController.onActivityResult(i, i2, intent)) {
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    public void setNotify(Context context) {
        IpcObj.getInstance().setNotify(this);
        IpcObj.getInstance().init(context);
        IpcObj.getInstance().setObserverMoudle(0, 34, 35);
        IpcObj.getInstance().setObserverMoudle(2, 17);
        IpcObj.getInstance().setObserverMoudle(3, 30);
        IpcObj.getInstance().setObserverMoudle(7, 1005);
    }

    @Override // com.android.settings.deviceinfo.DeviceNamePreferenceController.DeviceNamePreferenceHost
    public void showDeviceNameWarningDialog(String str) {
        DeviceNameWarningDialog.show(this);
    }

    private void updateMainMsg(Message message) {
        int i = message.code;
        if (i == 34) {
            Log.d("MyDeviceInfoFragment", "updateMainMsg: " + message.toString());
            if (getPreferenceScreen().findPreference("mcu_version") != null) {
                getPreferenceScreen().findPreference("mcu_version").setSummary(message.strs[0]);
            }
        } else if (i != 35) {
        } else {
            Log.d("MyDeviceInfoFragment", "updateMainMsg: " + message.toString());
            if (getPreferenceScreen().findPreference("machine_serial_number") != null) {
                getPreferenceScreen().findPreference("machine_serial_number").setSummary(message.strs[0]);
            }
        }
    }

    private void updateBtMsg(Message message) {
        if (message.code == 17 && getPreferenceScreen().findPreference("bt_version") != null) {
            String str = message.strs[0];
            if (TextUtils.isEmpty(str)) {
                str = getContext().getResources().getString(R$string.device_info_default);
            }
            getPreferenceScreen().findPreference("bt_version").setSummary(str);
        }
    }

    private void updateDvdMsg(Message message) {
        if (message.code == 30 && getPreferenceScreen().findPreference("dvd_version") != null) {
            getPreferenceScreen().findPreference("dvd_version").setSummary(message.strs[0]);
        }
    }

    private void updateCanbusMsg(Message message) {
        if (message.code == 1005 && getPreferenceScreen().findPreference("canbus_version") != null) {
            String str = message.strs[0];
            if (TextUtils.isEmpty(str)) {
                str = getContext().getResources().getString(R$string.device_info_default);
            }
            getPreferenceScreen().findPreference("canbus_version").setSummary(str);
        }
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyMain(Message message) {
        updateMainMsg(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyDvd(Message message) {
        updateDvdMsg(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyCanbus(Message message) {
        updateCanbusMsg(message);
    }

    @Override // com.android.settings.ipc.IpcNotify
    public void notifyBt(Message message) {
        updateBtMsg(message);
    }
}
