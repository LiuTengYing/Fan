package com.android.settings.slices;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settingslib.SliceBroadcastRelay;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public class VolumeSliceHelper {
    static IntentFilter sIntentFilter;
    static Map<Uri, Integer> sRegisteredUri = new ArrayMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerIntentToUri(Context context, IntentFilter intentFilter, Uri uri, int i) {
        Log.d("VolumeSliceHelper", "Registering uri for broadcast relay: " + uri);
        synchronized (sRegisteredUri) {
            if (sRegisteredUri.isEmpty()) {
                SliceBroadcastRelay.registerReceiver(context, CustomSliceRegistry.VOLUME_SLICES_URI, VolumeSliceRelayReceiver.class, intentFilter);
                sIntentFilter = intentFilter;
            }
            sRegisteredUri.put(uri, Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean unregisterUri(Context context, Uri uri) {
        if (sRegisteredUri.containsKey(uri)) {
            Log.d("VolumeSliceHelper", "Unregistering uri broadcast relay: " + uri);
            synchronized (sRegisteredUri) {
                sRegisteredUri.remove(uri);
                if (sRegisteredUri.isEmpty()) {
                    sIntentFilter = null;
                    SliceBroadcastRelay.unregisterReceivers(context, CustomSliceRegistry.VOLUME_SLICES_URI);
                }
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void onReceive(Context context, Intent intent) {
        String stringExtra;
        String action = intent.getAction();
        IntentFilter intentFilter = sIntentFilter;
        if (intentFilter == null || action == null || !intentFilter.hasAction(action) || (stringExtra = intent.getStringExtra("uri")) == null) {
            return;
        }
        if (!CustomSliceRegistry.VOLUME_SLICES_URI.equals(ContentProvider.getUriWithoutUserId(Uri.parse(stringExtra)))) {
            Log.w("VolumeSliceHelper", "Invalid uri: " + stringExtra);
        } else if ("android.media.VOLUME_CHANGED_ACTION".equals(action)) {
            handleVolumeChanged(context, intent);
        } else if ("android.media.STREAM_MUTE_CHANGED_ACTION".equals(action) || "android.media.STREAM_DEVICES_CHANGED_ACTION".equals(action)) {
            handleStreamChanged(context, intent);
        } else {
            notifyAllStreamsChanged(context);
        }
    }

    private static void handleVolumeChanged(Context context, Intent intent) {
        if (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1) != intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1)) {
            handleStreamChanged(context, intent);
        }
    }

    private static void handleStreamChanged(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
        synchronized (sRegisteredUri) {
            Iterator<Map.Entry<Uri, Integer>> it = sRegisteredUri.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<Uri, Integer> next = it.next();
                if (next.getValue().intValue() == intExtra) {
                    context.getContentResolver().notifyChange(next.getKey(), null);
                    break;
                }
            }
        }
    }

    private static void notifyAllStreamsChanged(final Context context) {
        synchronized (sRegisteredUri) {
            sRegisteredUri.forEach(new BiConsumer() { // from class: com.android.settings.slices.VolumeSliceHelper$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    VolumeSliceHelper.lambda$notifyAllStreamsChanged$0(context, (Uri) obj, (Integer) obj2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$notifyAllStreamsChanged$0(Context context, Uri uri, Integer num) {
        context.getContentResolver().notifyChange(uri, null);
    }
}
