package androidx.mediarouter.media;

import android.os.Messenger;
/* loaded from: classes.dex */
abstract class MediaRouteProviderProtocol {
    public static boolean isValidRemoteMessenger(Messenger messenger) {
        if (messenger != null) {
            try {
                return messenger.getBinder() != null;
            } catch (NullPointerException unused) {
                return false;
            }
        }
        return false;
    }
}
