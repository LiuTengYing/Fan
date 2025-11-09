package com.android.settings.connecteddevice;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.bluetooth.BluetoothDeviceRenamePreferenceController;
import com.android.settings.bluetooth.BluetoothSwitchPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.password.PasswordUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MainSwitchBarController;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class BluetoothDashboardFragment extends DashboardFragment {
    private static final boolean DEBUG = Log.isLoggable("BluetoothDashboardFrag", 3);
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.bluetooth_screen);
    private BluetoothSwitchPreferenceController mController;
    private FooterPreference mFooterPreference;
    private SettingsMainSwitchBar mSwitchBar;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1390;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_bluetooth_screen;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.bluetooth_screen;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFooterPreference = (FooterPreference) findPreference("bluetooth_screen_footer");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BluetoothDeviceRenamePreferenceController) use(BluetoothDeviceRenamePreferenceController.class)).setFragment(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivity().getActivityToken());
        String action = getIntent() != null ? getIntent().getAction() : "";
        if (DEBUG) {
            Log.d("BluetoothDashboardFrag", "onActivityCreated() calling package name is : " + callingAppPackageName + ", action : " + action);
        }
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.bluetooth_main_switch_title));
        BluetoothSwitchPreferenceController bluetoothSwitchPreferenceController = new BluetoothSwitchPreferenceController(settingsActivity, new MainSwitchBarController(this.mSwitchBar), this.mFooterPreference);
        this.mController = bluetoothSwitchPreferenceController;
        bluetoothSwitchPreferenceController.setAlwaysDiscoverable(isAlwaysDiscoverable(callingAppPackageName, action));
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        if (settingsLifecycle != null) {
            settingsLifecycle.addObserver(this.mController);
        }
    }

    boolean isAlwaysDiscoverable(String str, String str2) {
        if (TextUtils.equals("com.android.settings.SEARCH_RESULT_TRAMPOLINE", str2)) {
            return false;
        }
        return TextUtils.equals("com.android.settings", str) || TextUtils.equals("com.android.systemui", str);
    }
}
