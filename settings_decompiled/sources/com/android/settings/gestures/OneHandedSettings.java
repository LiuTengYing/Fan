package com.android.settings.gestures;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment;
import com.android.settings.gestures.OneHandedSettingsUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class OneHandedSettings extends AccessibilityShortcutPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.one_handed_settings) { // from class: com.android.settings.gestures.OneHandedSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return OneHandedSettingsUtils.isSupportOneHandedMode();
        }
    };
    private String mFeatureName;
    private OneHandedSettingsUtils mUtils;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1841;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected String getShortcutPreferenceKey() {
        return "one_handed_shortcuts_preference";
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected boolean showGeneralCategory() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    /* renamed from: updatePreferenceStates */
    public void lambda$onStart$1() {
        int i;
        OneHandedSettingsUtils.setUserId(UserHandle.myUserId());
        super.updatePreferenceStates();
        IllustrationPreference illustrationPreference = (IllustrationPreference) getPreferenceScreen().findPreference("one_handed_header");
        if (OneHandedSettingsUtils.isSwipeDownNotificationEnabled(getContext())) {
            i = R$raw.lottie_swipe_for_notifications;
        } else {
            i = R$raw.lottie_one_hand_mode;
        }
        illustrationPreference.setLottieAnimationResId(i);
        ((MainSwitchPreference) getPreferenceScreen().findPreference("gesture_one_handed_mode_enabled_main_switch")).addOnSwitchChangeListener(new OnMainSwitchChangeListener() { // from class: com.android.settings.gestures.OneHandedSettings$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                OneHandedSettings.this.lambda$updatePreferenceStates$0(r1, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreferenceStates$0(Switch r1, boolean z) {
        r1.setChecked(z);
        if (z) {
            showQuickSettingsTooltipIfNeeded(1);
        }
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        int dialogMetricsCategory = super.getDialogMetricsCategory(i);
        if (dialogMetricsCategory == 0) {
            return 1841;
        }
        return dialogMetricsCategory;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected CharSequence getShortcutTitle() {
        return getText(R$string.one_handed_mode_shortcut_title);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        OneHandedSettingsUtils oneHandedSettingsUtils = new OneHandedSettingsUtils(getContext());
        this.mUtils = oneHandedSettingsUtils;
        oneHandedSettingsUtils.registerToggleAwareObserver(new OneHandedSettingsUtils.TogglesCallback() { // from class: com.android.settings.gestures.OneHandedSettings$$ExternalSyntheticLambda1
            @Override // com.android.settings.gestures.OneHandedSettingsUtils.TogglesCallback
            public final void onChange(Uri uri) {
                OneHandedSettings.this.lambda$onStart$2(uri);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$2(Uri uri) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() { // from class: com.android.settings.gestures.OneHandedSettings$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    OneHandedSettings.this.lambda$onStart$1();
                }
            });
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mUtils.unregisterToggleAwareObserver();
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected ComponentName getComponentName() {
        return AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected CharSequence getLabelName() {
        return this.mFeatureName;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected ComponentName getTileComponentName() {
        return AccessibilityShortcutController.ONE_HANDED_TILE_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment
    protected CharSequence getTileTooltipContent(int i) {
        if (i == 0) {
            return getText(R$string.accessibility_one_handed_mode_qs_tooltip_content);
        }
        return getText(R$string.accessibility_one_handed_mode_auto_added_qs_tooltip_content);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.one_handed_settings;
    }

    @Override // com.android.settings.accessibility.AccessibilityShortcutPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.mFeatureName = getContext().getString(R$string.one_handed_title);
        super.onCreate(bundle);
    }
}
