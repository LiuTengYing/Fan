package com.android.settings.applications.specialaccess;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.UserManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class DefaultPaymentSettingsPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private final NfcAdapter mNfcAdapter;
    private final PackageManager mPackageManager;
    private PaymentSettingsEnabler mPaymentSettingsEnabler;
    private final UserManager mUserManager;

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

    public DefaultPaymentSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (!isAvailable()) {
            this.mPaymentSettingsEnabler = null;
            return;
        }
        this.mPaymentSettingsEnabler = new PaymentSettingsEnabler(this.mContext, preferenceScreen.findPreference(getPreferenceKey()));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        PaymentSettingsEnabler paymentSettingsEnabler = this.mPaymentSettingsEnabler;
        if (paymentSettingsEnabler != null) {
            paymentSettingsEnabler.resume();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        PaymentSettingsEnabler paymentSettingsEnabler = this.mPaymentSettingsEnabler;
        if (paymentSettingsEnabler != null) {
            paymentSettingsEnabler.pause();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mPackageManager.hasSystemFeature("android.hardware.nfc") && this.mPackageManager.hasSystemFeature("android.hardware.nfc.hce")) {
            if (this.mUserManager.isGuestUser()) {
                return 4;
            }
            NfcAdapter nfcAdapter = this.mNfcAdapter;
            if (nfcAdapter == null) {
                return 2;
            }
            return !nfcAdapter.isEnabled() ? 5 : 0;
        }
        return 3;
    }
}
