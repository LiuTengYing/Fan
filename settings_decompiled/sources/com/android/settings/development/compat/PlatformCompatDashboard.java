package com.android.settings.development.compat;

import android.app.AlertDialog;
import android.compat.Compatibility;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.ArraySet;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.internal.compat.AndroidBuildClassifier;
import com.android.internal.compat.CompatibilityChangeConfig;
import com.android.internal.compat.CompatibilityChangeInfo;
import com.android.internal.compat.IPlatformCompat;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.AppPicker;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class PlatformCompatDashboard extends DashboardFragment {
    private CompatibilityChangeInfo[] mChanges;
    private IPlatformCompat mPlatformCompat;
    String mSelectedApp;
    private AndroidBuildClassifier mAndroidBuildClassifier = new AndroidBuildClassifier();
    private boolean mShouldStartAppPickerOnResume = true;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PlatformCompatDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1805;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.platform_compat_settings;
    }

    IPlatformCompat getPlatformCompat() {
        if (this.mPlatformCompat == null) {
            this.mPlatformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
        }
        return this.mPlatformCompat;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.mChanges = getPlatformCompat().listUIChanges();
            if (bundle != null) {
                this.mShouldStartAppPickerOnResume = false;
                this.mSelectedApp = bundle.getString("compat_app");
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Could not list changes!", e);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 6) {
            this.mShouldStartAppPickerOnResume = false;
            if (i2 == -2) {
                this.mSelectedApp = null;
                return;
            } else if (i2 == -1) {
                this.mSelectedApp = intent.getAction();
                return;
            } else if (i2 == 0 && TextUtils.isEmpty(this.mSelectedApp)) {
                finish();
                return;
            } else {
                return;
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (isFinishingOrDestroyed()) {
            return;
        }
        if (!this.mShouldStartAppPickerOnResume) {
            if (TextUtils.isEmpty(this.mSelectedApp)) {
                new AlertDialog.Builder(getContext()).setTitle(R$string.platform_compat_dialog_title_no_apps).setMessage(R$string.platform_compat_dialog_text_no_apps).setPositiveButton(R$string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.compat.PlatformCompatDashboard$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PlatformCompatDashboard.this.lambda$onResume$0(dialogInterface, i);
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.development.compat.PlatformCompatDashboard$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        PlatformCompatDashboard.this.lambda$onResume$1(dialogInterface);
                    }
                }).setCancelable(false).show();
                return;
            }
            try {
                addPreferences(getApplicationInfo());
                return;
            } catch (PackageManager.NameNotFoundException unused) {
                this.mShouldStartAppPickerOnResume = true;
                this.mSelectedApp = null;
            }
        }
        startAppPicker();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$0(DialogInterface dialogInterface, int i) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$1(DialogInterface dialogInterface) {
        finish();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("compat_app", this.mSelectedApp);
    }

    private void addPreferences(ApplicationInfo applicationInfo) {
        CompatibilityChangeInfo[] compatibilityChangeInfoArr;
        List list;
        getPreferenceScreen().removeAll();
        getPreferenceScreen().addPreference(createAppPreference(applicationInfo));
        CompatibilityChangeConfig appChangeMappings = getAppChangeMappings();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        TreeMap treeMap = new TreeMap();
        for (CompatibilityChangeInfo compatibilityChangeInfo : this.mChanges) {
            if (compatibilityChangeInfo.getEnableSinceTargetSdk() > 0) {
                if (!treeMap.containsKey(Integer.valueOf(compatibilityChangeInfo.getEnableSinceTargetSdk()))) {
                    list = new ArrayList();
                    treeMap.put(Integer.valueOf(compatibilityChangeInfo.getEnableSinceTargetSdk()), list);
                } else {
                    list = (List) treeMap.get(Integer.valueOf(compatibilityChangeInfo.getEnableSinceTargetSdk()));
                }
                list.add(compatibilityChangeInfo);
            } else if (compatibilityChangeInfo.getDisabled()) {
                arrayList2.add(compatibilityChangeInfo);
            } else {
                arrayList.add(compatibilityChangeInfo);
            }
        }
        createChangeCategoryPreference(arrayList, appChangeMappings, getString(R$string.platform_compat_default_enabled_title));
        createChangeCategoryPreference(arrayList2, appChangeMappings, getString(R$string.platform_compat_default_disabled_title));
        for (Integer num : treeMap.keySet()) {
            createChangeCategoryPreference((List) treeMap.get(num), appChangeMappings, getString(R$string.platform_compat_target_sdk_title, num));
        }
    }

    private CompatibilityChangeConfig getAppChangeMappings() {
        try {
            return getPlatformCompat().getAppConfig(getApplicationInfo());
        } catch (PackageManager.NameNotFoundException | RemoteException e) {
            throw new RuntimeException("Could not get app config!", e);
        }
    }

    Preference createPreferenceForChange(Context context, CompatibilityChangeInfo compatibilityChangeInfo, CompatibilityChangeConfig compatibilityChangeConfig) {
        String str;
        boolean isChangeEnabled = compatibilityChangeConfig.isChangeEnabled(compatibilityChangeInfo.getId());
        SwitchPreference switchPreference = new SwitchPreference(context);
        if (compatibilityChangeInfo.getName() != null) {
            str = compatibilityChangeInfo.getName();
        } else {
            str = "Change_" + compatibilityChangeInfo.getId();
        }
        switchPreference.setSummary(str);
        switchPreference.setKey(str);
        try {
            switchPreference.setEnabled(getPlatformCompat().getOverrideValidator().getOverrideAllowedState(compatibilityChangeInfo.getId(), this.mSelectedApp).state == 0);
            switchPreference.setChecked(isChangeEnabled);
            switchPreference.setOnPreferenceChangeListener(new CompatChangePreferenceChangeListener(compatibilityChangeInfo.getId()));
            return switchPreference;
        } catch (RemoteException e) {
            throw new RuntimeException("Could not check if change can be overridden for app.", e);
        }
    }

    ApplicationInfo getApplicationInfo() throws PackageManager.NameNotFoundException {
        return getPackageManager().getApplicationInfo(this.mSelectedApp, 0);
    }

    Preference createAppPreference(ApplicationInfo applicationInfo) {
        Context context = getPreferenceScreen().getContext();
        Drawable loadIcon = applicationInfo.loadIcon(context.getPackageManager());
        Preference preference = new Preference(context);
        preference.setIcon(loadIcon);
        preference.setSummary(getString(R$string.platform_compat_selected_app_summary, this.mSelectedApp, Integer.valueOf(applicationInfo.targetSdkVersion)));
        preference.setKey(this.mSelectedApp);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.development.compat.PlatformCompatDashboard$$ExternalSyntheticLambda2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$createAppPreference$2;
                lambda$createAppPreference$2 = PlatformCompatDashboard.this.lambda$createAppPreference$2(preference2);
                return lambda$createAppPreference$2;
            }
        });
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createAppPreference$2(Preference preference) {
        startAppPicker();
        return true;
    }

    PreferenceCategory createChangeCategoryPreference(List<CompatibilityChangeInfo> list, CompatibilityChangeConfig compatibilityChangeConfig, String str) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPreferenceScreen().getContext());
        preferenceCategory.setTitle(str);
        getPreferenceScreen().addPreference(preferenceCategory);
        addChangePreferencesToCategory(list, preferenceCategory, compatibilityChangeConfig);
        return preferenceCategory;
    }

    private void addChangePreferencesToCategory(List<CompatibilityChangeInfo> list, PreferenceCategory preferenceCategory, CompatibilityChangeConfig compatibilityChangeConfig) {
        for (CompatibilityChangeInfo compatibilityChangeInfo : list) {
            preferenceCategory.addPreference(createPreferenceForChange(getPreferenceScreen().getContext(), compatibilityChangeInfo, compatibilityChangeConfig));
        }
    }

    private void startAppPicker() {
        Intent putExtra = new Intent(getContext(), AppPicker.class).putExtra("com.android.settings.extra.INCLUDE_NOTHING", false);
        if (!this.mAndroidBuildClassifier.isDebuggableBuild()) {
            putExtra.putExtra("com.android.settings.extra.DEBUGGABLE", true);
        }
        startActivityForResult(putExtra, 6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CompatChangePreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final long changeId;

        CompatChangePreferenceChangeListener(long j) {
            this.changeId = j;
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            try {
                ArraySet arraySet = new ArraySet();
                ArraySet arraySet2 = new ArraySet();
                if (((Boolean) obj).booleanValue()) {
                    arraySet.add(Long.valueOf(this.changeId));
                } else {
                    arraySet2.add(Long.valueOf(this.changeId));
                }
                PlatformCompatDashboard.this.getPlatformCompat().setOverrides(new CompatibilityChangeConfig(new Compatibility.ChangeConfig(arraySet, arraySet2)), PlatformCompatDashboard.this.mSelectedApp);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
