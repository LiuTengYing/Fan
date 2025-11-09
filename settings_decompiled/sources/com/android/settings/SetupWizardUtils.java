package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sysprop.SetupWizardProperties;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.Arrays;
/* loaded from: classes.dex */
public class SetupWizardUtils {
    public static String getThemeString(Intent intent) {
        String stringExtra = intent.getStringExtra("theme");
        return stringExtra == null ? (String) SetupWizardProperties.theme().orElse("") : stringExtra;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getTheme(Context context, Intent intent) {
        char c;
        char c2;
        char c3;
        String themeString = getThemeString(intent);
        if (themeString != null) {
            if (WizardManagerHelper.isAnySetupWizard(intent)) {
                if (ThemeHelper.isSetupWizardDayNightEnabled(context)) {
                    switch (themeString.hashCode()) {
                        case -2128555920:
                            if (themeString.equals("glif_v2_light")) {
                                c3 = 0;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case -1241052239:
                            if (themeString.equals("glif_v3_light")) {
                                c3 = 1;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case -353548558:
                            if (themeString.equals("glif_v4_light")) {
                                c3 = 2;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 3175618:
                            if (themeString.equals("glif")) {
                                c3 = 3;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 115650329:
                            if (themeString.equals("glif_v2")) {
                                c3 = 4;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 115650330:
                            if (themeString.equals("glif_v3")) {
                                c3 = 5;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 115650331:
                            if (themeString.equals("glif_v4")) {
                                c3 = 6;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 767685465:
                            if (themeString.equals("glif_light")) {
                                c3 = 7;
                                break;
                            }
                            c3 = 65535;
                            break;
                        default:
                            c3 = 65535;
                            break;
                    }
                    switch (c3) {
                        case 0:
                        case 4:
                            return R$style.GlifV2Theme_DayNight;
                        case 1:
                        case 5:
                            return R$style.GlifV3Theme_DayNight;
                        case 2:
                        case 6:
                            return R$style.GlifV4Theme_DayNight;
                        case 3:
                        case 7:
                            return R$style.GlifTheme_DayNight;
                    }
                }
                switch (themeString.hashCode()) {
                    case -2128555920:
                        if (themeString.equals("glif_v2_light")) {
                            c2 = 0;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case -1241052239:
                        if (themeString.equals("glif_v3_light")) {
                            c2 = 1;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case -353548558:
                        if (themeString.equals("glif_v4_light")) {
                            c2 = 2;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 3175618:
                        if (themeString.equals("glif")) {
                            c2 = 3;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 115650329:
                        if (themeString.equals("glif_v2")) {
                            c2 = 4;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 115650330:
                        if (themeString.equals("glif_v3")) {
                            c2 = 5;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 115650331:
                        if (themeString.equals("glif_v4")) {
                            c2 = 6;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 767685465:
                        if (themeString.equals("glif_light")) {
                            c2 = 7;
                            break;
                        }
                        c2 = 65535;
                        break;
                    default:
                        c2 = 65535;
                        break;
                }
                switch (c2) {
                    case 0:
                        return R$style.GlifV2Theme_Light;
                    case 1:
                        return R$style.GlifV3Theme_Light;
                    case 2:
                        return R$style.GlifV4Theme_Light;
                    case 3:
                        return R$style.GlifTheme;
                    case 4:
                        return R$style.GlifV2Theme;
                    case 5:
                        return R$style.GlifV3Theme;
                    case 6:
                        return R$style.GlifV4Theme;
                    case 7:
                        return R$style.GlifTheme_Light;
                }
            }
            switch (themeString.hashCode()) {
                case -2128555920:
                    if (themeString.equals("glif_v2_light")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1241052239:
                    if (themeString.equals("glif_v3_light")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -353548558:
                    if (themeString.equals("glif_v4_light")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 3175618:
                    if (themeString.equals("glif")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650329:
                    if (themeString.equals("glif_v2")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650330:
                    if (themeString.equals("glif_v3")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 115650331:
                    if (themeString.equals("glif_v4")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 767685465:
                    if (themeString.equals("glif_light")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 4:
                    return R$style.GlifV2Theme;
                case 1:
                case 5:
                    return R$style.GlifV3Theme;
                case 2:
                case 6:
                    return R$style.GlifV4Theme;
                case 3:
                case 7:
                    return R$style.GlifTheme;
            }
        }
        return R$style.GlifTheme;
    }

    public static int getTransparentTheme(Context context, Intent intent) {
        int i;
        int theme = getTheme(context, intent);
        if (ThemeHelper.isSetupWizardDayNightEnabled(context)) {
            i = R$style.GlifV2Theme_DayNight_Transparent;
        } else {
            i = R$style.GlifV2Theme_Light_Transparent;
        }
        if (theme == R$style.GlifV3Theme_DayNight) {
            return R$style.GlifV3Theme_DayNight_Transparent;
        }
        if (theme == R$style.GlifV3Theme_Light) {
            return R$style.GlifV3Theme_Light_Transparent;
        }
        if (theme == R$style.GlifV2Theme_DayNight) {
            return R$style.GlifV2Theme_DayNight_Transparent;
        }
        if (theme == R$style.GlifV2Theme_Light) {
            return R$style.GlifV2Theme_Light_Transparent;
        }
        if (theme == R$style.GlifTheme_DayNight) {
            return R$style.SetupWizardTheme_DayNight_Transparent;
        }
        if (theme == R$style.GlifTheme_Light) {
            return R$style.SetupWizardTheme_Light_Transparent;
        }
        if (theme == R$style.GlifV3Theme) {
            return R$style.GlifV3Theme_Transparent;
        }
        if (theme == R$style.GlifV2Theme) {
            return R$style.GlifV2Theme_Transparent;
        }
        return theme == R$style.GlifTheme ? R$style.SetupWizardTheme_Transparent : i;
    }

    public static void copySetupExtras(Intent intent, Intent intent2) {
        WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
    }

    public static Bundle copyLifecycleExtra(Bundle bundle, Bundle bundle2) {
        for (String str : Arrays.asList("firstRun", "isSetupFlow")) {
            bundle2.putBoolean(str, bundle.getBoolean(str, false));
        }
        return bundle2;
    }
}
