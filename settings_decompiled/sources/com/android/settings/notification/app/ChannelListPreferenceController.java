package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class ChannelListPreferenceController extends NotificationPreferenceController {
    private List<NotificationChannelGroup> mChannelGroupList;
    private PreferenceCategory mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "channels";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public ChannelListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if (this.mChannel != null) {
            return (this.mBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || "miscellaneous".equals(this.mChannel.getId())) ? false : true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = (PreferenceCategory) preference;
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.ChannelListPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                ChannelListPreferenceController channelListPreferenceController = ChannelListPreferenceController.this;
                NotificationBackend notificationBackend = channelListPreferenceController.mBackend;
                NotificationBackend.AppRow appRow = channelListPreferenceController.mAppRow;
                channelListPreferenceController.mChannelGroupList = notificationBackend.getGroups(appRow.pkg, appRow.uid).getList();
                Collections.sort(ChannelListPreferenceController.this.mChannelGroupList, NotificationPreferenceController.CHANNEL_GROUP_COMPARATOR);
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                ChannelListPreferenceController channelListPreferenceController = ChannelListPreferenceController.this;
                if (((NotificationPreferenceController) channelListPreferenceController).mContext == null) {
                    return;
                }
                channelListPreferenceController.updateFullList(channelListPreferenceController.mPreference, ChannelListPreferenceController.this.mChannelGroupList);
            }
        }.execute(new Void[0]);
    }

    void updateFullList(PreferenceCategory preferenceCategory, List<NotificationChannelGroup> list) {
        if (list.isEmpty()) {
            if (preferenceCategory.getPreferenceCount() == 1 && "zeroCategories".equals(preferenceCategory.getPreference(0).getKey())) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceCategory.getPreference(0);
                preferenceGroup.setTitle(R$string.notification_channels);
                preferenceGroup.getPreference(0).setTitle(R$string.no_channels);
                return;
            }
            preferenceCategory.removeAll();
            PreferenceCategory preferenceCategory2 = new PreferenceCategory(((NotificationPreferenceController) this).mContext);
            preferenceCategory2.setTitle(R$string.notification_channels);
            preferenceCategory2.setKey("zeroCategories");
            preferenceCategory.addPreference(preferenceCategory2);
            Preference preference = new Preference(((NotificationPreferenceController) this).mContext);
            preference.setTitle(R$string.no_channels);
            preference.setEnabled(false);
            preferenceCategory2.addPreference(preference);
            return;
        }
        updateGroupList(preferenceCategory, list);
    }

    private PreferenceCategory findOrCreateGroupCategoryForKey(PreferenceCategory preferenceCategory, String str, int i) {
        if (str == null) {
            str = "categories";
        }
        int preferenceCount = preferenceCategory.getPreferenceCount();
        if (i < preferenceCount) {
            Preference preference = preferenceCategory.getPreference(i);
            if (str.equals(preference.getKey())) {
                return (PreferenceCategory) preference;
            }
        }
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference2 = preferenceCategory.getPreference(i2);
            if (str.equals(preference2.getKey())) {
                preference2.setOrder(i);
                return (PreferenceCategory) preference2;
            }
        }
        PreferenceCategory preferenceCategory2 = new PreferenceCategory(((NotificationPreferenceController) this).mContext);
        preferenceCategory2.setOrder(i);
        preferenceCategory2.setKey(str);
        preferenceCategory.addPreference(preferenceCategory2);
        return preferenceCategory2;
    }

    private void updateGroupList(PreferenceCategory preferenceCategory, List<NotificationChannelGroup> list) {
        int size = list.size();
        int preferenceCount = preferenceCategory.getPreferenceCount();
        ArrayList<PreferenceCategory> arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            NotificationChannelGroup notificationChannelGroup = list.get(i);
            PreferenceCategory findOrCreateGroupCategoryForKey = findOrCreateGroupCategoryForKey(preferenceCategory, notificationChannelGroup.getId(), i);
            arrayList.add(findOrCreateGroupCategoryForKey);
            updateGroupPreferences(notificationChannelGroup, findOrCreateGroupCategoryForKey);
        }
        int preferenceCount2 = preferenceCategory.getPreferenceCount();
        boolean z = (preferenceCount == 0 || preferenceCount == size) ? false : true;
        boolean z2 = preferenceCount2 != size;
        if (z || z2) {
            preferenceCategory.removeAll();
            for (PreferenceCategory preferenceCategory2 : arrayList) {
                preferenceCategory.addPreference(preferenceCategory2);
            }
        }
    }

    private PrimarySwitchPreference findOrCreateChannelPrefForKey(PreferenceGroup preferenceGroup, String str, int i) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        if (i < preferenceCount) {
            Preference preference = preferenceGroup.getPreference(i);
            if (str.equals(preference.getKey())) {
                return (PrimarySwitchPreference) preference;
            }
        }
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference2 = preferenceGroup.getPreference(i2);
            if (str.equals(preference2.getKey())) {
                preference2.setOrder(i);
                return (PrimarySwitchPreference) preference2;
            }
        }
        PrimarySwitchPreference primarySwitchPreference = new PrimarySwitchPreference(((NotificationPreferenceController) this).mContext);
        primarySwitchPreference.setOrder(i);
        primarySwitchPreference.setKey(str);
        preferenceGroup.addPreference(primarySwitchPreference);
        return primarySwitchPreference;
    }

    private void updateGroupPreferences(NotificationChannelGroup notificationChannelGroup, PreferenceGroup preferenceGroup) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        ArrayList<Preference> arrayList = new ArrayList();
        if (notificationChannelGroup.getId() == null) {
            preferenceGroup.setTitle(R$string.notification_channels_other);
        } else {
            preferenceGroup.setTitle(notificationChannelGroup.getName());
            arrayList.add(addOrUpdateGroupToggle(preferenceGroup, notificationChannelGroup));
        }
        boolean z = preferenceGroup.getPreferenceCount() == arrayList.size();
        List<NotificationChannel> emptyList = notificationChannelGroup.isBlocked() ? Collections.emptyList() : notificationChannelGroup.getChannels();
        Collections.sort(emptyList, NotificationPreferenceController.CHANNEL_COMPARATOR);
        for (NotificationChannel notificationChannel : emptyList) {
            if (TextUtils.isEmpty(notificationChannel.getConversationId()) || notificationChannel.isDemoted()) {
                PrimarySwitchPreference findOrCreateChannelPrefForKey = findOrCreateChannelPrefForKey(preferenceGroup, notificationChannel.getId(), arrayList.size());
                updateSingleChannelPrefs(findOrCreateChannelPrefForKey, notificationChannel, notificationChannelGroup.isBlocked());
                arrayList.add(findOrCreateChannelPrefForKey);
            }
        }
        int preferenceCount2 = preferenceGroup.getPreferenceCount();
        int size = arrayList.size();
        boolean z2 = (z || preferenceCount == size) ? false : true;
        boolean z3 = preferenceCount2 != size;
        if (z2 || z3) {
            preferenceGroup.removeAll();
            for (Preference preference : arrayList) {
                preferenceGroup.addPreference(preference);
            }
        }
    }

    private Preference addOrUpdateGroupToggle(PreferenceGroup preferenceGroup, final NotificationChannelGroup notificationChannelGroup) {
        RestrictedSwitchPreference restrictedSwitchPreference;
        boolean z;
        boolean z2 = false;
        if (preferenceGroup.getPreferenceCount() > 0 && (preferenceGroup.getPreference(0) instanceof RestrictedSwitchPreference)) {
            restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceGroup.getPreference(0);
            z = false;
        } else {
            restrictedSwitchPreference = new RestrictedSwitchPreference(((NotificationPreferenceController) this).mContext);
            z = true;
        }
        restrictedSwitchPreference.setOrder(-1);
        restrictedSwitchPreference.setTitle(((NotificationPreferenceController) this).mContext.getString(R$string.notification_switch_label, notificationChannelGroup.getName()));
        if (this.mAdmin == null && isChannelGroupBlockable(notificationChannelGroup)) {
            z2 = true;
        }
        restrictedSwitchPreference.setEnabled(z2);
        restrictedSwitchPreference.setChecked(true ^ notificationChannelGroup.isBlocked());
        restrictedSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.ChannelListPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$addOrUpdateGroupToggle$0;
                lambda$addOrUpdateGroupToggle$0 = ChannelListPreferenceController.this.lambda$addOrUpdateGroupToggle$0(notificationChannelGroup, preference);
                return lambda$addOrUpdateGroupToggle$0;
            }
        });
        if (z) {
            preferenceGroup.addPreference(restrictedSwitchPreference);
        }
        return restrictedSwitchPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$addOrUpdateGroupToggle$0(NotificationChannelGroup notificationChannelGroup, Preference preference) {
        notificationChannelGroup.setBlocked(!((SwitchPreference) preference).isChecked());
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannelGroup(appRow.pkg, appRow.uid, notificationChannelGroup);
        onGroupBlockStateChanged(notificationChannelGroup);
        return true;
    }

    private void updateSingleChannelPrefs(PrimarySwitchPreference primarySwitchPreference, final NotificationChannel notificationChannel, boolean z) {
        primarySwitchPreference.setSwitchEnabled(this.mAdmin == null && isChannelBlockable(notificationChannel) && isChannelConfigurable(notificationChannel) && !z);
        if (notificationChannel.getImportance() > 2) {
            primarySwitchPreference.setIcon(getAlertingIcon());
        } else {
            primarySwitchPreference.setIcon(R$drawable.empty_icon);
        }
        primarySwitchPreference.setIconSize(2);
        primarySwitchPreference.setTitle(notificationChannel.getName());
        primarySwitchPreference.setSummary(NotificationBackend.getSentSummary(((NotificationPreferenceController) this).mContext, this.mAppRow.sentByChannel.get(notificationChannel.getId()), false));
        primarySwitchPreference.setChecked(notificationChannel.getImportance() != 0);
        Bundle bundle = new Bundle();
        bundle.putInt("uid", this.mAppRow.uid);
        bundle.putString("package", this.mAppRow.pkg);
        bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
        bundle.putBoolean("fromSettings", true);
        primarySwitchPreference.setIntent(new SubSettingLauncher(((NotificationPreferenceController) this).mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setTitleRes(R$string.notification_channel_title).setSourceMetricsCategory(72).toIntent());
        primarySwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelListPreferenceController$$ExternalSyntheticLambda1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$updateSingleChannelPrefs$1;
                lambda$updateSingleChannelPrefs$1 = ChannelListPreferenceController.this.lambda$updateSingleChannelPrefs$1(notificationChannel, preference, obj);
                return lambda$updateSingleChannelPrefs$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateSingleChannelPrefs$1(NotificationChannel notificationChannel, Preference preference, Object obj) {
        notificationChannel.setImportance(((Boolean) obj).booleanValue() ? Math.max(notificationChannel.getOriginalImportance(), 2) : 0);
        notificationChannel.lockFields(4);
        PrimarySwitchPreference primarySwitchPreference = (PrimarySwitchPreference) preference;
        primarySwitchPreference.setIcon(R$drawable.empty_icon);
        if (notificationChannel.getImportance() > 2) {
            primarySwitchPreference.setIcon(getAlertingIcon());
        }
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
        return true;
    }

    private Drawable getAlertingIcon() {
        Drawable drawable = ((NotificationPreferenceController) this).mContext.getDrawable(R$drawable.ic_notifications_alert);
        drawable.setTintList(Utils.getColorAccent(((NotificationPreferenceController) this).mContext));
        return drawable;
    }

    protected void onGroupBlockStateChanged(NotificationChannelGroup notificationChannelGroup) {
        PreferenceGroup preferenceGroup;
        if (notificationChannelGroup == null || (preferenceGroup = (PreferenceGroup) this.mPreference.findPreference(notificationChannelGroup.getId())) == null) {
            return;
        }
        updateGroupPreferences(notificationChannelGroup, preferenceGroup);
    }
}
