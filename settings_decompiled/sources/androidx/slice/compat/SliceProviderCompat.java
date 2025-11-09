package androidx.slice.compat;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
/* loaded from: classes.dex */
public class SliceProviderCompat {
    public static void grantSlicePermission(Context context, String str, String str2, Uri uri) {
        try {
            ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
            Bundle bundle = new Bundle();
            bundle.putParcelable("slice_uri", uri);
            bundle.putString("provider_pkg", str);
            bundle.putString("pkg", str2);
            acquireClient.mProvider.call("grant_perms", "supports_versioned_parcelable", bundle);
            acquireClient.close();
        } catch (RemoteException e) {
            Log.e("SliceProviderCompat", "Unable to get slice descendants", e);
        }
    }

    private static ProviderHolder acquireClient(ContentResolver contentResolver, Uri uri) {
        ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri);
        if (acquireUnstableContentProviderClient == null) {
            throw new IllegalArgumentException("No provider found for " + uri);
        }
        return new ProviderHolder(acquireUnstableContentProviderClient);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProviderHolder implements AutoCloseable {
        final ContentProviderClient mProvider;

        ProviderHolder(ContentProviderClient contentProviderClient) {
            this.mProvider = contentProviderClient;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            ContentProviderClient contentProviderClient = this.mProvider;
            if (contentProviderClient == null) {
                return;
            }
            contentProviderClient.close();
        }
    }
}
