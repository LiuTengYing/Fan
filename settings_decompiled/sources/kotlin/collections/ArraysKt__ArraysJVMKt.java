package kotlin.collections;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ArraysJVM.kt */
/* loaded from: classes2.dex */
public class ArraysKt__ArraysJVMKt {
    public static final void copyOfRangeToIndexCheck(int i, int i2) {
        if (i <= i2) {
            return;
        }
        throw new IndexOutOfBoundsException("toIndex (" + i + ") is greater than size (" + i2 + ").");
    }
}
