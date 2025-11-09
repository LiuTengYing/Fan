package com.android.settings.nfc;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Pair;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.PaymentBackend;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;
/* loaded from: classes.dex */
public class NfcForegroundPreferenceController extends BasePreferenceController implements PaymentBackend.Callback, Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private final String[] mListEntries;
    private final String[] mListValues;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private PaymentBackend mPaymentBackend;
    private ListPreference mPreference;

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

    public NfcForegroundPreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mListValues = context.getResources().getStringArray(R$array.nfc_payment_favor_values);
        this.mListEntries = context.getResources().getStringArray(R$array.nfc_payment_favor);
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
        PaymentBackend paymentBackend;
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos;
        return (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") || (paymentBackend = this.mPaymentBackend) == null || (paymentAppInfos = paymentBackend.getPaymentAppInfos()) == null || paymentAppInfos.isEmpty()) ? 3 : 0;
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setIconSpaceReserved(false);
            listPreference.setValue(this.mListValues[this.mPaymentBackend.isForegroundMode() ? 1 : 0]);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mListEntries[this.mPaymentBackend.isForegroundMode() ? 1 : 0];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            String str = (String) obj;
            listPreference.setSummary(this.mListEntries[listPreference.findIndexOfValue(str)]);
            boolean z = Integer.parseInt(str) != 0;
            this.mPaymentBackend.setForegroundMode(z);
            this.mMetricsFeatureProvider.action(this.mContext, z ? 1622 : 1623, new Pair[0]);
            return true;
        }
        return false;
    }
}
