package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes.dex */
public abstract class ZenModeRuleSettingsBase extends ZenModeSettingsBase {
    protected static final boolean DEBUG = ZenModeSettingsBase.DEBUG;
    private final String CUSTOM_BEHAVIOR_KEY = "zen_custom_setting";
    protected ZenRuleButtonsPreferenceController mActionButtons;
    protected Preference mCustomBehaviorPreference;
    protected boolean mDisableListeners;
    protected ZenAutomaticRuleHeaderPreferenceController mHeader;
    protected String mId;
    protected AutomaticZenRule mRule;
    protected ZenAutomaticRuleSwitchPreferenceController mSwitch;

    protected abstract void onCreateInternal();

    protected abstract boolean setRule(AutomaticZenRule automaticZenRule);

    protected abstract void updateControlsInternal();

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent intent = getActivity().getIntent();
        boolean z = DEBUG;
        if (z) {
            Log.d("ZenModeSettings", "onCreate getIntent()=" + intent);
        }
        if (intent == null) {
            Log.w("ZenModeSettings", "No intent");
            toastAndFinish();
            return;
        }
        String stringExtra = intent.getStringExtra("android.service.notification.extra.RULE_ID");
        this.mId = stringExtra;
        if (stringExtra == null) {
            String stringExtra2 = intent.getStringExtra("android.app.extra.AUTOMATIC_RULE_ID");
            this.mId = stringExtra2;
            if (stringExtra2 == null) {
                Log.w("ZenModeSettings", "rule id is null");
                toastAndFinish();
                return;
            }
        }
        if (z) {
            Log.d("ZenModeSettings", "mId=" + this.mId);
        }
        refreshRuleOrFinish();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isFinishingOrDestroyed()) {
            return;
        }
        Preference findPreference = getPreferenceScreen().findPreference("zen_custom_setting");
        this.mCustomBehaviorPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeRuleSettingsBase.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Bundle bundle2 = new Bundle();
                bundle2.putString("RULE_ID", ZenModeRuleSettingsBase.this.mId);
                new SubSettingLauncher(ZenModeRuleSettingsBase.this.mContext).setDestination(ZenCustomRuleSettings.class.getName()).setArguments(bundle2).setSourceMetricsCategory(0).launch();
                return true;
            }
        });
        onCreateInternal();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (isUiRestricted() || refreshRuleOrFinish()) {
            return;
        }
        updateControls();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Utils.setActionBarShadowAnimation(getActivity(), getSettingsLifecycle(), getListView());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_interruptions;
    }

    protected void updateHeader() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mSwitch.displayPreference(preferenceScreen);
        updatePreference(this.mSwitch);
        this.mHeader.displayPreference(preferenceScreen);
        updatePreference(this.mHeader);
        this.mActionButtons.displayPreference(preferenceScreen);
        updatePreference(this.mActionButtons);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateRule(Uri uri) {
        this.mRule.setConditionId(uri);
        this.mBackend.updateZenRule(this.mId, this.mRule);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.notification.zen.ZenModeSettingsBase
    public void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
        if (refreshRuleOrFinish()) {
            return;
        }
        updateControls();
    }

    private boolean refreshRuleOrFinish() {
        this.mRule = getZenRule();
        if (DEBUG) {
            Log.d("ZenModeSettings", "mRule=" + this.mRule);
        }
        this.mHeader.setRule(this.mRule);
        this.mSwitch.setIdAndRule(this.mId, this.mRule);
        this.mActionButtons.setIdAndRule(this.mId, this.mRule);
        if (setRule(this.mRule)) {
            return false;
        }
        toastAndFinish();
        return true;
    }

    private void toastAndFinish() {
        Toast.makeText(this.mContext, R$string.zen_mode_rule_not_found_text, 0).show();
        getActivity().finish();
    }

    private AutomaticZenRule getZenRule() {
        return NotificationManager.from(this.mContext).getAutomaticZenRule(this.mId);
    }

    private void updateControls() {
        this.mDisableListeners = true;
        updateControlsInternal();
        updateHeader();
        if (this.mRule.getZenPolicy() == null) {
            this.mCustomBehaviorPreference.setSummary(R$string.zen_mode_custom_behavior_summary_default);
        } else {
            this.mCustomBehaviorPreference.setSummary(R$string.zen_mode_custom_behavior_summary);
        }
        this.mDisableListeners = false;
    }
}
