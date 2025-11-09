package com.android.settings.notification.zen;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppChannelsBypassingDndSettings;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class ZenModeAllBypassingAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public static final String KEY_NO_APPS = getKey("none");
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    ApplicationsState mApplicationsState;
    private Fragment mHostFragment;
    private final NotificationBackend mNotificationBackend;
    Context mPrefContext;
    PreferenceCategory mPreferenceCategory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getKey(String str) {
        return str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_bypassing_apps_list";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeAllBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, NotificationBackend notificationBackend) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, notificationBackend);
    }

    private ZenModeAllBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, NotificationBackend notificationBackend) {
        super(context);
        ApplicationsState.Callbacks callbacks = new ApplicationsState.Callbacks() { // from class: com.android.settings.notification.zen.ZenModeAllBypassingAppsPreferenceController.1
            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }
        };
        this.mAppSessionCallbacks = callbacks;
        this.mNotificationBackend = notificationBackend;
        this.mApplicationsState = applicationsState;
        this.mHostFragment = fragment;
        if (applicationsState == null || fragment == null) {
            return;
        }
        this.mAppSession = applicationsState.newSession(callbacks, fragment.getLifecycle());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("zen_mode_bypassing_apps_list");
        this.mPrefContext = preferenceScreen.getContext();
        updateAppList();
        super.displayPreference(preferenceScreen);
    }

    public void updateAppList() {
        ApplicationsState.Session session = this.mAppSession;
        if (session == null) {
            return;
        }
        updateAppList(session.rebuild(ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.ALPHA_COMPARATOR));
    }

    private void updateIcon(final Preference preference, final ApplicationsState.AppEntry appEntry) {
        synchronized (appEntry) {
            Drawable iconFromCache = AppUtils.getIconFromCache(appEntry);
            if (iconFromCache != null && appEntry.mounted) {
                preference.setIcon(iconFromCache);
            } else {
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.notification.zen.ZenModeAllBypassingAppsPreferenceController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ZenModeAllBypassingAppsPreferenceController.this.lambda$updateIcon$1(appEntry, preference);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIcon$1(ApplicationsState.AppEntry appEntry, final Preference preference) {
        final Drawable icon = AppUtils.getIcon(this.mPrefContext, appEntry);
        if (icon != null) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.notification.zen.ZenModeAllBypassingAppsPreferenceController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Preference.this.setIcon(icon);
                }
            });
        }
    }

    void updateAppList(List<ApplicationsState.AppEntry> list) {
        if (this.mPreferenceCategory == null || list == null) {
            return;
        }
        ArrayList<Preference> arrayList = new ArrayList();
        for (final ApplicationsState.AppEntry appEntry : list) {
            ApplicationInfo applicationInfo = appEntry.info;
            String str = applicationInfo.packageName;
            int channelCount = this.mNotificationBackend.getChannelCount(str, applicationInfo.uid);
            int size = this.mNotificationBackend.getNotificationChannelsBypassingDnd(str, appEntry.info.uid).getList().size();
            if (size > 0) {
                String key = getKey(str);
                Preference findPreference = this.mPreferenceCategory.findPreference(key);
                if (findPreference == null) {
                    findPreference = new AppPreference(this.mPrefContext);
                    findPreference.setKey(key);
                    findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeAllBypassingAppsPreferenceController$$ExternalSyntheticLambda0
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            boolean lambda$updateAppList$2;
                            lambda$updateAppList$2 = ZenModeAllBypassingAppsPreferenceController.this.lambda$updateAppList$2(appEntry, preference);
                            return lambda$updateAppList$2;
                        }
                    });
                }
                findPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                updateIcon(findPreference, appEntry);
                if (channelCount > size) {
                    findPreference.setSummary(R$string.zen_mode_bypassing_apps_summary_some);
                } else {
                    findPreference.setSummary(R$string.zen_mode_bypassing_apps_summary_all);
                }
                arrayList.add(findPreference);
            }
        }
        if (arrayList.size() == 0) {
            PreferenceCategory preferenceCategory = this.mPreferenceCategory;
            String str2 = KEY_NO_APPS;
            Preference findPreference2 = preferenceCategory.findPreference(str2);
            if (findPreference2 == null) {
                findPreference2 = new Preference(this.mPrefContext);
                findPreference2.setKey(str2);
                findPreference2.setTitle(R$string.zen_mode_bypassing_apps_none);
            }
            arrayList.add(findPreference2);
        }
        if (hasAppListChanged(arrayList, this.mPreferenceCategory)) {
            this.mPreferenceCategory.removeAll();
            for (Preference preference : arrayList) {
                this.mPreferenceCategory.addPreference(preference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateAppList$2(ApplicationsState.AppEntry appEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appEntry.info.packageName);
        bundle.putInt("uid", appEntry.info.uid);
        new SubSettingLauncher(this.mContext).setDestination(AppChannelsBypassingDndSettings.class.getName()).setArguments(bundle).setUserHandle(UserHandle.getUserHandleForUid(appEntry.info.uid)).setResultListener(this.mHostFragment, 0).setSourceMetricsCategory(1589).launch();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasAppListChanged(List<Preference> list, PreferenceCategory preferenceCategory) {
        if (list.size() != preferenceCategory.getPreferenceCount()) {
            return true;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!Objects.equals(list.get(i).getKey(), preferenceCategory.getPreference(i).getKey())) {
                return true;
            }
        }
        return false;
    }
}
