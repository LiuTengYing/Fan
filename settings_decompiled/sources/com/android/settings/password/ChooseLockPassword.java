package com.android.settings.password;

import android.app.admin.DevicePolicyManager;
import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImeAwareEditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.PasswordValidationError;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settings.password.ChooseLockPassword;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.android.settingslib.utils.StringUtil;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ChooseLockPassword extends SettingsActivity {
    @Override // com.android.settings.core.SettingsBaseActivity
    protected boolean isToolbarEnabled() {
        return false;
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    /* loaded from: classes.dex */
    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            Intent intent = new Intent(context, ChooseLockPassword.class);
            this.mIntent = intent;
            intent.putExtra("confirm_credentials", false);
            intent.putExtra("extra_require_password", false);
        }

        public IntentBuilder setPasswordType(int i) {
            this.mIntent.putExtra("lockscreen.password_type", i);
            return this;
        }

        public IntentBuilder setUserId(int i) {
            this.mIntent.putExtra("android.intent.extra.USER_ID", i);
            return this;
        }

        public IntentBuilder setRequestGatekeeperPasswordHandle(boolean z) {
            this.mIntent.putExtra("request_gk_pw_handle", z);
            return this;
        }

        public IntentBuilder setPassword(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("password", (Parcelable) lockscreenCredential);
            return this;
        }

        public IntentBuilder setForFingerprint(boolean z) {
            this.mIntent.putExtra("for_fingerprint", z);
            return this;
        }

        public IntentBuilder setForFace(boolean z) {
            this.mIntent.putExtra("for_face", z);
            return this;
        }

        public IntentBuilder setForBiometrics(boolean z) {
            this.mIntent.putExtra("for_biometrics", z);
            return this;
        }

        public IntentBuilder setPasswordRequirement(int i, PasswordMetrics passwordMetrics) {
            this.mIntent.putExtra("min_complexity", i);
            this.mIntent.putExtra("min_metrics", (Parcelable) passwordMetrics);
            return this;
        }

        public IntentBuilder setProfileToUnify(int i, LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("unification_profile_id", i);
            this.mIntent.putExtra("unification_profile_credential", (Parcelable) lockscreenCredential);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ChooseLockPasswordFragment.class.getName().equals(str);
    }

    Class<? extends Fragment> getFragmentClass() {
        return ChooseLockPasswordFragment.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(R$id.content_parent).setFitsSystemWindows(false);
        getWindow().addFlags(8192);
    }

    /* loaded from: classes.dex */
    public static class ChooseLockPasswordFragment extends InstrumentedFragment implements TextView.OnEditorActionListener, TextWatcher, SaveChosenLockWorkerBase.Listener {
        private LockscreenCredential mChosenPassword;
        private LockscreenCredential mCurrentCredential;
        private LockscreenCredential mFirstPassword;
        protected boolean mForBiometrics;
        protected boolean mForFace;
        protected boolean mForFingerprint;
        protected boolean mIsAlphaMode;
        protected boolean mIsManagedProfile;
        private GlifLayout mLayout;
        private LockPatternUtils mLockPatternUtils;
        private TextView mMessage;
        private PasswordMetrics mMinMetrics;
        private FooterButton mNextButton;
        private ImeAwareEditText mPasswordEntry;
        private TextViewInputDisabler mPasswordEntryInputDisabler;
        private byte[] mPasswordHistoryHashFactor;
        private PasswordRequirementAdapter mPasswordRequirementAdapter;
        private RecyclerView mPasswordRestrictionView;
        private boolean mRequestGatekeeperPassword;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        private TextChangedHandler mTextChangedHandler;
        protected int mUserId;
        private List<PasswordValidationError> mValidationErrors;
        private int mMinComplexity = 0;
        private int mUnificationProfileId = -10000;
        private int mPasswordType = 131072;
        protected Stage mUiStage = Stage.Introduction;

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 28;
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        protected int toVisibility(boolean z) {
            return z ? 0 : 8;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum Introduction uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:444)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:391)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:320)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:258)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class Stage {
            private static final /* synthetic */ Stage[] $VALUES;
            public static final Stage ConfirmWrong;
            public static final Stage Introduction;
            public static final Stage NeedToConfirm;
            public final int alphaHint;
            public final int alphaHintForBiometrics;
            public final int alphaHintForFace;
            public final int alphaHintForFingerprint;
            public final int alphaHintForProfile;
            public final String alphaHintOverrideForProfile;
            public final int alphaMessageForBiometrics;
            public final int buttonText;
            public final int numericHint;
            public final int numericHintForBiometrics;
            public final int numericHintForFace;
            public final int numericHintForFingerprint;
            public final int numericHintForProfile;
            public final String numericHintOverrideForProfile;
            public final int numericMessageForBiometrics;

            public static Stage valueOf(String str) {
                return (Stage) Enum.valueOf(Stage.class, str);
            }

            public static Stage[] values() {
                return (Stage[]) $VALUES.clone();
            }

            static {
                int i = R$string.lockpassword_choose_your_password_header;
                int i2 = R$string.lockpassword_choose_your_profile_password_header;
                int i3 = R$string.lockpassword_choose_your_password_header_for_fingerprint;
                int i4 = R$string.lockpassword_choose_your_password_header_for_face;
                int i5 = R$string.lockpassword_choose_your_password_header_for_biometrics;
                int i6 = R$string.lockpassword_choose_your_pin_header;
                int i7 = R$string.lockpassword_choose_your_profile_pin_header;
                int i8 = R$string.lockpassword_choose_your_pin_header_for_fingerprint;
                int i9 = R$string.lockpassword_choose_your_pin_header_for_face;
                int i10 = R$string.lockpassword_choose_your_pin_header_for_biometrics;
                int i11 = R$string.lock_settings_picker_biometrics_added_security_message;
                Stage stage = new Stage("Introduction", 0, i, "Settings.SET_WORK_PROFILE_PASSWORD_HEADER", i2, i3, i4, i5, i6, "Settings.SET_WORK_PROFILE_PIN_HEADER", i7, i8, i9, i10, i11, i11, R$string.next_label);
                Introduction = stage;
                int i12 = R$string.lockpassword_confirm_your_password_header;
                int i13 = R$string.lockpassword_reenter_your_profile_password_header;
                int i14 = R$string.lockpassword_confirm_your_pin_header;
                int i15 = R$string.lockpassword_reenter_your_profile_pin_header;
                int i16 = R$string.lockpassword_confirm_label;
                Stage stage2 = new Stage("NeedToConfirm", 1, i12, "Settings.REENTER_WORK_PROFILE_PASSWORD_HEADER", i13, i12, i12, i12, i14, "Settings.REENTER_WORK_PROFILE_PIN_HEADER", i15, i14, i14, i14, 0, 0, i16);
                NeedToConfirm = stage2;
                int i17 = R$string.lockpassword_confirm_passwords_dont_match;
                int i18 = R$string.lockpassword_confirm_pins_dont_match;
                Stage stage3 = new Stage("ConfirmWrong", 2, i17, "UNDEFINED", i17, i17, i17, i17, i18, "UNDEFINED", i18, i18, i18, i18, 0, 0, i16);
                ConfirmWrong = stage3;
                $VALUES = new Stage[]{stage, stage2, stage3};
            }

            private Stage(String str, int i, int i2, String str2, int i3, int i4, int i5, int i6, int i7, String str3, int i8, int i9, int i10, int i11, int i12, int i13, int i14) {
                this.alphaHint = i2;
                this.alphaHintOverrideForProfile = str2;
                this.alphaHintForProfile = i3;
                this.alphaHintForFingerprint = i4;
                this.alphaHintForFace = i5;
                this.alphaHintForBiometrics = i6;
                this.numericHint = i7;
                this.numericHintOverrideForProfile = str3;
                this.numericHintForProfile = i8;
                this.numericHintForFingerprint = i9;
                this.numericHintForFace = i10;
                this.numericHintForBiometrics = i11;
                this.alphaMessageForBiometrics = i12;
                this.numericMessageForBiometrics = i13;
                this.buttonText = i14;
            }

            public String getHint(final Context context, boolean z, int i, boolean z2) {
                if (z) {
                    if (i == 1) {
                        return context.getString(this.alphaHintForFingerprint);
                    }
                    if (i == 2) {
                        return context.getString(this.alphaHintForFace);
                    }
                    if (i == 3) {
                        return context.getString(this.alphaHintForBiometrics);
                    }
                    if (z2) {
                        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(this.alphaHintOverrideForProfile, new Supplier() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$Stage$$ExternalSyntheticLambda0
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                String lambda$getHint$0;
                                lambda$getHint$0 = ChooseLockPassword.ChooseLockPasswordFragment.Stage.this.lambda$getHint$0(context);
                                return lambda$getHint$0;
                            }
                        });
                    }
                    return context.getString(this.alphaHint);
                } else if (i == 1) {
                    return context.getString(this.numericHintForFingerprint);
                } else {
                    if (i == 2) {
                        return context.getString(this.numericHintForFace);
                    }
                    if (i == 3) {
                        return context.getString(this.numericHintForBiometrics);
                    }
                    if (z2) {
                        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(this.numericHintOverrideForProfile, new Supplier() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$Stage$$ExternalSyntheticLambda1
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                String lambda$getHint$1;
                                lambda$getHint$1 = ChooseLockPassword.ChooseLockPasswordFragment.Stage.this.lambda$getHint$1(context);
                                return lambda$getHint$1;
                            }
                        });
                    }
                    return context.getString(this.numericHint);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ String lambda$getHint$0(Context context) {
                return context.getString(this.alphaHintForProfile);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ String lambda$getHint$1(Context context) {
                return context.getString(this.numericHintForProfile);
            }

            public int getMessage(boolean z, int i) {
                if (i == 1 || i == 2 || i == 3) {
                    return z ? this.alphaMessageForBiometrics : this.numericMessageForBiometrics;
                }
                return 0;
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            Intent intent = getActivity().getIntent();
            if (!(getActivity() instanceof ChooseLockPassword)) {
                throw new SecurityException("Fragment contained in wrong activity");
            }
            this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
            this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
            this.mForFace = intent.getBooleanExtra("for_face", false);
            this.mForBiometrics = intent.getBooleanExtra("for_biometrics", false);
            this.mPasswordType = intent.getIntExtra("lockscreen.password_type", 131072);
            this.mUnificationProfileId = intent.getIntExtra("unification_profile_id", -10000);
            this.mMinComplexity = intent.getIntExtra("min_complexity", 0);
            PasswordMetrics parcelableExtra = intent.getParcelableExtra("min_metrics");
            this.mMinMetrics = parcelableExtra;
            if (parcelableExtra == null) {
                this.mMinMetrics = new PasswordMetrics(-1);
            }
            if (intent.getBooleanExtra("for_cred_req_boot", false)) {
                SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
                boolean booleanExtra = getActivity().getIntent().getBooleanExtra("extra_require_password", true);
                LockscreenCredential lockscreenCredential = (LockscreenCredential) intent.getParcelableExtra("password");
                LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
                saveAndFinishWorker.setBlocking(true);
                saveAndFinishWorker.setListener(this);
                saveAndFinishWorker.start(lockPatternUtils, booleanExtra, false, lockscreenCredential, lockscreenCredential, this.mUserId);
            }
            this.mTextChangedHandler = new TextChangedHandler();
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(R$layout.choose_lock_password, viewGroup, false);
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mLayout = (GlifLayout) view;
            ((ViewGroup) view.findViewById(R$id.password_container)).setOpticalInsets(Insets.NONE);
            FooterBarMixin footerBarMixin = (FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getActivity()).setText(R$string.lockpassword_clear_label).setListener(new View.OnClickListener() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChooseLockPassword.ChooseLockPasswordFragment.this.onSkipOrClearButtonClick(view2);
                }
            }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(R$string.next_label).setListener(new View.OnClickListener() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChooseLockPassword.ChooseLockPasswordFragment.this.onNextButtonClick(view2);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            this.mMessage = (TextView) view.findViewById(R$id.sud_layout_description);
            this.mLayout.setIcon(getActivity().getDrawable(R$drawable.ic_lock));
            int i = this.mPasswordType;
            this.mIsAlphaMode = 262144 == i || 327680 == i || 393216 == i;
            setupPasswordRequirementsView(view);
            this.mPasswordRestrictionView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ImeAwareEditText findViewById = view.findViewById(R$id.password_entry);
            this.mPasswordEntry = findViewById;
            findViewById.setOnEditorActionListener(this);
            this.mPasswordEntry.addTextChangedListener(this);
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntryInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
            FragmentActivity activity = getActivity();
            int inputType = this.mPasswordEntry.getInputType();
            ImeAwareEditText imeAwareEditText = this.mPasswordEntry;
            if (!this.mIsAlphaMode) {
                inputType = 18;
            }
            imeAwareEditText.setInputType(inputType);
            if (this.mIsAlphaMode) {
                this.mPasswordEntry.setContentDescription(getString(R$string.unlock_set_unlock_password_title));
            } else {
                this.mPasswordEntry.setContentDescription(getString(R$string.unlock_set_unlock_pin_title));
            }
            this.mPasswordEntry.setTypeface(Typeface.create(getContext().getString(17039982), 0));
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("confirm_credentials", true);
            this.mCurrentCredential = intent.getParcelableExtra("password");
            this.mRequestGatekeeperPassword = intent.getBooleanExtra("request_gk_pw_handle", false);
            if (bundle == null) {
                updateStage(Stage.Introduction);
                if (booleanExtra) {
                    new ChooseLockSettingsHelper.Builder(getActivity()).setRequestCode(58).setTitle(getString(R$string.unlock_set_unlock_launch_picker_title)).setReturnCredentials(true).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPassword).setUserId(this.mUserId).show();
                }
            } else {
                this.mFirstPassword = bundle.getParcelable("first_password");
                String string = bundle.getString("ui_stage");
                if (string != null) {
                    Stage valueOf = Stage.valueOf(string);
                    this.mUiStage = valueOf;
                    updateStage(valueOf);
                }
                this.mCurrentCredential = bundle.getParcelable("current_credential");
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
            }
            if (activity instanceof SettingsActivity) {
                String hint = Stage.Introduction.getHint(getContext(), this.mIsAlphaMode, getStageType(), this.mIsManagedProfile);
                ((SettingsActivity) activity).setTitle(hint);
                this.mLayout.setHeaderText(hint);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            LockscreenCredential lockscreenCredential = this.mCurrentCredential;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        protected int getStageType() {
            if (this.mForFingerprint) {
                return 1;
            }
            if (this.mForFace) {
                return 2;
            }
            return this.mForBiometrics ? 3 : 0;
        }

        private void setupPasswordRequirementsView(View view) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R$id.password_requirements_view);
            this.mPasswordRestrictionView = recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            PasswordRequirementAdapter passwordRequirementAdapter = new PasswordRequirementAdapter();
            this.mPasswordRequirementAdapter = passwordRequirementAdapter;
            this.mPasswordRestrictionView.setAdapter(passwordRequirementAdapter);
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(this);
                return;
            }
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntry.scheduleShowSoftInput();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(null);
            }
            super.onPause();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("ui_stage", this.mUiStage.name());
            bundle.putParcelable("first_password", this.mFirstPassword);
            LockscreenCredential lockscreenCredential = this.mCurrentCredential;
            if (lockscreenCredential != null) {
                bundle.putParcelable("current_credential", lockscreenCredential.duplicate());
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 58) {
                return;
            }
            if (i2 != -1) {
                getActivity().setResult(1);
                getActivity().finish();
                return;
            }
            this.mCurrentCredential = intent.getParcelableExtra("password");
        }

        protected Intent getRedactionInterstitialIntent(Context context) {
            return RedactionInterstitial.createStartIntent(context, this.mUserId);
        }

        protected void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            updateUi();
            if (stage2 != stage) {
                GlifLayout glifLayout = this.mLayout;
                glifLayout.announceForAccessibility(glifLayout.getHeaderText());
            }
        }

        @VisibleForTesting
        boolean validatePassword(LockscreenCredential lockscreenCredential) {
            byte[] credential = lockscreenCredential.getCredential();
            List<PasswordValidationError> validatePassword = PasswordMetrics.validatePassword(this.mMinMetrics, this.mMinComplexity, !this.mIsAlphaMode, credential);
            this.mValidationErrors = validatePassword;
            if (validatePassword.isEmpty() && this.mLockPatternUtils.checkPasswordHistory(credential, getPasswordHistoryHashFactor(), this.mUserId)) {
                this.mValidationErrors = Collections.singletonList(new PasswordValidationError(14));
            }
            return this.mValidationErrors.isEmpty();
        }

        private byte[] getPasswordHistoryHashFactor() {
            if (this.mPasswordHistoryHashFactor == null) {
                LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
                LockscreenCredential lockscreenCredential = this.mCurrentCredential;
                if (lockscreenCredential == null) {
                    lockscreenCredential = LockscreenCredential.createNone();
                }
                this.mPasswordHistoryHashFactor = lockPatternUtils.getPasswordHistoryHashFactor(lockscreenCredential, this.mUserId);
            }
            return this.mPasswordHistoryHashFactor;
        }

        public void handleNext() {
            if (this.mSaveAndFinishWorker != null) {
                return;
            }
            Editable text = this.mPasswordEntry.getText();
            if (TextUtils.isEmpty(text)) {
                return;
            }
            LockscreenCredential createPassword = this.mIsAlphaMode ? LockscreenCredential.createPassword(text) : LockscreenCredential.createPin(text);
            this.mChosenPassword = createPassword;
            Stage stage = this.mUiStage;
            if (stage == Stage.Introduction) {
                if (validatePassword(createPassword)) {
                    this.mFirstPassword = this.mChosenPassword;
                    this.mPasswordEntry.setText("");
                    updateStage(Stage.NeedToConfirm);
                    return;
                }
                this.mChosenPassword.zeroize();
            } else if (stage == Stage.NeedToConfirm) {
                if (createPassword.equals(this.mFirstPassword)) {
                    startSaveAndFinish();
                    return;
                }
                Editable text2 = this.mPasswordEntry.getText();
                if (text2 != null) {
                    Selection.setSelection(text2, 0, text2.length());
                }
                updateStage(Stage.ConfirmWrong);
                this.mChosenPassword.zeroize();
            }
        }

        protected void setNextEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        protected void setNextText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            this.mPasswordEntry.setText("");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleNext();
        }

        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 0 || i == 6 || i == 5) {
                handleNext();
                return true;
            }
            return false;
        }

        String[] convertErrorCodeToMessages() {
            int i;
            int i2;
            ArrayList arrayList = new ArrayList();
            for (PasswordValidationError passwordValidationError : this.mValidationErrors) {
                switch (passwordValidationError.errorCode) {
                    case 2:
                        arrayList.add(getString(R$string.lockpassword_illegal_character));
                        break;
                    case 3:
                        Resources resources = getResources();
                        if (this.mIsAlphaMode) {
                            i = R$plurals.lockpassword_password_too_short;
                        } else {
                            i = R$plurals.lockpassword_pin_too_short;
                        }
                        int i3 = passwordValidationError.requirement;
                        arrayList.add(resources.getQuantityString(i, i3, Integer.valueOf(i3)));
                        break;
                    case 4:
                        arrayList.add(StringUtil.getIcuPluralsString(getContext(), passwordValidationError.requirement, R$string.lockpassword_password_too_short_all_numeric));
                        break;
                    case 5:
                        Resources resources2 = getResources();
                        if (this.mIsAlphaMode) {
                            i2 = R$plurals.lockpassword_password_too_long;
                        } else {
                            i2 = R$plurals.lockpassword_pin_too_long;
                        }
                        int i4 = passwordValidationError.requirement;
                        arrayList.add(resources2.getQuantityString(i2, i4 + 1, Integer.valueOf(i4 + 1)));
                        break;
                    case 6:
                        arrayList.add(getString(R$string.lockpassword_pin_no_sequential_digits));
                        break;
                    case 7:
                        Resources resources3 = getResources();
                        int i5 = R$plurals.lockpassword_password_requires_letters;
                        int i6 = passwordValidationError.requirement;
                        arrayList.add(resources3.getQuantityString(i5, i6, Integer.valueOf(i6)));
                        break;
                    case 8:
                        Resources resources4 = getResources();
                        int i7 = R$plurals.lockpassword_password_requires_uppercase;
                        int i8 = passwordValidationError.requirement;
                        arrayList.add(resources4.getQuantityString(i7, i8, Integer.valueOf(i8)));
                        break;
                    case 9:
                        Resources resources5 = getResources();
                        int i9 = R$plurals.lockpassword_password_requires_lowercase;
                        int i10 = passwordValidationError.requirement;
                        arrayList.add(resources5.getQuantityString(i9, i10, Integer.valueOf(i10)));
                        break;
                    case 10:
                        Resources resources6 = getResources();
                        int i11 = R$plurals.lockpassword_password_requires_numeric;
                        int i12 = passwordValidationError.requirement;
                        arrayList.add(resources6.getQuantityString(i11, i12, Integer.valueOf(i12)));
                        break;
                    case 11:
                        Resources resources7 = getResources();
                        int i13 = R$plurals.lockpassword_password_requires_symbols;
                        int i14 = passwordValidationError.requirement;
                        arrayList.add(resources7.getQuantityString(i13, i14, Integer.valueOf(i14)));
                        break;
                    case 12:
                        Resources resources8 = getResources();
                        int i15 = R$plurals.lockpassword_password_requires_nonletter;
                        int i16 = passwordValidationError.requirement;
                        arrayList.add(resources8.getQuantityString(i15, i16, Integer.valueOf(i16)));
                        break;
                    case 13:
                        Resources resources9 = getResources();
                        int i17 = R$plurals.lockpassword_password_requires_nonnumerical;
                        int i18 = passwordValidationError.requirement;
                        arrayList.add(resources9.getQuantityString(i17, i18, Integer.valueOf(i18)));
                        break;
                    case 14:
                        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
                        if (this.mIsAlphaMode) {
                            arrayList.add(devicePolicyManager.getResources().getString("Settings.PASSWORD_RECENTLY_USED", new Supplier() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$$ExternalSyntheticLambda2
                                @Override // java.util.function.Supplier
                                public final Object get() {
                                    String lambda$convertErrorCodeToMessages$0;
                                    lambda$convertErrorCodeToMessages$0 = ChooseLockPassword.ChooseLockPasswordFragment.this.lambda$convertErrorCodeToMessages$0();
                                    return lambda$convertErrorCodeToMessages$0;
                                }
                            }));
                            break;
                        } else {
                            arrayList.add(devicePolicyManager.getResources().getString("Settings.PIN_RECENTLY_USED", new Supplier() { // from class: com.android.settings.password.ChooseLockPassword$ChooseLockPasswordFragment$$ExternalSyntheticLambda3
                                @Override // java.util.function.Supplier
                                public final Object get() {
                                    String lambda$convertErrorCodeToMessages$1;
                                    lambda$convertErrorCodeToMessages$1 = ChooseLockPassword.ChooseLockPasswordFragment.this.lambda$convertErrorCodeToMessages$1();
                                    return lambda$convertErrorCodeToMessages$1;
                                }
                            }));
                            break;
                        }
                    default:
                        Log.wtf("ChooseLockPassword", "unknown error validating password: " + passwordValidationError);
                        break;
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$convertErrorCodeToMessages$0() {
            return getString(R$string.lockpassword_password_recently_used);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$convertErrorCodeToMessages$1() {
            return getString(R$string.lockpassword_pin_recently_used);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void updateUi() {
            LockscreenCredential createPinOrNone;
            boolean z = true;
            boolean z2 = this.mSaveAndFinishWorker == null;
            if (this.mIsAlphaMode) {
                createPinOrNone = LockscreenCredential.createPasswordOrNone(this.mPasswordEntry.getText());
            } else {
                createPinOrNone = LockscreenCredential.createPinOrNone(this.mPasswordEntry.getText());
            }
            int size = createPinOrNone.size();
            if (this.mUiStage == Stage.Introduction) {
                this.mPasswordRestrictionView.setVisibility(0);
                boolean validatePassword = validatePassword(createPinOrNone);
                this.mPasswordRequirementAdapter.setRequirements(convertErrorCodeToMessages());
                setNextEnabled(validatePassword);
            } else {
                this.mPasswordRestrictionView.setVisibility(8);
                setHeaderText(this.mUiStage.getHint(getContext(), this.mIsAlphaMode, getStageType(), this.mIsManagedProfile));
                setNextEnabled(z2 && size >= 4);
                FooterButton footerButton = this.mSkipOrClearButton;
                if (!z2 || size <= 0) {
                    z = false;
                }
                footerButton.setVisibility(toVisibility(z));
            }
            int stageType = getStageType();
            if (getStageType() != 0) {
                int message = this.mUiStage.getMessage(this.mIsAlphaMode, stageType);
                if (message != 0) {
                    this.mMessage.setVisibility(0);
                    this.mMessage.setText(message);
                } else {
                    this.mMessage.setVisibility(4);
                }
            } else {
                this.mMessage.setVisibility(8);
            }
            setNextText(this.mUiStage.buttonText);
            this.mPasswordEntryInputDisabler.setInputEnabled(z2);
            createPinOrNone.zeroize();
        }

        private void setHeaderText(String str) {
            if (TextUtils.isEmpty(this.mLayout.getHeaderText()) || !this.mLayout.getHeaderText().toString().equals(str)) {
                this.mLayout.setHeaderText(str);
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            if (this.mUiStage == Stage.ConfirmWrong) {
                this.mUiStage = Stage.NeedToConfirm;
            }
            this.mTextChangedHandler.notifyAfterTextChanged();
        }

        private void startSaveAndFinish() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("ChooseLockPassword", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            this.mPasswordEntryInputDisabler.setInputEnabled(false);
            setNextEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            getFragmentManager().beginTransaction().add(this.mSaveAndFinishWorker, "save_and_finish_worker").commit();
            getFragmentManager().executePendingTransactions();
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("extra_require_password", true);
            if (this.mUnificationProfileId != -10000) {
                LockscreenCredential parcelableExtra = intent.getParcelableExtra("unification_profile_credential");
                try {
                    this.mSaveAndFinishWorker.setProfileToUnify(this.mUnificationProfileId, parcelableExtra);
                    if (parcelableExtra != null) {
                        parcelableExtra.close();
                    }
                } catch (Throwable th) {
                    if (parcelableExtra != null) {
                        try {
                            parcelableExtra.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, booleanExtra, this.mRequestGatekeeperPassword, this.mChosenPassword, this.mCurrentCredential, this.mUserId);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase.Listener
        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            Intent redactionInterstitialIntent;
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            LockscreenCredential lockscreenCredential3 = this.mFirstPassword;
            if (lockscreenCredential3 != null) {
                lockscreenCredential3.zeroize();
            }
            this.mPasswordEntry.setText("");
            if (!z && (redactionInterstitialIntent = getRedactionInterstitialIntent(getActivity())) != null) {
                startActivity(redactionInterstitialIntent);
            }
            getActivity().finish();
        }

        /* loaded from: classes.dex */
        class TextChangedHandler extends Handler {
            TextChangedHandler() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void notifyAfterTextChanged() {
                removeMessages(1);
                sendEmptyMessageDelayed(1, 100L);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (ChooseLockPasswordFragment.this.getActivity() != null && message.what == 1) {
                    ChooseLockPasswordFragment.this.updateUi();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private LockscreenCredential mChosenPassword;
        private LockscreenCredential mCurrentCredential;

        @Override // com.android.settings.password.SaveChosenLockWorkerBase, androidx.fragment.app.Fragment
        public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public /* bridge */ /* synthetic */ void setBlocking(boolean z) {
            super.setBlocking(z);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public /* bridge */ /* synthetic */ void setListener(SaveChosenLockWorkerBase.Listener listener) {
            super.setListener(listener);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public /* bridge */ /* synthetic */ void setProfileToUnify(int i, LockscreenCredential lockscreenCredential) {
            super.setProfileToUnify(i, lockscreenCredential);
        }

        public void start(LockPatternUtils lockPatternUtils, boolean z, boolean z2, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i) {
            prepare(lockPatternUtils, z, z2, i);
            this.mChosenPassword = lockscreenCredential;
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mCurrentCredential = lockscreenCredential2;
            this.mUserId = i;
            start();
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        protected Pair<Boolean, Intent> saveAndVerifyInBackground() {
            boolean lockCredential = this.mUtils.setLockCredential(this.mChosenPassword, this.mCurrentCredential, this.mUserId);
            if (lockCredential) {
                unifyProfileCredentialIfRequested();
            }
            Intent intent = null;
            if (lockCredential && this.mRequestGatekeeperPassword) {
                VerifyCredentialResponse verifyCredential = this.mUtils.verifyCredential(this.mChosenPassword, this.mUserId, 1);
                if (!verifyCredential.isMatched() || !verifyCredential.containsGatekeeperPasswordHandle()) {
                    Log.e("ChooseLockPassword", "critical: bad response or missing GK PW handle for known good password: " + verifyCredential.toString());
                }
                intent = new Intent();
                intent.putExtra("gk_pw_handle", verifyCredential.getGatekeeperPasswordHandle());
            }
            return Pair.create(Boolean.valueOf(lockCredential), intent);
        }
    }
}
