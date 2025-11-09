package com.android.settings.flashlight;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class FlashlightSlice implements CustomSliceable {
    private final Context mContext;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    public FlashlightSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        if (isFlashlightAvailable(this.mContext)) {
            PendingIntent broadcastIntent = getBroadcastIntent(this.mContext);
            return new ListBuilder(this.mContext, CustomSliceRegistry.FLASHLIGHT_SLICE_URI, -1L).setAccentColor(Utils.getColorAccentDefaultColor(this.mContext)).addRow(new ListBuilder.RowBuilder().setTitle(this.mContext.getText(R$string.power_flashlight)).setTitleItem(IconCompat.createWithResource(this.mContext, R$drawable.ic_signal_flashlight), 0).setPrimaryAction(SliceAction.createToggle(broadcastIntent, null, isFlashlightEnabled(this.mContext)))).build();
        }
        return null;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.FLASHLIGHT_SLICE_URI;
    }

    @Override // com.android.settings.slices.Sliceable
    public IntentFilter getIntentFilter() {
        return new IntentFilter("com.android.settings.flashlight.action.FLASHLIGHT_CHANGED");
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        try {
            String cameraId = getCameraId(this.mContext);
            if (cameraId != null) {
                ((CameraManager) this.mContext.getSystemService(CameraManager.class)).setTorchMode(cameraId, intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", isFlashlightEnabled(this.mContext)));
            }
        } catch (CameraAccessException e) {
            Log.e("FlashlightSlice", "Camera couldn't set torch mode.", e);
        }
        this.mContext.getContentResolver().notifyChange(CustomSliceRegistry.FLASHLIGHT_SLICE_URI, null);
    }

    private static String getCameraId(Context context) throws CameraAccessException {
        String[] cameraIdList;
        CameraManager cameraManager = (CameraManager) context.getSystemService(CameraManager.class);
        for (String str : cameraManager.getCameraIdList()) {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
            Boolean bool = (Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (bool != null && bool.booleanValue() && num != null && num.intValue() == 1) {
                return str;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:11:0x001f  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isFlashlightAvailable(android.content.Context r5) {
        /*
            r0 = 0
            r1 = 1
            java.lang.String r2 = getCameraId(r5)     // Catch: android.hardware.camera2.CameraAccessException -> La
            if (r2 == 0) goto L12
            r2 = r1
            goto L13
        La:
            r2 = move-exception
            java.lang.String r3 = "FlashlightSlice"
            java.lang.String r4 = "Error getting camera id."
            android.util.Log.e(r3, r4, r2)
        L12:
            r2 = r0
        L13:
            android.content.ContentResolver r5 = r5.getContentResolver()
            java.lang.String r3 = "flashlight_available"
            int r5 = android.provider.Settings.Secure.getInt(r5, r3, r2)
            if (r5 != r1) goto L20
            r0 = r1
        L20:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.flashlight.FlashlightSlice.isFlashlightAvailable(android.content.Context):boolean");
    }

    private static boolean isFlashlightEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "flashlight_enabled", 0) == 1;
    }
}
