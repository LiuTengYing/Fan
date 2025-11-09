package com.android.settings;

import android.app.Activity;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.Objects;
/* loaded from: classes.dex */
public class FallbackHome extends Activity {
    private int mProgressTimeout;
    private boolean mProvisioned;
    private WallpaperManager mWallManager;
    private final Runnable mProgressTimeoutRunnable = new Runnable() { // from class: com.android.settings.FallbackHome$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            FallbackHome.this.lambda$new$0();
        }
    };
    private final WallpaperManager.OnColorsChangedListener mColorsChangedListener = new WallpaperManager.OnColorsChangedListener() { // from class: com.android.settings.FallbackHome.1
        @Override // android.app.WallpaperManager.OnColorsChangedListener
        public void onColorsChanged(WallpaperColors wallpaperColors, int i) {
            if (wallpaperColors != null) {
                View decorView = FallbackHome.this.getWindow().getDecorView();
                decorView.setSystemUiVisibility(FallbackHome.this.updateVisibilityFlagsFromColors(wallpaperColors, decorView.getSystemUiVisibility()));
                FallbackHome.this.mWallManager.removeOnColorsChangedListener(this);
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.FallbackHome.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            FallbackHome.this.maybeFinish();
        }
    };
    private Handler mHandler = new Handler() { // from class: com.android.settings.FallbackHome.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            FallbackHome.this.maybeFinish();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        getLayoutInflater().inflate(R$layout.fallback_home_finishing_boot, (ViewGroup) null);
        getWindow().addFlags(128);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        int integer = getResources().getInteger(17694910);
        this.mProgressTimeout = integer;
        if (integer <= 0) {
            this.mProgressTimeout = 0;
        }
        boolean z = Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) != 0;
        this.mProvisioned = z;
        if (z) {
            i = 1536;
        } else {
            setTheme(R$style.FallbackHome_SetupWizard);
            i = 4102;
        }
        WallpaperManager wallpaperManager = (WallpaperManager) getSystemService(WallpaperManager.class);
        this.mWallManager = wallpaperManager;
        if (wallpaperManager == null) {
            Log.w("FallbackHome", "Wallpaper manager isn't ready, can't listen to color changes!");
        } else {
            loadWallpaperColors(i);
        }
        getWindow().getDecorView().setSystemUiVisibility(i);
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        maybeFinish();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mProvisioned) {
            this.mHandler.postDelayed(this.mProgressTimeoutRunnable, this.mProgressTimeout);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this.mProgressTimeoutRunnable);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
        WallpaperManager wallpaperManager = this.mWallManager;
        if (wallpaperManager != null) {
            wallpaperManager.removeOnColorsChangedListener(this.mColorsChangedListener);
        }
    }

    private void loadWallpaperColors(final int i) {
        new AsyncTask<Object, Void, Integer>() { // from class: com.android.settings.FallbackHome.3
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public Integer doInBackground(Object... objArr) {
                WallpaperColors wallpaperColors = FallbackHome.this.mWallManager.getWallpaperColors(1);
                if (wallpaperColors == null) {
                    FallbackHome.this.mWallManager.addOnColorsChangedListener(FallbackHome.this.mColorsChangedListener, null);
                    return null;
                }
                return Integer.valueOf(FallbackHome.this.updateVisibilityFlagsFromColors(wallpaperColors, i));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Integer num) {
                if (num == null) {
                    return;
                }
                FallbackHome.this.getWindow().getDecorView().setSystemUiVisibility(num.intValue());
            }
        }.execute(new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeFinish() {
        if (((UserManager) getSystemService(UserManager.class)).isUserUnlocked()) {
            if (Objects.equals(getPackageName(), getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0).activityInfo.packageName)) {
                Log.d("FallbackHome", "User unlocked but no home; let's hope someone enables one soon?");
                this.mHandler.sendEmptyMessageDelayed(0, 500L);
                return;
            }
            Log.d("FallbackHome", "User unlocked and real home found; let's go!");
            ((PowerManager) getSystemService(PowerManager.class)).userActivity(SystemClock.uptimeMillis(), false);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int updateVisibilityFlagsFromColors(WallpaperColors wallpaperColors, int i) {
        return (wallpaperColors.getColorHints() & 1) != 0 ? i | 8192 | 16 : i & (-8193) & (-17);
    }
}
