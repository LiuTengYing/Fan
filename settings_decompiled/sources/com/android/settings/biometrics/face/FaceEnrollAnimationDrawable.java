package com.android.settings.biometrics.face;

import android.animation.TimeAnimator;
import android.app.UiModeManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.face.ParticleCollection;
/* loaded from: classes.dex */
public class FaceEnrollAnimationDrawable extends Drawable implements BiometricEnrollSidecar.Listener {
    private final ParticleCollection.Listener mAnimationListener = new ParticleCollection.Listener() { // from class: com.android.settings.biometrics.face.FaceEnrollAnimationDrawable.1
        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            if (FaceEnrollAnimationDrawable.this.mTimeAnimator == null || !FaceEnrollAnimationDrawable.this.mTimeAnimator.isStarted()) {
                return;
            }
            FaceEnrollAnimationDrawable.this.mTimeAnimator.end();
        }
    };
    private Rect mBounds;
    private final Paint mCircleCutoutPaint;
    private final Context mContext;
    private final ParticleCollection.Listener mListener;
    private ParticleCollection mParticleCollection;
    private final Paint mSquarePaint;
    private TimeAnimator mTimeAnimator;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public FaceEnrollAnimationDrawable(Context context, ParticleCollection.Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        Paint paint = new Paint();
        this.mSquarePaint = paint;
        if (((UiModeManager) context.getSystemService("uimode")).getNightMode() == 2) {
            paint.setColor(-16777216);
        } else {
            paint.setColor(-1);
        }
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mCircleCutoutPaint = paint2;
        paint2.setColor(0);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint2.setAntiAlias(true);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        this.mParticleCollection.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        this.mParticleCollection.onEnrollmentError(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mParticleCollection.onEnrollmentProgressChange(i, i2);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mBounds = rect;
        this.mParticleCollection = new ParticleCollection(this.mContext, this.mAnimationListener, rect, 20);
        if (this.mTimeAnimator == null) {
            TimeAnimator timeAnimator = new TimeAnimator();
            this.mTimeAnimator = timeAnimator;
            timeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.android.settings.biometrics.face.FaceEnrollAnimationDrawable$$ExternalSyntheticLambda0
                @Override // android.animation.TimeAnimator.TimeListener
                public final void onTimeUpdate(TimeAnimator timeAnimator2, long j, long j2) {
                    FaceEnrollAnimationDrawable.this.lambda$onBoundsChange$0(timeAnimator2, j, j2);
                }
            });
            this.mTimeAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBoundsChange$0(TimeAnimator timeAnimator, long j, long j2) {
        this.mParticleCollection.update(j, j2);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mBounds == null) {
            return;
        }
        canvas.save();
        canvas.drawRect(0.0f, 0.0f, this.mBounds.width(), this.mBounds.height(), this.mSquarePaint);
        canvas.drawCircle(this.mBounds.exactCenterX(), this.mBounds.exactCenterY(), (this.mBounds.height() / 2) - 20, this.mCircleCutoutPaint);
        this.mParticleCollection.draw(canvas);
        canvas.restore();
    }
}
