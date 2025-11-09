package com.google.android.setupdesign.util;

import android.app.Activity;
import android.content.Context;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.util.BuildCompatUtils;
import com.google.android.setupcompat.util.Logger;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.R$color;
import com.google.android.setupdesign.R$style;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ThemeHelper {
    private static final Logger LOG = new Logger("ThemeHelper");

    public static boolean isSetupWizardDayNightEnabled(Context context) {
        return PartnerConfigHelper.isSetupWizardDayNightEnabled(context);
    }

    public static boolean shouldApplyMaterialYouStyle(Context context) {
        return PartnerConfigHelper.shouldApplyMaterialYouStyle(context);
    }

    public static boolean shouldApplyDynamicColor(Context context) {
        return PartnerConfigHelper.isSetupWizardDynamicColorEnabled(context);
    }

    public static int getDynamicColorTheme(Context context) {
        int i;
        try {
            boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(PartnerCustomizationLayout.lookupActivityFromContext(context).getIntent());
            boolean isSetupWizardDayNightEnabled = isSetupWizardDayNightEnabled(context);
            if (!isAnySetupWizard) {
                if (isSetupWizardDayNightEnabled) {
                    i = R$style.SudFullDynamicColorTheme_DayNight;
                } else {
                    i = R$style.SudFullDynamicColorTheme_Light;
                }
                Logger logger = LOG;
                StringBuilder sb = new StringBuilder();
                sb.append("Return ");
                sb.append(isSetupWizardDayNightEnabled ? "SudFullDynamicColorTheme_DayNight" : "SudFullDynamicColorTheme_Light");
                logger.atInfo(sb.toString());
            } else if (isSetupWizardDayNightEnabled) {
                i = R$style.SudDynamicColorTheme_DayNight;
            } else {
                i = R$style.SudDynamicColorTheme_Light;
            }
            Logger logger2 = LOG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Gets the dynamic accentColor: [Light] ");
            sb2.append(colorIntToHex(context, R$color.sud_dynamic_color_accent_glif_v3_light));
            sb2.append(", ");
            sb2.append(BuildCompatUtils.isAtLeastS() ? colorIntToHex(context, 17170495) : "n/a");
            sb2.append(", [Dark] ");
            sb2.append(colorIntToHex(context, R$color.sud_dynamic_color_accent_glif_v3_dark));
            sb2.append(", ");
            sb2.append(BuildCompatUtils.isAtLeastS() ? colorIntToHex(context, 17170490) : "n/a");
            logger2.atDebug(sb2.toString());
            return i;
        } catch (IllegalArgumentException e) {
            Logger logger3 = LOG;
            String message = e.getMessage();
            Objects.requireNonNull(message);
            logger3.e(message);
            return 0;
        }
    }

    public static boolean trySetDynamicColor(Context context) {
        if (!BuildCompatUtils.isAtLeastS()) {
            LOG.w("Dynamic color require platform version at least S.");
            return false;
        } else if (!shouldApplyDynamicColor(context)) {
            LOG.w("SetupWizard does not support the dynamic color or supporting status unknown.");
            return false;
        } else {
            try {
                Activity lookupActivityFromContext = PartnerCustomizationLayout.lookupActivityFromContext(context);
                int dynamicColorTheme = getDynamicColorTheme(context);
                if (dynamicColorTheme != 0) {
                    lookupActivityFromContext.setTheme(dynamicColorTheme);
                    return true;
                }
                LOG.w("Error occurred on getting dynamic color theme.");
                return false;
            } catch (IllegalArgumentException e) {
                Logger logger = LOG;
                String message = e.getMessage();
                Objects.requireNonNull(message);
                logger.e(message);
                return false;
            }
        }
    }

    private static String colorIntToHex(Context context, int i) {
        return String.format("#%06X", Integer.valueOf(context.getResources().getColor(i) & 16777215));
    }
}
