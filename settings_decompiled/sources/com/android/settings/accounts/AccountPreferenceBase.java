package com.android.settings.accounts;

import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.utils.ThreadUtils;
import java.text.DateFormat;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AccountPreferenceBase extends SettingsPreferenceFragment implements AuthenticatorHelper.OnAccountsUpdateListener {
    protected static final boolean VERBOSE = Log.isLoggable("AccountPreferenceBase", 2);
    protected AccountTypePreferenceLoader mAccountTypePreferenceLoader;
    protected AuthenticatorHelper mAuthenticatorHelper;
    private DateFormat mDateFormat;
    private Object mStatusChangeListenerHandle;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() { // from class: com.android.settings.accounts.AccountPreferenceBase$$ExternalSyntheticLambda0
        @Override // android.content.SyncStatusObserver
        public final void onStatusChanged(int i) {
            AccountPreferenceBase.this.lambda$new$1(i);
        }
    };
    private DateFormat mTimeFormat;
    private UserManager mUm;
    protected UserHandle mUserHandle;

    @Override // com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
    }

    protected void onAuthDescriptionsUpdated() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: onSyncStateUpdated */
    public void lambda$new$0() {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUm = (UserManager) getSystemService("user");
        FragmentActivity activity = getActivity();
        this.mUserHandle = Utils.getSecureTargetUser(activity.getActivityToken(), this.mUm, getArguments(), activity.getIntent().getExtras());
        AuthenticatorHelper authenticatorHelper = new AuthenticatorHelper(activity, this.mUserHandle, this);
        this.mAuthenticatorHelper = authenticatorHelper;
        this.mAccountTypePreferenceLoader = new AccountTypePreferenceLoader(this, authenticatorHelper, this.mUserHandle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        this.mDateFormat = android.text.format.DateFormat.getDateFormat(activity);
        this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(activity);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
        lambda$new$0();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.accounts.AccountPreferenceBase$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AccountPreferenceBase.this.lambda$new$0();
            }
        });
    }

    public void updateAuthDescriptions() {
        this.mAuthenticatorHelper.updateAuthDescriptions(getActivity());
        onAuthDescriptionsUpdated();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable getDrawableForType(String str) {
        return this.mAuthenticatorHelper.getDrawableForType(getActivity(), str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence getLabelForType(String str) {
        return this.mAuthenticatorHelper.getLabelForType(getActivity(), str);
    }
}
