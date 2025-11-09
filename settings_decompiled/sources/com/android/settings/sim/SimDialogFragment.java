package com.android.settings.sim;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.SubscriptionsChangeListener;
/* loaded from: classes.dex */
public abstract class SimDialogFragment extends InstrumentedDialogFragment implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    protected boolean mWasDismissed = false;

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    public abstract void updateDialog();

    /* JADX INFO: Access modifiers changed from: protected */
    public static Bundle initArguments(int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_type", i);
        bundle.putInt("title_id", i2);
        return bundle;
    }

    public int getDialogType() {
        return getArguments().getInt("dialog_type");
    }

    public int getTitleResId() {
        return getArguments().getInt("title_id");
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("SimDialogFragment", "Dialog Attached.");
        this.mWasDismissed = false;
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mChangeListener.stop();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mChangeListener.start();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("SimDialogFragment", "Dialog Dismissed.");
        this.mWasDismissed = true;
        super.onDismiss(dialogInterface);
        SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
        if (simDialogActivity == null || simDialogActivity.isFinishing()) {
            return;
        }
        simDialogActivity.onFragmentDismissed(this);
    }

    @Override // androidx.fragment.app.DialogFragment
    public void dismiss() {
        this.mChangeListener.stop();
        super.dismiss();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        updateDialog();
    }
}
