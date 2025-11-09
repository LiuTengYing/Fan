package com.android.settings.location;

import android.content.Context;
import android.os.Bundle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class RecentLocationAccessSeeAllFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.location_recent_access_see_all);
    private RecentLocationAccessSeeAllPreferenceController mController;
    private MenuItem mHideSystemMenu;
    private boolean mShowSystem = false;
    private MenuItem mShowSystemMenu;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "RecentLocAccessSeeAll";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1325;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        RecentLocationAccessSeeAllPreferenceController recentLocationAccessSeeAllPreferenceController = (RecentLocationAccessSeeAllPreferenceController) use(RecentLocationAccessSeeAllPreferenceController.class);
        this.mController = recentLocationAccessSeeAllPreferenceController;
        recentLocationAccessSeeAllPreferenceController.init(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = false;
        if (DeviceConfig.getBoolean("privacy", "location_indicators_small_enabled", false) && Settings.Secure.getInt(getContentResolver(), "locationShowSystemOps", 0) == 1) {
            z = true;
        }
        this.mShowSystem = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_recent_access_see_all;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2 || itemId == 3) {
            this.mShowSystem = menuItem.getItemId() == 2;
            Settings.Secure.putInt(getContentResolver(), "locationShowSystemOps", this.mShowSystem ? 1 : 0);
            updateMenu();
            RecentLocationAccessSeeAllPreferenceController recentLocationAccessSeeAllPreferenceController = this.mController;
            if (recentLocationAccessSeeAllPreferenceController != null) {
                recentLocationAccessSeeAllPreferenceController.setShowSystem(this.mShowSystem);
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updateMenu() {
        this.mShowSystemMenu.setVisible(!this.mShowSystem);
        this.mHideSystemMenu.setVisible(this.mShowSystem);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mShowSystemMenu = menu.add(0, 2, 0, R$string.menu_show_system);
        this.mHideSystemMenu = menu.add(0, 3, 0, R$string.menu_hide_system);
        updateMenu();
    }
}
