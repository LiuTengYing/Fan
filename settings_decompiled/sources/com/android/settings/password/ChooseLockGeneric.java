package com.android.settings.password;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.service.persistentdata.PersistentDataBlockManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.EncryptionInterstitial;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockGenericController;
import com.android.settings.password.ChooseLockPassword;
import com.android.settings.password.ChooseLockPattern;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.safetycenter.LockScreenSafetySource;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.widget.FooterPreference;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ChooseLockGeneric extends SettingsActivity {

    /* loaded from: classes.dex */
    public static class InternalActivity extends ChooseLockGeneric {
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ChooseLockGenericFragment.class.getName().equals(str);
    }

    Class<? extends Fragment> getFragmentClass() {
        return ChooseLockGenericFragment.class;
    }

    /* loaded from: classes.dex */
    public static class ChooseLockGenericFragment extends SettingsPreferenceFragment {
        static final int CHOOSE_LOCK_BEFORE_BIOMETRIC_REQUEST = 103;
        static final int CHOOSE_LOCK_REQUEST = 102;
        static final int CONFIRM_EXISTING_REQUEST = 100;
        static final int ENABLE_ENCRYPTION_REQUEST = 101;
        static final int SKIP_FINGERPRINT_REQUEST = 104;
        private ChooseLockGenericController mController;
        private DevicePolicyManager mDpm;
        private FaceManager mFaceManager;
        private FingerprintManager mFingerprintManager;
        private boolean mIsCallingAppAdmin;
        private boolean mIsManagedProfile;
        private LockPatternUtils mLockPatternUtils;
        private ManagedLockPasswordProvider mManagedPasswordProvider;
        private int mRequestedMinComplexity;
        private LockscreenCredential mUnificationProfileCredential;
        private int mUserId;
        private UserManager mUserManager;
        private LockscreenCredential mUserPassword;
        private boolean mRequestGatekeeperPasswordHandle = false;
        private boolean mPasswordConfirmed = false;
        private boolean mWaitingForConfirmation = false;
        private boolean mForChangeCredRequiredForBoot = false;
        private boolean mIsSetNewPassword = false;
        private int mUnificationProfileId = -10000;
        private String mCallerAppName = null;
        protected boolean mForFingerprint = false;
        protected boolean mForFace = false;
        protected boolean mForBiometrics = false;
        private boolean mOnlyEnforceDevicePasswordRequirement = false;

        protected boolean alwaysHideInsecureScreenLockTypes() {
            return false;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 27;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            Bundle arguments = getArguments();
            if (!WizardManagerHelper.isDeviceProvisioned(activity) && !canRunBeforeDeviceProvisioned()) {
                Log.i("ChooseLockGenericFragment", "Refusing to start because device is not provisioned");
                activity.finish();
                return;
            }
            Intent intent = activity.getIntent();
            String action = intent.getAction();
            this.mFingerprintManager = Utils.getFingerprintManagerOrNull(activity);
            this.mFaceManager = Utils.getFaceManagerOrNull(activity);
            this.mDpm = (DevicePolicyManager) getSystemService("device_policy");
            this.mLockPatternUtils = new LockPatternUtils(activity);
            this.mIsSetNewPassword = "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(action) || "android.app.action.SET_NEW_PASSWORD".equals(action);
            boolean booleanExtra = intent.getBooleanExtra("confirm_credentials", true);
            if (activity instanceof InternalActivity) {
                this.mPasswordConfirmed = !booleanExtra;
                this.mUserPassword = intent.getParcelableExtra("password");
            } else if (arguments != null) {
                LockscreenCredential parcelable = arguments.getParcelable("password");
                this.mUserPassword = parcelable;
                this.mPasswordConfirmed = parcelable != null;
            }
            this.mRequestGatekeeperPasswordHandle = intent.getBooleanExtra("request_gk_pw_handle", false);
            this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
            this.mForFace = intent.getBooleanExtra("for_face", false);
            this.mForBiometrics = intent.getBooleanExtra("for_biometrics", false);
            this.mRequestedMinComplexity = intent.getIntExtra("requested_min_complexity", 0);
            this.mOnlyEnforceDevicePasswordRequirement = intent.getBooleanExtra("device_password_requirement_only", false);
            this.mIsCallingAppAdmin = intent.getBooleanExtra("is_calling_app_admin", false);
            this.mForChangeCredRequiredForBoot = arguments != null && arguments.getBoolean("for_cred_req_boot");
            this.mUserManager = UserManager.get(activity);
            if (arguments != null) {
                this.mUnificationProfileCredential = arguments.getParcelable("unification_profile_credential");
                this.mUnificationProfileId = arguments.getInt("unification_profile_id", -10000);
            }
            if (bundle != null) {
                this.mPasswordConfirmed = bundle.getBoolean("password_confirmed");
                this.mWaitingForConfirmation = bundle.getBoolean("waiting_for_confirmation");
                this.mUserPassword = bundle.getParcelable("password");
            }
            this.mUserId = Utils.getSecureTargetUser(activity.getActivityToken(), UserManager.get(activity), arguments, intent.getExtras()).getIdentifier();
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
            ChooseLockGenericController build = new ChooseLockGenericController.Builder(getContext(), this.mUserId, this.mLockPatternUtils).setAppRequestedMinComplexity(this.mRequestedMinComplexity).setEnforceDevicePasswordRequirementOnly(this.mOnlyEnforceDevicePasswordRequirement).setProfileToUnify(this.mUnificationProfileId).setHideInsecureScreenLockTypes(alwaysHideInsecureScreenLockTypes() || intent.getBooleanExtra("hide_insecure_options", false)).build();
            this.mController = build;
            this.mCallerAppName = build.isComplexityProvidedByAdmin() ? null : intent.getStringExtra("caller_app_name");
            this.mManagedPasswordProvider = ManagedLockPasswordProvider.get(activity, this.mUserId);
            if (this.mPasswordConfirmed) {
                updatePreferencesOrFinish(bundle != null);
                if (this.mForChangeCredRequiredForBoot) {
                    maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
                }
            } else if (!this.mWaitingForConfirmation) {
                ChooseLockSettingsHelper.Builder builder = new ChooseLockSettingsHelper.Builder(activity, this);
                builder.setRequestCode(100).setTitle(getString(R$string.unlock_set_unlock_launch_picker_title)).setReturnCredentials(true).setUserId(this.mUserId);
                if (((this.mIsManagedProfile && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) && !this.mIsSetNewPassword) || !builder.show()) {
                    this.mPasswordConfirmed = true;
                    updatePreferencesOrFinish(bundle != null);
                } else {
                    this.mWaitingForConfirmation = true;
                }
            }
            addHeaderView();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            updateActivityTitle();
            return super.onCreateView(layoutInflater, viewGroup, bundle);
        }

        private void updateActivityTitle() {
            LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
            if (lockPatternUtils == null) {
                return;
            }
            if (this.mIsManagedProfile) {
                if (lockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) {
                    getActivity().setTitle(this.mDpm.getResources().getString("Settings.LOCK_SETTINGS_UPDATE_PROFILE_LOCK_TITLE", new Supplier() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda0
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateActivityTitle$0;
                            lambda$updateActivityTitle$0 = ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$updateActivityTitle$0();
                            return lambda$updateActivityTitle$0;
                        }
                    }));
                } else {
                    getActivity().setTitle(this.mDpm.getResources().getString("Settings.LOCK_SETTINGS_NEW_PROFILE_LOCK_TITLE", new Supplier() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda1
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$updateActivityTitle$1;
                            lambda$updateActivityTitle$1 = ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$updateActivityTitle$1();
                            return lambda$updateActivityTitle$1;
                        }
                    }));
                }
            } else if (lockPatternUtils.isSecure(this.mUserId)) {
                getActivity().setTitle(R$string.lock_settings_picker_update_lock_title);
            } else {
                getActivity().setTitle(R$string.lock_settings_picker_new_lock_title);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$updateActivityTitle$0() {
            return getString(R$string.lock_settings_picker_update_profile_lock_title);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$updateActivityTitle$1() {
            return getString(R$string.lock_settings_picker_new_profile_lock_title);
        }

        protected boolean canRunBeforeDeviceProvisioned() {
            PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) getSystemService("persistent_data_block");
            return persistentDataBlockManager == null || persistentDataBlockManager.getDataBlockSize() == 0;
        }

        protected Class<? extends InternalActivity> getInternalActivityClass() {
            return InternalActivity.class;
        }

        protected void addHeaderView() {
            setHeaderView(R$layout.choose_lock_generic_biometric_header);
            TextView textView = (TextView) getHeaderView().findViewById(R$id.biometric_header_description);
            if (this.mForFingerprint) {
                if (this.mIsSetNewPassword) {
                    textView.setText(R$string.fingerprint_unlock_title);
                } else {
                    textView.setText(R$string.lock_settings_picker_biometric_message);
                }
            } else if (this.mForFace) {
                if (this.mIsSetNewPassword) {
                    textView.setText(R$string.face_unlock_title);
                } else {
                    textView.setText(R$string.lock_settings_picker_biometric_message);
                }
            } else if (this.mForBiometrics) {
                if (this.mIsSetNewPassword) {
                    textView.setText(R$string.biometrics_unlock_title);
                } else {
                    textView.setText(R$string.lock_settings_picker_biometric_message);
                }
            } else if (this.mIsManagedProfile) {
                textView.setText(this.mDpm.getResources().getString("Settings.WORK_PROFILE_SCREEN_LOCK_SETUP_MESSAGE", new Supplier() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda2
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$addHeaderView$2;
                        lambda$addHeaderView$2 = ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$addHeaderView$2();
                        return lambda$addHeaderView$2;
                    }
                }));
            } else {
                textView.setText("");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$addHeaderView$2() {
            return getString(R$string.lock_settings_picker_profile_message);
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            writePreferenceClickMetric(preference);
            String key = preference.getKey();
            if (!isUnlockMethodSecure(key) && this.mLockPatternUtils.isSecure(this.mUserId)) {
                showFactoryResetProtectionWarningDialog(key);
                return true;
            } else if ("unlock_skip_fingerprint".equals(key) || "unlock_skip_face".equals(key) || "unlock_skip_biometrics".equals(key)) {
                Intent intent = new Intent(getActivity(), getInternalActivityClass());
                intent.setAction(getIntent().getAction());
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("confirm_credentials", !this.mPasswordConfirmed);
                intent.putExtra("requested_min_complexity", this.mRequestedMinComplexity);
                intent.putExtra("device_password_requirement_only", this.mOnlyEnforceDevicePasswordRequirement);
                intent.putExtra("caller_app_name", this.mCallerAppName);
                LockscreenCredential lockscreenCredential = this.mUserPassword;
                if (lockscreenCredential != null) {
                    intent.putExtra("password", (Parcelable) lockscreenCredential);
                }
                startActivityForResult(intent, 104);
                return true;
            } else {
                return setUnlockMethod(key);
            }
        }

        private void maybeEnableEncryption(int i, boolean z) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
            if (UserManager.get(getActivity()).isAdminUser() && this.mUserId == UserHandle.myUserId() && LockPatternUtils.isDeviceEncryptionEnabled() && !LockPatternUtils.isFileEncryptionEnabled() && !devicePolicyManager.getDoNotAskCredentialsOnBoot()) {
                Intent intentForUnlockMethod = getIntentForUnlockMethod(i);
                intentForUnlockMethod.putExtra("for_cred_req_boot", this.mForChangeCredRequiredForBoot);
                FragmentActivity activity = getActivity();
                Intent encryptionInterstitialIntent = getEncryptionInterstitialIntent(activity, i, this.mLockPatternUtils.isCredentialRequiredToDecrypt(!AccessibilityManager.getInstance(activity).isEnabled()), intentForUnlockMethod);
                encryptionInterstitialIntent.putExtra("for_fingerprint", this.mForFingerprint);
                encryptionInterstitialIntent.putExtra("for_face", this.mForFace);
                encryptionInterstitialIntent.putExtra("for_biometrics", this.mForBiometrics);
                startActivityForResult(encryptionInterstitialIntent, (this.mIsSetNewPassword && this.mRequestGatekeeperPasswordHandle) ? 103 : 101);
            } else if (this.mForChangeCredRequiredForBoot) {
                finish();
            } else {
                updateUnlockMethodAndFinish(i, z, false);
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            this.mWaitingForConfirmation = false;
            if (i == 100 && i2 == -1) {
                this.mPasswordConfirmed = true;
                this.mUserPassword = intent != null ? (LockscreenCredential) intent.getParcelableExtra("password") : null;
                updatePreferencesOrFinish(false);
                if (this.mForChangeCredRequiredForBoot) {
                    LockscreenCredential lockscreenCredential = this.mUserPassword;
                    if (lockscreenCredential != null && !lockscreenCredential.isNone()) {
                        maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
                    } else {
                        finish();
                    }
                }
            } else if (i == 102 || i == 101) {
                if (i2 != 0 || this.mForChangeCredRequiredForBoot) {
                    getActivity().setResult(i2, intent);
                    finish();
                } else if (getIntent().getIntExtra("lockscreen.password_type", -1) != -1) {
                    getActivity().setResult(0, intent);
                    finish();
                }
            } else if (i == 103 && i2 == 1) {
                Intent biometricEnrollIntent = getBiometricEnrollIntent(getActivity());
                if (intent != null) {
                    biometricEnrollIntent.putExtras(intent.getExtras());
                }
                biometricEnrollIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                startActivity(biometricEnrollIntent);
                finish();
            } else if (i == 104) {
                if (i2 != 0) {
                    FragmentActivity activity = getActivity();
                    if (i2 == 1) {
                        i2 = -1;
                    }
                    activity.setResult(i2, intent);
                    finish();
                }
            } else if (i == 501) {
                return;
            } else {
                getActivity().setResult(0);
                finish();
            }
            if (i == 0 && this.mForChangeCredRequiredForBoot) {
                finish();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Intent getBiometricEnrollIntent(Context context) {
            Intent intent = new Intent(context, BiometricEnrollActivity.InternalActivity.class);
            intent.putExtra("skip_intro", true);
            return intent;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
            bundle.putBoolean("waiting_for_confirmation", this.mWaitingForConfirmation);
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                bundle.putParcelable("password", lockscreenCredential.duplicate());
            }
        }

        void updatePreferencesOrFinish(boolean z) {
            int i;
            Intent intent = getActivity().getIntent();
            if (StorageManager.isFileEncryptedNativeOrEmulated()) {
                i = intent.getIntExtra("lockscreen.password_type", -1);
            } else {
                Log.i("ChooseLockGenericFragment", "Ignoring PASSWORD_TYPE_KEY because device is not file encrypted");
                i = -1;
            }
            if (i != -1) {
                if (z) {
                    return;
                }
                updateUnlockMethodAndFinish(i, false, true);
                return;
            }
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.removeAll();
            }
            addPreferences();
            disableUnusablePreferences();
            updatePreferenceText();
            updateCurrentPreference();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void addPreferences() {
            addPreferencesFromResource(R$xml.security_settings_picker);
            final int managedProfileId = Utils.getManagedProfileId(this.mUserManager, this.mUserId);
            FooterPreference footerPreference = (FooterPreference) findPreference("lock_settings_footer");
            if (!TextUtils.isEmpty(this.mCallerAppName) && !this.mIsCallingAppAdmin) {
                footerPreference.setVisible(true);
                footerPreference.setTitle(getFooterString());
            } else if (!this.mForFace && !this.mForBiometrics && !this.mForFingerprint && !this.mIsManagedProfile && this.mController.isScreenLockRestrictedByAdmin() && managedProfileId != -10000) {
                StringBuilder sb = new StringBuilder(this.mDpm.getResources().getString("Settings.WORK_PROFILE_IT_ADMIN_CANT_RESET_SCREEN_LOCK", new Supplier() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda3
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$addPreferences$3;
                        lambda$addPreferences$3 = ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$addPreferences$3();
                        return lambda$addPreferences$3;
                    }
                }));
                footerPreference.setVisible(true);
                footerPreference.setTitle(sb);
                StringBuilder sb2 = new StringBuilder(this.mDpm.getResources().getString("Settings.WORK_PROFILE_IT_ADMIN_CANT_RESET_SCREEN_LOCK_ACTION", new Supplier() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$addPreferences$4;
                        lambda$addPreferences$4 = ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$addPreferences$4();
                        return lambda$addPreferences$4;
                    }
                }));
                View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChooseLockGeneric.ChooseLockGenericFragment.this.lambda$addPreferences$5(managedProfileId, view);
                    }
                };
                footerPreference.setLearnMoreText(sb2);
                footerPreference.setLearnMoreAction(onClickListener);
            } else {
                footerPreference.setVisible(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$addPreferences$3() {
            return getString(R$string.lock_settings_picker_admin_restricted_personal_message);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$addPreferences$4() {
            return getString(R$string.lock_settings_picker_admin_restricted_personal_message_action);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addPreferences$5(int i, View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.intent.extra.USER_ID", i);
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                bundle.putParcelable("password", lockscreenCredential);
            }
            new SubSettingLauncher(getActivity()).setDestination(ChooseLockGenericFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setArguments(bundle).launch();
            finish();
        }

        private String getFooterString() {
            int i;
            int aggregatedPasswordComplexity = this.mController.getAggregatedPasswordComplexity();
            if (aggregatedPasswordComplexity == 65536) {
                i = R$string.unlock_footer_low_complexity_requested;
            } else if (aggregatedPasswordComplexity == 196608) {
                i = R$string.unlock_footer_medium_complexity_requested;
            } else if (aggregatedPasswordComplexity == 327680) {
                i = R$string.unlock_footer_high_complexity_requested;
            } else {
                i = R$string.unlock_footer_none_complexity_requested;
            }
            return getResources().getString(i, this.mCallerAppName);
        }

        private void updatePreferenceText() {
            boolean z = this.mForFingerprint;
            this.mManagedPasswordProvider.isSettingManagedPasswordSupported();
            if (!this.mForFingerprint || !this.mIsSetNewPassword) {
                removePreference("unlock_skip_fingerprint");
            }
            if (!this.mForFace || !this.mIsSetNewPassword) {
                removePreference("unlock_skip_face");
            }
            if (this.mForBiometrics && this.mIsSetNewPassword) {
                return;
            }
            removePreference("unlock_skip_biometrics");
        }

        private void updateCurrentPreference() {
            Preference findPreference = findPreference(getKeyForCurrent());
            if (findPreference != null) {
                findPreference.setSummary(R$string.current_screen_lock);
            }
        }

        private String getKeyForCurrent() {
            int credentialOwnerProfile = UserManager.get(getContext()).getCredentialOwnerProfile(this.mUserId);
            if (this.mLockPatternUtils.isLockScreenDisabled(credentialOwnerProfile)) {
                return ScreenLockType.NONE.preferenceKey;
            }
            ScreenLockType fromQuality = ScreenLockType.fromQuality(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(credentialOwnerProfile));
            if (fromQuality != null) {
                return fromQuality.preferenceKey;
            }
            return null;
        }

        private void disableUnusablePreferences() {
            ScreenLockType[] values;
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for (ScreenLockType screenLockType : ScreenLockType.values()) {
                Preference findPreference = findPreference(screenLockType.preferenceKey);
                if (findPreference instanceof RestrictedPreference) {
                    boolean isScreenLockVisible = this.mController.isScreenLockVisible(screenLockType);
                    boolean isScreenLockEnabled = this.mController.isScreenLockEnabled(screenLockType);
                    if (!isScreenLockVisible) {
                        preferenceScreen.removePreference(findPreference);
                    } else if (!isScreenLockEnabled) {
                        findPreference.setEnabled(false);
                    }
                }
            }
        }

        protected Intent getLockManagedPasswordIntent(LockscreenCredential lockscreenCredential) {
            return this.mManagedPasswordProvider.createIntent(false, lockscreenCredential);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Intent getLockPasswordIntent(int i) {
            ChooseLockPassword.IntentBuilder requestGatekeeperPasswordHandle = new ChooseLockPassword.IntentBuilder(getContext()).setPasswordType(i).setPasswordRequirement(this.mController.getAggregatedPasswordComplexity(), this.mController.getAggregatedPasswordMetrics()).setForFingerprint(this.mForFingerprint).setForFace(this.mForFace).setForBiometrics(this.mForBiometrics).setUserId(this.mUserId).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPasswordHandle);
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                requestGatekeeperPasswordHandle.setPassword(lockscreenCredential);
            }
            int i2 = this.mUnificationProfileId;
            if (i2 != -10000) {
                requestGatekeeperPasswordHandle.setProfileToUnify(i2, this.mUnificationProfileCredential);
            }
            return requestGatekeeperPasswordHandle.build();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Intent getLockPatternIntent() {
            ChooseLockPattern.IntentBuilder requestGatekeeperPasswordHandle = new ChooseLockPattern.IntentBuilder(getContext()).setForFingerprint(this.mForFingerprint).setForFace(this.mForFace).setForBiometrics(this.mForBiometrics).setUserId(this.mUserId).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPasswordHandle);
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                requestGatekeeperPasswordHandle.setPattern(lockscreenCredential);
            }
            int i = this.mUnificationProfileId;
            if (i != -10000) {
                requestGatekeeperPasswordHandle.setProfileToUnify(i, this.mUnificationProfileCredential);
            }
            return requestGatekeeperPasswordHandle.build();
        }

        protected Intent getEncryptionInterstitialIntent(Context context, int i, boolean z, Intent intent) {
            return EncryptionInterstitial.createStartIntent(context, i, z, intent);
        }

        void updateUnlockMethodAndFinish(int i, boolean z, boolean z2) {
            if (!this.mPasswordConfirmed) {
                throw new IllegalStateException("Tried to update password without confirming it");
            }
            int upgradeQuality = this.mController.upgradeQuality(i);
            Intent intentForUnlockMethod = getIntentForUnlockMethod(upgradeQuality);
            if (intentForUnlockMethod != null) {
                if (getIntent().getBooleanExtra("show_options_button", false)) {
                    intentForUnlockMethod.putExtra("show_options_button", z2);
                }
                intentForUnlockMethod.putExtra("choose_lock_generic_extras", getIntent().getExtras());
                startActivityForResult(intentForUnlockMethod, (this.mIsSetNewPassword && this.mRequestGatekeeperPasswordHandle) ? 103 : 102);
            } else if (upgradeQuality == 0) {
                if (this.mUserPassword != null) {
                    this.mLockPatternUtils.setLockCredential(LockscreenCredential.createNone(), this.mUserPassword, this.mUserId);
                }
                this.mLockPatternUtils.setLockScreenDisabled(z, this.mUserId);
                getActivity().setResult(-1);
                LockScreenSafetySource.onLockScreenChange(getContext());
                finish();
            }
        }

        private Intent getIntentForUnlockMethod(int i) {
            if (i >= 524288) {
                return getLockManagedPasswordIntent(this.mUserPassword);
            }
            if (i >= 131072) {
                return getLockPasswordIntent(i);
            }
            if (i == 65536) {
                return getLockPatternIntent();
            }
            return null;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        @Override // com.android.settings.support.actionbar.HelpResourceProvider
        public int getHelpResource() {
            return R$string.help_url_choose_lockscreen;
        }

        private int getResIdForFactoryResetProtectionWarningTitle() {
            return this.mIsManagedProfile ? R$string.unlock_disable_frp_warning_title_profile : R$string.unlock_disable_frp_warning_title;
        }

        private int getResIdForFactoryResetProtectionWarningMessage() {
            FingerprintManager fingerprintManager = this.mFingerprintManager;
            boolean z = false;
            boolean hasEnrolledFingerprints = (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) ? false : this.mFingerprintManager.hasEnrolledFingerprints(this.mUserId);
            FaceManager faceManager = this.mFaceManager;
            if (faceManager != null && faceManager.isHardwareDetected()) {
                z = this.mFaceManager.hasEnrolledTemplates(this.mUserId);
            }
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
            if (keyguardStoredPasswordQuality == 65536) {
                if (hasEnrolledFingerprints && z) {
                    return R$string.unlock_disable_frp_warning_content_pattern_face_fingerprint;
                }
                if (hasEnrolledFingerprints) {
                    return R$string.unlock_disable_frp_warning_content_pattern_fingerprint;
                }
                if (z) {
                    return R$string.unlock_disable_frp_warning_content_pattern_face;
                }
                return R$string.unlock_disable_frp_warning_content_pattern;
            } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                if (hasEnrolledFingerprints && z) {
                    return R$string.unlock_disable_frp_warning_content_pin_face_fingerprint;
                }
                if (hasEnrolledFingerprints) {
                    return R$string.unlock_disable_frp_warning_content_pin_fingerprint;
                }
                if (z) {
                    return R$string.unlock_disable_frp_warning_content_pin_face;
                }
                return R$string.unlock_disable_frp_warning_content_pin;
            } else if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                if (hasEnrolledFingerprints && z) {
                    return R$string.unlock_disable_frp_warning_content_password_face_fingerprint;
                }
                if (hasEnrolledFingerprints) {
                    return R$string.unlock_disable_frp_warning_content_password_fingerprint;
                }
                if (z) {
                    return R$string.unlock_disable_frp_warning_content_password_face;
                }
                return R$string.unlock_disable_frp_warning_content_password;
            } else if (hasEnrolledFingerprints && z) {
                return R$string.unlock_disable_frp_warning_content_unknown_face_fingerprint;
            } else {
                if (hasEnrolledFingerprints) {
                    return R$string.unlock_disable_frp_warning_content_unknown_fingerprint;
                }
                if (z) {
                    return R$string.unlock_disable_frp_warning_content_unknown_face;
                }
                return R$string.unlock_disable_frp_warning_content_unknown;
            }
        }

        private boolean isUnlockMethodSecure(String str) {
            return (ScreenLockType.SWIPE.preferenceKey.equals(str) || ScreenLockType.NONE.preferenceKey.equals(str)) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setUnlockMethod(String str) {
            EventLog.writeEvent(90200, str);
            ScreenLockType fromKey = ScreenLockType.fromKey(str);
            if (fromKey != null) {
                switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[fromKey.ordinal()]) {
                    case 1:
                    case 2:
                        updateUnlockMethodAndFinish(fromKey.defaultQuality, fromKey == ScreenLockType.NONE, false);
                        return true;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        maybeEnableEncryption(fromKey.defaultQuality, false);
                        return true;
                }
            }
            Log.e("ChooseLockGenericFragment", "Encountered unknown unlock method to set: " + str);
            return false;
        }

        private void showFactoryResetProtectionWarningDialog(String str) {
            FactoryResetProtectionWarningDialog.newInstance(getResIdForFactoryResetProtectionWarningTitle(), getResIdForFactoryResetProtectionWarningMessage(), str).show(getChildFragmentManager(), "frp_warning_dialog");
        }

        /* loaded from: classes.dex */
        public static class FactoryResetProtectionWarningDialog extends InstrumentedDialogFragment {
            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 528;
            }

            public static FactoryResetProtectionWarningDialog newInstance(int i, int i2, String str) {
                FactoryResetProtectionWarningDialog factoryResetProtectionWarningDialog = new FactoryResetProtectionWarningDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("titleRes", i);
                bundle.putInt("messageRes", i2);
                bundle.putString("unlockMethodToSet", str);
                factoryResetProtectionWarningDialog.setArguments(bundle);
                return factoryResetProtectionWarningDialog;
            }

            @Override // androidx.fragment.app.DialogFragment
            public void show(FragmentManager fragmentManager, String str) {
                if (fragmentManager.findFragmentByTag(str) == null) {
                    super.show(fragmentManager, str);
                }
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                final Bundle arguments = getArguments();
                return new AlertDialog.Builder(getActivity()).setTitle(arguments.getInt("titleRes")).setMessage(arguments.getInt("messageRes")).setPositiveButton(R$string.unlock_disable_frp_warning_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.lambda$onCreateDialog$0(arguments, dialogInterface, i);
                    }
                }).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.password.ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.lambda$onCreateDialog$1(dialogInterface, i);
                    }
                }).create();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onCreateDialog$0(Bundle bundle, DialogInterface dialogInterface, int i) {
                ((ChooseLockGenericFragment) getParentFragment()).setUnlockMethod(bundle.getString("unlockMethodToSet"));
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockGeneric$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        static {
            int[] iArr = new int[ScreenLockType.values().length];
            $SwitchMap$com$android$settings$password$ScreenLockType = iArr;
            try {
                iArr[ScreenLockType.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.SWIPE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PATTERN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PIN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PASSWORD.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.MANAGED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }
}
