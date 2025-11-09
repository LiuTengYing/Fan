package com.android.settings.core.instrumentation;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class SettingsMetricsFeatureProvider extends MetricsFeatureProvider {
    @Override // com.android.settingslib.core.instrumentation.MetricsFeatureProvider
    protected void installLogWriters() {
        this.mLoggerWriters.add(new StatsLogWriter());
        this.mLoggerWriters.add(new SettingsEventLogWriter());
        this.mLoggerWriters.add(new SettingsIntelligenceLogWriter());
    }

    @Override // com.android.settingslib.core.instrumentation.MetricsFeatureProvider
    @Deprecated
    public void action(Context context, int i, Pair<Integer, Object>... pairArr) {
        Log.w("SettingsMetricsFeature", "action(Pair<Integer, Object>... taggedData) is deprecated, Use action(int, int, int, String, int) instead.");
        super.action(context, i, pairArr);
    }
}
