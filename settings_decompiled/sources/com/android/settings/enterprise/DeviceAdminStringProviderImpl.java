package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import com.android.settings.R$string;
import com.android.settingslib.enterprise.DeviceAdminStringProvider;
import java.util.Objects;
import java.util.function.Supplier;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DeviceAdminStringProviderImpl implements DeviceAdminStringProvider {
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeviceAdminStringProviderImpl(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDefaultDisabledByPolicyTitle() {
        return this.mDevicePolicyManager.getResources().getString("Settings.DISABLED_BY_IT_ADMIN_TITLE", new Supplier() { // from class: com.android.settings.enterprise.DeviceAdminStringProviderImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDefaultDisabledByPolicyTitle$0;
                lambda$getDefaultDisabledByPolicyTitle$0 = DeviceAdminStringProviderImpl.this.lambda$getDefaultDisabledByPolicyTitle$0();
                return lambda$getDefaultDisabledByPolicyTitle$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDefaultDisabledByPolicyTitle$0() {
        return this.mContext.getString(R$string.disabled_by_policy_title);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowAdjustVolumeTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_adjust_volume);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowOutgoingCallsTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_outgoing_calls);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowSmsTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_sms);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisableCameraTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_camera);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisableScreenCaptureTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_screen_capture);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getSuspendPackagesTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_suspend_packages);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDefaultDisabledByPolicyContent() {
        return this.mDevicePolicyManager.getResources().getString("Settings.CONTACT_YOUR_IT_ADMIN", new Supplier() { // from class: com.android.settings.enterprise.DeviceAdminStringProviderImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDefaultDisabledByPolicyContent$1;
                lambda$getDefaultDisabledByPolicyContent$1 = DeviceAdminStringProviderImpl.this.lambda$getDefaultDisabledByPolicyContent$1();
                return lambda$getDefaultDisabledByPolicyContent$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDefaultDisabledByPolicyContent$1() {
        return this.mContext.getString(R$string.default_admin_support_msg);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getLearnMoreHelpPageUrl() {
        return this.mDevicePolicyManager.getResources().getString("Settings.IT_ADMIN_POLICY_DISABLING_INFO_URL", new Supplier() { // from class: com.android.settings.enterprise.DeviceAdminStringProviderImpl$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getLearnMoreHelpPageUrl$2;
                lambda$getLearnMoreHelpPageUrl$2 = DeviceAdminStringProviderImpl.this.lambda$getLearnMoreHelpPageUrl$2();
                return lambda$getLearnMoreHelpPageUrl$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getLearnMoreHelpPageUrl$2() {
        return this.mContext.getString(R$string.help_url_action_disabled_by_it_admin);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledByPolicyTitleForFinancedDevice() {
        return this.mContext.getString(R$string.disabled_by_policy_title_financed_device);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledBiometricsParentConsentTitle() {
        return this.mContext.getString(R$string.disabled_by_policy_title_biometric_parental_consent);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledBiometricsParentConsentContent() {
        return this.mContext.getString(R$string.disabled_by_policy_content_biometric_parental_consent);
    }
}
