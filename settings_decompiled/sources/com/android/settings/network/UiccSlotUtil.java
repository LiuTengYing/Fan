package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.UiccSlotMapping;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.utils.ThreadUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class UiccSlotUtil {
    public static ImmutableList<UiccSlotInfo> getSlotInfos(TelephonyManager telephonyManager) {
        UiccSlotInfo[] uiccSlotsInfo = telephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(uiccSlotsInfo);
    }

    public static synchronized void switchToRemovableSlot(int i, Context context) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            switchToRemovableSlot(context, i, null);
        }
    }

    public static synchronized void switchToRemovableSlot(Context context, int i, SubscriptionInfo subscriptionInfo) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            if (ThreadUtils.isMainThread()) {
                throw new IllegalThreadStateException("Do not call switchToRemovableSlot on the main thread.");
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            int inactiveRemovableSlot = getInactiveRemovableSlot(telephonyManager.getUiccSlotsInfo(), i);
            Log.d("UiccSlotUtil", "The InactiveRemovableSlot: " + inactiveRemovableSlot);
            if (inactiveRemovableSlot == -1) {
                return;
            }
            Collection simSlotMapping = telephonyManager.getSimSlotMapping();
            Log.d("UiccSlotUtil", "The SimSlotMapping: " + simSlotMapping);
            performSwitchToSlot(telephonyManager, prepareUiccSlotMappings(simSlotMapping, true, inactiveRemovableSlot, 0, getExcludedLogicalSlotIndex(simSlotMapping, SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) context.getSystemService(SubscriptionManager.class)), subscriptionInfo, telephonyManager.isMultiSimEnabled())), context);
        }
    }

    public static synchronized void switchToEuiccSlot(Context context, int i, int i2, SubscriptionInfo subscriptionInfo) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            if (ThreadUtils.isMainThread()) {
                throw new IllegalThreadStateException("Do not call switchToRemovableSlot on the main thread.");
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            Collection simSlotMapping = telephonyManager.getSimSlotMapping();
            Log.d("UiccSlotUtil", "The SimSlotMapping: " + simSlotMapping);
            if (isTargetSlotActive(simSlotMapping, i, i2)) {
                Log.d("UiccSlotUtil", "The slot is active, then the sim can enable directly.");
            } else {
                performSwitchToSlot(telephonyManager, prepareUiccSlotMappings(simSlotMapping, false, i, i2, getExcludedLogicalSlotIndex(simSlotMapping, SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) context.getSystemService(SubscriptionManager.class)), subscriptionInfo, telephonyManager.isMultiSimEnabled())), context);
            }
        }
    }

    public static int getEsimSlotId(Context context, int i) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        List<UiccCardInfo> uiccCardsInfo = telephonyManager.getUiccCardsInfo();
        final ImmutableList<UiccSlotInfo> slotInfos = getSlotInfos(telephonyManager);
        SubscriptionInfo subById = SubscriptionUtil.getSubById((SubscriptionManager) context.getSystemService(SubscriptionManager.class), i);
        if (subById != null && subById.isEmbedded()) {
            for (UiccCardInfo uiccCardInfo : uiccCardsInfo) {
                if (uiccCardInfo.getCardId() == subById.getCardId() && uiccCardInfo.getCardId() > -1 && uiccCardInfo.isEuicc() && uiccCardInfo.isRemovable()) {
                    Log.d("UiccSlotUtil", "getEsimSlotId: This subInfo is removable esim.");
                    return uiccCardInfo.getPhysicalSlotIndex();
                }
            }
        }
        int orElse = IntStream.range(0, slotInfos.size()).filter(new IntPredicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda3
            @Override // java.util.function.IntPredicate
            public final boolean test(int i2) {
                boolean lambda$getEsimSlotId$0;
                lambda$getEsimSlotId$0 = UiccSlotUtil.lambda$getEsimSlotId$0(ImmutableList.this, i2);
                return lambda$getEsimSlotId$0;
            }
        }).findFirst().orElse(-1);
        Log.i("UiccSlotUtil", "firstEsimSlot: " + orElse);
        return orElse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getEsimSlotId$0(ImmutableList immutableList, int i) {
        UiccSlotInfo uiccSlotInfo = (UiccSlotInfo) immutableList.get(i);
        if (uiccSlotInfo == null) {
            return false;
        }
        return uiccSlotInfo.getIsEuicc();
    }

    private static boolean isTargetSlotActive(Collection<UiccSlotMapping> collection, final int i, final int i2) {
        return collection.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isTargetSlotActive$1;
                lambda$isTargetSlotActive$1 = UiccSlotUtil.lambda$isTargetSlotActive$1(i, i2, (UiccSlotMapping) obj);
                return lambda$isTargetSlotActive$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isTargetSlotActive$1(int i, int i2, UiccSlotMapping uiccSlotMapping) {
        return uiccSlotMapping.getPhysicalSlotIndex() == i && uiccSlotMapping.getPortIndex() == i2;
    }

    private static void performSwitchToSlot(TelephonyManager telephonyManager, Collection<UiccSlotMapping> collection, Context context) throws UiccSlotsException {
        CountDownLatch countDownLatch;
        CarrierConfigChangedReceiver carrierConfigChangedReceiver;
        long j = Settings.Global.getLong(context.getContentResolver(), "euicc_switch_slot_timeout_millis", 25000L);
        BroadcastReceiver broadcastReceiver = null;
        try {
            try {
                countDownLatch = new CountDownLatch(1);
                carrierConfigChangedReceiver = new CarrierConfigChangedReceiver(countDownLatch);
            } catch (InterruptedException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            carrierConfigChangedReceiver.registerOn(context);
            telephonyManager.setSimSlotMapping(collection);
            countDownLatch.await(j, TimeUnit.MILLISECONDS);
            context.unregisterReceiver(carrierConfigChangedReceiver);
        } catch (InterruptedException e2) {
            e = e2;
            broadcastReceiver = carrierConfigChangedReceiver;
            Thread.currentThread().interrupt();
            Log.e("UiccSlotUtil", "Failed switching to physical slot.", e);
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (Throwable th2) {
            th = th2;
            broadcastReceiver = carrierConfigChangedReceiver;
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
            throw th;
        }
    }

    private static int getInactiveRemovableSlot(UiccSlotInfo[] uiccSlotInfoArr, int i) throws UiccSlotsException {
        if (uiccSlotInfoArr != null) {
            if (i == -1) {
                for (int i2 = 0; i2 < uiccSlotInfoArr.length; i2++) {
                    if (uiccSlotInfoArr[i2].isRemovable() && !((UiccPortInfo) uiccSlotInfoArr[i2].getPorts().stream().findFirst().get()).isActive() && uiccSlotInfoArr[i2].getCardStateInfo() != 3 && uiccSlotInfoArr[i2].getCardStateInfo() != 4) {
                        return i2;
                    }
                }
            } else if (i >= uiccSlotInfoArr.length || !uiccSlotInfoArr[i].isRemovable()) {
                throw new UiccSlotsException("The given slotId is not a removable slot: " + i);
            } else if (!((UiccPortInfo) uiccSlotInfoArr[i].getPorts().stream().findFirst().get()).isActive()) {
                return i;
            }
            return -1;
        }
        throw new UiccSlotsException("UiccSlotInfo is null");
    }

    @VisibleForTesting
    static Collection<UiccSlotMapping> prepareUiccSlotMappings(Collection<UiccSlotMapping> collection, boolean z, int i, int i2, int i3) {
        if (i3 == -1) {
            Log.d("UiccSlotUtil", "There is no removedLogicalSlotId. Do nothing.");
            return collection;
        }
        int i4 = 0;
        Log.d("UiccSlotUtil", String.format("Create new SimSlotMapping. Remove the UiccSlotMapping of logicalSlot%d, and insert PhysicalSlotId%d-Port%d", Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2)));
        ArrayList arrayList = new ArrayList();
        if (z) {
            arrayList.add(new UiccSlotMapping(i2, i, 0));
            i4 = 1;
        }
        for (UiccSlotMapping uiccSlotMapping : (Collection) collection.stream().sorted(Comparator.comparingInt(new UiccSlotUtil$$ExternalSyntheticLambda1())).collect(Collectors.toList())) {
            if (uiccSlotMapping.getLogicalSlotIndex() != i3) {
                if (z) {
                    uiccSlotMapping = new UiccSlotMapping(uiccSlotMapping.getPortIndex(), uiccSlotMapping.getPhysicalSlotIndex(), i4);
                    i4++;
                }
                arrayList.add(uiccSlotMapping);
            } else if (!z) {
                arrayList.add(new UiccSlotMapping(i2, i, uiccSlotMapping.getLogicalSlotIndex()));
            }
        }
        Log.d("UiccSlotUtil", "The new SimSlotMapping: " + arrayList);
        return arrayList;
    }

    @VisibleForTesting
    static int getExcludedLogicalSlotIndex(Collection<UiccSlotMapping> collection, final Collection<SubscriptionInfo> collection2, SubscriptionInfo subscriptionInfo, boolean z) {
        if (!z) {
            Log.i("UiccSlotUtil", "In the ss mode.");
            return 0;
        } else if (subscriptionInfo != null) {
            Log.i("UiccSlotUtil", "The removedSubInfo is not null");
            return subscriptionInfo.getSimSlotIndex();
        } else {
            Log.i("UiccSlotUtil", "The removedSubInfo is null");
            return collection.stream().filter(new Predicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getExcludedLogicalSlotIndex$2;
                    lambda$getExcludedLogicalSlotIndex$2 = UiccSlotUtil.lambda$getExcludedLogicalSlotIndex$2(collection2, (UiccSlotMapping) obj);
                    return lambda$getExcludedLogicalSlotIndex$2;
                }
            }).sorted(Comparator.comparingInt(new UiccSlotUtil$$ExternalSyntheticLambda1())).mapToInt(new ToIntFunction() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda2
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int logicalSlotIndex;
                    logicalSlotIndex = ((UiccSlotMapping) obj).getLogicalSlotIndex();
                    return logicalSlotIndex;
                }
            }).findFirst().orElse(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getExcludedLogicalSlotIndex$2(Collection collection, UiccSlotMapping uiccSlotMapping) {
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (((SubscriptionInfo) it.next()).getSimSlotIndex() == uiccSlotMapping.getLogicalSlotIndex()) {
                return false;
            }
        }
        return true;
    }
}
