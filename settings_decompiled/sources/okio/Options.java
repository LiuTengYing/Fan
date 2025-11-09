package okio;

import java.util.List;
import java.util.RandomAccess;
import kotlin.collections.AbstractList;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
/* compiled from: Options.kt */
/* loaded from: classes2.dex */
public final class Options extends AbstractList<ByteString> implements RandomAccess {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ByteString[] byteStrings;
    @NotNull
    private final int[] trie;

    public /* synthetic */ Options(ByteString[] byteStringArr, int[] iArr, DefaultConstructorMarker defaultConstructorMarker) {
        this(byteStringArr, iArr);
    }

    @NotNull
    public static final Options of(@NotNull ByteString... byteStringArr) {
        return Companion.of(byteStringArr);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public final /* bridge */ boolean contains(Object obj) {
        if (obj instanceof ByteString) {
            return contains((ByteString) obj);
        }
        return false;
    }

    public /* bridge */ boolean contains(ByteString byteString) {
        return super.contains((Options) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object obj) {
        if (obj instanceof ByteString) {
            return indexOf((ByteString) obj);
        }
        return -1;
    }

    public /* bridge */ int indexOf(ByteString byteString) {
        return super.indexOf((Options) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object obj) {
        if (obj instanceof ByteString) {
            return lastIndexOf((ByteString) obj);
        }
        return -1;
    }

    public /* bridge */ int lastIndexOf(ByteString byteString) {
        return super.lastIndexOf((Options) byteString);
    }

    @NotNull
    public final ByteString[] getByteStrings$external__okio__android_common__okio_lib() {
        return this.byteStrings;
    }

    @NotNull
    public final int[] getTrie$external__okio__android_common__okio_lib() {
        return this.trie;
    }

    private Options(ByteString[] byteStringArr, int[] iArr) {
        this.byteStrings = byteStringArr;
        this.trie = iArr;
    }

    @Override // kotlin.collections.AbstractCollection
    public int getSize() {
        return this.byteStrings.length;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    @NotNull
    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    /* compiled from: Options.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:59:0x00e2, code lost:
            continue;
         */
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final okio.Options of(@org.jetbrains.annotations.NotNull okio.ByteString... r17) {
            /*
                Method dump skipped, instructions count: 320
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.Options.Companion.of(okio.ByteString[]):okio.Options");
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long j, Buffer buffer, int i, List list, int i2, int i3, List list2, int i4, Object obj) {
            companion.buildTrieRecursive((i4 & 1) != 0 ? 0L : j, buffer, (i4 & 4) != 0 ? 0 : i, list, (i4 & 16) != 0 ? 0 : i2, (i4 & 32) != 0 ? list.size() : i3, list2);
        }

        private final void buildTrieRecursive(long j, Buffer buffer, int i, List<? extends ByteString> list, int i2, int i3, List<Integer> list2) {
            int i4;
            int i5;
            int i6;
            int i7;
            Buffer buffer2;
            int i8 = i;
            int i9 = 1;
            if (!(i2 < i3)) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            int i10 = i2;
            while (i10 < i3) {
                int i11 = i10 + 1;
                if (!(list.get(i10).size() >= i8)) {
                    throw new IllegalArgumentException("Failed requirement.".toString());
                }
                i10 = i11;
            }
            ByteString byteString = list.get(i2);
            ByteString byteString2 = list.get(i3 - 1);
            int i12 = -1;
            if (i8 == byteString.size()) {
                int i13 = i2 + 1;
                i4 = i13;
                i5 = list2.get(i2).intValue();
                byteString = list.get(i13);
            } else {
                i4 = i2;
                i5 = -1;
            }
            if (byteString.getByte(i8) != byteString2.getByte(i8)) {
                int i14 = i4 + 1;
                while (i14 < i3) {
                    int i15 = i14 + 1;
                    if (list.get(i14 - 1).getByte(i8) != list.get(i14).getByte(i8)) {
                        i9++;
                    }
                    i14 = i15;
                }
                long intCount = j + getIntCount(buffer) + 2 + (i9 * 2);
                buffer.writeInt(i9);
                buffer.writeInt(i5);
                int i16 = i4;
                while (i16 < i3) {
                    int i17 = i16 + 1;
                    byte b = list.get(i16).getByte(i8);
                    if (i16 == i4 || b != list.get(i16 - 1).getByte(i8)) {
                        buffer.writeInt(b & 255);
                    }
                    i16 = i17;
                }
                Buffer buffer3 = new Buffer();
                while (i4 < i3) {
                    byte b2 = list.get(i4).getByte(i8);
                    int i18 = i4 + 1;
                    int i19 = i18;
                    while (true) {
                        if (i19 >= i3) {
                            i6 = i3;
                            break;
                        }
                        int i20 = i19 + 1;
                        if (b2 != list.get(i19).getByte(i8)) {
                            i6 = i19;
                            break;
                        }
                        i19 = i20;
                    }
                    if (i18 == i6 && i8 + 1 == list.get(i4).size()) {
                        buffer.writeInt(list2.get(i4).intValue());
                        i7 = i6;
                        buffer2 = buffer3;
                    } else {
                        buffer.writeInt(((int) (intCount + getIntCount(buffer3))) * i12);
                        i7 = i6;
                        buffer2 = buffer3;
                        buildTrieRecursive(intCount, buffer3, i8 + 1, list, i4, i6, list2);
                    }
                    buffer3 = buffer2;
                    i4 = i7;
                    i12 = -1;
                }
                buffer.writeAll(buffer3);
                return;
            }
            int min = Math.min(byteString.size(), byteString2.size());
            int i21 = i8;
            int i22 = 0;
            while (i21 < min) {
                int i23 = i21 + 1;
                if (byteString.getByte(i21) != byteString2.getByte(i21)) {
                    break;
                }
                i22++;
                i21 = i23;
            }
            long intCount2 = j + getIntCount(buffer) + 2 + i22 + 1;
            buffer.writeInt(-i22);
            buffer.writeInt(i5);
            int i24 = i8 + i22;
            while (i8 < i24) {
                buffer.writeInt(byteString.getByte(i8) & 255);
                i8++;
            }
            if (i4 + 1 == i3) {
                if (!(i24 == list.get(i4).size())) {
                    throw new IllegalStateException("Check failed.".toString());
                }
                buffer.writeInt(list2.get(i4).intValue());
                return;
            }
            Buffer buffer4 = new Buffer();
            buffer.writeInt(((int) (getIntCount(buffer4) + intCount2)) * (-1));
            buildTrieRecursive(intCount2, buffer4, i24, list, i4, i3, list2);
            buffer.writeAll(buffer4);
        }

        private final long getIntCount(Buffer buffer) {
            return buffer.size() / 4;
        }
    }
}
