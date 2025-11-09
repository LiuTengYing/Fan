package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Map;
/* loaded from: classes2.dex */
public final class Code128Writer extends OneDimensionalCodeWriter {
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String str, BarcodeFormat barcodeFormat, int i, int i2, Map<EncodeHintType, ?> map) throws WriterException {
        if (barcodeFormat != BarcodeFormat.CODE_128) {
            throw new IllegalArgumentException("Can only encode CODE_128, but got " + barcodeFormat);
        }
        return super.encode(str, barcodeFormat, i, i2, map);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String str) {
        int length = str.length();
        if (length < 1 || length > 80) {
            throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
        }
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            if (charAt < ' ' || charAt > '~') {
                switch (charAt) {
                    case 241:
                    case 242:
                    case 243:
                    case 244:
                        break;
                    default:
                        throw new IllegalArgumentException("Bad character in input: " + charAt);
                }
            }
        }
        ArrayList<int[]> arrayList = new ArrayList();
        int i3 = 1;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < length) {
            int i7 = 100;
            int i8 = isDigits(str, i4, i6 == 99 ? 2 : 4) ? 99 : 100;
            if (i8 == i6) {
                if (i6 != 100) {
                    switch (str.charAt(i4)) {
                        case 241:
                            i7 = 102;
                            break;
                        case 242:
                            i7 = 97;
                            break;
                        case 243:
                            i7 = 96;
                            break;
                        case 244:
                            break;
                        default:
                            int i9 = i4 + 2;
                            i7 = Integer.parseInt(str.substring(i4, i9));
                            i4 = i9;
                            break;
                    }
                } else {
                    i7 = str.charAt(i4) - ' ';
                }
                i4++;
            } else {
                i7 = i6 == 0 ? i8 == 100 ? 104 : 105 : i8;
                i6 = i8;
            }
            arrayList.add(Code128Reader.CODE_PATTERNS[i7]);
            i5 += i7 * i3;
            if (i4 != 0) {
                i3++;
            }
        }
        int[][] iArr = Code128Reader.CODE_PATTERNS;
        arrayList.add(iArr[i5 % 103]);
        arrayList.add(iArr[106]);
        int i10 = 0;
        for (int[] iArr2 : arrayList) {
            for (int i11 : iArr2) {
                i10 += i11;
            }
        }
        boolean[] zArr = new boolean[i10];
        for (int[] iArr3 : arrayList) {
            i += OneDimensionalCodeWriter.appendPattern(zArr, i, iArr3, true);
        }
        return zArr;
    }

    private static boolean isDigits(CharSequence charSequence, int i, int i2) {
        int i3 = i2 + i;
        int length = charSequence.length();
        while (i < i3 && i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < '0' || charAt > '9') {
                if (charAt != 241) {
                    return false;
                }
                i3++;
            }
            i++;
        }
        return i3 <= length;
    }
}
