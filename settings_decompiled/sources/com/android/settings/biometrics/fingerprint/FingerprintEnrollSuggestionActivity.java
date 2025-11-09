package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class FingerprintEnrollSuggestionActivity extends FingerprintEnrollIntroduction {
    public static boolean isSuggestionComplete(Context context) {
        if (Utils.hasFingerprintHardware(context) && FingerprintSuggestionActivity.isFingerprintEnabled(context) && Utils.hasFingerprintHardware(context)) {
            return Utils.getFingerprintManagerOrNull(context).hasEnrolledFingerprints();
        }
        return true;
    }
}
