package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.SidecarFragment;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.ConfirmDialogFragment;
import java.util.List;
/* loaded from: classes.dex */
public class DeleteEuiccSubscriptionDialogActivity extends SubscriptionActionDialogActivity implements SidecarFragment.Listener, ConfirmDialogFragment.OnConfirmListener {
    private DeleteEuiccSubscriptionSidecar mDeleteEuiccSubscriptionSidecar;
    private SubscriptionInfo mSubscriptionToBeDeleted;
    private List<SubscriptionInfo> mSubscriptionsToBeDeleted;

    public static Intent getIntent(Context context, int i) {
        Intent intent = new Intent(context, DeleteEuiccSubscriptionDialogActivity.class);
        intent.putExtra("sub_id", i);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.network.telephony.SubscriptionActionDialogActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("sub_id", -1);
        this.mSubscriptionToBeDeleted = SubscriptionUtil.getSubById(this.mSubscriptionManager, intExtra);
        List<SubscriptionInfo> findAllSubscriptionsInGroup = SubscriptionUtil.findAllSubscriptionsInGroup(this.mSubscriptionManager, intExtra);
        this.mSubscriptionsToBeDeleted = findAllSubscriptionsInGroup;
        if (this.mSubscriptionToBeDeleted == null || findAllSubscriptionsInGroup.isEmpty()) {
            Log.e("DeleteEuiccSubscriptionDialogActivity", "Cannot find subscription with sub ID: " + intExtra);
            finish();
            return;
        }
        this.mDeleteEuiccSubscriptionSidecar = DeleteEuiccSubscriptionSidecar.get(getFragmentManager());
        if (bundle == null) {
            showDeleteSimConfirmDialog();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mDeleteEuiccSubscriptionSidecar.addListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.mDeleteEuiccSubscriptionSidecar.removeListener(this);
        super.onPause();
    }

    @Override // com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mDeleteEuiccSubscriptionSidecar) {
            handleDeleteEuiccSubscriptionSidecarStateChange();
        }
    }

    @Override // com.android.settings.network.telephony.ConfirmDialogFragment.OnConfirmListener
    public void onConfirm(int i, boolean z, int i2) {
        if (!z) {
            finish();
        } else if (i == 1) {
            Log.i("DeleteEuiccSubscriptionDialogActivity", "Subscription deletion confirmed");
            showProgressDialog(getString(R$string.erasing_sim));
            this.mDeleteEuiccSubscriptionSidecar.run(this.mSubscriptionsToBeDeleted);
        } else {
            Log.e("DeleteEuiccSubscriptionDialogActivity", "Unrecognized confirmation dialog tag: " + i);
        }
    }

    private void handleDeleteEuiccSubscriptionSidecarStateChange() {
        int state = this.mDeleteEuiccSubscriptionSidecar.getState();
        if (state == 2) {
            Log.i("DeleteEuiccSubscriptionDialogActivity", "Successfully delete the subscription.");
            this.mDeleteEuiccSubscriptionSidecar.reset();
            dismissProgressDialog();
            finish();
        } else if (state != 3) {
        } else {
            Log.e("DeleteEuiccSubscriptionDialogActivity", "Failed to delete the subscription.");
            this.mDeleteEuiccSubscriptionSidecar.reset();
            showErrorDialog(getString(R$string.erase_sim_fail_title), getString(R$string.erase_sim_fail_text));
        }
    }

    private void showDeleteSimConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 1, getString(R$string.erase_sim_dialog_title), getString(R$string.erase_sim_dialog_text, new Object[]{SubscriptionUtil.getUniqueSubscriptionDisplayName(this.mSubscriptionToBeDeleted, this)}), getString(R$string.erase_sim_confirm_button), getString(R$string.cancel));
    }
}
