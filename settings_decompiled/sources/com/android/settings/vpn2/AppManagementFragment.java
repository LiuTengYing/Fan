package com.android.settings.vpn2;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.VpnManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.net.VpnConfig;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.vpn2.AppDialogFragment;
import com.android.settings.vpn2.ConfirmLockdownFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class AppManagementFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, ConfirmLockdownFragment.ConfirmLockdownListener {
    private DevicePolicyManager mDevicePolicyManager;
    private PackageInfo mPackageInfo;
    private PackageManager mPackageManager;
    private String mPackageName;
    private RestrictedSwitchPreference mPreferenceAlwaysOn;
    private RestrictedPreference mPreferenceForget;
    private RestrictedSwitchPreference mPreferenceLockdown;
    private String mVpnLabel;
    private VpnManager mVpnManager;
    private final int mUserId = UserHandle.myUserId();
    private final AppDialogFragment.Listener mForgetVpnDialogFragmentListener = new AppDialogFragment.Listener() { // from class: com.android.settings.vpn2.AppManagementFragment.1
        @Override // com.android.settings.vpn2.AppDialogFragment.Listener
        public void onCancel() {
        }

        @Override // com.android.settings.vpn2.AppDialogFragment.Listener
        public void onForget() {
            if (AppManagementFragment.this.isVpnAlwaysOn()) {
                AppManagementFragment.this.setAlwaysOnVpn(false, false);
            }
            AppManagementFragment.this.finish();
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 100;
    }

    public static void show(Context context, AppPreference appPreference, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appPreference.getPackageName());
        new SubSettingLauncher(context).setDestination(AppManagementFragment.class.getName()).setArguments(bundle).setTitleText(appPreference.getLabel()).setSourceMetricsCategory(i).setUserHandle(new UserHandle(appPreference.getUserId())).launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.vpn_app_management);
        this.mPackageManager = getContext().getPackageManager();
        this.mDevicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
        this.mVpnManager = (VpnManager) getContext().getSystemService(VpnManager.class);
        this.mPreferenceAlwaysOn = (RestrictedSwitchPreference) findPreference("always_on_vpn");
        this.mPreferenceLockdown = (RestrictedSwitchPreference) findPreference("lockdown_vpn");
        this.mPreferenceForget = (RestrictedPreference) findPreference("forget_vpn");
        this.mPreferenceAlwaysOn.setOnPreferenceChangeListener(this);
        this.mPreferenceLockdown.setOnPreferenceChangeListener(this);
        this.mPreferenceForget.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (loadInfo()) {
            updateUI();
            if (getPreferenceScreen().findPreference("version") != null) {
                return;
            }
            Preference preference = new Preference(getPrefContext()) { // from class: com.android.settings.vpn2.AppManagementFragment.2
                @Override // androidx.preference.Preference
                public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                    super.onBindViewHolder(preferenceViewHolder);
                    TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
                    if (textView != null) {
                        textView.setTextAppearance(R$style.vpn_app_management_version_title);
                    }
                    TextView textView2 = (TextView) preferenceViewHolder.findViewById(16908304);
                    if (textView2 != null) {
                        textView2.setTextAppearance(R$style.vpn_app_management_version_summary);
                        textView2.setMaxHeight(AppManagementFragment.this.getListView().getHeight());
                        textView2.setVerticalScrollBarEnabled(false);
                        textView2.setHorizontallyScrolling(false);
                    }
                }
            };
            preference.setOrder(0);
            preference.setKey("version");
            preference.setTitle(R$string.vpn_version);
            preference.setSummary(this.mPackageInfo.versionName);
            preference.setSelectable(false);
            getPreferenceScreen().addPreference(preference);
            return;
        }
        finish();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("forget_vpn")) {
            return onForgetVpnClick();
        }
        Log.w("AppManagementFragment", "unknown key is clicked: " + key);
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("always_on_vpn")) {
            return onAlwaysOnVpnClick(((Boolean) obj).booleanValue(), this.mPreferenceLockdown.isChecked());
        }
        if (key.equals("lockdown_vpn")) {
            return onAlwaysOnVpnClick(this.mPreferenceAlwaysOn.isChecked(), ((Boolean) obj).booleanValue());
        }
        Log.w("AppManagementFragment", "unknown key is clicked: " + preference.getKey());
        return false;
    }

    private boolean onForgetVpnClick() {
        updateRestrictedViews();
        if (this.mPreferenceForget.isEnabled()) {
            AppDialogFragment.show(this, this.mForgetVpnDialogFragmentListener, this.mPackageInfo, this.mVpnLabel, true, true);
            return true;
        }
        return false;
    }

    private boolean onAlwaysOnVpnClick(boolean z, boolean z2) {
        boolean isAnotherVpnActive = isAnotherVpnActive();
        boolean isAnyLockdownActive = VpnUtils.isAnyLockdownActive(getActivity());
        if (ConfirmLockdownFragment.shouldShow(isAnotherVpnActive, isAnyLockdownActive, z2)) {
            ConfirmLockdownFragment.show(this, isAnotherVpnActive, z, isAnyLockdownActive, z2, null);
            return false;
        }
        return setAlwaysOnVpnByUI(z, z2);
    }

    @Override // com.android.settings.vpn2.ConfirmLockdownFragment.ConfirmLockdownListener
    public void onConfirmLockdown(Bundle bundle, boolean z, boolean z2) {
        setAlwaysOnVpnByUI(z, z2);
    }

    private boolean setAlwaysOnVpnByUI(boolean z, boolean z2) {
        updateRestrictedViews();
        if (this.mPreferenceAlwaysOn.isEnabled()) {
            if (this.mUserId == 0) {
                VpnUtils.clearLockdownVpn(getContext());
            }
            boolean alwaysOnVpn = setAlwaysOnVpn(z, z2);
            if (z && (!alwaysOnVpn || !isVpnAlwaysOn())) {
                CannotConnectFragment.show(this, this.mVpnLabel);
            } else {
                updateUI();
            }
            return alwaysOnVpn;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setAlwaysOnVpn(boolean z, boolean z2) {
        return this.mVpnManager.setAlwaysOnVpnPackageForUser(this.mUserId, z ? this.mPackageName : null, z2, null);
    }

    private void updateUI() {
        if (isAdded()) {
            boolean isVpnAlwaysOn = isVpnAlwaysOn();
            boolean z = isVpnAlwaysOn && VpnUtils.isAnyLockdownActive(getActivity());
            this.mPreferenceAlwaysOn.setChecked(isVpnAlwaysOn);
            this.mPreferenceLockdown.setChecked(z);
            updateRestrictedViews();
        }
    }

    private void updateRestrictedViews() {
        if (isAdded()) {
            this.mPreferenceAlwaysOn.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            this.mPreferenceLockdown.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            this.mPreferenceForget.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            if (this.mPackageName.equals(this.mDevicePolicyManager.getAlwaysOnVpnPackage())) {
                RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(getContext(), UserHandle.of(this.mUserId));
                this.mPreferenceAlwaysOn.setDisabledByAdmin(profileOrDeviceOwner);
                this.mPreferenceForget.setDisabledByAdmin(profileOrDeviceOwner);
                if (this.mDevicePolicyManager.isAlwaysOnVpnLockdownEnabled()) {
                    this.mPreferenceLockdown.setDisabledByAdmin(profileOrDeviceOwner);
                }
            }
            if (this.mVpnManager.isAlwaysOnVpnPackageSupportedForUser(this.mUserId, this.mPackageName)) {
                this.mPreferenceAlwaysOn.setSummary(R$string.vpn_always_on_summary);
                return;
            }
            this.mPreferenceAlwaysOn.setEnabled(false);
            this.mPreferenceLockdown.setEnabled(false);
            this.mPreferenceAlwaysOn.setSummary(R$string.vpn_always_on_summary_not_supported);
        }
    }

    private String getAlwaysOnVpnPackage() {
        return this.mVpnManager.getAlwaysOnVpnPackageForUser(this.mUserId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVpnAlwaysOn() {
        return this.mPackageName.equals(getAlwaysOnVpnPackage());
    }

    private boolean loadInfo() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e("AppManagementFragment", "empty bundle");
            return false;
        }
        String string = arguments.getString("package");
        this.mPackageName = string;
        if (string == null) {
            Log.e("AppManagementFragment", "empty package name");
            return false;
        }
        try {
            this.mPackageInfo = this.mPackageManager.getPackageInfo(string, 0);
            this.mVpnLabel = VpnConfig.getVpnLabel(getPrefContext(), this.mPackageName).toString();
            if (this.mPackageInfo.applicationInfo == null) {
                Log.e("AppManagementFragment", "package does not include an application");
                return false;
            } else if (appHasVpnPermission(getContext(), this.mPackageInfo.applicationInfo)) {
                return true;
            } else {
                Log.e("AppManagementFragment", "package didn't register VPN profile");
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppManagementFragment", "package not found", e);
            return false;
        }
    }

    static boolean appHasVpnPermission(Context context, ApplicationInfo applicationInfo) {
        return !ArrayUtils.isEmpty(((AppOpsManager) context.getSystemService("appops")).getOpsForPackage(applicationInfo.uid, applicationInfo.packageName, new int[]{47, 94}));
    }

    private boolean isAnotherVpnActive() {
        VpnConfig vpnConfig = this.mVpnManager.getVpnConfig(this.mUserId);
        return (vpnConfig == null || TextUtils.equals(vpnConfig.user, this.mPackageName)) ? false : true;
    }

    /* loaded from: classes.dex */
    public static class CannotConnectFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 547;
        }

        public static void show(AppManagementFragment appManagementFragment, String str) {
            if (appManagementFragment.getFragmentManager().findFragmentByTag("CannotConnect") == null) {
                Bundle bundle = new Bundle();
                bundle.putString("label", str);
                CannotConnectFragment cannotConnectFragment = new CannotConnectFragment();
                cannotConnectFragment.setArguments(bundle);
                cannotConnectFragment.show(appManagementFragment.getFragmentManager(), "CannotConnect");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R$string.vpn_cant_connect_title, new Object[]{getArguments().getString("label")})).setMessage(getActivity().getString(R$string.vpn_cant_connect_message)).setPositiveButton(R$string.okay, (DialogInterface.OnClickListener) null).create();
        }
    }
}
