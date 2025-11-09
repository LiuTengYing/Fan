package com.android.settings.password;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserManager;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.R$attr;
import com.android.settings.R$bool;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settings.password.ChooseLockPattern;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.collect.Lists;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.template.IconMixin;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ChooseLockPattern extends SettingsActivity {
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
            Intent intent = new Intent(context, ChooseLockPattern.class);
            this.mIntent = intent;
            intent.putExtra("extra_require_password", false);
            intent.putExtra("confirm_credentials", false);
        }

        public IntentBuilder setUserId(int i) {
            this.mIntent.putExtra("android.intent.extra.USER_ID", i);
            return this;
        }

        public IntentBuilder setRequestGatekeeperPasswordHandle(boolean z) {
            this.mIntent.putExtra("request_gk_pw_handle", z);
            return this;
        }

        public IntentBuilder setPattern(LockscreenCredential lockscreenCredential) {
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
        return ChooseLockPatternFragment.class.getName().equals(str);
    }

    Class<? extends Fragment> getFragmentClass() {
        return ChooseLockPatternFragment.class;
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

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    /* loaded from: classes.dex */
    public static class ChooseLockPatternFragment extends InstrumentedFragment implements SaveChosenLockWorkerBase.Listener {
        @VisibleForTesting
        protected LockscreenCredential mChosenPattern;
        private LockscreenCredential mCurrentCredential;
        private ColorStateList mDefaultHeaderColorList;
        protected TextView mFooterText;
        protected boolean mForBiometrics;
        protected boolean mForFace;
        protected boolean mForFingerprint;
        protected TextView mHeaderText;
        protected boolean mIsManagedProfile;
        private LockPatternUtils mLockPatternUtils;
        protected LockPatternView mLockPatternView;
        private FooterButton mNextButton;
        private boolean mRequestGatekeeperPassword;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        protected int mUserId;
        private final List<LockPatternView.Cell> mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(new LockPatternView.Cell[]{LockPatternView.Cell.of(0, 0), LockPatternView.Cell.of(0, 1), LockPatternView.Cell.of(1, 1), LockPatternView.Cell.of(2, 1)}));
        protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() { // from class: com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.1
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            public void onPatternStart() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
                patternInProgress();
            }

            public void onPatternCleared() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> list) {
                if (ChooseLockPatternFragment.this.mUiStage == Stage.NeedToConfirm || ChooseLockPatternFragment.this.mUiStage == Stage.ConfirmWrong) {
                    if (ChooseLockPatternFragment.this.mChosenPattern == null) {
                        throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                    }
                    LockscreenCredential createPattern = LockscreenCredential.createPattern(list);
                    try {
                        if (ChooseLockPatternFragment.this.mChosenPattern.equals(createPattern)) {
                            ChooseLockPatternFragment.this.updateStage(Stage.ChoiceConfirmed);
                        } else {
                            ChooseLockPatternFragment.this.updateStage(Stage.ConfirmWrong);
                        }
                        if (createPattern != null) {
                            createPattern.close();
                        }
                    } catch (Throwable th) {
                        if (createPattern != null) {
                            try {
                                createPattern.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } else if (ChooseLockPatternFragment.this.mUiStage == Stage.Introduction || ChooseLockPatternFragment.this.mUiStage == Stage.ChoiceTooShort) {
                    if (list.size() < 4) {
                        ChooseLockPatternFragment.this.updateStage(Stage.ChoiceTooShort);
                        return;
                    }
                    ChooseLockPatternFragment.this.mChosenPattern = LockscreenCredential.createPattern(list);
                    ChooseLockPatternFragment.this.updateStage(Stage.FirstChoiceValid);
                } else {
                    throw new IllegalStateException("Unexpected stage " + ChooseLockPatternFragment.this.mUiStage + " when entering the pattern.");
                }
            }

            private void patternInProgress() {
                ChooseLockPatternFragment.this.mHeaderText.setText(R$string.lockpattern_recording_inprogress);
                if (ChooseLockPatternFragment.this.mDefaultHeaderColorList != null) {
                    ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                    chooseLockPatternFragment.mHeaderText.setTextColor(chooseLockPatternFragment.mDefaultHeaderColorList);
                }
                ChooseLockPatternFragment.this.mFooterText.setText("");
                ChooseLockPatternFragment.this.mNextButton.setEnabled(false);
            }
        };
        private Stage mUiStage = Stage.Introduction;
        private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.2
            @Override // java.lang.Runnable
            public void run() {
                ChooseLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 29;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 55) {
                return;
            }
            if (i2 != -1) {
                getActivity().setResult(1);
                getActivity().finish();
            } else {
                this.mCurrentCredential = intent.getParcelableExtra("password");
            }
            updateStage(Stage.Introduction);
        }

        protected void setRightButtonEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        protected void setRightButtonText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum Retry uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:444)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:391)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:320)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:258)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class LeftButtonMode {
            private static final /* synthetic */ LeftButtonMode[] $VALUES;
            public static final LeftButtonMode Gone;
            public static final LeftButtonMode Retry;
            public static final LeftButtonMode RetryDisabled;
            final boolean enabled;
            final int text;

            public static LeftButtonMode valueOf(String str) {
                return (LeftButtonMode) Enum.valueOf(LeftButtonMode.class, str);
            }

            public static LeftButtonMode[] values() {
                return (LeftButtonMode[]) $VALUES.clone();
            }

            static {
                int i = R$string.lockpattern_retry_button_text;
                LeftButtonMode leftButtonMode = new LeftButtonMode("Retry", 0, i, true);
                Retry = leftButtonMode;
                LeftButtonMode leftButtonMode2 = new LeftButtonMode("RetryDisabled", 1, i, false);
                RetryDisabled = leftButtonMode2;
                LeftButtonMode leftButtonMode3 = new LeftButtonMode("Gone", 2, -1, false);
                Gone = leftButtonMode3;
                $VALUES = new LeftButtonMode[]{leftButtonMode, leftButtonMode2, leftButtonMode3};
            }

            private LeftButtonMode(String str, int i, int i2, boolean z) {
                this.text = i2;
                this.enabled = z;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum Continue uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:444)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:391)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:320)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:258)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class RightButtonMode {
            private static final /* synthetic */ RightButtonMode[] $VALUES;
            public static final RightButtonMode Confirm;
            public static final RightButtonMode ConfirmDisabled;
            public static final RightButtonMode Continue;
            public static final RightButtonMode ContinueDisabled;
            public static final RightButtonMode Ok;
            final boolean enabled;
            final int text;

            public static RightButtonMode valueOf(String str) {
                return (RightButtonMode) Enum.valueOf(RightButtonMode.class, str);
            }

            public static RightButtonMode[] values() {
                return (RightButtonMode[]) $VALUES.clone();
            }

            static {
                int i = R$string.next_label;
                RightButtonMode rightButtonMode = new RightButtonMode("Continue", 0, i, true);
                Continue = rightButtonMode;
                RightButtonMode rightButtonMode2 = new RightButtonMode("ContinueDisabled", 1, i, false);
                ContinueDisabled = rightButtonMode2;
                int i2 = R$string.lockpattern_confirm_button_text;
                RightButtonMode rightButtonMode3 = new RightButtonMode("Confirm", 2, i2, true);
                Confirm = rightButtonMode3;
                RightButtonMode rightButtonMode4 = new RightButtonMode("ConfirmDisabled", 3, i2, false);
                ConfirmDisabled = rightButtonMode4;
                RightButtonMode rightButtonMode5 = new RightButtonMode("Ok", 4, 17039370, true);
                Ok = rightButtonMode5;
                $VALUES = new RightButtonMode[]{rightButtonMode, rightButtonMode2, rightButtonMode3, rightButtonMode4, rightButtonMode5};
            }

            private RightButtonMode(String str, int i, int i2, boolean z) {
                this.text = i2;
                this.enabled = z;
            }
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
            public static final Stage ChoiceConfirmed;
            public static final Stage ChoiceTooShort;
            public static final Stage ConfirmWrong;
            public static final Stage FirstChoiceValid;
            public static final Stage HelpScreen;
            public static final Stage Introduction;
            public static final Stage NeedToConfirm;
            final int footerMessage;
            final int headerMessage;
            final LeftButtonMode leftMode;
            final int messageForBiometrics;
            final boolean patternEnabled;
            final RightButtonMode rightMode;

            public static Stage valueOf(String str) {
                return (Stage) Enum.valueOf(Stage.class, str);
            }

            public static Stage[] values() {
                return (Stage[]) $VALUES.clone();
            }

            static {
                int i = R$string.lock_settings_picker_biometrics_added_security_message;
                int i2 = R$string.lockpattern_recording_intro_header;
                LeftButtonMode leftButtonMode = LeftButtonMode.Gone;
                RightButtonMode rightButtonMode = RightButtonMode.ContinueDisabled;
                Stage stage = new Stage("Introduction", 0, i, i2, leftButtonMode, rightButtonMode, -1, true);
                Introduction = stage;
                Stage stage2 = new Stage("HelpScreen", 1, -1, R$string.lockpattern_settings_help_how_to_record, leftButtonMode, RightButtonMode.Ok, -1, false);
                HelpScreen = stage2;
                int i3 = R$string.lockpattern_recording_incorrect_too_short;
                LeftButtonMode leftButtonMode2 = LeftButtonMode.Retry;
                Stage stage3 = new Stage("ChoiceTooShort", 2, i, i3, leftButtonMode2, rightButtonMode, -1, true);
                ChoiceTooShort = stage3;
                Stage stage4 = new Stage("FirstChoiceValid", 3, i, R$string.lockpattern_pattern_entered_header, leftButtonMode2, RightButtonMode.Continue, -1, false);
                FirstChoiceValid = stage4;
                int i4 = R$string.lockpattern_need_to_confirm;
                RightButtonMode rightButtonMode2 = RightButtonMode.ConfirmDisabled;
                Stage stage5 = new Stage("NeedToConfirm", 4, -1, i4, leftButtonMode, rightButtonMode2, -1, true);
                NeedToConfirm = stage5;
                Stage stage6 = new Stage("ConfirmWrong", 5, -1, R$string.lockpattern_need_to_unlock_wrong, leftButtonMode, rightButtonMode2, -1, true);
                ConfirmWrong = stage6;
                Stage stage7 = new Stage("ChoiceConfirmed", 6, -1, R$string.lockpattern_pattern_confirmed_header, leftButtonMode, RightButtonMode.Confirm, -1, false);
                ChoiceConfirmed = stage7;
                $VALUES = new Stage[]{stage, stage2, stage3, stage4, stage5, stage6, stage7};
            }

            private Stage(String str, int i, int i2, int i3, LeftButtonMode leftButtonMode, RightButtonMode rightButtonMode, int i4, boolean z) {
                this.headerMessage = i3;
                this.messageForBiometrics = i2;
                this.leftMode = leftButtonMode;
                this.rightMode = rightButtonMode;
                this.footerMessage = i4;
                this.patternEnabled = z;
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (!(getActivity() instanceof ChooseLockPattern)) {
                throw new SecurityException("Fragment contained in wrong activity");
            }
            Intent intent = getActivity().getIntent();
            this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            if (intent.getBooleanExtra("for_cred_req_boot", false)) {
                SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
                boolean booleanExtra = getActivity().getIntent().getBooleanExtra("extra_require_password", true);
                LockscreenCredential lockscreenCredential = (LockscreenCredential) intent.getParcelableExtra("password");
                saveAndFinishWorker.setBlocking(true);
                saveAndFinishWorker.setListener(this);
                saveAndFinishWorker.start(this.mLockPatternUtils, booleanExtra, false, lockscreenCredential, lockscreenCredential, this.mUserId);
            }
            this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
            this.mForFace = intent.getBooleanExtra("for_face", false);
            this.mForBiometrics = intent.getBooleanExtra("for_biometrics", false);
        }

        private void updateActivityTitle() {
            String string;
            if (this.mForFingerprint) {
                string = getString(R$string.lockpassword_choose_your_pattern_header_for_fingerprint);
            } else if (this.mForFace) {
                string = getString(R$string.lockpassword_choose_your_pattern_header_for_face);
            } else if (this.mIsManagedProfile) {
                string = ((DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.SET_WORK_PROFILE_PATTERN_HEADER", new Supplier() { // from class: com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$$ExternalSyntheticLambda3
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateActivityTitle$0;
                        lambda$updateActivityTitle$0 = ChooseLockPattern.ChooseLockPatternFragment.this.lambda$updateActivityTitle$0();
                        return lambda$updateActivityTitle$0;
                    }
                });
            } else {
                string = getString(R$string.lockpassword_choose_your_pattern_header);
            }
            getActivity().setTitle(string);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$updateActivityTitle$0() {
            return getString(R$string.lockpassword_choose_your_profile_pattern_header);
        }

        @Override // androidx.fragment.app.Fragment
        @SuppressLint({"ClickableViewAccessibility"})
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            GlifLayout glifLayout = (GlifLayout) layoutInflater.inflate(R$layout.choose_lock_pattern, viewGroup, false);
            glifLayout.findViewById(R$id.lockPattern).setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onCreateView$1;
                    lambda$onCreateView$1 = ChooseLockPattern.ChooseLockPatternFragment.lambda$onCreateView$1(view, motionEvent);
                    return lambda$onCreateView$1;
                }
            });
            updateActivityTitle();
            glifLayout.setHeaderText(getActivity().getTitle());
            glifLayout.getHeaderTextView().setAccessibilityLiveRegion(1);
            if (getResources().getBoolean(R$bool.config_lock_pattern_minimal_ui)) {
                if (glifLayout.findViewById(R$id.sud_layout_icon) != null) {
                    ((IconMixin) glifLayout.getMixin(IconMixin.class)).setVisibility(8);
                }
            } else {
                glifLayout.setIcon(getActivity().getDrawable(R$drawable.ic_lock));
            }
            FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getActivity()).setText(R$string.lockpattern_tutorial_cancel_label).setListener(new View.OnClickListener() { // from class: com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChooseLockPattern.ChooseLockPatternFragment.this.onSkipOrClearButtonClick(view);
                }
            }).setButtonType(0).setTheme(R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(R$string.lockpattern_tutorial_continue_label).setListener(new View.OnClickListener() { // from class: com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChooseLockPattern.ChooseLockPatternFragment.this.onNextButtonClick(view);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            return glifLayout;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onCreateView$1(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
            return false;
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            TextView textView = (TextView) view.findViewById(R$id.headerText);
            this.mHeaderText = textView;
            this.mDefaultHeaderColorList = textView.getTextColors();
            LockPatternView findViewById = view.findViewById(R$id.lockPattern);
            this.mLockPatternView = findViewById;
            findViewById.setOnPatternListener(this.mChooseNewLockPatternListener);
            this.mLockPatternView.setFadePattern(false);
            this.mFooterText = (TextView) view.findViewById(R$id.footerText);
            view.findViewById(R$id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
            Intent intent = getActivity().getIntent();
            this.mCurrentCredential = intent.getParcelableExtra("password");
            this.mRequestGatekeeperPassword = intent.getBooleanExtra("request_gk_pw_handle", false);
            if (bundle != null) {
                this.mChosenPattern = bundle.getParcelable("chosenPattern");
                this.mCurrentCredential = bundle.getParcelable("currentPattern");
                updateStage(Stage.values()[bundle.getInt("uiStage")]);
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
            } else if (booleanExtra) {
                updateStage(Stage.NeedToConfirm);
                if (new ChooseLockSettingsHelper.Builder(getActivity()).setRequestCode(55).setTitle(getString(R$string.unlock_set_unlock_launch_picker_title)).setReturnCredentials(true).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPassword).setUserId(this.mUserId).show()) {
                    return;
                }
                updateStage(Stage.Introduction);
            } else {
                updateStage(Stage.Introduction);
            }
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            if (this.mSaveAndFinishWorker != null) {
                setRightButtonEnabled(false);
                this.mSaveAndFinishWorker.setListener(this);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(null);
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

        protected Intent getRedactionInterstitialIntent(Context context) {
            return RedactionInterstitial.createStartIntent(context, this.mUserId);
        }

        public void handleLeftButton() {
            if (this.mUiStage.leftMode == LeftButtonMode.Retry) {
                LockscreenCredential lockscreenCredential = this.mChosenPattern;
                if (lockscreenCredential != null) {
                    lockscreenCredential.zeroize();
                    this.mChosenPattern = null;
                }
                this.mLockPatternView.clearPattern();
                updateStage(Stage.Introduction);
                return;
            }
            throw new IllegalStateException("left footer button pressed, but stage of " + this.mUiStage + " doesn't make sense");
        }

        public void handleRightButton() {
            Stage stage = this.mUiStage;
            RightButtonMode rightButtonMode = stage.rightMode;
            RightButtonMode rightButtonMode2 = RightButtonMode.Continue;
            if (rightButtonMode == rightButtonMode2) {
                Stage stage2 = Stage.FirstChoiceValid;
                if (stage != stage2) {
                    throw new IllegalStateException("expected ui stage " + stage2 + " when button is " + rightButtonMode2);
                }
                updateStage(Stage.NeedToConfirm);
                return;
            }
            RightButtonMode rightButtonMode3 = RightButtonMode.Confirm;
            if (rightButtonMode == rightButtonMode3) {
                Stage stage3 = Stage.ChoiceConfirmed;
                if (stage != stage3) {
                    throw new IllegalStateException("expected ui stage " + stage3 + " when button is " + rightButtonMode3);
                }
                startSaveAndFinish();
            } else if (rightButtonMode == RightButtonMode.Ok) {
                if (stage != Stage.HelpScreen) {
                    throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
                }
                this.mLockPatternView.clearPattern();
                this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                updateStage(Stage.Introduction);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            handleLeftButton();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleRightButton();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("uiStage", this.mUiStage.ordinal());
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                bundle.putParcelable("chosenPattern", lockscreenCredential);
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                bundle.putParcelable("currentPattern", lockscreenCredential2.duplicate());
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            Stage stage3 = Stage.ChoiceTooShort;
            boolean z = false;
            if (stage == stage3) {
                this.mHeaderText.setText(getResources().getString(stage.headerMessage, 4));
            } else {
                this.mHeaderText.setText(stage.headerMessage);
            }
            GlifLayout glifLayout = (GlifLayout) getActivity().findViewById(R$id.setup_wizard_layout);
            boolean z2 = this.mForFingerprint || this.mForFace || this.mForBiometrics;
            if (z2) {
                int i = stage.messageForBiometrics;
                if (i == -1) {
                    glifLayout.setDescriptionText("");
                } else {
                    glifLayout.setDescriptionText(i);
                }
            } else {
                glifLayout.getDescriptionTextView().setVisibility(8);
            }
            int i2 = stage.footerMessage;
            if (i2 == -1) {
                this.mFooterText.setText("");
            } else {
                this.mFooterText.setText(i2);
            }
            if (stage == Stage.ConfirmWrong || stage == stage3) {
                TypedValue typedValue = new TypedValue();
                getActivity().getTheme().resolveAttribute(R$attr.colorError, typedValue, true);
                this.mHeaderText.setTextColor(typedValue.data);
            } else {
                ColorStateList colorStateList = this.mDefaultHeaderColorList;
                if (colorStateList != null) {
                    this.mHeaderText.setTextColor(colorStateList);
                }
                if (stage == Stage.NeedToConfirm && z2) {
                    this.mHeaderText.setText("");
                    glifLayout.setHeaderText(R$string.lockpassword_draw_your_pattern_again_header);
                }
            }
            updateFooterLeftButton(stage);
            setRightButtonText(stage.rightMode.text);
            setRightButtonEnabled(stage.rightMode.enabled);
            if (stage.patternEnabled) {
                this.mLockPatternView.enableInput();
            } else {
                this.mLockPatternView.disableInput();
            }
            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
            int i3 = AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[this.mUiStage.ordinal()];
            if (i3 == 1) {
                this.mLockPatternView.clearPattern();
            } else if (i3 != 2) {
                if (i3 == 3) {
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                } else if (i3 == 5) {
                    this.mLockPatternView.clearPattern();
                } else if (i3 == 6) {
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                }
                z = true;
            } else {
                this.mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
            }
            if (stage2 != stage || z) {
                TextView textView = this.mHeaderText;
                textView.announceForAccessibility(textView.getText());
            }
        }

        protected void updateFooterLeftButton(Stage stage) {
            if (stage.leftMode == LeftButtonMode.Gone) {
                this.mSkipOrClearButton.setVisibility(8);
                return;
            }
            this.mSkipOrClearButton.setVisibility(0);
            this.mSkipOrClearButton.setText(getActivity(), stage.leftMode.text);
            this.mSkipOrClearButton.setEnabled(stage.leftMode.enabled);
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
        }

        private void startSaveAndFinish() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("ChooseLockPattern", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            setRightButtonEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            getFragmentManager().beginTransaction().add(this.mSaveAndFinishWorker, "save_and_finish_worker").commit();
            getFragmentManager().executePendingTransactions();
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("extra_require_password", true);
            if (intent.hasExtra("unification_profile_id")) {
                LockscreenCredential parcelableExtra = intent.getParcelableExtra("unification_profile_credential");
                try {
                    this.mSaveAndFinishWorker.setProfileToUnify(intent.getIntExtra("unification_profile_id", -10000), parcelableExtra);
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
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, booleanExtra, this.mRequestGatekeeperPassword, this.mChosenPattern, this.mCurrentCredential, this.mUserId);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase.Listener
        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            Intent redactionInterstitialIntent;
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            if (!z && (redactionInterstitialIntent = getRedactionInterstitialIntent(getActivity())) != null) {
                startActivity(redactionInterstitialIntent);
            }
            getActivity().finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockPattern$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage;

        static {
            int[] iArr = new int[ChooseLockPatternFragment.Stage.values().length];
            $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage = iArr;
            try {
                iArr[ChooseLockPatternFragment.Stage.Introduction.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.HelpScreen.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ChoiceTooShort.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.FirstChoiceValid.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.NeedToConfirm.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ConfirmWrong.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ChoiceConfirmed.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private LockscreenCredential mChosenPattern;
        private LockscreenCredential mCurrentCredential;
        private boolean mLockVirgin;

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
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mCurrentCredential = lockscreenCredential2;
            this.mChosenPattern = lockscreenCredential;
            this.mUserId = i;
            this.mLockVirgin = !this.mUtils.isPatternEverChosen(i);
            start();
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        protected Pair<Boolean, Intent> saveAndVerifyInBackground() {
            int i = this.mUserId;
            boolean lockCredential = this.mUtils.setLockCredential(this.mChosenPattern, this.mCurrentCredential, i);
            if (lockCredential) {
                unifyProfileCredentialIfRequested();
            }
            Intent intent = null;
            if (lockCredential && this.mRequestGatekeeperPassword) {
                VerifyCredentialResponse verifyCredential = this.mUtils.verifyCredential(this.mChosenPattern, i, 1);
                if (!verifyCredential.isMatched() || !verifyCredential.containsGatekeeperPasswordHandle()) {
                    Log.e("ChooseLockPattern", "critical: bad response or missing GK PW handle for known good pattern: " + verifyCredential.toString());
                }
                intent = new Intent();
                intent.putExtra("gk_pw_handle", verifyCredential.getGatekeeperPasswordHandle());
            }
            return Pair.create(Boolean.valueOf(lockCredential), intent);
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        protected void finish(Intent intent) {
            if (this.mLockVirgin) {
                this.mUtils.setVisiblePatternEnabled(true, this.mUserId);
            }
            super.finish(intent);
        }
    }
}
