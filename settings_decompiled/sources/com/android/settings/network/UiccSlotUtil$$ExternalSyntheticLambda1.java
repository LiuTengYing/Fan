package com.android.settings.network;

import android.telephony.UiccSlotMapping;
import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class UiccSlotUtil$$ExternalSyntheticLambda1 implements ToIntFunction {
    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((UiccSlotMapping) obj).getLogicalSlotIndex();
    }
}
