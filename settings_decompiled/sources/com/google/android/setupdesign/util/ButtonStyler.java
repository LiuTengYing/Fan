package com.google.android.setupdesign.util;

import android.content.Context;
import android.widget.Button;
import com.google.android.setupcompat.template.FooterButtonStyleUtils;
/* loaded from: classes2.dex */
public class ButtonStyler {
    public static void applyPartnerCustomizationPrimaryButtonStyle(Context context, Button button) {
        if (button == null || context == null) {
            return;
        }
        FooterButtonStyleUtils.applyPrimaryButtonPartnerResource(context, button, ThemeHelper.shouldApplyDynamicColor(context));
    }
}
