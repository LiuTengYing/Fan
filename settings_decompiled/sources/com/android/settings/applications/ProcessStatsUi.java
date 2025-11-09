package com.android.settings.applications;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.ProcStatsData;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class ProcessStatsUi extends ProcessStatsBase {
    private PreferenceGroup mAppListGroup;
    private MenuItem mMenuAvg;
    private MenuItem mMenuMax;
    private PackageManager mPm;
    private boolean mShowMax;
    public static final int[] BACKGROUND_AND_SYSTEM_PROC_STATES = {0, 4, 5, 6, 10, 7, 8, 9, 11};
    public static final int[] FOREGROUND_PROC_STATES = {1};
    public static final int[] CACHED_PROC_STATES = {13, 14, 15};
    static final Comparator<ProcStatsPackageEntry> sPackageEntryCompare = new Comparator<ProcStatsPackageEntry>() { // from class: com.android.settings.applications.ProcessStatsUi.1
        @Override // java.util.Comparator
        public int compare(ProcStatsPackageEntry procStatsPackageEntry, ProcStatsPackageEntry procStatsPackageEntry2) {
            double max = Math.max(procStatsPackageEntry2.mRunWeight, procStatsPackageEntry2.mBgWeight);
            double max2 = Math.max(procStatsPackageEntry.mRunWeight, procStatsPackageEntry.mBgWeight);
            if (max2 == max) {
                return 0;
            }
            return max2 < max ? 1 : -1;
        }
    };
    static final Comparator<ProcStatsPackageEntry> sMaxPackageEntryCompare = new Comparator<ProcStatsPackageEntry>() { // from class: com.android.settings.applications.ProcessStatsUi.2
        @Override // java.util.Comparator
        public int compare(ProcStatsPackageEntry procStatsPackageEntry, ProcStatsPackageEntry procStatsPackageEntry2) {
            double max = Math.max(procStatsPackageEntry2.mMaxBgMem, procStatsPackageEntry2.mMaxRunMem);
            double max2 = Math.max(procStatsPackageEntry.mMaxBgMem, procStatsPackageEntry.mMaxRunMem);
            if (max2 == max) {
                return 0;
            }
            return max2 < max ? 1 : -1;
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 23;
    }

    @Override // com.android.settings.applications.ProcessStatsBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPm = getActivity().getPackageManager();
        addPreferencesFromResource(R$xml.process_stats_ui);
        this.mAppListGroup = (PreferenceGroup) findPreference("app_list");
        setHasOptionsMenu(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mMenuAvg = menu.add(0, 1, 0, R$string.sort_avg_use);
        this.mMenuMax = menu.add(0, 2, 0, R$string.sort_max_use);
        updateMenu();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1 || itemId == 2) {
            this.mShowMax = !this.mShowMax;
            refreshUi();
            updateMenu();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updateMenu() {
        this.mMenuMax.setVisible(!this.mShowMax);
        this.mMenuAvg.setVisible(this.mShowMax);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_process_stats_apps;
    }

    @Override // com.android.settings.applications.ProcessStatsBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof ProcessStatsPreference) {
            ProcessStatsBase.launchMemoryDetail((SettingsActivity) getActivity(), this.mStatsManager.getMemInfo(), ((ProcessStatsPreference) preference).getEntry(), true);
            return super.onPreferenceTreeClick(preference);
        }
        return false;
    }

    @Override // com.android.settings.applications.ProcessStatsBase
    public void refreshUi() {
        this.mAppListGroup.removeAll();
        int i = 0;
        this.mAppListGroup.setOrderingAsAdded(false);
        this.mAppListGroup.setTitle(this.mShowMax ? R$string.maximum_memory_use : R$string.average_memory_use);
        FragmentActivity activity = getActivity();
        ProcStatsData.MemInfo memInfo = this.mStatsManager.getMemInfo();
        List<ProcStatsPackageEntry> entries = this.mStatsManager.getEntries();
        int size = entries.size();
        for (int i2 = 0; i2 < size; i2++) {
            entries.get(i2).updateMetrics();
        }
        Collections.sort(entries, this.mShowMax ? sMaxPackageEntryCompare : sPackageEntryCompare);
        double d = this.mShowMax ? memInfo.realTotalRam : memInfo.usedWeight * memInfo.weightToRam;
        while (i < entries.size()) {
            ProcStatsPackageEntry procStatsPackageEntry = entries.get(i);
            ProcessStatsPreference processStatsPreference = new ProcessStatsPreference(getPrefContext());
            procStatsPackageEntry.retrieveUiData(activity, this.mPm);
            processStatsPreference.init(procStatsPackageEntry, this.mPm, d, memInfo.weightToRam, memInfo.totalScale, !this.mShowMax);
            processStatsPreference.setOrder(i);
            this.mAppListGroup.addPreference(processStatsPreference);
            i++;
            activity = activity;
        }
    }
}
