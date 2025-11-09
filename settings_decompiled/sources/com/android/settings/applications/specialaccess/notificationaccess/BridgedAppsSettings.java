package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class BridgedAppsSettings extends DashboardFragment {
    private ComponentName mComponentName;
    private ApplicationsState.AppFilter mFilter;
    private boolean mShowSystem;
    private int mUserId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BridgedAppsSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1865;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mShowSystem = bundle != null && bundle.getBoolean("show_system");
        ((BridgedAppsPreferenceController) use(BridgedAppsPreferenceController.class)).setNm(new NotificationBackend());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 43, 0, this.mShowSystem ? R$string.menu_hide_system : R$string.menu_show_system);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 43) {
            boolean z = !this.mShowSystem;
            this.mShowSystem = z;
            menuItem.setTitle(z ? R$string.menu_hide_system : R$string.menu_show_system);
            this.mFilter = this.mShowSystem ? ApplicationsState.FILTER_NOT_HIDE : ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
            ((BridgedAppsPreferenceController) use(BridgedAppsPreferenceController.class)).setFilter(this.mFilter).rebuild();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_system", this.mShowSystem);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mFilter = this.mShowSystem ? ApplicationsState.FILTER_ALL_ENABLED : ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
        Bundle arguments = getArguments();
        Intent intent = arguments == null ? getIntent() : (Intent) arguments.getParcelable("intent");
        String string = arguments.getString("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME");
        if (string != null) {
            this.mComponentName = ComponentName.unflattenFromString(string);
        }
        if (intent != null && intent.hasExtra("android.intent.extra.user_handle")) {
            this.mUserId = ((UserHandle) intent.getParcelableExtra("android.intent.extra.user_handle")).getIdentifier();
        } else {
            this.mUserId = UserHandle.myUserId();
        }
        ((BridgedAppsPreferenceController) use(BridgedAppsPreferenceController.class)).setAppState(ApplicationsState.getInstance((Application) context.getApplicationContext())).setCn(this.mComponentName).setUserId(this.mUserId).setSession(getSettingsLifecycle()).setFilter(this.mFilter).rebuild();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.notification_access_bridged_apps_settings;
    }
}
