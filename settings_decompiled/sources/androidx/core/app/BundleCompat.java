package androidx.core.app;

import android.os.Bundle;
import android.os.IBinder;
/* loaded from: classes.dex */
public final class BundleCompat {
    public static IBinder getBinder(Bundle bundle, String str) {
        return Api18Impl.getBinder(bundle, str);
    }

    /* loaded from: classes.dex */
    static class Api18Impl {
        static IBinder getBinder(Bundle bundle, String str) {
            return bundle.getBinder(str);
        }
    }
}
