package com.android.wifitrackerlib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.applications.RecentAppOpsAccess;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
public class Utils {
    public static int convertSecurityTypeToDpmWifiSecurity(int i) {
        switch (i) {
            case 0:
            case 6:
                return 0;
            case 1:
            case 2:
            case 4:
            case 7:
                return 1;
            case 3:
            case 8:
            case 9:
            case 11:
            case 12:
                return 2;
            case 5:
                return 3;
            case 10:
            default:
                return -1;
        }
    }

    public static ScanResult getBestScanResultByLevel(List<ScanResult> list) {
        if (list.isEmpty()) {
            return null;
        }
        return (ScanResult) Collections.max(list, Comparator.comparingInt(new ToIntFunction() { // from class: com.android.wifitrackerlib.Utils$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int i;
                i = ((ScanResult) obj).level;
                return i;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<Integer> getSecurityTypesFromScanResult(ScanResult scanResult) {
        ArrayList arrayList = new ArrayList();
        for (int i : scanResult.getSecurityTypes()) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<Integer> getSecurityTypesFromWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(14)) {
            return Arrays.asList(8);
        }
        if (wifiConfiguration.allowedKeyManagement.get(13)) {
            return Arrays.asList(7);
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return Arrays.asList(5);
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return Arrays.asList(6);
        }
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return Arrays.asList(4);
        }
        if (wifiConfiguration.allowedKeyManagement.get(4)) {
            return Arrays.asList(2);
        }
        if (wifiConfiguration.allowedKeyManagement.get(2)) {
            return (wifiConfiguration.requirePmf && !wifiConfiguration.allowedPairwiseCiphers.get(1) && wifiConfiguration.allowedProtocols.get(1)) ? Arrays.asList(9) : Arrays.asList(3, 9);
        } else if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return Arrays.asList(2);
        } else {
            if (wifiConfiguration.allowedKeyManagement.get(0) && wifiConfiguration.wepKeys != null) {
                int i = 0;
                while (true) {
                    String[] strArr = wifiConfiguration.wepKeys;
                    if (i >= strArr.length) {
                        break;
                    } else if (strArr[i] != null) {
                        return Arrays.asList(1);
                    } else {
                        i++;
                    }
                }
            }
            return Arrays.asList(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSingleSecurityTypeFromMultipleSecurityTypes(List<Integer> list) {
        if (list.size() == 1) {
            return list.get(0).intValue();
        }
        if (list.size() == 2) {
            if (list.contains(0)) {
                return 0;
            }
            if (list.contains(2)) {
                return 2;
            }
            return list.contains(3) ? 3 : -1;
        }
        return -1;
    }

    static String getAppLabel(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getConnectedDescription(Context context, WifiConfiguration wifiConfiguration, NetworkCapabilities networkCapabilities, boolean z, boolean z2, int i) {
        StringJoiner stringJoiner = new StringJoiner(context.getString(R$string.wifitrackerlib_summary_separator));
        if (wifiConfiguration != null && (wifiConfiguration.fromWifiNetworkSuggestion || wifiConfiguration.fromWifiNetworkSpecifier)) {
            String suggestionOrSpecifierLabel = getSuggestionOrSpecifierLabel(context, wifiConfiguration);
            if (!TextUtils.isEmpty(suggestionOrSpecifierLabel)) {
                if (z) {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_connected_via_app, suggestionOrSpecifierLabel));
                } else {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_available_via_app, suggestionOrSpecifierLabel));
                }
            }
        }
        if (z2) {
            stringJoiner.add(context.getString(R$string.wifi_connected_low_quality));
        }
        String currentNetworkCapabilitiesInformation = getCurrentNetworkCapabilitiesInformation(context, networkCapabilities, i);
        if (!TextUtils.isEmpty(currentNetworkCapabilitiesInformation)) {
            stringJoiner.add(currentNetworkCapabilitiesInformation);
        }
        if (stringJoiner.length() == 0 && z) {
            return context.getResources().getStringArray(R$array.wifitrackerlib_wifi_status)[NetworkInfo.DetailedState.CONNECTED.ordinal()];
        }
        return stringJoiner.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getConnectingDescription(Context context, NetworkInfo networkInfo) {
        NetworkInfo.DetailedState detailedState;
        if (context == null || networkInfo == null || (detailedState = networkInfo.getDetailedState()) == null) {
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(R$array.wifitrackerlib_wifi_status);
        int ordinal = detailedState.ordinal();
        return ordinal >= stringArray.length ? "" : stringArray[ordinal];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getDisconnectedDescription(WifiTrackerInjector wifiTrackerInjector, Context context, WifiConfiguration wifiConfiguration, boolean z, boolean z2) {
        if (context == null || wifiConfiguration == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(context.getString(R$string.wifitrackerlib_summary_separator));
        if (z2) {
            stringJoiner.add(context.getString(R$string.wifitrackerlib_wifi_disconnected));
        } else if (z && !wifiConfiguration.isPasspoint()) {
            if (!wifiTrackerInjector.getNoAttributionAnnotationPackages().contains(wifiConfiguration.creatorName)) {
                String appLabel = getAppLabel(context, wifiConfiguration.creatorName);
                if (!TextUtils.isEmpty(appLabel)) {
                    stringJoiner.add(context.getString(R$string.wifitrackerlib_saved_network, appLabel));
                }
            }
        } else if (wifiConfiguration.fromWifiNetworkSuggestion) {
            String suggestionOrSpecifierLabel = getSuggestionOrSpecifierLabel(context, wifiConfiguration);
            if (!TextUtils.isEmpty(suggestionOrSpecifierLabel)) {
                stringJoiner.add(context.getString(R$string.wifitrackerlib_available_via_app, suggestionOrSpecifierLabel));
            }
        } else {
            stringJoiner.add(context.getString(R$string.wifitrackerlib_wifi_remembered));
        }
        String wifiConfigurationFailureMessage = getWifiConfigurationFailureMessage(context, wifiConfiguration);
        if (!TextUtils.isEmpty(wifiConfigurationFailureMessage)) {
            stringJoiner.add(wifiConfigurationFailureMessage);
        }
        return stringJoiner.toString();
    }

    private static String getSuggestionOrSpecifierLabel(Context context, WifiConfiguration wifiConfiguration) {
        if (context == null || wifiConfiguration == null) {
            return "";
        }
        String carrierNameForSubId = getCarrierNameForSubId(context, getSubIdForConfig(context, wifiConfiguration));
        if (TextUtils.isEmpty(carrierNameForSubId)) {
            String appLabel = getAppLabel(context, wifiConfiguration.creatorName);
            return !TextUtils.isEmpty(appLabel) ? appLabel : wifiConfiguration.creatorName;
        }
        return carrierNameForSubId;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0049, code lost:
        if (r1 != 9) goto L38;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getWifiConfigurationFailureMessage(android.content.Context r4, android.net.wifi.WifiConfiguration r5) {
        /*
            java.lang.String r0 = ""
            if (r4 == 0) goto La5
            if (r5 != 0) goto L8
            goto La5
        L8:
            boolean r1 = r5.hasNoInternetAccess()
            r2 = 2
            if (r1 == 0) goto L23
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r5 = r5.getNetworkSelectionStatus()
            int r5 = r5.getNetworkSelectionStatus()
            if (r5 != r2) goto L1c
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet_no_reconnect
            goto L1e
        L1c:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet
        L1e:
            java.lang.String r4 = r4.getString(r5)
            return r4
        L23:
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r1 = r5.getNetworkSelectionStatus()
            int r1 = r1.getNetworkSelectionStatus()
            if (r1 == 0) goto L6f
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r1 = r5.getNetworkSelectionStatus()
            int r1 = r1.getNetworkSelectionDisableReason()
            r3 = 1
            if (r1 == r3) goto L68
            if (r1 == r2) goto L61
            r2 = 3
            if (r1 == r2) goto L5a
            r2 = 4
            if (r1 == r2) goto L53
            r2 = 6
            if (r1 == r2) goto L53
            r2 = 8
            if (r1 == r2) goto L4c
            r2 = 9
            if (r1 == r2) goto L61
            goto L6f
        L4c:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_check_password_try_again
            java.lang.String r4 = r4.getString(r5)
            return r4
        L53:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_no_internet_no_reconnect
            java.lang.String r4 = r4.getString(r5)
            return r4
        L5a:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_network_failure
            java.lang.String r4 = r4.getString(r5)
            return r4
        L61:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_password_failure
            java.lang.String r4 = r4.getString(r5)
            return r4
        L68:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_disabled_generic
            java.lang.String r4 = r4.getString(r5)
            return r4
        L6f:
            int r5 = r5.getRecentFailureReason()
            r1 = 17
            if (r5 == r1) goto L9e
            switch(r5) {
                case 1002: goto L9e;
                case 1003: goto L97;
                case 1004: goto L9e;
                case 1005: goto L90;
                case 1006: goto L89;
                case 1007: goto L90;
                case 1008: goto L90;
                case 1009: goto L82;
                case 1010: goto L82;
                case 1011: goto L7b;
                default: goto L7a;
            }
        L7a:
            return r0
        L7b:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_network_not_found
            java.lang.String r4 = r4.getString(r5)
            return r4
        L82:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_oce_assoc_disallowed_insufficient_rssi
            java.lang.String r4 = r4.getString(r5)
            return r4
        L89:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_assoc_disallowed_max_num_sta_associated
            java.lang.String r4 = r4.getString(r5)
            return r4
        L90:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_mbo_assoc_disallowed_cannot_connect
            java.lang.String r4 = r4.getString(r5)
            return r4
        L97:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_poor_channel_conditions
            java.lang.String r4 = r4.getString(r5)
            return r4
        L9e:
            int r5 = com.android.wifitrackerlib.R$string.wifitrackerlib_wifi_ap_unable_to_handle_new_sta
            java.lang.String r4 = r4.getString(r5)
            return r4
        La5:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.Utils.getWifiConfigurationFailureMessage(android.content.Context, android.net.wifi.WifiConfiguration):java.lang.String");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getAutoConnectDescription(Context context, WifiEntry wifiEntry) {
        return (context == null || wifiEntry == null || !wifiEntry.canSetAutoJoinEnabled() || wifiEntry.isAutoJoinEnabled()) ? "" : context.getString(R$string.wifitrackerlib_auto_connect_disable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMeteredDescription(Context context, WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null) {
            return "";
        }
        if (wifiEntry.canSetMeteredChoice() || wifiEntry.getMeteredChoice() == 1) {
            if (wifiEntry.getMeteredChoice() == 1) {
                return context.getString(R$string.wifitrackerlib_wifi_metered_label);
            }
            if (wifiEntry.getMeteredChoice() == 2) {
                return context.getString(R$string.wifitrackerlib_wifi_unmetered_label);
            }
            return wifiEntry.isMetered() ? context.getString(R$string.wifitrackerlib_wifi_metered_label) : "";
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getVerboseLoggingDescription(WifiEntry wifiEntry) {
        if (!BaseWifiTracker.isVerboseLoggingEnabled() || wifiEntry == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(" ");
        String wifiInfoDescription = wifiEntry.getWifiInfoDescription();
        if (!TextUtils.isEmpty(wifiInfoDescription)) {
            stringJoiner.add(wifiInfoDescription);
        }
        String networkCapabilityDescription = wifiEntry.getNetworkCapabilityDescription();
        if (!TextUtils.isEmpty(networkCapabilityDescription)) {
            stringJoiner.add(networkCapabilityDescription);
        }
        String scanResultDescription = wifiEntry.getScanResultDescription();
        if (!TextUtils.isEmpty(scanResultDescription)) {
            stringJoiner.add(scanResultDescription);
        }
        String networkSelectionDescription = wifiEntry.getNetworkSelectionDescription();
        if (!TextUtils.isEmpty(networkSelectionDescription)) {
            stringJoiner.add(networkSelectionDescription);
        }
        return stringJoiner.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getNetworkSelectionDescription(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
        if (networkSelectionStatus.getNetworkSelectionStatus() != 0) {
            sb.append(" (" + networkSelectionStatus.getNetworkStatusString());
            if (networkSelectionStatus.getDisableTime() > 0) {
                sb.append(" " + DateUtils.formatElapsedTime((System.currentTimeMillis() - networkSelectionStatus.getDisableTime()) / 1000));
            }
            sb.append(")");
        }
        int maxNetworkSelectionDisableReason = WifiConfiguration.NetworkSelectionStatus.getMaxNetworkSelectionDisableReason();
        for (int i = 0; i <= maxNetworkSelectionDisableReason; i++) {
            int disableReasonCounter = networkSelectionStatus.getDisableReasonCounter(i);
            if (disableReasonCounter != 0) {
                sb.append(" ");
                sb.append(WifiConfiguration.NetworkSelectionStatus.getNetworkSelectionDisableReasonString(i));
                sb.append("=");
                sb.append(disableReasonCounter);
            }
        }
        return sb.toString();
    }

    static String getCurrentNetworkCapabilitiesInformation(Context context, NetworkCapabilities networkCapabilities, int i) {
        if (context != null && networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", RecentAppOpsAccess.ANDROID_SYSTEM_PACKAGE_NAME));
            }
            if (networkCapabilities.hasCapability(24)) {
                return context.getString(R$string.wifitrackerlib_wifi_limited_connection);
            }
            if (!networkCapabilities.hasCapability(16)) {
                if (networkCapabilities.isPrivateDnsBroken()) {
                    return context.getString(R$string.wifitrackerlib_private_dns_broken);
                }
                Log.d("Utils", "ncUpdateCount is: " + i);
                if (i >= 2) {
                    return context.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet);
                }
            }
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSimPresent(Context context, final int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        if (subscriptionManager == null || (activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList()) == null || activeSubscriptionInfoList.isEmpty()) {
            return false;
        }
        if (i == -1) {
            return true;
        }
        return activeSubscriptionInfoList.stream().anyMatch(new Predicate() { // from class: com.android.wifitrackerlib.Utils$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isSimPresent$1;
                lambda$isSimPresent$1 = Utils.lambda$isSimPresent$1(i, (SubscriptionInfo) obj);
                return lambda$isSimPresent$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isSimPresent$1(int i, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getCarrierId() == i;
    }

    static String getCarrierNameForSubId(Context context, int i) {
        TelephonyManager telephonyManager;
        TelephonyManager createForSubscriptionId;
        CharSequence simCarrierIdName;
        if (i == -1 || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null || (createForSubscriptionId = telephonyManager.createForSubscriptionId(i)) == null || (simCarrierIdName = createForSubscriptionId.getSimCarrierIdName()) == null) {
            return null;
        }
        return simCarrierIdName.toString();
    }

    static boolean isServerCertUsedNetwork(WifiConfiguration wifiConfiguration) {
        WifiEnterpriseConfig wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;
        return wifiEnterpriseConfig != null && wifiEnterpriseConfig.isEapMethodServerCertUsed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSimCredential(WifiConfiguration wifiConfiguration) {
        WifiEnterpriseConfig wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;
        return wifiEnterpriseConfig != null && wifiEnterpriseConfig.isAuthenticationSimBased();
    }

    static int getSubIdForConfig(Context context, WifiConfiguration wifiConfiguration) {
        SubscriptionManager subscriptionManager;
        int i = -1;
        if (wifiConfiguration.carrierId == -1 || (subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service")) == null) {
            return -1;
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null && !activeSubscriptionInfoList.isEmpty()) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getCarrierId() == wifiConfiguration.carrierId && (i = subscriptionInfo.getSubscriptionId()) == defaultDataSubscriptionId) {
                    break;
                }
            }
        }
        return i;
    }

    static boolean isImsiPrivacyProtectionProvided(Context context, int i) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        return (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null || (configForSubId.getInt("imsi_key_availability_int") & 2) == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence getImsiProtectionDescription(Context context, WifiConfiguration wifiConfiguration) {
        int subIdForConfig;
        if (context != null && wifiConfiguration != null && isSimCredential(wifiConfiguration) && !isServerCertUsedNetwork(wifiConfiguration)) {
            if (wifiConfiguration.carrierId == -1) {
                subIdForConfig = SubscriptionManager.getDefaultSubscriptionId();
            } else {
                subIdForConfig = getSubIdForConfig(context, wifiConfiguration);
            }
            if (subIdForConfig != -1 && !isImsiPrivacyProtectionProvided(context, subIdForConfig)) {
                return NonSdkApiWrapper.linkifyAnnotation(context, context.getText(R$string.wifitrackerlib_imsi_protection_warning), "url", context.getString(R$string.wifitrackerlib_help_url_imsi_protection));
            }
        }
        return "";
    }

    public static InetAddress getNetworkPart(InetAddress inetAddress, int i) {
        byte[] address = inetAddress.getAddress();
        maskRawAddress(address, i);
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException("getNetworkPart error - " + e.toString());
        }
    }

    public static void maskRawAddress(byte[] bArr, int i) {
        if (i < 0 || i > bArr.length * 8) {
            throw new RuntimeException("IP address with " + bArr.length + " bytes has invalid prefix length " + i);
        }
        int i2 = i / 8;
        byte b = (byte) (255 << (8 - (i % 8)));
        if (i2 < bArr.length) {
            bArr[i2] = (byte) (b & bArr[i2]);
        }
        while (true) {
            i2++;
            if (i2 >= bArr.length) {
                return;
            }
            bArr[i2] = 0;
        }
    }

    private static Context createPackageContextAsUser(int i, Context context) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.getUserHandleForUid(i));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    private static DevicePolicyManager retrieveDevicePolicyManagerFromUserContext(int i, Context context) {
        Context createPackageContextAsUser = createPackageContextAsUser(i, context);
        if (createPackageContextAsUser == null) {
            return null;
        }
        return (DevicePolicyManager) createPackageContextAsUser.getSystemService(DevicePolicyManager.class);
    }

    private static Pair<UserHandle, ComponentName> getDeviceOwner(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        if (devicePolicyManager == null) {
            return null;
        }
        try {
            UserHandle deviceOwnerUser = devicePolicyManager.getDeviceOwnerUser();
            ComponentName deviceOwnerComponentOnAnyUser = devicePolicyManager.getDeviceOwnerComponentOnAnyUser();
            if (deviceOwnerUser == null || deviceOwnerComponentOnAnyUser == null || deviceOwnerComponentOnAnyUser.getPackageName() == null) {
                return null;
            }
            return new Pair<>(deviceOwnerUser, deviceOwnerComponentOnAnyUser);
        } catch (Exception e) {
            throw new RuntimeException("getDeviceOwner error - " + e.toString());
        }
    }

    public static boolean isDeviceOwner(int i, String str, Context context) {
        Pair<UserHandle, ComponentName> deviceOwner;
        return str != null && (deviceOwner = getDeviceOwner(context)) != null && ((UserHandle) deviceOwner.first).equals(UserHandle.getUserHandleForUid(i)) && ((ComponentName) deviceOwner.second).getPackageName().equals(str);
    }

    public static boolean isProfileOwner(int i, String str, Context context) {
        DevicePolicyManager retrieveDevicePolicyManagerFromUserContext;
        if (str == null || (retrieveDevicePolicyManagerFromUserContext = retrieveDevicePolicyManagerFromUserContext(i, context)) == null) {
            return false;
        }
        return retrieveDevicePolicyManagerFromUserContext.isProfileOwnerApp(str);
    }

    public static boolean isDeviceOrProfileOwner(int i, String str, Context context) {
        return isDeviceOwner(i, str, context) || isProfileOwner(i, str, context);
    }

    public static String getStandardString(Context context, int i) {
        if (i == 1) {
            return context.getString(R$string.wifitrackerlib_wifi_standard_legacy);
        }
        switch (i) {
            case 4:
                return context.getString(R$string.wifitrackerlib_wifi_standard_11n);
            case 5:
                return context.getString(R$string.wifitrackerlib_wifi_standard_11ac);
            case 6:
                return context.getString(R$string.wifitrackerlib_wifi_standard_11ax);
            case 7:
                return context.getString(R$string.wifitrackerlib_wifi_standard_11ad);
            case 8:
                return context.getString(R$string.wifitrackerlib_wifi_standard_11be);
            default:
                return context.getString(R$string.wifitrackerlib_wifi_standard_unknown);
        }
    }
}
