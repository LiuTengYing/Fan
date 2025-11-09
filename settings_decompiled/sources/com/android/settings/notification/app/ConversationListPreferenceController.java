package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.ConversationChannelWrapper;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.AppPreference;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class ConversationListPreferenceController extends AbstractPreferenceController {
    protected final NotificationBackend mBackend;
    Comparator<ConversationChannelWrapper> mConversationComparator;
    private PreferenceGroup mPreferenceGroup;

    abstract Preference getSummaryPreference();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    abstract boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper);

    public ConversationListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context);
        this.mConversationComparator = new Comparator<ConversationChannelWrapper>() { // from class: com.android.settings.notification.app.ConversationListPreferenceController.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(ConversationChannelWrapper conversationChannelWrapper, ConversationChannelWrapper conversationChannelWrapper2) {
                if (conversationChannelWrapper.getShortcutInfo() == null || conversationChannelWrapper2.getShortcutInfo() != null) {
                    if (conversationChannelWrapper.getShortcutInfo() != null || conversationChannelWrapper2.getShortcutInfo() == null) {
                        if (conversationChannelWrapper.getShortcutInfo() == null && conversationChannelWrapper2.getShortcutInfo() == null) {
                            return conversationChannelWrapper.getNotificationChannel().getId().compareTo(conversationChannelWrapper2.getNotificationChannel().getId());
                        }
                        if (conversationChannelWrapper.getShortcutInfo().getLabel() != null || conversationChannelWrapper2.getShortcutInfo().getLabel() == null) {
                            if (conversationChannelWrapper.getShortcutInfo().getLabel() == null || conversationChannelWrapper2.getShortcutInfo().getLabel() != null) {
                                return this.sCollator.compare(conversationChannelWrapper.getShortcutInfo().getLabel().toString(), conversationChannelWrapper2.getShortcutInfo().getLabel().toString());
                            }
                            return -1;
                        }
                        return 1;
                    }
                    return 1;
                }
                return -1;
            }
        };
        this.mBackend = notificationBackend;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateList(List<ConversationChannelWrapper> list) {
        this.mPreferenceGroup.setVisible(false);
        this.mPreferenceGroup.removeAll();
        if (list != null) {
            populateConversations(list);
        }
        boolean z = this.mPreferenceGroup.getPreferenceCount() != 0;
        if (z) {
            Preference summaryPreference = getSummaryPreference();
            if (summaryPreference != null) {
                summaryPreference.setKey(getPreferenceKey() + "_summary");
                this.mPreferenceGroup.addPreference(summaryPreference);
            }
            this.mPreferenceGroup.setVisible(true);
        }
        return z;
    }

    void populateConversations(List<ConversationChannelWrapper> list) {
        final AtomicInteger atomicInteger = new AtomicInteger(100);
        list.stream().filter(new Predicate() { // from class: com.android.settings.notification.app.ConversationListPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$populateConversations$0;
                lambda$populateConversations$0 = ConversationListPreferenceController.this.lambda$populateConversations$0((ConversationChannelWrapper) obj);
                return lambda$populateConversations$0;
            }
        }).sorted(this.mConversationComparator).map(new Function() { // from class: com.android.settings.notification.app.ConversationListPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Preference createConversationPref;
                createConversationPref = ConversationListPreferenceController.this.createConversationPref((ConversationChannelWrapper) obj);
                return createConversationPref;
            }
        }).forEachOrdered(new Consumer() { // from class: com.android.settings.notification.app.ConversationListPreferenceController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ConversationListPreferenceController.this.lambda$populateConversations$1(atomicInteger, (Preference) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$populateConversations$0(ConversationChannelWrapper conversationChannelWrapper) {
        return !conversationChannelWrapper.getNotificationChannel().isDemoted() && matchesFilter(conversationChannelWrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$populateConversations$1(AtomicInteger atomicInteger, Preference preference) {
        preference.setOrder(atomicInteger.getAndIncrement());
        this.mPreferenceGroup.addPreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Preference createConversationPref(final ConversationChannelWrapper conversationChannelWrapper) {
        final AppPreference appPreference = new AppPreference(this.mContext);
        appPreference.setTitle(getTitle(conversationChannelWrapper));
        appPreference.setSummary(getSummary(conversationChannelWrapper));
        appPreference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannelWrapper.getShortcutInfo(), conversationChannelWrapper.getPkg(), conversationChannelWrapper.getUid(), conversationChannelWrapper.getNotificationChannel().isImportantConversation()));
        appPreference.setKey(conversationChannelWrapper.getNotificationChannel().getId());
        appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.ConversationListPreferenceController$$ExternalSyntheticLambda3
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$createConversationPref$2;
                lambda$createConversationPref$2 = ConversationListPreferenceController.this.lambda$createConversationPref$2(conversationChannelWrapper, appPreference, preference);
                return lambda$createConversationPref$2;
            }
        });
        return appPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createConversationPref$2(ConversationChannelWrapper conversationChannelWrapper, AppPreference appPreference, Preference preference) {
        getSubSettingLauncher(conversationChannelWrapper, appPreference.getTitle()).launch();
        return true;
    }

    CharSequence getSummary(ConversationChannelWrapper conversationChannelWrapper) {
        if (TextUtils.isEmpty(conversationChannelWrapper.getGroupLabel())) {
            return conversationChannelWrapper.getParentChannelLabel();
        }
        return this.mContext.getString(R$string.notification_conversation_summary, conversationChannelWrapper.getParentChannelLabel(), conversationChannelWrapper.getGroupLabel());
    }

    CharSequence getTitle(ConversationChannelWrapper conversationChannelWrapper) {
        ShortcutInfo shortcutInfo = conversationChannelWrapper.getShortcutInfo();
        if (shortcutInfo != null) {
            return shortcutInfo.getLabel();
        }
        return conversationChannelWrapper.getNotificationChannel().getName();
    }

    SubSettingLauncher getSubSettingLauncher(ConversationChannelWrapper conversationChannelWrapper, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannelWrapper.getUid());
        bundle.putString("package", conversationChannelWrapper.getPkg());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannelWrapper.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannelWrapper.getNotificationChannel().getConversationId());
        return new SubSettingLauncher(this.mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setUserHandle(UserHandle.getUserHandleForUid(conversationChannelWrapper.getUid())).setTitleText(charSequence).setSourceMetricsCategory(1834);
    }
}
