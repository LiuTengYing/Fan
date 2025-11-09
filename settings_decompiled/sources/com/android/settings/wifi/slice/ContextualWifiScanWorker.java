package com.android.settings.wifi.slice;

import android.content.Context;
import android.net.Uri;
/* loaded from: classes2.dex */
public class ContextualWifiScanWorker extends WifiScanWorker {
    public ContextualWifiScanWorker(Context context, Uri uri) {
        super(context, uri);
    }

    @Override // com.android.settings.wifi.slice.WifiScanWorker
    protected int getApRowCount() {
        return ContextualWifiSlice.getApRowCount();
    }
}
