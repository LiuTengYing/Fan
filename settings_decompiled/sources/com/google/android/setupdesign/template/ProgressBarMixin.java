package com.google.android.setupdesign.template;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.R$dimen;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.util.HeaderAreaStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;
/* loaded from: classes2.dex */
public class ProgressBarMixin implements Mixin {
    private ColorStateList color;
    private final TemplateLayout templateLayout;
    private final boolean useBottomProgressBar;

    public ProgressBarMixin(TemplateLayout templateLayout) {
        this(templateLayout, null, 0);
    }

    public ProgressBarMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        this.templateLayout = templateLayout;
        boolean z = false;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = templateLayout.getContext().obtainStyledAttributes(attributeSet, R$styleable.SudProgressBarMixin, i, 0);
            int i2 = R$styleable.SudProgressBarMixin_sudUseBottomProgressBar;
            boolean z2 = obtainStyledAttributes.hasValue(i2) ? obtainStyledAttributes.getBoolean(i2, false) : false;
            obtainStyledAttributes.recycle();
            setShown(false);
            z = z2;
        }
        this.useBottomProgressBar = z;
    }

    public boolean isShown() {
        View findManagedViewById = this.templateLayout.findManagedViewById(this.useBottomProgressBar ? R$id.sud_glif_progress_bar : R$id.sud_layout_progress);
        return findManagedViewById != null && findManagedViewById.getVisibility() == 0;
    }

    public void setShown(boolean z) {
        if (z) {
            ProgressBar progressBar = getProgressBar();
            if (progressBar != null) {
                progressBar.setVisibility(0);
                return;
            }
            return;
        }
        ProgressBar peekProgressBar = peekProgressBar();
        if (peekProgressBar != null) {
            peekProgressBar.setVisibility(this.useBottomProgressBar ? 4 : 8);
        }
    }

    private ProgressBar getProgressBar() {
        if (peekProgressBar() == null && !this.useBottomProgressBar) {
            ViewStub viewStub = (ViewStub) this.templateLayout.findManagedViewById(R$id.sud_layout_progress_stub);
            if (viewStub != null) {
                viewStub.inflate();
            }
            setColor(this.color);
        }
        return peekProgressBar();
    }

    public ProgressBar peekProgressBar() {
        return (ProgressBar) this.templateLayout.findManagedViewById(this.useBottomProgressBar ? R$id.sud_glif_progress_bar : R$id.sud_layout_progress);
    }

    public void setColor(ColorStateList colorStateList) {
        this.color = colorStateList;
        ProgressBar peekProgressBar = peekProgressBar();
        if (peekProgressBar != null) {
            peekProgressBar.setIndeterminateTintList(colorStateList);
            peekProgressBar.setProgressBackgroundTintList(colorStateList);
        }
    }

    public ColorStateList getColor() {
        return this.color;
    }

    public void tryApplyPartnerCustomizationStyle() {
        ProgressBar peekProgressBar = peekProgressBar();
        if (!this.useBottomProgressBar || peekProgressBar == null) {
            return;
        }
        if (PartnerStyleHelper.isPartnerHeavyThemeLayout(this.templateLayout)) {
            HeaderAreaStyler.applyPartnerCustomizationProgressBarStyle(peekProgressBar);
            return;
        }
        Context context = peekProgressBar.getContext();
        ViewGroup.LayoutParams layoutParams = peekProgressBar.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, (int) context.getResources().getDimension(R$dimen.sud_progress_bar_margin_top), marginLayoutParams.rightMargin, (int) context.getResources().getDimension(R$dimen.sud_progress_bar_margin_bottom));
        }
    }
}
