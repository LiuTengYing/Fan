package com.android.settings.slices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.settings.bluetooth.BluetoothSliceBuilder;
import com.android.settings.notification.zen.ZenModeSliceBuilder;
/* loaded from: classes.dex */
public class SliceDeepLinkSpringBoard extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Intent contentIntent;
        super.onCreate(bundle);
        Uri parse = parse(getIntent().getData());
        if (parse == null) {
            Log.e("DeeplinkSpringboard", "No data found");
            finish();
            return;
        }
        try {
            if (CustomSliceRegistry.isValidUri(parse)) {
                contentIntent = CustomSliceable.createInstance(getApplicationContext(), CustomSliceRegistry.getSliceClassByUri(parse)).getIntent();
            } else if (CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(parse)) {
                contentIntent = ZenModeSliceBuilder.getIntent(this);
            } else if (CustomSliceRegistry.BLUETOOTH_URI.equals(parse)) {
                contentIntent = BluetoothSliceBuilder.getIntent(this);
            } else {
                contentIntent = SliceBuilderUtils.getContentIntent(this, new SlicesDatabaseAccessor(this).getSliceDataFromUri(parse));
            }
            startActivity(contentIntent);
            finish();
        } catch (Exception e) {
            Log.w("DeeplinkSpringboard", "Couldn't launch Slice intent", e);
            startActivity(new Intent("android.settings.SETTINGS"));
            finish();
        }
    }

    private static Uri parse(Uri uri) {
        String queryParameter = uri.getQueryParameter("slice");
        if (TextUtils.isEmpty(queryParameter)) {
            EventLog.writeEvent(1397638484, "122836081", -1, "");
            return null;
        }
        return Uri.parse(queryParameter);
    }
}
