package com.android.settings.network.telephony;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.WirelessUtils;
import java.io.IOException;
import java.util.List;
/* loaded from: classes.dex */
public class MobileDataSlice implements CustomSliceable {
    private final Context mContext;
    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyManager mTelephonyManager;

    public MobileDataSlice(Context context) {
        this.mContext = context;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.ic_network_cell);
        String charSequence = this.mContext.getText(R$string.mobile_data_settings_title).toString();
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(this.mContext);
        if (!isAirplaneModeEnabled() && isMobileDataAvailable()) {
            CharSequence summary = getSummary();
            PendingIntent broadcastIntent = getBroadcastIntent(this.mContext);
            ListBuilder.RowBuilder primaryAction = new ListBuilder.RowBuilder().setTitle(charSequence).addEndItem(SliceAction.createToggle(broadcastIntent, null, isMobileDataEnabled())).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, charSequence));
            if (!com.android.settings.Utils.isSettingsIntelligence(this.mContext)) {
                primaryAction.setSubtitle(summary);
            }
            return new ListBuilder(this.mContext, getUri(), -1L).setAccentColor(colorAccentDefaultColor).addRow(primaryAction).build();
        }
        return null;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.MOBILE_DATA_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", isMobileDataEnabled());
        int defaultSubscriptionId = getDefaultSubscriptionId(this.mSubscriptionManager);
        if (defaultSubscriptionId == -1) {
            return;
        }
        MobileNetworkUtils.setMobileDataEnabled(this.mContext, defaultSubscriptionId, booleanExtra, false);
    }

    @Override // com.android.settings.slices.Sliceable
    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        return intentFilter;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return new Intent("android.settings.NETWORK_OPERATOR_SETTINGS").setPackage("com.android.settings");
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_network;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return MobileDataWorker.class;
    }

    protected static int getDefaultSubscriptionId(SubscriptionManager subscriptionManager) {
        SubscriptionInfo activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
        if (activeSubscriptionInfo == null) {
            return -1;
        }
        return activeSubscriptionInfo.getSubscriptionId();
    }

    private CharSequence getSummary() {
        SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
        if (activeSubscriptionInfo == null) {
            return null;
        }
        return SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo, this.mContext);
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864);
    }

    private boolean isMobileDataAvailable() {
        List<SubscriptionInfo> selectableSubscriptionInfoList = SubscriptionUtil.getSelectableSubscriptionInfoList(this.mContext);
        return (selectableSubscriptionInfoList == null || selectableSubscriptionInfoList.isEmpty()) ? false : true;
    }

    boolean isAirplaneModeEnabled() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }

    boolean isMobileDataEnabled() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            return false;
        }
        return telephonyManager.isDataEnabled();
    }

    /* loaded from: classes.dex */
    public static class MobileDataWorker extends SliceBackgroundWorker<Void> {
        DataContentObserver mMobileDataObserver;

        public MobileDataWorker(Context context, Uri uri) {
            super(context, uri);
            this.mMobileDataObserver = new DataContentObserver(new Handler(Looper.getMainLooper()), this);
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSlicePinned() {
            this.mMobileDataObserver.register(getContext(), MobileDataSlice.getDefaultSubscriptionId((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)));
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSliceUnpinned() {
            this.mMobileDataObserver.unRegister(getContext());
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.mMobileDataObserver = null;
        }

        public void updateSlice() {
            notifySliceChange();
        }

        /* loaded from: classes.dex */
        public class DataContentObserver extends ContentObserver {
            private final MobileDataWorker mSliceBackgroundWorker;

            public DataContentObserver(Handler handler, MobileDataWorker mobileDataWorker) {
                super(handler);
                this.mSliceBackgroundWorker = mobileDataWorker;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                this.mSliceBackgroundWorker.updateSlice();
            }

            public void register(Context context, int i) {
                context.getContentResolver().registerContentObserver(MobileDataContentObserver.getObservableUri(context, i), false, this);
            }

            public void unRegister(Context context) {
                context.getContentResolver().unregisterContentObserver(this);
            }
        }
    }
}
