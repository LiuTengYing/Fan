package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes2.dex */
public interface Encoder {
    int decode(String str, OutputStream outputStream) throws IOException;
}
