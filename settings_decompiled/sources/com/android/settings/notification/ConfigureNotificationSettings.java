package com.android.settings.notification;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ConfigureNotificationSettings extends DashboardFragment implements OnActivityResultListener {
    static final String KEY_SWIPE_DOWN = "gesture_swipe_down_fingerprint_notifications";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.configure_notification_settings) { // from class: com.android.settings.notification.ConfigureNotificationSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ConfigureNotificationSettings.buildPreferenceControllers(context, null, null);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(ConfigureNotificationSettings.KEY_SWIPE_DOWN);
            return nonIndexableKeys;
        }
    };
    private NotificationAssistantPreferenceController mNotificationAssistantPreferenceController;
    private RingtonePreference mRequestPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConfigNotiSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 337;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle("lock_screen_work_redact", "Settings.WORK_PROFILE_LOCK_SCREEN_REDACT_NOTIFICATION_TITLE", R$string.lock_screen_notifs_redact_work);
        replaceEnterpriseStringSummary("lock_screen_work_redact", "Settings.WORK_PROFILE_LOCK_SCREEN_REDACT_NOTIFICATION_SUMMARY", R$string.lock_screen_notifs_redact_work_summary);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.configure_notification_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, activity != null ? activity.getApplication() : null, this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        NotificationAssistantPreferenceController notificationAssistantPreferenceController = (NotificationAssistantPreferenceController) use(NotificationAssistantPreferenceController.class);
        this.mNotificationAssistantPreferenceController = notificationAssistantPreferenceController;
        notificationAssistantPreferenceController.setFragment(this);
        this.mNotificationAssistantPreferenceController.setBackend(new NotificationBackend());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Application application, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ShowOnLockScreenNotificationPreferenceController(context, "lock_screen_notifications"));
        arrayList.add(new NotificationRingtonePreferenceController(context) { // from class: com.android.settings.notification.ConfigureNotificationSettings.1
            @Override // com.android.settings.notification.NotificationRingtonePreferenceController, com.android.settingslib.core.AbstractPreferenceController
            public String getPreferenceKey() {
                return "notification_default_ringtone";
            }
        });
        arrayList.add(new EmergencyBroadcastPreferenceController(context, "app_and_notif_cell_broadcast_settings"));
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RingtonePreference) {
            writePreferenceClickMetric(preference);
            RingtonePreference ringtonePreference = (RingtonePreference) preference;
            this.mRequestPreference = ringtonePreference;
            ringtonePreference.onPrepareRingtonePickerIntent(ringtonePreference.getIntent());
            getActivity().startActivityForResultAsUser(this.mRequestPreference.getIntent(), 200, null, UserHandle.of(this.mRequestPreference.getUserId()));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void enableNAS(ComponentName componentName) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        NotificationAssistantPreferenceController notificationAssistantPreferenceController = (NotificationAssistantPreferenceController) use(NotificationAssistantPreferenceController.class);
        notificationAssistantPreferenceController.setNotificationAssistantGranted(componentName);
        notificationAssistantPreferenceController.updateState(preferenceScreen.findPreference(notificationAssistantPreferenceController.getPreferenceKey()));
    }
}
