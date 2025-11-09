package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class DeletedChannelsPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "deleted";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public DeletedChannelsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable() && this.mChannel == null && !hasValidGroup()) {
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow = this.mAppRow;
            return notificationBackend.getDeletedChannelCount(appRow.pkg, appRow.uid) > 0;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow != null) {
            int deletedChannelCount = this.mBackend.getDeletedChannelCount(appRow.pkg, appRow.uid);
            preference.setTitle(((NotificationPreferenceController) this).mContext.getResources().getQuantityString(R$plurals.deleted_channels, deletedChannelCount, Integer.valueOf(deletedChannelCount)));
        }
        preference.setSelectable(false);
    }
}
