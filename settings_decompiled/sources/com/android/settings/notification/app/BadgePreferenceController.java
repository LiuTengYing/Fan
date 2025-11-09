package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class BadgePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "badge";
    }

    public BadgePreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable()) {
            if ((this.mAppRow == null && this.mChannel == null) || Settings.Secure.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_badging", 1) == 0) {
                return false;
            }
            if (this.mChannel == null || isDefaultChannel()) {
                return true;
            }
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (appRow == null) {
                return false;
            }
            return appRow.showBadge;
        }
        return false;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("launcher");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                restrictedSwitchPreference.setChecked(notificationChannel.canShowBadge());
                restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
                return;
            }
            restrictedSwitchPreference.setChecked(this.mAppRow.showBadge);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null) {
            notificationChannel.setShowBadge(booleanValue);
            saveChannel();
            return true;
        }
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow != null) {
            appRow.showBadge = booleanValue;
            this.mBackend.setShowBadge(appRow.pkg, appRow.uid, booleanValue);
            return true;
        }
        return true;
    }
}
