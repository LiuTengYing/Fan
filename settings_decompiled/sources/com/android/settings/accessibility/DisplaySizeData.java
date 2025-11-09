package com.android.settings.accessibility;

import android.content.Context;
import com.android.settingslib.display.DisplayDensityConfiguration;
import com.android.settingslib.display.DisplayDensityUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
class DisplaySizeData extends PreviewSizeData<Integer> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplaySizeData(Context context) {
        super(context);
        DisplayDensityUtils displayDensityUtils = new DisplayDensityUtils(getContext());
        int currentIndex = displayDensityUtils.getCurrentIndex();
        if (currentIndex < 0) {
            int i = getContext().getResources().getDisplayMetrics().densityDpi;
            setDefaultValue(Integer.valueOf(i));
            setInitialIndex(0);
            setValues(Collections.singletonList(Integer.valueOf(i)));
            return;
        }
        setDefaultValue(Integer.valueOf(displayDensityUtils.getDefaultDensity()));
        setInitialIndex(currentIndex);
        setValues((List) Arrays.stream(displayDensityUtils.getValues()).boxed().collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void commit(int i) {
        int intValue = getValues().get(i).intValue();
        if (intValue == getDefaultValue().intValue()) {
            DisplayDensityConfiguration.clearForcedDisplayDensity(0);
        } else {
            DisplayDensityConfiguration.setForcedDisplayDensity(0, intValue);
        }
    }
}
