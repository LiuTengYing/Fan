package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
/* loaded from: classes2.dex */
public class Base64 {
    private static final Encoder encoder = new Base64Encoder();

    public static byte[] decode(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((str.length() / 4) * 3);
        try {
            encoder.decode(str, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new DecoderException("unable to decode base64 string: " + e.getMessage(), e);
        }
    }
}
