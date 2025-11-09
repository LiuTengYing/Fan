package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class StorageRenameFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 563;
    }

    public static void show(Fragment fragment, VolumeInfo volumeInfo) {
        StorageRenameFragment storageRenameFragment = new StorageRenameFragment();
        storageRenameFragment.setTargetFragment(fragment, 0);
        Bundle bundle = new Bundle();
        bundle.putString("android.os.storage.extra.FS_UUID", volumeInfo.getFsUuid());
        storageRenameFragment.setArguments(bundle);
        storageRenameFragment.show(fragment.getFragmentManager(), "rename");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        final StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        final String string = getArguments().getString("android.os.storage.extra.FS_UUID");
        VolumeRecord findRecordByUuid = storageManager.findRecordByUuid(string);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = LayoutInflater.from(builder.getContext()).inflate(R$layout.dialog_edittext, (ViewGroup) null, false);
        final EditText editText = (EditText) inflate.findViewById(R$id.edittext);
        editText.setText(findRecordByUuid.getNickname());
        editText.requestFocus();
        return builder.setTitle(R$string.storage_rename_title).setView(inflate).setPositiveButton(R$string.save, new DialogInterface.OnClickListener() { // from class: com.android.settings.deviceinfo.storage.StorageRenameFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                StorageRenameFragment.lambda$onCreateDialog$0(storageManager, string, editText, dialogInterface, i);
            }
        }).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$0(StorageManager storageManager, String str, EditText editText, DialogInterface dialogInterface, int i) {
        storageManager.setVolumeNickname(str, editText.getText().toString());
    }
}
