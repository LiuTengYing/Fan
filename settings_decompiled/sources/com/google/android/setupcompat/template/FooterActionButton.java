package com.google.android.setupcompat.template;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
/* loaded from: classes2.dex */
public class FooterActionButton extends Button {
    private FooterButton footerButton;
    private boolean isPrimaryButtonStyle;

    public FooterActionButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isPrimaryButtonStyle = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFooterButton(FooterButton footerButton) {
        this.footerButton = footerButton;
    }

    @Override // android.widget.TextView, android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        FooterButton footerButton;
        View.OnClickListener onClickListenerWhenDisabled;
        if (motionEvent.getAction() == 0 && (footerButton = this.footerButton) != null && !footerButton.isEnabled() && this.footerButton.getVisibility() == 0 && (onClickListenerWhenDisabled = this.footerButton.getOnClickListenerWhenDisabled()) != null) {
            onClickListenerWhenDisabled.onClick(this);
        }
        return super.onTouchEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPrimaryButtonStyle(boolean z) {
        this.isPrimaryButtonStyle = z;
    }

    public boolean isPrimaryButtonStyle() {
        return this.isPrimaryButtonStyle;
    }
}
