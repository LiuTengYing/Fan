package okhttp3.internal;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.TimeZone;
import java.util.regex.Pattern;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.ByteString;
/* loaded from: classes2.dex */
public final class Util {
    public static final byte[] EMPTY_BYTE_ARRAY;
    public static final RequestBody EMPTY_REQUEST;
    public static final ResponseBody EMPTY_RESPONSE;
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final ByteString UTF_8_BOM = ByteString.decodeHex("efbbbf");
    private static final ByteString UTF_16_BE_BOM = ByteString.decodeHex("feff");
    private static final ByteString UTF_16_LE_BOM = ByteString.decodeHex("fffe");
    private static final ByteString UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
    private static final ByteString UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset UTF_16_BE = Charset.forName("UTF-16BE");
    private static final Charset UTF_16_LE = Charset.forName("UTF-16LE");
    private static final Charset UTF_32_BE = Charset.forName("UTF-32BE");
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");
    public static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

    static {
        byte[] bArr = new byte[0];
        EMPTY_BYTE_ARRAY = bArr;
        EMPTY_RESPONSE = ResponseBody.create(null, bArr);
        EMPTY_REQUEST = RequestBody.create(null, bArr);
    }

    public static void checkOffsetAndCount(long j, long j2, long j3) {
        if ((j2 | j3) < 0 || j2 > j || j - j2 < j3) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception unused) {
            }
        }
    }

    public static Charset bomAwareCharset(BufferedSource bufferedSource, Charset charset) throws IOException {
        ByteString byteString = UTF_8_BOM;
        if (bufferedSource.rangeEquals(0L, byteString)) {
            bufferedSource.skip(byteString.size());
            return UTF_8;
        }
        ByteString byteString2 = UTF_16_BE_BOM;
        if (bufferedSource.rangeEquals(0L, byteString2)) {
            bufferedSource.skip(byteString2.size());
            return UTF_16_BE;
        }
        ByteString byteString3 = UTF_16_LE_BOM;
        if (bufferedSource.rangeEquals(0L, byteString3)) {
            bufferedSource.skip(byteString3.size());
            return UTF_16_LE;
        }
        ByteString byteString4 = UTF_32_BE_BOM;
        if (bufferedSource.rangeEquals(0L, byteString4)) {
            bufferedSource.skip(byteString4.size());
            return UTF_32_BE;
        }
        ByteString byteString5 = UTF_32_LE_BOM;
        if (bufferedSource.rangeEquals(0L, byteString5)) {
            bufferedSource.skip(byteString5.size());
            return UTF_32_LE;
        }
        return charset;
    }
}
