package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.text.format.DateUtils;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Date;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class AdminActionPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final EnterprisePrivacyFeatureProvider mFeatureProvider;

    protected abstract Date getAdminActionTimestamp();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdminActionPreferenceControllerBase(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String formatDateTime;
        Date adminActionTimestamp = getAdminActionTimestamp();
        if (adminActionTimestamp == null) {
            formatDateTime = getEnterprisePrivacyNone();
        } else {
            formatDateTime = DateUtils.formatDateTime(this.mContext, adminActionTimestamp.getTime(), 17);
        }
        preference.setSummary(formatDateTime);
    }

    private String getEnterprisePrivacyNone() {
        return ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getResources().getString("Settings.ADMIN_ACTION_NONE", new Supplier() { // from class: com.android.settings.enterprise.AdminActionPreferenceControllerBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getEnterprisePrivacyNone$0;
                lambda$getEnterprisePrivacyNone$0 = AdminActionPreferenceControllerBase.this.lambda$getEnterprisePrivacyNone$0();
                return lambda$getEnterprisePrivacyNone$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEnterprisePrivacyNone$0() {
        return this.mContext.getString(R$string.enterprise_privacy_none);
    }
}
