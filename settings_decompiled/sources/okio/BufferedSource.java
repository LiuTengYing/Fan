package okio;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
/* compiled from: BufferedSource.kt */
/* loaded from: classes2.dex */
public interface BufferedSource extends Source, ReadableByteChannel {
    @NotNull
    Buffer getBuffer();

    long indexOf(@NotNull ByteString byteString) throws IOException;

    long indexOfElement(@NotNull ByteString byteString) throws IOException;

    boolean rangeEquals(long j, @NotNull ByteString byteString) throws IOException;

    @NotNull
    String readString(@NotNull Charset charset) throws IOException;

    boolean request(long j) throws IOException;

    int select(@NotNull Options options) throws IOException;

    void skip(long j) throws IOException;
}
