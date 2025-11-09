package com.android.settingslib.applications;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
/* loaded from: classes2.dex */
public class InterestingConfigChanges {
    private final int mFlags;
    private final Configuration mLastConfiguration;
    private int mLastDensity;

    public InterestingConfigChanges() {
        this(-2147474940);
    }

    public InterestingConfigChanges(int i) {
        this.mLastConfiguration = new Configuration();
        this.mFlags = i;
    }

    @SuppressLint({"NewApi"})
    public boolean applyNewConfig(Resources resources) {
        Configuration configuration = this.mLastConfiguration;
        int updateFrom = configuration.updateFrom(Configuration.generateDelta(configuration, resources.getConfiguration()));
        if ((this.mLastDensity != resources.getDisplayMetrics().densityDpi) || (updateFrom & this.mFlags) != 0) {
            this.mLastDensity = resources.getDisplayMetrics().densityDpi;
            return true;
        }
        return false;
    }
}
