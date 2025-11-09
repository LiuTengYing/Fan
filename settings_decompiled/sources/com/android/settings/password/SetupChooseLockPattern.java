package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.android.settings.R$bool;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.password.ChooseLockPattern;
import com.android.settings.password.ChooseLockTypeDialogFragment;
import com.android.settings.password.SetupChooseLockPattern;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes.dex */
public class SetupChooseLockPattern extends ChooseLockPattern {
    public static Intent modifyIntentForSetup(Context context, Intent intent) {
        intent.setClass(context, SetupChooseLockPattern.class);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ChooseLockPattern, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SetupChooseLockPatternFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.password.ChooseLockPattern
    Class<? extends Fragment> getFragmentClass() {
        return SetupChooseLockPatternFragment.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ChooseLockPattern, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R$string.lockpassword_choose_your_pattern_header);
    }

    /* loaded from: classes.dex */
    public static class SetupChooseLockPatternFragment extends ChooseLockPattern.ChooseLockPatternFragment implements ChooseLockTypeDialogFragment.OnLockTypeSelectedListener {
        private boolean mLeftButtonIsSkip;
        private Button mOptionsButton;

        @Override // com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
            if (!getResources().getBoolean(R$bool.config_lock_pattern_minimal_ui)) {
                Button button = (Button) onCreateView.findViewById(R$id.screen_lock_options);
                this.mOptionsButton = button;
                button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.password.SetupChooseLockPattern$SetupChooseLockPatternFragment$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SetupChooseLockPattern.SetupChooseLockPatternFragment.this.lambda$onCreateView$0(view);
                    }
                });
            }
            this.mSkipOrClearButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.password.SetupChooseLockPattern$SetupChooseLockPatternFragment$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SetupChooseLockPattern.SetupChooseLockPatternFragment.this.onSkipOrClearButtonClick(view);
                }
            });
            return onCreateView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateView$0(View view) {
            ChooseLockTypeDialogFragment.newInstance(this.mUserId).show(getChildFragmentManager(), "skip_screen_lock_dialog");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment
        public void onSkipOrClearButtonClick(View view) {
            if (this.mLeftButtonIsSkip) {
                Intent intent = getActivity().getIntent();
                SetupSkipDialog.newInstance(intent.getBooleanExtra(":settings:frp_supported", false), true, false, intent.getBooleanExtra("for_fingerprint", false), intent.getBooleanExtra("for_face", false), intent.getBooleanExtra("for_biometrics", false)).show(getFragmentManager());
                return;
            }
            super.onSkipOrClearButtonClick(view);
        }

        @Override // com.android.settings.password.ChooseLockTypeDialogFragment.OnLockTypeSelectedListener
        public void onLockTypeSelected(ScreenLockType screenLockType) {
            if (ScreenLockType.PATTERN == screenLockType) {
                return;
            }
            startChooseLockActivity(screenLockType, getActivity());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment
        public void updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage stage) {
            Button button;
            super.updateStage(stage);
            if (!getResources().getBoolean(R$bool.config_lock_pattern_minimal_ui) && (button = this.mOptionsButton) != null) {
                button.setVisibility((stage == ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction || stage == ChooseLockPattern.ChooseLockPatternFragment.Stage.HelpScreen || stage == ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceTooShort || stage == ChooseLockPattern.ChooseLockPatternFragment.Stage.FirstChoiceValid) ? 0 : 4);
            }
            if (stage.leftMode == ChooseLockPattern.ChooseLockPatternFragment.LeftButtonMode.Gone && stage == ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction) {
                this.mSkipOrClearButton.setVisibility(0);
                this.mSkipOrClearButton.setText(getActivity(), R$string.skip_label);
                this.mLeftButtonIsSkip = true;
            } else {
                this.mLeftButtonIsSkip = false;
            }
            ((GlifLayout) getActivity().findViewById(R$id.setup_wizard_layout)).setDescriptionText("");
        }

        @Override // com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment
        protected Intent getRedactionInterstitialIntent(Context context) {
            SetupRedactionInterstitial.setEnabled(context, true);
            return null;
        }
    }
}
