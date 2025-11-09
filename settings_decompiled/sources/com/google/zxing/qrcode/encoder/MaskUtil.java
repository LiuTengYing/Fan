package com.google.zxing.qrcode.encoder;
/* loaded from: classes2.dex */
final class MaskUtil {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int applyMaskPenaltyRule1(ByteMatrix byteMatrix) {
        return applyMaskPenaltyRule1Internal(byteMatrix, true) + applyMaskPenaltyRule1Internal(byteMatrix, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int applyMaskPenaltyRule2(ByteMatrix byteMatrix) {
        byte[][] array = byteMatrix.getArray();
        int width = byteMatrix.getWidth();
        int height = byteMatrix.getHeight();
        int i = 0;
        for (int i2 = 0; i2 < height - 1; i2++) {
            int i3 = 0;
            while (i3 < width - 1) {
                byte[] bArr = array[i2];
                byte b = bArr[i3];
                int i4 = i3 + 1;
                if (b == bArr[i4]) {
                    byte[] bArr2 = array[i2 + 1];
                    if (b == bArr2[i3] && b == bArr2[i4]) {
                        i++;
                    }
                }
                i3 = i4;
            }
        }
        return i * 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int applyMaskPenaltyRule3(ByteMatrix byteMatrix) {
        int i;
        int i2;
        int i3;
        int i4;
        byte[][] array = byteMatrix.getArray();
        int width = byteMatrix.getWidth();
        int height = byteMatrix.getHeight();
        int i5 = 0;
        for (int i6 = 0; i6 < height; i6++) {
            for (int i7 = 0; i7 < width; i7++) {
                int i8 = i7 + 6;
                if (i8 < width) {
                    byte[] bArr = array[i6];
                    if (bArr[i7] == 1 && bArr[i7 + 1] == 0 && bArr[i7 + 2] == 1 && bArr[i7 + 3] == 1 && bArr[i7 + 4] == 1 && bArr[i7 + 5] == 0 && bArr[i8] == 1 && (((i3 = i7 + 10) < width && bArr[i7 + 7] == 0 && bArr[i7 + 8] == 0 && bArr[i7 + 9] == 0 && bArr[i3] == 0) || (i7 - 4 >= 0 && bArr[i7 - 1] == 0 && bArr[i7 - 2] == 0 && bArr[i7 - 3] == 0 && bArr[i4] == 0))) {
                        i5 += 40;
                    }
                }
                int i9 = i6 + 6;
                if (i9 < height && array[i6][i7] == 1 && array[i6 + 1][i7] == 0 && array[i6 + 2][i7] == 1 && array[i6 + 3][i7] == 1 && array[i6 + 4][i7] == 1 && array[i6 + 5][i7] == 0 && array[i9][i7] == 1 && (((i = i6 + 10) < height && array[i6 + 7][i7] == 0 && array[i6 + 8][i7] == 0 && array[i6 + 9][i7] == 0 && array[i][i7] == 0) || (i6 - 4 >= 0 && array[i6 - 1][i7] == 0 && array[i6 - 2][i7] == 0 && array[i6 - 3][i7] == 0 && array[i2][i7] == 0))) {
                    i5 += 40;
                }
            }
        }
        return i5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int applyMaskPenaltyRule4(ByteMatrix byteMatrix) {
        byte[][] array = byteMatrix.getArray();
        int width = byteMatrix.getWidth();
        int height = byteMatrix.getHeight();
        int i = 0;
        for (int i2 = 0; i2 < height; i2++) {
            byte[] bArr = array[i2];
            for (int i3 = 0; i3 < width; i3++) {
                if (bArr[i3] == 1) {
                    i++;
                }
            }
        }
        return ((int) (Math.abs((i / (byteMatrix.getHeight() * byteMatrix.getWidth())) - 0.5d) * 20.0d)) * 10;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean getDataMaskBit(int i, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int i7;
        switch (i) {
            case 0:
                i3 += i2;
            case 1:
                i4 = i3 & 1;
                break;
            case 2:
                i4 = i2 % 3;
                break;
            case 3:
                i4 = (i3 + i2) % 3;
                break;
            case 4:
                i5 = i3 >>> 1;
                i6 = i2 / 3;
                i7 = i5 + i6;
                i4 = i7 & 1;
                break;
            case 5:
                int i8 = i3 * i2;
                i4 = (i8 & 1) + (i8 % 3);
                break;
            case 6:
                int i9 = i3 * i2;
                i7 = (i9 & 1) + (i9 % 3);
                i4 = i7 & 1;
                break;
            case 7:
                i5 = (i3 * i2) % 3;
                i6 = (i3 + i2) & 1;
                i7 = i5 + i6;
                i4 = i7 & 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid mask pattern: " + i);
        }
        return i4 == 0;
    }

    private static int applyMaskPenaltyRule1Internal(ByteMatrix byteMatrix, boolean z) {
        int height = z ? byteMatrix.getHeight() : byteMatrix.getWidth();
        int width = z ? byteMatrix.getWidth() : byteMatrix.getHeight();
        byte[][] array = byteMatrix.getArray();
        int i = 0;
        for (int i2 = 0; i2 < height; i2++) {
            byte b = -1;
            int i3 = 0;
            for (int i4 = 0; i4 < width; i4++) {
                byte b2 = z ? array[i2][i4] : array[i4][i2];
                if (b2 == b) {
                    i3++;
                } else {
                    if (i3 >= 5) {
                        i += (i3 - 5) + 3;
                    }
                    i3 = 1;
                    b = b2;
                }
            }
            if (i3 >= 5) {
                i += (i3 - 5) + 3;
            }
        }
        return i;
    }
}
