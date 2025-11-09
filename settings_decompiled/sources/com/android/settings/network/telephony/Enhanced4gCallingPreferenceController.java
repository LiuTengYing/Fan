package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class Enhanced4gCallingPreferenceController extends Enhanced4gBasePreferenceController {
    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController
    protected int getMode() {
        return 2;
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController, com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public Enhanced4gCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController
    protected String getTitle() {
        return this.mContext.getString(R$string.enhanced_4g_lte_mode_title_advanced_calling);
    }
}
