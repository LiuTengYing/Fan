package okio;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import okio.internal.BufferKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: Buffer.kt */
/* loaded from: classes2.dex */
public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    @Nullable
    public Segment head;
    private long size;

    @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable, java.nio.channels.Channel
    public void close() {
    }

    @Override // java.io.Flushable
    public void flush() {
    }

    @Override // okio.BufferedSource
    @NotNull
    public Buffer getBuffer() {
        return this;
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return true;
    }

    public final long size() {
        return this.size;
    }

    public final void setSize$external__okio__android_common__okio_lib(long j) {
        this.size = j;
    }

    public long indexOfElement(@NotNull ByteString targetBytes, long j) {
        int i;
        int i2;
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        long j2 = 0;
        if (j >= 0) {
            Segment segment = this.head;
            if (segment == null) {
                return -1L;
            }
            if (size() - j < j) {
                j2 = size();
                while (j2 > j) {
                    segment = segment.prev;
                    Intrinsics.checkNotNull(segment);
                    j2 -= segment.limit - segment.pos;
                }
                if (targetBytes.size() == 2) {
                    byte b = targetBytes.getByte(0);
                    byte b2 = targetBytes.getByte(1);
                    while (j2 < size()) {
                        byte[] bArr = segment.data;
                        i = (int) ((segment.pos + j) - j2);
                        int i3 = segment.limit;
                        while (i < i3) {
                            byte b3 = bArr[i];
                            if (b3 != b && b3 != b2) {
                                i++;
                            }
                            i2 = segment.pos;
                        }
                        j2 += segment.limit - segment.pos;
                        segment = segment.next;
                        Intrinsics.checkNotNull(segment);
                        j = j2;
                    }
                    return -1L;
                }
                byte[] internalArray$external__okio__android_common__okio_lib = targetBytes.internalArray$external__okio__android_common__okio_lib();
                while (j2 < size()) {
                    byte[] bArr2 = segment.data;
                    i = (int) ((segment.pos + j) - j2);
                    int i4 = segment.limit;
                    while (i < i4) {
                        byte b4 = bArr2[i];
                        int length = internalArray$external__okio__android_common__okio_lib.length;
                        int i5 = 0;
                        while (i5 < length) {
                            byte b5 = internalArray$external__okio__android_common__okio_lib[i5];
                            i5++;
                            if (b4 == b5) {
                                i2 = segment.pos;
                            }
                        }
                        i++;
                    }
                    j2 += segment.limit - segment.pos;
                    segment = segment.next;
                    Intrinsics.checkNotNull(segment);
                    j = j2;
                }
                return -1L;
            }
            while (true) {
                long j3 = (segment.limit - segment.pos) + j2;
                if (j3 > j) {
                    break;
                }
                segment = segment.next;
                Intrinsics.checkNotNull(segment);
                j2 = j3;
            }
            if (targetBytes.size() == 2) {
                byte b6 = targetBytes.getByte(0);
                byte b7 = targetBytes.getByte(1);
                while (j2 < size()) {
                    byte[] bArr3 = segment.data;
                    i = (int) ((segment.pos + j) - j2);
                    int i6 = segment.limit;
                    while (i < i6) {
                        byte b8 = bArr3[i];
                        if (b8 != b6 && b8 != b7) {
                            i++;
                        }
                        i2 = segment.pos;
                    }
                    j2 += segment.limit - segment.pos;
                    segment = segment.next;
                    Intrinsics.checkNotNull(segment);
                    j = j2;
                }
                return -1L;
            }
            byte[] internalArray$external__okio__android_common__okio_lib2 = targetBytes.internalArray$external__okio__android_common__okio_lib();
            while (j2 < size()) {
                byte[] bArr4 = segment.data;
                i = (int) ((segment.pos + j) - j2);
                int i7 = segment.limit;
                while (i < i7) {
                    byte b9 = bArr4[i];
                    int length2 = internalArray$external__okio__android_common__okio_lib2.length;
                    int i8 = 0;
                    while (i8 < length2) {
                        byte b10 = internalArray$external__okio__android_common__okio_lib2[i8];
                        i8++;
                        if (b9 == b10) {
                            i2 = segment.pos;
                        }
                    }
                    i++;
                }
                j2 += segment.limit - segment.pos;
                segment = segment.next;
                Intrinsics.checkNotNull(segment);
                j = j2;
            }
            return -1L;
            return (i - i2) + j2;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("fromIndex < 0: ", Long.valueOf(j)).toString());
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    @Override // okio.BufferedSource
    public boolean request(long j) {
        return this.size >= j;
    }

    public byte readByte() throws EOFException {
        if (size() == 0) {
            throw new EOFException();
        }
        Segment segment = this.head;
        Intrinsics.checkNotNull(segment);
        int i = segment.pos;
        int i2 = segment.limit;
        int i3 = i + 1;
        byte b = segment.data[i];
        setSize$external__okio__android_common__okio_lib(size() - 1);
        if (i3 == i2) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = i3;
        }
        return b;
    }

    @NotNull
    public String readUtf8() {
        return readString(this.size, Charsets.UTF_8);
    }

    @NotNull
    public String readUtf8(long j) throws EOFException {
        return readString(j, Charsets.UTF_8);
    }

    @Override // okio.BufferedSource
    @NotNull
    public String readString(@NotNull Charset charset) {
        Intrinsics.checkNotNullParameter(charset, "charset");
        return readString(this.size, charset);
    }

    @NotNull
    public String readString(long j, @NotNull Charset charset) throws EOFException {
        Intrinsics.checkNotNullParameter(charset, "charset");
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (!(i >= 0 && j <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(j)).toString());
        }
        if (this.size >= j) {
            if (i == 0) {
                return "";
            }
            Segment segment = this.head;
            Intrinsics.checkNotNull(segment);
            int i2 = segment.pos;
            if (i2 + j > segment.limit) {
                return new String(readByteArray(j), charset);
            }
            int i3 = (int) j;
            String str = new String(segment.data, i2, i3, charset);
            int i4 = segment.pos + i3;
            segment.pos = i4;
            this.size -= j;
            if (i4 == segment.limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            }
            return str;
        }
        throw new EOFException();
    }

    public int readInt() throws EOFException {
        if (size() < 4) {
            throw new EOFException();
        }
        Segment segment = this.head;
        Intrinsics.checkNotNull(segment);
        int i = segment.pos;
        int i2 = segment.limit;
        if (i2 - i < 4) {
            return (readByte() & 255) | ((readByte() & 255) << 24) | ((readByte() & 255) << 16) | ((readByte() & 255) << 8);
        }
        byte[] bArr = segment.data;
        int i3 = i + 1;
        int i4 = i3 + 1;
        int i5 = ((bArr[i] & 255) << 24) | ((bArr[i3] & 255) << 16);
        int i6 = i4 + 1;
        int i7 = i5 | ((bArr[i4] & 255) << 8);
        int i8 = i6 + 1;
        int i9 = i7 | (bArr[i6] & 255);
        setSize$external__okio__android_common__okio_lib(size() - 4);
        if (i8 == i2) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = i8;
        }
        return i9;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(@NotNull ByteBuffer sink) throws IOException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(sink.remaining(), segment.limit - segment.pos);
        sink.put(segment.data, segment.pos, min);
        int i = segment.pos + min;
        segment.pos = i;
        this.size -= min;
        if (i == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    @Override // okio.BufferedSink
    @NotNull
    public Buffer writeUtf8(@NotNull String string) {
        Intrinsics.checkNotNullParameter(string, "string");
        return writeUtf8(string, 0, string.length());
    }

    public final byte getByte(long j) {
        Util.checkOffsetAndCount(size(), j, 1L);
        Segment segment = this.head;
        if (segment != null) {
            if (size() - j < j) {
                long size = size();
                while (size > j) {
                    segment = segment.prev;
                    Intrinsics.checkNotNull(segment);
                    size -= segment.limit - segment.pos;
                }
                Intrinsics.checkNotNull(segment);
                return segment.data[(int) ((segment.pos + j) - size)];
            }
            long j2 = 0;
            while (true) {
                long j3 = (segment.limit - segment.pos) + j2;
                if (j3 <= j) {
                    segment = segment.next;
                    Intrinsics.checkNotNull(segment);
                    j2 = j3;
                } else {
                    Intrinsics.checkNotNull(segment);
                    return segment.data[(int) ((segment.pos + j) - j2)];
                }
            }
        } else {
            Intrinsics.checkNotNull(null);
            throw null;
        }
    }

    public final void clear() {
        skip(size());
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(@NotNull ByteBuffer source) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        int remaining = source.remaining();
        int i = remaining;
        while (i > 0) {
            Segment writableSegment$external__okio__android_common__okio_lib = writableSegment$external__okio__android_common__okio_lib(1);
            int min = Math.min(i, 8192 - writableSegment$external__okio__android_common__okio_lib.limit);
            source.get(writableSegment$external__okio__android_common__okio_lib.data, writableSegment$external__okio__android_common__okio_lib.limit, min);
            i -= min;
            writableSegment$external__okio__android_common__okio_lib.limit += min;
        }
        this.size += remaining;
        return remaining;
    }

    @Override // okio.BufferedSource
    public void skip(long j) throws EOFException {
        while (j > 0) {
            Segment segment = this.head;
            if (segment != null) {
                int min = (int) Math.min(j, segment.limit - segment.pos);
                long j2 = min;
                setSize$external__okio__android_common__okio_lib(size() - j2);
                j -= j2;
                int i = segment.pos + min;
                segment.pos = i;
                if (i == segment.limit) {
                    this.head = segment.pop();
                    SegmentPool.recycle(segment);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    @Override // okio.BufferedSource
    public long indexOf(@NotNull ByteString bytes) throws IOException {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return indexOf(bytes, 0L);
    }

    @Override // okio.BufferedSource
    public long indexOfElement(@NotNull ByteString targetBytes) {
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        return indexOfElement(targetBytes, 0L);
    }

    @Override // okio.BufferedSource
    public boolean rangeEquals(long j, @NotNull ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return rangeEquals(j, bytes, 0, bytes.size());
    }

    @NotNull
    public final Segment writableSegment$external__okio__android_common__okio_lib(int i) {
        boolean z = true;
        if (!((i < 1 || i > 8192) ? false : false)) {
            throw new IllegalArgumentException("unexpected capacity".toString());
        }
        Segment segment = this.head;
        if (segment == null) {
            Segment take = SegmentPool.take();
            this.head = take;
            take.prev = take;
            take.next = take;
            return take;
        }
        Intrinsics.checkNotNull(segment);
        Segment segment2 = segment.prev;
        Intrinsics.checkNotNull(segment2);
        if (segment2.limit + i > 8192 || !segment2.owner) {
            segment2 = segment2.push(SegmentPool.take());
        }
        return segment2;
    }

    @NotNull
    public String toString() {
        return snapshot().toString();
    }

    @NotNull
    public Buffer write(@NotNull byte[] source) {
        Intrinsics.checkNotNullParameter(source, "source");
        return write(source, 0, source.length);
    }

    @NotNull
    public Buffer clone() {
        return copy();
    }

    @NotNull
    public Buffer write(@NotNull byte[] source, int i, int i2) {
        Intrinsics.checkNotNullParameter(source, "source");
        long j = i2;
        Util.checkOffsetAndCount(source.length, i, j);
        int i3 = i2 + i;
        while (i < i3) {
            Segment writableSegment$external__okio__android_common__okio_lib = writableSegment$external__okio__android_common__okio_lib(1);
            int min = Math.min(i3 - i, 8192 - writableSegment$external__okio__android_common__okio_lib.limit);
            int i4 = i + min;
            ArraysKt___ArraysJvmKt.copyInto(source, writableSegment$external__okio__android_common__okio_lib.data, writableSegment$external__okio__android_common__okio_lib.limit, i, i4);
            writableSegment$external__okio__android_common__okio_lib.limit += min;
            i = i4;
        }
        setSize$external__okio__android_common__okio_lib(size() + j);
        return this;
    }

    @NotNull
    public byte[] readByteArray(long j) throws EOFException {
        if (!(j >= 0 && j <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(j)).toString());
        }
        if (size() < j) {
            throw new EOFException();
        }
        byte[] bArr = new byte[(int) j];
        readFully(bArr);
        return bArr;
    }

    public void readFully(@NotNull byte[] sink) throws EOFException {
        Intrinsics.checkNotNullParameter(sink, "sink");
        int i = 0;
        while (i < sink.length) {
            int read = read(sink, i, sink.length - i);
            if (read == -1) {
                throw new EOFException();
            }
            i += read;
        }
    }

    public int read(@NotNull byte[] sink, int i, int i2) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        Util.checkOffsetAndCount(sink.length, i, i2);
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(i2, segment.limit - segment.pos);
        byte[] bArr = segment.data;
        int i3 = segment.pos;
        ArraysKt___ArraysJvmKt.copyInto(bArr, sink, i, i3, i3 + min);
        segment.pos += min;
        setSize$external__okio__android_common__okio_lib(size() - min);
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    @NotNull
    public ByteString readByteString() {
        return readByteString(size());
    }

    @NotNull
    public ByteString readByteString(long j) throws EOFException {
        if (!(j >= 0 && j <= 2147483647L)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount: ", Long.valueOf(j)).toString());
        }
        if (size() >= j) {
            if (j >= 4096) {
                ByteString snapshot = snapshot((int) j);
                skip(j);
                return snapshot;
            }
            return new ByteString(readByteArray(j));
        }
        throw new EOFException();
    }

    @Override // okio.BufferedSource
    public int select(@NotNull Options options) {
        Intrinsics.checkNotNullParameter(options, "options");
        int selectPrefix$default = BufferKt.selectPrefix$default(this, options, false, 2, null);
        if (selectPrefix$default == -1) {
            return -1;
        }
        skip(options.getByteStrings$external__okio__android_common__okio_lib()[selectPrefix$default].size());
        return selectPrefix$default;
    }

    @Override // okio.BufferedSink
    @NotNull
    public Buffer writeUtf8(@NotNull String string, int i, int i2) {
        char charAt;
        Intrinsics.checkNotNullParameter(string, "string");
        if (i >= 0) {
            if (!(i2 >= i)) {
                throw new IllegalArgumentException(("endIndex < beginIndex: " + i2 + " < " + i).toString());
            }
            if (!(i2 <= string.length())) {
                throw new IllegalArgumentException(("endIndex > string.length: " + i2 + " > " + string.length()).toString());
            }
            while (i < i2) {
                char charAt2 = string.charAt(i);
                if (charAt2 < 128) {
                    Segment writableSegment$external__okio__android_common__okio_lib = writableSegment$external__okio__android_common__okio_lib(1);
                    byte[] bArr = writableSegment$external__okio__android_common__okio_lib.data;
                    int i3 = writableSegment$external__okio__android_common__okio_lib.limit - i;
                    int min = Math.min(i2, 8192 - i3);
                    int i4 = i + 1;
                    bArr[i + i3] = (byte) charAt2;
                    while (true) {
                        i = i4;
                        if (i >= min || (charAt = string.charAt(i)) >= 128) {
                            break;
                        }
                        i4 = i + 1;
                        bArr[i + i3] = (byte) charAt;
                    }
                    int i5 = writableSegment$external__okio__android_common__okio_lib.limit;
                    int i6 = (i3 + i) - i5;
                    writableSegment$external__okio__android_common__okio_lib.limit = i5 + i6;
                    setSize$external__okio__android_common__okio_lib(size() + i6);
                } else {
                    if (charAt2 < 2048) {
                        Segment writableSegment$external__okio__android_common__okio_lib2 = writableSegment$external__okio__android_common__okio_lib(2);
                        byte[] bArr2 = writableSegment$external__okio__android_common__okio_lib2.data;
                        int i7 = writableSegment$external__okio__android_common__okio_lib2.limit;
                        bArr2[i7] = (byte) ((charAt2 >> 6) | 192);
                        bArr2[i7 + 1] = (byte) ((charAt2 & '?') | 128);
                        writableSegment$external__okio__android_common__okio_lib2.limit = i7 + 2;
                        setSize$external__okio__android_common__okio_lib(size() + 2);
                    } else if (charAt2 < 55296 || charAt2 > 57343) {
                        Segment writableSegment$external__okio__android_common__okio_lib3 = writableSegment$external__okio__android_common__okio_lib(3);
                        byte[] bArr3 = writableSegment$external__okio__android_common__okio_lib3.data;
                        int i8 = writableSegment$external__okio__android_common__okio_lib3.limit;
                        bArr3[i8] = (byte) ((charAt2 >> '\f') | 224);
                        bArr3[i8 + 1] = (byte) ((63 & (charAt2 >> 6)) | 128);
                        bArr3[i8 + 2] = (byte) ((charAt2 & '?') | 128);
                        writableSegment$external__okio__android_common__okio_lib3.limit = i8 + 3;
                        setSize$external__okio__android_common__okio_lib(size() + 3);
                    } else {
                        int i9 = i + 1;
                        char charAt3 = i9 < i2 ? string.charAt(i9) : (char) 0;
                        if (charAt2 <= 56319) {
                            if (56320 <= charAt3 && charAt3 < 57344) {
                                int i10 = (((charAt2 & 1023) << 10) | (charAt3 & 1023)) + 65536;
                                Segment writableSegment$external__okio__android_common__okio_lib4 = writableSegment$external__okio__android_common__okio_lib(4);
                                byte[] bArr4 = writableSegment$external__okio__android_common__okio_lib4.data;
                                int i11 = writableSegment$external__okio__android_common__okio_lib4.limit;
                                bArr4[i11] = (byte) ((i10 >> 18) | 240);
                                bArr4[i11 + 1] = (byte) (((i10 >> 12) & 63) | 128);
                                bArr4[i11 + 2] = (byte) (((i10 >> 6) & 63) | 128);
                                bArr4[i11 + 3] = (byte) ((i10 & 63) | 128);
                                writableSegment$external__okio__android_common__okio_lib4.limit = i11 + 4;
                                setSize$external__okio__android_common__okio_lib(size() + 4);
                                i += 2;
                            }
                        }
                        writeByte(63);
                        i = i9;
                    }
                    i++;
                }
            }
            return this;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("beginIndex < 0: ", Integer.valueOf(i)).toString());
    }

    public long writeAll(@NotNull Source source) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        long j = 0;
        while (true) {
            long read = source.read(this, 8192L);
            if (read == -1) {
                return j;
            }
            j += read;
        }
    }

    @Override // okio.BufferedSink
    @NotNull
    public Buffer writeByte(int i) {
        Segment writableSegment$external__okio__android_common__okio_lib = writableSegment$external__okio__android_common__okio_lib(1);
        byte[] bArr = writableSegment$external__okio__android_common__okio_lib.data;
        int i2 = writableSegment$external__okio__android_common__okio_lib.limit;
        writableSegment$external__okio__android_common__okio_lib.limit = i2 + 1;
        bArr[i2] = (byte) i;
        setSize$external__okio__android_common__okio_lib(size() + 1);
        return this;
    }

    @NotNull
    public Buffer writeInt(int i) {
        Segment writableSegment$external__okio__android_common__okio_lib = writableSegment$external__okio__android_common__okio_lib(4);
        byte[] bArr = writableSegment$external__okio__android_common__okio_lib.data;
        int i2 = writableSegment$external__okio__android_common__okio_lib.limit;
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((i >>> 24) & 255);
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((i >>> 16) & 255);
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((i >>> 8) & 255);
        bArr[i5] = (byte) (i & 255);
        writableSegment$external__okio__android_common__okio_lib.limit = i5 + 1;
        setSize$external__okio__android_common__okio_lib(size() + 4);
        return this;
    }

    public void write(@NotNull Buffer source, long j) {
        Segment segment;
        Segment segment2;
        Intrinsics.checkNotNullParameter(source, "source");
        if (!(source != this)) {
            throw new IllegalArgumentException("source == this".toString());
        }
        Util.checkOffsetAndCount(source.size(), 0L, j);
        while (j > 0) {
            Segment segment3 = source.head;
            Intrinsics.checkNotNull(segment3);
            int i = segment3.limit;
            Intrinsics.checkNotNull(source.head);
            if (j < i - segment.pos) {
                Segment segment4 = this.head;
                if (segment4 != null) {
                    Intrinsics.checkNotNull(segment4);
                    segment2 = segment4.prev;
                } else {
                    segment2 = null;
                }
                if (segment2 != null && segment2.owner) {
                    if ((segment2.limit + j) - (segment2.shared ? 0 : segment2.pos) <= 8192) {
                        Segment segment5 = source.head;
                        Intrinsics.checkNotNull(segment5);
                        segment5.writeTo(segment2, (int) j);
                        source.setSize$external__okio__android_common__okio_lib(source.size() - j);
                        setSize$external__okio__android_common__okio_lib(size() + j);
                        return;
                    }
                }
                Segment segment6 = source.head;
                Intrinsics.checkNotNull(segment6);
                source.head = segment6.split((int) j);
            }
            Segment segment7 = source.head;
            Intrinsics.checkNotNull(segment7);
            long j2 = segment7.limit - segment7.pos;
            source.head = segment7.pop();
            Segment segment8 = this.head;
            if (segment8 == null) {
                this.head = segment7;
                segment7.prev = segment7;
                segment7.next = segment7;
            } else {
                Intrinsics.checkNotNull(segment8);
                Segment segment9 = segment8.prev;
                Intrinsics.checkNotNull(segment9);
                segment9.push(segment7).compact();
            }
            source.setSize$external__okio__android_common__okio_lib(source.size() - j2);
            setSize$external__okio__android_common__okio_lib(size() + j2);
            j -= j2;
        }
    }

    @Override // okio.Source
    public long read(@NotNull Buffer sink, long j) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (!(j >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(j)).toString());
        }
        if (size() == 0) {
            return -1L;
        }
        if (j > size()) {
            j = size();
        }
        sink.write(this, j);
        return j;
    }

    public long indexOf(@NotNull ByteString bytes, long j) throws IOException {
        long j2;
        int i;
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (bytes.size() > 0) {
            long j3 = 0;
            if (j >= 0) {
                Segment segment = this.head;
                if (segment != null) {
                    if (size() - j < j) {
                        long size = size();
                        while (size > j) {
                            segment = segment.prev;
                            Intrinsics.checkNotNull(segment);
                            size -= segment.limit - segment.pos;
                        }
                        byte[] internalArray$external__okio__android_common__okio_lib = bytes.internalArray$external__okio__android_common__okio_lib();
                        byte b = internalArray$external__okio__android_common__okio_lib[0];
                        int size2 = bytes.size();
                        long size3 = (size() - size2) + 1;
                        j2 = size;
                        long j4 = j;
                        while (j2 < size3) {
                            byte[] bArr = segment.data;
                            int min = (int) Math.min(segment.limit, (segment.pos + size3) - j2);
                            i = (int) ((segment.pos + j4) - j2);
                            while (i < min) {
                                int i2 = i + 1;
                                if (bArr[i] == b && BufferKt.rangeEquals(segment, i2, internalArray$external__okio__android_common__okio_lib, 1, size2)) {
                                    return (i - segment.pos) + j2;
                                }
                                i = i2;
                            }
                            j2 += segment.limit - segment.pos;
                            segment = segment.next;
                            Intrinsics.checkNotNull(segment);
                            j4 = j2;
                        }
                    } else {
                        while (true) {
                            long j5 = (segment.limit - segment.pos) + j3;
                            if (j5 > j) {
                                break;
                            }
                            segment = segment.next;
                            Intrinsics.checkNotNull(segment);
                            j3 = j5;
                        }
                        byte[] internalArray$external__okio__android_common__okio_lib2 = bytes.internalArray$external__okio__android_common__okio_lib();
                        byte b2 = internalArray$external__okio__android_common__okio_lib2[0];
                        int size4 = bytes.size();
                        long size5 = (size() - size4) + 1;
                        j2 = j3;
                        long j6 = j;
                        while (j2 < size5) {
                            byte[] bArr2 = segment.data;
                            int min2 = (int) Math.min(segment.limit, (segment.pos + size5) - j2);
                            i = (int) ((segment.pos + j6) - j2);
                            while (i < min2) {
                                int i3 = i + 1;
                                if (bArr2[i] == b2 && BufferKt.rangeEquals(segment, i3, internalArray$external__okio__android_common__okio_lib2, 1, size4)) {
                                    return (i - segment.pos) + j2;
                                }
                                i = i3;
                            }
                            j2 += segment.limit - segment.pos;
                            segment = segment.next;
                            Intrinsics.checkNotNull(segment);
                            j6 = j2;
                        }
                    }
                }
                return -1L;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("fromIndex < 0: ", Long.valueOf(j)).toString());
        }
        throw new IllegalArgumentException("bytes is empty".toString());
    }

    public boolean rangeEquals(long j, @NotNull ByteString bytes, int i, int i2) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (j < 0 || i < 0 || i2 < 0 || size() - j < i2 || bytes.size() - i < i2) {
            return false;
        }
        int i3 = 0;
        while (i3 < i2) {
            int i4 = i3 + 1;
            if (getByte(i3 + j) != bytes.getByte(i3 + i)) {
                return false;
            }
            i3 = i4;
        }
        return true;
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (!(obj instanceof Buffer)) {
                return false;
            }
            Buffer buffer = (Buffer) obj;
            if (size() != buffer.size()) {
                return false;
            }
            if (size() != 0) {
                Segment segment = this.head;
                Intrinsics.checkNotNull(segment);
                Segment segment2 = buffer.head;
                Intrinsics.checkNotNull(segment2);
                int i = segment.pos;
                int i2 = segment2.pos;
                long j = 0;
                while (j < size()) {
                    long min = Math.min(segment.limit - i, segment2.limit - i2);
                    long j2 = 0;
                    while (j2 < min) {
                        j2++;
                        int i3 = i + 1;
                        int i4 = i2 + 1;
                        if (segment.data[i] != segment2.data[i2]) {
                            return false;
                        }
                        i = i3;
                        i2 = i4;
                    }
                    if (i == segment.limit) {
                        segment = segment.next;
                        Intrinsics.checkNotNull(segment);
                        i = segment.pos;
                    }
                    if (i2 == segment2.limit) {
                        segment2 = segment2.next;
                        Intrinsics.checkNotNull(segment2);
                        i2 = segment2.pos;
                    }
                    j += min;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        Segment segment = this.head;
        if (segment == null) {
            return 0;
        }
        int i = 1;
        do {
            int i2 = segment.limit;
            for (int i3 = segment.pos; i3 < i2; i3++) {
                i = (i * 31) + segment.data[i3];
            }
            segment = segment.next;
            Intrinsics.checkNotNull(segment);
        } while (segment != this.head);
        return i;
    }

    @NotNull
    public final Buffer copy() {
        Buffer buffer = new Buffer();
        if (size() != 0) {
            Segment segment = this.head;
            Intrinsics.checkNotNull(segment);
            Segment sharedCopy = segment.sharedCopy();
            buffer.head = sharedCopy;
            sharedCopy.prev = sharedCopy;
            sharedCopy.next = sharedCopy;
            for (Segment segment2 = segment.next; segment2 != segment; segment2 = segment2.next) {
                Segment segment3 = sharedCopy.prev;
                Intrinsics.checkNotNull(segment3);
                Intrinsics.checkNotNull(segment2);
                segment3.push(segment2.sharedCopy());
            }
            buffer.setSize$external__okio__android_common__okio_lib(size());
        }
        return buffer;
    }

    @NotNull
    public final ByteString snapshot() {
        if (!(size() <= 2147483647L)) {
            throw new IllegalStateException(Intrinsics.stringPlus("size > Int.MAX_VALUE: ", Long.valueOf(size())).toString());
        }
        return snapshot((int) size());
    }

    @NotNull
    public final ByteString snapshot(int i) {
        if (i == 0) {
            return ByteString.EMPTY;
        }
        Util.checkOffsetAndCount(size(), 0L, i);
        Segment segment = this.head;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < i) {
            Intrinsics.checkNotNull(segment);
            int i5 = segment.limit;
            int i6 = segment.pos;
            if (i5 == i6) {
                throw new AssertionError("s.limit == s.pos");
            }
            i3 += i5 - i6;
            i4++;
            segment = segment.next;
        }
        byte[][] bArr = new byte[i4];
        int[] iArr = new int[i4 * 2];
        Segment segment2 = this.head;
        int i7 = 0;
        while (i2 < i) {
            Intrinsics.checkNotNull(segment2);
            bArr[i7] = segment2.data;
            i2 += segment2.limit - segment2.pos;
            iArr[i7] = Math.min(i2, i);
            iArr[i7 + i4] = segment2.pos;
            segment2.shared = true;
            i7++;
            segment2 = segment2.next;
        }
        return new SegmentedByteString(bArr, iArr);
    }
}
