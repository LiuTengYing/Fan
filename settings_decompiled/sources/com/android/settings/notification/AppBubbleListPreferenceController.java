package com.android.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.service.notification.ConversationChannelWrapper;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppConversationListPreferenceController;
import com.android.settings.notification.app.NotificationPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class AppBubbleListPreferenceController extends AppConversationListPreferenceController {
    @Override // com.android.settings.notification.app.AppConversationListPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bubble_conversations";
    }

    public AppBubbleListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setVisible(false);
        super.updateState(preference);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    public void onResume(NotificationBackend.AppRow appRow, NotificationChannel notificationChannel, NotificationChannelGroup notificationChannelGroup, Drawable drawable, ShortcutInfo shortcutInfo, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, List<String> list) {
        super.onResume(appRow, notificationChannel, notificationChannelGroup, drawable, shortcutInfo, enforcedAdmin, list);
        loadConversationsAndPopulate();
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController, com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        return (this.mChannel == null || !(this.mBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || "miscellaneous".equals(this.mChannel.getId()))) && this.mAppRow.bubblePreference != 0;
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public List<ConversationChannelWrapper> filterAndSortConversations(List<ConversationChannelWrapper> list) {
        return (List) list.stream().sorted(this.mConversationComparator).filter(new Predicate() { // from class: com.android.settings.notification.AppBubbleListPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$filterAndSortConversations$0;
                lambda$filterAndSortConversations$0 = AppBubbleListPreferenceController.this.lambda$filterAndSortConversations$0((ConversationChannelWrapper) obj);
                return lambda$filterAndSortConversations$0;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$filterAndSortConversations$0(ConversationChannelWrapper conversationChannelWrapper) {
        int i = this.mAppRow.bubblePreference;
        if (i == 2) {
            return conversationChannelWrapper.getNotificationChannel().canBubble();
        }
        return i == 1 && conversationChannelWrapper.getNotificationChannel().getAllowBubbles() == 0;
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    protected int getTitleResId() {
        if (this.mAppRow.bubblePreference == 2) {
            return R$string.bubble_app_setting_selected_conversation_title;
        }
        return R$string.bubble_app_setting_excluded_conversation_title;
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public Preference createConversationPref(final ConversationChannelWrapper conversationChannelWrapper) {
        final ConversationPreference conversationPreference = new ConversationPreference(((NotificationPreferenceController) this).mContext);
        populateConversationPreference(conversationChannelWrapper, conversationPreference);
        conversationPreference.setOnClickBubblesConversation(this.mAppRow.bubblePreference == 1);
        conversationPreference.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.AppBubbleListPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppBubbleListPreferenceController.this.lambda$createConversationPref$1(conversationChannelWrapper, conversationPreference, view);
            }
        });
        return conversationPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createConversationPref$1(ConversationChannelWrapper conversationChannelWrapper, ConversationPreference conversationPreference, View view) {
        conversationChannelWrapper.getNotificationChannel().setAllowBubbles(-1);
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannel(appRow.pkg, appRow.uid, conversationChannelWrapper.getNotificationChannel());
        this.mPreference.removePreference(conversationPreference);
        if (this.mPreference.getPreferenceCount() == 0) {
            this.mPreference.setVisible(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public void populateList() {
        super.populateList();
        PreferenceCategory preferenceCategory = this.mPreference;
        if (preferenceCategory == null) {
            return;
        }
        preferenceCategory.setVisible(preferenceCategory.getPreferenceCount() > 0);
    }

    /* loaded from: classes.dex */
    public static class ConversationPreference extends Preference implements View.OnClickListener {
        boolean mOnClickBubbles;
        View.OnClickListener mOnClickListener;

        ConversationPreference(Context context) {
            super(context);
            setWidgetLayoutResource(R$layout.bubble_conversation_remove_button);
        }

        @Override // androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            String string;
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(R$id.button);
            if (this.mOnClickBubbles) {
                string = getContext().getString(R$string.bubble_app_setting_bubble_conversation);
            } else {
                string = getContext().getString(R$string.bubble_app_setting_unbubble_conversation);
            }
            imageView.setContentDescription(string);
            imageView.setOnClickListener(this.mOnClickListener);
        }

        public void setOnClickBubblesConversation(boolean z) {
            this.mOnClickBubbles = z;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View.OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }
}
