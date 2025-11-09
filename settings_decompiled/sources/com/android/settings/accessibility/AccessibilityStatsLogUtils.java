package com.android.settings.accessibility;

import android.content.ComponentName;
import com.android.settings.core.instrumentation.SettingsStatsLog;
/* loaded from: classes.dex */
public final class AccessibilityStatsLogUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int convertToEntryPoint(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    i2 = 4;
                    if (i != 4) {
                        return 0;
                    }
                }
            }
        }
        return i2;
    }

    private static int convertToLoggingServiceEnabled(boolean z) {
        return z ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logAccessibilityServiceEnabled(ComponentName componentName, boolean z) {
        SettingsStatsLog.write(267, componentName.flattenToString(), convertToLoggingServiceEnabled(z));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logDisableNonA11yCategoryService(String str, long j) {
        com.android.internal.accessibility.util.AccessibilityStatsLogUtils.logNonA11yToolServiceWarningReported(str, com.android.internal.accessibility.util.AccessibilityStatsLogUtils.ACCESSIBILITY_PRIVACY_WARNING_STATUS_SERVICE_DISABLED, j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int convertToItemKeyName(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1539906063:
                if (str.equals("font_size")) {
                    c = 0;
                    break;
                }
                break;
            case -721243016:
                if (str.equals("toggle_high_text_contrast_preference")) {
                    c = 1;
                    break;
                }
                break;
            case -227503256:
                if (str.equals("toggle_force_bold_text")) {
                    c = 2;
                    break;
                }
                break;
            case 108404047:
                if (str.equals("reset")) {
                    c = 3;
                    break;
                }
                break;
            case 1615243614:
                if (str.equals("display_size")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 1;
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
                return 5;
            case 4:
                return 2;
            default:
                return 0;
        }
    }
}
