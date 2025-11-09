package com.android.settings.accessibility;

import android.content.Context;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.List;
/* loaded from: classes.dex */
public interface AccessibilitySearchFeatureProvider {
    List<SearchIndexableRaw> getSearchIndexableRawData(Context context);
}
