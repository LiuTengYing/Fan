package com.android.settings.network;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.SubscriptionManager;
import android.util.EventLog;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkProviderWorker;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.slice.WifiSlice;
import com.android.settings.wifi.slice.WifiSliceItem;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.WifiUtils;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ProviderModelSlice extends WifiSlice {
    private final ProviderModelSliceHelper mHelper;
    private final SharedPreferences mSharedPref;

    @Override // com.android.settings.wifi.slice.WifiSlice
    protected boolean isApRowCollapsed() {
        return false;
    }

    public ProviderModelSlice(Context context) {
        super(context);
        this.mHelper = getHelper();
        this.mSharedPref = getSharedPreference();
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.PROVIDER_MODEL_SLICE_URI;
    }

    private static void log(String str) {
        Log.d("ProviderModelSlice", str);
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder createListBuilder = this.mHelper.createListBuilder(getUri());
        int i = 0;
        if (WifiSlice.isGuestUser(this.mContext)) {
            Log.e("ProviderModelSlice", "Guest user is not allowed to configure Internet!");
            EventLog.writeEvent(1397638484, "227470877", -1, "User is a guest");
            return createListBuilder.build();
        }
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            i = worker.getApRowCount();
        } else {
            log("network provider worker is null.");
        }
        if (getInternetType() == 4) {
            log("get Ethernet item which is connected");
            createListBuilder.addRow(createEthernetRow());
            i--;
        }
        if (!this.mHelper.isAirplaneModeEnabled()) {
            boolean hasCarrier = this.mHelper.hasCarrier();
            log("hasCarrier: " + hasCarrier);
            if (hasCarrier) {
                this.mHelper.updateTelephony();
                createListBuilder.addRow(this.mHelper.createCarrierRow(worker != null ? worker.getNetworkTypeDescription() : ""));
                i--;
            }
        }
        boolean isWifiEnabled = this.mWifiManager.isWifiEnabled();
        createListBuilder.addRow(createWifiToggleRow(this.mContext, isWifiEnabled));
        int i2 = i - 1;
        if (!isWifiEnabled) {
            log("Wi-Fi is disabled");
            return createListBuilder.build();
        }
        List<WifiSliceItem> results = worker != null ? worker.getResults() : null;
        if (results == null || results.size() <= 0) {
            log("Wi-Fi list is empty");
            return createListBuilder.build();
        }
        WifiSliceItem connectedWifiItem = this.mHelper.getConnectedWifiItem(results);
        if (connectedWifiItem != null) {
            log("get Wi-Fi item which is connected");
            createListBuilder.addRow(getWifiSliceItemRow(connectedWifiItem));
            i2--;
        }
        log("get Wi-Fi items which are not connected. Wi-Fi items : " + results.size());
        for (WifiSliceItem wifiSliceItem : (List) results.stream().filter(new Predicate() { // from class: com.android.settings.network.ProviderModelSlice$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSlice$0;
                lambda$getSlice$0 = ProviderModelSlice.lambda$getSlice$0((WifiSliceItem) obj);
                return lambda$getSlice$0;
            }
        }).limit((long) (i2 - 1)).collect(Collectors.toList())) {
            createListBuilder.addRow(getWifiSliceItemRow(wifiSliceItem));
        }
        log("add See-All");
        createListBuilder.addRow(getSeeAllRow());
        return createListBuilder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSlice$0(WifiSliceItem wifiSliceItem) {
        return wifiSliceItem.getConnectedState() != 2;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public PendingIntent getBroadcastIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(getUri().toString()).addFlags(268435456).setData(getUri()).setClass(context, SliceBroadcastReceiver.class), 167772160);
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        SharedPreferences sharedPreferences;
        if (this.mHelper.getSubscriptionManager() == null) {
            return;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        log("defaultSubId:" + defaultDataSubscriptionId);
        if (defaultSubscriptionIsUsable(defaultDataSubscriptionId)) {
            boolean hasExtra = intent.hasExtra("android.app.slice.extra.TOGGLE_STATE");
            boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", this.mHelper.isMobileDataEnabled());
            if (hasExtra) {
                if (!booleanExtra && (sharedPreferences = this.mSharedPref) != null && sharedPreferences.getBoolean("PrefHasTurnedOffMobileData", true)) {
                    String mobileTitle = this.mHelper.getMobileTitle();
                    if (mobileTitle.equals(this.mContext.getString(R$string.mobile_data_settings_title))) {
                        mobileTitle = this.mContext.getString(R$string.mobile_data_disable_message_default_carrier);
                    }
                    showMobileDataDisableDialog(getMobileDataDisableDialog(defaultDataSubscriptionId, mobileTitle));
                    return;
                }
                MobileNetworkUtils.setMobileDataEnabled(this.mContext, defaultDataSubscriptionId, booleanExtra, false);
            }
            if (!hasExtra) {
                booleanExtra = MobileNetworkUtils.isMobileDataEnabled(this.mContext);
            }
            doCarrierNetworkAction(hasExtra, booleanExtra, defaultDataSubscriptionId);
        }
    }

    AlertDialog getMobileDataDisableDialog(final int i, String str) {
        return new AlertDialog.Builder(this.mContext).setTitle(R$string.mobile_data_disable_title).setMessage(this.mContext.getString(R$string.mobile_data_disable_message, str)).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.ProviderModelSlice$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProviderModelSlice.this.lambda$getMobileDataDisableDialog$1(dialogInterface, i2);
            }
        }).setPositiveButton(17039659, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.ProviderModelSlice$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProviderModelSlice.this.lambda$getMobileDataDisableDialog$2(i, dialogInterface, i2);
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDataDisableDialog$1(DialogInterface dialogInterface, int i) {
        NetworkProviderWorker worker = getWorker();
        if (worker != null) {
            worker.updateSlice();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMobileDataDisableDialog$2(int i, DialogInterface dialogInterface, int i2) {
        MobileNetworkUtils.setMobileDataEnabled(this.mContext, i, false, false);
        doCarrierNetworkAction(true, false, i);
        SharedPreferences sharedPreferences = this.mSharedPref;
        if (sharedPreferences != null) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("PrefHasTurnedOffMobileData", false);
            edit.apply();
        }
    }

    private void showMobileDataDisableDialog(AlertDialog alertDialog) {
        if (alertDialog == null) {
            log("AlertDialog is null");
            return;
        }
        alertDialog.getWindow().setType(2009);
        alertDialog.show();
    }

    void doCarrierNetworkAction(boolean z, boolean z2, int i) {
        NetworkProviderWorker worker = getWorker();
        if (worker == null) {
            return;
        }
        if (z) {
            worker.setCarrierNetworkEnabledIfNeeded(z2, i);
        } else if (z2) {
            worker.connectCarrierNetwork();
        }
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, NetworkProviderSettings.class.getName(), "", this.mContext.getText(R$string.provider_internet_settings).toString(), 1401, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(getUri());
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        if (WifiSlice.isGuestUser(this.mContext)) {
            return null;
        }
        return NetworkProviderWorker.class;
    }

    ProviderModelSliceHelper getHelper() {
        return new ProviderModelSliceHelper(this.mContext, this);
    }

    NetworkProviderWorker getWorker() {
        return (NetworkProviderWorker) SliceBackgroundWorker.getInstance(getUri());
    }

    SharedPreferences getSharedPreference() {
        return this.mContext.getSharedPreferences("ProviderModelSlice", 0);
    }

    private int getInternetType() {
        NetworkProviderWorker worker = getWorker();
        if (worker == null) {
            return 1;
        }
        return worker.getInternetType();
    }

    ListBuilder.RowBuilder createEthernetRow() {
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_settings_ethernet);
        if (drawable != null) {
            drawable.setTintList(Utils.getColorAttr(this.mContext, 16843829));
            rowBuilder.setTitleItem(com.android.settings.Utils.createIconWithDrawable(drawable), 0);
        }
        return rowBuilder.setTitle(this.mContext.getText(R$string.ethernet)).setSubtitle(this.mContext.getText(R$string.to_switch_networks_disconnect_ethernet));
    }

    protected ListBuilder.RowBuilder createWifiToggleRow(Context context, boolean z) {
        Uri uri = CustomSliceRegistry.WIFI_SLICE_URI;
        return new ListBuilder.RowBuilder().setTitle(context.getString(R$string.wifi_settings)).setPrimaryAction(SliceAction.createToggle(PendingIntent.getBroadcast(context, 0, new Intent(uri.toString()).setData(uri).setClass(context, SliceBroadcastReceiver.class).putExtra("android.app.slice.extra.TOGGLE_STATE", !z).addFlags(268435456), 201326592), null, z));
    }

    protected ListBuilder.RowBuilder getSeeAllRow() {
        CharSequence text = this.mContext.getText(R$string.previous_connected_see_all);
        IconCompat seeAllIcon = getSeeAllIcon();
        return new ListBuilder.RowBuilder().setTitleItem(seeAllIcon, 0).setTitle(text).setPrimaryAction(getPrimaryAction(seeAllIcon, text));
    }

    protected IconCompat getSeeAllIcon() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_arrow_forward);
        if (drawable != null) {
            drawable.setTint(Utils.getColorAttrDefaultColor(this.mContext, 16843817));
            return com.android.settings.Utils.createIconWithDrawable(drawable);
        }
        return com.android.settings.Utils.createIconWithDrawable(new ColorDrawable(0));
    }

    protected SliceAction getPrimaryAction(IconCompat iconCompat, CharSequence charSequence) {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864), iconCompat, 0, charSequence);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiSlice
    public IconCompat getWifiSliceItemLevelIcon(WifiSliceItem wifiSliceItem) {
        if (wifiSliceItem.getConnectedState() == 2 && getInternetType() != 2) {
            int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
            Drawable drawable = this.mContext.getDrawable(WifiUtils.getInternetIconResource(wifiSliceItem.getLevel(), wifiSliceItem.shouldShowXLevelIcon()));
            drawable.setTint(colorAttrDefaultColor);
            return com.android.settings.Utils.createIconWithDrawable(drawable);
        }
        return super.getWifiSliceItemLevelIcon(wifiSliceItem);
    }

    protected boolean defaultSubscriptionIsUsable(int i) {
        return SubscriptionManager.isUsableSubscriptionId(i);
    }
}
