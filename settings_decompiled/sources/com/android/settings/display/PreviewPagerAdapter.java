package com.android.settings.display;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;
import java.lang.reflect.Array;
/* loaded from: classes.dex */
public class PreviewPagerAdapter extends PagerAdapter {
    private static final Interpolator FADE_IN_INTERPOLATOR = new DecelerateInterpolator();
    private static final Interpolator FADE_OUT_INTERPOLATOR = new AccelerateInterpolator();
    private int mAnimationCounter;
    private Runnable mAnimationEndAction;
    private boolean mIsLayoutRtl;
    private FrameLayout[] mPreviewFrames;
    private boolean[][] mViewStubInflated;

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public PreviewPagerAdapter(Context context, boolean z, int[] iArr, Configuration[] configurationArr) {
        this.mIsLayoutRtl = z;
        this.mPreviewFrames = new FrameLayout[iArr.length];
        this.mViewStubInflated = (boolean[][]) Array.newInstance(boolean.class, iArr.length, configurationArr.length);
        for (final int i = 0; i < iArr.length; i++) {
            int length = this.mIsLayoutRtl ? (iArr.length - 1) - i : i;
            this.mPreviewFrames[length] = new FrameLayout(context);
            this.mPreviewFrames[length].setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            this.mPreviewFrames[length].setClipToPadding(true);
            this.mPreviewFrames[length].setClipChildren(true);
            for (final int i2 = 0; i2 < configurationArr.length; i2++) {
                Context createConfigurationContext = context.createConfigurationContext(configurationArr[i2]);
                createConfigurationContext.getTheme().setTo(context.getTheme());
                ViewStub viewStub = new ViewStub(createConfigurationContext);
                viewStub.setLayoutResource(iArr[i]);
                viewStub.setOnInflateListener(new ViewStub.OnInflateListener() { // from class: com.android.settings.display.PreviewPagerAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.ViewStub.OnInflateListener
                    public final void onInflate(ViewStub viewStub2, View view) {
                        PreviewPagerAdapter.this.lambda$new$0(i, i2, viewStub2, view);
                    }
                });
                this.mPreviewFrames[length].addView(viewStub);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, ViewStub viewStub, View view) {
        view.setVisibility(viewStub.getVisibility());
        this.mViewStubInflated[i][i2] = true;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mPreviewFrames.length;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        viewGroup.addView(this.mPreviewFrames[i]);
        return this.mPreviewFrames[i];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mAnimationCounter > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAnimationEndAction(Runnable runnable) {
        this.mAnimationEndAction = runnable;
    }

    public void setPreviewLayer(int i, int i2, int i3, boolean z) {
        FrameLayout[] frameLayoutArr;
        for (FrameLayout frameLayout : this.mPreviewFrames) {
            if (i2 >= 0) {
                View childAt = frameLayout.getChildAt(i2);
                if (this.mViewStubInflated[i3][i2]) {
                    if (frameLayout == this.mPreviewFrames[i3]) {
                        setVisibility(childAt, 4, z);
                    } else {
                        setVisibility(childAt, 4, false);
                    }
                }
            }
            View childAt2 = frameLayout.getChildAt(i);
            if (frameLayout == this.mPreviewFrames[i3]) {
                if (!this.mViewStubInflated[i3][i]) {
                    childAt2 = ((ViewStub) childAt2).inflate();
                    childAt2.setAlpha(0.0f);
                }
                setVisibility(childAt2, 0, z);
            } else {
                setVisibility(childAt2, 0, false);
            }
        }
    }

    private void setVisibility(final View view, final int i, boolean z) {
        float f = i == 0 ? 1.0f : 0.0f;
        if (!z) {
            view.setAlpha(f);
            view.setVisibility(i);
            return;
        }
        Interpolator interpolator = FADE_IN_INTERPOLATOR;
        if (i == 0) {
            view.animate().alpha(f).setInterpolator(interpolator).setDuration(400L).setListener(new PreviewFrameAnimatorListener()).withStartAction(new Runnable() { // from class: com.android.settings.display.PreviewPagerAdapter.1
                @Override // java.lang.Runnable
                public void run() {
                    view.setVisibility(i);
                }
            });
        } else {
            view.animate().alpha(f).setInterpolator(FADE_OUT_INTERPOLATOR).setDuration(400L).setListener(new PreviewFrameAnimatorListener()).withEndAction(new Runnable() { // from class: com.android.settings.display.PreviewPagerAdapter.2
                @Override // java.lang.Runnable
                public void run() {
                    view.setVisibility(i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runAnimationEndAction() {
        if (this.mAnimationEndAction == null || isAnimating()) {
            return;
        }
        this.mAnimationEndAction.run();
        this.mAnimationEndAction = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PreviewFrameAnimatorListener implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        private PreviewFrameAnimatorListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            PreviewPagerAdapter.this.mAnimationCounter++;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            PreviewPagerAdapter previewPagerAdapter = PreviewPagerAdapter.this;
            previewPagerAdapter.mAnimationCounter--;
            PreviewPagerAdapter.this.runAnimationEndAction();
        }
    }
}
