package com.android.settings.datausage.lib;

import android.content.Context;
import android.net.NetworkTemplate;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class DataUsageLib {
    public static NetworkTemplate getMobileTemplate(Context context, int i) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        int subscriptionId = telephonyManager.getSubscriptionId();
        List<SubscriptionInfo> availableSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList == null) {
            Log.i("DataUsageLib", "Subscription is not inited: " + i);
            return getMobileTemplateForSubId(telephonyManager, subscriptionId);
        }
        for (SubscriptionInfo subscriptionInfo : availableSubscriptionInfoList) {
            if (subscriptionInfo != null && subscriptionInfo.getSubscriptionId() == i) {
                return getNormalizedMobileTemplate(telephonyManager, i);
            }
        }
        Log.i("DataUsageLib", "Subscription is not active: " + i);
        return getMobileTemplateForSubId(telephonyManager, subscriptionId);
    }

    private static NetworkTemplate getNormalizedMobileTemplate(TelephonyManager telephonyManager, int i) {
        NetworkTemplate mobileTemplateForSubId = getMobileTemplateForSubId(telephonyManager, i);
        String[] mergedImsisFromGroup = telephonyManager.createForSubscriptionId(i).getMergedImsisFromGroup();
        if (ArrayUtils.isEmpty(mergedImsisFromGroup)) {
            Log.i("DataUsageLib", "mergedSubscriberIds is null.");
            return mobileTemplateForSubId;
        }
        return normalizeMobileTemplate(mobileTemplateForSubId, mergedImsisFromGroup);
    }

    private static NetworkTemplate normalizeMobileTemplate(NetworkTemplate networkTemplate, String[] strArr) {
        if (networkTemplate.getSubscriberIds().isEmpty()) {
            return networkTemplate;
        }
        return Set.of((Object[]) strArr).contains((String) networkTemplate.getSubscriberIds().iterator().next()) ? new NetworkTemplate.Builder(networkTemplate.getMatchRule()).setSubscriberIds(Set.of((Object[]) strArr)).setMeteredness(networkTemplate.getMeteredness()).build() : networkTemplate;
    }

    public static NetworkTemplate getMobileTemplateForSubId(TelephonyManager telephonyManager, int i) {
        String subscriberId = telephonyManager.getSubscriberId(i);
        if (subscriberId != null) {
            return new NetworkTemplate.Builder(10).setSubscriberIds(Set.of(subscriberId)).setMeteredness(1).build();
        }
        return new NetworkTemplate.Builder(1).setMeteredness(1).build();
    }
}
