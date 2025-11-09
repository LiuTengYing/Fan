package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
/* loaded from: classes2.dex */
public abstract class ResponseBody implements Closeable {
    public abstract MediaType contentType();

    public abstract BufferedSource source();

    public final String string() throws IOException {
        BufferedSource source = source();
        try {
            return source.readString(Util.bomAwareCharset(source, charset()));
        } finally {
            Util.closeQuietly(source);
        }
    }

    private Charset charset() {
        contentType();
        return Util.UTF_8;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        Util.closeQuietly(source());
    }

    public static ResponseBody create(MediaType mediaType, byte[] bArr) {
        return create(mediaType, bArr.length, new Buffer().write(bArr));
    }

    public static ResponseBody create(MediaType mediaType, long j, BufferedSource bufferedSource) {
        if (bufferedSource == null) {
            throw new NullPointerException("source == null");
        }
        return new ResponseBody(mediaType, j, bufferedSource) { // from class: okhttp3.ResponseBody.1
            final /* synthetic */ BufferedSource val$content;
            final /* synthetic */ long val$contentLength;

            @Override // okhttp3.ResponseBody
            public MediaType contentType() {
                return null;
            }

            {
                this.val$contentLength = j;
                this.val$content = bufferedSource;
            }

            @Override // okhttp3.ResponseBody
            public BufferedSource source() {
                return this.val$content;
            }
        };
    }
}
