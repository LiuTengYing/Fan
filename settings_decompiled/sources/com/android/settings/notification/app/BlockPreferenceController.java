package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class BlockPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, OnMainSwitchChangeListener {
    private NotificationSettings.DependentFieldListener mDependentFieldListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "block";
    }

    public BlockPreferenceController(Context context, NotificationSettings.DependentFieldListener dependentFieldListener, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mAppRow == null) {
            return false;
        }
        return this.mPreferenceFilter == null || isIncludedInFilter();
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("importance");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        SettingsMainSwitchPreference settingsMainSwitchPreference = (SettingsMainSwitchPreference) preference;
        if (settingsMainSwitchPreference != null) {
            settingsMainSwitchPreference.setTitle(getSwitchBarText());
            settingsMainSwitchPreference.show();
            try {
                settingsMainSwitchPreference.addOnSwitchChangeListener(this);
            } catch (IllegalStateException unused) {
            }
            settingsMainSwitchPreference.setDisabledByAdmin(this.mAdmin);
            boolean z = false;
            if (this.mChannel != null && (!isChannelBlockable() || !isChannelConfigurable(this.mChannel))) {
                settingsMainSwitchPreference.setSwitchBarEnabled(false);
            }
            if (this.mChannelGroup != null && !isChannelGroupBlockable()) {
                settingsMainSwitchPreference.setSwitchBarEnabled(false);
            }
            if (this.mChannel == null && !isAppBlockable()) {
                settingsMainSwitchPreference.setSwitchBarEnabled(false);
            }
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                if (!this.mAppRow.banned && notificationChannel.getImportance() != 0) {
                    z = true;
                }
                settingsMainSwitchPreference.setChecked(z);
                return;
            }
            NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
            if (notificationChannelGroup != null) {
                if (!this.mAppRow.banned && !notificationChannelGroup.isBlocked()) {
                    z = true;
                }
                settingsMainSwitchPreference.setChecked(z);
                return;
            }
            settingsMainSwitchPreference.setChecked(!this.mAppRow.banned);
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        int max;
        boolean z2 = !z;
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null) {
            int importance = notificationChannel.getImportance();
            if (z2 || importance == 0) {
                if (z2) {
                    max = 0;
                } else {
                    max = isDefaultChannel() ? -1000 : Math.max(this.mChannel.getOriginalImportance(), 2);
                }
                this.mChannel.setImportance(max);
                saveChannel();
            }
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (notificationBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid)) {
                NotificationBackend.AppRow appRow2 = this.mAppRow;
                if (appRow2.banned != z2) {
                    appRow2.banned = z2;
                    this.mBackend.setNotificationsEnabledForPackage(appRow2.pkg, appRow2.uid, !z2);
                }
            }
        } else {
            NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
            if (notificationChannelGroup != null) {
                notificationChannelGroup.setBlocked(z2);
                NotificationBackend notificationBackend2 = this.mBackend;
                NotificationBackend.AppRow appRow3 = this.mAppRow;
                notificationBackend2.updateChannelGroup(appRow3.pkg, appRow3.uid, this.mChannelGroup);
            } else {
                NotificationBackend.AppRow appRow4 = this.mAppRow;
                if (appRow4 != null) {
                    appRow4.banned = z2;
                    this.mBackend.setNotificationsEnabledForPackage(appRow4.pkg, appRow4.uid, !z2);
                }
            }
        }
        this.mDependentFieldListener.onFieldValueChanged();
    }

    String getSwitchBarText() {
        CharSequence charSequence;
        if (this.mChannel != null) {
            return ((NotificationPreferenceController) this).mContext.getString(R$string.notification_content_block_title);
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup != null) {
            charSequence = notificationChannelGroup.getName();
        } else {
            charSequence = this.mAppRow.label;
        }
        return ((NotificationPreferenceController) this).mContext.getString(R$string.notification_app_switch_label, charSequence);
    }
}
