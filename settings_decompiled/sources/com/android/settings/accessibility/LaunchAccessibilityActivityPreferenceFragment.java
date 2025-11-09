package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class LaunchAccessibilityActivityPreferenceFragment extends ToggleFeaturePreferenceFragment {
    private ComponentName mTileComponentName;

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return FeatureFactory.getFactory(getActivity().getApplicationContext()).getAccessibilityMetricsFeatureProvider().getDownloadedFeatureMetricsCategory((ComponentName) getArguments().getParcelable("component_name"));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        initLaunchPreference();
        removePreference("use_service");
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onProcessArguments(Bundle bundle) {
        super.onProcessArguments(bundle);
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        this.mPackageName = getAccessibilityShortcutInfo().getActivityInfo().loadLabel(getPackageManager()).toString();
        int i = bundle.getInt("animated_image_res");
        if (i > 0) {
            this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(i)).build();
        }
        this.mHtmlDescription = bundle.getCharSequence("html_description");
        String string = bundle.getString("settings_title");
        Intent settingsIntent = TextUtils.isEmpty(string) ? null : getSettingsIntent(bundle);
        this.mSettingsIntent = settingsIntent;
        if (settingsIntent == null) {
            string = null;
        }
        this.mSettingsTitle = string;
        if (bundle.containsKey("tile_service_component_name")) {
            this.mTileComponentName = ComponentName.unflattenFromString(bundle.getString("tile_service_component_name"));
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
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

    private AccessibilityShortcutInfo getAccessibilityShortcutInfo() {
        List installedAccessibilityShortcutListAsUser = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityShortcutListAsUser(getPrefContext(), UserHandle.myUserId());
        int size = installedAccessibilityShortcutListAsUser.size();
        for (int i = 0; i < size; i++) {
            AccessibilityShortcutInfo accessibilityShortcutInfo = (AccessibilityShortcutInfo) installedAccessibilityShortcutListAsUser.get(i);
            ActivityInfo activityInfo = accessibilityShortcutInfo.getActivityInfo();
            if (this.mComponentName.getPackageName().equals(activityInfo.packageName) && this.mComponentName.getClassName().equals(activityInfo.name)) {
                return accessibilityShortcutInfo;
            }
        }
        return null;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("animated_image");
        arrayList.add("launch_preference");
        arrayList.add("general_categories");
        arrayList.add("html_description");
        return arrayList;
    }

    private void initLaunchPreference() {
        Preference preference = new Preference(getPrefContext());
        preference.setLayoutResource(R$layout.accessibility_launch_activity_preference);
        preference.setKey("launch_preference");
        AccessibilityShortcutInfo accessibilityShortcutInfo = getAccessibilityShortcutInfo();
        preference.setTitle(accessibilityShortcutInfo == null ? "" : getString(R$string.accessibility_service_primary_open_title, accessibilityShortcutInfo.getActivityInfo().loadLabel(getPackageManager())));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accessibility.LaunchAccessibilityActivityPreferenceFragment$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$initLaunchPreference$0;
                lambda$initLaunchPreference$0 = LaunchAccessibilityActivityPreferenceFragment.this.lambda$initLaunchPreference$0(preference2);
                return lambda$initLaunchPreference$0;
            }
        });
        getPreferenceScreen().addPreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initLaunchPreference$0(Preference preference) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, true);
        launchShortcutTargetActivity(getPrefContext().getDisplayId(), this.mComponentName);
        return true;
    }

    private void launchShortcutTargetActivity(int i, ComponentName componentName) {
        Intent intent = new Intent();
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle();
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        try {
            getPrefContext().startActivityAsUser(intent, bundle, UserHandle.of(UserHandle.myUserId()));
        } catch (ActivityNotFoundException unused) {
            Log.w("LaunchA11yActivity", "Target activity not found.");
        }
    }

    private Intent getSettingsIntent(Bundle bundle) {
        String string = bundle.getString("settings_component_name");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string));
        if (getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
            return null;
        }
        return component;
    }
}
