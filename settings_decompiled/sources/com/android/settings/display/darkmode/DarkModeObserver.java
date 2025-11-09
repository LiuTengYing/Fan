package com.android.settings.display.darkmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class DarkModeObserver {
    private Runnable mCallback;
    private Context mContext;
    private final BroadcastReceiver mBatterySaverReceiver = new BroadcastReceiver() { // from class: com.android.settings.display.darkmode.DarkModeObserver.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            DarkModeObserver.this.mCallback.run();
        }
    };
    private ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.darkmode.DarkModeObserver.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if ((uri == null ? null : uri.getLastPathSegment()) == null || DarkModeObserver.this.mCallback == null) {
                return;
            }
            DarkModeObserver.this.mCallback.run();
        }
    };

    public DarkModeObserver(Context context) {
        this.mContext = context;
    }

    public void subscribe(Runnable runnable) {
        runnable.run();
        this.mCallback = runnable;
        Uri uriFor = Settings.Secure.getUriFor("ui_night_mode");
        Uri uriFor2 = Settings.Secure.getUriFor("ui_night_mode_custom_type");
        Uri uriFor3 = Settings.Secure.getUriFor("dark_theme_custom_start_time");
        Uri uriFor4 = Settings.Secure.getUriFor("dark_theme_custom_end_time");
        this.mContext.getContentResolver().registerContentObserver(uriFor, false, this.mContentObserver);
        this.mContext.getContentResolver().registerContentObserver(uriFor2, false, this.mContentObserver);
        this.mContext.getContentResolver().registerContentObserver(uriFor3, false, this.mContentObserver);
        this.mContext.getContentResolver().registerContentObserver(uriFor4, false, this.mContentObserver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        this.mContext.registerReceiver(this.mBatterySaverReceiver, intentFilter);
    }

    public void unsubscribe() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        try {
            this.mContext.unregisterReceiver(this.mBatterySaverReceiver);
        } catch (IllegalArgumentException e) {
            Log.w("DarkModeObserver", e.getMessage());
        }
        this.mCallback = null;
    }

    @VisibleForTesting
    protected void setContentObserver(ContentObserver contentObserver) {
        this.mContentObserver = contentObserver;
    }

    @VisibleForTesting
    protected ContentObserver getContentObserver() {
        return this.mContentObserver;
    }
}
