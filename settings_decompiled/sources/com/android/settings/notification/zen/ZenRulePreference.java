package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.ZenModeConfig;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.Map;
/* loaded from: classes.dex */
public class ZenRulePreference extends PrimarySwitchPreference {
    private static final ManagedServiceSettings.Config CONFIG = ZenModeAutomationSettings.getConditionProviderConfig();
    final ZenModeBackend mBackend;
    final Context mContext;
    final String mId;
    private Intent mIntent;
    final MetricsFeatureProvider mMetricsFeatureProvider;
    CharSequence mName;
    final Fragment mParent;
    final PackageManager mPm;
    final Preference mPref;
    AutomaticZenRule mRule;
    private final ZenRuleScheduleHelper mScheduleHelper;
    final ZenServiceListing mServiceListing;

    public ZenRulePreference(Context context, Map.Entry<String, AutomaticZenRule> entry, Fragment fragment, MetricsFeatureProvider metricsFeatureProvider) {
        super(context);
        this.mScheduleHelper = new ZenRuleScheduleHelper();
        this.mBackend = ZenModeBackend.getInstance(context);
        this.mContext = context;
        AutomaticZenRule value = entry.getValue();
        this.mRule = value;
        this.mName = value.getName();
        this.mId = entry.getKey();
        this.mParent = fragment;
        this.mPm = context.getPackageManager();
        ZenServiceListing zenServiceListing = new ZenServiceListing(context, CONFIG);
        this.mServiceListing = zenServiceListing;
        zenServiceListing.reloadApprovedServices();
        this.mPref = this;
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        setAttributes(this.mRule);
        setWidgetLayoutResource(getSecondTargetResId());
        super.setChecked(this.mRule.isEnabled());
    }

    public void updatePreference(AutomaticZenRule automaticZenRule) {
        if (!this.mRule.getName().equals(automaticZenRule.getName())) {
            String name = automaticZenRule.getName();
            this.mName = name;
            setTitle(name);
        }
        if (this.mRule.isEnabled() != automaticZenRule.isEnabled()) {
            setChecked(automaticZenRule.isEnabled());
        }
        setSummary(computeRuleSummary(automaticZenRule));
        this.mRule = automaticZenRule;
    }

    @Override // androidx.preference.Preference
    public void onClick() {
        this.mContext.startActivity(this.mIntent);
    }

    @Override // com.android.settingslib.PrimarySwitchPreference
    public void setChecked(boolean z) {
        this.mRule.setEnabled(z);
        this.mBackend.updateZenRule(this.mId, this.mRule);
        setAttributes(this.mRule);
        super.setChecked(z);
    }

    protected void setAttributes(AutomaticZenRule automaticZenRule) {
        boolean isValidScheduleConditionId = ZenModeConfig.isValidScheduleConditionId(automaticZenRule.getConditionId(), true);
        boolean isValidEventConditionId = ZenModeConfig.isValidEventConditionId(automaticZenRule.getConditionId());
        setSummary(computeRuleSummary(automaticZenRule));
        setTitle(this.mName);
        setPersistent(false);
        Intent ruleIntent = AbstractZenModeAutomaticRulePreferenceController.getRuleIntent(isValidScheduleConditionId ? "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS" : isValidEventConditionId ? "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS" : "", AbstractZenModeAutomaticRulePreferenceController.getSettingsActivity(this.mPm, automaticZenRule, this.mServiceListing.findService(automaticZenRule.getOwner())), this.mId);
        this.mIntent = ruleIntent;
        if (ruleIntent.resolveActivity(this.mPm) == null) {
            this.mIntent = null;
        }
        setKey(this.mId);
    }

    private String computeRuleSummary(AutomaticZenRule automaticZenRule) {
        if (automaticZenRule != null) {
            ZenModeConfig.ScheduleInfo tryParseScheduleConditionId = ZenModeConfig.tryParseScheduleConditionId(automaticZenRule.getConditionId());
            if (tryParseScheduleConditionId != null) {
                String daysAndTimeSummary = this.mScheduleHelper.getDaysAndTimeSummary(this.mContext, tryParseScheduleConditionId);
                return daysAndTimeSummary != null ? daysAndTimeSummary : this.mContext.getResources().getString(R$string.zen_mode_schedule_rule_days_none);
            }
            ZenModeConfig.EventInfo tryParseEventConditionId = ZenModeConfig.tryParseEventConditionId(automaticZenRule.getConditionId());
            if (tryParseEventConditionId != null) {
                String str = tryParseEventConditionId.calName;
                return str != null ? str : this.mContext.getResources().getString(R$string.zen_mode_event_rule_calendar_any);
            }
        }
        if (automaticZenRule == null || !automaticZenRule.isEnabled()) {
            return this.mContext.getResources().getString(R$string.switch_off_text);
        }
        return this.mContext.getResources().getString(R$string.switch_on_text);
    }
}
