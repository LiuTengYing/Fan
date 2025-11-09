package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class BackupCallingPreferenceController extends TelephonyTogglePreferenceController {
    private static final String LOG_TAG = "BackupCallingPrefCtrl";
    private Preference mPreference;

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

    public BackupCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public BackupCallingPreferenceController init(int i) {
        this.mSubId = i;
        return this;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        if (hasBackupCallingFeature(i)) {
            List<SubscriptionInfo> activeSubscriptionList = getActiveSubscriptionList();
            return (getSubscriptionInfoFromList(activeSubscriptionList, i) != null && activeSubscriptionList.size() > 1) ? 0 : 2;
        }
        return 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(this.mSubId);
        if (imsMmTelManager == null) {
            return false;
        }
        try {
            imsMmTelManager.setCrossSimCallingEnabled(z);
            return true;
        } catch (ImsException e) {
            Log.w(LOG_TAG, "fail to change cross SIM calling configuration: " + z, e);
            return false;
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(this.mSubId);
        if (imsMmTelManager == null) {
            return false;
        }
        try {
            return imsMmTelManager.isCrossSimCallingEnabled();
        } catch (ImsException e) {
            Log.w(LOG_TAG, "fail to get cross SIM calling configuration", e);
            return false;
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        SubscriptionInfo subscriptionInfoFromActiveList = getSubscriptionInfoFromActiveList(this.mSubId);
        this.mPreference = preference;
        ((SwitchPreference) preference).setChecked(subscriptionInfoFromActiveList != null ? isChecked() : false);
        updateSummary(getLatestSummary(subscriptionInfoFromActiveList));
    }

    private String getLatestSummary(SubscriptionInfo subscriptionInfo) {
        return Objects.toString(subscriptionInfo == null ? null : SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext), "");
    }

    private void updateSummary(String str) {
        Preference preference = this.mPreference;
        if (preference == null) {
            return;
        }
        preference.setSummary(String.format(getResourcesForSubId().getString(R$string.backup_calling_setting_summary), str).toString());
    }

    private boolean hasBackupCallingFeature(int i) {
        return isCrossSimEnabledByPlatform(this.mContext, i);
    }

    protected boolean isCrossSimEnabledByPlatform(Context context, int i) {
        if (new WifiCallingQueryImsState(context, i).isWifiCallingSupported()) {
            PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
            return carrierConfigForSubId != null && carrierConfigForSubId.getBoolean("carrier_cross_sim_ims_available_bool", false);
        }
        Log.d(LOG_TAG, "Not supported by framework. subId = " + i);
        return false;
    }

    private ImsMmTelManager getImsMmTelManager(int i) {
        ImsManager imsManager;
        if (SubscriptionManager.isUsableSubscriptionId(i) && (imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class)) != null) {
            return imsManager.getImsMmTelManager(i);
        }
        return null;
    }

    private List<SubscriptionInfo> getActiveSubscriptionList() {
        return SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
    }

    private SubscriptionInfo getSubscriptionInfoFromList(List<SubscriptionInfo> list, int i) {
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo != null && subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private SubscriptionInfo getSubscriptionInfoFromActiveList(int i) {
        if (SubscriptionManager.isUsableSubscriptionId(i)) {
            return getSubscriptionInfoFromList(getActiveSubscriptionList(), i);
        }
        return null;
    }
}
