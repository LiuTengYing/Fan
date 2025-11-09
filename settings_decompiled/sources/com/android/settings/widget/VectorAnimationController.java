package com.android.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.TextureView;
import android.view.View;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import com.android.settings.widget.VideoPreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class VectorAnimationController implements VideoPreference.AnimationController {
    private AnimatedVectorDrawableCompat mAnimatedVectorDrawableCompat;
    private Animatable2Compat.AnimationCallback mAnimationCallback = new Animatable2Compat.AnimationCallback() { // from class: com.android.settings.widget.VectorAnimationController.1
        @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
        public void onAnimationEnd(Drawable drawable) {
            VectorAnimationController.this.mAnimatedVectorDrawableCompat.start();
        }
    };
    private Drawable mPreviewDrawable;

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getDuration() {
        return 5000;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VectorAnimationController(Context context, int i) {
        this.mAnimatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(context, i);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoWidth() {
        return this.mAnimatedVectorDrawableCompat.getIntrinsicWidth();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoHeight() {
        return this.mAnimatedVectorDrawableCompat.getIntrinsicHeight();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void attachView(TextureView textureView, final View view, final View view2) {
        this.mPreviewDrawable = view.getForeground();
        textureView.setVisibility(8);
        updateViewStates(view, view2);
        view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.VectorAnimationController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                VectorAnimationController.this.lambda$attachView$0(view, view2, view3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$attachView$0(View view, View view2, View view3) {
        updateViewStates(view, view2);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void release() {
        this.mAnimatedVectorDrawableCompat.stop();
        this.mAnimatedVectorDrawableCompat.clearAnimationCallbacks();
    }

    private void updateViewStates(View view, View view2) {
        if (this.mAnimatedVectorDrawableCompat.isRunning()) {
            this.mAnimatedVectorDrawableCompat.stop();
            this.mAnimatedVectorDrawableCompat.clearAnimationCallbacks();
            view2.setVisibility(0);
            view.setForeground(this.mPreviewDrawable);
            return;
        }
        view2.setVisibility(8);
        view.setForeground(this.mAnimatedVectorDrawableCompat);
        this.mAnimatedVectorDrawableCompat.start();
        this.mAnimatedVectorDrawableCompat.registerAnimationCallback(this.mAnimationCallback);
    }
}
