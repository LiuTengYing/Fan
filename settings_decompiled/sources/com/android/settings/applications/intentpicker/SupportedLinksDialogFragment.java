package com.android.settings.applications.intentpicker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.List;
import java.util.Set;
import java.util.UUID;
/* loaded from: classes.dex */
public class SupportedLinksDialogFragment extends InstrumentedDialogFragment {
    private String mPackage;
    private List<SupportedLinkWrapper> mSupportedLinkWrapperList;
    private SupportedLinkViewModel mViewModel;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPackage = getArguments().getString("app_package");
        SupportedLinkViewModel supportedLinkViewModel = (SupportedLinkViewModel) ViewModelProviders.of(getActivity()).get(SupportedLinkViewModel.class);
        this.mViewModel = supportedLinkViewModel;
        this.mSupportedLinkWrapperList = supportedLinkViewModel.getSupportedLinkWrapperList();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        return new AlertDialog.Builder(activity).setTitle(IntentPickerUtils.getCentralizedDialogTitle(getSupportedLinksTitle())).setAdapter(new SupportedLinksAdapter(activity, this.mSupportedLinkWrapperList), null).setCancelable(true).setPositiveButton(R$string.app_launch_supported_links_add, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.intentpicker.SupportedLinksDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                SupportedLinksDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(R$string.app_launch_dialog_cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        doSelectedAction();
    }

    public void showDialog(FragmentManager fragmentManager) {
        show(fragmentManager, "SupportedLinksDialog");
    }

    private String getSupportedLinksTitle() {
        int size = this.mSupportedLinkWrapperList.size();
        return getResources().getQuantityString(R$plurals.app_launch_supported_links_title, size, Integer.valueOf(size));
    }

    private void doSelectedAction() {
        DomainVerificationManager domainVerificationManager = (DomainVerificationManager) getActivity().getSystemService(DomainVerificationManager.class);
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(domainVerificationManager, this.mPackage);
        if (domainVerificationUserState == null || this.mSupportedLinkWrapperList == null) {
            return;
        }
        updateUserSelection(domainVerificationManager, domainVerificationUserState);
        displaySelectedItem();
    }

    private void updateUserSelection(DomainVerificationManager domainVerificationManager, DomainVerificationUserState domainVerificationUserState) {
        ArraySet arraySet = new ArraySet();
        for (SupportedLinkWrapper supportedLinkWrapper : this.mSupportedLinkWrapperList) {
            if (supportedLinkWrapper.isChecked()) {
                arraySet.add(supportedLinkWrapper.getHost());
            }
        }
        if (arraySet.size() > 0) {
            setDomainVerificationUserSelection(domainVerificationManager, domainVerificationUserState.getIdentifier(), arraySet, true);
        }
    }

    private void setDomainVerificationUserSelection(DomainVerificationManager domainVerificationManager, UUID uuid, Set<String> set, boolean z) {
        try {
            domainVerificationManager.setDomainVerificationUserSelection(uuid, set, z);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("SupportedLinksDialogFrg", "addSelectedItems : " + e.getMessage());
        }
    }

    private void displaySelectedItem() {
        for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
            if (fragment instanceof AppLaunchSettings) {
                ((AppLaunchSettings) fragment).addSelectedLinksPreference();
            }
        }
    }
}
