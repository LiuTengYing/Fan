package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public abstract class CaCertsPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final EnterprisePrivacyFeatureProvider mFeatureProvider;

    protected abstract int getNumberOfCaCerts();

    public CaCertsPreferenceControllerBase(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int numberOfCaCerts = getNumberOfCaCerts();
        preference.setSummary(this.mContext.getResources().getQuantityString(R$plurals.enterprise_privacy_number_ca_certs, numberOfCaCerts, Integer.valueOf(numberOfCaCerts)));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return getNumberOfCaCerts() > 0;
    }
}
