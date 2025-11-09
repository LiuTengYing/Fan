package com.android.settings.applications.intentpicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
/* loaded from: classes.dex */
public class ProgressDialogFragment extends InstrumentedDialogFragment {
    private DomainVerificationManager mDomainVerificationManager;
    private Handler mHandle;
    private String mPackage;
    private ProgressAlertDialog mProgressAlertDialog;
    private List<SupportedLinkWrapper> mSupportedLinkWrapperList;
    private SupportedLinkViewModel mViewModel;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mViewModel = (SupportedLinkViewModel) ViewModelProviders.of(getActivity()).get(SupportedLinkViewModel.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mPackage = getArguments().getString("app_package");
        this.mDomainVerificationManager = (DomainVerificationManager) getActivity().getSystemService(DomainVerificationManager.class);
        this.mHandle = new Handler(Looper.getMainLooper());
        ProgressAlertDialog createProgressAlertDialog = createProgressAlertDialog();
        this.mProgressAlertDialog = createProgressAlertDialog;
        return createProgressAlertDialog;
    }

    private ProgressAlertDialog createProgressAlertDialog() {
        FragmentActivity activity = getActivity();
        ProgressAlertDialog progressAlertDialog = new ProgressAlertDialog(activity);
        progressAlertDialog.setTitle(IntentPickerUtils.getCentralizedDialogTitle(activity.getResources().getString(R$string.app_launch_checking_links_title)));
        progressAlertDialog.setButton(-2, activity.getText(R$string.app_launch_dialog_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.intentpicker.ProgressDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialogFragment.lambda$createProgressAlertDialog$0(dialogInterface, i);
            }
        });
        progressAlertDialog.setCanceledOnTouchOutside(true);
        return progressAlertDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$createProgressAlertDialog$0(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            dialogInterface.cancel();
        }
    }

    public void showDialog(FragmentManager fragmentManager) {
        show(fragmentManager, "ProgressDialog");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        generateProgressAlertDialog();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        ProgressAlertDialog progressAlertDialog = this.mProgressAlertDialog;
        if (progressAlertDialog == null || !progressAlertDialog.isShowing()) {
            return;
        }
        this.mProgressAlertDialog.cancel();
    }

    private void generateProgressAlertDialog() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.intentpicker.ProgressDialogFragment$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ProgressDialogFragment.this.lambda$generateProgressAlertDialog$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$generateProgressAlertDialog$2() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        queryLinksInBackground();
        IntentPickerUtils.logd("queryLinksInBackground take time: " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        if (this.mProgressAlertDialog.isShowing()) {
            this.mHandle.post(new Runnable() { // from class: com.android.settings.applications.intentpicker.ProgressDialogFragment$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ProgressDialogFragment.this.lambda$generateProgressAlertDialog$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$generateProgressAlertDialog$1() {
        synchronized (this.mHandle) {
            if (this.mProgressAlertDialog.isShowing()) {
                this.mProgressAlertDialog.dismiss();
                IntentPickerUtils.logd("mProgressAlertDialog.dismiss() and isShowing: " + this.mProgressAlertDialog.isShowing());
                launchSupportedLinksDialogFragment();
            }
        }
    }

    private void queryLinksInBackground() {
        int i = 0;
        List<String> linksList = IntentPickerUtils.getLinksList(this.mDomainVerificationManager, this.mPackage, 0);
        int size = linksList.size();
        this.mSupportedLinkWrapperList = new ArrayList();
        Iterator<String> it = linksList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String next = it.next();
            SortedSet ownersForDomain = this.mDomainVerificationManager.getOwnersForDomain(next);
            this.mSupportedLinkWrapperList.add(new SupportedLinkWrapper(getActivity(), next, ownersForDomain));
            i++;
            if (!this.mProgressAlertDialog.isShowing()) {
                Log.w("ProgressDialogFragment", "Exit the background thread!!!");
                this.mSupportedLinkWrapperList.clear();
                break;
            }
            final int i2 = (i * 100) / size;
            this.mHandle.post(new Runnable() { // from class: com.android.settings.applications.intentpicker.ProgressDialogFragment$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ProgressDialogFragment.this.lambda$queryLinksInBackground$3(i2);
                }
            });
            if (ownersForDomain.size() == 0) {
                SystemClock.sleep(20L);
            }
        }
        IntentPickerUtils.logd("queryLinksInBackground : SupportedLinkWrapperList size=" + this.mSupportedLinkWrapperList.size());
        Collections.sort(this.mSupportedLinkWrapperList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$queryLinksInBackground$3(int i) {
        synchronized (this.mHandle) {
            if (!this.mProgressAlertDialog.isShowing()) {
                Log.w("ProgressDialogFragment", "Exit the UI thread");
            } else {
                this.mProgressAlertDialog.getProgressBar().setProgress(i);
            }
        }
    }

    private void launchSupportedLinksDialogFragment() {
        if (this.mSupportedLinkWrapperList.size() > 0) {
            this.mViewModel.setSupportedLinkWrapperList(this.mSupportedLinkWrapperList);
            Bundle bundle = new Bundle();
            bundle.putString("app_package", this.mPackage);
            SupportedLinksDialogFragment supportedLinksDialogFragment = new SupportedLinksDialogFragment();
            supportedLinksDialogFragment.setArguments(bundle);
            supportedLinksDialogFragment.showDialog(getActivity().getSupportFragmentManager());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ProgressAlertDialog extends AlertDialog {
        private ProgressBar mProgressBar;

        protected ProgressAlertDialog(Context context) {
            this(context, 0);
        }

        protected ProgressAlertDialog(Context context, int i) {
            super(context, i);
            init(context);
        }

        private void init(Context context) {
            View inflate = LayoutInflater.from(context).inflate(R$layout.app_launch_progress, (ViewGroup) null);
            ProgressBar progressBar = (ProgressBar) inflate.findViewById(R$id.scan_links_progressbar);
            this.mProgressBar = progressBar;
            progressBar.setProgress(0);
            this.mProgressBar.setMax(100);
            setView(inflate);
        }

        ProgressBar getProgressBar() {
            return this.mProgressBar;
        }
    }
}
