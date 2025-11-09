package com.android.settings.notification.app;

import android.content.Context;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class AllConversationsPreferenceController extends ConversationListPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "other_conversations";
    }

    public AllConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(R$string.other_conversations_summary);
        preference.setSelectable(false);
        return preference;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return !conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }
}
