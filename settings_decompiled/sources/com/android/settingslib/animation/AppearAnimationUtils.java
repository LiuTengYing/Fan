package com.android.settingslib.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.settingslib.R$dimen;
/* loaded from: classes2.dex */
public class AppearAnimationUtils implements AppearAnimationCreator<View> {
    protected final float mDelayScale;
    private final long mDuration;
    private final Interpolator mInterpolator;
    protected RowTranslationScaler mRowTranslationScaler;
    private final float mStartTranslation;
    private final AppearAnimationProperties mProperties = new AppearAnimationProperties();
    protected boolean mAppearing = true;

    /* loaded from: classes2.dex */
    public interface RowTranslationScaler {
        float getRowTranslationScale(int i, int i2);
    }

    public AppearAnimationUtils(Context context, long j, float f, float f2, Interpolator interpolator) {
        this.mInterpolator = interpolator;
        this.mStartTranslation = context.getResources().getDimensionPixelOffset(R$dimen.appear_y_translation_start) * f;
        this.mDelayScale = f2;
        this.mDuration = j;
    }

    public void startAnimation(View[] viewArr, Runnable runnable) {
        startAnimation(viewArr, runnable, this);
    }

    public <T> void startAnimation2d(T[][] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        startAnimations(getDelays((Object[][]) tArr), (Object[][]) tArr, runnable, (AppearAnimationCreator) appearAnimationCreator);
    }

    public <T> void startAnimation(T[] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        startAnimations(getDelays(tArr), tArr, runnable, appearAnimationCreator);
    }

    private <T> void startAnimations(AppearAnimationProperties appearAnimationProperties, T[] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex == -1 || appearAnimationProperties.maxDelayColIndex == -1) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            long[][] jArr = appearAnimationProperties.delays;
            if (i >= jArr.length) {
                return;
            }
            long j = jArr[i][0];
            Runnable runnable2 = (appearAnimationProperties.maxDelayRowIndex == i && appearAnimationProperties.maxDelayColIndex == 0) ? runnable : null;
            RowTranslationScaler rowTranslationScaler = this.mRowTranslationScaler;
            float rowTranslationScale = (rowTranslationScaler != null ? rowTranslationScaler.getRowTranslationScale(i, jArr.length) : 1.0f) * this.mStartTranslation;
            T t = tArr[i];
            long j2 = this.mDuration;
            boolean z = this.mAppearing;
            if (!z) {
                rowTranslationScale = -rowTranslationScale;
            }
            appearAnimationCreator.createAnimation(t, j, j2, rowTranslationScale, z, this.mInterpolator, runnable2);
            i++;
        }
    }

    private <T> void startAnimations(AppearAnimationProperties appearAnimationProperties, T[][] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex == -1 || appearAnimationProperties.maxDelayColIndex == -1) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            long[][] jArr = appearAnimationProperties.delays;
            if (i >= jArr.length) {
                return;
            }
            long[] jArr2 = jArr[i];
            RowTranslationScaler rowTranslationScaler = this.mRowTranslationScaler;
            float rowTranslationScale = (rowTranslationScaler != null ? rowTranslationScaler.getRowTranslationScale(i, jArr.length) : 1.0f) * this.mStartTranslation;
            int i2 = 0;
            while (i2 < jArr2.length) {
                long j = jArr2[i2];
                Runnable runnable2 = (appearAnimationProperties.maxDelayRowIndex == i && appearAnimationProperties.maxDelayColIndex == i2) ? runnable : null;
                T t = tArr[i][i2];
                long j2 = this.mDuration;
                boolean z = this.mAppearing;
                appearAnimationCreator.createAnimation(t, j, j2, z ? rowTranslationScale : -rowTranslationScale, z, this.mInterpolator, runnable2);
                i2++;
            }
            i++;
        }
    }

    private <T> AppearAnimationProperties getDelays(T[] tArr) {
        AppearAnimationProperties appearAnimationProperties = this.mProperties;
        appearAnimationProperties.maxDelayColIndex = -1;
        appearAnimationProperties.maxDelayRowIndex = -1;
        appearAnimationProperties.delays = new long[tArr.length];
        long j = -1;
        for (int i = 0; i < tArr.length; i++) {
            this.mProperties.delays[i] = new long[1];
            long calculateDelay = calculateDelay(i, 0);
            AppearAnimationProperties appearAnimationProperties2 = this.mProperties;
            appearAnimationProperties2.delays[i][0] = calculateDelay;
            if (tArr[i] != null && calculateDelay > j) {
                appearAnimationProperties2.maxDelayColIndex = 0;
                appearAnimationProperties2.maxDelayRowIndex = i;
                j = calculateDelay;
            }
        }
        return this.mProperties;
    }

    private <T> AppearAnimationProperties getDelays(T[][] tArr) {
        AppearAnimationProperties appearAnimationProperties = this.mProperties;
        appearAnimationProperties.maxDelayColIndex = -1;
        appearAnimationProperties.maxDelayRowIndex = -1;
        appearAnimationProperties.delays = new long[tArr.length];
        long j = -1;
        for (int i = 0; i < tArr.length; i++) {
            T[] tArr2 = tArr[i];
            this.mProperties.delays[i] = new long[tArr2.length];
            for (int i2 = 0; i2 < tArr2.length; i2++) {
                long calculateDelay = calculateDelay(i, i2);
                AppearAnimationProperties appearAnimationProperties2 = this.mProperties;
                appearAnimationProperties2.delays[i][i2] = calculateDelay;
                if (tArr[i][i2] != null && calculateDelay > j) {
                    appearAnimationProperties2.maxDelayColIndex = i2;
                    appearAnimationProperties2.maxDelayRowIndex = i;
                    j = calculateDelay;
                }
            }
        }
        return this.mProperties;
    }

    protected long calculateDelay(int i, int i2) {
        return (long) (((i * 40) + (i2 * (Math.pow(i, 0.4d) + 0.4d) * 20.0d)) * this.mDelayScale);
    }

    @Override // com.android.settingslib.animation.AppearAnimationCreator
    public void createAnimation(View view, long j, long j2, float f, boolean z, Interpolator interpolator, Runnable runnable) {
        createAnimation(view, j, j2, f, z, interpolator, runnable, null);
    }

    public void createAnimation(final View view, long j, long j2, float f, boolean z, Interpolator interpolator, final Runnable runnable, AnimatorListenerAdapter animatorListenerAdapter) {
        RenderNodeAnimator ofFloat;
        if (view != null) {
            final float f2 = z ? 1.0f : 0.0f;
            float f3 = z ? 0.0f : f;
            view.setAlpha(1.0f - f2);
            view.setTranslationY(f - f3);
            if (view.isHardwareAccelerated()) {
                ofFloat = new RenderNodeAnimator(11, f2);
                ofFloat.setTarget(view);
            } else {
                ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), f2);
            }
            ofFloat.setInterpolator(interpolator);
            ofFloat.setDuration(j2);
            ofFloat.setStartDelay(j);
            if (view.hasOverlappingRendering()) {
                view.setLayerType(2, null);
                ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settingslib.animation.AppearAnimationUtils.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        view.setLayerType(0, null);
                    }
                });
            }
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settingslib.animation.AppearAnimationUtils.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    view.setAlpha(f2);
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            });
            ofFloat.start();
            startTranslationYAnimation(view, j, j2, f3, interpolator, animatorListenerAdapter);
        }
    }

    public static void startTranslationYAnimation(final View view, long j, long j2, final float f, Interpolator interpolator, AnimatorListenerAdapter animatorListenerAdapter) {
        RenderNodeAnimator ofFloat;
        if (view.isHardwareAccelerated()) {
            ofFloat = new RenderNodeAnimator(1, f);
            ofFloat.setTarget(view);
        } else {
            ofFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), f);
        }
        ofFloat.setInterpolator(interpolator);
        ofFloat.setDuration(j2);
        ofFloat.setStartDelay(j);
        if (animatorListenerAdapter != null) {
            ofFloat.addListener(animatorListenerAdapter);
        }
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settingslib.animation.AppearAnimationUtils.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setTranslationY(f);
            }
        });
        ofFloat.start();
    }

    /* loaded from: classes2.dex */
    public class AppearAnimationProperties {
        public long[][] delays;
        public int maxDelayColIndex;
        public int maxDelayRowIndex;

        public AppearAnimationProperties() {
        }
    }
}
