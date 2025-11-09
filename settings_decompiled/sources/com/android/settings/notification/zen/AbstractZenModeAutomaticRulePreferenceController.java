package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.notification.zen.ZenRuleNameDialog;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class AbstractZenModeAutomaticRulePreferenceController extends AbstractZenModePreferenceController {
    protected ZenModeBackend mBackend;
    protected Fragment mParent;
    protected PackageManager mPm;
    protected Map.Entry<String, AutomaticZenRule>[] mRules;

    public AbstractZenModeAutomaticRulePreferenceController(Context context, String str, Fragment fragment, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mBackend = ZenModeBackend.getInstance(context);
        this.mPm = this.mContext.getPackageManager();
        this.mParent = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mRules = this.mBackend.getAutomaticZenRules();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map.Entry<String, AutomaticZenRule>[] getRules() {
        if (this.mRules == null) {
            this.mRules = this.mBackend.getAutomaticZenRules();
        }
        return this.mRules;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showNameRuleDialog(ZenRuleInfo zenRuleInfo, Fragment fragment) {
        ZenRuleNameDialog.show(fragment, null, zenRuleInfo.defaultConditionId, new RuleNameChangeListener(zenRuleInfo));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Intent getRuleIntent(String str, ComponentName componentName, String str2) {
        Intent putExtra = new Intent().addFlags(67108864).putExtra("android.service.notification.extra.RULE_ID", str2).putExtra("android.app.extra.AUTOMATIC_RULE_ID", str2);
        if (componentName != null) {
            putExtra.setComponent(componentName);
        } else {
            putExtra.setAction(str);
        }
        return putExtra;
    }

    public static ZenRuleInfo getRuleInfo(PackageManager packageManager, ComponentInfo componentInfo) {
        Bundle bundle;
        String string;
        int i;
        if (componentInfo != null && (bundle = componentInfo.metaData) != null) {
            boolean z = componentInfo instanceof ServiceInfo;
            if (z) {
                string = bundle.getString("android.service.zen.automatic.ruleType");
            } else {
                string = bundle.getString("android.service.zen.automatic.ruleType");
            }
            ComponentName settingsActivity = getSettingsActivity(packageManager, null, componentInfo);
            if (string != null && !string.trim().isEmpty() && settingsActivity != null) {
                ZenRuleInfo zenRuleInfo = new ZenRuleInfo();
                zenRuleInfo.serviceComponent = z ? new ComponentName(componentInfo.packageName, componentInfo.name) : null;
                zenRuleInfo.settingsAction = "android.settings.ZEN_MODE_EXTERNAL_RULE_SETTINGS";
                zenRuleInfo.title = string;
                zenRuleInfo.packageName = componentInfo.packageName;
                zenRuleInfo.configurationActivity = settingsActivity;
                zenRuleInfo.packageLabel = componentInfo.applicationInfo.loadLabel(packageManager);
                if (z) {
                    i = componentInfo.metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
                } else {
                    i = componentInfo.metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
                }
                zenRuleInfo.ruleInstanceLimit = i;
                return zenRuleInfo;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ComponentName getSettingsActivity(PackageManager packageManager, AutomaticZenRule automaticZenRule, ComponentInfo componentInfo) {
        String string;
        ComponentName unflattenFromString;
        String packageName = automaticZenRule != null ? automaticZenRule.getPackageName() : componentInfo.packageName;
        if (automaticZenRule == null || automaticZenRule.getConfigurationActivity() == null) {
            if (componentInfo != null) {
                if (componentInfo instanceof ActivityInfo) {
                    unflattenFromString = new ComponentName(componentInfo.packageName, componentInfo.name);
                } else {
                    Bundle bundle = componentInfo.metaData;
                    if (bundle != null && (string = bundle.getString("android.service.zen.automatic.configurationActivity")) != null) {
                        unflattenFromString = ComponentName.unflattenFromString(string);
                    }
                }
            }
            unflattenFromString = null;
        } else {
            unflattenFromString = automaticZenRule.getConfigurationActivity();
        }
        if (unflattenFromString == null || packageName == null) {
            return unflattenFromString;
        }
        try {
            if (packageManager.getPackageUid(packageName, 0) == packageManager.getPackageUid(unflattenFromString.getPackageName(), 0)) {
                return unflattenFromString;
            }
            Log.w("PrefControllerMixin", "Config activity not in owner package for " + automaticZenRule.getName());
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("PrefControllerMixin", "Failed to find config activity");
            return null;
        }
    }

    /* loaded from: classes.dex */
    public class RuleNameChangeListener implements ZenRuleNameDialog.PositiveClickListener {
        ZenRuleInfo mRuleInfo;

        public RuleNameChangeListener(ZenRuleInfo zenRuleInfo) {
            this.mRuleInfo = zenRuleInfo;
        }

        @Override // com.android.settings.notification.zen.ZenRuleNameDialog.PositiveClickListener
        public void onOk(String str, Fragment fragment) {
            AbstractZenModeAutomaticRulePreferenceController abstractZenModeAutomaticRulePreferenceController = AbstractZenModeAutomaticRulePreferenceController.this;
            abstractZenModeAutomaticRulePreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) abstractZenModeAutomaticRulePreferenceController).mContext, 1267, new Pair[0]);
            ZenRuleInfo zenRuleInfo = this.mRuleInfo;
            String addZenRule = AbstractZenModeAutomaticRulePreferenceController.this.mBackend.addZenRule(new AutomaticZenRule(str, zenRuleInfo.serviceComponent, zenRuleInfo.configurationActivity, zenRuleInfo.defaultConditionId, null, 2, true));
            if (addZenRule != null) {
                fragment.startActivity(AbstractZenModeAutomaticRulePreferenceController.getRuleIntent(this.mRuleInfo.settingsAction, null, addZenRule));
            }
        }
    }
}
