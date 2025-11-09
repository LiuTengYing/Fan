package com.android.settings.network.telephony;

import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
/* loaded from: classes.dex */
public class SubscriptionActionDialogActivity extends FragmentActivity {
    protected SubscriptionManager mSubscriptionManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);
        setProgressState(0);
    }

    @Override // android.app.Activity
    public void finish() {
        setProgressState(0);
        super.finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showProgressDialog(String str) {
        ProgressDialogFragment.show(getFragmentManager(), str, null);
        setProgressState(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dismissProgressDialog() {
        ProgressDialogFragment.dismiss(getFragmentManager());
        setProgressState(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showErrorDialog(String str, String str2) {
        AlertDialogFragment.show(this, str, str2);
    }

    protected void setProgressState(int i) {
        getSharedPreferences("sim_action_dialog_prefs", 0).edit().putInt("progress_state", i).apply();
        Log.i("SubscriptionActionDialogActivity", "setProgressState:" + i);
    }
}
