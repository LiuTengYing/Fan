package com.android.settingslib.collapsingtoolbar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.android.settingslib.collapsingtoolbar.R$id;
import com.android.settingslib.collapsingtoolbar.R$layout;
import com.android.settingslib.collapsingtoolbar.R$styleable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
/* loaded from: classes2.dex */
public class CollapsingCoordinatorLayout extends CoordinatorLayout {
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean mIsMatchParentHeight;
    private CharSequence mToolbarTitle;

    public CollapsingCoordinatorLayout(Context context) {
        this(context, null);
    }

    public CollapsingCoordinatorLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CollapsingCoordinatorLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsMatchParentHeight = false;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CollapsingCoordinatorLayout);
            this.mToolbarTitle = obtainStyledAttributes.getText(R$styleable.CollapsingCoordinatorLayout_collapsing_toolbar_title);
            this.mIsMatchParentHeight = obtainStyledAttributes.getBoolean(R$styleable.CollapsingCoordinatorLayout_content_frame_height_match_parent, false);
            obtainStyledAttributes.recycle();
        }
        init();
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        int id = view.getId();
        int i2 = R$id.content_frame;
        if (id == i2 && this.mIsMatchParentHeight) {
            layoutParams.height = -1;
        }
        ViewGroup viewGroup = (ViewGroup) findViewById(i2);
        if (viewGroup != null && isContentFrameChild(view.getId())) {
            viewGroup.addView(view, i, layoutParams);
        } else {
            super.addView(view, i, layoutParams);
        }
    }

    private boolean isContentFrameChild(int i) {
        return (i == R$id.app_bar || i == R$id.content_frame) ? false : true;
    }

    private void init() {
        ViewGroup.inflate(getContext(), R$layout.collapsing_toolbar_content_layout, this);
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R$id.collapsing_toolbar);
        this.mAppBarLayout = (AppBarLayout) findViewById(R$id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
            this.mCollapsingToolbarLayout.setHyphenationFrequency(3);
            if (!TextUtils.isEmpty(this.mToolbarTitle)) {
                this.mCollapsingToolbarLayout.setTitle(this.mToolbarTitle);
            }
        }
        disableCollapsingToolbarLayoutScrollingBehavior();
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return this.mCollapsingToolbarLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return this.mAppBarLayout;
    }

    public View getContentFrameLayout() {
        return findViewById(R$id.content_frame);
    }

    public Toolbar getSupportToolbar() {
        return (Toolbar) this.mCollapsingToolbarLayout.findViewById(R$id.support_action_bar);
    }

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout == null) {
            return;
        }
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() { // from class: com.android.settingslib.collapsingtoolbar.widget.CollapsingCoordinatorLayout.1
            @Override // com.google.android.material.appbar.AppBarLayout.BaseBehavior.BaseDragCallback
            public boolean canDrag(AppBarLayout appBarLayout2) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(behavior);
    }
}
