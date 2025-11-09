package okhttp3;

import okhttp3.internal.Util;
/* loaded from: classes2.dex */
public abstract class RequestBody {
    public static RequestBody create(MediaType mediaType, byte[] bArr) {
        return create(mediaType, bArr, 0, bArr.length);
    }

    public static RequestBody create(MediaType mediaType, byte[] bArr, int i, int i2) {
        if (bArr == null) {
            throw new NullPointerException("content == null");
        }
        Util.checkOffsetAndCount(bArr.length, i, i2);
        return new RequestBody(mediaType, i2, bArr, i) { // from class: okhttp3.RequestBody.2
            final /* synthetic */ int val$byteCount;
            final /* synthetic */ byte[] val$content;
            final /* synthetic */ int val$offset;

            {
                this.val$byteCount = i2;
                this.val$content = bArr;
                this.val$offset = i;
            }
        };
    }
}
