package com.android.settings.password;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.ConfirmLockPattern;
/* loaded from: classes.dex */
public final class ChooseLockSettingsHelper {
    private final Activity mActivity;
    private final Builder mBuilder;
    private final Fragment mFragment;
    LockPatternUtils mLockPatternUtils;

    private ChooseLockSettingsHelper(Builder builder, Activity activity, Fragment fragment) {
        this.mBuilder = builder;
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mLockPatternUtils = new LockPatternUtils(activity);
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private final Activity mActivity;
        private boolean mAllowAnyUserId;
        private CharSequence mAlternateButton;
        private CharSequence mDescription;
        private boolean mExternal;
        private boolean mForceVerifyPath;
        private boolean mForegroundOnly;
        private Fragment mFragment;
        private CharSequence mHeader;
        private int mRequestCode;
        boolean mRequestGatekeeperPasswordHandle;
        private boolean mReturnCredentials;
        private CharSequence mTitle;
        private int mUserId;

        public Builder(Activity activity) {
            this.mActivity = activity;
            this.mUserId = Utils.getCredentialOwnerUserId(activity);
        }

        public Builder(Activity activity, Fragment fragment) {
            this(activity);
            this.mFragment = fragment;
        }

        public Builder setRequestCode(int i) {
            this.mRequestCode = i;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public Builder setHeader(CharSequence charSequence) {
            this.mHeader = charSequence;
            return this;
        }

        public Builder setDescription(CharSequence charSequence) {
            this.mDescription = charSequence;
            return this;
        }

        public Builder setAlternateButton(CharSequence charSequence) {
            this.mAlternateButton = charSequence;
            return this;
        }

        public Builder setReturnCredentials(boolean z) {
            this.mReturnCredentials = z;
            return this;
        }

        public Builder setUserId(int i) {
            this.mUserId = i;
            return this;
        }

        public Builder setAllowAnyUserId(boolean z) {
            this.mAllowAnyUserId = z;
            return this;
        }

        public Builder setExternal(boolean z) {
            this.mExternal = z;
            return this;
        }

        public Builder setForegroundOnly(boolean z) {
            this.mForegroundOnly = z;
            return this;
        }

        public Builder setForceVerifyPath(boolean z) {
            this.mForceVerifyPath = z;
            return this;
        }

        public Builder setRequestGatekeeperPasswordHandle(boolean z) {
            this.mRequestGatekeeperPasswordHandle = z;
            return this;
        }

        public ChooseLockSettingsHelper build() {
            int i;
            if (!this.mAllowAnyUserId && (i = this.mUserId) != -9999) {
                Utils.enforceSameOwner(this.mActivity, i);
            }
            if (this.mExternal && this.mReturnCredentials) {
                throw new IllegalArgumentException("External and ReturnCredentials specified.  External callers should never be allowed to receive credentials in onActivityResult");
            }
            if (this.mRequestGatekeeperPasswordHandle && !this.mReturnCredentials) {
                Log.w("ChooseLockSettingsHelper", "Requested gatekeeper password handle but not requesting ReturnCredentials. Are you sure this is what you want?");
            }
            return new ChooseLockSettingsHelper(this, this.mActivity, this.mFragment);
        }

        public boolean show() {
            return build().launch();
        }
    }

    public boolean launch() {
        return launchConfirmationActivity(this.mBuilder.mRequestCode, this.mBuilder.mTitle, this.mBuilder.mHeader, this.mBuilder.mDescription, this.mBuilder.mReturnCredentials, this.mBuilder.mExternal, this.mBuilder.mForceVerifyPath, this.mBuilder.mUserId, this.mBuilder.mAlternateButton, this.mBuilder.mAllowAnyUserId, this.mBuilder.mForegroundOnly, this.mBuilder.mRequestGatekeeperPasswordHandle);
    }

    private boolean launchConfirmationActivity(int i, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z, boolean z2, boolean z3, int i2, CharSequence charSequence4, boolean z4, boolean z5, boolean z6) {
        Class<?> cls;
        Class<?> cls2;
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserManager.get(this.mActivity).getCredentialOwnerProfile(i2));
        if (keyguardStoredPasswordQuality == 65536) {
            if (z || z3) {
                cls = ConfirmLockPattern.InternalActivity.class;
            } else {
                cls = ConfirmLockPattern.class;
            }
            return launchConfirmationActivity(i, charSequence, charSequence2, charSequence3, cls, z, z2, z3, i2, charSequence4, z4, z5, z6);
        } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
            if (z || z3) {
                cls2 = ConfirmLockPassword.InternalActivity.class;
            } else {
                cls2 = ConfirmLockPassword.class;
            }
            return launchConfirmationActivity(i, charSequence, charSequence2, charSequence3, cls2, z, z2, z3, i2, charSequence4, z4, z5, z6);
        } else {
            return false;
        }
    }

    private boolean launchConfirmationActivity(int i, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, Class<?> cls, boolean z, boolean z2, boolean z3, int i2, CharSequence charSequence4, boolean z4, boolean z5, boolean z6) {
        Intent intent = new Intent();
        intent.putExtra("com.android.settings.ConfirmCredentials.title", charSequence);
        intent.putExtra("com.android.settings.ConfirmCredentials.header", charSequence2);
        intent.putExtra("com.android.settings.ConfirmCredentials.details", charSequence3);
        intent.putExtra("com.android.settings.ConfirmCredentials.darkTheme", false);
        intent.putExtra("com.android.settings.ConfirmCredentials.showCancelButton", false);
        intent.putExtra("com.android.settings.ConfirmCredentials.showWhenLocked", z2);
        intent.putExtra("com.android.settings.ConfirmCredentials.useFadeAnimation", z2);
        intent.putExtra("return_credentials", z);
        intent.putExtra("force_verify", z3);
        intent.putExtra("android.intent.extra.USER_ID", i2);
        intent.putExtra("android.app.extra.ALTERNATE_BUTTON_LABEL", charSequence4);
        intent.putExtra("foreground_only", z5);
        intent.putExtra("allow_any_user", z4);
        intent.putExtra("request_gk_pw_handle", z6);
        intent.setClassName("com.android.settings", cls.getName());
        intent.putExtra("page_transition_type", 1);
        Fragment fragment = this.mFragment;
        Intent intent2 = fragment != null ? fragment.getActivity().getIntent() : this.mActivity.getIntent();
        copyInternalExtras(intent2, intent);
        if (z2) {
            intent.addFlags(33554432);
            copyOptionalExtras(intent2, intent);
            Fragment fragment2 = this.mFragment;
            if (fragment2 != null) {
                fragment2.startActivity(intent);
            } else {
                this.mActivity.startActivity(intent);
            }
        } else {
            Fragment fragment3 = this.mFragment;
            if (fragment3 != null) {
                fragment3.startActivityForResult(intent, i);
            } else {
                this.mActivity.startActivityForResult(intent, i);
            }
        }
        return true;
    }

    private void copyOptionalExtras(Intent intent, Intent intent2) {
        IntentSender intentSender = (IntentSender) intent.getParcelableExtra("android.intent.extra.INTENT");
        if (intentSender != null) {
            intent2.putExtra("android.intent.extra.INTENT", intentSender);
        }
        int intExtra = intent.getIntExtra("android.intent.extra.TASK_ID", -1);
        if (intExtra != -1) {
            intent2.putExtra("android.intent.extra.TASK_ID", intExtra);
        }
        if (intentSender == null && intExtra == -1) {
            return;
        }
        intent2.addFlags(8388608);
        intent2.addFlags(1073741824);
    }

    private void copyInternalExtras(Intent intent, Intent intent2) {
        SetupWizardUtils.copySetupExtras(intent, intent2);
        String stringExtra = intent.getStringExtra("theme");
        if (stringExtra != null) {
            intent2.putExtra("theme", stringExtra);
        }
    }
}
