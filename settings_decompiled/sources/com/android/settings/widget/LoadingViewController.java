package com.android.settings.widget;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
/* loaded from: classes.dex */
public class LoadingViewController {
    private final View mContentView;
    private final View mEmptyView;
    private final Handler mFgHandler;
    private final View mLoadingView;
    private Runnable mShowLoadingContainerRunnable;

    public LoadingViewController(View view, View view2) {
        this(view, view2, null);
    }

    public LoadingViewController(View view, View view2, View view3) {
        this.mShowLoadingContainerRunnable = new Runnable() { // from class: com.android.settings.widget.LoadingViewController.1
            @Override // java.lang.Runnable
            public void run() {
                LoadingViewController.this.showLoadingView();
            }
        };
        this.mLoadingView = view;
        this.mContentView = view2;
        this.mEmptyView = view3;
        this.mFgHandler = new Handler(Looper.getMainLooper());
    }

    public void showContent(boolean z) {
        this.mFgHandler.removeCallbacks(this.mShowLoadingContainerRunnable);
        handleLoadingContainer(true, false, z);
    }

    public void showEmpty(boolean z) {
        if (this.mEmptyView == null) {
            return;
        }
        this.mFgHandler.removeCallbacks(this.mShowLoadingContainerRunnable);
        handleLoadingContainer(false, true, z);
    }

    public void showLoadingView() {
        handleLoadingContainer(false, false, false);
    }

    public void showLoadingViewDelayed() {
        this.mFgHandler.postDelayed(this.mShowLoadingContainerRunnable, 100L);
    }

    private void handleLoadingContainer(boolean z, boolean z2, boolean z3) {
        handleLoadingContainer(this.mLoadingView, this.mContentView, this.mEmptyView, z, z2, z3);
    }

    public static void handleLoadingContainer(View view, View view2, boolean z, boolean z2) {
        setViewShown(view, !z, z2);
        setViewShown(view2, z, z2);
    }

    public static void handleLoadingContainer(View view, View view2, View view3, boolean z, boolean z2, boolean z3) {
        if (view3 != null) {
            setViewShown(view3, z2, z3);
        }
        setViewShown(view2, z, z3);
        setViewShown(view, (z || z2) ? false : true, z3);
    }

    private static void setViewShown(final View view, boolean z, boolean z2) {
        if (z2) {
            Animation loadAnimation = AnimationUtils.loadAnimation(view.getContext(), z ? 17432576 : 17432577);
            if (z) {
                view.setVisibility(0);
            } else {
                loadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.settings.widget.LoadingViewController.2
                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(4);
                    }
                });
            }
            view.startAnimation(loadAnimation);
            return;
        }
        view.clearAnimation();
        view.setVisibility(z ? 0 : 4);
    }
}
