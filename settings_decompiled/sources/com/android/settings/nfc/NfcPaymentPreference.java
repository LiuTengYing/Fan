package com.android.settings.nfc;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.CustomDialogPreferenceCompat;
/* loaded from: classes.dex */
public class NfcPaymentPreference extends CustomDialogPreferenceCompat {
    private Listener mListener;

    /* loaded from: classes.dex */
    interface Listener {
        void onBindViewHolder(PreferenceViewHolder preferenceViewHolder);

        void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener);
    }

    public NfcPaymentPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public NfcPaymentPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public NfcPaymentPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(Listener listener) {
        this.mListener = listener;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onBindViewHolder(preferenceViewHolder);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onPrepareDialogBuilder(builder, onClickListener);
        }
    }
}
