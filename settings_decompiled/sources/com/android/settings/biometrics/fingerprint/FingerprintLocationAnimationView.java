package com.android.settings.biometrics.fingerprint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.window.R;
import com.android.settings.R$dimen;
import com.android.settings.R$fraction;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class FingerprintLocationAnimationView extends View implements FingerprintFindSensorAnimation {
    private ValueAnimator mAlphaAnimator;
    private final Paint mDotPaint;
    private final int mDotRadius;
    private final Interpolator mFastOutSlowInInterpolator;
    private String mFingerprintLocation;
    private final float mFractionCenterX;
    private final float mFractionCenterY;
    private final Interpolator mLinearOutSlowInInterpolator;
    private final int mMaxPulseRadius;
    private final Paint mPulsePaint;
    private float mPulseRadius;
    private ValueAnimator mRadiusAnimator;
    private final Runnable mStartPhaseRunnable;

    public FingerprintLocationAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFingerprintLocation = SystemProperties.get("ro.boot.hardware");
        Paint paint = new Paint();
        this.mDotPaint = paint;
        Paint paint2 = new Paint();
        this.mPulsePaint = paint2;
        this.mStartPhaseRunnable = new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.5
            @Override // java.lang.Runnable
            public void run() {
                FingerprintLocationAnimationView.this.startPhase();
            }
        };
        this.mDotRadius = getResources().getDimensionPixelSize(R$dimen.fingerprint_dot_radius);
        this.mMaxPulseRadius = getResources().getDimensionPixelSize(R$dimen.fingerprint_pulse_radius);
        this.mFractionCenterX = getResources().getFraction(R$fraction.fingerprint_sensor_location_fraction_x, 1, 1);
        this.mFractionCenterY = getResources().getFraction(R$fraction.fingerprint_sensor_location_fraction_y, 1, 1);
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(context);
        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint.setColor(colorAccentDefaultColor);
        paint2.setColor(colorAccentDefaultColor);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mFingerprintLocation.contains("9230") || this.mFingerprintLocation.contains("7885") || this.mFingerprintLocation.contains("9620") || this.mFingerprintLocation.contains("9621")) {
            this.mFingerprintLocation = "RIGHT";
        }
        drawPulse(canvas);
        drawDot(canvas);
    }

    private void drawDot(Canvas canvas) {
        String str = this.mFingerprintLocation;
        str.hashCode();
        if (str.equals("LEFT") || str.equals("RIGHT")) {
            return;
        }
        canvas.drawCircle(getCenterX(), getCenterY(), this.mDotRadius, this.mDotPaint);
    }

    private void drawPulse(Canvas canvas) {
        String str = this.mFingerprintLocation;
        str.hashCode();
        if (str.equals("LEFT")) {
            drawLeftPulse(canvas);
        } else if (str.equals("RIGHT")) {
            drawRightPulse(canvas);
        } else {
            canvas.drawCircle(getCenterX(), getCenterY(), this.mPulseRadius, this.mPulsePaint);
        }
    }

    private void drawLeftPulse(Canvas canvas) {
        float height = getHeight() * 0.5f * (1.0f - getResources().getFraction(R$fraction.fingerprint_sensor_location_side_fraction, 1, 1));
        canvas.drawArc(0.0f, height, getWidth() * 2, getHeight() - height, 90.0f, 180.0f, true, this.mPulsePaint);
    }

    private void drawRightPulse(Canvas canvas) {
        float fraction = getResources().getFraction(R$fraction.fingerprint_sensor_location_side_fraction, 1, 1);
        float width = 0.0f - getWidth();
        float height = getHeight() * 0.5f * (1.0f - fraction);
        canvas.drawArc(width, height, getWidth(), getHeight() - height, -90.0f, 180.0f, true, this.mPulsePaint);
    }

    private float getCenterX() {
        return getWidth() * this.mFractionCenterX;
    }

    private float getCenterY() {
        return getHeight() * this.mFractionCenterY;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void startAnimation() {
        startPhase();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void stopAnimation() {
        removeCallbacks(this.mStartPhaseRunnable);
        ValueAnimator valueAnimator = this.mRadiusAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.mAlphaAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void pauseAnimation() {
        stopAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPhase() {
        startRadiusAnimation();
        startAlphaAnimation();
    }

    private void startRadiusAnimation() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, this.mMaxPulseRadius);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                FingerprintLocationAnimationView.this.mPulseRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                FingerprintLocationAnimationView.this.invalidate();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.2
            boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                FingerprintLocationAnimationView.this.mRadiusAnimator = null;
                if (this.mCancelled) {
                    return;
                }
                FingerprintLocationAnimationView fingerprintLocationAnimationView = FingerprintLocationAnimationView.this;
                fingerprintLocationAnimationView.postDelayed(fingerprintLocationAnimationView.mStartPhaseRunnable, 1000L);
            }
        });
        ofFloat.setDuration(1000L);
        ofFloat.setInterpolator(this.mLinearOutSlowInInterpolator);
        ofFloat.start();
        this.mRadiusAnimator = ofFloat;
    }

    private void startAlphaAnimation() {
        this.mPulsePaint.setAlpha(R.styleable.AppCompatTheme_tooltipForegroundColor);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.45f, 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                FingerprintLocationAnimationView.this.mPulsePaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
                FingerprintLocationAnimationView.this.invalidate();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                FingerprintLocationAnimationView.this.mAlphaAnimator = null;
            }
        });
        ofFloat.setDuration(750L);
        ofFloat.setInterpolator(this.mFastOutSlowInInterpolator);
        ofFloat.setStartDelay(250L);
        ofFloat.start();
        this.mAlphaAnimator = ofFloat;
    }
}
