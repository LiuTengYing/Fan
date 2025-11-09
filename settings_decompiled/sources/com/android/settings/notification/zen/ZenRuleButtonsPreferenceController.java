package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenDeleteRuleDialog;
import com.android.settings.notification.zen.ZenRuleNameDialog;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.ActionButtonsPreference;
/* loaded from: classes.dex */
public class ZenRuleButtonsPreferenceController extends AbstractZenModePreferenceController {
    private final PreferenceFragmentCompat mFragment;
    private String mId;
    private AutomaticZenRule mRule;

    public ZenRuleButtonsPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, Lifecycle lifecycle) {
        super(context, "zen_action_buttons", lifecycle);
        this.mFragment = preferenceFragmentCompat;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIdAndRule(String str, AutomaticZenRule automaticZenRule) {
        this.mId = str;
        this.mRule = automaticZenRule;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        if (isAvailable()) {
            ((ActionButtonsPreference) preferenceScreen.findPreference("zen_action_buttons")).setButton1Text(R$string.zen_mode_rule_name_edit).setButton1Icon(17302778).setButton1OnClickListener(new EditRuleNameClickListener()).setButton2Text(R$string.zen_mode_delete_rule_button).setButton2Icon(R$drawable.ic_settings_delete).setButton2OnClickListener(new DeleteRuleClickListener());
        }
    }

    /* loaded from: classes.dex */
    public class EditRuleNameClickListener implements View.OnClickListener {
        public EditRuleNameClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ZenRuleNameDialog.show(ZenRuleButtonsPreferenceController.this.mFragment, ZenRuleButtonsPreferenceController.this.mRule.getName(), null, new ZenRuleNameDialog.PositiveClickListener() { // from class: com.android.settings.notification.zen.ZenRuleButtonsPreferenceController.EditRuleNameClickListener.1
                @Override // com.android.settings.notification.zen.ZenRuleNameDialog.PositiveClickListener
                public void onOk(String str, Fragment fragment) {
                    if (TextUtils.equals(str, ZenRuleButtonsPreferenceController.this.mRule.getName())) {
                        return;
                    }
                    ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController = ZenRuleButtonsPreferenceController.this;
                    zenRuleButtonsPreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) zenRuleButtonsPreferenceController).mContext, 1267, new Pair[0]);
                    ZenRuleButtonsPreferenceController.this.mRule.setName(str);
                    ZenRuleButtonsPreferenceController.this.mRule.setModified(true);
                    ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController2 = ZenRuleButtonsPreferenceController.this;
                    zenRuleButtonsPreferenceController2.mBackend.updateZenRule(zenRuleButtonsPreferenceController2.mId, ZenRuleButtonsPreferenceController.this.mRule);
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public class DeleteRuleClickListener implements View.OnClickListener {
        public DeleteRuleClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ZenDeleteRuleDialog.show(ZenRuleButtonsPreferenceController.this.mFragment, ZenRuleButtonsPreferenceController.this.mRule.getName(), ZenRuleButtonsPreferenceController.this.mId, new ZenDeleteRuleDialog.PositiveClickListener() { // from class: com.android.settings.notification.zen.ZenRuleButtonsPreferenceController.DeleteRuleClickListener.1
                @Override // com.android.settings.notification.zen.ZenDeleteRuleDialog.PositiveClickListener
                public void onOk(String str) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DELETE_RULE", str);
                    ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController = ZenRuleButtonsPreferenceController.this;
                    zenRuleButtonsPreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) zenRuleButtonsPreferenceController).mContext, 175, new Pair[0]);
                    new SubSettingLauncher(((AbstractPreferenceController) ZenRuleButtonsPreferenceController.this).mContext).addFlags(67108864).setDestination(ZenModeAutomationSettings.class.getName()).setSourceMetricsCategory(142).setArguments(bundle).launch();
                }
            });
        }
    }
}
