package com.android.settings.applications;

import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SummaryPreference;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class ProcessStatsSummary extends ProcessStatsBase implements Preference.OnPreferenceClickListener {
    private Preference mAppListPreference;
    private Preference mAverageUsed;
    private Preference mFree;
    private Preference mPerformance;
    private SummaryPreference mSummaryPref;
    private Preference mTotalMemory;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 202;
    }

    @Override // com.android.settings.applications.ProcessStatsBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.process_stats_summary);
        this.mSummaryPref = (SummaryPreference) findPreference("status_header");
        this.mPerformance = findPreference("performance");
        this.mTotalMemory = findPreference("total_memory");
        this.mAverageUsed = findPreference("average_used");
        this.mFree = findPreference("free");
        Preference findPreference = findPreference("apps_list");
        this.mAppListPreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.applications.ProcessStatsBase
    public void refreshUi() {
        CharSequence charSequence;
        Context context = getContext();
        ProcStatsData.MemInfo memInfo = this.mStatsManager.getMemInfo();
        double d = memInfo.realUsedRam;
        double d2 = memInfo.realTotalRam;
        double d3 = memInfo.realFreeRam;
        long j = (long) d;
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 1);
        long j2 = (long) d2;
        String formatShortFileSize = Formatter.formatShortFileSize(context, j2);
        String formatShortFileSize2 = Formatter.formatShortFileSize(context, (long) d3);
        CharSequence[] textArray = getResources().getTextArray(R$array.ram_states);
        int memState = this.mStatsManager.getMemState();
        if (memState >= 0 && memState < textArray.length - 1) {
            charSequence = textArray[memState];
        } else {
            charSequence = textArray[textArray.length - 1];
        }
        this.mSummaryPref.setAmount(formatBytes.value);
        this.mSummaryPref.setUnits(formatBytes.units);
        float f = (float) (d / (d3 + d));
        this.mSummaryPref.setRatios(f, 0.0f, 1.0f - f);
        this.mPerformance.setSummary(charSequence);
        this.mTotalMemory.setSummary(formatShortFileSize);
        this.mAverageUsed.setSummary(Utils.formatPercentage(j, j2));
        this.mFree.setSummary(formatShortFileSize2);
        String string = getString(ProcessStatsBase.sDurationLabels[this.mDurationIndex]);
        int size = this.mStatsManager.getEntries().size();
        this.mAppListPreference.setSummary(getResources().getQuantityString(R$plurals.memory_usage_apps_summary, size, Integer.valueOf(size), string));
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_process_stats_summary;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mAppListPreference) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("transfer_stats", true);
            bundle.putInt("duration_index", this.mDurationIndex);
            this.mStatsManager.xferStats();
            new SubSettingLauncher(getContext()).setDestination(ProcessStatsUi.class.getName()).setTitleRes(R$string.memory_usage_apps).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
            return true;
        }
        return false;
    }
}
