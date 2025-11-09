package com.android.settings.network.telephony.cdma;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class CdmaSystemSelectPreferenceController extends CdmaBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String LOG_TAG = "CdmaSystemSelectPreferenceController";
    private Handler mUiHandler;

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CdmaSystemSelectPreferenceController(Context context, String str) {
        super(context, str);
        this.mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        final ListPreference listPreference = (ListPreference) preference;
        boolean z = true;
        listPreference.setVisible(getAvailabilityStatus() == 0);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CdmaSystemSelectPreferenceController.this.lambda$updateState$1(listPreference);
            }
        });
        int networkTypeFromRaf = MobileNetworkUtils.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesForReason(0));
        if (networkTypeFromRaf == 9 || networkTypeFromRaf == 26) {
            z = false;
        }
        listPreference.setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(final ListPreference listPreference) {
        Log.d(LOG_TAG, "getCdmaRoamingMode in backgroud.");
        final int cdmaRoamingMode = this.mTelephonyManager.getCdmaRoamingMode();
        this.mUiHandler.post(new Runnable() { // from class: com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CdmaSystemSelectPreferenceController.this.lambda$updateState$0(cdmaRoamingMode, listPreference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(int i, ListPreference listPreference) {
        if (i != -1) {
            if (i == 0 || i == 2) {
                listPreference.setValue(Integer.toString(i));
            } else {
                resetCdmaRoamingModeToDefault();
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        try {
            this.mTelephonyManager.setCdmaRoamingMode(parseInt);
            Settings.Global.putInt(this.mContext.getContentResolver(), "roaming_settings", parseInt);
            return true;
        } catch (IllegalStateException unused) {
            return false;
        }
    }

    private void resetCdmaRoamingModeToDefault() {
        ((ListPreference) this.mPreference).setValue(Integer.toString(2));
        Settings.Global.putInt(this.mContext.getContentResolver(), "roaming_settings", 2);
        this.mTelephonyManager.setCdmaRoamingMode(2);
    }
}
