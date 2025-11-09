package okhttp3;

import java.io.Closeable;
/* loaded from: classes2.dex */
public final class Response implements Closeable {
    final ResponseBody body;
    final int code;
    final String message;
    final Protocol protocol;

    public ResponseBody body() {
        return this.body;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.body.close();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response{protocol=");
        sb.append(this.protocol);
        sb.append(", code=");
        sb.append(this.code);
        sb.append(", message=");
        sb.append(this.message);
        sb.append(", url=");
        throw null;
    }
}
