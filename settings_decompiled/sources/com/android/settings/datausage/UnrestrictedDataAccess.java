package com.android.settings.datausage;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.applications.AppIconCacheManager;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class UnrestrictedDataAccess extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.unrestricted_data_access_settings);
    private ApplicationsState.AppFilter mFilter;
    private boolean mShowSystem;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "UnrestrictedDataAccess";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 349;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mShowSystem = bundle != null && bundle.getBoolean("show_system");
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setParentFragment(this);
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
            this.mFilter = this.mShowSystem ? ApplicationsState.FILTER_ALL_ENABLED : ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
            ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setFilter(this.mFilter);
            ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).rebuild();
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
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setSession(getSettingsLifecycle());
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setFilter(this.mFilter);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_unrestricted_data_access;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.unrestricted_data_access_settings;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        AppIconCacheManager.getInstance();
        AppIconCacheManager.release();
    }
}
