package com.android.settings.enterprise;

import android.content.Context;
import android.net.ConnectivityManager;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class GlobalHttpProxyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final ConnectivityManager mCm;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "global_http_proxy";
    }

    public GlobalHttpProxyPreferenceController(Context context) {
        super(context);
        this.mCm = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mCm.getGlobalProxy() != null;
    }
}
