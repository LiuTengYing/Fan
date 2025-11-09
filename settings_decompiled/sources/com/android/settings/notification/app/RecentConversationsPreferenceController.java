package com.android.settings.notification.app;

import android.app.people.ConversationChannel;
import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.RecentConversationPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class RecentConversationsPreferenceController extends AbstractPreferenceController {
    private final NotificationBackend mBackend;
    Comparator<ConversationChannel> mConversationComparator;
    private PreferenceGroup mPreferenceGroup;
    private final IPeopleManager mPs;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "recent_conversations";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RecentConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context);
        this.mConversationComparator = new Comparator<ConversationChannel>() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(ConversationChannel conversationChannel, ConversationChannel conversationChannel2) {
                int compare = (conversationChannel.getShortcutInfo().getLabel() == null || conversationChannel2.getShortcutInfo().getLabel() == null) ? 0 : this.sCollator.compare(conversationChannel.getShortcutInfo().getLabel().toString(), conversationChannel2.getShortcutInfo().getLabel().toString());
                return compare == 0 ? conversationChannel.getNotificationChannel().getId().compareTo(conversationChannel2.getNotificationChannel().getId()) : compare;
            }
        };
        this.mBackend = notificationBackend;
        this.mPs = iPeopleManager;
    }

    LayoutPreference getClearAll(final PreferenceGroup preferenceGroup) {
        LayoutPreference layoutPreference = new LayoutPreference(this.mContext, R$layout.conversations_clear_recents);
        layoutPreference.setKey(getPreferenceKey() + "_clear_all");
        layoutPreference.setOrder(1);
        final Button button = (Button) layoutPreference.findViewById(R$id.conversation_settings_clear_recents);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentConversationsPreferenceController.this.lambda$getClearAll$0(preferenceGroup, button, view);
            }
        });
        return layoutPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getClearAll$0(PreferenceGroup preferenceGroup, Button button, View view) {
        try {
            this.mPs.removeAllRecentConversations();
            for (int preferenceCount = preferenceGroup.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceGroup.getPreference(preferenceCount);
                if ((preference instanceof RecentConversationPreference) && ((RecentConversationPreference) preference).hasClearListener()) {
                    preferenceGroup.removePreference(preference);
                }
            }
            button.announceForAccessibility(this.mContext.getString(R$string.recent_convos_removed));
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recents", e);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateList() {
        List<ConversationChannel> emptyList = Collections.emptyList();
        try {
            emptyList = this.mPs.getRecentConversations().getList();
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not get recent conversations", e);
        }
        return populateList(emptyList);
    }

    boolean populateList(List<ConversationChannel> list) {
        LayoutPreference clearAll;
        this.mPreferenceGroup.removeAll();
        boolean populateConversations = list != null ? populateConversations(list) : false;
        boolean z = this.mPreferenceGroup.getPreferenceCount() != 0;
        this.mPreferenceGroup.setVisible(z);
        if (z && populateConversations && (clearAll = getClearAll(this.mPreferenceGroup)) != null) {
            this.mPreferenceGroup.addPreference(clearAll);
        }
        return z;
    }

    protected boolean populateConversations(List<ConversationChannel> list) {
        final AtomicInteger atomicInteger = new AtomicInteger(100);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        list.stream().filter(new Predicate() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$populateConversations$1;
                lambda$populateConversations$1 = RecentConversationsPreferenceController.lambda$populateConversations$1((ConversationChannel) obj);
                return lambda$populateConversations$1;
            }
        }).sorted(this.mConversationComparator).map(new Function() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return RecentConversationsPreferenceController.this.createConversationPref((ConversationChannel) obj);
            }
        }).forEachOrdered(new Consumer() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RecentConversationsPreferenceController.this.lambda$populateConversations$2(atomicInteger, atomicBoolean, (RecentConversationPreference) obj);
            }
        });
        return atomicBoolean.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$populateConversations$1(ConversationChannel conversationChannel) {
        return conversationChannel.getNotificationChannel().getImportance() != 0 && (conversationChannel.getNotificationChannelGroup() == null || !conversationChannel.getNotificationChannelGroup().isBlocked());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$populateConversations$2(AtomicInteger atomicInteger, AtomicBoolean atomicBoolean, RecentConversationPreference recentConversationPreference) {
        recentConversationPreference.setOrder(atomicInteger.getAndIncrement());
        this.mPreferenceGroup.addPreference(recentConversationPreference);
        if (recentConversationPreference.hasClearListener()) {
            atomicBoolean.set(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RecentConversationPreference createConversationPref(final ConversationChannel conversationChannel) {
        final String str = conversationChannel.getShortcutInfo().getPackage();
        final int uid = conversationChannel.getUid();
        final String id = conversationChannel.getShortcutInfo().getId();
        final RecentConversationPreference recentConversationPreference = new RecentConversationPreference(this.mContext);
        if (!conversationChannel.hasActiveNotifications()) {
            recentConversationPreference.setOnClearClickListener(new RecentConversationPreference.OnClearClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda4
                @Override // com.android.settings.notification.app.RecentConversationPreference.OnClearClickListener
                public final void onClear() {
                    RecentConversationsPreferenceController.this.lambda$createConversationPref$3(str, uid, id, recentConversationPreference);
                }
            });
        }
        recentConversationPreference.setTitle(getTitle(conversationChannel));
        recentConversationPreference.setSummary(getSummary(conversationChannel));
        recentConversationPreference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannel.getShortcutInfo(), str, uid, false));
        recentConversationPreference.setKey(conversationChannel.getNotificationChannel().getId() + ":" + id);
        recentConversationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda5
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$createConversationPref$4;
                lambda$createConversationPref$4 = RecentConversationsPreferenceController.this.lambda$createConversationPref$4(str, uid, conversationChannel, id, recentConversationPreference, preference);
                return lambda$createConversationPref$4;
            }
        });
        return recentConversationPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createConversationPref$3(String str, int i, String str2, RecentConversationPreference recentConversationPreference) {
        try {
            this.mPs.removeRecentConversation(str, UserHandle.getUserId(i), str2);
            recentConversationPreference.getClearView().announceForAccessibility(this.mContext.getString(R$string.recent_convo_removed));
            this.mPreferenceGroup.removePreference(recentConversationPreference);
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recent", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createConversationPref$4(String str, int i, ConversationChannel conversationChannel, String str2, RecentConversationPreference recentConversationPreference, Preference preference) {
        this.mBackend.createConversationNotificationChannel(str, i, conversationChannel.getNotificationChannel(), str2);
        getSubSettingLauncher(conversationChannel, recentConversationPreference.getTitle()).launch();
        return true;
    }

    CharSequence getSummary(ConversationChannel conversationChannel) {
        if (conversationChannel.getNotificationChannelGroup() == null) {
            return conversationChannel.getNotificationChannel().getName();
        }
        return this.mContext.getString(R$string.notification_conversation_summary, conversationChannel.getNotificationChannel().getName(), conversationChannel.getNotificationChannelGroup().getName());
    }

    CharSequence getTitle(ConversationChannel conversationChannel) {
        return conversationChannel.getShortcutInfo().getLabel();
    }

    SubSettingLauncher getSubSettingLauncher(ConversationChannel conversationChannel, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannel.getUid());
        bundle.putString("package", conversationChannel.getShortcutInfo().getPackage());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannel.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannel.getShortcutInfo().getId());
        return new SubSettingLauncher(this.mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setUserHandle(UserHandle.getUserHandleForUid(conversationChannel.getUid())).setTitleText(charSequence).setSourceMetricsCategory(1834);
    }
}
