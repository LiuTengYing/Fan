package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Switch;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.accessibility.AccessibilityServiceWarning;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class ToggleAccessibilityServicePreferenceFragment extends ToggleFeaturePreferenceFragment {
    private BroadcastReceiver mPackageRemovedReceiver;
    private ComponentName mTileComponentName;
    private Dialog mWarningDialog;
    private AtomicBoolean mIsDialogShown = new AtomicBoolean(false);
    private boolean mDisabledStateLogged = false;
    private long mStartTimeMillsForLogging = 0;

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return FeatureFactory.getFactory(getActivity().getApplicationContext()).getAccessibilityMetricsFeatureProvider().getDownloadedFeatureMetricsCategory((ComponentName) getArguments().getParcelable("component_name"));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null || !bundle.containsKey("has_logged")) {
            return;
        }
        this.mDisabledStateLogged = bundle.getBoolean("has_logged");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        super.registerKeysToObserverCallback(accessibilitySettingsContentObserver);
        accessibilitySettingsContentObserver.registerObserverCallback(new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda5
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleAccessibilityServicePreferenceFragment.this.lambda$registerKeysToObserverCallback$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$0(String str) {
        updateSwitchBarToggleSwitch();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            getActivity().finishAndRemoveTask();
        } else if (AccessibilityUtil.isSystemApp(accessibilityServiceInfo)) {
        } else {
            registerPackageRemoveReceiver();
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mStartTimeMillsForLogging > 0) {
            bundle.putBoolean("has_logged", this.mDisabledStateLogged);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onPreferenceToggled(String str, boolean z) {
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(unflattenFromString, z);
        if (!z) {
            logDisabledState(unflattenFromString.getPackageName());
        }
        AccessibilityUtils.setAccessibilityServiceState(getPrefContext(), unflattenFromString, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityServiceInfo getAccessibilityServiceInfo() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityServiceList();
        int size = installedAccessibilityServiceList.size();
        for (int i = 0; i < size; i++) {
            AccessibilityServiceInfo accessibilityServiceInfo = installedAccessibilityServiceList.get(i);
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            if (this.mComponentName.getPackageName().equals(resolveInfo.serviceInfo.packageName) && this.mComponentName.getClassName().equals(resolveInfo.serviceInfo.name)) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        switch (i) {
            case 1002:
                if (accessibilityServiceInfo == null) {
                    return null;
                }
                Dialog createCapabilitiesDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromEnableToggleClicked(view);
                    }
                }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda1
                    @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                    public final void uninstallPackage() {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                    }
                });
                this.mWarningDialog = createCapabilitiesDialog;
                return createCapabilitiesDialog;
            case 1003:
                if (accessibilityServiceInfo == null) {
                    return null;
                }
                Dialog createCapabilitiesDialog2 = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutClicked(view);
                    }
                }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda1
                    @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                    public final void uninstallPackage() {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                    }
                });
                this.mWarningDialog = createCapabilitiesDialog2;
                return createCapabilitiesDialog2;
            case 1004:
                if (accessibilityServiceInfo == null) {
                    return null;
                }
                Dialog createCapabilitiesDialog3 = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutToggleClicked(view);
                    }
                }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda1
                    @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                    public final void uninstallPackage() {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                    }
                });
                this.mWarningDialog = createCapabilitiesDialog3;
                return createCapabilitiesDialog3;
            case 1005:
                if (accessibilityServiceInfo == null) {
                    return null;
                }
                Dialog createDisableDialog = AccessibilityServiceWarning.createDisableDialog(getPrefContext(), accessibilityServiceInfo, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromDisableToggleClicked(dialogInterface, i2);
                    }
                });
                this.mWarningDialog = createDisableDialog;
                return createDisableDialog;
            default:
                return super.onCreateDialog(i);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 1008) {
            switch (i) {
                case 1002:
                case 1003:
                case 1004:
                    return 583;
                case 1005:
                    return 584;
                default:
                    return super.getDialogMetricsCategory(i);
            }
        }
        return 1810;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    ComponentName getTileComponentName() {
        return this.mTileComponentName;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    CharSequence getTileTooltipContent(int i) {
        CharSequence loadTileLabel;
        int i2;
        ComponentName tileComponentName = getTileComponentName();
        if (tileComponentName == null || (loadTileLabel = loadTileLabel(getPrefContext(), tileComponentName)) == null) {
            return null;
        }
        if (i == 0) {
            i2 = R$string.accessibility_service_qs_tooltip_content;
        } else {
            i2 = R$string.accessibility_service_auto_added_qs_tooltip_content;
        }
        return getString(i2, loadTileLabel);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateSwitchBarToggleSwitch() {
        boolean isAccessibilityServiceEnabled = isAccessibilityServiceEnabled();
        if (this.mToggleServiceSwitchPreference.isChecked() == isAccessibilityServiceEnabled) {
            return;
        }
        this.mToggleServiceSwitchPreference.setChecked(isAccessibilityServiceEnabled);
    }

    private boolean isAccessibilityServiceEnabled() {
        return AccessibilityUtils.getEnabledServicesFromSettings(getPrefContext()).contains(this.mComponentName);
    }

    private void registerPackageRemoveReceiver() {
        if (this.mPackageRemovedReceiver != null || getContext() == null) {
            return;
        }
        this.mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (TextUtils.equals(ToggleAccessibilityServicePreferenceFragment.this.mComponentName.getPackageName(), intent.getData().getSchemeSpecificPart())) {
                    ToggleAccessibilityServicePreferenceFragment.this.getActivity().finishAndRemoveTask();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
    }

    private void unregisterPackageRemoveReceiver() {
        if (this.mPackageRemovedReceiver == null || getContext() == null) {
            return;
        }
        getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        this.mPackageRemovedReceiver = null;
    }

    private boolean isServiceSupportAccessibilityButton() {
        ServiceInfo serviceInfo;
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if ((accessibilityServiceInfo.flags & 256) != 0 && (serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo) != null && TextUtils.equals(serviceInfo.name, getAccessibilityServiceInfo().getResolveInfo().serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }

    private void handleConfirmServiceEnabled(boolean z) {
        getArguments().putBoolean("checked", z);
        onPreferenceToggled(this.mPreferenceKey, z);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z != isAccessibilityServiceEnabled()) {
            onPreferenceClick(z);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
        if (shortcutPreference.isChecked()) {
            if (!this.mToggleServiceSwitchPreference.isChecked()) {
                shortcutPreference.setChecked(false);
                showPopupDialog(1004);
            } else {
                AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
                showPopupDialog(1008);
            }
        } else {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showPopupDialog(this.mShortcutPreference.isChecked() || this.mToggleServiceSwitchPreference.isChecked() ? 1 : 1003);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onProcessArguments(Bundle bundle) {
        super.onProcessArguments(bundle);
        String string = bundle.getString("settings_title");
        String string2 = bundle.getString("settings_component_name");
        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
            Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string2.toString()));
            if (!getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
                this.mSettingsTitle = string;
                this.mSettingsIntent = component;
                setHasOptionsMenu(true);
            }
        }
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        int i = bundle.getInt("animated_image_res");
        if (i > 0) {
            this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(i)).build();
        }
        this.mPackageName = getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager());
        if (bundle.containsKey("tile_service_component_name")) {
            this.mTileComponentName = ComponentName.unflattenFromString(bundle.getString("tile_service_component_name"));
        }
        this.mStartTimeMillsForLogging = bundle.getLong("start_time_to_log_a11y_tool");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromDisableToggleClicked(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            handleConfirmServiceEnabled(true);
        } else if (i == -1) {
            handleConfirmServiceEnabled(false);
        } else {
            throw new IllegalArgumentException("Unexpected button identifier");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromEnableToggleClicked(View view) {
        int id = view.getId();
        if (id == R$id.permission_enable_allow_button) {
            onAllowButtonFromEnableToggleClicked();
        } else if (id == R$id.permission_enable_deny_button) {
            onDenyButtonFromEnableToggleClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromUninstallClicked() {
        this.mWarningDialog.dismiss();
        Intent createUninstallPackageActivityIntent = createUninstallPackageActivityIntent();
        if (createUninstallPackageActivityIntent == null) {
            return;
        }
        startActivity(createUninstallPackageActivityIntent);
    }

    private Intent createUninstallPackageActivityIntent() {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            Log.w("ToggleAccessibilityServicePreferenceFragment", "createUnInstallIntent -- invalid a11yServiceInfo");
            return null;
        }
        ApplicationInfo applicationInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo.applicationInfo;
        return new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + applicationInfo.packageName));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        unregisterPackageRemoveReceiver();
    }

    private void onAllowButtonFromEnableToggleClicked() {
        handleConfirmServiceEnabled(true);
        if (isServiceSupportAccessibilityButton()) {
            this.mIsDialogShown.set(false);
            showPopupDialog(1008);
        }
        this.mWarningDialog.dismiss();
    }

    private void onDenyButtonFromEnableToggleClicked() {
        handleConfirmServiceEnabled(false);
        this.mWarningDialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutToggleClicked(View view) {
        int id = view.getId();
        if (id == R$id.permission_enable_allow_button) {
            onAllowButtonFromShortcutToggleClicked();
        } else if (id == R$id.permission_enable_deny_button) {
            onDenyButtonFromShortcutToggleClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    private void onAllowButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(true);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1), this.mComponentName);
        this.mIsDialogShown.set(false);
        showPopupDialog(1008);
        this.mWarningDialog.dismiss();
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    private void onDenyButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(false);
        this.mWarningDialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutClicked(View view) {
        int id = view.getId();
        if (id == R$id.permission_enable_allow_button) {
            onAllowButtonFromShortcutClicked();
        } else if (id == R$id.permission_enable_deny_button) {
            onDenyButtonFromShortcutClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    private void onAllowButtonFromShortcutClicked() {
        this.mIsDialogShown.set(false);
        showPopupDialog(1);
        this.mWarningDialog.dismiss();
    }

    private void onDenyButtonFromShortcutClicked() {
        this.mWarningDialog.dismiss();
    }

    private boolean onPreferenceClick(boolean z) {
        if (z) {
            this.mToggleServiceSwitchPreference.setChecked(false);
            getArguments().putBoolean("checked", false);
            if (!this.mShortcutPreference.isChecked()) {
                showPopupDialog(1002);
            } else {
                handleConfirmServiceEnabled(true);
                if (isServiceSupportAccessibilityButton()) {
                    showPopupDialog(1008);
                }
            }
        } else {
            this.mToggleServiceSwitchPreference.setChecked(true);
            getArguments().putBoolean("checked", true);
            showDialog(1005);
        }
        return true;
    }

    private void showPopupDialog(int i) {
        if (this.mIsDialogShown.compareAndSet(false, true)) {
            showDialog(i);
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda6
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ToggleAccessibilityServicePreferenceFragment.this.lambda$showPopupDialog$1(dialogInterface);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopupDialog$1(DialogInterface dialogInterface) {
        this.mIsDialogShown.compareAndSet(true, false);
    }

    private void logDisabledState(String str) {
        if (this.mStartTimeMillsForLogging <= 0 || this.mDisabledStateLogged) {
            return;
        }
        AccessibilityStatsLogUtils.logDisableNonA11yCategoryService(str, SystemClock.elapsedRealtime() - this.mStartTimeMillsForLogging);
        this.mDisabledStateLogged = true;
    }
}
