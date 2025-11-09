package com.android.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.R$string;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class WirelessDebuggingEnabler implements SwitchWidgetController.OnSwitchChangeListener, LifecycleObserver, OnResume, OnPause {
    private final ContentResolver mContentResolver;
    private Context mContext;
    private final Handler mHandler;
    private OnEnabledListener mListener;
    private boolean mListeningToOnSwitchChange = false;
    private final ContentObserver mSettingsObserver;
    private final SwitchWidgetController mSwitchWidget;

    /* loaded from: classes.dex */
    public interface OnEnabledListener {
        void onEnabled(boolean z);
    }

    public WirelessDebuggingEnabler(Context context, SwitchWidgetController switchWidgetController, OnEnabledListener onEnabledListener, Lifecycle lifecycle) {
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mContext = context;
        this.mSwitchWidget = switchWidgetController;
        switchWidgetController.setListener(this);
        switchWidgetController.setupView();
        this.mListener = onEnabledListener;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mContentResolver = context.getContentResolver();
        this.mSettingsObserver = new ContentObserver(handler) { // from class: com.android.settings.development.WirelessDebuggingEnabler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                Log.i("WirelessDebuggingEnabler", "ADB_WIFI_ENABLED=" + WirelessDebuggingEnabler.this.isAdbWifiEnabled());
                WirelessDebuggingEnabler wirelessDebuggingEnabler = WirelessDebuggingEnabler.this;
                wirelessDebuggingEnabler.onWirelessDebuggingEnabled(wirelessDebuggingEnabler.isAdbWifiEnabled());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAdbWifiEnabled() {
        return Settings.Global.getInt(this.mContentResolver, "adb_wifi_enabled", 0) != 0;
    }

    public void teardownSwitchController() {
        if (this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.stopListening();
            this.mListeningToOnSwitchChange = false;
        }
        this.mSwitchWidget.teardownView();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (!this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.startListening();
            this.mListeningToOnSwitchChange = true;
        }
        onWirelessDebuggingEnabled(isAdbWifiEnabled());
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, this.mSettingsObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.stopListening();
            this.mListeningToOnSwitchChange = false;
        }
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWirelessDebuggingEnabled(boolean z) {
        this.mSwitchWidget.setChecked(z);
        OnEnabledListener onEnabledListener = this.mListener;
        if (onEnabledListener != null) {
            onEnabledListener.onEnabled(z);
        }
    }

    protected void writeAdbWifiSetting(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "adb_wifi_enabled", z ? 1 : 0);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (z && !WirelessDebuggingPreferenceController.isWifiConnected(this.mContext)) {
            Toast.makeText(this.mContext, R$string.adb_wireless_no_network_msg, 1).show();
            this.mSwitchWidget.setChecked(false);
            return false;
        }
        writeAdbWifiSetting(z);
        return true;
    }
}
