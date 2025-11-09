package com.android.settings.aware;

import android.content.Context;
import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public class AwareFeatureProviderImpl implements AwareFeatureProvider {
    @Override // com.android.settings.aware.AwareFeatureProvider
    public boolean isEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.aware.AwareFeatureProvider
    public boolean isSupported(Context context) {
        return false;
    }

    @Override // com.android.settings.aware.AwareFeatureProvider
    public void showRestrictionDialog(Fragment fragment) {
    }
}
