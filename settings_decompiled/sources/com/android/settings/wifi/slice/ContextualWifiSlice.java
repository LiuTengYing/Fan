package com.android.settings.wifi.slice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settingslib.Utils;
/* loaded from: classes2.dex */
public class ContextualWifiSlice extends WifiSlice {
    static final int COLLAPSED_ROW_COUNT = 0;
    static long sActiveUiSession = -1000;
    static boolean sApRowCollapsed;
    private final ConnectivityManager mConnectivityManager;

    public ContextualWifiSlice(Context context) {
        super(context);
        this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.CONTEXTUAL_WIFI_SLICE_URI;
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        long uiSessionToken = FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().getUiSessionToken();
        if (uiSessionToken != sActiveUiSession) {
            sActiveUiSession = uiSessionToken;
            sApRowCollapsed = hasWorkingNetwork();
        } else if (!this.mWifiManager.isWifiEnabled()) {
            sApRowCollapsed = false;
        }
        return super.getSlice();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getApRowCount() {
        return sApRowCollapsed ? 0 : 3;
    }

    @Override // com.android.settings.wifi.slice.WifiSlice
    protected boolean isApRowCollapsed() {
        return sApRowCollapsed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiSlice
    public ListBuilder.RowBuilder getHeaderRow(boolean z, WifiSliceItem wifiSliceItem) {
        ListBuilder.RowBuilder headerRow = super.getHeaderRow(z, wifiSliceItem);
        headerRow.setTitleItem(getHeaderIcon(z, wifiSliceItem), 0);
        if (sApRowCollapsed) {
            headerRow.setSubtitle(getHeaderSubtitle(wifiSliceItem));
        }
        return headerRow;
    }

    private IconCompat getHeaderIcon(boolean z, WifiSliceItem wifiSliceItem) {
        Drawable drawable;
        int colorAttrDefaultColor;
        if (!z) {
            drawable = this.mContext.getDrawable(R$drawable.ic_wifi_off);
            Context context = this.mContext;
            colorAttrDefaultColor = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843817));
        } else {
            drawable = this.mContext.getDrawable(Utils.getWifiIconResource(2));
            if (wifiSliceItem != null && wifiSliceItem.getConnectedState() == 2) {
                colorAttrDefaultColor = Utils.getColorAccentDefaultColor(this.mContext);
            } else {
                colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
            }
        }
        drawable.setTint(colorAttrDefaultColor);
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    private CharSequence getHeaderSubtitle(WifiSliceItem wifiSliceItem) {
        if (wifiSliceItem == null || wifiSliceItem.getConnectedState() == 0) {
            return this.mContext.getText(R$string.disconnected);
        }
        return wifiSliceItem.getConnectedState() == 1 ? this.mContext.getString(R$string.wifi_connecting_to_message, wifiSliceItem.getTitle()) : this.mContext.getString(R$string.wifi_connected_to_message, wifiSliceItem.getTitle());
    }

    private boolean hasWorkingNetwork() {
        return !TextUtils.equals(getActiveSSID(), "<unknown ssid>") && hasInternetAccess();
    }

    private String getActiveSSID() {
        return this.mWifiManager.getWifiState() != 3 ? "<unknown ssid>" : WifiInfo.sanitizeSsid(this.mWifiManager.getConnectionInfo().getSSID());
    }

    private boolean hasInternetAccess() {
        NetworkCapabilities networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
        return (networkCapabilities == null || networkCapabilities.hasCapability(17) || networkCapabilities.hasCapability(24) || !networkCapabilities.hasCapability(16)) ? false : true;
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return ContextualWifiScanWorker.class;
    }
}
