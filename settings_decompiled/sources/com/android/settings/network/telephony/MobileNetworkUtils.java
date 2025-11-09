package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$dimen;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settingslib.Utils;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public class MobileNetworkUtils {
    public static final Drawable EMPTY_DRAWABLE = new ColorDrawable(0);

    private static int getAdjustedRaf(int i) {
        if ((i & 32771) > 0) {
            i |= 32771;
        }
        if ((i & 17284) > 0) {
            i |= 17284;
        }
        if ((i & 72) > 0) {
            i |= 72;
        }
        if ((i & 10288) > 0) {
            i |= 10288;
        }
        if ((i & 266240) > 0) {
            i |= 266240;
        }
        return (i & 524288) > 0 ? i | 524288 : i;
    }

    public static long getRafFromNetworkType(int i) {
        switch (i) {
            case 0:
                return 50055L;
            case 1:
                return 32771L;
            case 2:
                return 17284L;
            case 3:
                return 50055L;
            case 4:
                return 10360L;
            case 5:
                return 72L;
            case 6:
                return 10288L;
            case 7:
                return 60415L;
            case 8:
                return 276600L;
            case 9:
                return 316295L;
            case 10:
                return 326655L;
            case 11:
                return 266240L;
            case 12:
                return 283524L;
            case 13:
                return 65536L;
            case 14:
                return 82820L;
            case 15:
                return 331776L;
            case 16:
                return 98307L;
            case 17:
                return 364547L;
            case 18:
                return 115591L;
            case 19:
                return 349060L;
            case 20:
                return 381831L;
            case 21:
                return 125951L;
            case 22:
                return 392191L;
            case 23:
                return 524288L;
            case 24:
                return 790528L;
            case 25:
                return 800888L;
            case 26:
                return 840583L;
            case 27:
                return 850943L;
            case 28:
                return 807812L;
            case 29:
                return 856064L;
            case 30:
                return 888835L;
            case 31:
                return 873348L;
            case 32:
                return 906119L;
            case 33:
                return 916479L;
            default:
                return 0L;
        }
    }

    public static boolean isDpcApnEnforced(Context context) {
        Cursor query = context.getContentResolver().query(Telephony.Carriers.ENFORCE_MANAGED_URI, null, null, null, null);
        if (query != null) {
            try {
                if (query.getCount() == 1) {
                    query.moveToFirst();
                    boolean z = query.getInt(0) > 0;
                    query.close();
                    return z;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query != null) {
            query.close();
        }
        return false;
    }

    public static boolean isWfcProvisionedOnDevice(int i) {
        ProvisioningManager createForSubscriptionId = ProvisioningManager.createForSubscriptionId(i);
        if (createForSubscriptionId == null) {
            return true;
        }
        return createForSubscriptionId.getProvisioningStatusForCapability(1, 1);
    }

    public static boolean isContactDiscoveryEnabled(Context context, int i) {
        return isContactDiscoveryEnabled((ImsManager) context.getSystemService(ImsManager.class), i);
    }

    public static boolean isContactDiscoveryEnabled(ImsManager imsManager, int i) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager == null) {
            return false;
        }
        try {
            return imsRcsManager.getUceAdapter().isUceSettingEnabled();
        } catch (ImsException e) {
            Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
            return false;
        }
    }

    public static void setContactDiscoveryEnabled(ImsManager imsManager, int i, boolean z) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager == null) {
            return;
        }
        try {
            imsRcsManager.getUceAdapter().setUceSettingEnabled(z);
        } catch (ImsException e) {
            Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
        }
    }

    private static ImsRcsManager getImsRcsManager(ImsManager imsManager, int i) {
        if (imsManager == null) {
            return null;
        }
        try {
            return imsManager.getImsRcsManager(i);
        } catch (Exception e) {
            Log.w("MobileNetworkUtils", "Could not resolve ImsRcsManager: " + e.getMessage());
            return null;
        }
    }

    public static boolean isContactDiscoveryVisible(Context context, int i) {
        CarrierConfigCache carrierConfigCache = CarrierConfigCache.getInstance(context);
        if (!carrierConfigCache.hasCarrierConfigManager()) {
            Log.w("MobileNetworkUtils", "isContactDiscoveryVisible: Could not resolve carrier config");
            return false;
        }
        PersistableBundle configForSubId = carrierConfigCache.getConfigForSubId(i);
        return configForSubId.getBoolean("use_rcs_presence_bool", false) || configForSubId.getBoolean("ims.rcs_bulk_capability_exchange_bool", false);
    }

    public static Intent buildPhoneAccountConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle) {
        Intent buildConfigureIntent = buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONFIGURE_PHONE_ACCOUNT");
        return buildConfigureIntent == null ? buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONNECTION_SERVICE_CONFIGURE") : buildConfigureIntent;
    }

    private static Intent buildConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle, String str) {
        if (phoneAccountHandle == null || phoneAccountHandle.getComponentName() == null || TextUtils.isEmpty(phoneAccountHandle.getComponentName().getPackageName())) {
            return null;
        }
        Intent intent = new Intent(str);
        intent.setPackage(phoneAccountHandle.getComponentName().getPackageName());
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() == 0) {
            return null;
        }
        return intent;
    }

    public static boolean showEuiccSettings(final Context context) {
        if (!((EuiccManager) context.getSystemService(EuiccManager.class)).isEnabled()) {
            Log.w("MobileNetworkUtils", "EuiccManager is not enabled.");
            return false;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            Boolean bool = (Boolean) ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.telephony.MobileNetworkUtils$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    Object lambda$showEuiccSettings$0;
                    lambda$showEuiccSettings$0 = MobileNetworkUtils.lambda$showEuiccSettings$0(context);
                    return lambda$showEuiccSettings$0;
                }
            }).get(3L, TimeUnit.SECONDS);
            if (bool != null) {
                return bool.booleanValue();
            }
            return false;
        } catch (InterruptedException | ExecutionException | TimeoutException unused) {
            Log.w("MobileNetworkUtils", "Accessing Euicc takes too long: +" + (SystemClock.elapsedRealtime() - elapsedRealtime) + "ms");
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Object lambda$showEuiccSettings$0(Context context) throws Exception {
        try {
            return showEuiccSettingsDetecting(context);
        } catch (Exception e) {
            Log.w("MobileNetworkUtils", "Accessing Euicc failure", e);
            return Boolean.FALSE;
        }
    }

    public static Boolean showEuiccSettingsDetecting(Context context) {
        if (!((EuiccManager) context.getSystemService(EuiccManager.class)).isEnabled()) {
            Log.w("MobileNetworkUtils", "EuiccManager is not enabled.");
            return Boolean.FALSE;
        }
        ContentResolver contentResolver = context.getContentResolver();
        boolean contains = Arrays.asList(TextUtils.split(SystemProperties.get("ro.setupwizard.esim_cid_ignore", ""), ",")).contains(SystemProperties.get("ro.boot.cid"));
        boolean z = true;
        boolean z2 = SystemProperties.getBoolean("esim.enable_esim_system_ui_by_default", true);
        boolean z3 = Settings.Global.getInt(contentResolver, "euicc_provisioned", 0) != 0;
        boolean isDevelopmentSettingsEnabled = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        Log.i("MobileNetworkUtils", String.format("showEuiccSettings: esimIgnoredDevice: %b, enabledEsimUiByDefault: %b, euiccProvisioned: %b, inDeveloperMode: %b.", Boolean.valueOf(contains), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(isDevelopmentSettingsEnabled)));
        if (!z3 && ((contains || !isDevelopmentSettingsEnabled) && (contains || !z2 || !isCurrentCountrySupported(context)))) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    public static boolean isMobileDataEnabled(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager.isDataEnabled()) {
            return true;
        }
        TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        return createForSubscriptionId != null && createForSubscriptionId.isDataEnabled();
    }

    public static void setMobileDataEnabled(Context context, int i, boolean z, boolean z2) {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        createForSubscriptionId.setDataEnabled(z);
        if (!z2 || (activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList()) == null) {
            return;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSubscriptionId() != i && !subscriptionInfo.isOpportunistic()) {
                ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).setDataEnabled(false);
            }
        }
    }

    public static boolean isCdmaOptions(Context context, int i) {
        int networkTypeFromRaf;
        if (i == -1) {
            return false;
        }
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(context).getConfigForSubId(i);
        if (configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !configForSubId.getBoolean("world_phone_bool")) {
            TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
            if (createForSubscriptionId.getPhoneType() == 2) {
                return true;
            }
            return isWorldMode(context, i) && ((networkTypeFromRaf = getNetworkTypeFromRaf((int) createForSubscriptionId.getAllowedNetworkTypesForReason(0))) == 9 || networkTypeFromRaf == 8 || networkTypeFromRaf == 26 || networkTypeFromRaf == 25 || shouldSpeciallyUpdateGsmCdma(context, i));
        }
        return true;
    }

    public static boolean isGsmOptions(Context context, int i) {
        if (i == -1) {
            return false;
        }
        if (isGsmBasicOptions(context, i)) {
            return true;
        }
        int networkTypeFromRaf = getNetworkTypeFromRaf((int) ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
        return isWorldMode(context, i) && (networkTypeFromRaf == 8 || networkTypeFromRaf == 9 || networkTypeFromRaf == 25 || networkTypeFromRaf == 26 || shouldSpeciallyUpdateGsmCdma(context, i));
    }

    private static boolean isGsmBasicOptions(Context context, int i) {
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(context).getConfigForSubId(i);
        return !(configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !configForSubId.getBoolean("world_phone_bool")) || ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getPhoneType() == 1;
    }

    public static boolean isWorldMode(Context context, int i) {
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(context).getConfigForSubId(i);
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("world_mode_enabled_bool");
    }

    public static boolean shouldDisplayNetworkSelectOptions(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(context).getConfigForSubId(i);
        if (i == -1 || configForSubId == null || !configForSubId.getBoolean("operator_selection_expand_bool") || configForSubId.getBoolean("hide_carrier_network_settings_bool") || (configForSubId.getBoolean("csp_enabled_bool") && !createForSubscriptionId.isManualNetworkSelectionAllowed())) {
            return false;
        }
        if (isWorldMode(context, i)) {
            int networkTypeFromRaf = getNetworkTypeFromRaf((int) createForSubscriptionId.getAllowedNetworkTypesForReason(0));
            if (networkTypeFromRaf == 8 || shouldSpeciallyUpdateGsmCdma(context, i)) {
                return false;
            }
            if (networkTypeFromRaf == 9) {
                return true;
            }
        }
        return isGsmBasicOptions(context, i);
    }

    public static boolean isTdscdmaSupported(Context context, int i) {
        return isTdscdmaSupported(context, ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    private static boolean isTdscdmaSupported(Context context, TelephonyManager telephonyManager) {
        PersistableBundle config = CarrierConfigCache.getInstance(context).getConfig();
        if (config == null) {
            return false;
        }
        if (config.getBoolean("support_tdscdma_bool")) {
            return true;
        }
        String[] stringArray = config.getStringArray("support_tdscdma_roaming_networks_string_array");
        if (stringArray == null) {
            return false;
        }
        ServiceState serviceState = telephonyManager.getServiceState();
        String operatorNumeric = serviceState != null ? serviceState.getOperatorNumeric() : null;
        if (operatorNumeric == null) {
            return false;
        }
        for (String str : stringArray) {
            if (operatorNumeric.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static int getSearchableSubscriptionId(Context context) {
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (activeSubscriptionIdList.length >= 1) {
            return activeSubscriptionIdList[0];
        }
        return -1;
    }

    public static int getAvailability(Context context, int i, TelephonyAvailabilityCallback telephonyAvailabilityCallback) {
        if (i != -1) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(i);
        }
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (ArrayUtils.isEmpty(activeSubscriptionIdList)) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(-1);
        }
        for (int i2 : activeSubscriptionIdList) {
            int availabilityStatus = telephonyAvailabilityCallback.getAvailabilityStatus(i2);
            if (availabilityStatus == 0) {
                return availabilityStatus;
            }
        }
        return telephonyAvailabilityCallback.getAvailabilityStatus(activeSubscriptionIdList[0]);
    }

    static boolean shouldSpeciallyUpdateGsmCdma(Context context, int i) {
        if (isWorldMode(context, i)) {
            int networkTypeFromRaf = getNetworkTypeFromRaf((int) ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
            return (networkTypeFromRaf == 17 || networkTypeFromRaf == 20 || networkTypeFromRaf == 15 || networkTypeFromRaf == 19 || networkTypeFromRaf == 22 || networkTypeFromRaf == 10) && !isTdscdmaSupported(context, i);
        }
        return false;
    }

    public static Drawable getSignalStrengthIcon(Context context, int i, int i2, int i3, boolean z) {
        Drawable drawable;
        SignalDrawable signalDrawable = new SignalDrawable(context);
        signalDrawable.setLevel(SignalDrawable.getState(i, i2, z));
        if (i3 == 0) {
            drawable = EMPTY_DRAWABLE;
        } else {
            drawable = context.getResources().getDrawable(i3, context.getTheme());
        }
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.signal_strength_icon_size);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable, signalDrawable});
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
            layerDrawable.setLayerGravity(0, 53);
        } else {
            layerDrawable.setLayerGravity(0, 51);
        }
        layerDrawable.setLayerGravity(1, 85);
        layerDrawable.setLayerSize(1, dimensionPixelSize, dimensionPixelSize);
        layerDrawable.setTintList(Utils.getColorAttr(context, 16843817));
        return layerDrawable;
    }

    public static CharSequence getCurrentCarrierNameForDisplay(Context context, int i) {
        SubscriptionInfo subscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager != null && (subscriptionInfo = getSubscriptionInfo(subscriptionManager, i)) != null) {
            return subscriptionInfo.getCarrierName();
        }
        return getOperatorNameFromTelephonyManager(context);
    }

    public static CharSequence getCurrentCarrierNameForDisplay(Context context) {
        SubscriptionInfo subscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager != null && (subscriptionInfo = getSubscriptionInfo(subscriptionManager, SubscriptionManager.getDefaultSubscriptionId())) != null) {
            return subscriptionInfo.getCarrierName();
        }
        return getOperatorNameFromTelephonyManager(context);
    }

    private static SubscriptionInfo getSubscriptionInfo(SubscriptionManager subscriptionManager, int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private static String getOperatorNameFromTelephonyManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getNetworkOperatorName();
    }

    private static int[] getActiveSubscriptionIdList(Context context) {
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        int i = 0;
        if (activeSubscriptionInfoList == null) {
            return new int[0];
        }
        int[] iArr = new int[activeSubscriptionInfoList.size()];
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            iArr[i] = subscriptionInfo.getSubscriptionId();
            i++;
        }
        return iArr;
    }

    private static boolean isCurrentCountrySupported(Context context) {
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        for (int i = 0; i < telephonyManager.getPhoneCount(); i++) {
            String networkCountryIso = telephonyManager.getNetworkCountryIso(i);
            if (euiccManager.isSupportedCountry(networkCountryIso)) {
                Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is supported in " + networkCountryIso);
                return true;
            }
        }
        Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is not supported in the current country.");
        return false;
    }

    public static int getNetworkTypeFromRaf(int i) {
        switch (getAdjustedRaf(i)) {
            case 72:
                return 5;
            case 10288:
                return 6;
            case 10360:
                return 4;
            case 17284:
                return 2;
            case 32771:
                return 1;
            case 50055:
                return 0;
            case 60415:
                return 7;
            case 65536:
                return 13;
            case 82820:
                return 14;
            case 98307:
                return 16;
            case 115591:
                return 18;
            case 125951:
                return 21;
            case 266240:
                return 11;
            case 276600:
                return 8;
            case 283524:
                return 12;
            case 316295:
                return 9;
            case 326655:
                return 10;
            case 331776:
                return 15;
            case 349060:
                return 19;
            case 364547:
                return 17;
            case 381831:
                return 20;
            case 392191:
                return 22;
            case 524288:
                return 23;
            case 790528:
                return 24;
            case 800888:
                return 25;
            case 807812:
                return 28;
            case 840583:
                return 26;
            case 850943:
                return 27;
            case 856064:
                return 29;
            case 873348:
                return 31;
            case 888835:
                return 30;
            case 906119:
                return 32;
            case 916479:
                return 33;
            default:
                return -1;
        }
    }

    public static boolean activeNetworkIsCellular(Context context) {
        NetworkCapabilities networkCapabilities;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null || (networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)) == null) {
            return false;
        }
        return networkCapabilities.hasTransport(0);
    }

    public static boolean isWifiCallingEnabled(Context context, int i, WifiCallingQueryImsState wifiCallingQueryImsState, PhoneAccountHandle phoneAccountHandle) {
        if (phoneAccountHandle == null) {
            phoneAccountHandle = ((TelecomManager) context.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(i);
        }
        if (phoneAccountHandle != null) {
            return buildPhoneAccountConfigureIntent(context, phoneAccountHandle) != null;
        }
        if (wifiCallingQueryImsState == null) {
            wifiCallingQueryImsState = new WifiCallingQueryImsState(context, i);
        }
        return wifiCallingQueryImsState.isReadyToWifiCalling();
    }

    public static CharSequence getPreferredStatus(boolean z, Context context, SubscriptionManager subscriptionManager, boolean z2) {
        CharSequence preferredSmsStatus;
        List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(subscriptionManager);
        if (activeSubscriptions.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (SubscriptionInfo subscriptionInfo : activeSubscriptions) {
            int size = activeSubscriptions.size();
            CharSequence displayName = subscriptionInfo.getDisplayName();
            if (size == 1 && SubscriptionManager.isValidSubscriptionId(subscriptionInfo.getSubscriptionId())) {
                return displayName;
            }
            if (z2) {
                preferredSmsStatus = getPreferredCallStatus(context, subscriptionInfo);
            } else {
                preferredSmsStatus = getPreferredSmsStatus(context, subscriptionInfo);
            }
            if (preferredSmsStatus.toString().isEmpty()) {
                sb.append(displayName);
            } else {
                sb.append(displayName);
                sb.append(" (");
                sb.append(preferredSmsStatus);
                sb.append(")");
            }
            if (subscriptionInfo != activeSubscriptions.get(activeSubscriptions.size() - 1)) {
                sb.append(", ");
            }
            if (z) {
                sb.insert(0, "\u200f").insert(sb.length(), "\u200f");
            }
        }
        return sb;
    }

    private static CharSequence getPreferredCallStatus(Context context, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == SubscriptionManager.getDefaultVoiceSubscriptionId() ? setSummaryResId(context, R$string.calls_sms_preferred) : "";
    }

    private static CharSequence getPreferredSmsStatus(Context context, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == SubscriptionManager.getDefaultSmsSubscriptionId() ? setSummaryResId(context, R$string.calls_sms_preferred) : "";
    }

    private static String setSummaryResId(Context context, int i) {
        return context.getResources().getString(i);
    }

    public static void launchMobileNetworkSettings(Context context, SubscriptionInfo subscriptionInfo) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        if (subscriptionId == -1) {
            Log.d("MobileNetworkUtils", "launchMobileNetworkSettings fail, subId is invalid.");
            return;
        }
        Log.d("MobileNetworkUtils", "launchMobileNetworkSettings for subId: " + subscriptionId);
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", subscriptionId);
        new SubSettingLauncher(context).setTitleText(subscriptionInfo.getDisplayName()).setDestination(MobileNetworkSettings.class.getCanonicalName()).setSourceMetricsCategory(0).setArguments(bundle).launch();
    }

    public static boolean isSupportUplmn(int i) {
        return SystemProperties.getBoolean("persist.sys.uplmn", false);
    }

    public static boolean isNetworkSelectEnabled(Context context) {
        if (((TelephonyManager) context.getSystemService("phone")).getCallState() != 0) {
            Toast.makeText(context, R$string.select_during_call_prohibited, 0).show();
            return false;
        } else if (WirelessUtils.isAirplaneModeOn(context)) {
            Toast.makeText(context, R$string.select_under_airplane_prohibited, 0).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean getBooleanCarrierConfig(Context context, String str, int i) {
        return getBooleanCarrierConfig(context, str, true, i);
    }

    public static boolean getBooleanCarrierConfig(Context context, String str, boolean z, int i) {
        boolean z2;
        CarrierConfigCache carrierConfigCache = CarrierConfigCache.getInstance(context);
        if (!carrierConfigCache.hasCarrierConfigManager()) {
            Log.w("MobileNetworkUtils", "getBooleanCarrierConfig: Could not resolve carrier config");
            return false;
        }
        PersistableBundle configForSubId = carrierConfigCache.getConfigForSubId(i);
        if (configForSubId != null) {
            z2 = configForSubId.getBoolean(str, z);
        } else {
            z2 = carrierConfigCache.getConfig().getBoolean(str);
        }
        Log.d("MobileNetworkUtils", "getBooleanCarrierConfig, key = " + str + ", ret = " + z2);
        return z2;
    }

    public static boolean isShowWfcByDefault(Context context, int i) {
        return getBooleanCarrierConfig(context, "default_show_wifi_call", i);
    }

    public static boolean isOmaWfcEnable(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("oma.wfc.enable");
        sb.append(i);
        return Settings.Global.getInt(contentResolver, sb.toString(), 0) == 1;
    }

    public static Uri getNotifyContentUri(Uri uri, boolean z, int i) {
        if (z) {
            return Uri.withAppendedPath(uri, "" + i);
        }
        return uri;
    }

    public static boolean isVTResolutionEditable(Context context, int i) {
        return getBooleanCarrierConfig(context, "editable_vt_resolution_bool", i);
    }
}
