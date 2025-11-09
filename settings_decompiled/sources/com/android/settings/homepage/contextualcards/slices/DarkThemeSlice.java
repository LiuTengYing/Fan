package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class DarkThemeSlice implements CustomSliceable {
    static boolean sKeepSliceShow;
    private final Context mContext;
    private final PowerManager mPowerManager;
    private final UiModeManager mUiModeManager;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static long sActiveUiSession = -1000;
    static boolean sSliceClicked = false;
    static boolean sPreChecked = false;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public DarkThemeSlice(Context context) {
        this.mContext = context;
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        long uiSessionToken = FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().getUiSessionToken();
        if (uiSessionToken != sActiveUiSession) {
            sActiveUiSession = uiSessionToken;
            sKeepSliceShow = false;
        }
        if (DEBUG) {
            Log.d("DarkThemeSlice", "sKeepSliceShow = " + sKeepSliceShow + ", sSliceClicked = " + sSliceClicked + ", isAvailable = " + isAvailable(this.mContext));
        }
        if (this.mPowerManager.isPowerSaveMode() || ((!sKeepSliceShow || !sSliceClicked) && !isAvailable(this.mContext))) {
            return new ListBuilder(this.mContext, CustomSliceRegistry.DARK_THEME_SLICE_URI, -1L).setIsError(true).build();
        }
        sKeepSliceShow = true;
        PendingIntent broadcastIntent = getBroadcastIntent(this.mContext);
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(this.mContext);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.dark_theme);
        boolean isNightMode = com.android.settings.Utils.isNightMode(this.mContext);
        if (sPreChecked != isNightMode) {
            resetValue(isNightMode, false);
        }
        return new ListBuilder(this.mContext, CustomSliceRegistry.DARK_THEME_SLICE_URI, -1L).setAccentColor(colorAccentDefaultColor).addRow(new ListBuilder.RowBuilder().setTitle(this.mContext.getText(R$string.dark_theme_slice_title)).setTitleItem(createWithResource, 0).setSubtitle(this.mContext.getText(R$string.dark_theme_slice_subtitle)).setPrimaryAction(SliceAction.createToggle(broadcastIntent, null, isNightMode))).build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.DARK_THEME_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        final boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false);
        if (booleanExtra) {
            resetValue(booleanExtra, true);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.android.settings.homepage.contextualcards.slices.DarkThemeSlice$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DarkThemeSlice.this.lambda$onNotifyChange$0(booleanExtra);
            }
        }, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onNotifyChange$0(boolean z) {
        this.mUiModeManager.setNightModeActivated(z);
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return DarkThemeWorker.class;
    }

    boolean isAvailable(Context context) {
        if (com.android.settings.Utils.isNightMode(context) || isNightModeScheduled()) {
            return false;
        }
        int intProperty = ((BatteryManager) context.getSystemService(BatteryManager.class)).getIntProperty(4);
        Log.d("DarkThemeSlice", "battery level = " + intProperty);
        return intProperty <= 50;
    }

    private void resetValue(boolean z, boolean z2) {
        sPreChecked = z;
        sSliceClicked = z2;
    }

    private boolean isNightModeScheduled() {
        int nightMode = this.mUiModeManager.getNightMode();
        if (DEBUG) {
            Log.d("DarkThemeSlice", "night mode = " + nightMode);
        }
        return nightMode == 0 || nightMode == 3;
    }

    /* loaded from: classes.dex */
    public static class DarkThemeWorker extends SliceBackgroundWorker<Void> {
        private final ContentObserver mContentObserver;
        private final Context mContext;

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }

        public DarkThemeWorker(Context context, Uri uri) {
            super(context, uri);
            this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.homepage.contextualcards.slices.DarkThemeSlice.DarkThemeWorker.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    if (((PowerManager) DarkThemeWorker.this.mContext.getSystemService(PowerManager.class)).isPowerSaveMode()) {
                        DarkThemeWorker.this.notifySliceChange();
                    }
                }
            };
            this.mContext = context;
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSlicePinned() {
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power"), false, this.mContentObserver);
        }

        @Override // com.android.settings.slices.SliceBackgroundWorker
        protected void onSliceUnpinned() {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
    }
}
