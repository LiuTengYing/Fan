package com.android.settings.development;

import android.content.Context;
import android.text.format.Formatter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.applications.ProcessStatsBase;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class MemoryUsagePreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private ProcStatsData mProcStatsData;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "memory";
    }

    public MemoryUsagePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mProcStatsData = getProcStatsData();
        setDuration();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.development.MemoryUsagePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MemoryUsagePreferenceController.this.lambda$updateState$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1() {
        this.mProcStatsData.refreshStats(true);
        ProcStatsData.MemInfo memInfo = this.mProcStatsData.getMemInfo();
        final String formatShortFileSize = Formatter.formatShortFileSize(this.mContext, (long) memInfo.realUsedRam);
        final String formatShortFileSize2 = Formatter.formatShortFileSize(this.mContext, (long) memInfo.realTotalRam);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.development.MemoryUsagePreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MemoryUsagePreferenceController.this.lambda$updateState$0(formatShortFileSize, formatShortFileSize2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(String str, String str2) {
        this.mPreference.setSummary(this.mContext.getString(R$string.memory_summary, str, str2));
    }

    void setDuration() {
        this.mProcStatsData.setDuration(ProcessStatsBase.sDurations[0]);
    }

    ProcStatsData getProcStatsData() {
        return new ProcStatsData(this.mContext, false);
    }
}
