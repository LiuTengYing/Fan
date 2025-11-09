package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.telephony.PsDataOffDialogFragment;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.unisoc.telephony.RadioInteractor;
/* loaded from: classes.dex */
public class PsDataOffPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop, PsDataOffDialogFragment.PsDataOffDialogListener {
    private static final String DEBUG_TEST = "persist.radio.psdataoff.debug";
    public static final String DIALOG_TAG = "PsDataOffDialog";
    public static final String LOG_TAG = "PsDataOffPreferenceController";
    public static final String PS_DATA_OFF_ENABLED = "persist.radio.ps.data.off";
    FragmentManager mFragmentManager;
    boolean mNeedDialog;
    private SwitchPreference mSwitchPreference;

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

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PsDataOffPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return SystemProperties.getBoolean(DEBUG_TEST, false) || this.mContext.getResources().getBoolean(R$bool.enable_ps_data_off) ? 0 : 4;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            if (this.mNeedDialog) {
                showDialog();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mNeedDialog = z;
        int phoneId = SubscriptionManager.getPhoneId(this.mSubId);
        if (this.mNeedDialog) {
            return false;
        }
        setPsDataOff(phoneId, false, -1);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isPsDataOff();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setSummary(R$string.ps_data_off_summary);
    }

    public void init(FragmentManager fragmentManager, int i) {
        this.mFragmentManager = fragmentManager;
        this.mSubId = i;
    }

    private void showDialog() {
        this.mSwitchPreference.setEnabled(false);
        PsDataOffDialogFragment newInstance = PsDataOffDialogFragment.newInstance(this.mSubId);
        newInstance.setController(this);
        newInstance.show(this.mFragmentManager, DIALOG_TAG);
    }

    @Override // com.android.settings.network.telephony.PsDataOffDialogFragment.PsDataOffDialogListener
    public void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment) {
        Log.d(LOG_TAG, "onDialogDismiss");
        this.mSwitchPreference.setEnabled(true);
        updateState(this.mSwitchPreference);
    }

    private boolean isPsDataOff() {
        return !TelephonyManager.getTelephonyProperty(SubscriptionManager.getPhoneId(this.mSubId), PS_DATA_OFF_ENABLED, "-1").equals("-1");
    }

    private void setPsDataOff(int i, boolean z, int i2) {
        new RadioInteractor(this.mContext).setPsDataOff(i, z, i2);
        if (z) {
            PsDataOffDialogFragment.setTelephonyProperty(i, PS_DATA_OFF_ENABLED, Integer.toString(i2));
        } else {
            PsDataOffDialogFragment.setTelephonyProperty(i, PS_DATA_OFF_ENABLED, "-1");
        }
    }
}
