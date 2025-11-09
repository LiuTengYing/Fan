package com.android.settingslib.display;

import android.util.MathUtils;
/* loaded from: classes2.dex */
public class BrightnessUtils {
    public static final int convertLinearToGammaFloat(float f, float f2, float f3) {
        float log;
        float norm = MathUtils.norm(f2, f3, f) * 12.0f;
        if (norm <= 1.0f) {
            log = MathUtils.sqrt(norm) * 0.5f;
        } else {
            log = (MathUtils.log(norm - 0.28466892f) * 0.17883277f) + 0.5599107f;
        }
        return Math.round(MathUtils.lerp(0, 65535, log));
    }
}
