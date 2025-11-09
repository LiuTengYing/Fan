package okio;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: -Util.kt */
/* renamed from: okio.-Util  reason: invalid class name */
/* loaded from: classes2.dex */
public final class Util {
    public static final void checkOffsetAndCount(long j, long j2, long j3) {
        if ((j2 | j3) < 0 || j2 > j || j - j2 < j3) {
            throw new ArrayIndexOutOfBoundsException("size=" + j + " offset=" + j2 + " byteCount=" + j3);
        }
    }

    public static final boolean arrayRangeEquals(@NotNull byte[] a2, int i, @NotNull byte[] b, int i2, int i3) {
        Intrinsics.checkNotNullParameter(a2, "a");
        Intrinsics.checkNotNullParameter(b, "b");
        int i4 = 0;
        while (i4 < i3) {
            int i5 = i4 + 1;
            if (a2[i4 + i] != b[i4 + i2]) {
                return false;
            }
            i4 = i5;
        }
        return true;
    }
}
