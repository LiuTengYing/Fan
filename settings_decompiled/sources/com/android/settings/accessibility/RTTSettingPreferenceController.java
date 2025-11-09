package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.accessibility.rtt.TelecomUtil;
import com.android.settings.core.BasePreferenceController;
import java.util.List;
/* loaded from: classes.dex */
public class RTTSettingPreferenceController extends BasePreferenceController {
    private static final String DIALER_RTT_CONFIGURATION = "dialer_rtt_configuration";
    private static final String TAG = "RTTSettingsCtr";
    private final CarrierConfigManager mCarrierConfigManager;
    private final Context mContext;
    private final String mDialerPackage;
    private final CharSequence[] mModes;
    private final PackageManager mPackageManager;
    Intent mRTTIntent;

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

    public RTTSettingPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mModes = context.getResources().getTextArray(R$array.rtt_setting_mode);
        this.mDialerPackage = context.getString(R$string.config_rtt_setting_package_name);
        this.mPackageManager = context.getPackageManager();
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mRTTIntent = new Intent(context.getString(R$string.config_rtt_setting_intent_action));
        Log.d(TAG, "init controller");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(this.mRTTIntent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty() || !isRttSettingSupported()) ? 3 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.findPreference(getPreferenceKey()).setIntent(this.mRTTIntent);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), DIALER_RTT_CONFIGURATION, 0);
        Log.d(TAG, "DIALER_RTT_CONFIGURATION value =  " + i);
        return this.mModes[i];
    }

    boolean isRttSettingSupported() {
        Log.d(TAG, "isRttSettingSupported [start]");
        if (!isDefaultDialerSupportedRTT(this.mContext)) {
            Log.d(TAG, "Dialer doesn't support RTT.");
            return false;
        }
        for (PhoneAccountHandle phoneAccountHandle : TelecomUtil.getCallCapablePhoneAccounts(this.mContext)) {
            int subIdForPhoneAccountHandle = TelecomUtil.getSubIdForPhoneAccountHandle(this.mContext, phoneAccountHandle);
            Log.d(TAG, "subscription id for the device: " + subIdForPhoneAccountHandle);
            boolean isRttSupportedByTelecom = isRttSupportedByTelecom(phoneAccountHandle);
            Log.d(TAG, "rtt calling supported by telecom:: " + isRttSupportedByTelecom);
            if (isRttSupportedByTelecom) {
                if (this.mCarrierConfigManager.getConfigForSubId(subIdForPhoneAccountHandle) != null && getBooleanCarrierConfig("ignore_rtt_mode_setting_bool")) {
                    Log.d(TAG, "RTT visibility setting is supported.");
                    return true;
                }
                Log.d(TAG, "IGNORE_RTT_MODE_SETTING_BOOL is false.");
            }
        }
        Log.d(TAG, "isRttSettingSupported [Not support]");
        return false;
    }

    private boolean isRttSupportedByTelecom(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = TelecomUtil.getTelecomManager(this.mContext).getPhoneAccount(phoneAccountHandle);
        if (phoneAccount == null || !phoneAccount.hasCapabilities(4096)) {
            return false;
        }
        Log.d(TAG, "Phone account has RTT capability.");
        return true;
    }

    private boolean getBooleanCarrierConfig(String str) {
        if (this.mCarrierConfigManager == null) {
            return CarrierConfigManager.getDefaultConfig().getBoolean(str);
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(SubscriptionManager.getDefaultVoiceSubscriptionId());
        if (configForSubId != null) {
            return configForSubId.getBoolean(str);
        }
        return CarrierConfigManager.getDefaultConfig().getBoolean(str);
    }

    private static boolean isDefaultDialerSupportedRTT(Context context) {
        return TextUtils.equals(context.getString(R$string.config_rtt_setting_package_name), TelecomUtil.getTelecomManager(context).getDefaultDialerPackage());
    }
}
