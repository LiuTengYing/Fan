package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.password.ChooseLockGenericController;
import com.android.settings.password.ChooseLockPassword;
import com.android.settings.password.ChooseLockTypeDialogFragment;
import com.android.settings.password.SetupChooseLockPassword;
/* loaded from: classes.dex */
public class SetupChooseLockPassword extends ChooseLockPassword {
    public static Intent modifyIntentForSetup(Context context, Intent intent) {
        intent.setClass(context, SetupChooseLockPassword.class);
        intent.putExtra("extra_prefs_show_button_bar", false);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ChooseLockPassword, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SetupChooseLockPasswordFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.password.ChooseLockPassword
    Class<? extends Fragment> getFragmentClass() {
        return SetupChooseLockPasswordFragment.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ChooseLockPassword, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        findViewById(R$id.content_parent).setFitsSystemWindows(false);
    }

    /* loaded from: classes.dex */
    public static class SetupChooseLockPasswordFragment extends ChooseLockPassword.ChooseLockPasswordFragment implements ChooseLockTypeDialogFragment.OnLockTypeSelectedListener {
        private boolean mLeftButtonIsSkip;
        private Button mOptionsButton;

        @Override // com.android.settings.password.ChooseLockPassword.ChooseLockPasswordFragment
        protected int getStageType() {
            return 0;
        }

        @Override // com.android.settings.password.ChooseLockPassword.ChooseLockPasswordFragment, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            FragmentActivity activity = getActivity();
            boolean z = new ChooseLockGenericController.Builder(activity, this.mUserId).setHideInsecureScreenLockTypes(true).build().getVisibleAndEnabledScreenLockTypes().size() > 0;
            boolean booleanExtra = activity.getIntent().getBooleanExtra("show_options_button", false);
            if (!z) {
                Log.w("SetupChooseLockPassword", "Visible screen lock types is empty!");
            }
            if (booleanExtra && z) {
                Button button = (Button) view.findViewById(R$id.screen_lock_options);
                this.mOptionsButton = button;
                button.setVisibility(0);
                this.mOptionsButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.password.SetupChooseLockPassword$SetupChooseLockPasswordFragment$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SetupChooseLockPassword.SetupChooseLockPasswordFragment.this.lambda$onViewCreated$0(view2);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onViewCreated$0(View view) {
            ChooseLockTypeDialogFragment.newInstance(this.mUserId).show(getChildFragmentManager(), "skip_screen_lock_dialog");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockPassword.ChooseLockPasswordFragment
        public void onSkipOrClearButtonClick(View view) {
            if (this.mLeftButtonIsSkip) {
                Intent intent = getActivity().getIntent();
                SetupSkipDialog newInstance = SetupSkipDialog.newInstance(intent.getBooleanExtra(":settings:frp_supported", false), false, this.mIsAlphaMode, intent.getBooleanExtra("for_fingerprint", false), intent.getBooleanExtra("for_face", false), intent.getBooleanExtra("for_biometrics", false));
                ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                newInstance.show(getFragmentManager());
                return;
            }
            super.onSkipOrClearButtonClick(view);
        }

        @Override // com.android.settings.password.ChooseLockPassword.ChooseLockPasswordFragment
        protected Intent getRedactionInterstitialIntent(Context context) {
            SetupRedactionInterstitial.setEnabled(context, true);
            return null;
        }

        @Override // com.android.settings.password.ChooseLockTypeDialogFragment.OnLockTypeSelectedListener
        public void onLockTypeSelected(ScreenLockType screenLockType) {
            if (screenLockType == (this.mIsAlphaMode ? ScreenLockType.PASSWORD : ScreenLockType.PIN)) {
                return;
            }
            startChooseLockActivity(screenLockType, getActivity());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockPassword.ChooseLockPasswordFragment
        public void updateUi() {
            super.updateUi();
            ChooseLockPassword.ChooseLockPasswordFragment.Stage stage = this.mUiStage;
            ChooseLockPassword.ChooseLockPasswordFragment.Stage stage2 = ChooseLockPassword.ChooseLockPasswordFragment.Stage.Introduction;
            if (stage == stage2) {
                this.mSkipOrClearButton.setText(getActivity(), R$string.skip_label);
                this.mLeftButtonIsSkip = true;
            } else {
                this.mSkipOrClearButton.setText(getActivity(), R$string.lockpassword_clear_label);
                this.mLeftButtonIsSkip = false;
            }
            Button button = this.mOptionsButton;
            if (button != null) {
                button.setVisibility(this.mUiStage != stage2 ? 8 : 0);
            }
        }
    }
}
