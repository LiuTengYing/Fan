package com.android.settingslib.mobile;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.UniCarrierConfigManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.android.settingslib.R$bool;
/* loaded from: classes2.dex */
public class MobileMappings {
    public static String getIconKey(TelephonyDisplayInfo telephonyDisplayInfo) {
        if (telephonyDisplayInfo.getOverrideNetworkType() == 0) {
            return toIconKey(telephonyDisplayInfo.getNetworkType());
        }
        return toDisplayIconKey(telephonyDisplayInfo.getOverrideNetworkType());
    }

    public static String toIconKey(int i) {
        return Integer.toString(i);
    }

    public static String toDisplayIconKey(int i) {
        if (i == 1) {
            return toIconKey(13) + "_CA";
        } else if (i == 2) {
            return toIconKey(13) + "_CA_Plus";
        } else if (i != 3) {
            if (i != 5) {
                return "unsupported";
            }
            return toIconKey(20) + "_Plus";
        } else {
            return toIconKey(20);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x00de  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00fd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.Map<java.lang.String, com.android.settingslib.SignalIcon$MobileIconGroup> mapIconSets(com.android.settingslib.mobile.MobileMappings.Config r10) {
        /*
            Method dump skipped, instructions count: 366
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.mobile.MobileMappings.mapIconSets(com.android.settingslib.mobile.MobileMappings$Config):java.util.Map");
    }

    /* loaded from: classes2.dex */
    public static class Config {
        public boolean hspaDataDistinguishable;
        public boolean showAtLeast3G = false;
        public boolean show4gFor3g = false;
        public boolean alwaysShowCdmaRssi = false;
        public boolean show4gForLte = false;
        public boolean show4glteForLte = false;
        public boolean hideLtePlus = false;
        public boolean hspaDataDistinguishableForSim = false;
        public boolean alwaysShowDataRatIcon = false;
        public boolean showNoServiceVolteIcon = false;

        public static Config readConfig(Context context) {
            Config config = new Config();
            Resources resources = context.getResources();
            config.showAtLeast3G = resources.getBoolean(R$bool.config_showMin3G);
            config.alwaysShowCdmaRssi = resources.getBoolean(17891366);
            config.hspaDataDistinguishable = resources.getBoolean(R$bool.config_hspa_data_distinguishable);
            config.showNoServiceVolteIcon = resources.getBoolean(R$bool.config_show_no_service_volte_icon);
            int i = Settings.Global.getInt(context.getContentResolver(), "multi_sim_data_call", -1);
            int phoneId = SubscriptionManager.getPhoneId(i);
            String string = Settings.Global.getString(context.getContentResolver(), "mobile_icc_operator_numeric" + phoneId);
            Log.d("MobileMappings", "defaultDataSubId " + i + " simOperator " + string);
            PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService("carrier_config")).getConfigForSubId(i);
            if (configForSubId != null) {
                config.alwaysShowDataRatIcon = configForSubId.getBoolean("always_show_data_rat_icon_bool");
                config.show4gForLte = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
                config.show4glteForLte = configForSubId.getBoolean("show_4glte_for_lte_data_icon_bool");
                config.show4gFor3g = configForSubId.getBoolean("show_4g_for_3g_data_icon_bool");
                config.hideLtePlus = configForSubId.getBoolean("hide_lte_plus_data_icon_bool");
                if (!TextUtils.isEmpty(string) && string.matches("^[0-9]{5,6}$")) {
                    Resources resourcesForMccMnc = getResourcesForMccMnc(context, string);
                    config.alwaysShowDataRatIcon |= resourcesForMccMnc.getBoolean(134414341);
                    config.show4gForLte = resourcesForMccMnc.getBoolean(134414353) | config.show4gForLte;
                }
                Log.d("MobileMappings", "config.alwaysShowDataRatIcon " + config.alwaysShowDataRatIcon + " config.show4gForLte " + config.show4gForLte);
            }
            PersistableBundle configForSubId2 = new UniCarrierConfigManager(context).getConfigForSubId(i);
            if (configForSubId2 != null) {
                config.hspaDataDistinguishableForSim = configForSubId2.getBoolean("hspa_data_distinguishable");
            }
            return config;
        }

        private static Resources getResourcesForMccMnc(Context context, String str) {
            Configuration configuration = context.getResources().getConfiguration();
            Configuration configuration2 = new Configuration();
            configuration2.setTo(configuration);
            try {
                configuration2.mcc = Integer.parseInt(str.substring(0, 3));
                int parseInt = Integer.parseInt(str.substring(3));
                configuration2.mnc = parseInt;
                if (parseInt == 0) {
                    configuration2.mnc = 65535;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            DisplayMetrics displayMetrics2 = new DisplayMetrics();
            displayMetrics2.setTo(displayMetrics);
            return new Resources(context.getResources().getAssets(), displayMetrics2, configuration2);
        }
    }
}
