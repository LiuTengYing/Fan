package com.android.settings.network;

import android.content.Context;
import android.os.ParcelUuid;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.MccTable;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.helper.SelectableSubscriptions;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settings.network.telephony.DeleteEuiccSubscriptionDialogActivity;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.android.settingslib.DeviceInfoUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class SubscriptionUtil {
    private static List<SubscriptionInfo> sActiveResultsForTesting;
    private static List<SubscriptionInfo> sAvailableResultsForTesting;

    public static void setAvailableSubscriptionsForTesting(List<SubscriptionInfo> list) {
        sAvailableResultsForTesting = list;
    }

    public static void setActiveSubscriptionsForTesting(List<SubscriptionInfo> list) {
        sActiveResultsForTesting = list;
    }

    public static List<SubscriptionInfo> getActiveSubscriptions(SubscriptionManager subscriptionManager) {
        List<SubscriptionInfo> list = sActiveResultsForTesting;
        if (list != null) {
            return list;
        }
        if (subscriptionManager == null) {
            return Collections.emptyList();
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        return activeSubscriptionInfoList == null ? new ArrayList() : activeSubscriptionInfoList;
    }

    static boolean isInactiveInsertedPSim(UiccSlotInfo uiccSlotInfo) {
        return (uiccSlotInfo == null || uiccSlotInfo.getIsEuicc() || ((UiccPortInfo) uiccSlotInfo.getPorts().stream().findFirst().get()).isActive() || uiccSlotInfo.getCardStateInfo() != 2) ? false : true;
    }

    public static List<SubscriptionInfo> getAvailableSubscriptions(Context context) {
        List<SubscriptionInfo> list = sAvailableResultsForTesting;
        return list != null ? list : new ArrayList(CollectionUtils.emptyIfNull(getSelectableSubscriptionInfoList(context)));
    }

    public static SubscriptionInfo getAvailableSubscription(Context context, ProxySubscriptionManager proxySubscriptionManager, int i) {
        SubscriptionInfo accessibleSubscriptionInfo = proxySubscriptionManager.getAccessibleSubscriptionInfo(i);
        if (accessibleSubscriptionInfo == null) {
            return null;
        }
        ParcelUuid groupUuid = accessibleSubscriptionInfo.getGroupUuid();
        if (groupUuid == null || isPrimarySubscriptionWithinSameUuid(getUiccSlotsInfo(context), groupUuid, proxySubscriptionManager.getAccessibleSubscriptionsInfo(), i)) {
            return accessibleSubscriptionInfo;
        }
        return null;
    }

    private static UiccSlotInfo[] getUiccSlotsInfo(Context context) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).getUiccSlotsInfo();
    }

    private static boolean isPrimarySubscriptionWithinSameUuid(UiccSlotInfo[] uiccSlotInfoArr, ParcelUuid parcelUuid, List<SubscriptionInfo> list, int i) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : list) {
            if (parcelUuid.equals(subscriptionInfo.getGroupUuid())) {
                if (!subscriptionInfo.isEmbedded()) {
                    arrayList.add(subscriptionInfo);
                } else {
                    if (!subscriptionInfo.isOpportunistic()) {
                        arrayList2.add(subscriptionInfo);
                    }
                    if (subscriptionInfo.getSimSlotIndex() != -1) {
                        arrayList3.add(subscriptionInfo);
                    } else {
                        arrayList4.add(subscriptionInfo);
                    }
                }
            }
        }
        if (uiccSlotInfoArr != null && arrayList.size() > 0) {
            SubscriptionInfo searchForSubscriptionId = searchForSubscriptionId(arrayList, i);
            if (searchForSubscriptionId == null) {
                return false;
            }
            for (UiccSlotInfo uiccSlotInfo : uiccSlotInfoArr) {
                if (uiccSlotInfo != null && !uiccSlotInfo.getIsEuicc() && ((UiccPortInfo) uiccSlotInfo.getPorts().stream().findFirst().get()).getLogicalSlotIndex() == searchForSubscriptionId.getSimSlotIndex()) {
                    return true;
                }
            }
            return false;
        } else if (arrayList2.size() <= 0) {
            if (arrayList.size() > 0) {
                return false;
            }
            return arrayList3.size() > 0 ? ((SubscriptionInfo) arrayList3.get(0)).getSubscriptionId() == i : ((SubscriptionInfo) arrayList4.get(0)).getSubscriptionId() == i;
        } else {
            Iterator it = arrayList2.iterator();
            int i2 = 0;
            boolean z = false;
            while (it.hasNext()) {
                SubscriptionInfo subscriptionInfo2 = (SubscriptionInfo) it.next();
                boolean z2 = subscriptionInfo2.getSubscriptionId() == i;
                if (subscriptionInfo2.getSimSlotIndex() == -1) {
                    z |= z2;
                } else if (z2) {
                    return true;
                } else {
                    i2++;
                }
            }
            if (i2 > 0) {
                return false;
            }
            return z;
        }
    }

    private static SubscriptionInfo searchForSubscriptionId(List<SubscriptionInfo> list, int i) {
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.network.SubscriptionUtil$1DisplayInfo  reason: invalid class name */
    /* loaded from: classes.dex */
    public class C1DisplayInfo {
        public CharSequence originalName;
        public SubscriptionInfo subscriptionInfo;
        public CharSequence uniqueName;

        C1DisplayInfo() {
        }
    }

    public static Map<Integer, CharSequence> getUniqueSubscriptionDisplayNames(final Context context) {
        final Supplier supplier = new Supplier() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                Stream lambda$getUniqueSubscriptionDisplayNames$2;
                lambda$getUniqueSubscriptionDisplayNames$2 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$2(context);
                return lambda$getUniqueSubscriptionDisplayNames$2;
            }
        };
        final HashSet hashSet = new HashSet();
        final Set set = (Set) ((Stream) supplier.get()).filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getUniqueSubscriptionDisplayNames$3;
                lambda$getUniqueSubscriptionDisplayNames$3 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$3(hashSet, (SubscriptionUtil.C1DisplayInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$3;
            }
        }).map(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                CharSequence charSequence;
                charSequence = ((SubscriptionUtil.C1DisplayInfo) obj).originalName;
                return charSequence;
            }
        }).collect(Collectors.toSet());
        Supplier supplier2 = new Supplier() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                Stream lambda$getUniqueSubscriptionDisplayNames$6;
                lambda$getUniqueSubscriptionDisplayNames$6 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$6(supplier, set, context);
                return lambda$getUniqueSubscriptionDisplayNames$6;
            }
        };
        hashSet.clear();
        final Set set2 = (Set) ((Stream) supplier2.get()).filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getUniqueSubscriptionDisplayNames$7;
                lambda$getUniqueSubscriptionDisplayNames$7 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$7(hashSet, (SubscriptionUtil.C1DisplayInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$7;
            }
        }).map(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                CharSequence charSequence;
                charSequence = ((SubscriptionUtil.C1DisplayInfo) obj).uniqueName;
                return charSequence;
            }
        }).collect(Collectors.toSet());
        return (Map) ((Stream) supplier2.get()).map(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionUtil.C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$9;
                lambda$getUniqueSubscriptionDisplayNames$9 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$9(set2, (SubscriptionUtil.C1DisplayInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$9;
            }
        }).collect(Collectors.toMap(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getUniqueSubscriptionDisplayNames$10;
                lambda$getUniqueSubscriptionDisplayNames$10 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$10((SubscriptionUtil.C1DisplayInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$10;
            }
        }, new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                CharSequence charSequence;
                charSequence = ((SubscriptionUtil.C1DisplayInfo) obj).uniqueName;
                return charSequence;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Stream lambda$getUniqueSubscriptionDisplayNames$2(final Context context) {
        return getAvailableSubscriptions(context).stream().filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getUniqueSubscriptionDisplayNames$0;
                lambda$getUniqueSubscriptionDisplayNames$0 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$0((SubscriptionInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$0;
            }
        }).map(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionUtil.C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$1;
                lambda$getUniqueSubscriptionDisplayNames$1 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$1(context, (SubscriptionInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getUniqueSubscriptionDisplayNames$0(SubscriptionInfo subscriptionInfo) {
        return (subscriptionInfo == null || subscriptionInfo.getDisplayName() == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$1(Context context, SubscriptionInfo subscriptionInfo) {
        String trim;
        C1DisplayInfo c1DisplayInfo = new C1DisplayInfo();
        c1DisplayInfo.subscriptionInfo = subscriptionInfo;
        String charSequence = subscriptionInfo.getDisplayName().toString();
        if (TextUtils.equals(charSequence, "CARD")) {
            trim = context.getResources().getString(R$string.sim_card);
        } else {
            trim = charSequence.trim();
        }
        c1DisplayInfo.originalName = trim;
        return c1DisplayInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getUniqueSubscriptionDisplayNames$3(Set set, C1DisplayInfo c1DisplayInfo) {
        return !set.add(c1DisplayInfo.originalName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Stream lambda$getUniqueSubscriptionDisplayNames$6(Supplier supplier, final Set set, final Context context) {
        return ((Stream) supplier.get()).map(new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionUtil.C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$5;
                lambda$getUniqueSubscriptionDisplayNames$5 = SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$5(set, context, (SubscriptionUtil.C1DisplayInfo) obj);
                return lambda$getUniqueSubscriptionDisplayNames$5;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$5(Set set, Context context, C1DisplayInfo c1DisplayInfo) {
        if (set.contains(c1DisplayInfo.originalName)) {
            String bidiFormattedPhoneNumber = DeviceInfoUtils.getBidiFormattedPhoneNumber(context, c1DisplayInfo.subscriptionInfo);
            if (bidiFormattedPhoneNumber == null) {
                bidiFormattedPhoneNumber = "";
            } else if (bidiFormattedPhoneNumber.length() > 4) {
                bidiFormattedPhoneNumber = bidiFormattedPhoneNumber.substring(bidiFormattedPhoneNumber.length() - 4);
            }
            if (TextUtils.isEmpty(bidiFormattedPhoneNumber)) {
                c1DisplayInfo.uniqueName = c1DisplayInfo.originalName;
            } else {
                c1DisplayInfo.uniqueName = ((Object) c1DisplayInfo.originalName) + " " + bidiFormattedPhoneNumber;
            }
        } else {
            c1DisplayInfo.uniqueName = c1DisplayInfo.originalName;
        }
        return c1DisplayInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getUniqueSubscriptionDisplayNames$7(Set set, C1DisplayInfo c1DisplayInfo) {
        return !set.add(c1DisplayInfo.uniqueName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ C1DisplayInfo lambda$getUniqueSubscriptionDisplayNames$9(Set set, C1DisplayInfo c1DisplayInfo) {
        if (set.contains(c1DisplayInfo.uniqueName)) {
            c1DisplayInfo.uniqueName = ((Object) c1DisplayInfo.originalName) + " " + c1DisplayInfo.subscriptionInfo.getSubscriptionId();
        }
        return c1DisplayInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$getUniqueSubscriptionDisplayNames$10(C1DisplayInfo c1DisplayInfo) {
        return Integer.valueOf(c1DisplayInfo.subscriptionInfo.getSubscriptionId());
    }

    public static CharSequence getUniqueSubscriptionDisplayName(Integer num, Context context) {
        return getUniqueSubscriptionDisplayNames(context).getOrDefault(num, "");
    }

    public static CharSequence getUniqueSubscriptionDisplayName(SubscriptionInfo subscriptionInfo, Context context) {
        return subscriptionInfo == null ? "" : getUniqueSubscriptionDisplayName(Integer.valueOf(subscriptionInfo.getSubscriptionId()), context);
    }

    public static boolean showToggleForPhysicalSim(SubscriptionManager subscriptionManager) {
        return subscriptionManager.canDisablePhysicalSubscription();
    }

    public static int getPhoneId(Context context, int i) {
        SubscriptionInfo activeSubscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null || (activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(i)) == null) {
            return -1;
        }
        return activeSubscriptionInfo.getSimSlotIndex();
    }

    public static List<SubscriptionInfo> getSelectableSubscriptionInfoList(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        List<SubscriptionInfo> availableSubscriptionInfoList = subscriptionManager.getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        for (SubscriptionInfo subscriptionInfo : availableSubscriptionInfoList) {
            if (isSubscriptionVisible(subscriptionManager, context, subscriptionInfo)) {
                ParcelUuid groupUuid = subscriptionInfo.getGroupUuid();
                if (groupUuid == null) {
                    arrayList.add(subscriptionInfo);
                } else if (!hashMap.containsKey(groupUuid) || (((SubscriptionInfo) hashMap.get(groupUuid)).getSimSlotIndex() == -1 && subscriptionInfo.getSimSlotIndex() != -1)) {
                    arrayList.remove(hashMap.get(groupUuid));
                    arrayList.add(subscriptionInfo);
                    hashMap.put(groupUuid, subscriptionInfo);
                }
            }
        }
        return arrayList;
    }

    public static void startToggleSubscriptionDialogActivity(Context context, int i, boolean z) {
        if (!SubscriptionManager.isUsableSubscriptionId(i)) {
            Log.i("SubscriptionUtil", "Unable to toggle subscription due to invalid subscription ID.");
        } else {
            context.startActivity(ToggleSubscriptionDialogActivity.getIntent(context, i, z));
        }
    }

    public static void startDeleteEuiccSubscriptionDialogActivity(Context context, int i) {
        if (!SubscriptionManager.isUsableSubscriptionId(i)) {
            Log.i("SubscriptionUtil", "Unable to delete subscription due to invalid subscription ID.");
        } else {
            context.startActivity(DeleteEuiccSubscriptionDialogActivity.getIntent(context, i));
        }
    }

    public static SubscriptionInfo getSubById(SubscriptionManager subscriptionManager, final int i) {
        if (i == -1) {
            return null;
        }
        return (SubscriptionInfo) subscriptionManager.getAllSubscriptionInfoList().stream().filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSubById$12;
                lambda$getSubById$12 = SubscriptionUtil.lambda$getSubById$12(i, (SubscriptionInfo) obj);
                return lambda$getSubById$12;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSubById$12(int i, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == i;
    }

    public static boolean isSubscriptionVisible(SubscriptionManager subscriptionManager, Context context, SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return false;
        }
        if (subscriptionInfo.getGroupUuid() == null || !subscriptionInfo.isOpportunistic()) {
            return true;
        }
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).hasCarrierPrivileges() || subscriptionManager.canManageSubscription(subscriptionInfo);
    }

    public static List<SubscriptionInfo> findAllSubscriptionsInGroup(SubscriptionManager subscriptionManager, int i) {
        SubscriptionInfo subById = getSubById(subscriptionManager, i);
        if (subById == null) {
            return Collections.emptyList();
        }
        final ParcelUuid groupUuid = subById.getGroupUuid();
        List availableSubscriptionInfoList = subscriptionManager.getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList == null || availableSubscriptionInfoList.isEmpty() || groupUuid == null) {
            return Collections.singletonList(subById);
        }
        return (List) availableSubscriptionInfoList.stream().filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda14
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findAllSubscriptionsInGroup$13;
                lambda$findAllSubscriptionsInGroup$13 = SubscriptionUtil.lambda$findAllSubscriptionsInGroup$13(groupUuid, (SubscriptionInfo) obj);
                return lambda$findAllSubscriptionsInGroup$13;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findAllSubscriptionsInGroup$13(ParcelUuid parcelUuid, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.isEmbedded() && parcelUuid.equals(subscriptionInfo.getGroupUuid());
    }

    public static String getFormattedPhoneNumber(Context context, SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            Log.e("SubscriptionUtil", "Invalid subscription.");
            return null;
        }
        String phoneNumber = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getPhoneNumber(subscriptionInfo.getSubscriptionId());
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        return PhoneNumberUtils.formatNumber(phoneNumber, MccTable.countryCodeForMcc(subscriptionInfo.getMccString()));
    }

    public static SubscriptionInfo getFirstRemovableSubscription(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        List<UiccCardInfo> uiccCardsInfo = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            Log.w("SubscriptionUtil", "UICC cards info list is empty.");
            return null;
        }
        List<SubscriptionInfo> allSubscriptionInfoList = subscriptionManager.getAllSubscriptionInfoList();
        if (allSubscriptionInfoList == null) {
            Log.w("SubscriptionUtil", "All subscription info list is empty.");
            return null;
        }
        for (UiccCardInfo uiccCardInfo : uiccCardsInfo) {
            if (uiccCardInfo == null) {
                Log.w("SubscriptionUtil", "Got null card.");
            } else if (!uiccCardInfo.isRemovable() || uiccCardInfo.getCardId() == -1) {
                Log.i("SubscriptionUtil", "Skip embedded card or invalid cardId on slot: " + uiccCardInfo.getPhysicalSlotIndex());
            } else {
                Log.i("SubscriptionUtil", "Target removable cardId :" + uiccCardInfo.getCardId());
                for (SubscriptionInfo subscriptionInfo : allSubscriptionInfoList) {
                    if (uiccCardInfo.getCardId() == subscriptionInfo.getCardId()) {
                        return subscriptionInfo;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public static CharSequence getDefaultSimConfig(Context context, int i) {
        boolean z = i == getDefaultVoiceSubscriptionId();
        boolean z2 = i == getDefaultSmsSubscriptionId();
        boolean z3 = i == getDefaultDataSubscriptionId();
        if (z3 || z || z2) {
            StringBuilder sb = new StringBuilder();
            if (z3) {
                sb.append(getResForDefaultConfig(context, R$string.default_active_sim_mobile_data));
                sb.append(", ");
            }
            if (z) {
                sb.append(getResForDefaultConfig(context, R$string.default_active_sim_calls));
                sb.append(", ");
            }
            if (z2) {
                sb.append(getResForDefaultConfig(context, R$string.default_active_sim_sms));
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            return context.getResources().getString(R$string.sim_category_default_active_sim, sb);
        }
        return null;
    }

    private static String getResForDefaultConfig(Context context, int i) {
        return context.getResources().getString(i);
    }

    private static int getDefaultVoiceSubscriptionId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    private static int getDefaultSmsSubscriptionId() {
        return SubscriptionManager.getDefaultSmsSubscriptionId();
    }

    private static int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SubscriptionAnnotation getDefaultSubscriptionSelection(List<SubscriptionAnnotation> list) {
        if (list == null) {
            return null;
        }
        return list.stream().filter(new MobileNetworkSummaryStatus$$ExternalSyntheticLambda3()).filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((SubscriptionAnnotation) obj).isActive();
            }
        }).findFirst().orElse(null);
    }

    public static SubscriptionInfo getSubscriptionOrDefault(Context context, int i) {
        return getSubscription(context, i, i != -1 ? null : new Function() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionAnnotation defaultSubscriptionSelection;
                defaultSubscriptionSelection = SubscriptionUtil.getDefaultSubscriptionSelection((List) obj);
                return defaultSubscriptionSelection;
            }
        });
    }

    private static SubscriptionInfo getSubscription(Context context, final int i, Function<List<SubscriptionAnnotation>, SubscriptionAnnotation> function) {
        List<SubscriptionAnnotation> call = new SelectableSubscriptions(context, true).call();
        Log.d("SubscriptionUtil", "get subId=" + i + " from " + call);
        SubscriptionAnnotation orElse = call.stream().filter(new MobileNetworkSummaryStatus$$ExternalSyntheticLambda3()).filter(new Predicate() { // from class: com.android.settings.network.SubscriptionUtil$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSubscription$15;
                lambda$getSubscription$15 = SubscriptionUtil.lambda$getSubscription$15(i, (SubscriptionAnnotation) obj);
                return lambda$getSubscription$15;
            }
        }).findFirst().orElse(null);
        if (orElse == null && function != null) {
            orElse = function.apply(call);
        }
        if (orElse == null) {
            return null;
        }
        return orElse.getSubInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSubscription$15(int i, SubscriptionAnnotation subscriptionAnnotation) {
        return subscriptionAnnotation.getSubscriptionId() == i;
    }
}
