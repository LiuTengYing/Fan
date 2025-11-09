package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public abstract class AdminGrantedPermissionsPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final boolean mAsync;
    private final ApplicationFeatureProvider mFeatureProvider;
    private boolean mHasApps;
    private final String[] mPermissions;

    public AdminGrantedPermissionsPreferenceControllerBase(Context context, boolean z, String[] strArr) {
        super(context);
        this.mPermissions = strArr;
        this.mFeatureProvider = FeatureFactory.getFactory(context).getApplicationFeatureProvider(context);
        this.mAsync = z;
        this.mHasApps = false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        this.mFeatureProvider.calculateNumberOfAppsWithAdminGrantedPermissions(this.mPermissions, true, new ApplicationFeatureProvider.NumberOfAppsCallback() { // from class: com.android.settings.enterprise.AdminGrantedPermissionsPreferenceControllerBase$$ExternalSyntheticLambda1
            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                AdminGrantedPermissionsPreferenceControllerBase.this.lambda$updateState$0(preference, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(Preference preference, int i) {
        if (i == 0) {
            this.mHasApps = false;
        } else {
            preference.setSummary(this.mContext.getResources().getQuantityString(R$plurals.enterprise_privacy_number_packages_lower_bound, i, Integer.valueOf(i)));
            this.mHasApps = true;
        }
        preference.setVisible(this.mHasApps);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mAsync) {
            return true;
        }
        final Boolean[] boolArr = {null};
        this.mFeatureProvider.calculateNumberOfAppsWithAdminGrantedPermissions(this.mPermissions, false, new ApplicationFeatureProvider.NumberOfAppsCallback() { // from class: com.android.settings.enterprise.AdminGrantedPermissionsPreferenceControllerBase$$ExternalSyntheticLambda0
            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                AdminGrantedPermissionsPreferenceControllerBase.lambda$isAvailable$1(boolArr, i);
            }
        });
        boolean booleanValue = boolArr[0].booleanValue();
        this.mHasApps = booleanValue;
        return booleanValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$isAvailable$1(Boolean[] boolArr, int i) {
        boolArr[0] = Boolean.valueOf(i > 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey()) && this.mHasApps) {
            return super.handlePreferenceTreeClick(preference);
        }
        return false;
    }
}
