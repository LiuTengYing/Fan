package com.android.settings.biometrics;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricManager;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$anim;
import com.android.settings.R$integer;
import com.android.settings.R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;
/* loaded from: classes.dex */
public class BiometricEnrollActivity extends InstrumentedActivity {
    private boolean mConfirmingCredentials;
    private Long mGkPwHandle;
    private boolean mIsEnrollActionLogged;
    private MultiBiometricEnrollHelper mMultiBiometricEnrollHelper;
    private ParentalConsentHelper mParentalConsentHelper;
    private Bundle mParentalOptions;
    private int mUserId = UserHandle.myUserId();
    private boolean mHasFeatureFace = false;
    private boolean mHasFeatureFingerprint = false;
    private boolean mIsFaceEnrollable = false;
    private boolean mIsFingerprintEnrollable = false;
    private boolean mParentalOptionsRequired = false;
    private boolean mSkipReturnToParent = false;

    /* loaded from: classes.dex */
    public static final class InternalActivity extends BiometricEnrollActivity {
    }

    private static boolean isSuccessfulConfirmOrChooseCredential(int i, int i2) {
        return (i == 1 && i2 == 1) || (i == 2 && i2 == -1);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1586;
    }

    @Override // com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        int i;
        int i2;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (this instanceof InternalActivity) {
            this.mUserId = intent.getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
                this.mGkPwHandle = Long.valueOf(BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
            }
        }
        if (bundle != null) {
            this.mConfirmingCredentials = bundle.getBoolean("confirming_credentials", false);
            this.mIsEnrollActionLogged = bundle.getBoolean("enroll_action_logged", false);
            this.mParentalOptions = bundle.getBundle("enroll_preferences");
            if (bundle.containsKey("gk_pw_handle")) {
                this.mGkPwHandle = Long.valueOf(bundle.getLong("gk_pw_handle"));
            }
        }
        if (!this.mIsEnrollActionLogged && "android.settings.BIOMETRIC_ENROLL".equals(intent.getAction())) {
            this.mIsEnrollActionLogged = true;
            BiometricManager biometricManager = (BiometricManager) getSystemService(BiometricManager.class);
            int i3 = 12;
            if (biometricManager != null) {
                i3 = biometricManager.canAuthenticate(15);
                i2 = biometricManager.canAuthenticate(255);
                i = biometricManager.canAuthenticate(32768);
            } else {
                i = 12;
                i2 = 12;
            }
            FrameworkStatsLog.write(355, i3 == 0, i2 == 0, i == 0, intent.hasExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED"), intent.getIntExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED", 0));
        }
        if (intent.getStringExtra("theme") == null) {
            intent.putExtra("theme", SetupWizardUtils.getThemeString(intent));
        }
        PackageManager packageManager = getApplicationContext().getPackageManager();
        this.mHasFeatureFingerprint = packageManager.hasSystemFeature("android.hardware.fingerprint");
        this.mHasFeatureFace = packageManager.hasSystemFeature("android.hardware.biometrics.face");
        int intExtra = getIntent().getIntExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED", 255);
        Log.d("BiometricEnrollActivity", "Authenticators: " + intExtra);
        this.mParentalOptionsRequired = intent.getBooleanExtra("require_consent", false);
        this.mSkipReturnToParent = intent.getBooleanExtra("skip_return_to_parent", false);
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        boolean z = this.mHasFeatureFace && this.mHasFeatureFingerprint;
        Log.d("BiometricEnrollActivity", "parentalOptionsRequired: " + this.mParentalOptionsRequired + ", skipReturnToParent: " + this.mSkipReturnToParent + ", isSetupWizard: " + isAnySetupWizard + ", isMultiSensor: " + z);
        if (this.mHasFeatureFace) {
            FaceManager faceManager = (FaceManager) getSystemService(FaceManager.class);
            List sensorPropertiesInternal = faceManager.getSensorPropertiesInternal();
            int integer = getApplicationContext().getResources().getInteger(R$integer.suw_max_faces_enrollable);
            if (!sensorPropertiesInternal.isEmpty()) {
                FaceSensorPropertiesInternal faceSensorPropertiesInternal = (FaceSensorPropertiesInternal) sensorPropertiesInternal.get(0);
                if (!isAnySetupWizard) {
                    integer = faceSensorPropertiesInternal.maxEnrollmentsPerUser;
                }
                this.mIsFaceEnrollable = faceManager.getEnrolledFaces(this.mUserId).size() < integer;
                if ((isAnySetupWizard || (this.mParentalOptionsRequired && !WizardManagerHelper.isUserSetupComplete(this))) && z && this.mIsFaceEnrollable && faceSensorPropertiesInternal.sensorStrength == 0) {
                    Log.i("BiometricEnrollActivity", "Excluding face from SuW enrollment (STRENGTH_CONVENIENCE)");
                    this.mIsFaceEnrollable = false;
                }
            }
        }
        if (this.mHasFeatureFingerprint) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FingerprintManager.class);
            List sensorPropertiesInternal2 = fingerprintManager.getSensorPropertiesInternal();
            int integer2 = getApplicationContext().getResources().getInteger(R$integer.suw_max_fingerprints_enrollable);
            if (!sensorPropertiesInternal2.isEmpty()) {
                if (!isAnySetupWizard) {
                    integer2 = ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal2.get(0)).maxEnrollmentsPerUser;
                }
                this.mIsFingerprintEnrollable = fingerprintManager.getEnrolledFingerprints(this.mUserId).size() < integer2;
            }
        }
        if (isAnySetupWizard && this.mParentalOptionsRequired) {
            Log.w("BiometricEnrollActivity", "Enrollment with parental consent is not supported when launched  directly from SuW - skipping enrollment");
            setResult(2);
            finish();
            return;
        }
        if (isAnySetupWizard && this.mParentalOptionsRequired) {
            if (ParentalControlsUtils.parentConsentRequired(this, 10) != null) {
                Log.w("BiometricEnrollActivity", "Consent was already setup - skipping enrollment");
                setResult(2);
                finish();
                return;
            }
        }
        if (this.mParentalOptionsRequired && this.mParentalOptions == null) {
            this.mParentalConsentHelper = new ParentalConsentHelper(this.mGkPwHandle);
            setOrConfirmCredentialsNow();
            return;
        }
        startEnrollWith(intExtra, isAnySetupWizard);
    }

    private void startEnrollWith(@BiometricManager.Authenticators.Types int i, boolean z) {
        int canAuthenticate;
        if (!z && !this.mParentalOptionsRequired && (canAuthenticate = ((BiometricManager) getSystemService(BiometricManager.class)).canAuthenticate(i)) != 11) {
            Log.e("BiometricEnrollActivity", "Unexpected result (has enrollments): " + canAuthenticate);
            finish();
            return;
        }
        boolean z2 = this.mHasFeatureFace;
        boolean z3 = this.mHasFeatureFingerprint;
        if (this.mParentalOptionsRequired) {
            Bundle bundle = this.mParentalOptions;
            if (bundle == null) {
                throw new IllegalStateException("consent options required, but not set");
            }
            z2 = z2 && ParentalConsentHelper.hasFaceConsent(bundle);
            z3 = z3 && ParentalConsentHelper.hasFingerprintConsent(this.mParentalOptions);
        }
        if (!z && i == 32768) {
            launchCredentialOnlyEnroll();
            finish();
        } else if (z2 && z3) {
            if (this.mGkPwHandle != null) {
                launchFaceAndFingerprintEnroll();
            } else {
                setOrConfirmCredentialsNow();
            }
        } else if (z3) {
            launchFingerprintOnlyEnroll();
        } else if (z2) {
            launchFaceOnlyEnroll();
        } else {
            if (this.mParentalOptionsRequired) {
                Log.d("BiometricEnrollActivity", "No consent for any modality: skipping enrollment");
                setResult(-1, newResultIntent());
            } else {
                Log.e("BiometricEnrollActivity", "Unknown state, finishing (was SUW: " + z + ")");
            }
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("confirming_credentials", this.mConfirmingCredentials);
        bundle.putBoolean("enroll_action_logged", this.mIsEnrollActionLogged);
        Bundle bundle2 = this.mParentalOptions;
        if (bundle2 != null) {
            bundle.putBundle("enroll_preferences", bundle2);
        }
        Long l = this.mGkPwHandle;
        if (l != null) {
            bundle.putLong("gk_pw_handle", l.longValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.mParentalConsentHelper != null) {
            boolean z = true;
            boolean z2 = ParentalControlsUtils.parentConsentRequired(this, 8) != null;
            boolean z3 = ParentalControlsUtils.parentConsentRequired(this, 2) != null;
            boolean z4 = z2 && this.mHasFeatureFace && this.mIsFaceEnrollable;
            if (!z3 || !this.mHasFeatureFingerprint) {
                z = false;
            }
            Log.d("BiometricEnrollActivity", "faceConsentRequired: " + z2 + ", fpConsentRequired: " + z3 + ", hasFeatureFace: " + this.mHasFeatureFace + ", hasFeatureFingerprint: " + this.mHasFeatureFingerprint + ", faceEnrollable: " + this.mIsFaceEnrollable + ", fpEnrollable: " + this.mIsFingerprintEnrollable);
            this.mParentalConsentHelper.setConsentRequirement(z4, z);
            handleOnActivityResultWhileConsenting(i, i2, intent);
            return;
        }
        handleOnActivityResultWhileEnrolling(i, i2, intent);
    }

    private void handleOnActivityResultWhileConsenting(int i, int i2, Intent intent) {
        overridePendingTransition(R$anim.sud_slide_next_in, R$anim.sud_slide_next_out);
        if (i == 1 || i == 2) {
            this.mConfirmingCredentials = false;
            if (isSuccessfulConfirmOrChooseCredential(i, i2)) {
                updateGatekeeperPasswordHandle(intent);
                if (this.mParentalConsentHelper.launchNext(this, 3)) {
                    return;
                }
                Log.e("BiometricEnrollActivity", "Nothing to prompt for consent (no modalities enabled)!");
                finish();
                return;
            }
            Log.d("BiometricEnrollActivity", "Unknown result for set/choose lock: " + i2);
            setResult(i2);
            finish();
        } else if (i != 3) {
            Log.w("BiometricEnrollActivity", "Unknown consenting requestCode: " + i + ", finishing");
            finish();
        } else if (i2 == 4 || i2 == 5) {
            if (this.mParentalConsentHelper.launchNext(this, 3, i2, intent)) {
                return;
            }
            this.mParentalOptions = this.mParentalConsentHelper.getConsentResult();
            this.mParentalConsentHelper = null;
            Log.d("BiometricEnrollActivity", "Enrollment consent options set, starting enrollment: " + this.mParentalOptions);
            startEnrollWith(4095, WizardManagerHelper.isAnySetupWizard(getIntent()));
        } else {
            Log.d("BiometricEnrollActivity", "Unknown or cancelled parental consent");
            setResult(0);
            finish();
        }
    }

    private void handleOnActivityResultWhileEnrolling(int i, int i2, Intent intent) {
        if (i == 4) {
            Log.d("BiometricEnrollActivity", "Enrollment complete, requesting handoff, result: " + i2);
            setResult(-1, newResultIntent());
            finish();
        } else if (this.mMultiBiometricEnrollHelper == null) {
            overridePendingTransition(R$anim.sud_slide_next_in, R$anim.sud_slide_next_out);
            if (i != 1 && i != 2) {
                if (i == 5) {
                    finishOrLaunchHandToParent(i2);
                    return;
                }
                Log.w("BiometricEnrollActivity", "Unknown enrolling requestCode: " + i + ", finishing");
                finish();
                return;
            }
            this.mConfirmingCredentials = false;
            if (isSuccessfulConfirmOrChooseCredential(i, i2) && this.mHasFeatureFace && this.mHasFeatureFingerprint) {
                updateGatekeeperPasswordHandle(intent);
                launchFaceAndFingerprintEnroll();
                return;
            }
            Log.d("BiometricEnrollActivity", "Unknown result for set/choose lock: " + i2);
            setResult(i2);
            finish();
        } else {
            Log.d("BiometricEnrollActivity", "RequestCode: " + i + " resultCode: " + i2);
            BiometricUtils.removeGatekeeperPasswordHandle(this, this.mGkPwHandle.longValue());
            finishOrLaunchHandToParent(i2);
        }
    }

    private void finishOrLaunchHandToParent(int i) {
        if (this.mParentalOptionsRequired) {
            if (!this.mSkipReturnToParent) {
                launchHandoffToParent();
                return;
            }
            setResult(-1, newResultIntent());
            finish();
            return;
        }
        setResult(i);
        finish();
    }

    private Intent newResultIntent() {
        Intent intent = new Intent();
        Bundle deepCopy = this.mParentalOptions.deepCopy();
        intent.putExtra("consent_status", deepCopy);
        Log.v("BiometricEnrollActivity", "Result consent status: " + deepCopy);
        return intent;
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        int theme2 = SetupWizardUtils.getTheme(this, getIntent());
        theme.applyStyle(R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, theme2, z);
    }

    private void setOrConfirmCredentialsNow() {
        if (this.mConfirmingCredentials) {
            return;
        }
        this.mConfirmingCredentials = true;
        if (!userHasPassword(this.mUserId)) {
            launchChooseLock();
        } else {
            launchConfirmLock();
        }
    }

    private void updateGatekeeperPasswordHandle(Intent intent) {
        this.mGkPwHandle = Long.valueOf(BiometricUtils.getGatekeeperPasswordHandle(intent));
        ParentalConsentHelper parentalConsentHelper = this.mParentalConsentHelper;
        if (parentalConsentHelper != null) {
            parentalConsentHelper.updateGatekeeperHandle(intent);
        }
    }

    private boolean userHasPassword(int i) {
        return new LockPatternUtils(this).getActivePasswordQuality(((UserManager) getSystemService(UserManager.class)).getCredentialOwnerProfile(i)) != 0;
    }

    private void launchChooseLock() {
        Log.d("BiometricEnrollActivity", "launchChooseLock");
        Intent chooseLockIntent = BiometricUtils.getChooseLockIntent(this, getIntent());
        chooseLockIntent.putExtra("hide_insecure_options", true);
        chooseLockIntent.putExtra("request_gk_pw_handle", true);
        chooseLockIntent.putExtra("for_biometrics", true);
        int i = this.mUserId;
        if (i != -10000) {
            chooseLockIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        startActivityForResult(chooseLockIntent, 1);
    }

    private void launchConfirmLock() {
        Log.d("BiometricEnrollActivity", "launchConfirmLock");
        ChooseLockSettingsHelper.Builder builder = new ChooseLockSettingsHelper.Builder(this);
        builder.setRequestCode(2).setRequestGatekeeperPasswordHandle(true).setForegroundOnly(true).setReturnCredentials(true);
        int i = this.mUserId;
        if (i != -10000) {
            builder.setUserId(i);
        }
        if (builder.show()) {
            return;
        }
        finish();
    }

    private void launchSingleSensorEnrollActivity(Intent intent, int i) {
        BiometricUtils.launchEnrollForResult(this, intent, i, this instanceof InternalActivity ? getIntent().getByteArrayExtra("hw_auth_token") : null, this.mGkPwHandle, this.mUserId);
    }

    private void launchCredentialOnlyEnroll() {
        Intent intent = new Intent(this, ChooseLockGeneric.class);
        intent.putExtra("hide_insecure_options", true);
        launchSingleSensorEnrollActivity(intent, 0);
    }

    private void launchFingerprintOnlyEnroll() {
        Intent fingerprintIntroIntent;
        if (getIntent().getBooleanExtra("skip_intro", false) && (this instanceof InternalActivity)) {
            fingerprintIntroIntent = BiometricUtils.getFingerprintFindSensorIntent(this, getIntent());
        } else {
            fingerprintIntroIntent = BiometricUtils.getFingerprintIntroIntent(this, getIntent());
        }
        launchSingleSensorEnrollActivity(fingerprintIntroIntent, 5);
    }

    private void launchFaceOnlyEnroll() {
        launchSingleSensorEnrollActivity(BiometricUtils.getFaceIntroIntent(this, getIntent()), 5);
    }

    private void launchFaceAndFingerprintEnroll() {
        MultiBiometricEnrollHelper multiBiometricEnrollHelper = new MultiBiometricEnrollHelper(this, this.mUserId, this.mIsFaceEnrollable, this.mIsFingerprintEnrollable, this.mGkPwHandle.longValue());
        this.mMultiBiometricEnrollHelper = multiBiometricEnrollHelper;
        multiBiometricEnrollHelper.startNextStep();
    }

    private void launchHandoffToParent() {
        startActivityForResult(BiometricUtils.getHandoffToParentIntent(this, getIntent()), 4);
    }
}
