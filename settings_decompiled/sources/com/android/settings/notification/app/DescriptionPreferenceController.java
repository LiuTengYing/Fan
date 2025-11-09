package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
/* loaded from: classes.dex */
public class DescriptionPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "desc";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public DescriptionPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable()) {
            if (this.mChannel != null || hasValidGroup()) {
                NotificationChannel notificationChannel = this.mChannel;
                if (notificationChannel == null || TextUtils.isEmpty(notificationChannel.getDescription())) {
                    return hasValidGroup() && !TextUtils.isEmpty(this.mChannelGroup.getDescription());
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                preference.setTitle(notificationChannel.getDescription());
            } else if (hasValidGroup()) {
                preference.setTitle(this.mChannelGroup.getDescription());
            }
        }
        preference.setEnabled(false);
        preference.setSelectable(false);
    }
}
