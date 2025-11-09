package com.android.settingslib.collapsingtoolbar;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarDelegate;
import com.android.settingslib.utils.BuildCompatUtils;
/* loaded from: classes2.dex */
public class CollapsingToolbarBaseActivity extends FragmentActivity {
    private int mCustomizeLayoutResId = 0;
    private CollapsingToolbarDelegate mToolbardelegate;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DelegateCallback implements CollapsingToolbarDelegate.HostCallback {
        private DelegateCallback() {
        }

        @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarDelegate.HostCallback
        public ActionBar setActionBar(Toolbar toolbar) {
            CollapsingToolbarBaseActivity.super.setActionBar(toolbar);
            return CollapsingToolbarBaseActivity.super.getActionBar();
        }

        @Override // com.android.settingslib.collapsingtoolbar.CollapsingToolbarDelegate.HostCallback
        public void setOuterTitle(CharSequence charSequence) {
            CollapsingToolbarBaseActivity.super.setTitle(charSequence);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mCustomizeLayoutResId > 0 && !BuildCompatUtils.isAtLeastS()) {
            super.setContentView(this.mCustomizeLayoutResId);
        } else {
            super.setContentView(getToolbarDelegate().onCreateView(getLayoutInflater(), null));
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(int i) {
        CollapsingToolbarDelegate collapsingToolbarDelegate = this.mToolbardelegate;
        ViewGroup contentFrameLayout = collapsingToolbarDelegate == null ? (ViewGroup) findViewById(R$id.content_frame) : collapsingToolbarDelegate.getContentFrameLayout();
        if (contentFrameLayout != null) {
            contentFrameLayout.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, contentFrameLayout);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view) {
        CollapsingToolbarDelegate collapsingToolbarDelegate = this.mToolbardelegate;
        ViewGroup contentFrameLayout = collapsingToolbarDelegate == null ? (ViewGroup) findViewById(R$id.content_frame) : collapsingToolbarDelegate.getContentFrameLayout();
        if (contentFrameLayout != null) {
            contentFrameLayout.addView(view);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        CollapsingToolbarDelegate collapsingToolbarDelegate = this.mToolbardelegate;
        ViewGroup contentFrameLayout = collapsingToolbarDelegate == null ? (ViewGroup) findViewById(R$id.content_frame) : collapsingToolbarDelegate.getContentFrameLayout();
        if (contentFrameLayout != null) {
            contentFrameLayout.addView(view, layoutParams);
        }
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        getToolbarDelegate().setTitle(charSequence);
    }

    @Override // android.app.Activity
    public void setTitle(int i) {
        setTitle(getText(i));
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finishAfterTransition();
        return true;
    }

    private CollapsingToolbarDelegate getToolbarDelegate() {
        if (this.mToolbardelegate == null) {
            this.mToolbardelegate = new CollapsingToolbarDelegate(new DelegateCallback());
        }
        return this.mToolbardelegate;
    }
}
