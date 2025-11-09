package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.accessibility.AccessibilityManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.content.PackageMonitor;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class AccessibilitySettings extends DashboardFragment {
    private static final String[] CATEGORIES = {"screen_reader_category", "captions_category", "audio_category", "display_category", "interaction_control_category", "user_installed_services_category"};
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_settings) { // from class: com.android.settings.accessibility.AccessibilitySettings.3
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            return FeatureFactory.getFactory(context).getAccessibilitySearchFeatureProvider().getSearchIndexableRawData(context);
        }
    };
    final AccessibilitySettingsContentObserver mSettingsContentObserver;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.AccessibilitySettings.1
        @Override // java.lang.Runnable
        public void run() {
            if (AccessibilitySettings.this.getActivity() != null) {
                AccessibilitySettings.this.onContentChanged();
            }
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.AccessibilitySettings.2
        public void onPackageAdded(String str, int i) {
            sendUpdate();
        }

        public void onPackageAppeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageDisappeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageRemoved(String str, int i) {
            sendUpdate();
        }

        private void sendUpdate() {
            AccessibilitySettings.this.mHandler.postDelayed(AccessibilitySettings.this.mUpdateRunnable, 1000L);
        }
    };
    private final Map<String, PreferenceCategory> mCategoryToPrefCategoryMap = new ArrayMap();
    private final Map<Preference, PreferenceCategory> mServicePreferenceToPreferenceCategoryMap = new ArrayMap();
    private final Map<ComponentName, PreferenceCategory> mPreBundledServiceComponentToCategoryMap = new ArrayMap();
    private boolean mNeedPreferencesUpdate = false;
    private boolean mIsForeground = true;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccessibilitySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 2;
    }

    protected void updateSystemPreferences() {
    }

    public AccessibilitySettings() {
        Collection<AccessibilityShortcutController.ToggleableFrameworkFeatureInfo> values = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap().values();
        ArrayList arrayList = new ArrayList(values.size());
        for (AccessibilityShortcutController.ToggleableFrameworkFeatureInfo toggleableFrameworkFeatureInfo : values) {
            arrayList.add(toggleableFrameworkFeatureInfo.getSettingKey());
        }
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        AccessibilitySettingsContentObserver accessibilitySettingsContentObserver = new AccessibilitySettingsContentObserver(this.mHandler);
        this.mSettingsContentObserver = accessibilitySettingsContentObserver;
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.AccessibilitySettings$$ExternalSyntheticLambda0
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                AccessibilitySettings.this.lambda$new$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str) {
        onContentChanged();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_accessibility;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AccessibilityHearingAidPreferenceController) use(AccessibilityHearingAidPreferenceController.class)).setFragmentManager(getFragmentManager());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
        updateAllPreferences();
        registerContentMonitors();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateAllPreferences();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        if (this.mNeedPreferencesUpdate) {
            updateAllPreferences();
            this.mNeedPreferencesUpdate = false;
        }
        this.mIsForeground = true;
        super.onStart();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mIsForeground = false;
        super.onStop();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        unregisterContentMonitors();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_settings;
    }

    public static CharSequence getServiceSummary(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        CharSequence text;
        if (z && accessibilityServiceInfo.crashed) {
            return context.getText(R$string.accessibility_summary_state_stopped);
        }
        if (AccessibilityUtil.getAccessibilityServiceFragmentType(accessibilityServiceInfo) == 1) {
            if (AccessibilityUtil.getUserShortcutTypesFromSettings(context, new ComponentName(accessibilityServiceInfo.getResolveInfo().serviceInfo.packageName, accessibilityServiceInfo.getResolveInfo().serviceInfo.name)) != 0) {
                text = context.getText(R$string.accessibility_summary_shortcut_enabled);
            } else {
                text = context.getText(R$string.accessibility_summary_shortcut_disabled);
            }
        } else if (z) {
            text = context.getText(R$string.accessibility_summary_state_enabled);
        } else {
            text = context.getText(R$string.accessibility_summary_state_disabled);
        }
        CharSequence loadSummary = accessibilityServiceInfo.loadSummary(context.getPackageManager());
        return TextUtils.isEmpty(loadSummary) ? text : context.getString(R$string.preference_summary_default_combination, text, loadSummary);
    }

    public static CharSequence getServiceDescription(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        if (z && accessibilityServiceInfo.crashed) {
            return context.getText(R$string.accessibility_description_state_stopped);
        }
        return accessibilityServiceInfo.loadDescription(context.getPackageManager());
    }

    void onContentChanged() {
        if (this.mIsForeground) {
            updateAllPreferences();
        } else {
            this.mNeedPreferencesUpdate = true;
        }
    }

    private void initializeAllPreferences() {
        int i = 0;
        while (true) {
            String[] strArr = CATEGORIES;
            if (i >= strArr.length) {
                return;
            }
            this.mCategoryToPrefCategoryMap.put(strArr[i], (PreferenceCategory) findPreference(strArr[i]));
            i++;
        }
    }

    void updateAllPreferences() {
        updateSystemPreferences();
        updateServicePreferences();
    }

    private void registerContentMonitors() {
        FragmentActivity activity = getActivity();
        this.mSettingsPackageMonitor.register(activity, activity.getMainLooper(), false);
        this.mSettingsContentObserver.register(getContentResolver());
    }

    private void unregisterContentMonitors() {
        this.mSettingsPackageMonitor.unregister();
        this.mSettingsContentObserver.unregister(getContentResolver());
    }

    protected void updateServicePreferences() {
        ArrayList arrayList = new ArrayList(this.mServicePreferenceToPreferenceCategoryMap.keySet());
        for (int i = 0; i < arrayList.size(); i++) {
            Preference preference = (Preference) arrayList.get(i);
            this.mServicePreferenceToPreferenceCategoryMap.get(preference).removePreference(preference);
        }
        initializePreBundledServicesMapFromArray("screen_reader_category", R$array.config_preinstalled_screen_reader_services);
        initializePreBundledServicesMapFromArray("captions_category", R$array.config_preinstalled_captions_services);
        initializePreBundledServicesMapFromArray("audio_category", R$array.config_preinstalled_audio_services);
        initializePreBundledServicesMapFromArray("display_category", R$array.config_preinstalled_display_services);
        initializePreBundledServicesMapFromArray("interaction_control_category", R$array.config_preinstalled_interaction_control_services);
        List<RestrictedPreference> installedAccessibilityList = getInstalledAccessibilityList(getPrefContext());
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get("user_installed_services_category");
        int size = installedAccessibilityList.size();
        for (int i2 = 0; i2 < size; i2++) {
            RestrictedPreference restrictedPreference = installedAccessibilityList.get(i2);
            ComponentName componentName = (ComponentName) restrictedPreference.getExtras().getParcelable("component_name");
            PreferenceCategory preferenceCategory2 = this.mPreBundledServiceComponentToCategoryMap.containsKey(componentName) ? this.mPreBundledServiceComponentToCategoryMap.get(componentName) : preferenceCategory;
            preferenceCategory2.addPreference(restrictedPreference);
            this.mServicePreferenceToPreferenceCategoryMap.put(restrictedPreference, preferenceCategory2);
        }
        updateCategoryOrderFromArray("screen_reader_category", R$array.config_order_screen_reader_services);
        updateCategoryOrderFromArray("captions_category", R$array.config_order_captions_services);
        updateCategoryOrderFromArray("audio_category", R$array.config_order_audio_services);
        updateCategoryOrderFromArray("interaction_control_category", R$array.config_order_interaction_control_services);
        updateCategoryOrderFromArray("display_category", R$array.config_order_display_services);
        if (preferenceCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(preferenceCategory);
        } else {
            getPreferenceScreen().addPreference(preferenceCategory);
        }
        updatePreferenceCategoryVisibility("screen_reader_category");
    }

    private List<RestrictedPreference> getInstalledAccessibilityList(Context context) {
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(context);
        RestrictedPreferenceHelper restrictedPreferenceHelper = new RestrictedPreferenceHelper(context);
        final List<AccessibilityShortcutInfo> installedAccessibilityShortcutListAsUser = accessibilityManager.getInstalledAccessibilityShortcutListAsUser(context, UserHandle.myUserId());
        ArrayList arrayList = new ArrayList(accessibilityManager.getInstalledAccessibilityServiceList());
        arrayList.removeIf(new Predicate() { // from class: com.android.settings.accessibility.AccessibilitySettings$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getInstalledAccessibilityList$1;
                lambda$getInstalledAccessibilityList$1 = AccessibilitySettings.this.lambda$getInstalledAccessibilityList$1(installedAccessibilityShortcutListAsUser, (AccessibilityServiceInfo) obj);
                return lambda$getInstalledAccessibilityList$1;
            }
        });
        List<RestrictedPreference> createAccessibilityActivityPreferenceList = restrictedPreferenceHelper.createAccessibilityActivityPreferenceList(installedAccessibilityShortcutListAsUser);
        List<RestrictedPreference> createAccessibilityServicePreferenceList = restrictedPreferenceHelper.createAccessibilityServicePreferenceList(arrayList);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.addAll(createAccessibilityActivityPreferenceList);
        arrayList2.addAll(createAccessibilityServicePreferenceList);
        return arrayList2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: containsTargetNameInList */
    public boolean lambda$getInstalledAccessibilityList$1(List<AccessibilityShortcutInfo> list, AccessibilityServiceInfo accessibilityServiceInfo) {
        ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
        String str = serviceInfo.packageName;
        CharSequence loadLabel = serviceInfo.loadLabel(getPackageManager());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ActivityInfo activityInfo = list.get(i).getActivityInfo();
            String str2 = activityInfo.packageName;
            CharSequence loadLabel2 = activityInfo.loadLabel(getPackageManager());
            if (str.equals(str2) && loadLabel.equals(loadLabel2)) {
                return true;
            }
        }
        return false;
    }

    private void initializePreBundledServicesMapFromArray(String str, int i) {
        String[] stringArray = getResources().getStringArray(i);
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        for (String str2 : stringArray) {
            this.mPreBundledServiceComponentToCategoryMap.put(ComponentName.unflattenFromString(str2), preferenceCategory);
        }
    }

    private void updateCategoryOrderFromArray(String str, int i) {
        String[] stringArray = getResources().getStringArray(i);
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        int preferenceCount = preferenceCategory.getPreferenceCount();
        int length = stringArray.length;
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    break;
                } else if (preferenceCategory.getPreference(i2).getKey().equals(stringArray[i3])) {
                    preferenceCategory.getPreference(i2).setOrder(i3);
                    break;
                } else {
                    i3++;
                }
            }
        }
    }

    private void updatePreferenceCategoryVisibility(String str) {
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        preferenceCategory.setVisible(preferenceCategory.getPreferenceCount() != 0);
    }
}
