package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintEnrollFindSensor extends BiometricEnrollBase implements BiometricEnrollSidecar.Listener {
    private FingerprintFindSensorAnimation mAnimation;
    private boolean mCanAssumeSidefps;
    private boolean mCanAssumeUdfps;
    private boolean mNextClicked;
    private OrientationEventListener mOrientationEventListener;
    private FingerprintEnrollSidecar mSidecar;
    private int mPreviousRotation = 0;
    private String mFingerprintLocation = SystemProperties.get("ro.boot.hardware");

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 241;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        List sensorPropertiesInternal = ((FingerprintManager) getSystemService(FingerprintManager.class)).getSensorPropertiesInternal();
        this.mCanAssumeUdfps = sensorPropertiesInternal != null && sensorPropertiesInternal.size() == 1 && ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnyUdfpsType();
        this.mCanAssumeSidefps = sensorPropertiesInternal != null && sensorPropertiesInternal.size() == 1 && ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnySidefpsType();
        setContentView(getContentView());
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_fingerprint_enroll_enrolling_skip).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FingerprintEnrollFindSensor.this.onSkipButtonClick(view);
            }
        }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        initFingerprintLocation();
        listenOrientationEvent();
        if (this.mCanAssumeUdfps) {
            setHeaderText(R$string.security_settings_udfps_enroll_find_sensor_title);
            setDescriptionText(R$string.security_settings_udfps_enroll_find_sensor_message);
            this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_udfps_enroll_find_sensor_start_button).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FingerprintEnrollFindSensor.this.onStartButtonClick(view);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
            LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R$id.illustration_lottie);
            if (((AccessibilityManager) getSystemService(AccessibilityManager.class)).isEnabled()) {
                lottieAnimationView.setAnimation(R$raw.udfps_edu_a11y_lottie);
            }
        } else if (this.mCanAssumeSidefps) {
            setHeaderText(R$string.security_settings_fingerprint_enroll_find_sensor_title);
            setDescriptionText(R$string.security_settings_fingerprint_enroll_find_sensor_message);
            LottieAnimationView lottieAnimationView2 = (LottieAnimationView) findViewById(R$id.illustration_lottie);
            LottieAnimationView lottieAnimationView3 = (LottieAnimationView) findViewById(R$id.illustration_lottie_portrait);
            int rotation = getApplicationContext().getDisplay().getRotation();
            if (rotation == 1) {
                lottieAnimationView2.setVisibility(8);
                lottieAnimationView3.setVisibility(0);
            } else if (rotation == 2) {
                lottieAnimationView2.setVisibility(0);
                lottieAnimationView2.setRotation(180.0f);
                lottieAnimationView3.setVisibility(8);
            } else if (rotation == 3) {
                lottieAnimationView2.setVisibility(8);
                lottieAnimationView3.setVisibility(0);
                lottieAnimationView3.setRotation(180.0f);
            } else {
                lottieAnimationView2.setVisibility(0);
                lottieAnimationView3.setVisibility(8);
            }
        } else {
            setDataIfLocatedNotOnBack();
        }
        if (this.mToken == null && BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            ((FingerprintManager) getSystemService(FingerprintManager.class)).generateChallenge(this.mUserId, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor$$ExternalSyntheticLambda2
                public final void onChallengeGenerated(int i, int i2, long j) {
                    FingerprintEnrollFindSensor.this.lambda$onCreate$0(i, i2, j);
                }
            });
        } else if (this.mToken != null) {
            startLookingForFingerprint();
        } else {
            throw new IllegalStateException("HAT and GkPwHandle both missing...");
        }
        this.mAnimation = null;
        if (this.mCanAssumeUdfps) {
            ((LottieAnimationView) findViewById(R$id.illustration_lottie)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    FingerprintEnrollFindSensor.this.onStartButtonClick(view);
                }
            });
        } else {
            initmAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, int i2, long j) {
        this.mChallenge = j;
        this.mSensorId = i;
        this.mToken = BiometricUtils.requestGatekeeperHat(this, getIntent(), this.mUserId, j);
        getIntent().putExtra("hw_auth_token", this.mToken);
        startLookingForFingerprint();
    }

    private void initmAnimation() {
        View findViewById = findViewById(getLocationViewIdAndHidenOthers());
        if (findViewById instanceof FingerprintFindSensorAnimation) {
            this.mAnimation = (FingerprintFindSensorAnimation) findViewById;
        }
    }

    private void setDataIfLocatedNotOnBack() {
        setHeaderText(R$string.security_settings_fingerprint_enroll_find_sensor_title);
        setDescriptionText(getFingerprintLocationMessage());
        if (isFingerprintLocatedOnBack()) {
            return;
        }
        ((ImageView) findViewById(R$id.fingerprint_sensor_location)).setImageResource(R$drawable.fingerprint_sensor_location_foreground);
    }

    private void initFingerprintLocation() {
        if (this.mFingerprintLocation.contains("9230") || this.mFingerprintLocation.contains("7885") || this.mFingerprintLocation.contains("9620") || this.mFingerprintLocation.contains("9621")) {
            this.mFingerprintLocation = "RIGHT";
        }
    }

    private boolean isFingerprintLocatedOnBack() {
        this.mFingerprintLocation.hashCode();
        return false;
    }

    private int getLocationViewIdAndHidenOthers() {
        String str = this.mFingerprintLocation;
        str.hashCode();
        if (str.equals("LEFT")) {
            int i = R$id.fingerprint_sensor_location_left_animation;
            findViewById(R$id.fingerprint_sensor_location_right_animation).setVisibility(8);
            findViewById(R$id.fingerprint_sensor_location_animation).setVisibility(4);
            return i;
        } else if (str.equals("RIGHT")) {
            int i2 = R$id.fingerprint_sensor_location_right_animation;
            findViewById(R$id.fingerprint_sensor_location_left_animation).setVisibility(8);
            findViewById(R$id.fingerprint_sensor_location_animation).setVisibility(4);
            return i2;
        } else {
            int i3 = R$id.fingerprint_sensor_location_animation;
            findViewById(R$id.fingerprint_sensor_location_right_animation).setVisibility(8);
            findViewById(R$id.fingerprint_sensor_location_left_animation).setVisibility(8);
            return i3;
        }
    }

    private int getFingerprintLocationMessage() {
        String str = this.mFingerprintLocation;
        str.hashCode();
        if (str.equals("LEFT")) {
            return R$string.security_settings_fingerprint_enroll_find_sensor_left;
        }
        if (str.equals("RIGHT")) {
            return R$string.security_settings_fingerprint_enroll_find_sensor_right;
        }
        return R$string.security_settings_fingerprint_enroll_find_sensor_message;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        stopLookingForFingerprint();
        super.onBackPressed();
    }

    protected int getContentView() {
        if (this.mCanAssumeUdfps) {
            return R$layout.udfps_enroll_find_sensor_layout;
        }
        if (this.mCanAssumeSidefps) {
            return R$layout.sfps_enroll_find_sensor_layout;
        }
        return R$layout.fingerprint_enroll_find_sensor;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.startAnimation();
        }
    }

    private void stopLookingForFingerprint() {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = this.mSidecar;
        if (fingerprintEnrollSidecar != null) {
            fingerprintEnrollSidecar.setListener(null);
            this.mSidecar.cancelEnrollment();
            getSupportFragmentManager().beginTransaction().remove(this.mSidecar).commitAllowingStateLoss();
            this.mSidecar = null;
        }
    }

    private void startLookingForFingerprint() {
        if (this.mCanAssumeUdfps) {
            return;
        }
        FingerprintEnrollSidecar fingerprintEnrollSidecar = (FingerprintEnrollSidecar) getSupportFragmentManager().findFragmentByTag("sidecar");
        this.mSidecar = fingerprintEnrollSidecar;
        if (fingerprintEnrollSidecar == null) {
            FingerprintEnrollSidecar fingerprintEnrollSidecar2 = new FingerprintEnrollSidecar();
            this.mSidecar = fingerprintEnrollSidecar2;
            fingerprintEnrollSidecar2.setEnrollReason(1);
            getSupportFragmentManager().beginTransaction().add(this.mSidecar, "sidecar").commitAllowingStateLoss();
        }
        this.mSidecar.setListener(this);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mNextClicked = true;
        proceedToEnrolling(true);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        if (this.mNextClicked && i == 5) {
            this.mNextClicked = false;
            proceedToEnrolling(false);
            return;
        }
        FingerprintErrorDialog.showErrorDialog(this, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.pauseAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mNextClicked;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        stopListenOrientationEvent();
        super.onDestroy();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.stopAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartButtonClick(View view) {
        startActivityForResult(getFingerprintEnrollingIntent(), 5);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        stopLookingForFingerprint();
        setResult(2);
        finish();
    }

    private void proceedToEnrolling(boolean z) {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = this.mSidecar;
        if (fingerprintEnrollSidecar != null) {
            if (z && fingerprintEnrollSidecar.cancelEnrollment()) {
                return;
            }
            getSupportFragmentManager().beginTransaction().remove(this.mSidecar).commitAllowingStateLoss();
            this.mSidecar = null;
            startActivityForResult(getFingerprintEnrollingIntent(), 5);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 4) {
            if (i2 == -1 && intent != null) {
                throw new IllegalStateException("Pretty sure this is dead code");
            }
            finish();
        } else if (i != 5) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == 1 || i2 == 2 || i2 == 3) {
            setResult(i2);
            finish();
        } else {
            FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
            if (fingerprintManagerOrNull.getEnrolledFingerprints().size() >= ((FingerprintSensorPropertiesInternal) fingerprintManagerOrNull.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser) {
                finish();
            } else {
                startLookingForFingerprint();
            }
        }
    }

    private void listenOrientationEvent() {
        if (this.mCanAssumeSidefps) {
            OrientationEventListener orientationEventListener = new OrientationEventListener(this) { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor.2
                @Override // android.view.OrientationEventListener
                public void onOrientationChanged(int i) {
                    int rotation = FingerprintEnrollFindSensor.this.getDisplay().getRotation();
                    if ((rotation + 2) % 4 == FingerprintEnrollFindSensor.this.mPreviousRotation) {
                        FingerprintEnrollFindSensor.this.mPreviousRotation = rotation;
                        FingerprintEnrollFindSensor.this.recreate();
                    }
                }
            };
            this.mOrientationEventListener = orientationEventListener;
            orientationEventListener.enable();
            this.mPreviousRotation = getDisplay().getRotation();
        }
    }

    private void stopListenOrientationEvent() {
        if (this.mCanAssumeSidefps) {
            OrientationEventListener orientationEventListener = this.mOrientationEventListener;
            if (orientationEventListener != null) {
                orientationEventListener.disable();
            }
            this.mOrientationEventListener = null;
        }
    }
}
