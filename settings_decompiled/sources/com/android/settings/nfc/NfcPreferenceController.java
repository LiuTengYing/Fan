package com.android.settings.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.io.IOException;
/* loaded from: classes.dex */
public class NfcPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnResume, OnPause, OnMainSwitchChangeListener {
    public static final String KEY_TOGGLE_NFC = "toggle_nfc";
    private final NfcAdapter mNfcAdapter;
    private NfcEnabler mNfcEnabler;
    private MainSwitchPreference mPreference;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean hasAsyncUpdate() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NfcPreferenceController(Context context, String str) {
        super(context, str);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (!isAvailable()) {
            this.mNfcEnabler = null;
            return;
        }
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = mainSwitchPreference;
        mainSwitchPreference.addOnSwitchChangeListener(this);
        this.mNfcEnabler = new NfcEnabler(this.mContext, this.mPreference);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z != this.mNfcAdapter.isEnabled()) {
            setChecked(z);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mNfcAdapter.isEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            this.mNfcAdapter.enable();
            return true;
        }
        this.mNfcAdapter.disable();
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mNfcAdapter != null ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_connected_devices;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return NfcSliceWorker.class;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.resume();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.pause();
        }
    }

    public static boolean shouldTurnOffNFCInAirplaneMode(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "airplane_mode_radios");
        return string != null && string.contains("nfc");
    }

    public static boolean isToggleableInAirplaneMode(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "airplane_mode_toggleable_radios");
        return string != null && string.contains("nfc");
    }

    /* loaded from: classes.dex */
    public static class NfcSliceWorker extends SliceBackgroundWorker<Void> {
        private static final IntentFilter NFC_FILTER = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
        private NfcUpdateReceiver mUpdateObserver;

        public NfcSliceWorker(Context context, Uri uri) {
            super(context, uri);
            this.mUpdateObserver = new NfcUpdateReceiver(this);
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSlicePinned() {
            getContext().registerReceiver(this.mUpdateObserver, NFC_FILTER);
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSliceUnpinned() {
            getContext().unregisterReceiver(this.mUpdateObserver);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.mUpdateObserver = null;
        }

        public void updateSlice() {
            notifySliceChange();
        }

        /* loaded from: classes.dex */
        public class NfcUpdateReceiver extends BroadcastReceiver {
            private final int NO_EXTRA = -1;
            private final NfcSliceWorker mSliceBackgroundWorker;

            public NfcUpdateReceiver(NfcSliceWorker nfcSliceWorker) {
                this.mSliceBackgroundWorker = nfcSliceWorker;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android.nfc.extra.ADAPTER_STATE", -1);
                if (intExtra == -1 || intExtra == 2 || intExtra == 4) {
                    Log.d("NfcSliceWorker", "Transitional update, dropping broadcast");
                    return;
                }
                Log.d("NfcSliceWorker", "Nfc broadcast received, updating Slice.");
                this.mSliceBackgroundWorker.updateSlice();
            }
        }
    }
}
