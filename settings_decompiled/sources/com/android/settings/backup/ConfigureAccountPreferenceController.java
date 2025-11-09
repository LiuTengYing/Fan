package com.android.settings.backup;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class ConfigureAccountPreferenceController extends BasePreferenceController {
    private PrivacySettingsConfigData mPSCD;

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

    public ConfigureAccountPreferenceController(Context context, String str) {
        super(context, str);
        this.mPSCD = PrivacySettingsConfigData.getInstance();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (PrivacySettingsUtils.isAdminUser(this.mContext)) {
            return PrivacySettingsUtils.isInvisibleKey(this.mContext, "configure_account") ? 3 : 0;
        }
        return 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Intent configIntent = this.mPSCD.getConfigIntent();
        preference.setEnabled(configIntent != null && this.mPSCD.isBackupEnabled());
        preference.setIntent(configIntent);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String configSummary = this.mPSCD.getConfigSummary();
        return configSummary != null ? configSummary : this.mContext.getText(R$string.backup_configure_account_default_summary);
    }
}
