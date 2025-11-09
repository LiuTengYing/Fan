package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class ConversationPromotePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    SettingsPreferenceFragment mHostFragment;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "convo_promote";
    }

    public ConversationPromotePreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mHostFragment = settingsPreferenceFragment;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationChannel notificationChannel;
        return (!super.isAvailable() || this.mAppRow == null || (notificationChannel = this.mChannel) == null || TextUtils.isEmpty(notificationChannel.getConversationId()) || !this.mChannel.isDemoted()) ? false : true;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("conversation");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setEnabled(this.mAdmin == null);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.mChannel == null || !"convo_promote".equals(preference.getKey())) {
            return false;
        }
        this.mChannel.setDemoted(false);
        this.mChannel.setBypassDnd(false);
        saveChannel();
        SettingsPreferenceFragment settingsPreferenceFragment = this.mHostFragment;
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getActivity().finish();
            return true;
        }
        return true;
    }
}
