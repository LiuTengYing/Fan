package androidx.core.os;

import android.os.Trace;
@Deprecated
/* loaded from: classes.dex */
public final class TraceCompat {
    public static void beginSection(String str) {
        Api18Impl.beginSection(str);
    }

    public static void endSection() {
        Api18Impl.endSection();
    }

    /* loaded from: classes.dex */
    static class Api18Impl {
        static void beginSection(String str) {
            Trace.beginSection(str);
        }

        static void endSection() {
            Trace.endSection();
        }
    }
}
