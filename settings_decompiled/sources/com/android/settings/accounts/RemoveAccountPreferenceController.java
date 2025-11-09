package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.accounts.RemoveAccountPreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.LayoutPreference;
import java.io.IOException;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class RemoveAccountPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, View.OnClickListener {
    private Account mAccount;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private Fragment mParentFragment;
    private LayoutPreference mRemoveAccountPreference;
    private UserHandle mUserHandle;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "remove_account";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RemoveAccountPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mParentFragment = fragment;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference("remove_account");
        this.mRemoveAccountPreference = layoutPreference;
        ((Button) layoutPreference.findViewById(R$id.button)).setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced;
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.logClickedPreference(this.mRemoveAccountPreference, metricsFeatureProvider.getMetricsCategory(this.mParentFragment));
        UserHandle userHandle = this.mUserHandle;
        if (userHandle != null && (checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_modify_accounts", userHandle.getIdentifier())) != null) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, checkIfRestrictionEnforced);
        } else {
            ConfirmRemoveAccountDialog.show(this.mParentFragment, this.mAccount, this.mUserHandle);
        }
    }

    public void init(Account account, UserHandle userHandle) {
        this.mAccount = account;
        this.mUserHandle = userHandle;
    }

    /* loaded from: classes.dex */
    public static class ConfirmRemoveAccountDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        private Account mAccount;
        private UserHandle mUserHandle;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 585;
        }

        public static ConfirmRemoveAccountDialog show(Fragment fragment, Account account, UserHandle userHandle) {
            if (fragment.isAdded()) {
                ConfirmRemoveAccountDialog confirmRemoveAccountDialog = new ConfirmRemoveAccountDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", account);
                bundle.putParcelable("android.intent.extra.USER", userHandle);
                confirmRemoveAccountDialog.setArguments(bundle);
                confirmRemoveAccountDialog.setTargetFragment(fragment, 0);
                confirmRemoveAccountDialog.show(fragment.getFragmentManager(), "confirmRemoveAccount");
                return confirmRemoveAccountDialog;
            }
            return null;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            Bundle arguments = getArguments();
            this.mAccount = (Account) arguments.getParcelable("account");
            this.mUserHandle = (UserHandle) arguments.getParcelable("android.intent.extra.USER");
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setTitle(R$string.really_remove_account_title).setMessage(R$string.really_remove_account_message).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R$string.remove_account_label, this).create();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            FragmentActivity activity = getTargetFragment().getActivity();
            AccountManager.get(activity).removeAccountAsUser(this.mAccount, activity, new AccountManagerCallback() { // from class: com.android.settings.accounts.RemoveAccountPreferenceController$ConfirmRemoveAccountDialog$$ExternalSyntheticLambda0
                @Override // android.accounts.AccountManagerCallback
                public final void run(AccountManagerFuture accountManagerFuture) {
                    RemoveAccountPreferenceController.ConfirmRemoveAccountDialog.this.lambda$onClick$0(accountManagerFuture);
                }
            }, null, this.mUserHandle);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0(AccountManagerFuture accountManagerFuture) {
            FragmentActivity activity = getTargetFragment().getActivity();
            if (activity == null || activity.isFinishing()) {
                Log.w("RemoveAccountPrefController", "Activity is no longer alive, skipping results");
                return;
            }
            boolean z = true;
            try {
                z = true ^ ((Bundle) accountManagerFuture.getResult()).getBoolean("booleanResult");
            } catch (AuthenticatorException | OperationCanceledException | IOException e) {
                Log.w("RemoveAccountPrefController", "Remove account error: " + e);
            }
            Log.i("RemoveAccountPrefController", "failed: " + z);
            if (z) {
                RemoveAccountFailureDialog.show(getTargetFragment());
            } else {
                activity.finish();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class RemoveAccountFailureDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 586;
        }

        public static void show(Fragment fragment) {
            if (fragment.isAdded()) {
                RemoveAccountFailureDialog removeAccountFailureDialog = new RemoveAccountFailureDialog();
                removeAccountFailureDialog.setTargetFragment(fragment, 0);
                try {
                    removeAccountFailureDialog.show(fragment.getFragmentManager(), "removeAccountFailed");
                } catch (IllegalStateException e) {
                    Log.w("RemoveAccountPrefController", "Can't show RemoveAccountFailureDialog. " + e.getMessage());
                }
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setTitle(R$string.remove_account_label).setMessage(((DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.REMOVE_ACCOUNT_FAILED_ADMIN_RESTRICTION", new Supplier() { // from class: com.android.settings.accounts.RemoveAccountPreferenceController$RemoveAccountFailureDialog$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreateDialog$0;
                    lambda$onCreateDialog$0 = RemoveAccountPreferenceController.RemoveAccountFailureDialog.this.lambda$onCreateDialog$0();
                    return lambda$onCreateDialog$0;
                }
            })).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$onCreateDialog$0() {
            return getString(R$string.remove_account_failed);
        }
    }
}
