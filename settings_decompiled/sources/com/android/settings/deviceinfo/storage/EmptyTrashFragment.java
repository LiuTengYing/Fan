package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class EmptyTrashFragment extends InstrumentedDialogFragment {
    private final OnEmptyTrashCompleteListener mOnEmptyTrashCompleteListener;
    private final Fragment mParentFragment;
    private final long mTrashSize;
    private final int mUserId;

    /* loaded from: classes.dex */
    public interface OnEmptyTrashCompleteListener {
        void onEmptyTrashComplete();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1875;
    }

    public EmptyTrashFragment(Fragment fragment, int i, long j, OnEmptyTrashCompleteListener onEmptyTrashCompleteListener) {
        this.mParentFragment = fragment;
        setTargetFragment(fragment, 0);
        this.mUserId = i;
        this.mTrashSize = j;
        this.mOnEmptyTrashCompleteListener = onEmptyTrashCompleteListener;
    }

    public void show() {
        show(this.mParentFragment.getFragmentManager(), "empty_trash");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.storage_trash_dialog_title).setMessage(getActivity().getString(R$string.storage_trash_dialog_ask_message, new Object[]{StorageUtils.getStorageSizeLabel(getActivity(), this.mTrashSize)})).setPositiveButton(R$string.storage_trash_dialog_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.deviceinfo.storage.EmptyTrashFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                EmptyTrashFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        emptyTrashAsync();
    }

    private void emptyTrashAsync() {
        FragmentActivity activity = getActivity();
        try {
            final Context createPackageContextAsUser = activity.createPackageContextAsUser(activity.getApplicationContext().getPackageName(), 0, UserHandle.of(this.mUserId));
            final Bundle bundle = new Bundle();
            bundle.putInt("android:query-arg-match-trashed", 3);
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.storage.EmptyTrashFragment$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EmptyTrashFragment.this.lambda$emptyTrashAsync$2(createPackageContextAsUser, bundle);
                }
            });
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("EmptyTrashFragment", "Not able to get Context for user ID " + this.mUserId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyTrashAsync$2(Context context, Bundle bundle) {
        context.getContentResolver().delete(MediaStore.Files.getContentUri("external"), bundle);
        if (this.mOnEmptyTrashCompleteListener == null) {
            return;
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.storage.EmptyTrashFragment$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                EmptyTrashFragment.this.lambda$emptyTrashAsync$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyTrashAsync$1() {
        this.mOnEmptyTrashCompleteListener.onEmptyTrashComplete();
    }
}
