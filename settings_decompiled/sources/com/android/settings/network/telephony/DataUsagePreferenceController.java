package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import android.os.Parcelable;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.datausage.lib.DataUsageLib;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class DataUsagePreferenceController extends TelephonyBasePreferenceController {
    private static final String LOG_TAG = "DataUsagePreferCtrl";
    private Future<Long> mHistoricalUsageLevel;
    private AtomicReference<NetworkTemplate> mTemplate;
    private Future<NetworkTemplate> mTemplateFuture;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DataUsagePreferenceController(Context context, String str) {
        super(context, str);
        this.mTemplate = new AtomicReference<>();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (SubscriptionManager.isValidSubscriptionId(i) && DataUsageUtils.hasMobileData(this.mContext)) ? 0 : 1;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            Intent intent = new Intent("android.settings.MOBILE_DATA_USAGE");
            intent.putExtra("network_template", (Parcelable) getNetworkTemplate());
            intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
            this.mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            preference.setEnabled(false);
            return;
        }
        CharSequence dataUsageSummary = getDataUsageSummary(this.mContext, this.mSubId);
        if (dataUsageSummary == null) {
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(dataUsageSummary);
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTemplate.set(null);
        this.mTemplateFuture = ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.telephony.DataUsagePreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$init$0;
                lambda$init$0 = DataUsagePreferenceController.this.lambda$init$0();
                return lambda$init$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$init$0() throws Exception {
        return fetchMobileTemplate(this.mContext, this.mSubId);
    }

    private NetworkTemplate fetchMobileTemplate(Context context, int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return DataUsageLib.getMobileTemplate(context, i);
        }
        return null;
    }

    private NetworkTemplate getNetworkTemplate() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        NetworkTemplate networkTemplate = this.mTemplate.get();
        if (networkTemplate != null) {
            return networkTemplate;
        }
        try {
            NetworkTemplate networkTemplate2 = this.mTemplateFuture.get();
            try {
                this.mTemplate.set(networkTemplate2);
                return networkTemplate2;
            } catch (InterruptedException | NullPointerException | ExecutionException e) {
                e = e;
                networkTemplate = networkTemplate2;
                Log.e(LOG_TAG, "Fail to get data usage template", e);
                return networkTemplate;
            }
        } catch (InterruptedException | NullPointerException | ExecutionException e2) {
            e = e2;
        }
    }

    @VisibleForTesting
    DataUsageController.DataUsageInfo getDataUsageInfo(DataUsageController dataUsageController) {
        return dataUsageController.getDataUsageInfo(getNetworkTemplate());
    }

    private CharSequence getDataUsageSummary(Context context, int i) {
        final DataUsageController dataUsageController = new DataUsageController(context);
        dataUsageController.setSubscriptionId(i);
        this.mHistoricalUsageLevel = ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.telephony.DataUsagePreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$getDataUsageSummary$1;
                lambda$getDataUsageSummary$1 = DataUsagePreferenceController.this.lambda$getDataUsageSummary$1(dataUsageController);
                return lambda$getDataUsageSummary$1;
            }
        });
        DataUsageController.DataUsageInfo dataUsageInfo = getDataUsageInfo(dataUsageController);
        long j = dataUsageInfo.usageLevel;
        if (j <= 0) {
            try {
                j = this.mHistoricalUsageLevel.get().longValue();
            } catch (Exception unused) {
            }
        }
        if (j <= 0) {
            return null;
        }
        return context.getString(R$string.data_usage_template, DataUsageUtils.formatDataUsage(context, j), dataUsageInfo.period);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$getDataUsageSummary$1(DataUsageController dataUsageController) throws Exception {
        return Long.valueOf(dataUsageController.getHistoricalUsageLevel(getNetworkTemplate()));
    }
}
