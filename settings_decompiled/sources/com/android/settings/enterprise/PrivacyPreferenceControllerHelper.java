package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import java.util.Objects;
import java.util.function.Supplier;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PrivacyPreferenceControllerHelper {
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrivacyPreferenceControllerHelper(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        final String deviceOwnerOrganizationName = this.mFeatureProvider.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            preference.setSummary(this.mDevicePolicyManager.getResources().getString("Settings.MANAGED_DEVICE_INFO_SUMMARY", new Supplier() { // from class: com.android.settings.enterprise.PrivacyPreferenceControllerHelper$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateState$0;
                    lambda$updateState$0 = PrivacyPreferenceControllerHelper.this.lambda$updateState$0();
                    return lambda$updateState$0;
                }
            }));
        } else {
            preference.setSummary(this.mDevicePolicyManager.getResources().getString("Settings.MANAGED_DEVICE_INFO_SUMMARY_WITH_NAME", new Supplier() { // from class: com.android.settings.enterprise.PrivacyPreferenceControllerHelper$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateState$1;
                    lambda$updateState$1 = PrivacyPreferenceControllerHelper.this.lambda$updateState$1(deviceOwnerOrganizationName);
                    return lambda$updateState$1;
                }
            }, new Object[]{deviceOwnerOrganizationName}));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() {
        return this.mContext.getString(R$string.enterprise_privacy_settings_summary_generic);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$1(String str) {
        return this.mContext.getResources().getString(R$string.enterprise_privacy_settings_summary_with_name, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasDeviceOwner() {
        return this.mFeatureProvider.hasDeviceOwner();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFinancedDevice() {
        if (this.mDevicePolicyManager.isDeviceManaged()) {
            DevicePolicyManager devicePolicyManager = this.mDevicePolicyManager;
            if (devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return true;
            }
        }
        return false;
    }
}
