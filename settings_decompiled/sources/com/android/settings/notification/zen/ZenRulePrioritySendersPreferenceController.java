package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.ZenPolicy;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class ZenRulePrioritySendersPreferenceController extends AbstractZenCustomRulePreferenceController {
    private ZenPrioritySendersHelper mHelper;
    private final boolean mIsMessages;
    private PreferenceCategory mPreferenceCategory;
    @VisibleForTesting
    SelectorWithWidgetPreference.OnClickListener mSelectorClickListener;

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public /* bridge */ /* synthetic */ void setIdAndRule(String str, AutomaticZenRule automaticZenRule) {
        super.setIdAndRule(str, automaticZenRule);
    }

    public ZenRulePrioritySendersPreferenceController(Context context, String str, Lifecycle lifecycle, boolean z, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mSelectorClickListener = new SelectorWithWidgetPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRulePrioritySendersPreferenceController.2
            @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
            public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
                AutomaticZenRule automaticZenRule = ZenRulePrioritySendersPreferenceController.this.mRule;
                if (automaticZenRule == null || automaticZenRule.getZenPolicy() == null) {
                    return;
                }
                int[] iArr = ZenRulePrioritySendersPreferenceController.this.mHelper.settingsToSaveOnClick(selectorWithWidgetPreference, ZenRulePrioritySendersPreferenceController.this.getPrioritySenders(), ZenRulePrioritySendersPreferenceController.this.getPriorityConversationSenders());
                int i = iArr[0];
                int i2 = iArr[1];
                if (i == -10 && i2 == -10) {
                    return;
                }
                if (i != -10) {
                    if (ZenRulePrioritySendersPreferenceController.this.mIsMessages) {
                        ZenRulePrioritySendersPreferenceController.this.mRule.setZenPolicy(new ZenPolicy.Builder(ZenRulePrioritySendersPreferenceController.this.mRule.getZenPolicy()).allowMessages(ZenRulePrioritySendersPreferenceController.zenPolicySettingFromSender(i)).build());
                    } else {
                        ZenRulePrioritySendersPreferenceController.this.mRule.setZenPolicy(new ZenPolicy.Builder(ZenRulePrioritySendersPreferenceController.this.mRule.getZenPolicy()).allowCalls(ZenRulePrioritySendersPreferenceController.zenPolicySettingFromSender(i)).build());
                    }
                }
                if (ZenRulePrioritySendersPreferenceController.this.mIsMessages && i2 != -10) {
                    ZenRulePrioritySendersPreferenceController.this.mRule.setZenPolicy(new ZenPolicy.Builder(ZenRulePrioritySendersPreferenceController.this.mRule.getZenPolicy()).allowConversations(i2).build());
                }
                ZenRulePrioritySendersPreferenceController zenRulePrioritySendersPreferenceController = ZenRulePrioritySendersPreferenceController.this;
                zenRulePrioritySendersPreferenceController.mBackend.updateZenRule(zenRulePrioritySendersPreferenceController.mId, zenRulePrioritySendersPreferenceController.mRule);
            }
        };
        this.mIsMessages = z;
        this.mHelper = new ZenPrioritySendersHelper(context, z, this.mBackend, notificationBackend, this.mSelectorClickListener);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        this.mHelper.displayPreference(preferenceCategory);
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule == null || automaticZenRule.getZenPolicy() == null) {
            return;
        }
        this.mHelper.updateState(getPrioritySenders(), getPriorityConversationSenders());
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        if (this.mIsMessages) {
            updateChannelCounts();
        }
        this.mHelper.updateSummaries();
    }

    private void updateChannelCounts() {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.zen.ZenRulePrioritySendersPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                ZenRulePrioritySendersPreferenceController.this.mHelper.updateChannelCounts();
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                if (((AbstractPreferenceController) ZenRulePrioritySendersPreferenceController.this).mContext == null) {
                    return;
                }
                ZenRulePrioritySendersPreferenceController zenRulePrioritySendersPreferenceController = ZenRulePrioritySendersPreferenceController.this;
                zenRulePrioritySendersPreferenceController.updateState(zenRulePrioritySendersPreferenceController.mPreferenceCategory);
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPrioritySenders() {
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule == null || automaticZenRule.getZenPolicy() == null) {
            return -10;
        }
        if (this.mIsMessages) {
            return ZenModeBackend.getContactSettingFromZenPolicySetting(this.mRule.getZenPolicy().getPriorityMessageSenders());
        }
        return ZenModeBackend.getContactSettingFromZenPolicySetting(this.mRule.getZenPolicy().getPriorityCallSenders());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPriorityConversationSenders() {
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule == null || automaticZenRule.getZenPolicy() == null) {
            return -10;
        }
        return this.mRule.getZenPolicy().getPriorityConversationSenders();
    }

    static int zenPolicySettingFromSender(int i) {
        return ZenModeBackend.getZenPolicySettingFromPrefKey(ZenModeBackend.getKeyFromSetting(i));
    }
}
