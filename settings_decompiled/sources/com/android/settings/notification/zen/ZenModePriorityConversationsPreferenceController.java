package com.android.settings.notification.zen;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.icu.text.MessageFormat;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.ConversationListSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class ZenModePriorityConversationsPreferenceController extends AbstractZenModePreferenceController {
    static final String KEY_ALL = "conversations_all";
    static final String KEY_IMPORTANT = "conversations_important";
    static final String KEY_NONE = "conversations_none";
    private View.OnClickListener mConversationSettingsWidgetClickListener;
    private final NotificationBackend mNotificationBackend;
    private int mNumConversations;
    private int mNumImportantConversations;
    private PreferenceCategory mPreferenceCategory;
    private Context mPreferenceScreenContext;
    private SelectorWithWidgetPreference.OnClickListener mRadioButtonClickListener;
    private List<SelectorWithWidgetPreference> mSelectorWithWidgetPreferences;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModePriorityConversationsPreferenceController(Context context, String str, Lifecycle lifecycle, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mNumImportantConversations = -1;
        this.mNumConversations = -1;
        this.mSelectorWithWidgetPreferences = new ArrayList();
        this.mConversationSettingsWidgetClickListener = new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new SubSettingLauncher(ZenModePriorityConversationsPreferenceController.this.mPreferenceScreenContext).setDestination(ConversationListSettings.class.getName()).setSourceMetricsCategory(1837).launch();
            }
        };
        this.mRadioButtonClickListener = new SelectorWithWidgetPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.3
            @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
            public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
                int keyToSetting = ZenModePriorityConversationsPreferenceController.keyToSetting(selectorWithWidgetPreference.getKey());
                if (keyToSetting != ZenModePriorityConversationsPreferenceController.this.mBackend.getPriorityConversationSenders()) {
                    ZenModePriorityConversationsPreferenceController.this.mBackend.saveConversationSenders(keyToSetting);
                }
            }
        };
        this.mNotificationBackend = notificationBackend;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceScreenContext = preferenceScreen.getContext();
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory.findPreference(KEY_ALL) == null) {
            makeRadioPreference(KEY_ALL, R$string.zen_mode_from_all_conversations);
            makeRadioPreference(KEY_IMPORTANT, R$string.zen_mode_from_important_conversations);
            makeRadioPreference(KEY_NONE, R$string.zen_mode_from_no_conversations);
            updateChannelCounts();
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        updateChannelCounts();
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int priorityConversationSenders = this.mBackend.getPriorityConversationSenders();
        for (SelectorWithWidgetPreference selectorWithWidgetPreference : this.mSelectorWithWidgetPreferences) {
            selectorWithWidgetPreference.setChecked(keyToSetting(selectorWithWidgetPreference.getKey()) == priorityConversationSenders);
            selectorWithWidgetPreference.setSummary(getSummary(selectorWithWidgetPreference.getKey()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int keyToSetting(String str) {
        str.hashCode();
        if (str.equals(KEY_IMPORTANT)) {
            return 2;
        }
        return !str.equals(KEY_ALL) ? 3 : 1;
    }

    private String getSummary(String str) {
        int i;
        if (KEY_ALL.equals(str)) {
            i = this.mNumConversations;
        } else if (!KEY_IMPORTANT.equals(str)) {
            return null;
        } else {
            i = this.mNumImportantConversations;
        }
        if (i == -1) {
            return null;
        }
        MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R$string.zen_mode_conversations_count), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(i));
        return messageFormat.format(hashMap);
    }

    private void updateChannelCounts() {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                int i;
                int i2 = 0;
                ParceledListSlice<ConversationChannelWrapper> conversations = ZenModePriorityConversationsPreferenceController.this.mNotificationBackend.getConversations(false);
                if (conversations != null) {
                    i = 0;
                    for (ConversationChannelWrapper conversationChannelWrapper : conversations.getList()) {
                        if (!conversationChannelWrapper.getNotificationChannel().isDemoted()) {
                            i++;
                        }
                    }
                } else {
                    i = 0;
                }
                ZenModePriorityConversationsPreferenceController.this.mNumConversations = i;
                ParceledListSlice<ConversationChannelWrapper> conversations2 = ZenModePriorityConversationsPreferenceController.this.mNotificationBackend.getConversations(true);
                if (conversations2 != null) {
                    for (ConversationChannelWrapper conversationChannelWrapper2 : conversations2.getList()) {
                        if (!conversationChannelWrapper2.getNotificationChannel().isDemoted()) {
                            i2++;
                        }
                    }
                }
                ZenModePriorityConversationsPreferenceController.this.mNumImportantConversations = i2;
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                if (((AbstractPreferenceController) ZenModePriorityConversationsPreferenceController.this).mContext == null) {
                    return;
                }
                ZenModePriorityConversationsPreferenceController zenModePriorityConversationsPreferenceController = ZenModePriorityConversationsPreferenceController.this;
                zenModePriorityConversationsPreferenceController.updateState(zenModePriorityConversationsPreferenceController.mPreferenceCategory);
            }
        }.execute(new Void[0]);
    }

    private SelectorWithWidgetPreference makeRadioPreference(String str, int i) {
        SelectorWithWidgetPreference selectorWithWidgetPreference = new SelectorWithWidgetPreference(this.mPreferenceCategory.getContext());
        if (KEY_ALL.equals(str) || KEY_IMPORTANT.equals(str)) {
            selectorWithWidgetPreference.setExtraWidgetOnClickListener(this.mConversationSettingsWidgetClickListener);
        }
        selectorWithWidgetPreference.setKey(str);
        selectorWithWidgetPreference.setTitle(i);
        selectorWithWidgetPreference.setOnClickListener(this.mRadioButtonClickListener);
        this.mPreferenceCategory.addPreference(selectorWithWidgetPreference);
        this.mSelectorWithWidgetPreferences.add(selectorWithWidgetPreference);
        return selectorWithWidgetPreference;
    }
}
