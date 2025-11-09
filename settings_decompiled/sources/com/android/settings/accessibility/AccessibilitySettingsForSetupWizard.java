package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.RestrictedPreference;
import com.google.android.setupdesign.GlifPreferenceLayout;
/* loaded from: classes.dex */
public class AccessibilitySettingsForSetupWizard extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    private Preference mDisplayMagnificationPreference;
    private RestrictedPreference mScreenReaderPreference;
    private RestrictedPreference mSelectToSpeakPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccessibilitySettingsForSetupWizard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 367;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        String string = getContext().getString(R$string.vision_settings_title);
        String string2 = getContext().getString(R$string.vision_settings_description);
        Drawable drawable = getContext().getDrawable(R$drawable.ic_accessibility_visibility);
        AccessibilitySetupWizardUtils.updateGlifPreferenceLayout(getContext(), (GlifPreferenceLayout) view, string, string2, drawable);
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDisplayMagnificationPreference = findPreference("screen_magnification_preference");
        this.mScreenReaderPreference = (RestrictedPreference) findPreference("screen_reader_preference");
        this.mSelectToSpeakPreference = (RestrictedPreference) findPreference("select_to_speak_preference");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateAccessibilityServicePreference(this.mScreenReaderPreference, "com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService", VolumeShortcutToggleScreenReaderPreferenceFragmentForSetupWizard.class.getName());
        updateAccessibilityServicePreference(this.mSelectToSpeakPreference, "com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService", VolumeShortcutToggleSelectToSpeakPreferenceFragmentForSetupWizard.class.getName());
        configureMagnificationPreferenceIfNeeded(this.mDisplayMagnificationPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(false);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        Preference preference2 = this.mDisplayMagnificationPreference;
        if (preference2 == preference) {
            preference2.getExtras().putBoolean("from_suw", true);
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_settings_for_setup_wizard;
    }

    private AccessibilityServiceInfo findService(String str, String str2) {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) getActivity().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            if (TextUtils.equals(str, serviceInfo.packageName) && TextUtils.equals(str2, serviceInfo.name)) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    private void updateAccessibilityServicePreference(RestrictedPreference restrictedPreference, String str, String str2, String str3) {
        AccessibilityServiceInfo findService = findService(str, str2);
        if (findService == null) {
            getPreferenceScreen().removePreference(restrictedPreference);
            return;
        }
        ServiceInfo serviceInfo = findService.getResolveInfo().serviceInfo;
        restrictedPreference.setIcon(Utils.getAdaptiveIcon(getContext(), findService.getResolveInfo().loadIcon(getPackageManager()), -1));
        restrictedPreference.setIconSize(1);
        String charSequence = findService.getResolveInfo().loadLabel(getPackageManager()).toString();
        restrictedPreference.setTitle(charSequence);
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        restrictedPreference.setKey(componentName.flattenToString());
        if (AccessibilityUtil.getAccessibilityServiceFragmentType(findService) == 0) {
            restrictedPreference.setFragment(str3);
        }
        Bundle extras = restrictedPreference.getExtras();
        extras.putParcelable("component_name", componentName);
        extras.putString("preference_key", restrictedPreference.getKey());
        extras.putString("title", charSequence);
        extras.putString("summary", findService.loadDescription(getPackageManager()));
        extras.putInt("animated_image_res", findService.getAnimatedImageRes());
        extras.putString("html_description", findService.loadHtmlDescription(getPackageManager()));
    }

    private static void configureMagnificationPreferenceIfNeeded(Preference preference) {
        Context context = preference.getContext();
        preference.setFragment(ToggleScreenMagnificationPreferenceFragmentForSetupWizard.class.getName());
        MagnificationGesturesPreferenceController.populateMagnificationGesturesPreferenceExtras(preference.getExtras(), context);
    }
}
