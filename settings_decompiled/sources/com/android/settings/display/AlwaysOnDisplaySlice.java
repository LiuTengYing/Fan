package com.android.settings.display;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$string;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class AlwaysOnDisplaySlice implements CustomSliceable {
    private static final int MY_USER = UserHandle.myUserId();
    private final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private final AwareFeatureProvider mFeatureProvider;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public AlwaysOnDisplaySlice(Context context) {
        this.mContext = context;
        this.mConfig = new AmbientDisplayConfiguration(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        AmbientDisplayConfiguration ambientDisplayConfiguration = this.mConfig;
        int i = MY_USER;
        if (ambientDisplayConfiguration.alwaysOnAvailableForUser(i)) {
            PendingIntent broadcastIntent = getBroadcastIntent(this.mContext);
            return new ListBuilder(this.mContext, CustomSliceRegistry.ALWAYS_ON_SLICE_URI, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(new ListBuilder.RowBuilder().setTitle(this.mContext.getText(R$string.doze_always_on_title)).setSubtitle(this.mContext.getText(R$string.doze_always_on_summary)).setPrimaryAction(SliceAction.createToggle(broadcastIntent, null, this.mConfig.alwaysOnEnabled(i)))).build();
        }
        return null;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.ALWAYS_ON_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        int i = 0;
        boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean isSupported = this.mFeatureProvider.isSupported(this.mContext);
        boolean isEnabled = this.mFeatureProvider.isEnabled(this.mContext);
        Settings.Secure.putInt(contentResolver, "doze_always_on", booleanExtra ? 1 : 0);
        if (isEnabled && isSupported && booleanExtra) {
            i = 1;
        }
        Settings.Secure.putInt(contentResolver, "doze_wake_display_gesture", i);
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }
}
