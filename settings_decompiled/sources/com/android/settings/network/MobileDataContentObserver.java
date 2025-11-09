package com.android.settings.network;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
/* loaded from: classes.dex */
public class MobileDataContentObserver extends ContentObserver {
    private OnMobileDataChangedListener mListener;

    /* loaded from: classes.dex */
    public interface OnMobileDataChangedListener {
        void onMobileDataChanged();
    }

    public MobileDataContentObserver(Handler handler) {
        super(handler);
    }

    public static Uri getObservableUri(Context context, int i) {
        Uri uriFor = Settings.Global.getUriFor("mobile_data");
        if (((TelephonyManager) context.getSystemService(TelephonyManager.class)).getActiveModemCount() != 1) {
            return Settings.Global.getUriFor("mobile_data" + i);
        }
        return uriFor;
    }

    public void setOnMobileDataChangedListener(OnMobileDataChangedListener onMobileDataChangedListener) {
        this.mListener = onMobileDataChangedListener;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        super.onChange(z);
        Log.d("fangli", "onChange: " + z);
        OnMobileDataChangedListener onMobileDataChangedListener = this.mListener;
        if (onMobileDataChangedListener != null) {
            onMobileDataChangedListener.onMobileDataChanged();
        }
    }

    public void register(Context context, int i) {
        context.getContentResolver().registerContentObserver(getObservableUri(context, i), false, this);
    }

    public void unRegister(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
    }
}
