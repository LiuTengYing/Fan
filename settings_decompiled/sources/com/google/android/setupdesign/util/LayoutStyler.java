package com.google.android.setupdesign.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.R$attr;
import com.google.android.setupdesign.R$id;
/* loaded from: classes2.dex */
public final class LayoutStyler {
    @TargetApi(17)
    public static void applyPartnerCustomizationLayoutPaddingStyle(View view) {
        int paddingStart;
        int paddingEnd;
        if (view == null) {
            return;
        }
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_LAYOUT_MARGIN_START;
        boolean isPartnerConfigAvailable = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig);
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_LAYOUT_MARGIN_END;
        boolean isPartnerConfigAvailable2 = partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2);
        if (PartnerStyleHelper.shouldApplyPartnerResource(view)) {
            if (isPartnerConfigAvailable || isPartnerConfigAvailable2) {
                if (isPartnerConfigAvailable) {
                    paddingStart = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
                } else {
                    paddingStart = view.getPaddingStart();
                }
                if (isPartnerConfigAvailable2) {
                    paddingEnd = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2);
                } else {
                    paddingEnd = view.getPaddingEnd();
                }
                if (paddingStart == view.getPaddingStart() && paddingEnd == view.getPaddingEnd()) {
                    return;
                }
                view.setPadding(paddingStart, view.getPaddingTop(), paddingEnd, view.getPaddingBottom());
            }
        }
    }

    @TargetApi(17)
    public static void applyPartnerCustomizationExtraPaddingStyle(View view) {
        int paddingStart;
        int paddingEnd;
        ViewGroup.MarginLayoutParams marginLayoutParams;
        if (view == null) {
            return;
        }
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_LAYOUT_MARGIN_START;
        boolean isPartnerConfigAvailable = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig);
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_LAYOUT_MARGIN_END;
        boolean isPartnerConfigAvailable2 = partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2);
        if (PartnerStyleHelper.shouldApplyPartnerResource(view)) {
            if (isPartnerConfigAvailable || isPartnerConfigAvailable2) {
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sudMarginStart, R$attr.sudMarginEnd});
                int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, 0);
                int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(1, 0);
                obtainStyledAttributes.recycle();
                if (isPartnerConfigAvailable) {
                    paddingStart = ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig)) - dimensionPixelSize;
                } else {
                    paddingStart = view.getPaddingStart();
                }
                if (isPartnerConfigAvailable2) {
                    paddingEnd = ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2)) - dimensionPixelSize2;
                    if (view.getId() == R$id.sud_layout_content) {
                        paddingEnd = ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig)) - dimensionPixelSize2;
                    }
                } else {
                    paddingEnd = view.getPaddingEnd();
                    if (view.getId() == R$id.sud_layout_content) {
                        paddingEnd = view.getPaddingStart();
                    }
                }
                if (paddingStart == view.getPaddingStart() && paddingEnd == view.getPaddingEnd()) {
                    return;
                }
                if (view.getId() == R$id.sud_layout_content) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    } else {
                        marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
                    }
                    marginLayoutParams.setMargins(paddingStart, view.getPaddingTop(), paddingEnd, view.getPaddingBottom());
                    return;
                }
                view.setPadding(paddingStart, view.getPaddingTop(), paddingEnd, view.getPaddingBottom());
            }
        }
    }
}
