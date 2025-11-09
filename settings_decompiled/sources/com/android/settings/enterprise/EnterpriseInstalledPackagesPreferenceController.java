package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class EnterpriseInstalledPackagesPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final boolean mAsync;
    private final ApplicationFeatureProvider mFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "number_enterprise_installed_packages";
    }

    public EnterpriseInstalledPackagesPreferenceController(Context context, boolean z) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getApplicationFeatureProvider(context);
        this.mAsync = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        this.mFeatureProvider.calculateNumberOfPolicyInstalledApps(true, new ApplicationFeatureProvider.NumberOfAppsCallback() { // from class: com.android.settings.enterprise.EnterpriseInstalledPackagesPreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                EnterpriseInstalledPackagesPreferenceController.this.lambda$updateState$0(preference, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(Preference preference, int i) {
        boolean z = false;
        if (i != 0) {
            preference.setSummary(this.mContext.getResources().getQuantityString(R$plurals.enterprise_privacy_number_packages_lower_bound, i, Integer.valueOf(i)));
            z = true;
        }
        preference.setVisible(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mAsync) {
            return true;
        }
        final Boolean[] boolArr = {null};
        this.mFeatureProvider.calculateNumberOfPolicyInstalledApps(false, new ApplicationFeatureProvider.NumberOfAppsCallback() { // from class: com.android.settings.enterprise.EnterpriseInstalledPackagesPreferenceController$$ExternalSyntheticLambda1
            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                EnterpriseInstalledPackagesPreferenceController.lambda$isAvailable$1(boolArr, i);
            }
        });
        return boolArr[0].booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$isAvailable$1(Boolean[] boolArr, int i) {
        boolArr[0] = Boolean.valueOf(i > 0);
    }
}
