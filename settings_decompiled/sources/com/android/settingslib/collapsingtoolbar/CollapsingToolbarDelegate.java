package com.android.settingslib.collapsingtoolbar;

import android.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
/* loaded from: classes2.dex */
public class CollapsingToolbarDelegate {
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FrameLayout mContentFrameLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private final HostCallback mHostCallback;
    private Toolbar mToolbar;

    /* loaded from: classes2.dex */
    public interface HostCallback {
        ActionBar setActionBar(Toolbar toolbar);

        void setOuterTitle(CharSequence charSequence);
    }

    public CollapsingToolbarDelegate(HostCallback hostCallback) {
        this.mHostCallback = hostCallback;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflate = layoutInflater.inflate(R$layout.collapsing_toolbar_base_layout, viewGroup, false);
        if (inflate instanceof CoordinatorLayout) {
            this.mCoordinatorLayout = (CoordinatorLayout) inflate;
        }
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) inflate.findViewById(R$id.collapsing_toolbar);
        this.mAppBarLayout = (AppBarLayout) inflate.findViewById(R$id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
            this.mCollapsingToolbarLayout.setHyphenationFrequency(3);
        }
        disableCollapsingToolbarLayoutScrollingBehavior();
        this.mToolbar = (Toolbar) inflate.findViewById(R$id.action_bar);
        this.mContentFrameLayout = (FrameLayout) inflate.findViewById(R$id.content_frame);
        ActionBar actionBar = this.mHostCallback.setActionBar(this.mToolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        return inflate;
    }

    public void setTitle(CharSequence charSequence) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(charSequence);
        } else {
            this.mHostCallback.setOuterTitle(charSequence);
        }
    }

    public FrameLayout getContentFrameLayout() {
        return this.mContentFrameLayout;
    }

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout == null) {
            return;
        }
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() { // from class: com.android.settingslib.collapsingtoolbar.CollapsingToolbarDelegate.1
            @Override // com.google.android.material.appbar.AppBarLayout.BaseBehavior.BaseDragCallback
            public boolean canDrag(AppBarLayout appBarLayout2) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(behavior);
    }
}
