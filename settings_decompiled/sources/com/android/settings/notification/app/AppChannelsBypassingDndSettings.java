package com.android.settings.notification.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$xml;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AppChannelsBypassingDndSettings extends NotificationSettings {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppChannelsBypassingDndSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1840;
    }

    @Override // com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("AppChannelsBypassingDndSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, null, null, null, null, this.mSuspendedAppsAdmin, null);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.app_channels_bypassing_dnd_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new AppChannelsBypassingDndPreferenceController(context, new NotificationBackend()));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
