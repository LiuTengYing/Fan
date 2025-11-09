package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class HighImportancePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private NotificationSettings.DependentFieldListener mDependentFieldListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "high_importance";
    }

    public HighImportancePreferenceController(Context context, NotificationSettings.DependentFieldListener dependentFieldListener, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return super.isAvailable() && this.mChannel != null && !isDefaultChannel() && this.mChannel.getImportance() >= 3;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("importance");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationChannel notificationChannel;
        if (this.mAppRow == null || (notificationChannel = this.mChannel) == null) {
            return;
        }
        preference.setEnabled(this.mAdmin == null && isChannelConfigurable(notificationChannel));
        ((RestrictedSwitchPreference) preference).setChecked(this.mChannel.getImportance() >= 4);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel != null) {
            this.mChannel.setImportance(((Boolean) obj).booleanValue() ? 4 : 3);
            this.mChannel.lockFields(4);
            saveChannel();
            this.mDependentFieldListener.onFieldValueChanged();
            return true;
        }
        return true;
    }
}
