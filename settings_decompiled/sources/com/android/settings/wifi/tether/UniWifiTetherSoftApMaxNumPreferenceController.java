package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApCapability;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.development.BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda2;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class UniWifiTetherSoftApMaxNumPreferenceController extends WifiTetherBasePreferenceController implements WifiManager.SoftApCallback {
    private final int DEFAULT_MAX_NUM;
    private boolean entryValuesChanged;
    private Map<Integer, String> mClientNumberMap;
    private int mClientNumberValue;
    private boolean mIsSupported;
    private int mMaximumSupportedClientNumber;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether_support_max_clients";
    }

    public UniWifiTetherSoftApMaxNumPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.DEFAULT_MAX_NUM = 10;
        this.mIsSupported = false;
        this.mMaximumSupportedClientNumber = 10;
        this.mClientNumberMap = new LinkedHashMap();
        this.entryValuesChanged = false;
        this.mIsSupported = true;
        for (int i = 1; i <= 10; i++) {
            this.mClientNumberMap.put(Integer.valueOf(i), String.valueOf(i));
        }
        this.mWifiManager.registerSoftApCallback(context.getMainExecutor(), this);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        Preference preference = this.mPreference;
        if (preference == null) {
            return;
        }
        ListPreference listPreference = (ListPreference) preference;
        if (this.mClientNumberMap.keySet().removeIf(new Predicate() { // from class: com.android.settings.wifi.tether.UniWifiTetherSoftApMaxNumPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateDisplay$0;
                lambda$updateDisplay$0 = UniWifiTetherSoftApMaxNumPreferenceController.this.lambda$updateDisplay$0((Integer) obj);
                return lambda$updateDisplay$0;
            }
        })) {
            listPreference.setEntries((CharSequence[]) this.mClientNumberMap.values().stream().toArray(new IntFunction() { // from class: com.android.settings.wifi.tether.UniWifiTetherSoftApMaxNumPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$updateDisplay$1;
                    lambda$updateDisplay$1 = UniWifiTetherSoftApMaxNumPreferenceController.lambda$updateDisplay$1(i);
                    return lambda$updateDisplay$1;
                }
            }));
            listPreference.setEntryValues((CharSequence[]) this.mClientNumberMap.keySet().stream().map(new BluetoothMaxConnectedAudioDevicesPreferenceController$$ExternalSyntheticLambda2()).toArray(new IntFunction() { // from class: com.android.settings.wifi.tether.UniWifiTetherSoftApMaxNumPreferenceController$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    CharSequence[] lambda$updateDisplay$2;
                    lambda$updateDisplay$2 = UniWifiTetherSoftApMaxNumPreferenceController.lambda$updateDisplay$2(i);
                    return lambda$updateDisplay$2;
                }
            }));
            this.entryValuesChanged = true;
        }
        int maxNumberOfClients = this.mSoftApConfig.getMaxNumberOfClients();
        if (this.mClientNumberMap.get(Integer.valueOf(maxNumberOfClients)) == null) {
            maxNumberOfClients = this.mMaximumSupportedClientNumber;
        }
        this.mClientNumberValue = maxNumberOfClients;
        listPreference.setSummary(this.mClientNumberMap.get(Integer.valueOf(maxNumberOfClients)));
        listPreference.setValue(this.entryValuesChanged ? Integer.toBinaryString(this.mClientNumberValue) : String.valueOf(this.mClientNumberValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateDisplay$0(Integer num) {
        return num.intValue() > this.mMaximumSupportedClientNumber;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$1(int i) {
        return new CharSequence[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$2(int i) {
        return new CharSequence[i];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = this.entryValuesChanged ? Integer.parseInt((String) obj, 2) : Integer.parseInt((String) obj);
        this.mClientNumberValue = parseInt;
        preference.setSummary(this.mClientNumberMap.get(Integer.valueOf(parseInt)));
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener = this.mListener;
        if (onTetherConfigUpdateListener != null) {
            onTetherConfigUpdateListener.onTetherConfigUpdated(this);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mIsSupported;
    }

    public void onCapabilityChanged(SoftApCapability softApCapability) {
        int maxSupportedClients = softApCapability.getMaxSupportedClients();
        Log.i("UniSoftApMaxNumPref", "SoftAp max supported clients number : " + maxSupportedClients);
        if (this.mMaximumSupportedClientNumber != maxSupportedClients) {
            this.mMaximumSupportedClientNumber = maxSupportedClients;
            updateDisplay();
        }
        this.mWifiManager.unregisterSoftApCallback(this);
    }

    public int getMaximumClientNumber() {
        return this.mClientNumberValue;
    }
}
