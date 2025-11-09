package com.android.settings.network.helper;

import android.telephony.TelephonyManager;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class QuerySimSlotIndex implements Callable<AtomicIntegerArray> {
    private boolean mDisabledSlotsIncluded;
    private boolean mOnlySlotWithSim;
    private TelephonyManager mTelephonyManager;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$call$1(int i, int i2) {
        return i2 >= i;
    }

    public QuerySimSlotIndex(TelephonyManager telephonyManager, boolean z, boolean z2) {
        this.mTelephonyManager = telephonyManager;
        this.mDisabledSlotsIncluded = z;
        this.mOnlySlotWithSim = z2;
    }

    @Override // java.util.concurrent.Callable
    public AtomicIntegerArray call() {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return new AtomicIntegerArray(0);
        }
        final int i = this.mOnlySlotWithSim ? 0 : -1;
        return new AtomicIntegerArray(Arrays.stream(uiccSlotsInfo).flatMapToInt(new Function() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                IntStream lambda$call$0;
                lambda$call$0 = QuerySimSlotIndex.this.lambda$call$0((UiccSlotInfo) obj);
                return lambda$call$0;
            }
        }).filter(new IntPredicate() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda1
            @Override // java.util.function.IntPredicate
            public final boolean test(int i2) {
                boolean lambda$call$1;
                lambda$call$1 = QuerySimSlotIndex.lambda$call$1(i, i2);
                return lambda$call$1;
            }
        }).toArray());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: mapToLogicalSlotIndex */
    public IntStream lambda$call$0(UiccSlotInfo uiccSlotInfo) {
        if (uiccSlotInfo == null) {
            return IntStream.of(-1);
        }
        if (uiccSlotInfo.getCardStateInfo() == 1) {
            return IntStream.of(-1);
        }
        return uiccSlotInfo.getPorts().stream().filter(new Predicate() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$mapToLogicalSlotIndex$2;
                lambda$mapToLogicalSlotIndex$2 = QuerySimSlotIndex.this.lambda$mapToLogicalSlotIndex$2((UiccPortInfo) obj);
                return lambda$mapToLogicalSlotIndex$2;
            }
        }).mapToInt(new ToIntFunction() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda3
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int logicalSlotIndex;
                logicalSlotIndex = ((UiccPortInfo) obj).getLogicalSlotIndex();
                return logicalSlotIndex;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: filterPort */
    public boolean lambda$mapToLogicalSlotIndex$2(UiccPortInfo uiccPortInfo) {
        if (this.mDisabledSlotsIncluded) {
            return true;
        }
        if (uiccPortInfo == null) {
            return false;
        }
        return uiccPortInfo.isActive();
    }
}
