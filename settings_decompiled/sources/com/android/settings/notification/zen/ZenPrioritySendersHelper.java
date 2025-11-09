package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.icu.text.MessageFormat;
import android.service.notification.ConversationChannelWrapper;
import android.view.View;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.ConversationListSettings;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class ZenPrioritySendersHelper {
    private final Context mContext;
    private final boolean mIsMessages;
    private final NotificationBackend mNotificationBackend;
    private final PackageManager mPackageManager;
    private PreferenceCategory mPreferenceCategory;
    private final SelectorWithWidgetPreference.OnClickListener mSelectorClickListener;
    private final ZenModeBackend mZenModeBackend;
    private static final Intent ALL_CONTACTS_INTENT = new Intent("com.android.contacts.action.LIST_DEFAULT").setFlags(268468224);
    private static final Intent STARRED_CONTACTS_INTENT = new Intent("com.android.contacts.action.LIST_STARRED").setFlags(268468224);
    private static final Intent FALLBACK_INTENT = new Intent("android.intent.action.MAIN").setFlags(268468224);
    private int mNumImportantConversations = -10;
    private List<SelectorWithWidgetPreference> mSelectorPreferences = new ArrayList();

    public ZenPrioritySendersHelper(Context context, boolean z, ZenModeBackend zenModeBackend, NotificationBackend notificationBackend, SelectorWithWidgetPreference.OnClickListener onClickListener) {
        this.mContext = context;
        this.mIsMessages = z;
        this.mZenModeBackend = zenModeBackend;
        this.mNotificationBackend = notificationBackend;
        this.mSelectorClickListener = onClickListener;
        this.mPackageManager = context.getPackageManager();
        Intent intent = FALLBACK_INTENT;
        if (intent.hasCategory("android.intent.category.APP_CONTACTS")) {
            return;
        }
        intent.addCategory("android.intent.category.APP_CONTACTS");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void displayPreference(PreferenceCategory preferenceCategory) {
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory.getPreferenceCount() == 0) {
            makeSelectorPreference("senders_starred_contacts", R$string.zen_mode_from_starred, this.mIsMessages);
            makeSelectorPreference("senders_contacts", R$string.zen_mode_from_contacts, this.mIsMessages);
            if (this.mIsMessages) {
                makeSelectorPreference("conversations_important", R$string.zen_mode_from_important_conversations, true);
                updateChannelCounts();
            }
            makeSelectorPreference("senders_anyone", R$string.zen_mode_from_anyone, this.mIsMessages);
            makeSelectorPreference("senders_none", R$string.zen_mode_none_messages, this.mIsMessages);
            updateSummaries();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateState(int i, int i2) {
        for (SelectorWithWidgetPreference selectorWithWidgetPreference : this.mSelectorPreferences) {
            int[] keyToSettingEndState = keyToSettingEndState(selectorWithWidgetPreference.getKey(), true);
            int i3 = keyToSettingEndState[0];
            int i4 = keyToSettingEndState[1];
            boolean z = i3 == i;
            if (this.mIsMessages && i4 != -10) {
                z = (z || i3 == -10) && i4 == i2;
            }
            selectorWithWidgetPreference.setChecked(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSummaries() {
        for (SelectorWithWidgetPreference selectorWithWidgetPreference : this.mSelectorPreferences) {
            selectorWithWidgetPreference.setSummary(getSummary(selectorWithWidgetPreference.getKey()));
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0060, code lost:
        if (r12.equals("senders_none") == false) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    int[] keyToSettingEndState(java.lang.String r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 388
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.zen.ZenPrioritySendersHelper.keyToSettingEndState(java.lang.String, boolean):int[]");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] settingsToSaveOnClick(SelectorWithWidgetPreference selectorWithWidgetPreference, int i, int i2) {
        int[] iArr = {-10, -10};
        int[] keyToSettingEndState = keyToSettingEndState(selectorWithWidgetPreference.getKey(), (selectorWithWidgetPreference.isCheckBox() && selectorWithWidgetPreference.isChecked()) ? false : true);
        int i3 = keyToSettingEndState[0];
        int i4 = keyToSettingEndState[1];
        if (i3 != -10 && i3 != i) {
            iArr[0] = i3;
        }
        if (this.mIsMessages) {
            if (i4 != -10 && i4 != i2) {
                iArr[1] = i4;
            }
            if (selectorWithWidgetPreference.getKey() == "conversations_important" && i == 0) {
                iArr[0] = -1;
            }
            if ((selectorWithWidgetPreference.getKey() == "senders_starred_contacts" || selectorWithWidgetPreference.getKey() == "senders_contacts") && i2 == 1) {
                iArr[1] = 3;
            }
        }
        return iArr;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private String getSummary(String str) {
        char c;
        int i;
        switch (str.hashCode()) {
            case -1145842476:
                if (str.equals("senders_starred_contacts")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -133103980:
                if (str.equals("senders_contacts")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 660058867:
                if (str.equals("conversations_important")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1725241211:
                if (str.equals("senders_anyone")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1767544313:
                if (str.equals("senders_none")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c != 0) {
            if (c != 1) {
                if (c != 2) {
                    if (c != 3) {
                        return null;
                    }
                    Resources resources = this.mContext.getResources();
                    if (this.mIsMessages) {
                        i = R$string.zen_mode_all_messages_summary;
                    } else {
                        i = R$string.zen_mode_all_calls_summary;
                    }
                    return resources.getString(i);
                }
                return getConversationSummary();
            }
            return this.mZenModeBackend.getContactsNumberSummary(this.mContext);
        }
        return this.mZenModeBackend.getStarredContactsSummary(this.mContext);
    }

    private String getConversationSummary() {
        int i = this.mNumImportantConversations;
        if (i == -10) {
            return null;
        }
        MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R$string.zen_mode_conversations_count), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(i));
        return messageFormat.format(hashMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateChannelCounts() {
        ParceledListSlice<ConversationChannelWrapper> conversations = this.mNotificationBackend.getConversations(true);
        int i = 0;
        if (conversations != null) {
            for (ConversationChannelWrapper conversationChannelWrapper : conversations.getList()) {
                if (!conversationChannelWrapper.getNotificationChannel().isDemoted()) {
                    i++;
                }
            }
        }
        this.mNumImportantConversations = i;
    }

    private SelectorWithWidgetPreference makeSelectorPreference(String str, int i, boolean z) {
        SelectorWithWidgetPreference selectorWithWidgetPreference = new SelectorWithWidgetPreference(this.mPreferenceCategory.getContext(), z);
        selectorWithWidgetPreference.setKey(str);
        selectorWithWidgetPreference.setTitle(i);
        selectorWithWidgetPreference.setOnClickListener(this.mSelectorClickListener);
        View.OnClickListener widgetClickListener = getWidgetClickListener(str);
        if (widgetClickListener != null) {
            selectorWithWidgetPreference.setExtraWidgetOnClickListener(widgetClickListener);
        }
        this.mPreferenceCategory.addPreference(selectorWithWidgetPreference);
        this.mSelectorPreferences.add(selectorWithWidgetPreference);
        return selectorWithWidgetPreference;
    }

    private View.OnClickListener getWidgetClickListener(final String str) {
        if ("senders_contacts".equals(str) || "senders_starred_contacts".equals(str) || "conversations_important".equals(str)) {
            if (!"senders_starred_contacts".equals(str) || isStarredIntentValid()) {
                if (!"senders_contacts".equals(str) || isContactsIntentValid()) {
                    return new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenPrioritySendersHelper.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            if ("senders_starred_contacts".equals(str) && ZenPrioritySendersHelper.STARRED_CONTACTS_INTENT.resolveActivity(ZenPrioritySendersHelper.this.mPackageManager) != null) {
                                ZenPrioritySendersHelper.this.mContext.startActivity(ZenPrioritySendersHelper.STARRED_CONTACTS_INTENT);
                            } else if ("senders_contacts".equals(str) && ZenPrioritySendersHelper.ALL_CONTACTS_INTENT.resolveActivity(ZenPrioritySendersHelper.this.mPackageManager) != null) {
                                ZenPrioritySendersHelper.this.mContext.startActivity(ZenPrioritySendersHelper.ALL_CONTACTS_INTENT);
                            } else if ("conversations_important".equals(str)) {
                                new SubSettingLauncher(ZenPrioritySendersHelper.this.mContext).setDestination(ConversationListSettings.class.getName()).setSourceMetricsCategory(1837).launch();
                            } else {
                                ZenPrioritySendersHelper.this.mContext.startActivity(ZenPrioritySendersHelper.FALLBACK_INTENT);
                            }
                        }
                    };
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private boolean isStarredIntentValid() {
        return (STARRED_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }

    private boolean isContactsIntentValid() {
        return (ALL_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }
}
