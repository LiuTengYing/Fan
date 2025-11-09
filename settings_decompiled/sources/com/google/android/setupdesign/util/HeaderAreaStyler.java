package com.google.android.setupdesign.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.util.BuildCompatUtils;
import com.google.android.setupdesign.R$dimen;
import com.google.android.setupdesign.util.TextViewPartnerStyler;
/* loaded from: classes2.dex */
public final class HeaderAreaStyler {
    static final String WARN_TO_USE_DRAWABLE = "To achieve scaling icon in SetupDesign lib, should use vector drawable icon from ";

    public static void applyPartnerCustomizationHeaderStyle(TextView textView) {
        if (textView == null) {
            return;
        }
        TextViewPartnerStyler.applyPartnerCustomizationStyle(textView, new TextViewPartnerStyler.TextPartnerConfigs(PartnerConfig.CONFIG_HEADER_TEXT_COLOR, null, PartnerConfig.CONFIG_HEADER_TEXT_SIZE, PartnerConfig.CONFIG_HEADER_FONT_FAMILY, null, PartnerConfig.CONFIG_HEADER_TEXT_MARGIN_TOP, PartnerConfig.CONFIG_HEADER_TEXT_MARGIN_BOTTOM, PartnerStyleHelper.getLayoutGravity(textView.getContext())));
    }

    public static void applyPartnerCustomizationDescriptionHeavyStyle(TextView textView) {
        if (textView == null) {
            return;
        }
        TextViewPartnerStyler.applyPartnerCustomizationStyle(textView, new TextViewPartnerStyler.TextPartnerConfigs(PartnerConfig.CONFIG_DESCRIPTION_TEXT_COLOR, PartnerConfig.CONFIG_DESCRIPTION_LINK_TEXT_COLOR, PartnerConfig.CONFIG_DESCRIPTION_TEXT_SIZE, PartnerConfig.CONFIG_DESCRIPTION_FONT_FAMILY, PartnerConfig.CONFIG_DESCRIPTION_LINK_FONT_FAMILY, PartnerConfig.CONFIG_DESCRIPTION_TEXT_MARGIN_TOP, PartnerConfig.CONFIG_DESCRIPTION_TEXT_MARGIN_BOTTOM, PartnerStyleHelper.getLayoutGravity(textView.getContext())));
    }

    public static void applyPartnerCustomizationHeaderAreaStyle(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        Context context = viewGroup.getContext();
        viewGroup.setBackgroundColor(PartnerConfigHelper.get(context).getColor(context, PartnerConfig.CONFIG_HEADER_AREA_BACKGROUND_COLOR));
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_HEADER_CONTAINER_MARGIN_BOTTOM;
        if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
            ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig));
                viewGroup.setLayoutParams(layoutParams);
            }
        }
    }

    public static void applyPartnerCustomizationProgressBarStyle(ProgressBar progressBar) {
        if (progressBar == null) {
            return;
        }
        Context context = progressBar.getContext();
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            int i = marginLayoutParams.topMargin;
            PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig = PartnerConfig.CONFIG_PROGRESS_BAR_MARGIN_TOP;
            if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
                i = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig, context.getResources().getDimension(R$dimen.sud_progress_bar_margin_top));
            }
            int i2 = marginLayoutParams.bottomMargin;
            PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_PROGRESS_BAR_MARGIN_BOTTOM;
            if (partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2)) {
                i2 = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2, context.getResources().getDimension(R$dimen.sud_progress_bar_margin_bottom));
            }
            if (i == marginLayoutParams.topMargin && i2 == marginLayoutParams.bottomMargin) {
                return;
            }
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, i, marginLayoutParams.rightMargin, i2);
        }
    }

    public static void applyPartnerCustomizationIconStyle(ImageView imageView, FrameLayout frameLayout) {
        int dimension;
        int i;
        if (imageView == null || frameLayout == null) {
            return;
        }
        Context context = imageView.getContext();
        int i2 = 0;
        int layoutGravity = PartnerStyleHelper.getLayoutGravity(context);
        if (layoutGravity != 0) {
            setGravity(imageView, layoutGravity);
        }
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_ICON_SIZE;
        if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
            checkImageType(imageView);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
            layoutParams.width = -2;
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight() * 2 && (i = layoutParams.height) > (dimension = (int) context.getResources().getDimension(R$dimen.sud_horizontal_icon_height))) {
                i2 = i - dimension;
                layoutParams.height = dimension;
            }
        }
        ViewGroup.LayoutParams layoutParams2 = frameLayout.getLayoutParams();
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_ICON_MARGIN_TOP;
        if (partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2) && (layoutParams2 instanceof ViewGroup.MarginLayoutParams)) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams2;
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2)) + i2, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        }
    }

    private static void checkImageType(final ImageView imageView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.google.android.setupdesign.util.HeaderAreaStyler.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (!BuildCompatUtils.isAtLeastS() || imageView.getDrawable() == null || (imageView.getDrawable() instanceof VectorDrawable) || (imageView.getDrawable() instanceof VectorDrawableCompat)) {
                    return true;
                }
                String str = Build.TYPE;
                if (str.equals("userdebug") || str.equals("eng")) {
                    Log.w("HeaderAreaStyler", HeaderAreaStyler.WARN_TO_USE_DRAWABLE + imageView.getContext().getPackageName());
                    return true;
                }
                return true;
            }
        });
    }

    private static void setGravity(ImageView imageView, int i) {
        if (imageView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.gravity = i;
            imageView.setLayoutParams(layoutParams);
        }
    }
}
