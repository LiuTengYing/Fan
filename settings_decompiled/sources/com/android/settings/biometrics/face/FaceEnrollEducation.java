package com.android.settings.biometrics.face;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R$bool;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.view.IllustrationVideoView;
/* loaded from: classes.dex */
public class FaceEnrollEducation extends BiometricEnrollBase {
    private boolean mAccessibilityEnabled;
    private FaceManager mFaceManager;
    private View mIllustrationAccessibility;
    private IllustrationVideoView mIllustrationDefault;
    private LottieAnimationView mIllustrationLottie;
    private boolean mIsUsingLottie;
    private boolean mNextClicked;
    private Intent mResultIntent;
    private FaceEnrollAccessibilityToggle mSwitchDiversity;
    private final CompoundButton.OnCheckedChangeListener mSwitchDiversityListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation.1
        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            int i;
            if (z) {
                i = R$string.security_settings_face_enroll_education_message_accessibility;
            } else {
                i = R$string.security_settings_face_enroll_education_message;
            }
            FaceEnrollEducation.this.setDescriptionText(i);
            if (z) {
                FaceEnrollEducation.this.hideDefaultIllustration();
                FaceEnrollEducation.this.mIllustrationAccessibility.setVisibility(0);
                return;
            }
            FaceEnrollEducation.this.showDefaultIllustration();
            FaceEnrollEducation.this.mIllustrationAccessibility.setVisibility(4);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1506;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.face_enroll_education);
        setTitle(R$string.security_settings_face_enroll_education_title);
        setDescriptionText(R$string.security_settings_face_enroll_education_message);
        this.mFaceManager = Utils.getFaceManagerOrNull(this);
        this.mIllustrationDefault = (IllustrationVideoView) findViewById(R$id.illustration_default);
        this.mIllustrationLottie = (LottieAnimationView) findViewById(R$id.illustration_lottie);
        this.mIllustrationAccessibility = findViewById(R$id.illustration_accessibility);
        boolean z = getResources().getBoolean(R$bool.config_face_education_use_lottie);
        this.mIsUsingLottie = z;
        boolean z2 = false;
        if (z) {
            this.mIllustrationDefault.stop();
            this.mIllustrationDefault.setVisibility(4);
            this.mIllustrationLottie.setAnimation(R$raw.face_education_lottie);
            this.mIllustrationLottie.setVisibility(0);
            this.mIllustrationLottie.playAnimation();
        }
        this.mFooterBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            this.mFooterBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.skip_label).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FaceEnrollEducation.this.onSkipButtonClick(view);
                }
            }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        } else {
            this.mFooterBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_face_enroll_introduction_cancel).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FaceEnrollEducation.this.onSkipButtonClick(view);
                }
            }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        }
        final FooterButton build = new FooterButton.Builder(this).setText(R$string.security_settings_face_enroll_education_start).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollEducation.this.onNextButtonClick(view);
            }
        }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build();
        AccessibilityManager accessibilityManager = (AccessibilityManager) getApplicationContext().getSystemService(AccessibilityManager.class);
        if (accessibilityManager != null) {
            if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()) {
                z2 = true;
            }
            this.mAccessibilityEnabled = z2;
        }
        this.mFooterBarMixin.setPrimaryButton(build);
        final Button button = (Button) findViewById(R$id.accessibility_button);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollEducation.this.lambda$onCreate$0(build, button, view);
            }
        });
        FaceEnrollAccessibilityToggle faceEnrollAccessibilityToggle = (FaceEnrollAccessibilityToggle) findViewById(R$id.toggle_diversity);
        this.mSwitchDiversity = faceEnrollAccessibilityToggle;
        faceEnrollAccessibilityToggle.setListener(this.mSwitchDiversityListener);
        this.mSwitchDiversity.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollEducation.this.lambda$onCreate$1(view);
            }
        });
        if (this.mAccessibilityEnabled) {
            button.callOnClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(FooterButton footerButton, Button button, View view) {
        footerButton.setEnabled(true);
        this.mSwitchDiversity.setChecked(true);
        button.setVisibility(8);
        this.mSwitchDiversity.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        this.mSwitchDiversity.getSwitch().toggle();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mFaceManager.getEnrolledFaces(this.mUserId).size() >= getResources().getInteger(17694831)) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mNextClicked;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        final Intent intent = new Intent();
        byte[] bArr = this.mToken;
        if (bArr != null) {
            intent.putExtra("hw_auth_token", bArr);
        }
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        intent.putExtra("challenge", this.mChallenge);
        intent.putExtra("sensor_id", this.mSensorId);
        intent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        BiometricUtils.copyMultiBiometricExtras(getIntent(), intent);
        String string = getString(R$string.config_face_enroll);
        if (!TextUtils.isEmpty(string)) {
            intent.setComponent(ComponentName.unflattenFromString(string));
        } else {
            intent.setClass(this, FaceEnrollEnrolling.class);
        }
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        Intent intent2 = this.mResultIntent;
        if (intent2 != null) {
            intent.putExtras(intent2);
        }
        intent.putExtra("accessibility_diversity", !this.mSwitchDiversity.isChecked());
        if (!this.mSwitchDiversity.isChecked() && this.mAccessibilityEnabled) {
            FaceEnrollAccessibilityDialog newInstance = FaceEnrollAccessibilityDialog.newInstance();
            newInstance.setPositiveButtonListener(new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEducation$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    FaceEnrollEducation.this.lambda$onNextButtonClick$2(intent, dialogInterface, i2);
                }
            });
            newInstance.show(getSupportFragmentManager(), FaceEnrollAccessibilityDialog.class.getName());
            return;
        }
        startActivityForResult(intent, 2);
        this.mNextClicked = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onNextButtonClick$2(Intent intent, DialogInterface dialogInterface, int i) {
        startActivityForResult(intent, 2);
        this.mNextClicked = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        if (BiometricUtils.tryStartingNextBiometricEnroll(this, 6, "edu_skip")) {
            return;
        }
        setResult(2);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.mResultIntent = intent;
        if (i2 == 3) {
            setResult(i2, intent);
            finish();
        } else if (i == 2 || i == 6) {
            if (i2 == 2 || i2 == 1 || i2 == 11) {
                setResult(i2, intent);
                finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideDefaultIllustration() {
        if (this.mIsUsingLottie) {
            this.mIllustrationLottie.cancelAnimation();
            this.mIllustrationLottie.setVisibility(4);
            return;
        }
        this.mIllustrationDefault.stop();
        this.mIllustrationDefault.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDefaultIllustration() {
        if (this.mIsUsingLottie) {
            this.mIllustrationLottie.setVisibility(0);
            this.mIllustrationLottie.playAnimation();
            return;
        }
        this.mIllustrationDefault.setVisibility(0);
        this.mIllustrationDefault.start();
    }
}
