package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import com.android.wifitrackerlib.StandardWifiEntry;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class SavedNetworkTracker$$ExternalSyntheticLambda3 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return new StandardWifiEntry.StandardWifiEntryKey((WifiConfiguration) obj);
    }
}
