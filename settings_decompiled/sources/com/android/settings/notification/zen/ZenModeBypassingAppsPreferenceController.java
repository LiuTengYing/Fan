package com.android.settings.notification.zen;

import android.app.Application;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.icu.text.MessageFormat;
import android.text.TextUtils;
import android.util.ArraySet;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class ZenModeBypassingAppsPreferenceController extends AbstractZenModePreferenceController {
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    private NotificationBackend mNotificationBackend;
    @VisibleForTesting
    protected Preference mPreference;
    private String mSummary;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_behavior_apps";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, Lifecycle lifecycle) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, lifecycle);
    }

    private ZenModeBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_mode_behavior_apps", lifecycle);
        this.mNotificationBackend = new NotificationBackend();
        ApplicationsState.Callbacks callbacks = new ApplicationsState.Callbacks() { // from class: com.android.settings.notification.zen.ZenModeBypassingAppsPreferenceController.1
            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }
        };
        this.mAppSessionCallbacks = callbacks;
        if (applicationsState == null || fragment == null) {
            return;
        }
        this.mAppSession = applicationsState.newSession(callbacks, fragment.getLifecycle());
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = preferenceScreen.findPreference("zen_mode_behavior_apps");
        updateAppsBypassingDndSummaryText();
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getSummary() {
        return this.mSummary;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppsBypassingDndSummaryText() {
        ApplicationsState.Session session = this.mAppSession;
        if (session == null) {
            return;
        }
        updateAppsBypassingDndSummaryText(session.rebuild(ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.ALPHA_COMPARATOR));
    }

    @VisibleForTesting
    void updateAppsBypassingDndSummaryText(List<ApplicationsState.AppEntry> list) {
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            this.mPreference.setEnabled(false);
            this.mSummary = this.mContext.getResources().getString(R$string.zen_mode_bypassing_apps_subtext_none);
            return;
        }
        this.mPreference.setEnabled(true);
        if (list == null) {
            return;
        }
        ArraySet arraySet = new ArraySet();
        for (ApplicationsState.AppEntry appEntry : list) {
            ApplicationInfo applicationInfo = appEntry.info;
            for (NotificationChannel notificationChannel : this.mNotificationBackend.getNotificationChannelsBypassingDnd(applicationInfo.packageName, applicationInfo.uid).getList()) {
                if (TextUtils.isEmpty(notificationChannel.getConversationId()) || notificationChannel.isDemoted()) {
                    arraySet.add(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                }
            }
        }
        int size = arraySet.size();
        String[] strArr = (String[]) arraySet.toArray(new String[size]);
        MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R$string.zen_mode_bypassing_apps_subtext), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(size));
        if (size >= 1) {
            hashMap.put("app_1", strArr[0]);
            if (size >= 2) {
                hashMap.put("app_2", strArr[1]);
                if (size == 3) {
                    hashMap.put("app_3", strArr[2]);
                }
            }
        }
        this.mSummary = messageFormat.format(hashMap);
        refreshSummary(this.mPreference);
    }
}
