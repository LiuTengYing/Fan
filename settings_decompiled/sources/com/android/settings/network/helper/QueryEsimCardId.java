package com.android.settings.network.helper;

import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class QueryEsimCardId implements Callable<AtomicIntegerArray> {
    private TelephonyManager mTelephonyManager;

    public QueryEsimCardId(TelephonyManager telephonyManager) {
        this.mTelephonyManager = telephonyManager;
    }

    @Override // java.util.concurrent.Callable
    public AtomicIntegerArray call() {
        List<UiccCardInfo> uiccCardsInfo = this.mTelephonyManager.getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            return new AtomicIntegerArray(0);
        }
        return new AtomicIntegerArray(uiccCardsInfo.stream().filter(new Predicate() { // from class: com.android.settings.network.helper.QueryEsimCardId$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((UiccCardInfo) obj);
            }
        }).filter(new Predicate() { // from class: com.android.settings.network.helper.QueryEsimCardId$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$call$0;
                lambda$call$0 = QueryEsimCardId.lambda$call$0((UiccCardInfo) obj);
                return lambda$call$0;
            }
        }).mapToInt(new ToIntFunction() { // from class: com.android.settings.network.helper.QueryEsimCardId$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((UiccCardInfo) obj).getCardId();
            }
        }).toArray());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$call$0(UiccCardInfo uiccCardInfo) {
        return !uiccCardInfo.isRemovable() && uiccCardInfo.getCardId() >= 0;
    }
}
