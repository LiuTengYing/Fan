package com.google.android.setupdesign.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.view.RichTextView;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class TextViewPartnerStyler {
    public static void applyPartnerCustomizationStyle(TextView textView, TextPartnerConfigs textPartnerConfigs) {
        Typeface create;
        Typeface create2;
        int color;
        int color2;
        if (textView == null || textPartnerConfigs == null) {
            return;
        }
        Context context = textView.getContext();
        if (textPartnerConfigs.getTextColorConfig() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextColorConfig()) && (color2 = PartnerConfigHelper.get(context).getColor(context, textPartnerConfigs.getTextColorConfig())) != 0) {
            textView.setTextColor(color2);
        }
        if (textPartnerConfigs.getTextLinkedColorConfig() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextLinkedColorConfig()) && !PartnerStyleHelper.useDynamicColor(textView) && (color = PartnerConfigHelper.get(context).getColor(context, textPartnerConfigs.getTextLinkedColorConfig())) != 0) {
            textView.setLinkTextColor(color);
        }
        if (textPartnerConfigs.getTextSizeConfig() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextSizeConfig())) {
            float dimension = PartnerConfigHelper.get(context).getDimension(context, textPartnerConfigs.getTextSizeConfig(), 0.0f);
            if (dimension > 0.0f) {
                textView.setTextSize(0, dimension);
            }
        }
        if (textPartnerConfigs.getTextFontFamilyConfig() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextFontFamilyConfig()) && (create2 = Typeface.create(PartnerConfigHelper.get(context).getString(context, textPartnerConfigs.getTextFontFamilyConfig()), 0)) != null) {
            textView.setTypeface(create2);
        }
        if ((textView instanceof RichTextView) && textPartnerConfigs.getLinkTextFontFamilyConfig() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getLinkTextFontFamilyConfig()) && (create = Typeface.create(PartnerConfigHelper.get(context).getString(context, textPartnerConfigs.getLinkTextFontFamilyConfig()), 0)) != null) {
            ((RichTextView) textView).setSpanTypeface(create);
        }
        applyPartnerCustomizationVerticalMargins(textView, textPartnerConfigs);
        textView.setGravity(textPartnerConfigs.getTextGravity());
    }

    public static void applyPartnerCustomizationLightStyle(TextView textView, TextPartnerConfigs textPartnerConfigs) {
        if (textView == null || textPartnerConfigs == null) {
            return;
        }
        applyPartnerCustomizationVerticalMargins(textView, textPartnerConfigs);
        textView.setGravity(textPartnerConfigs.getTextGravity());
    }

    private static void applyPartnerCustomizationVerticalMargins(TextView textView, TextPartnerConfigs textPartnerConfigs) {
        int i;
        int i2;
        if (textPartnerConfigs.getTextMarginTop() == null && textPartnerConfigs.getTextMarginBottom() == null) {
            return;
        }
        Context context = textView.getContext();
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) layoutParams;
            if (textPartnerConfigs.getTextMarginTop() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextMarginTop())) {
                i = (int) PartnerConfigHelper.get(context).getDimension(context, textPartnerConfigs.getTextMarginTop());
            } else {
                i = layoutParams2.topMargin;
            }
            if (textPartnerConfigs.getTextMarginBottom() != null && PartnerConfigHelper.get(context).isPartnerConfigAvailable(textPartnerConfigs.getTextMarginBottom())) {
                i2 = (int) PartnerConfigHelper.get(context).getDimension(context, textPartnerConfigs.getTextMarginBottom());
            } else {
                i2 = layoutParams2.bottomMargin;
            }
            layoutParams2.setMargins(layoutParams2.leftMargin, i, layoutParams2.rightMargin, i2);
            textView.setLayoutParams(layoutParams);
        }
    }

    /* loaded from: classes2.dex */
    public static class TextPartnerConfigs {
        private final PartnerConfig textColorConfig;
        private final PartnerConfig textFontFamilyConfig;
        private final int textGravity;
        private final PartnerConfig textLinkFontFamilyConfig;
        private final PartnerConfig textLinkedColorConfig;
        private final PartnerConfig textMarginBottomConfig;
        private final PartnerConfig textMarginTopConfig;
        private final PartnerConfig textSizeConfig;

        public TextPartnerConfigs(PartnerConfig partnerConfig, PartnerConfig partnerConfig2, PartnerConfig partnerConfig3, PartnerConfig partnerConfig4, PartnerConfig partnerConfig5, PartnerConfig partnerConfig6, PartnerConfig partnerConfig7, int i) {
            this.textColorConfig = partnerConfig;
            this.textLinkedColorConfig = partnerConfig2;
            this.textSizeConfig = partnerConfig3;
            this.textFontFamilyConfig = partnerConfig4;
            this.textLinkFontFamilyConfig = partnerConfig5;
            this.textMarginTopConfig = partnerConfig6;
            this.textMarginBottomConfig = partnerConfig7;
            this.textGravity = i;
        }

        public PartnerConfig getTextColorConfig() {
            return this.textColorConfig;
        }

        public PartnerConfig getTextLinkedColorConfig() {
            return this.textLinkedColorConfig;
        }

        public PartnerConfig getTextSizeConfig() {
            return this.textSizeConfig;
        }

        public PartnerConfig getTextFontFamilyConfig() {
            return this.textFontFamilyConfig;
        }

        public PartnerConfig getLinkTextFontFamilyConfig() {
            return this.textLinkFontFamilyConfig;
        }

        public PartnerConfig getTextMarginTop() {
            return this.textMarginTopConfig;
        }

        public PartnerConfig getTextMarginBottom() {
            return this.textMarginBottomConfig;
        }

        public int getTextGravity() {
            return this.textGravity;
        }
    }
}
