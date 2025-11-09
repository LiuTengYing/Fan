package com.android.settingslib.collapsingtoolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
/* loaded from: classes2.dex */
public class AppBarScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private boolean mInitialized;

    public AppBarScrollingViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior, androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
        boolean onDependentViewChanged = super.onDependentViewChanged(coordinatorLayout, view, view2);
        if (!this.mInitialized && (view2 instanceof AppBarLayout)) {
            this.mInitialized = true;
            setAppBarLayoutTransparent((AppBarLayout) view2);
        }
        return onDependentViewChanged;
    }

    void setAppBarLayoutTransparent(AppBarLayout appBarLayout) {
        appBarLayout.setBackgroundColor(0);
        appBarLayout.setTargetElevation(0.0f);
    }
}
