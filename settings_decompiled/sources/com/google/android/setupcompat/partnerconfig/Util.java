package com.google.android.setupcompat.partnerconfig;

import android.content.res.Configuration;
/* loaded from: classes2.dex */
public final class Util {
    public static boolean isNightMode(Configuration configuration) {
        return (configuration.uiMode & 48) == 32;
    }
}
