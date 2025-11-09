package com.android.settings.nfc;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.NfcPaymentPreference;
import com.android.settings.nfc.PaymentBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;
/* loaded from: classes.dex */
public class NfcPaymentPreferenceController extends BasePreferenceController implements PaymentBackend.Callback, View.OnClickListener, NfcPaymentPreference.Listener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "NfcPaymentController";
    private final NfcPaymentAdapter mAdapter;
    private PaymentBackend mPaymentBackend;
    private NfcPaymentPreference mPreference;
    private ImageView mSettingsButtonView;

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

    public NfcPaymentPreferenceController(Context context, String str) {
        super(context, str);
        this.mAdapter = new NfcPaymentAdapter(context);
    }

    public void setPaymentBackend(PaymentBackend paymentBackend) {
        this.mPaymentBackend = paymentBackend;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.registerCallback(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.unregisterCallback(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") && NfcAdapter.getDefaultAdapter(this.mContext) != null) {
            if (this.mPaymentBackend == null) {
                this.mPaymentBackend = new PaymentBackend(this.mContext);
            }
            List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
            return (paymentAppInfos == null || paymentAppInfos.isEmpty()) ? 3 : 0;
        }
        return 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        NfcPaymentPreference nfcPaymentPreference = (NfcPaymentPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = nfcPaymentPreference;
        if (nfcPaymentPreference != null) {
            nfcPaymentPreference.initialize(this);
        }
    }

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R$id.settings_button);
        this.mSettingsButtonView = imageView;
        imageView.setOnClickListener(this);
        updateSettingsVisibility();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos != null) {
            this.mAdapter.updateApps((PaymentBackend.PaymentAppInfo[]) paymentAppInfos.toArray(new PaymentBackend.PaymentAppInfo[paymentAppInfos.size()]));
        }
        super.updateState(preference);
        updateSettingsVisibility();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        if (defaultApp != null) {
            return ((Object) defaultApp.label) + " (" + ((UserManager) this.mContext.createContextAsUser(defaultApp.userHandle, 0).getSystemService(UserManager.class)).getUserName() + ")";
        }
        return this.mContext.getText(R$string.nfc_payment_default_not_set);
    }

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setSingleChoiceItems(this.mAdapter, 0, onClickListener);
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        updateState(this.mPreference);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        if (defaultApp == null || defaultApp.settingsComponent == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(defaultApp.settingsComponent);
        intent.addFlags(268435456);
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "Settings activity not found.");
        }
    }

    private void updateSettingsVisibility() {
        if (this.mSettingsButtonView != null) {
            PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
            if (defaultApp == null || defaultApp.settingsComponent == null) {
                this.mSettingsButtonView.setVisibility(8);
            } else {
                this.mSettingsButtonView.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NfcPaymentAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private PaymentBackend.PaymentAppInfo[] appInfos;
        private final LayoutInflater mLayoutInflater;

        public NfcPaymentAdapter(Context context) {
            this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void updateApps(PaymentBackend.PaymentAppInfo[] paymentAppInfoArr) {
            this.appInfos = paymentAppInfoArr;
            notifyDataSetChanged();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            PaymentBackend.PaymentAppInfo[] paymentAppInfoArr = this.appInfos;
            if (paymentAppInfoArr != null) {
                return paymentAppInfoArr.length;
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public PaymentBackend.PaymentAppInfo getItem(int i) {
            return this.appInfos[i];
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.appInfos[i].componentName.hashCode();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            PaymentBackend.PaymentAppInfo paymentAppInfo = this.appInfos[i];
            if (view == null) {
                view = this.mLayoutInflater.inflate(R$layout.nfc_payment_option, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.radioButton = (RadioButton) view.findViewById(R$id.button);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            UserManager userManager = (UserManager) ((AbstractPreferenceController) NfcPaymentPreferenceController.this).mContext.createContextAsUser(paymentAppInfo.userHandle, 0).getSystemService(UserManager.class);
            viewHolder.radioButton.setOnCheckedChangeListener(null);
            viewHolder.radioButton.setChecked(paymentAppInfo.isDefault);
            RadioButton radioButton = viewHolder.radioButton;
            radioButton.setContentDescription(((Object) paymentAppInfo.label) + " (" + userManager.getUserName() + ")");
            viewHolder.radioButton.setOnCheckedChangeListener(this);
            viewHolder.radioButton.setTag(paymentAppInfo);
            RadioButton radioButton2 = viewHolder.radioButton;
            radioButton2.setText(((Object) paymentAppInfo.label) + " (" + userManager.getUserName() + ")");
            return view;
        }

        /* loaded from: classes.dex */
        private class ViewHolder {
            public RadioButton radioButton;

            private ViewHolder() {
            }
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            makeDefault((PaymentBackend.PaymentAppInfo) compoundButton.getTag());
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            makeDefault((PaymentBackend.PaymentAppInfo) view.getTag());
        }

        private void makeDefault(PaymentBackend.PaymentAppInfo paymentAppInfo) {
            if (!paymentAppInfo.isDefault) {
                NfcPaymentPreferenceController.this.mPaymentBackend.setDefaultPaymentApp(paymentAppInfo.componentName, paymentAppInfo.userHandle.getIdentifier());
            }
            Dialog dialog = NfcPaymentPreferenceController.this.mPreference.getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
