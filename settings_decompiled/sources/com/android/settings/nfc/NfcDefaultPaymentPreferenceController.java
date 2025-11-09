package com.android.settings.nfc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.applications.defaultapps.DefaultAppPreferenceController;
import com.android.settings.nfc.PaymentBackend;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.List;
/* loaded from: classes.dex */
public class NfcDefaultPaymentPreferenceController extends DefaultAppPreferenceController implements PaymentBackend.Callback, LifecycleObserver, OnResume, OnPause {
    private Context mContext;
    private PaymentBackend mPaymentBackend;
    private Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "nfc_payment_app";
    }

    public NfcDefaultPaymentPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mContext = context;
        this.mPaymentBackend = new PaymentBackend(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") && NfcAdapter.getDefaultAdapter(this.mContext) != null) {
            if (this.mPaymentBackend == null) {
                this.mPaymentBackend = new PaymentBackend(this.mContext);
            }
            List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
            return (paymentAppInfos == null || paymentAppInfos.isEmpty()) ? false : true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.registerCallback(this);
            this.mPaymentBackend.onResume();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.unregisterCallback(this);
            this.mPaymentBackend.onPause();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setIconSpaceReserved(true);
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        updateState(this.mPreference);
    }

    /* loaded from: classes.dex */
    public static class PaymentDefaultAppInfo extends DefaultAppInfo {
        public PaymentBackend.PaymentAppInfo mInfo;

        public PaymentDefaultAppInfo(Context context, PackageManager packageManager, int i, PaymentBackend.PaymentAppInfo paymentAppInfo) {
            super(context, packageManager, i, paymentAppInfo.componentName);
            this.mInfo = paymentAppInfo;
        }

        @Override // com.android.settingslib.applications.DefaultAppInfo, com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return this.mInfo.icon;
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        PaymentBackend.PaymentAppInfo defaultApp;
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend == null || (defaultApp = paymentBackend.getDefaultApp()) == null) {
            return null;
        }
        return new PaymentDefaultAppInfo(this.mContext, this.mPackageManager, defaultApp.userHandle.getIdentifier(), defaultApp);
    }
}
