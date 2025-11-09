package com.android.settings.notification.app;

import android.content.Context;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class PriorityConversationsPreferenceController extends ConversationListPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "important_conversations";
    }

    public PriorityConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(R$string.important_conversations_summary_bubbles);
        preference.setSelectable(false);
        return preference;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }
}
