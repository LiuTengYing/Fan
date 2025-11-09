package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
/* loaded from: classes.dex */
public class WifiCallingContentObserver {
    private WifiCallingContentObserverClient mClient;
    private Context mContext;
    private ContentObserver mWfcStateObserver = null;

    /* loaded from: classes.dex */
    public interface WifiCallingContentObserverClient {
        void onWfcStateChanged();
    }

    public WifiCallingContentObserver(Context context, WifiCallingContentObserverClient wifiCallingContentObserverClient) {
        this.mContext = context;
        this.mClient = wifiCallingContentObserverClient;
    }

    public void startForSubid(int i) {
        if (this.mWfcStateObserver == null) {
            this.mWfcStateObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.network.telephony.WifiCallingContentObserver.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    WifiCallingContentObserver.this.onWfcStateChanged();
                }
            };
        }
        this.mContext.getContentResolver().registerContentObserver(MobileNetworkUtils.getNotifyContentUri(SubscriptionManager.WFC_ENABLED_CONTENT_URI, true, i), true, this.mWfcStateObserver);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("oma.wfc.enable" + i), true, this.mWfcStateObserver);
    }

    public void stop() {
        if (this.mWfcStateObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mWfcStateObserver);
            this.mWfcStateObserver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWfcStateChanged() {
        this.mClient.onWfcStateChanged();
    }
}
