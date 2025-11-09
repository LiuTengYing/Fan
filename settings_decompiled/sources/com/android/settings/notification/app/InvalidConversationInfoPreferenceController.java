package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class InvalidConversationInfoPreferenceController extends NotificationPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "invalid_conversation_info";
    }

    public InvalidConversationInfoPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if (this.mPreferenceFilter == null || isIncludedInFilter()) {
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow2 = this.mAppRow;
            return notificationBackend.isInInvalidMsgState(appRow2.pkg, appRow2.uid);
        }
        return false;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("conversation");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null) {
            return;
        }
        preference.setSummary(((NotificationPreferenceController) this).mContext.getString(R$string.convo_not_supported_summary, appRow.label));
    }
}
