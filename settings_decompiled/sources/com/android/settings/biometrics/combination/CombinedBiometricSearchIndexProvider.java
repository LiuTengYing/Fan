package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.pm.PackageManager;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class CombinedBiometricSearchIndexProvider extends BaseSearchIndexProvider {
    public CombinedBiometricSearchIndexProvider(int i) {
        super(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.search.BaseSearchIndexProvider
    public boolean isPageSearchEnabled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature("android.hardware.biometrics.face") && packageManager.hasSystemFeature("android.hardware.fingerprint");
    }
}
