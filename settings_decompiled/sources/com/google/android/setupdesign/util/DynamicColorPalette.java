package com.google.android.setupdesign.util;

import android.content.Context;
import com.google.android.setupdesign.R$color;
/* loaded from: classes2.dex */
public final class DynamicColorPalette {
    public static int getColor(Context context, int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = R$color.sud_dynamic_color_accent_glif_v3;
                break;
            case 1:
                i2 = R$color.sud_system_primary_text;
                break;
            case 2:
                i2 = R$color.sud_system_secondary_text;
                break;
            case 3:
                i2 = R$color.sud_system_tertiary_text_inactive;
                break;
            case 4:
                i2 = R$color.sud_system_error_warning;
                break;
            case 5:
                i2 = R$color.sud_system_success_done;
                break;
            case 6:
                i2 = R$color.sud_system_fallback_accent;
                break;
            case 7:
                i2 = R$color.sud_system_background_surface;
                break;
            case 8:
                i2 = R$color.sud_system_surface;
                break;
            default:
                i2 = 0;
                break;
        }
        return context.getResources().getColor(i2);
    }
}
