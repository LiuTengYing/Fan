package com.android.settings.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class EnterpriseDisclosurePreferenceController extends BasePreferenceController {
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EnterpriseDisclosurePreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(this.mContext).getEnterprisePrivacyFeatureProvider(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        CharSequence disclosure = getDisclosure();
        if (disclosure == null) {
            return;
        }
        updateFooterPreference(preferenceScreen, disclosure);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return getDisclosure() == null ? 3 : 0;
    }

    CharSequence getDisclosure() {
        return this.mFeatureProvider.getDeviceOwnerDisclosure();
    }

    void updateFooterPreference(PreferenceScreen preferenceScreen, CharSequence charSequence) {
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference(getPreferenceKey());
        footerPreference.setTitle(charSequence);
        footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accounts.EnterpriseDisclosurePreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EnterpriseDisclosurePreferenceController.this.lambda$updateFooterPreference$0(view);
            }
        });
        footerPreference.setLearnMoreText(this.mContext.getString(R$string.footer_learn_more_content_description, getLabelName()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFooterPreference$0(View view) {
        this.mContext.startActivity(new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS"));
    }

    private String getLabelName() {
        return this.mContext.getString(R$string.header_add_an_account);
    }
}
