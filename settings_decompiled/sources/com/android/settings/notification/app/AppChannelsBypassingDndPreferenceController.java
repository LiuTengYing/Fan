package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.core.text.BidiFormatter;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class AppChannelsBypassingDndPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private RestrictedSwitchPreference mAllNotificationsToggle;
    private List<NotificationChannel> mChannels;
    private PreferenceCategory mPreferenceCategory;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_bypassing_app_channels_list";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public AppChannelsBypassingDndPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mChannels = new ArrayList();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("zen_mode_bypassing_app_channels_list");
        this.mPreferenceCategory = preferenceCategory;
        RestrictedSwitchPreference restrictedSwitchPreference = new RestrictedSwitchPreference(preferenceCategory.getContext());
        this.mAllNotificationsToggle = restrictedSwitchPreference;
        restrictedSwitchPreference.setTitle(R$string.zen_mode_bypassing_app_channels_toggle_all);
        this.mAllNotificationsToggle.setDisabledByAdmin(this.mAdmin);
        RestrictedSwitchPreference restrictedSwitchPreference2 = this.mAllNotificationsToggle;
        restrictedSwitchPreference2.setEnabled(this.mAdmin == null || !restrictedSwitchPreference2.isDisabledByAdmin());
        this.mAllNotificationsToggle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                boolean isChecked = ((SwitchPreference) preference).isChecked();
                for (NotificationChannel notificationChannel : AppChannelsBypassingDndPreferenceController.this.mChannels) {
                    if (AppChannelsBypassingDndPreferenceController.this.showNotification(notificationChannel) && AppChannelsBypassingDndPreferenceController.this.isChannelConfigurable(notificationChannel)) {
                        notificationChannel.setBypassDnd(isChecked);
                        notificationChannel.lockFields(1);
                        AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                        NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                        NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                        notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
                    }
                }
                for (int i = 1; i < AppChannelsBypassingDndPreferenceController.this.mPreferenceCategory.getPreferenceCount(); i++) {
                    AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController2 = AppChannelsBypassingDndPreferenceController.this;
                    ((PrimarySwitchPreference) AppChannelsBypassingDndPreferenceController.this.mPreferenceCategory.getPreference(i)).setChecked(appChannelsBypassingDndPreferenceController2.showNotificationInDnd((NotificationChannel) appChannelsBypassingDndPreferenceController2.mChannels.get(i - 1)));
                }
                return true;
            }
        });
        loadAppChannels();
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            loadAppChannels();
        }
    }

    private void loadAppChannels() {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                ArrayList arrayList = new ArrayList();
                AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                for (NotificationChannelGroup notificationChannelGroup : notificationBackend.getGroups(appRow.pkg, appRow.uid).getList()) {
                    for (NotificationChannel notificationChannel : notificationChannelGroup.getChannels()) {
                        if (!AppChannelsBypassingDndPreferenceController.this.isConversation(notificationChannel)) {
                            arrayList.add(notificationChannel);
                        }
                    }
                }
                Collections.sort(arrayList, NotificationPreferenceController.CHANNEL_COMPARATOR);
                AppChannelsBypassingDndPreferenceController.this.mChannels = arrayList;
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                if (((NotificationPreferenceController) appChannelsBypassingDndPreferenceController).mContext == null) {
                    return;
                }
                appChannelsBypassingDndPreferenceController.populateList();
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populateList() {
        PreferenceCategory preferenceCategory = this.mPreferenceCategory;
        if (preferenceCategory == null) {
            return;
        }
        preferenceCategory.removeAll();
        this.mPreferenceCategory.addPreference(this.mAllNotificationsToggle);
        for (final NotificationChannel notificationChannel : this.mChannels) {
            PrimarySwitchPreference primarySwitchPreference = new PrimarySwitchPreference(((NotificationPreferenceController) this).mContext);
            primarySwitchPreference.setDisabledByAdmin(this.mAdmin);
            primarySwitchPreference.setSwitchEnabled((this.mAdmin == null || !primarySwitchPreference.isDisabledByAdmin()) && isChannelConfigurable(notificationChannel) && showNotification(notificationChannel));
            primarySwitchPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(notificationChannel.getName()));
            primarySwitchPreference.setChecked(showNotificationInDnd(notificationChannel));
            primarySwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.3
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    notificationChannel.setBypassDnd(((Boolean) obj).booleanValue());
                    notificationChannel.lockFields(1);
                    AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                    NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                    NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                    notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
                    AppChannelsBypassingDndPreferenceController.this.mAllNotificationsToggle.setChecked(AppChannelsBypassingDndPreferenceController.this.areAllChannelsBypassing());
                    return true;
                }
            });
            final Bundle bundle = new Bundle();
            bundle.putInt("uid", this.mAppRow.uid);
            bundle.putString("package", this.mAppRow.pkg);
            bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
            bundle.putBoolean("fromSettings", true);
            primarySwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$populateList$0;
                    lambda$populateList$0 = AppChannelsBypassingDndPreferenceController.this.lambda$populateList$0(bundle, preference);
                    return lambda$populateList$0;
                }
            });
            this.mPreferenceCategory.addPreference(primarySwitchPreference);
        }
        this.mAllNotificationsToggle.setChecked(areAllChannelsBypassing());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$populateList$0(Bundle bundle, Preference preference) {
        new SubSettingLauncher(((NotificationPreferenceController) this).mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setUserHandle(UserHandle.of(this.mAppRow.userId)).setTitleRes(R$string.notification_channel_title).setSourceMetricsCategory(1840).launch();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean areAllChannelsBypassing() {
        boolean z = true;
        for (NotificationChannel notificationChannel : this.mChannels) {
            if (showNotification(notificationChannel)) {
                z &= showNotificationInDnd(notificationChannel);
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showNotificationInDnd(NotificationChannel notificationChannel) {
        return notificationChannel.canBypassDnd() && showNotification(notificationChannel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showNotification(NotificationChannel notificationChannel) {
        return notificationChannel.getImportance() != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isConversation(NotificationChannel notificationChannel) {
        return (notificationChannel.getConversationId() == null || notificationChannel.isDemoted()) ? false : true;
    }
}
