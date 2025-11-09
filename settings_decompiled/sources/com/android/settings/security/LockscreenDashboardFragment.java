package com.android.settings.security;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.AmbientDisplayAlwaysOnPreferenceController;
import com.android.settings.display.AmbientDisplayNotificationsPreferenceController;
import com.android.settings.gestures.DoubleTapScreenPreferenceController;
import com.android.settings.gestures.PickupGesturePreferenceController;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.OwnerInfoPreferenceController;
import com.android.settings.security.screenlock.LockScreenPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class LockscreenDashboardFragment extends DashboardFragment implements OwnerInfoPreferenceController.OwnerInfoCallback {
    static final String KEY_ADD_USER_FROM_LOCK_SCREEN = "security_lockscreen_add_users_when_locked";
    static final String KEY_LOCK_SCREEN_NOTIFICATON = "security_setting_lock_screen_notif";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE = "security_setting_lock_screen_notif_work";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER = "security_setting_lock_screen_notif_work_header";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.security_lockscreen_settings) { // from class: com.android.settings.security.LockscreenDashboardFragment.2
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new LockScreenNotificationPreferenceController(context));
            arrayList.add(new OwnerInfoPreferenceController(context, null));
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(LockscreenDashboardFragment.KEY_ADD_USER_FROM_LOCK_SCREEN);
            return nonIndexableKeys;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return new LockScreenPreferenceController(context, "anykey").isAvailable();
        }
    };
    private AmbientDisplayConfiguration mConfig;
    ContentObserver mControlsContentObserver;
    private OwnerInfoPreferenceController mOwnerInfoPreferenceController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LockscreenDashboardFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 882;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle(KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE, "Settings.WORK_PROFILE_LOCKED_NOTIFICATION_TITLE", R$string.locked_work_profile_notification_title);
        replaceEnterpriseStringTitle(KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER, "Settings.WORK_PROFILE_NOTIFICATIONS_SECTION_HEADER", R$string.profile_section_header);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.security_lockscreen_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_lockscreen;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AmbientDisplayAlwaysOnPreferenceController) use(AmbientDisplayAlwaysOnPreferenceController.class)).setConfig(getConfig(context));
        ((AmbientDisplayNotificationsPreferenceController) use(AmbientDisplayNotificationsPreferenceController.class)).setConfig(getConfig(context));
        ((DoubleTapScreenPreferenceController) use(DoubleTapScreenPreferenceController.class)).setConfig(getConfig(context));
        ((PickupGesturePreferenceController) use(PickupGesturePreferenceController.class)).setConfig(getConfig(context));
        this.mControlsContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.security.LockscreenDashboardFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                LockscreenDashboardFragment.this.updatePreferenceStates();
            }
        };
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lockscreen_show_controls"), false, this.mControlsContentObserver);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        if (this.mControlsContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mControlsContentObserver);
            this.mControlsContentObserver = null;
        }
        super.onDetach();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, KEY_LOCK_SCREEN_NOTIFICATON, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE);
        settingsLifecycle.addObserver(lockScreenNotificationPreferenceController);
        arrayList.add(lockScreenNotificationPreferenceController);
        OwnerInfoPreferenceController ownerInfoPreferenceController = new OwnerInfoPreferenceController(context, this);
        this.mOwnerInfoPreferenceController = ownerInfoPreferenceController;
        arrayList.add(ownerInfoPreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.security.OwnerInfoPreferenceController.OwnerInfoCallback
    public void onOwnerInfoUpdated() {
        OwnerInfoPreferenceController ownerInfoPreferenceController = this.mOwnerInfoPreferenceController;
        if (ownerInfoPreferenceController != null) {
            ownerInfoPreferenceController.updateSummary();
        }
    }

    private AmbientDisplayConfiguration getConfig(Context context) {
        if (this.mConfig == null) {
            this.mConfig = new AmbientDisplayConfiguration(context);
        }
        return this.mConfig;
    }
}
