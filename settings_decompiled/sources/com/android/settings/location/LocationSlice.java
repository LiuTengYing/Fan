package com.android.settings.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class LocationSlice implements CustomSliceable {
    private final Context mContext;

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
    }

    public LocationSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, 17302863);
        CharSequence text = this.mContext.getText(R$string.location_settings_title);
        return new ListBuilder(this.mContext, CustomSliceRegistry.LOCATION_SLICE_URI, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(new ListBuilder.RowBuilder().setTitle(text).setTitleItem(createWithResource, 0).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, text))).build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.LOCATION_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(R$string.location_settings_title).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, LocationSettings.class.getName(), "location", charSequence, 63, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("location").build());
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_location;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864);
    }
}
