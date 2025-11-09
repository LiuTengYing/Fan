package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SoundWorkSettings extends DashboardFragment implements OnActivityResultListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.sound_work_settings) { // from class: com.android.settings.notification.SoundWorkSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SoundWorkSettings.isSupportWorkProfileSound(context);
        }
    };
    private RingtonePreference mRequestPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SoundWorkSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1877;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            String string = bundle.getString("selected_preference", null);
            if (!TextUtils.isEmpty(string)) {
                this.mRequestPreference = (RingtonePreference) findPreference(string);
            }
        }
        replaceEnterprisePreferenceScreenTitle("Settings.WORK_PROFILE_SOUND_SETTINGS_SECTION_HEADER", R$string.sound_work_settings);
        replaceEnterpriseStringTitle("work_use_personal_sounds", "Settings.WORK_PROFILE_USE_PERSONAL_SOUNDS_TITLE", R$string.work_use_personal_sounds_title);
        replaceEnterpriseStringSummary("work_use_personal_sounds", "Settings.WORK_PROFILE_USE_PERSONAL_SOUNDS_SUMMARY", R$string.work_use_personal_sounds_summary);
        replaceEnterpriseStringTitle("work_ringtone", "Settings.WORK_PROFILE_RINGTONE_TITLE", R$string.work_ringtone_title);
        replaceEnterpriseStringTitle("work_alarm_ringtone", "Settings.WORK_PROFILE_ALARM_RINGTONE_TITLE", R$string.work_alarm_ringtone_title);
        replaceEnterpriseStringTitle("work_notification_ringtone", "Settings.WORK_PROFILE_NOTIFICATION_RINGTONE_TITLE", R$string.work_notification_ringtone_title);
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

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.sound_work_settings;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SoundWorkSettings soundWorkSettings, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SoundWorkSettingsController(context, soundWorkSettings, lifecycle));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isSupportWorkProfileSound(Context context) {
        AudioHelper audioHelper = new AudioHelper(context);
        return (audioHelper.getManagedProfileId(UserManager.get(context)) != -10000) && (audioHelper.isSingleVolume() ^ true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableWorkSync() {
        SoundWorkSettingsController soundWorkSettingsController = (SoundWorkSettingsController) use(SoundWorkSettingsController.class);
        if (soundWorkSettingsController != null) {
            soundWorkSettingsController.enableWorkSync();
        }
    }
}
