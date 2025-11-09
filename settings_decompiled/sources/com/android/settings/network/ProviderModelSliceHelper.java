package com.android.settings.network;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.wifi.slice.WifiSliceItem;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ProviderModelSliceHelper {
    protected final Context mContext;
    private CustomSliceable mSliceable;
    private final SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;

    public ProviderModelSliceHelper(Context context, CustomSliceable customSliceable) {
        this.mContext = context;
        this.mSliceable = customSliceable;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    public boolean hasCarrier() {
        SubscriptionManager subscriptionManager;
        return (isAirplaneModeEnabled() || (subscriptionManager = this.mSubscriptionManager) == null || this.mTelephonyManager == null || subscriptionManager.getActiveSubscriptionIdList().length <= 0) ? false : true;
    }

    public boolean isMobileDataEnabled() {
        return this.mTelephonyManager.isDataEnabled();
    }

    public boolean isDataSimActive() {
        return MobileNetworkUtils.activeNetworkIsCellular(this.mContext);
    }

    public boolean isDataStateInService() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState == null ? null : serviceState.getNetworkRegistrationInfo(2, 1);
        if (networkRegistrationInfo == null) {
            return false;
        }
        return networkRegistrationInfo.isRegistered();
    }

    public boolean isVoiceStateInService() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        return serviceState != null && serviceState.getState() == 0;
    }

    public Drawable getDrawableWithSignalStrength() {
        SignalStrength signalStrength = this.mTelephonyManager.getSignalStrength();
        int level = signalStrength == null ? 0 : signalStrength.getLevel();
        int i = 5;
        if (this.mSubscriptionManager != null && shouldInflateSignalStrength(SubscriptionManager.getDefaultDataSubscriptionId())) {
            level++;
            i = 6;
        }
        return MobileNetworkUtils.getSignalStrengthIcon(this.mContext, level, i, 0, false);
    }

    public void updateTelephony() {
        if (this.mSubscriptionManager == null || SubscriptionManager.getDefaultDataSubscriptionId() == -1) {
            return;
        }
        this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder createListBuilder(Uri uri) {
        return new ListBuilder(this.mContext, uri, -1L).setAccentColor(-1).setKeywords(getKeywords());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WifiSliceItem getConnectedWifiItem(List<WifiSliceItem> list) {
        if (list == null) {
            return null;
        }
        Optional<WifiSliceItem> findFirst = list.stream().filter(new Predicate() { // from class: com.android.settings.network.ProviderModelSliceHelper$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getConnectedWifiItem$0;
                lambda$getConnectedWifiItem$0 = ProviderModelSliceHelper.lambda$getConnectedWifiItem$0((WifiSliceItem) obj);
                return lambda$getConnectedWifiItem$0;
            }
        }).findFirst();
        if (findFirst.isPresent()) {
            return findFirst.get();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getConnectedWifiItem$0(WifiSliceItem wifiSliceItem) {
        return wifiSliceItem.getConnectedState() == 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder.RowBuilder createCarrierRow(String str) {
        String mobileTitle = getMobileTitle();
        String mobileSummary = getMobileSummary(str);
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_signal_strength_zero_bar_no_internet);
        try {
            drawable = getMobileDrawable(drawable);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        IconCompat createIconWithDrawable = Utils.createIconWithDrawable(drawable);
        PendingIntent broadcastIntent = this.mSliceable.getBroadcastIntent(this.mContext);
        return new ListBuilder.RowBuilder().setTitle(mobileTitle).setTitleItem(createIconWithDrawable, 0).addEndItem(SliceAction.createToggle(broadcastIntent, "mobile_toggle", isMobileDataEnabled())).setPrimaryAction(SliceAction.create(broadcastIntent, createIconWithDrawable, 0, mobileTitle)).setSubtitle(Html.fromHtml(mobileSummary, 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAirplaneModeEnabled() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SubscriptionManager getSubscriptionManager() {
        return this.mSubscriptionManager;
    }

    private static void log(String str) {
        Log.d("ProviderModelSlice", str);
    }

    private boolean shouldInflateSignalStrength(int i) {
        return SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, i);
    }

    Drawable getMobileDrawable(Drawable drawable) throws Throwable {
        if (this.mTelephonyManager == null) {
            log("mTelephonyManager == null");
            return drawable;
        }
        if (isDataStateInService() || isVoiceStateInService()) {
            final Semaphore semaphore = new Semaphore(0);
            final AtomicReference atomicReference = new AtomicReference();
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.ProviderModelSliceHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ProviderModelSliceHelper.this.lambda$getMobileDrawable$1(atomicReference, semaphore);
                }
            });
            semaphore.acquire();
            drawable = (Drawable) atomicReference.get();
        }
        drawable.setTint(com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, 16843817));
        if (isDataSimActive()) {
            drawable.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
        }
        return drawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDrawable$1(AtomicReference atomicReference, Semaphore semaphore) {
        atomicReference.set(getDrawableWithSignalStrength());
        semaphore.release();
    }

    private String getMobileSummary(String str) {
        if (!isMobileDataEnabled()) {
            return this.mContext.getString(R$string.mobile_data_off_summary);
        }
        if (!isDataStateInService()) {
            return this.mContext.getString(R$string.mobile_data_no_connection);
        }
        if (isDataSimActive()) {
            Context context = this.mContext;
            return context.getString(R$string.preference_summary_default_combination, context.getString(R$string.mobile_data_connection_active), str);
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getMobileTitle() {
        SubscriptionInfo activeSubscriptionInfo;
        String charSequence = this.mContext.getText(R$string.mobile_data_settings_title).toString();
        SubscriptionManager subscriptionManager = this.mSubscriptionManager;
        return (subscriptionManager == null || (activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId())) == null) ? charSequence : SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo, this.mContext).toString();
    }

    private Set<String> getKeywords() {
        return (Set) Arrays.stream(TextUtils.split(this.mContext.getString(R$string.keywords_internet), ",")).map(new ProviderModelSliceHelper$$ExternalSyntheticLambda2()).collect(Collectors.toSet());
    }
}
