package com.google.android.setupdesign.template;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.util.HeaderAreaStyler;
import com.google.android.setupdesign.util.LayoutStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;
/* loaded from: classes2.dex */
public class HeaderMixin implements Mixin {
    boolean autoTextSizeEnabled = false;
    private float headerAutoSizeLineExtraSpacingInPx;
    private int headerAutoSizeMaxLineOfMaxSize;
    private float headerAutoSizeMaxTextSizeInPx;
    private float headerAutoSizeMinTextSizeInPx;
    private final TemplateLayout templateLayout;

    public HeaderMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        this.templateLayout = templateLayout;
        TypedArray obtainStyledAttributes = templateLayout.getContext().obtainStyledAttributes(attributeSet, R$styleable.SucHeaderMixin, i, 0);
        CharSequence text = obtainStyledAttributes.getText(R$styleable.SucHeaderMixin_sucHeaderText);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.SucHeaderMixin_sucHeaderTextColor);
        obtainStyledAttributes.recycle();
        tryUpdateAutoTextSizeFlagWithPartnerConfig();
        if (text != null) {
            setText(text);
        }
        if (colorStateList != null) {
            setTextColor(colorStateList);
        }
    }

    private void tryUpdateAutoTextSizeFlagWithPartnerConfig() {
        Context context = this.templateLayout.getContext();
        if (!PartnerStyleHelper.shouldApplyPartnerResource(this.templateLayout)) {
            this.autoTextSizeEnabled = false;
            return;
        }
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_HEADER_AUTO_SIZE_ENABLED;
        if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
            this.autoTextSizeEnabled = PartnerConfigHelper.get(context).getBoolean(context, partnerConfig, this.autoTextSizeEnabled);
        }
        if (this.autoTextSizeEnabled) {
            PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_HEADER_AUTO_SIZE_MAX_TEXT_SIZE;
            if (partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2)) {
                this.headerAutoSizeMaxTextSizeInPx = PartnerConfigHelper.get(context).getDimension(context, partnerConfig2);
            }
            PartnerConfigHelper partnerConfigHelper3 = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig3 = PartnerConfig.CONFIG_HEADER_AUTO_SIZE_MIN_TEXT_SIZE;
            if (partnerConfigHelper3.isPartnerConfigAvailable(partnerConfig3)) {
                this.headerAutoSizeMinTextSizeInPx = PartnerConfigHelper.get(context).getDimension(context, partnerConfig3);
            }
            PartnerConfigHelper partnerConfigHelper4 = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig4 = PartnerConfig.CONFIG_HEADER_AUTO_SIZE_LINE_SPACING_EXTRA;
            if (partnerConfigHelper4.isPartnerConfigAvailable(partnerConfig4)) {
                this.headerAutoSizeLineExtraSpacingInPx = PartnerConfigHelper.get(context).getDimension(context, partnerConfig4);
            }
            PartnerConfigHelper partnerConfigHelper5 = PartnerConfigHelper.get(context);
            PartnerConfig partnerConfig5 = PartnerConfig.CONFIG_HEADER_AUTO_SIZE_MAX_LINE_OF_MAX_SIZE;
            if (partnerConfigHelper5.isPartnerConfigAvailable(partnerConfig5)) {
                this.headerAutoSizeMaxLineOfMaxSize = PartnerConfigHelper.get(context).getInteger(context, partnerConfig5, 0);
            }
            if (this.headerAutoSizeMaxLineOfMaxSize >= 1) {
                float f = this.headerAutoSizeMinTextSizeInPx;
                if (f > 0.0f && this.headerAutoSizeMaxTextSizeInPx >= f) {
                    return;
                }
            }
            Log.w("HeaderMixin", "Invalid configs, disable auto text size.");
            this.autoTextSizeEnabled = false;
        }
    }

    public void tryApplyPartnerCustomizationStyle() {
        TextView textView = (TextView) this.templateLayout.findManagedViewById(R$id.suc_layout_title);
        if (PartnerStyleHelper.shouldApplyPartnerResource(this.templateLayout)) {
            View findManagedViewById = this.templateLayout.findManagedViewById(R$id.sud_layout_header);
            LayoutStyler.applyPartnerCustomizationExtraPaddingStyle(findManagedViewById);
            HeaderAreaStyler.applyPartnerCustomizationHeaderStyle(textView);
            HeaderAreaStyler.applyPartnerCustomizationHeaderAreaStyle((ViewGroup) findManagedViewById);
        }
        tryUpdateAutoTextSizeFlagWithPartnerConfig();
        if (this.autoTextSizeEnabled) {
            autoAdjustTextSize(textView);
        }
    }

    public TextView getTextView() {
        return (TextView) this.templateLayout.findManagedViewById(R$id.suc_layout_title);
    }

    public void setText(int i) {
        TextView textView = getTextView();
        if (textView != null) {
            if (this.autoTextSizeEnabled) {
                autoAdjustTextSize(textView);
            }
            textView.setText(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView = getTextView();
        if (textView != null) {
            if (this.autoTextSizeEnabled) {
                autoAdjustTextSize(textView);
            }
            textView.setText(charSequence);
        }
    }

    private void autoAdjustTextSize(final TextView textView) {
        if (textView == null) {
            return;
        }
        textView.setTextSize(0, this.headerAutoSizeMaxTextSizeInPx);
        textView.setLineHeight(Math.round(this.headerAutoSizeLineExtraSpacingInPx + this.headerAutoSizeMaxTextSizeInPx));
        textView.setMaxLines(6);
        textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.google.android.setupdesign.template.HeaderMixin.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                textView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (textView.getLineCount() > HeaderMixin.this.headerAutoSizeMaxLineOfMaxSize) {
                    textView.setTextSize(0, HeaderMixin.this.headerAutoSizeMinTextSizeInPx);
                    textView.setLineHeight(Math.round(HeaderMixin.this.headerAutoSizeLineExtraSpacingInPx + HeaderMixin.this.headerAutoSizeMinTextSizeInPx));
                    textView.invalidate();
                    return false;
                }
                return true;
            }
        });
    }

    public CharSequence getText() {
        TextView textView = getTextView();
        if (textView != null) {
            return textView.getText();
        }
        return null;
    }

    public void setTextColor(ColorStateList colorStateList) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }

    public ColorStateList getTextColor() {
        TextView textView = getTextView();
        if (textView != null) {
            return textView.getTextColors();
        }
        return null;
    }
}
