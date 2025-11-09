package com.google.zxing.aztec;

import com.google.zxing.Reader;
/* loaded from: classes2.dex */
public final class AztecReader implements Reader {
    @Override // com.google.zxing.Reader
    public void reset() {
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0063 A[LOOP:0: B:36:0x0061->B:37:0x0063, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0080  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x008b  */
    @Override // com.google.zxing.Reader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.google.zxing.Result decode(com.google.zxing.BinaryBitmap r5, java.util.Map<com.google.zxing.DecodeHintType, ?> r6) throws com.google.zxing.NotFoundException, com.google.zxing.FormatException {
        /*
            r4 = this;
            com.google.zxing.aztec.detector.Detector r4 = new com.google.zxing.aztec.detector.Detector
            com.google.zxing.common.BitMatrix r5 = r5.getBlackMatrix()
            r4.<init>(r5)
            r5 = 0
            r0 = 0
            com.google.zxing.aztec.AztecDetectorResult r1 = r4.detect(r5)     // Catch: com.google.zxing.FormatException -> L25 com.google.zxing.NotFoundException -> L2b
            com.google.zxing.ResultPoint[] r2 = r1.getPoints()     // Catch: com.google.zxing.FormatException -> L25 com.google.zxing.NotFoundException -> L2b
            com.google.zxing.aztec.decoder.Decoder r3 = new com.google.zxing.aztec.decoder.Decoder     // Catch: com.google.zxing.FormatException -> L21 com.google.zxing.NotFoundException -> L23
            r3.<init>()     // Catch: com.google.zxing.FormatException -> L21 com.google.zxing.NotFoundException -> L23
            com.google.zxing.common.DecoderResult r1 = r3.decode(r1)     // Catch: com.google.zxing.FormatException -> L21 com.google.zxing.NotFoundException -> L23
            r3 = r2
            r2 = r0
            r0 = r1
            r1 = r2
            goto L2f
        L21:
            r1 = move-exception
            goto L27
        L23:
            r1 = move-exception
            goto L2d
        L25:
            r1 = move-exception
            r2 = r0
        L27:
            r3 = r2
            r2 = r1
            r1 = r0
            goto L2f
        L2b:
            r1 = move-exception
            r2 = r0
        L2d:
            r3 = r2
            r2 = r0
        L2f:
            if (r0 != 0) goto L54
            r0 = 1
            com.google.zxing.aztec.AztecDetectorResult r4 = r4.detect(r0)     // Catch: com.google.zxing.FormatException -> L44 com.google.zxing.NotFoundException -> L4c
            com.google.zxing.ResultPoint[] r3 = r4.getPoints()     // Catch: com.google.zxing.FormatException -> L44 com.google.zxing.NotFoundException -> L4c
            com.google.zxing.aztec.decoder.Decoder r0 = new com.google.zxing.aztec.decoder.Decoder     // Catch: com.google.zxing.FormatException -> L44 com.google.zxing.NotFoundException -> L4c
            r0.<init>()     // Catch: com.google.zxing.FormatException -> L44 com.google.zxing.NotFoundException -> L4c
            com.google.zxing.common.DecoderResult r0 = r0.decode(r4)     // Catch: com.google.zxing.FormatException -> L44 com.google.zxing.NotFoundException -> L4c
            goto L54
        L44:
            r4 = move-exception
            if (r1 != 0) goto L4b
            if (r2 == 0) goto L4a
            throw r2
        L4a:
            throw r4
        L4b:
            throw r1
        L4c:
            r4 = move-exception
            if (r1 != 0) goto L53
            if (r2 == 0) goto L52
            throw r2
        L52:
            throw r4
        L53:
            throw r1
        L54:
            if (r6 == 0) goto L6b
            com.google.zxing.DecodeHintType r4 = com.google.zxing.DecodeHintType.NEED_RESULT_POINT_CALLBACK
            java.lang.Object r4 = r6.get(r4)
            com.google.zxing.ResultPointCallback r4 = (com.google.zxing.ResultPointCallback) r4
            if (r4 == 0) goto L6b
            int r6 = r3.length
        L61:
            if (r5 >= r6) goto L6b
            r1 = r3[r5]
            r4.foundPossibleResultPoint(r1)
            int r5 = r5 + 1
            goto L61
        L6b:
            com.google.zxing.Result r4 = new com.google.zxing.Result
            java.lang.String r5 = r0.getText()
            byte[] r6 = r0.getRawBytes()
            com.google.zxing.BarcodeFormat r1 = com.google.zxing.BarcodeFormat.AZTEC
            r4.<init>(r5, r6, r3, r1)
            java.util.List r5 = r0.getByteSegments()
            if (r5 == 0) goto L85
            com.google.zxing.ResultMetadataType r6 = com.google.zxing.ResultMetadataType.BYTE_SEGMENTS
            r4.putMetadata(r6, r5)
        L85:
            java.lang.String r5 = r0.getECLevel()
            if (r5 == 0) goto L90
            com.google.zxing.ResultMetadataType r6 = com.google.zxing.ResultMetadataType.ERROR_CORRECTION_LEVEL
            r4.putMetadata(r6, r5)
        L90:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.aztec.AztecReader.decode(com.google.zxing.BinaryBitmap, java.util.Map):com.google.zxing.Result");
    }
}
