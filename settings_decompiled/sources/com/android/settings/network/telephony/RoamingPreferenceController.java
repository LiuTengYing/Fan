package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.GlobalSettingsChangeListener;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
/* loaded from: classes.dex */
public class RoamingPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String DIALOG_TAG = "MobileDataDialog";
    private static final String TAG = "RoamingController";
    private CarrierConfigManager mCarrierConfigManager;
    private RoamingDialogFragment mDialogFragment;
    FragmentManager mFragmentManager;
    private GlobalSettingsChangeListener mListener;
    private GlobalSettingsChangeListener mListenerForSubId;
    private RestrictedSwitchPreference mSwitchPreference;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RoamingPreferenceController(Context context, String str) {
        super(context, str);
        this.mDialogFragment = null;
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mListener == null) {
            this.mListener = new GlobalSettingsChangeListener(this.mContext, "data_roaming") { // from class: com.android.settings.network.telephony.RoamingPreferenceController.1
                @Override // com.android.settings.network.GlobalSettingsChangeListener
                public void onChanged(String str) {
                    RoamingPreferenceController roamingPreferenceController = RoamingPreferenceController.this;
                    roamingPreferenceController.updateState(roamingPreferenceController.mSwitchPreference);
                }
            };
        }
        stopMonitorSubIdSpecific();
        if (this.mSubId == -1) {
            return;
        }
        Context context = this.mContext;
        this.mListenerForSubId = new GlobalSettingsChangeListener(context, "data_roaming" + this.mSubId) { // from class: com.android.settings.network.telephony.RoamingPreferenceController.2
            @Override // com.android.settings.network.GlobalSettingsChangeListener
            public void onChanged(String str) {
                RoamingPreferenceController.this.stopMonitor();
                RoamingPreferenceController roamingPreferenceController = RoamingPreferenceController.this;
                roamingPreferenceController.updateState(roamingPreferenceController.mSwitchPreference);
            }
        };
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        stopMonitor();
        stopMonitorSubIdSpecific();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        if (this.mContext.getResources().getBoolean(134414378)) {
            return 2;
        }
        return i != -1 ? 0 : 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (isDialogNeeded()) {
            showDialog();
            return false;
        }
        this.mTelephonyManager.setDataRoamingEnabled(z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
        if (!restrictedSwitchPreference.isDisabledByAdmin()) {
            restrictedSwitchPreference.setEnabled(this.mSubId != -1);
            restrictedSwitchPreference.setChecked(isChecked());
        }
        if (UniMobileNetworkUtils.isPrefEnabledForSubsidy(this.mContext, this.mSubId)) {
            return;
        }
        restrictedSwitchPreference.setEnabled(false);
    }

    boolean isDialogNeeded() {
        boolean isDataRoamingEnabled = this.mTelephonyManager.isDataRoamingEnabled();
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (isDataRoamingEnabled) {
            return false;
        }
        return configForSubId == null || !configForSubId.getBoolean("disable_charge_indication_bool");
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.isDataRoamingEnabled();
    }

    public void init(FragmentManager fragmentManager, int i) {
        this.mFragmentManager = fragmentManager;
        this.mSubId = i;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        this.mTelephonyManager = telephonyManager;
        int i2 = this.mSubId;
        if (i2 == -1) {
            return;
        }
        TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(i2);
        if (createForSubscriptionId == null) {
            Log.w(TAG, "fail to init in sub" + this.mSubId);
            this.mSubId = -1;
            return;
        }
        this.mTelephonyManager = createForSubscriptionId;
    }

    private void showDialog() {
        RoamingDialogFragment roamingDialogFragment = this.mDialogFragment;
        if (roamingDialogFragment != null) {
            roamingDialogFragment.dismiss();
        }
        RoamingDialogFragment newInstance = RoamingDialogFragment.newInstance(this.mSubId);
        this.mDialogFragment = newInstance;
        newInstance.show(this.mFragmentManager, DIALOG_TAG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopMonitor() {
        GlobalSettingsChangeListener globalSettingsChangeListener = this.mListener;
        if (globalSettingsChangeListener != null) {
            globalSettingsChangeListener.close();
            this.mListener = null;
        }
    }

    private void stopMonitorSubIdSpecific() {
        GlobalSettingsChangeListener globalSettingsChangeListener = this.mListenerForSubId;
        if (globalSettingsChangeListener != null) {
            globalSettingsChangeListener.close();
            this.mListenerForSubId = null;
        }
    }
}
