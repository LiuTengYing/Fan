package com.android.settings.wifi.dpp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R$style;
import com.android.settings.core.InstrumentedFragment;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes2.dex */
public abstract class WifiDppQrCodeBaseFragment extends InstrumentedFragment {
    private GlifLayout mGlifLayout;
    protected FooterButton mLeftButton;
    protected FooterButton mRightButton;
    protected TextView mSummary;

    protected abstract boolean isFooterAvailable();

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mGlifLayout = (GlifLayout) view;
        this.mSummary = (TextView) view.findViewById(16908304);
        if (isFooterAvailable()) {
            this.mLeftButton = new FooterButton.Builder(getContext()).setButtonType(2).setTheme(R$style.SudGlifButton_Secondary).build();
            ((FooterBarMixin) this.mGlifLayout.getMixin(FooterBarMixin.class)).setSecondaryButton(this.mLeftButton);
            this.mRightButton = new FooterButton.Builder(getContext()).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build();
            ((FooterBarMixin) this.mGlifLayout.getMixin(FooterBarMixin.class)).setPrimaryButton(this.mRightButton);
        }
        this.mGlifLayout.getHeaderTextView().setAccessibilityLiveRegion(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderIconImageResource(int i) {
        this.mGlifLayout.setIcon(getDrawable(i));
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            Log.e("WifiDppQrCodeBaseFragment", "Resource does not exist: " + i);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderTitle(String str) {
        this.mGlifLayout.setHeaderText(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderTitle(int i, Object... objArr) {
        this.mGlifLayout.setHeaderText(getString(i, objArr));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setProgressBarShown(boolean z) {
        this.mGlifLayout.setProgressBarShown(z);
    }
}
