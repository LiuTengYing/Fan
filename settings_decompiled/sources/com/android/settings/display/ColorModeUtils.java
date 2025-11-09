package com.android.settings.display;

import android.content.res.Resources;
import android.util.ArrayMap;
import com.android.settings.R$array;
import java.util.Map;
/* loaded from: classes.dex */
final class ColorModeUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<Integer, String> getColorModeMapping(Resources resources) {
        String[] stringArray = resources.getStringArray(R$array.config_color_mode_options_strings);
        int[] intArray = resources.getIntArray(R$array.config_color_mode_options_values);
        if (stringArray.length != intArray.length) {
            throw new RuntimeException("Color mode options of unequal length");
        }
        ArrayMap arrayMap = new ArrayMap();
        for (int i = 0; i < intArray.length; i++) {
            int i2 = intArray[i];
            if (i2 == 0 || i2 == 1 || i2 == 2 || i2 == 3 || (i2 >= 256 && i2 <= 511)) {
                arrayMap.put(Integer.valueOf(i2), stringArray[i]);
            }
        }
        return arrayMap;
    }
}
