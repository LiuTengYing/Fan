package com.android.settings.network;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.settings.network.helper.SelectableSubscriptions;
import com.android.settings.network.helper.SubscriptionAnnotation;
import com.android.settings.network.helper.SubscriptionGrouping;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class MobileNetworkSummaryStatus {
    private boolean mDisableReEntranceUpdate;
    private Future<Boolean> mIsEuiccConfiguable;
    private Boolean mIsEuiccConfiguableCache;
    private Future<Boolean> mIsPsimDisableSupported;
    private Boolean mIsPsimDisableSupportedCache;
    private List<SubscriptionAnnotation> mSubscriptionList;
    private Future<Map<Integer, CharSequence>> mUniqueNameMapping;
    private Map<Integer, CharSequence> mUniqueNameMappingCache;

    public void update(final Context context, Consumer<MobileNetworkSummaryStatus> consumer) {
        if (this.mDisableReEntranceUpdate) {
            Log.d("MobileNetworkSummaryStatus", "network summary query ignored");
            if (consumer != null) {
                consumer.accept(this);
                return;
            }
            return;
        }
        this.mDisableReEntranceUpdate = true;
        Log.d("MobileNetworkSummaryStatus", "network summary query");
        this.mIsEuiccConfiguable = ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.MobileNetworkSummaryStatus$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$update$0;
                lambda$update$0 = MobileNetworkSummaryStatus.this.lambda$update$0(context);
                return lambda$update$0;
            }
        });
        this.mUniqueNameMapping = ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.MobileNetworkSummaryStatus$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$update$1;
                lambda$update$1 = MobileNetworkSummaryStatus.this.lambda$update$1(context);
                return lambda$update$1;
            }
        });
        this.mIsPsimDisableSupported = ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.MobileNetworkSummaryStatus$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$update$2;
                lambda$update$2 = MobileNetworkSummaryStatus.this.lambda$update$2(context);
                return lambda$update$2;
            }
        });
        this.mSubscriptionList = getSubscriptions(context);
        if (consumer != null) {
            consumer.accept(this);
        }
        this.mDisableReEntranceUpdate = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$update$0(Context context) throws Exception {
        return Boolean.valueOf(isEuiccConfiguable(context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$update$2(Context context) throws Exception {
        return Boolean.valueOf(isPhysicalSimDisableSupported(context));
    }

    public List<SubscriptionAnnotation> getSubscriptionList() {
        return this.mSubscriptionList;
    }

    public CharSequence getDisplayName(int i) {
        Future<Map<Integer, CharSequence>> future = this.mUniqueNameMapping;
        if (future != null) {
            try {
                this.mUniqueNameMappingCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get display names", e);
            }
            this.mUniqueNameMapping = null;
        }
        Map<Integer, CharSequence> map = this.mUniqueNameMappingCache;
        if (map == null) {
            return null;
        }
        return map.get(Integer.valueOf(i));
    }

    public boolean isEuiccConfigSupport() {
        Future<Boolean> future = this.mIsEuiccConfiguable;
        if (future != null) {
            try {
                this.mIsEuiccConfiguableCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get euicc config status", e);
            }
            this.mIsEuiccConfiguable = null;
        }
        Boolean bool = this.mIsEuiccConfiguableCache;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isPhysicalSimDisableSupport() {
        Future<Boolean> future = this.mIsPsimDisableSupported;
        if (future != null) {
            try {
                this.mIsPsimDisableSupportedCache = future.get();
            } catch (Exception e) {
                Log.w("MobileNetworkSummaryStatus", "Fail to get pSIM disable support", e);
            }
            this.mIsPsimDisableSupported = null;
        }
        Boolean bool = this.mIsPsimDisableSupportedCache;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    private List<SubscriptionAnnotation> getSubscriptions(Context context) {
        try {
            return (List) new SelectableSubscriptions(context, true).addFinisher(new SubscriptionGrouping()).call().stream().filter(new MobileNetworkSummaryStatus$$ExternalSyntheticLambda3()).collect(Collectors.toList());
        } catch (OutOfMemoryError unused) {
            Log.w("MobileNetworkSummaryStatus", "Fail to get getSubscriptions");
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getUniqueNameForDisplay */
    public Map<Integer, CharSequence> lambda$update$1(Context context) {
        return SubscriptionUtil.getUniqueSubscriptionDisplayNames(context);
    }

    private boolean isPhysicalSimDisableSupported(Context context) {
        return SubscriptionUtil.showToggleForPhysicalSim((SubscriptionManager) context.getSystemService(SubscriptionManager.class));
    }

    private boolean isEuiccConfiguable(Context context) {
        return MobileNetworkUtils.showEuiccSettingsDetecting(context).booleanValue();
    }
}
