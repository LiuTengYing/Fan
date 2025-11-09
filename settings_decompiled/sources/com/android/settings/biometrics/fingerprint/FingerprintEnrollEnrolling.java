package com.android.settings.biometrics.fingerprint;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.Process;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.BiometricsEnrollEnrolling;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.display.DisplayDensityUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintEnrollEnrolling extends BiometricsEnrollEnrolling {
    private AccessibilityManager mAccessibilityManager;
    private boolean mAnimationCancelled;
    private boolean mCanAssumeUdfps;
    private TextView mErrorText;
    private Interpolator mFastOutLinearInInterpolator;
    private Interpolator mFastOutSlowInInterpolator;
    private FingerprintManager mFingerprintManager;
    private boolean mHaveShownUdfpsLeftEdgeLottie;
    private boolean mHaveShownUdfpsRightEdgeLottie;
    private boolean mHaveShownUdfpsTipLottie;
    private AnimatedVectorDrawable mIconAnimationDrawable;
    private AnimatedVectorDrawable mIconBackgroundBlinksDrawable;
    private int mIconTouchCount;
    private LottieAnimationView mIllustrationLottie;
    private boolean mIsAccessibilityEnabled;
    private boolean mIsSetupWizard;
    private Interpolator mLinearOutSlowInInterpolator;
    private OrientationEventListener mOrientationEventListener;
    private ObjectAnimator mProgressAnim;
    private ProgressBar mProgressBar;
    private boolean mRestoring;
    private boolean mShouldShowLottie;
    private Vibrator mVibrator;
    private static final VibrationEffect VIBRATE_EFFECT_ERROR = VibrationEffect.createWaveform(new long[]{0, 5, 55, 60}, -1);
    private static final VibrationAttributes FINGERPRINT_ENROLLING_SONFICATION_ATTRIBUTES = VibrationAttributes.createForUsage(66);
    private int mPreviousRotation = 0;
    private final Animator.AnimatorListener mProgressAnimationListener = new Animator.AnimatorListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.2
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (FingerprintEnrollEnrolling.this.mProgressBar.getProgress() >= 10000) {
                FingerprintEnrollEnrolling.this.mProgressBar.postDelayed(FingerprintEnrollEnrolling.this.mDelayedFinishRunnable, FingerprintEnrollEnrolling.this.getFinishDelay());
            }
        }
    };
    private final Runnable mDelayedFinishRunnable = new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.3
        @Override // java.lang.Runnable
        public void run() {
            FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
            fingerprintEnrollEnrolling.launchFinish(((BiometricEnrollBase) fingerprintEnrollEnrolling).mToken);
        }
    };
    private final Animatable2.AnimationCallback mIconAnimationCallback = new Animatable2.AnimationCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.4
        @Override // android.graphics.drawable.Animatable2.AnimationCallback
        public void onAnimationEnd(Drawable drawable) {
            if (FingerprintEnrollEnrolling.this.mAnimationCancelled) {
                return;
            }
            FingerprintEnrollEnrolling.this.mProgressBar.post(new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.4.1
                @Override // java.lang.Runnable
                public void run() {
                    FingerprintEnrollEnrolling.this.startIconAnimation();
                }
            });
        }
    };
    private final Runnable mShowDialogRunnable = new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.5
        @Override // java.lang.Runnable
        public void run() {
            FingerprintEnrollEnrolling.this.showIconTouchDialog();
        }
    };
    private final Runnable mTouchAgainRunnable = new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.6
        @Override // java.lang.Runnable
        public void run() {
            FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
            fingerprintEnrollEnrolling.showError(fingerprintEnrollEnrolling.getString(R$string.security_settings_fingerprint_enroll_lift_touch_again));
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 240;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FingerprintManager.class);
        this.mFingerprintManager = fingerprintManager;
        List sensorPropertiesInternal = fingerprintManager.getSensorPropertiesInternal();
        this.mCanAssumeUdfps = sensorPropertiesInternal.size() == 1 && ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnyUdfpsType();
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        this.mAccessibilityManager = accessibilityManager;
        this.mIsAccessibilityEnabled = accessibilityManager.isEnabled();
        listenOrientationEvent();
        if (this.mCanAssumeUdfps) {
            if (BiometricUtils.isReverseLandscape(getApplicationContext())) {
                setContentView(R$layout.udfps_enroll_enrolling_land);
            } else {
                setContentView(R$layout.udfps_enroll_enrolling);
            }
            setDescriptionText(R$string.security_settings_udfps_enroll_start_message);
        } else {
            setContentView(R$layout.fingerprint_enroll_enrolling);
            setDescriptionText(R$string.security_settings_fingerprint_enroll_start_message);
        }
        this.mIsSetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        if (this.mCanAssumeUdfps) {
            updateTitleAndDescription();
        } else {
            setHeaderText(R$string.security_settings_fingerprint_enroll_repeat_title);
        }
        DisplayDensityUtils displayDensityUtils = new DisplayDensityUtils(getApplicationContext());
        this.mShouldShowLottie = displayDensityUtils.getDefaultDensity() == displayDensityUtils.getValues()[displayDensityUtils.getCurrentIndex()];
        updateOrientation(BiometricUtils.isReverseLandscape(getApplicationContext()) || BiometricUtils.isLandscape(getApplicationContext()) ? 2 : 1);
        this.mErrorText = (TextView) findViewById(R$id.error_text);
        this.mProgressBar = (ProgressBar) findViewById(R$id.fingerprint_progress_bar);
        this.mVibrator = (Vibrator) getSystemService(Vibrator.class);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_fingerprint_enroll_enrolling_skip).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FingerprintEnrollEnrolling.this.onSkipButtonClick(view);
            }
        }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        ProgressBar progressBar = this.mProgressBar;
        LayerDrawable layerDrawable = progressBar != null ? (LayerDrawable) progressBar.getBackground() : null;
        if (layerDrawable != null) {
            this.mIconAnimationDrawable = (AnimatedVectorDrawable) layerDrawable.findDrawableByLayerId(R$id.fingerprint_animation);
            this.mIconBackgroundBlinksDrawable = (AnimatedVectorDrawable) layerDrawable.findDrawableByLayerId(R$id.fingerprint_background);
            this.mIconAnimationDrawable.registerAnimationCallback(this.mIconAnimationCallback);
        }
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563661);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(this, 17563663);
        ProgressBar progressBar2 = this.mProgressBar;
        if (progressBar2 != null) {
            progressBar2.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling$$ExternalSyntheticLambda1
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onCreate$0;
                    lambda$onCreate$0 = FingerprintEnrollEnrolling.this.lambda$onCreate$0(view, motionEvent);
                    return lambda$onCreate$0;
                }
            });
        }
        this.mRestoring = bundle != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            int i = this.mIconTouchCount + 1;
            this.mIconTouchCount = i;
            if (i == 3) {
                showIconTouchDialog();
            } else {
                this.mProgressBar.postDelayed(this.mShowDialogRunnable, 500L);
            }
        } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1) {
            this.mProgressBar.removeCallbacks(this.mShowDialogRunnable);
        }
        return true;
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected BiometricEnrollSidecar getSidecar() {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = new FingerprintEnrollSidecar();
        fingerprintEnrollSidecar.setEnrollReason(2);
        return fingerprintEnrollSidecar;
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected boolean shouldStartAutomatically() {
        if (this.mCanAssumeUdfps) {
            return this.mRestoring;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        updateProgress(false);
        updateTitleAndDescription();
        if (this.mRestoring) {
            startIconAnimation();
        }
    }

    @Override // android.app.Activity
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (this.mCanAssumeUdfps) {
            startEnrollment();
        }
        this.mAnimationCancelled = false;
        startIconAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startIconAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconAnimationDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
    }

    private void stopIconAnimation() {
        this.mAnimationCancelled = true;
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconAnimationDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.stop();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        stopIconAnimation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        stopListenOrientationEvent();
        super.onDestroy();
    }

    private void animateProgress(int i) {
        if (this.mCanAssumeUdfps) {
            if (i >= 10000) {
                getMainThreadHandler().postDelayed(this.mDelayedFinishRunnable, getFinishDelay());
                return;
            }
            return;
        }
        ObjectAnimator objectAnimator = this.mProgressAnim;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ProgressBar progressBar = this.mProgressBar;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), i);
        ofInt.addListener(this.mProgressAnimationListener);
        ofInt.setInterpolator(this.mFastOutSlowInInterpolator);
        ofInt.setDuration(250L);
        ofInt.start();
        this.mProgressAnim = ofInt;
    }

    private void animateFlash() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconBackgroundBlinksDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected Intent getFinishIntent() {
        return new Intent(this, FingerprintEnrollFinish.class);
    }

    private void updateTitleAndDescription() {
        if (this.mCanAssumeUdfps) {
            updateTitleAndDescriptionForUdfps();
            return;
        }
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1) {
            setDescriptionText(R$string.security_settings_fingerprint_enroll_start_message);
        } else {
            setDescriptionText(R$string.security_settings_fingerprint_enroll_repeat_message);
        }
    }

    private void updateTitleAndDescriptionForUdfps() {
        int currentStage = getCurrentStage();
        if (currentStage == 0) {
            setHeaderText(R$string.security_settings_fingerprint_enroll_repeat_title);
            setDescriptionText(R$string.security_settings_udfps_enroll_start_message);
        } else if (currentStage == 1) {
            setHeaderText(R$string.security_settings_fingerprint_enroll_repeat_title);
            if (this.mIsAccessibilityEnabled) {
                setDescriptionText(R$string.security_settings_udfps_enroll_repeat_a11y_message);
            } else {
                setDescriptionText(R$string.security_settings_udfps_enroll_repeat_message);
            }
        } else if (currentStage == 2) {
            setHeaderText(R$string.security_settings_udfps_enroll_fingertip_title);
            if (this.mHaveShownUdfpsTipLottie || this.mIllustrationLottie == null) {
                return;
            }
            this.mHaveShownUdfpsTipLottie = true;
            setDescriptionText("");
            this.mIllustrationLottie.setAnimation(R$raw.udfps_tip_hint_lottie);
            this.mIllustrationLottie.setVisibility(0);
            this.mIllustrationLottie.playAnimation();
            this.mIllustrationLottie.setContentDescription(getString(R$string.security_settings_udfps_tip_fingerprint_help));
        } else if (currentStage == 3) {
            setHeaderText(R$string.security_settings_udfps_enroll_left_edge_title);
            if (!this.mHaveShownUdfpsLeftEdgeLottie && this.mIllustrationLottie != null) {
                this.mHaveShownUdfpsLeftEdgeLottie = true;
                setDescriptionText("");
                this.mIllustrationLottie.setAnimation(R$raw.udfps_left_edge_hint_lottie);
                this.mIllustrationLottie.setVisibility(0);
                this.mIllustrationLottie.playAnimation();
                this.mIllustrationLottie.setContentDescription(getString(R$string.security_settings_udfps_side_fingerprint_help));
            } else if (this.mIllustrationLottie == null) {
                if (isStageHalfCompleted()) {
                    setDescriptionText(R$string.security_settings_fingerprint_enroll_repeat_message);
                } else {
                    setDescriptionText(R$string.security_settings_udfps_enroll_edge_message);
                }
            }
        } else if (currentStage == 4) {
            setHeaderText(R$string.security_settings_udfps_enroll_right_edge_title);
            if (!this.mHaveShownUdfpsRightEdgeLottie && this.mIllustrationLottie != null) {
                this.mHaveShownUdfpsRightEdgeLottie = true;
                setDescriptionText("");
                this.mIllustrationLottie.setAnimation(R$raw.udfps_right_edge_hint_lottie);
                this.mIllustrationLottie.setVisibility(0);
                this.mIllustrationLottie.playAnimation();
                this.mIllustrationLottie.setContentDescription(getString(R$string.security_settings_udfps_side_fingerprint_help));
            } else if (this.mIllustrationLottie == null) {
                if (isStageHalfCompleted()) {
                    setDescriptionText(R$string.security_settings_fingerprint_enroll_repeat_message);
                } else {
                    setDescriptionText(R$string.security_settings_udfps_enroll_edge_message);
                }
            }
        } else {
            getLayout().setHeaderText(R$string.security_settings_fingerprint_enroll_udfps_title);
            setDescriptionText(R$string.security_settings_udfps_enroll_start_message);
            String string = getString(R$string.security_settings_udfps_enroll_a11y);
            getLayout().getHeaderTextView().setContentDescription(string);
            setTitle(string);
        }
    }

    private int getCurrentStage() {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1) {
            return -1;
        }
        int enrollmentSteps = this.mSidecar.getEnrollmentSteps() - this.mSidecar.getEnrollmentRemaining();
        if (enrollmentSteps < getStageThresholdSteps(0)) {
            return 0;
        }
        if (enrollmentSteps < getStageThresholdSteps(1)) {
            return 1;
        }
        if (enrollmentSteps < getStageThresholdSteps(2)) {
            return 2;
        }
        return enrollmentSteps < getStageThresholdSteps(3) ? 3 : 4;
    }

    private boolean isStageHalfCompleted() {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1) {
            return false;
        }
        int enrollmentSteps = this.mSidecar.getEnrollmentSteps() - this.mSidecar.getEnrollmentRemaining();
        int i = 0;
        int i2 = 0;
        while (i < this.mFingerprintManager.getEnrollStageCount()) {
            int stageThresholdSteps = getStageThresholdSteps(i);
            if (enrollmentSteps >= i2 && enrollmentSteps < stageThresholdSteps) {
                return enrollmentSteps - i2 >= (stageThresholdSteps - i2) / 2;
            }
            i++;
            i2 = stageThresholdSteps;
        }
        return true;
    }

    private int getStageThresholdSteps(int i) {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1) {
            Log.w("FingerprintEnrollEnrolling", "getStageThresholdSteps: Enrollment not started yet");
            return 1;
        }
        return Math.round(this.mSidecar.getEnrollmentSteps() * this.mFingerprintManager.getEnrollStageThreshold(i));
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        if (!this.mCanAssumeUdfps) {
            this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
        }
        showError(charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        FingerprintErrorDialog.showErrorDialog(this, i);
        stopIconAnimation();
        if (this.mCanAssumeUdfps) {
            return;
        }
        this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        updateProgress(true);
        updateTitleAndDescription();
        clearError();
        animateFlash();
        if (!this.mCanAssumeUdfps) {
            this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
            this.mErrorText.postDelayed(this.mTouchAgainRunnable, 2500L);
        } else if (this.mIsAccessibilityEnabled) {
            String string = getString(R$string.security_settings_udfps_enroll_progress_a11y_message, new Object[]{Integer.valueOf((int) (((i - i2) / i) * 100.0f))});
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.setClassName(getClass().getName());
            obtain.setPackageName(getPackageName());
            obtain.getText().add(string);
            this.mAccessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    private void updateProgress(boolean z) {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || !biometricEnrollSidecar.isEnrolling()) {
            Log.d("FingerprintEnrollEnrolling", "Enrollment not started yet");
            return;
        }
        int progress = getProgress(this.mSidecar.getEnrollmentSteps(), this.mSidecar.getEnrollmentRemaining());
        if (z) {
            animateProgress(progress);
            return;
        }
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (progress >= 10000) {
            this.mDelayedFinishRunnable.run();
        }
    }

    private int getProgress(int i, int i2) {
        if (i == -1) {
            return 0;
        }
        int i3 = i + 1;
        return (Math.max(0, i3 - i2) * 10000) / i3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showIconTouchDialog() {
        this.mIconTouchCount = 0;
        new IconTouchDialog().show(getSupportFragmentManager(), (String) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showError(CharSequence charSequence) {
        if (this.mCanAssumeUdfps) {
            setHeaderText(charSequence);
            setDescriptionText("");
        } else {
            this.mErrorText.setText(charSequence);
            if (this.mErrorText.getVisibility() == 4) {
                this.mErrorText.setVisibility(0);
                this.mErrorText.setTranslationY(getResources().getDimensionPixelSize(R$dimen.fingerprint_error_text_appear_distance));
                this.mErrorText.setAlpha(0.0f);
                this.mErrorText.animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            } else {
                this.mErrorText.animate().cancel();
                this.mErrorText.setAlpha(1.0f);
                this.mErrorText.setTranslationY(0.0f);
            }
        }
        if (isResumed() && this.mIsAccessibilityEnabled && !this.mCanAssumeUdfps) {
            Vibrator vibrator = this.mVibrator;
            int myUid = Process.myUid();
            String opPackageName = getApplicationContext().getOpPackageName();
            VibrationEffect vibrationEffect = VIBRATE_EFFECT_ERROR;
            vibrator.vibrate(myUid, opPackageName, vibrationEffect, getClass().getSimpleName() + "::showError", FINGERPRINT_ENROLLING_SONFICATION_ATTRIBUTES);
        }
    }

    private void clearError() {
        if (this.mCanAssumeUdfps || this.mErrorText.getVisibility() != 0) {
            return;
        }
        this.mErrorText.animate().alpha(0.0f).translationY(getResources().getDimensionPixelSize(R$dimen.fingerprint_error_text_disappear_distance)).setDuration(100L).setInterpolator(this.mFastOutLinearInInterpolator).withEndAction(new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FingerprintEnrollEnrolling.this.lambda$clearError$1();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearError$1() {
        this.mErrorText.setVisibility(4);
    }

    private void listenOrientationEvent() {
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.1
            @Override // android.view.OrientationEventListener
            public void onOrientationChanged(int i) {
                int rotation = FingerprintEnrollEnrolling.this.getDisplay().getRotation();
                if ((FingerprintEnrollEnrolling.this.mPreviousRotation == 1 && rotation == 3) || (FingerprintEnrollEnrolling.this.mPreviousRotation == 3 && rotation == 1)) {
                    FingerprintEnrollEnrolling.this.mPreviousRotation = rotation;
                    FingerprintEnrollEnrolling.this.recreate();
                }
            }
        };
        this.mOrientationEventListener = orientationEventListener;
        orientationEventListener.enable();
        this.mPreviousRotation = getDisplay().getRotation();
    }

    private void stopListenOrientationEvent() {
        OrientationEventListener orientationEventListener = this.mOrientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        this.mOrientationEventListener = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getFinishDelay() {
        return this.mCanAssumeUdfps ? 400L : 250L;
    }

    private void updateOrientation(int i) {
        if (i == 1) {
            if (this.mShouldShowLottie) {
                this.mIllustrationLottie = (LottieAnimationView) findViewById(R$id.illustration_lottie);
            }
        } else if (i == 2) {
            this.mIllustrationLottie = null;
        } else {
            Log.e("FingerprintEnrollEnrolling", "Error unhandled configuration change");
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        int i = configuration.orientation;
        if (i == 1) {
            updateOrientation(1);
        } else if (i == 2) {
            updateOrientation(2);
        } else {
            Log.e("FingerprintEnrollEnrolling", "Error unhandled configuration change");
        }
    }

    /* loaded from: classes.dex */
    public static class IconTouchDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 568;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R$string.security_settings_fingerprint_enroll_touch_dialog_title).setMessage(R$string.security_settings_fingerprint_enroll_touch_dialog_message).setPositiveButton(R$string.security_settings_fingerprint_enroll_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.IconTouchDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return builder.create();
        }
    }
}
