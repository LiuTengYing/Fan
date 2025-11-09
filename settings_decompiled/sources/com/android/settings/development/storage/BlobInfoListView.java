package com.android.settings.development.storage;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.blob.BlobInfo;
import android.app.blob.BlobStoreManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R$string;
import java.io.IOException;
import java.util.List;
/* loaded from: classes.dex */
public class BlobInfoListView extends ListActivity {
    private BlobListAdapter mAdapter;
    private BlobStoreManager mBlobStoreManager;
    private Context mContext;
    private LayoutInflater mInflater;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        this.mBlobStoreManager = (BlobStoreManager) getSystemService(BlobStoreManager.class);
        this.mInflater = (LayoutInflater) getSystemService(LayoutInflater.class);
        BlobListAdapter blobListAdapter = new BlobListAdapter(this);
        this.mAdapter = blobListAdapter;
        setListAdapter(blobListAdapter);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        queryBlobsAndUpdateList();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 8108 && i2 == -1) {
            Toast.makeText(this, R$string.shared_data_delete_failure_text, 1).show();
        }
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView listView, View view, int i, long j) {
        BlobInfo item = this.mAdapter.getItem(i);
        if (CollectionUtils.isEmpty(item.getLeases())) {
            showDeleteBlobDialog(item);
            return;
        }
        Intent intent = new Intent(this, LeaseInfoListView.class);
        intent.putExtra("BLOB_KEY", (Parcelable) item);
        startActivityForResult(intent, 8108);
    }

    private void showDeleteBlobDialog(BlobInfo blobInfo) {
        new AlertDialog.Builder(this.mContext).setMessage(R$string.shared_data_no_accessors_dialog_text).setPositiveButton(17039370, getDialogOnClickListener(blobInfo)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create().show();
    }

    private DialogInterface.OnClickListener getDialogOnClickListener(final BlobInfo blobInfo) {
        return new DialogInterface.OnClickListener() { // from class: com.android.settings.development.storage.BlobInfoListView$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BlobInfoListView.this.lambda$getDialogOnClickListener$0(blobInfo, dialogInterface, i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogOnClickListener$0(BlobInfo blobInfo, DialogInterface dialogInterface, int i) {
        try {
            this.mBlobStoreManager.deleteBlob(blobInfo);
        } catch (IOException e) {
            Log.e("BlobInfoListView", "Unable to delete blob: " + e.getMessage());
            Toast.makeText(this, R$string.shared_data_delete_failure_text, 1).show();
        }
        queryBlobsAndUpdateList();
    }

    private void queryBlobsAndUpdateList() {
        try {
            this.mAdapter.updateList(this.mBlobStoreManager.queryBlobsForUser(UserHandle.CURRENT));
        } catch (IOException e) {
            Log.e("BlobInfoListView", "Unable to fetch blobs for current user: " + e.getMessage());
            Toast.makeText(this, R$string.shared_data_query_failure_text, 1).show();
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BlobListAdapter extends ArrayAdapter<BlobInfo> {
        BlobListAdapter(Context context) {
            super(context, 0);
        }

        void updateList(List<BlobInfo> list) {
            clear();
            if (list.isEmpty()) {
                BlobInfoListView.this.finish();
            } else {
                addAll(list);
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            BlobInfoViewHolder createOrRecycle = BlobInfoViewHolder.createOrRecycle(BlobInfoListView.this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            BlobInfo item = getItem(i);
            createOrRecycle.blobLabel.setText(item.getLabel());
            createOrRecycle.blobId.setText(BlobInfoListView.this.getString(R$string.blob_id_text, new Object[]{Long.valueOf(item.getId())}));
            createOrRecycle.blobExpiry.setText(formatExpiryTime(item.getExpiryTimeMs()));
            createOrRecycle.blobSize.setText(SharedDataUtils.formatSize(item.getSizeBytes()));
            return view2;
        }

        private String formatExpiryTime(long j) {
            return j == 0 ? BlobInfoListView.this.getString(R$string.blob_never_expires_text) : BlobInfoListView.this.getString(R$string.blob_expires_text, new Object[]{SharedDataUtils.formatTime(j)});
        }
    }
}
