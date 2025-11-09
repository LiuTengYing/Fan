package com.android.settings.location;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
@Deprecated
/* loaded from: classes.dex */
public class RecentLocationRequestSeeAllFragment extends DashboardFragment {
    private RecentLocationRequestSeeAllPreferenceController mController;
    private MenuItem mHideSystemMenu;
    private boolean mShowSystem = false;
    private MenuItem mShowSystemMenu;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "RecentLocationReqAll";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1325;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("profile");
        RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = (RecentLocationRequestSeeAllPreferenceController) use(RecentLocationRequestSeeAllPreferenceController.class);
        this.mController = recentLocationRequestSeeAllPreferenceController;
        recentLocationRequestSeeAllPreferenceController.init(this);
        if (i != 0) {
            this.mController.setProfileType(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_recent_requests_see_all;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2 || itemId == 3) {
            this.mShowSystem = menuItem.getItemId() == 2;
            updateMenu();
            RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = this.mController;
            if (recentLocationRequestSeeAllPreferenceController != null) {
                recentLocationRequestSeeAllPreferenceController.setShowSystem(this.mShowSystem);
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
