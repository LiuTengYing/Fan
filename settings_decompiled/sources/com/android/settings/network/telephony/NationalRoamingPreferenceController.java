package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.internal.telephony.GlobalSettingsHelper;
import com.android.settings.R$string;
import com.android.telephony.Rlog;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
/* loaded from: classes.dex */
public class NationalRoamingPreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String LOG_TAG = "NationalRoamingPreferenceController";
    private static final int NATIONAL_ROAMING_TYPE_ALL_NETWORKS = 1;
    private static final int NATIONAL_ROAMING_TYPE_DISABLED = 0;
    private static final int NATIONAL_ROAMING_TYPE_NATIONAL_ROAMING_ONLY = 2;
    private PersistableBundle mPersistableBundle;
    private UniTelephonyManager mUniTelephonyManager;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NationalRoamingPreferenceController(Context context, String str) {
        super(context, str);
        this.mUniTelephonyManager = UniTelephonyManager.from(context);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        if (this.mContext.getResources().getBoolean(134414378)) {
            return i != -1 ? 0 : 1;
        }
        return 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        int nationalRoamingType = getNationalRoamingType();
        log("updateState: roamingType = " + nationalRoamingType + " ,mSubId = " + this.mSubId);
        listPreference.setValue(Integer.toString(nationalRoamingType));
        listPreference.setSummary(getPreferredNationalRoamingTypeSummaryResId(nationalRoamingType));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        ListPreference listPreference = (ListPreference) preference;
        log("onPreferenceChange: roamingType = " + parseInt);
        this.mUniTelephonyManager.setDataRoamingEnabled(this.mSubId, parseInt != 0);
        GlobalSettingsHelper.setInt(this.mContext, "data_roaming", this.mSubId, parseInt);
        listPreference.setValue(Integer.toString(parseInt));
        listPreference.setSummary(getPreferredNationalRoamingTypeSummaryResId(parseInt));
        return true;
    }

    public void init(int i) {
        this.mSubId = i;
    }

    private int getNationalRoamingType() {
        boolean isDefaultDataRoamingEnabled = this.mUniTelephonyManager.isDefaultDataRoamingEnabled(this.mSubId);
        log("defaultValue = " + isDefaultDataRoamingEnabled);
        return GlobalSettingsHelper.getInt(this.mContext, "data_roaming", this.mSubId, isDefaultDataRoamingEnabled ? 1 : 0);
    }

    private int getPreferredNationalRoamingTypeSummaryResId(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return R$string.preferred_data_roaming_national;
                }
                return R$string.preferred_data_roaming_disable;
            }
            return R$string.preferred_data_roaming_all_networks;
        }
        return R$string.preferred_data_roaming_disable;
    }

    private void log(String str) {
        Rlog.d(LOG_TAG, str);
    }
}
