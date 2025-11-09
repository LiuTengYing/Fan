package com.android.settings.biometrics.face;

import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricErrorDialog;
import com.android.settings.biometrics.BiometricsEnrollEnrolling;
import com.android.settings.biometrics.face.ParticleCollection;
import com.android.settings.slices.CustomSliceRegistry;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FaceEnrollEnrolling extends BiometricsEnrollEnrolling {
    private TextView mErrorText;
    private FaceManager mFaceManager;
    private TextView mHelpText;
    private Interpolator mLinearOutSlowInInterpolator;
    private FaceEnrollPreviewFragment mPreviewFragment;
    private ArrayList<Integer> mDisabledFeatures = new ArrayList<>();
    private ParticleCollection.Listener mListener = new ParticleCollection.Listener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling.1
        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            FaceEnrollEnrolling faceEnrollEnrolling = FaceEnrollEnrolling.this;
            faceEnrollEnrolling.launchFinish(((BiometricEnrollBase) faceEnrollEnrolling).mToken);
        }
    };
    private StartedListener mStartedListener = new StartedListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling.2
        @Override // com.android.settings.biometrics.face.FaceEnrollEnrolling.StartedListener
        public void onEnrollStarted() {
            Log.d("FaceEnrollEnrolling", "onEnrollStarted");
            FaceEnrollEnrolling.this.startEnrollment();
        }
    };
    private StopedListener mStopdListener = new StopedListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling.3
        @Override // com.android.settings.biometrics.face.FaceEnrollEnrolling.StopedListener
        public void onEnrollStoped() {
            Log.d("FaceEnrollEnrolling", "onEnrollStoped");
            FaceEnrollEnrolling.this.stopEnrollment();
        }
    };

    /* loaded from: classes.dex */
    public interface StartedListener {
        void onEnrollStarted();
    }

    /* loaded from: classes.dex */
    public interface StopedListener {
        void onEnrollStoped();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1507;
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected boolean shouldStartAutomatically() {
        return false;
    }

    /* loaded from: classes.dex */
    public static class FaceErrorDialog extends BiometricErrorDialog {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 1510;
        }

        static FaceErrorDialog newInstance(CharSequence charSequence, int i) {
            FaceErrorDialog faceErrorDialog = new FaceErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("error_msg", charSequence);
            bundle.putInt("error_id", i);
            faceErrorDialog.setArguments(bundle);
            return faceErrorDialog;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getTitleResId() {
            return R$string.security_settings_face_enroll_error_dialog_title;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getOkButtonTextResId() {
            return R$string.security_settings_face_enroll_dialog_ok;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isInMultiWindowMode()) {
            Toast.makeText(getApplicationContext(), R$string.not_support_in_split_mode, 0).show();
            finish();
            return;
        }
        setContentView(R$layout.face_enroll_enrolling);
        setHeaderText(R$string.security_settings_face_enroll_repeat_title);
        this.mErrorText = (TextView) findViewById(R$id.error_text);
        this.mHelpText = (TextView) findViewById(R$id.help_text);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563662);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_face_enroll_introduction_cancel).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollEnrolling.this.onSkipButtonClick(view);
            }
        }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        if (!getIntent().getBooleanExtra("accessibility_diversity", true)) {
            this.mDisabledFeatures.add(2);
        }
        if (!getIntent().getBooleanExtra("accessibility_vision", true)) {
            this.mDisabledFeatures.add(1);
        }
        FaceManager faceManager = (FaceManager) getSystemService(FaceManager.class);
        this.mFaceManager = faceManager;
        List enrolledFaces = faceManager.getEnrolledFaces(this.mUserId);
        if (!enrolledFaces.isEmpty()) {
            Log.d("FaceEnrollEnrolling", "face already enrolled start deleting..");
            this.mFaceManager.remove((Face) enrolledFaces.get(0), this.mUserId, (FaceManager.RemovalCallback) null);
        }
        startPreviewFrame();
    }

    private void startPreviewFrame() {
        Log.d("FaceEnrollEnrolling", "startPreviewFrame");
        FaceEnrollPreviewFragment faceEnrollPreviewFragment = (FaceEnrollPreviewFragment) getSupportFragmentManager().findFragmentByTag("tag_preview");
        this.mPreviewFragment = faceEnrollPreviewFragment;
        if (faceEnrollPreviewFragment == null) {
            this.mPreviewFragment = new FaceEnrollPreviewFragment();
            getSupportFragmentManager().beginTransaction().add(this.mPreviewFragment, "tag_preview").commitAllowingStateLoss();
        }
        this.mPreviewFragment.setListener(this.mListener);
        this.mPreviewFragment.setStartedListener(this.mStartedListener);
        this.mPreviewFragment.setStopedListener(this.mStopdListener);
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void startEnrollment() {
        FaceEnrollPreviewFragment faceEnrollPreviewFragment;
        super.startEnrollment();
        Log.d("FaceEnrollEnrolling", "startEnrollment");
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || (faceEnrollPreviewFragment = this.mPreviewFragment) == null) {
            return;
        }
        ((FaceEnrollSidecar) biometricEnrollSidecar).setSurfaceTexture(faceEnrollPreviewFragment.getPreviewSurface());
    }

    public void stopEnrollment() {
        Log.d("FaceEnrollEnrolling", "stopEnrollment");
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || this.mPreviewFragment == null) {
            return;
        }
        biometricEnrollSidecar.setListener(null);
        this.mSidecar.cancelEnrollment();
        getSupportFragmentManager().beginTransaction().remove(this.mSidecar).commitAllowingStateLoss();
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected Intent getFinishIntent() {
        return new Intent(this, FaceEnrollFinish.class);
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected BiometricEnrollSidecar getSidecar() {
        int[] iArr = new int[this.mDisabledFeatures.size()];
        for (int i = 0; i < this.mDisabledFeatures.size(); i++) {
            iArr[i] = this.mDisabledFeatures.get(i).intValue();
        }
        return new FaceEnrollSidecar(iArr);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        Log.v("FaceEnrollEnrolling", "onEnrollmentHelp helpMsgId: " + i + " helpString: " + ((Object) charSequence));
        if (!TextUtils.isEmpty(charSequence)) {
            showHelp(charSequence);
        }
        this.mPreviewFragment.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        int i2;
        Log.v("FaceEnrollEnrolling", "onEnrollmentError errMsgId: " + i + " errString: " + ((Object) charSequence));
        if (i == 3) {
            i2 = R$string.security_settings_face_enroll_error_timeout_dialog_message;
        } else if (i == 1004) {
            Intent intent = new Intent(this, FaceEnrollIntroduction.class);
            intent.putExtra("reenroll_token", true);
            intent.addFlags(67108864);
            startActivity(intent);
            finish();
            return;
        } else {
            i2 = R$string.security_settings_face_enroll_error_generic_dialog_message;
        }
        this.mPreviewFragment.onEnrollmentError(i, charSequence);
        showErrorDialog(getText(i2), i);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        Log.v("FaceEnrollEnrolling", "Steps: " + i + " Remaining: " + i2);
        this.mPreviewFragment.onEnrollmentProgressChange(i, i2);
        showError(String.format(getText(R$string.face_data_remaining).toString(), Integer.valueOf(i2)));
        if (i2 == 0) {
            getApplicationContext().getContentResolver().notifyChange(CustomSliceRegistry.FACE_ENROLL_SLICE_URI, null);
            launchFinish(this.mToken);
        }
    }

    private void showErrorDialog(CharSequence charSequence, int i) {
        FaceErrorDialog.newInstance(charSequence, i).show(getSupportFragmentManager(), FaceErrorDialog.class.getName());
    }

    private void showHelp(CharSequence charSequence) {
        this.mHelpText.setText(charSequence);
        if (this.mHelpText.getVisibility() == 4) {
            this.mHelpText.setVisibility(0);
            this.mHelpText.setTranslationY(getResources().getDimensionPixelSize(R$dimen.fingerprint_error_text_appear_distance));
            this.mHelpText.setAlpha(0.0f);
            this.mHelpText.animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            return;
        }
        this.mHelpText.animate().cancel();
        this.mHelpText.setAlpha(1.0f);
        this.mHelpText.setTranslationY(0.0f);
    }

    private void showError(CharSequence charSequence) {
        this.mErrorText.setText(charSequence);
        if (this.mErrorText.getVisibility() == 4) {
            this.mErrorText.setVisibility(0);
            this.mErrorText.setTranslationY(getResources().getDimensionPixelSize(R$dimen.fingerprint_error_text_appear_distance));
            this.mErrorText.setAlpha(0.0f);
            this.mErrorText.animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            return;
        }
        this.mErrorText.animate().cancel();
        this.mErrorText.setAlpha(1.0f);
        this.mErrorText.setTranslationY(0.0f);
    }
}
