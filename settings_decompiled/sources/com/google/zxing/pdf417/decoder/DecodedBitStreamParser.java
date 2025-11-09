package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.pdf417.PDF417ResultMetadata;
import java.math.BigInteger;
import java.util.Arrays;
/* loaded from: classes2.dex */
final class DecodedBitStreamParser {
    private static final BigInteger[] EXP900;
    private static final char[] PUNCT_CHARS = {';', '<', '>', '@', '[', '\\', '}', '_', '`', '~', '!', '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\''};
    private static final char[] MIXED_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^'};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    static {
        BigInteger[] bigIntegerArr = new BigInteger[16];
        EXP900 = bigIntegerArr;
        bigIntegerArr[0] = BigInteger.ONE;
        BigInteger valueOf = BigInteger.valueOf(900L);
        bigIntegerArr[1] = valueOf;
        int i = 2;
        while (true) {
            BigInteger[] bigIntegerArr2 = EXP900;
            if (i >= bigIntegerArr2.length) {
                return;
            }
            bigIntegerArr2[i] = bigIntegerArr2[i - 1].multiply(valueOf);
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DecoderResult decode(int[] iArr, String str) throws FormatException {
        int byteCompaction;
        int i = 2;
        StringBuilder sb = new StringBuilder(iArr.length * 2);
        int i2 = iArr[1];
        PDF417ResultMetadata pDF417ResultMetadata = new PDF417ResultMetadata();
        while (i < iArr[0]) {
            if (i2 == 913) {
                byteCompaction = byteCompaction(i2, iArr, i, sb);
            } else if (i2 != 928) {
                switch (i2) {
                    case 900:
                        byteCompaction = textCompaction(iArr, i, sb);
                        break;
                    case 901:
                        byteCompaction = byteCompaction(i2, iArr, i, sb);
                        break;
                    case 902:
                        byteCompaction = numericCompaction(iArr, i, sb);
                        break;
                    default:
                        switch (i2) {
                            case 922:
                            case 923:
                                throw FormatException.getFormatInstance();
                            case 924:
                                byteCompaction = byteCompaction(i2, iArr, i, sb);
                                break;
                            default:
                                byteCompaction = textCompaction(iArr, i - 1, sb);
                                break;
                        }
                }
            } else {
                byteCompaction = decodeMacroBlock(iArr, i, pDF417ResultMetadata);
            }
            if (byteCompaction < iArr.length) {
                i = byteCompaction + 1;
                i2 = iArr[byteCompaction];
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (sb.length() == 0) {
            throw FormatException.getFormatInstance();
        }
        DecoderResult decoderResult = new DecoderResult(null, sb.toString(), null, str);
        decoderResult.setOther(pDF417ResultMetadata);
        return decoderResult;
    }

    private static int decodeMacroBlock(int[] iArr, int i, PDF417ResultMetadata pDF417ResultMetadata) throws FormatException {
        if (i + 2 > iArr[0]) {
            throw FormatException.getFormatInstance();
        }
        int[] iArr2 = new int[2];
        int i2 = 0;
        while (i2 < 2) {
            iArr2[i2] = iArr[i];
            i2++;
            i++;
        }
        pDF417ResultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(iArr2, 2)));
        StringBuilder sb = new StringBuilder();
        int textCompaction = textCompaction(iArr, i, sb);
        pDF417ResultMetadata.setFileId(sb.toString());
        int i3 = iArr[textCompaction];
        if (i3 != 923) {
            if (i3 == 922) {
                pDF417ResultMetadata.setLastSegment(true);
                return textCompaction + 1;
            }
            return textCompaction;
        }
        int i4 = textCompaction + 1;
        int[] iArr3 = new int[iArr[0] - i4];
        boolean z = false;
        int i5 = 0;
        while (i4 < iArr[0] && !z) {
            int i6 = i4 + 1;
            int i7 = iArr[i4];
            if (i7 < 900) {
                iArr3[i5] = i7;
                i4 = i6;
                i5++;
            } else if (i7 == 922) {
                pDF417ResultMetadata.setLastSegment(true);
                z = true;
                i4 = i6 + 1;
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        pDF417ResultMetadata.setOptionalData(Arrays.copyOf(iArr3, i5));
        return i4;
    }

    private static int textCompaction(int[] iArr, int i, StringBuilder sb) {
        int i2 = iArr[0];
        int[] iArr2 = new int[(i2 - i) << 1];
        int[] iArr3 = new int[(i2 - i) << 1];
        boolean z = false;
        int i3 = 0;
        while (i < iArr[0] && !z) {
            int i4 = i + 1;
            int i5 = iArr[i];
            if (i5 < 900) {
                iArr2[i3] = i5 / 30;
                iArr2[i3 + 1] = i5 % 30;
                i3 += 2;
            } else if (i5 != 913) {
                if (i5 != 928) {
                    switch (i5) {
                        case 900:
                            iArr2[i3] = 900;
                            i3++;
                            break;
                        default:
                            switch (i5) {
                            }
                        case 901:
                        case 902:
                            i4--;
                            z = true;
                            break;
                    }
                }
                i4--;
                z = true;
            } else {
                iArr2[i3] = 913;
                i = i4 + 1;
                iArr3[i3] = iArr[i4];
                i3++;
            }
            i = i4;
        }
        decodeTextCompaction(iArr2, iArr3, i3, sb);
        return i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void decodeTextCompaction(int[] iArr, int[] iArr2, int i, StringBuilder sb) {
        Mode mode;
        int i2;
        Mode mode2 = Mode.ALPHA;
        Mode mode3 = mode2;
        for (int i3 = 0; i3 < i; i3++) {
            int i4 = iArr[i3];
            char c = ' ';
            switch (AnonymousClass1.$SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[mode2.ordinal()]) {
                case 1:
                    if (i4 < 26) {
                        i2 = i4 + 65;
                        c = (char) i2;
                        break;
                    } else if (i4 != 26) {
                        if (i4 == 27) {
                            mode2 = Mode.LOWER;
                        } else if (i4 == 28) {
                            mode2 = Mode.MIXED;
                        } else if (i4 == 29) {
                            mode = Mode.PUNCT_SHIFT;
                            c = 0;
                            Mode mode4 = mode;
                            mode3 = mode2;
                            mode2 = mode4;
                            break;
                        } else if (i4 == 913) {
                            sb.append((char) iArr2[i3]);
                        } else if (i4 == 900) {
                            mode2 = Mode.ALPHA;
                        }
                        c = 0;
                        break;
                    }
                    break;
                case 2:
                    if (i4 < 26) {
                        i2 = i4 + 97;
                        c = (char) i2;
                        break;
                    } else if (i4 != 26) {
                        if (i4 != 27) {
                            if (i4 == 28) {
                                mode2 = Mode.MIXED;
                            } else if (i4 == 29) {
                                mode = Mode.PUNCT_SHIFT;
                            } else if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode2 = Mode.ALPHA;
                            }
                            c = 0;
                            break;
                        } else {
                            mode = Mode.ALPHA_SHIFT;
                        }
                        c = 0;
                        Mode mode42 = mode;
                        mode3 = mode2;
                        mode2 = mode42;
                        break;
                    }
                    break;
                case 3:
                    if (i4 < 25) {
                        c = MIXED_CHARS[i4];
                        break;
                    } else {
                        if (i4 == 25) {
                            mode2 = Mode.PUNCT;
                        } else if (i4 != 26) {
                            if (i4 == 27) {
                                mode2 = Mode.LOWER;
                            } else if (i4 == 28) {
                                mode2 = Mode.ALPHA;
                            } else if (i4 == 29) {
                                mode = Mode.PUNCT_SHIFT;
                                c = 0;
                                Mode mode422 = mode;
                                mode3 = mode2;
                                mode2 = mode422;
                                break;
                            } else if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode2 = Mode.ALPHA;
                            }
                        }
                        c = 0;
                        break;
                    }
                    break;
                case 4:
                    if (i4 < 29) {
                        c = PUNCT_CHARS[i4];
                        break;
                    } else {
                        if (i4 == 29) {
                            mode2 = Mode.ALPHA;
                        } else if (i4 == 913) {
                            sb.append((char) iArr2[i3]);
                        } else if (i4 == 900) {
                            mode2 = Mode.ALPHA;
                        }
                        c = 0;
                        break;
                    }
                case 5:
                    if (i4 < 26) {
                        c = (char) (i4 + 65);
                    } else if (i4 != 26) {
                        if (i4 == 900) {
                            mode2 = Mode.ALPHA;
                            c = 0;
                            break;
                        }
                        c = 0;
                    }
                    mode2 = mode3;
                    break;
                case 6:
                    if (i4 < 29) {
                        c = PUNCT_CHARS[i4];
                        mode2 = mode3;
                        break;
                    } else {
                        if (i4 == 29) {
                            mode2 = Mode.ALPHA;
                        } else {
                            if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode2 = Mode.ALPHA;
                            }
                            c = 0;
                            mode2 = mode3;
                        }
                        c = 0;
                        break;
                    }
                default:
                    c = 0;
                    break;
            }
            if (c != 0) {
                sb.append(c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.pdf417.decoder.DecodedBitStreamParser$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode;

        static {
            int[] iArr = new int[Mode.values().length];
            $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = iArr;
            try {
                iArr[Mode.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.MIXED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.ALPHA_SHIFT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT_SHIFT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private static int byteCompaction(int i, int[] iArr, int i2, StringBuilder sb) {
        int i3;
        int i4;
        int i5 = 922;
        int i6 = 923;
        long j = 900;
        int i7 = 6;
        if (i != 901) {
            if (i == 924) {
                int i8 = i2;
                boolean z = false;
                int i9 = 0;
                long j2 = 0;
                while (i8 < iArr[0] && !z) {
                    int i10 = i8 + 1;
                    int i11 = iArr[i8];
                    if (i11 < 900) {
                        i9++;
                        j2 = (j2 * 900) + i11;
                    } else if (i11 == 900 || i11 == 901 || i11 == 902 || i11 == 924 || i11 == 928 || i11 == i6 || i11 == i5) {
                        i8 = i10 - 1;
                        z = true;
                        if (i9 % 5 != 0 && i9 > 0) {
                            char[] cArr = new char[6];
                            for (int i12 = 0; i12 < 6; i12++) {
                                cArr[5 - i12] = (char) (j2 & 255);
                                j2 >>= 8;
                            }
                            sb.append(cArr);
                            i9 = 0;
                        }
                        i5 = 922;
                        i6 = 923;
                    }
                    i8 = i10;
                    if (i9 % 5 != 0) {
                    }
                    i5 = 922;
                    i6 = 923;
                }
                return i8;
            }
            return i2;
        }
        char[] cArr2 = new char[6];
        int[] iArr2 = new int[6];
        int i13 = i2 + 1;
        boolean z2 = false;
        int i14 = 0;
        int i15 = iArr[i2];
        long j3 = 0;
        while (true) {
            i3 = iArr[0];
            if (i13 >= i3 || z2) {
                break;
            }
            int i16 = i14 + 1;
            iArr2[i14] = i15;
            j3 = (j3 * j) + i15;
            int i17 = i13 + 1;
            i15 = iArr[i13];
            if (i15 == 900 || i15 == 901 || i15 == 902 || i15 == 924 || i15 == 928 || i15 == 923 || i15 == 922) {
                i13 = i17 - 1;
                i15 = i15;
                i14 = i16;
                j = 900;
                i7 = 6;
                z2 = true;
            } else {
                if (i16 % 5 != 0 || i16 <= 0) {
                    i15 = i15;
                    i14 = i16;
                    i13 = i17;
                } else {
                    int i18 = 0;
                    while (i18 < i7) {
                        cArr2[5 - i18] = (char) (j3 % 256);
                        j3 >>= 8;
                        i18++;
                        i15 = i15;
                        i7 = 6;
                    }
                    sb.append(cArr2);
                    i13 = i17;
                    i14 = 0;
                }
                j = 900;
                i7 = 6;
            }
        }
        if (i13 != i3 || i15 >= 900) {
            i4 = i14;
        } else {
            i4 = i14 + 1;
            iArr2[i14] = i15;
        }
        for (int i19 = 0; i19 < i4; i19++) {
            sb.append((char) iArr2[i19]);
        }
        return i13;
    }

    private static int numericCompaction(int[] iArr, int i, StringBuilder sb) throws FormatException {
        int[] iArr2 = new int[15];
        boolean z = false;
        int i2 = 0;
        while (true) {
            int i3 = iArr[0];
            if (i >= i3 || z) {
                break;
            }
            int i4 = i + 1;
            int i5 = iArr[i];
            if (i4 == i3) {
                z = true;
            }
            if (i5 < 900) {
                iArr2[i2] = i5;
                i2++;
            } else if (i5 == 900 || i5 == 901 || i5 == 924 || i5 == 928 || i5 == 923 || i5 == 922) {
                i4--;
                z = true;
            }
            if (i2 % 15 == 0 || i5 == 902 || z) {
                sb.append(decodeBase900toBase10(iArr2, i2));
                i2 = 0;
            }
            i = i4;
        }
        return i;
    }

    private static String decodeBase900toBase10(int[] iArr, int i) throws FormatException {
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i2 = 0; i2 < i; i2++) {
            bigInteger = bigInteger.add(EXP900[(i - i2) - 1].multiply(BigInteger.valueOf(iArr[i2])));
        }
        String bigInteger2 = bigInteger.toString();
        if (bigInteger2.charAt(0) != '1') {
            throw FormatException.getFormatInstance();
        }
        return bigInteger2.substring(1);
    }
}
