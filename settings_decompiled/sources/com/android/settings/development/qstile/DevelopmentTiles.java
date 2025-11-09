package com.android.settings.development.qstile;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.SensorPrivacyManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.sysprop.DisplayProperties;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import android.widget.Toast;
import com.android.internal.app.LocalePicker;
import com.android.internal.inputmethod.ImeTracing;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settings.R$string;
import com.android.settings.development.WirelessDebuggingPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;
/* loaded from: classes.dex */
public abstract class DevelopmentTiles extends TileService {
    protected abstract boolean isEnabled();

    protected abstract void setIsEnabled(boolean z);

    @Override // android.service.quicksettings.TileService
    public void onStartListening() {
        super.onStartListening();
        refresh();
    }

    public void refresh() {
        int i = 0;
        if (!DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this)) {
            if (isEnabled()) {
                setIsEnabled(false);
                SystemPropPoker.getInstance().poke();
            }
            ComponentName componentName = new ComponentName(getPackageName(), getClass().getName());
            try {
                getPackageManager().setComponentEnabledSetting(componentName, 2, 1);
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    asInterface.remTile(componentName);
                }
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Failed to modify QS tile for component " + componentName.toString(), e);
            }
        } else {
            i = isEnabled() ? 2 : 1;
        }
        getQsTile().setState(i);
        getQsTile().updateTile();
    }

    @Override // android.service.quicksettings.TileService
    public void onClick() {
        setIsEnabled(getQsTile().getState() == 1);
        SystemPropPoker.getInstance().poke();
        refresh();
    }

    /* loaded from: classes.dex */
    public static class ShowLayout extends DevelopmentTiles {
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return ((Boolean) DisplayProperties.debug_layout().orElse(Boolean.FALSE)).booleanValue();
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            DisplayProperties.debug_layout(Boolean.valueOf(z));
        }
    }

    /* loaded from: classes.dex */
    public static class GPUProfiling extends DevelopmentTiles {
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return SystemProperties.get("debug.hwui.profile").equals("visual_bars");
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            SystemProperties.set("debug.hwui.profile", z ? "visual_bars" : "");
        }
    }

    /* loaded from: classes.dex */
    public static class ForceRTL extends DevelopmentTiles {
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return Settings.Global.getInt(getContentResolver(), "debug.force_rtl", 0) != 0;
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            Settings.Global.putInt(getContentResolver(), "debug.force_rtl", z ? 1 : 0);
            DisplayProperties.debug_force_rtl(Boolean.valueOf(z));
            LocalePicker.updateLocales(getResources().getConfiguration().getLocales());
        }
    }

    /* loaded from: classes.dex */
    public static class AnimationSpeed extends DevelopmentTiles {
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            try {
                return WindowManagerGlobal.getWindowManagerService().getAnimationScale(0) != 1.0f;
            } catch (RemoteException unused) {
                return false;
            }
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
            float f = z ? 10.0f : 1.0f;
            try {
                windowManagerService.setAnimationScale(0, f);
                windowManagerService.setAnimationScale(1, f);
                windowManagerService.setAnimationScale(2, f);
            } catch (RemoteException unused) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static class WinscopeTrace extends DevelopmentTiles {
        static final int SURFACE_FLINGER_LAYER_TRACE_CONTROL_CODE = 1025;
        static final int SURFACE_FLINGER_LAYER_TRACE_STATUS_CODE = 1026;
        private ImeTracing mImeTracing;
        private IBinder mSurfaceFlinger;
        private Toast mToast;
        private IWindowManager mWindowManager;

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
            this.mSurfaceFlinger = ServiceManager.getService("SurfaceFlinger");
            this.mImeTracing = ImeTracing.getInstance();
            this.mToast = Toast.makeText(getApplicationContext(), "Trace files written to /data/misc/wmtrace", 1);
        }

        private boolean isWindowTraceEnabled() {
            try {
                return this.mWindowManager.isWindowTraceEnabled();
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not get window trace status, defaulting to false." + e.toString());
                return false;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x005e  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean isLayerTraceEnabled() {
            /*
                r7 = this;
                r0 = 0
                r1 = 0
                android.os.IBinder r2 = r7.mSurfaceFlinger     // Catch: java.lang.Throwable -> L30 android.os.RemoteException -> L33
                if (r2 == 0) goto L26
                android.os.Parcel r2 = android.os.Parcel.obtain()     // Catch: java.lang.Throwable -> L30 android.os.RemoteException -> L33
                android.os.Parcel r0 = android.os.Parcel.obtain()     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L21
                java.lang.String r3 = "android.ui.ISurfaceComposer"
                r0.writeInterfaceToken(r3)     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L21
                android.os.IBinder r7 = r7.mSurfaceFlinger     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L21
                r3 = 1026(0x402, float:1.438E-42)
                r7.transact(r3, r0, r2, r1)     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L21
                boolean r1 = r2.readBoolean()     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L21
                goto L27
            L1f:
                r7 = move-exception
                goto L5c
            L21:
                r7 = move-exception
                r6 = r2
                r2 = r0
                r0 = r6
                goto L35
            L26:
                r2 = r0
            L27:
                if (r0 == 0) goto L57
                r0.recycle()
                r2.recycle()
                goto L57
            L30:
                r7 = move-exception
                r2 = r0
                goto L5c
            L33:
                r7 = move-exception
                r2 = r0
            L35:
                java.lang.String r3 = "DevelopmentTiles"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L58
                r4.<init>()     // Catch: java.lang.Throwable -> L58
                java.lang.String r5 = "Could not get layer trace status, defaulting to false."
                r4.append(r5)     // Catch: java.lang.Throwable -> L58
                java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L58
                r4.append(r7)     // Catch: java.lang.Throwable -> L58
                java.lang.String r7 = r4.toString()     // Catch: java.lang.Throwable -> L58
                android.util.Log.e(r3, r7)     // Catch: java.lang.Throwable -> L58
                if (r2 == 0) goto L57
                r2.recycle()
                r0.recycle()
            L57:
                return r1
            L58:
                r7 = move-exception
                r6 = r2
                r2 = r0
                r0 = r6
            L5c:
                if (r0 == 0) goto L64
                r0.recycle()
                r2.recycle()
            L64:
                throw r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.qstile.DevelopmentTiles.WinscopeTrace.isLayerTraceEnabled():boolean");
        }

        private boolean isSystemUiTracingEnabled() {
            try {
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    return asInterface.isTracing();
                }
                return false;
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not get system ui tracing status." + e.toString());
                return false;
            }
        }

        private boolean isImeTraceEnabled() {
            return this.mImeTracing.isEnabled();
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return isWindowTraceEnabled() || isLayerTraceEnabled() || isSystemUiTracingEnabled() || isImeTraceEnabled();
        }

        private void setWindowTraceEnabled(boolean z) {
            try {
                if (z) {
                    this.mWindowManager.startWindowTrace();
                } else {
                    this.mWindowManager.stopWindowTrace();
                }
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not set window trace status." + e.toString());
            }
        }

        private void setLayerTraceEnabled(boolean z) {
            Parcel parcel = null;
            try {
                try {
                    if (this.mSurfaceFlinger != null) {
                        Parcel obtain = Parcel.obtain();
                        try {
                            obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                            obtain.writeInt(z ? 1 : 0);
                            this.mSurfaceFlinger.transact(SURFACE_FLINGER_LAYER_TRACE_CONTROL_CODE, obtain, null, 0);
                            parcel = obtain;
                        } catch (RemoteException e) {
                            e = e;
                            parcel = obtain;
                            Log.e("DevelopmentTiles", "Could not set layer tracing." + e.toString());
                            if (parcel == null) {
                                return;
                            }
                            parcel.recycle();
                        } catch (Throwable th) {
                            th = th;
                            parcel = obtain;
                            if (parcel != null) {
                                parcel.recycle();
                            }
                            throw th;
                        }
                    }
                    if (parcel == null) {
                        return;
                    }
                } catch (RemoteException e2) {
                    e = e2;
                }
                parcel.recycle();
            } catch (Throwable th2) {
                th = th2;
            }
        }

        private void setSystemUiTracing(boolean z) {
            try {
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    if (z) {
                        asInterface.startTracing();
                    } else {
                        asInterface.stopTracing();
                    }
                }
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not set system ui tracing." + e.toString());
            }
        }

        private void setImeTraceEnabled(boolean z) {
            if (z) {
                this.mImeTracing.startImeTrace();
            } else {
                this.mImeTracing.stopImeTrace();
            }
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            setWindowTraceEnabled(z);
            setLayerTraceEnabled(z);
            setSystemUiTracing(z);
            setImeTraceEnabled(z);
            if (z) {
                return;
            }
            this.mToast.show();
        }
    }

    /* loaded from: classes.dex */
    public static class SensorsOff extends DevelopmentTiles {
        private Context mContext;
        private boolean mIsEnabled;
        private KeyguardManager mKeyguardManager;
        private MetricsFeatureProvider mMetricsFeatureProvider;
        private SensorPrivacyManager mSensorPrivacyManager;

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            Context applicationContext = getApplicationContext();
            this.mContext = applicationContext;
            SensorPrivacyManager sensorPrivacyManager = (SensorPrivacyManager) applicationContext.getSystemService("sensor_privacy");
            this.mSensorPrivacyManager = sensorPrivacyManager;
            this.mIsEnabled = sensorPrivacyManager.isAllSensorPrivacyEnabled();
            this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
            this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return this.mIsEnabled;
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            if (this.mIsEnabled && this.mKeyguardManager.isKeyguardLocked()) {
                return;
            }
            this.mMetricsFeatureProvider.action(getApplicationContext(), 1598, z);
            this.mIsEnabled = z;
            this.mSensorPrivacyManager.setAllSensorPrivacy(z);
        }
    }

    /* loaded from: classes.dex */
    public static class WirelessDebugging extends DevelopmentTiles {
        private Context mContext;
        private final Handler mHandler;
        private KeyguardManager mKeyguardManager;
        private final ContentObserver mSettingsObserver;
        private Toast mToast;

        public WirelessDebugging() {
            Handler handler = new Handler(Looper.getMainLooper());
            this.mHandler = handler;
            this.mSettingsObserver = new ContentObserver(handler) { // from class: com.android.settings.development.qstile.DevelopmentTiles.WirelessDebugging.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z, Uri uri) {
                    WirelessDebugging.this.refresh();
                }
            };
        }

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            Context applicationContext = getApplicationContext();
            this.mContext = applicationContext;
            this.mKeyguardManager = (KeyguardManager) applicationContext.getSystemService("keyguard");
            this.mToast = Toast.makeText(this.mContext, R$string.adb_wireless_no_network_msg, 1);
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles, android.service.quicksettings.TileService
        public void onStartListening() {
            super.onStartListening();
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, this.mSettingsObserver);
        }

        @Override // android.service.quicksettings.TileService
        public void onStopListening() {
            super.onStopListening();
            getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return isAdbWifiEnabled();
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            if (z && this.mKeyguardManager.isKeyguardLocked()) {
                return;
            }
            if (z && !WirelessDebuggingPreferenceController.isWifiConnected(this.mContext)) {
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                this.mToast.show();
                return;
            }
            writeAdbWifiSetting(z);
        }

        private boolean isAdbWifiEnabled() {
            return Settings.Global.getInt(getContentResolver(), "adb_wifi_enabled", 0) != 0;
        }

        protected void writeAdbWifiSetting(boolean z) {
            Settings.Global.putInt(getContentResolver(), "adb_wifi_enabled", z ? 1 : 0);
        }
    }

    /* loaded from: classes.dex */
    public static class ShowTaps extends DevelopmentTiles {
        private Context mContext;

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            this.mContext = getApplicationContext();
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected boolean isEnabled() {
            return Settings.System.getInt(this.mContext.getContentResolver(), "show_touches", 0) == 1;
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        protected void setIsEnabled(boolean z) {
            Settings.System.putInt(this.mContext.getContentResolver(), "show_touches", z ? 1 : 0);
        }
    }
}
