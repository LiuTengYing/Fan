package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
/* loaded from: classes.dex */
public class NrDisabledInDsdsFooterPreferenceController extends BasePreferenceController {
    private int mSubId;

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

    public NrDisabledInDsdsFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mSubId = -1;
    }

    public void init(int i) {
        this.mSubId = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setTitle("");
            preference.setTitle(getFooterText());
        }
    }

    private CharSequence getFooterText() {
        Context context = this.mContext;
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo(this.mContext, "url", HelpUtils.getHelpIntent(context, context.getString(R$string.help_uri_5g_dsds), this.mContext.getClass().getName()));
        return linkInfo.isActionable() ? AnnotationSpan.linkify(this.mContext.getText(R$string.no_5g_in_dsds_text), linkInfo) : AnnotationSpan.textWithoutLink(this.mContext.getText(R$string.no_5g_in_dsds_text));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mSubId == -1) {
            return 2;
        }
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId);
        int[] activeSubscriptionIdList = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getActiveSubscriptionIdList();
        return (!createForSubscriptionId.isDataEnabled() || (activeSubscriptionIdList == null ? 0 : activeSubscriptionIdList.length) < 2 || !is5GSupportedByRadio(createForSubscriptionId) || createForSubscriptionId.canConnectTo5GInDsdsMode()) ? 2 : 0;
    }

    private boolean is5GSupportedByRadio(TelephonyManager telephonyManager) {
        return (telephonyManager.getSupportedRadioAccessFamily() & 524288) > 0;
    }
}
