package com.android.settings.password;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.util.Log;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.android.settings.R$anim;
import com.android.settings.R$id;
import com.android.settings.R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.ConfirmLockPattern;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
public abstract class ConfirmDeviceCredentialBaseActivity extends SettingsActivity {
    private ConfirmCredentialTheme mConfirmCredentialTheme;
    private boolean mEnterAnimationPending;
    private boolean mFirstTimeVisible = true;
    private boolean mIsKeyguardLocked = false;
    private boolean mRestoring;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ConfirmCredentialTheme {
        NORMAL,
        DARK,
        WORK
    }

    @Override // com.android.settings.core.SettingsBaseActivity
    protected boolean isToolbarEnabled() {
        return false;
    }

    private boolean isInternalActivity() {
        return (this instanceof ConfirmLockPassword.InternalActivity) || (this instanceof ConfirmLockPattern.InternalActivity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        boolean z;
        try {
            if (UserManager.get(this).isManagedProfile(Utils.getCredentialOwnerUserId(this, Utils.getUserIdFromBundle(this, getIntent().getExtras(), isInternalActivity())))) {
                setTheme(SetupWizardUtils.getTheme(this, getIntent()));
                this.mConfirmCredentialTheme = ConfirmCredentialTheme.WORK;
            } else if (getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.darkTheme", false)) {
                setTheme(R$style.Theme_ConfirmDeviceCredentialsDark);
                this.mConfirmCredentialTheme = ConfirmCredentialTheme.DARK;
            } else {
                setTheme(SetupWizardUtils.getTheme(this, getIntent()));
                this.mConfirmCredentialTheme = ConfirmCredentialTheme.NORMAL;
            }
            ThemeHelper.trySetDynamicColor(this);
            super.onCreate(bundle);
            if (this.mConfirmCredentialTheme == ConfirmCredentialTheme.NORMAL) {
                findViewById(R$id.content_parent).setFitsSystemWindows(false);
            }
            getWindow().addFlags(8192);
            if (bundle == null) {
                z = ((KeyguardManager) getSystemService(KeyguardManager.class)).isKeyguardLocked();
            } else {
                z = bundle.getBoolean("STATE_IS_KEYGUARD_LOCKED", false);
            }
            this.mIsKeyguardLocked = z;
            if (z && getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showWhenLocked", false)) {
                getWindow().addFlags(524288);
            }
            setTitle(getIntent().getStringExtra("com.android.settings.ConfirmCredentials.title"));
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
            }
            this.mRestoring = bundle != null;
        } catch (SecurityException e) {
            Log.e("ConfirmDeviceCredentialBaseActivity", "Invalid user Id supplied", e);
            finish();
        }
    }

    @Override // com.android.settings.SettingsActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("STATE_IS_KEYGUARD_LOCKED", this.mIsKeyguardLocked);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (isChangingConfigurations() || this.mRestoring || this.mConfirmCredentialTheme != ConfirmCredentialTheme.DARK || !this.mFirstTimeVisible) {
            return;
        }
        this.mFirstTimeVisible = false;
        prepareEnterAnimation();
        this.mEnterAnimationPending = true;
    }

    private ConfirmDeviceCredentialBaseFragment getFragment() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R$id.main_content);
        if (findFragmentById == null || !(findFragmentById instanceof ConfirmDeviceCredentialBaseFragment)) {
            return null;
        }
        return (ConfirmDeviceCredentialBaseFragment) findFragmentById;
    }

    @Override // android.app.Activity
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (this.mEnterAnimationPending) {
            startEnterAnimation();
            this.mEnterAnimationPending = false;
        }
    }

    @Override // com.android.settings.SettingsActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        boolean booleanExtra = getIntent().getBooleanExtra("foreground_only", false);
        if (isChangingConfigurations() || !booleanExtra) {
            return;
        }
        finish();
    }

    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        new Handler(Looper.myLooper()).postDelayed(new Runnable() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ConfirmDeviceCredentialBaseActivity.lambda$onDestroy$0();
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDestroy$0() {
        System.gc();
        System.runFinalization();
        System.gc();
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        if (getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.useFadeAnimation", false)) {
            overridePendingTransition(0, R$anim.confirm_credential_biometric_transition_exit);
        }
    }

    public void prepareEnterAnimation() {
        getFragment().prepareEnterAnimation();
    }

    public void startEnterAnimation() {
        getFragment().startEnterAnimation();
    }

    public ConfirmCredentialTheme getConfirmCredentialTheme() {
        return this.mConfirmCredentialTheme;
    }
}
