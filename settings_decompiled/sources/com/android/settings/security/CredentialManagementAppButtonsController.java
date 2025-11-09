package com.android.settings.security;

import android.app.Dialog;
import android.app.admin.DevicePolicyEventLogger;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.security.CredentialManagementAppButtonsController;
import com.android.settingslib.widget.ActionButtonsPreference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class CredentialManagementAppButtonsController extends BasePreferenceController {
    private static final String TAG = "CredentialManagementApp";
    private final ExecutorService mExecutor;
    private Fragment mFragment;
    private final Handler mHandler;
    private boolean mHasCredentialManagerPackage;
    private final int mRemoveIcon;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CredentialManagementAppButtonsController(Context context, String str) {
        super(context, str);
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mHandler = new Handler(Looper.getMainLooper());
        if (context.getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mRemoveIcon = R$drawable.ic_redo_24;
        } else {
            this.mRemoveIcon = R$drawable.ic_undo_24;
        }
    }

    public void setParentFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(final PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mExecutor.execute(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppButtonsController.this.lambda$displayPreference$1(preferenceScreen);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(final PreferenceScreen preferenceScreen) {
        try {
            this.mHasCredentialManagerPackage = KeyChain.bind(this.mContext).getService().hasCredentialManagementApp();
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to display credential management app buttons");
        }
        this.mHandler.post(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppButtonsController.this.lambda$displayPreference$0(preferenceScreen);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: displayButtons */
    public void lambda$displayPreference$0(PreferenceScreen preferenceScreen) {
        if (this.mHasCredentialManagerPackage) {
            ((ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey())).setButton1Text(R$string.uninstall_certs_credential_management_app).setButton1Icon(R$drawable.ic_upload).setButton1OnClickListener(new View.OnClickListener() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CredentialManagementAppButtonsController.this.lambda$displayButtons$2(view);
                }
            }).setButton2Text(R$string.remove_credential_management_app).setButton2Icon(this.mRemoveIcon).setButton2OnClickListener(new View.OnClickListener() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CredentialManagementAppButtonsController.this.lambda$displayButtons$3(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayButtons$2(View view) {
        uninstallCertificates();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayButtons$3(View view) {
        showRemoveCredentialManagementAppDialog();
    }

    private void uninstallCertificates() {
        this.mExecutor.execute(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                CredentialManagementAppButtonsController.this.lambda$uninstallCertificates$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$uninstallCertificates$4() {
        try {
            IKeyChainService service = KeyChain.bind(this.mContext).getService();
            for (String str : service.getCredentialManagementAppPolicy().getAliases()) {
                service.removeKeyPair(str);
            }
        } catch (RemoteException | InterruptedException unused) {
            Log.e(TAG, "Unable to uninstall certificates");
        }
    }

    private void showRemoveCredentialManagementAppDialog() {
        RemoveCredentialManagementAppDialog.newInstance().show(this.mFragment.getParentFragmentManager(), RemoveCredentialManagementAppDialog.class.getName());
    }

    /* loaded from: classes.dex */
    public static class RemoveCredentialManagementAppDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 1895;
        }

        public static RemoveCredentialManagementAppDialog newInstance() {
            return new RemoveCredentialManagementAppDialog();
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getContext(), R$style.Theme_AlertDialog).setTitle(R$string.remove_credential_management_app_dialog_title).setMessage(R$string.remove_credential_management_app_dialog_message).setPositiveButton(R$string.remove_credential_management_app, new DialogInterface.OnClickListener() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$RemoveCredentialManagementAppDialog$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    CredentialManagementAppButtonsController.RemoveCredentialManagementAppDialog.this.lambda$onCreateDialog$0(dialogInterface, i);
                }
            }).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$RemoveCredentialManagementAppDialog$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    CredentialManagementAppButtonsController.RemoveCredentialManagementAppDialog.this.lambda$onCreateDialog$1(dialogInterface, i);
                }
            }).create();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
            removeCredentialManagementApp();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
            dismiss();
        }

        private void removeCredentialManagementApp() {
            Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.android.settings.security.CredentialManagementAppButtonsController$RemoveCredentialManagementAppDialog$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManagementAppButtonsController.RemoveCredentialManagementAppDialog.this.lambda$removeCredentialManagementApp$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeCredentialManagementApp$2() {
            try {
                KeyChain.bind(getContext()).getService().removeCredentialManagementApp();
                DevicePolicyEventLogger.createEvent(187).write();
                getParentFragment().getActivity().finish();
            } catch (RemoteException | InterruptedException unused) {
                Log.e(CredentialManagementAppButtonsController.TAG, "Unable to remove the credential management app");
            }
        }
    }
}
