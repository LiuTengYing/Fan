package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class FinderPatternFinder {
    private boolean hasSkipped;
    private final BitMatrix image;
    private final ResultPointCallback resultPointCallback;
    private final List<FinderPattern> possibleCenters = new ArrayList();
    private final int[] crossCheckStateCount = new int[5];

    public FinderPatternFinder(BitMatrix bitMatrix, ResultPointCallback resultPointCallback) {
        this.image = bitMatrix;
        this.resultPointCallback = resultPointCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final FinderPatternInfo find(Map<DecodeHintType, ?> map) throws NotFoundException {
        boolean z = map != null && map.containsKey(DecodeHintType.TRY_HARDER);
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int i = (height * 3) / 228;
        if (i < 3 || z) {
            i = 3;
        }
        int[] iArr = new int[5];
        int i2 = i - 1;
        boolean z2 = false;
        while (i2 < height && !z2) {
            iArr[0] = 0;
            iArr[1] = 0;
            iArr[2] = 0;
            iArr[3] = 0;
            iArr[4] = 0;
            int i3 = 0;
            int i4 = 0;
            while (i3 < width) {
                if (this.image.get(i3, i2)) {
                    if ((i4 & 1) == 1) {
                        i4++;
                    }
                    iArr[i4] = iArr[i4] + 1;
                } else if ((i4 & 1) != 0) {
                    iArr[i4] = iArr[i4] + 1;
                } else if (i4 == 4) {
                    if (!foundPatternCross(iArr)) {
                        iArr[0] = iArr[2];
                        iArr[1] = iArr[3];
                        iArr[2] = iArr[4];
                        iArr[3] = 1;
                        iArr[4] = 0;
                    } else if (handlePossibleCenter(iArr, i2, i3)) {
                        if (this.hasSkipped) {
                            z2 = haveMultiplyConfirmedCenters();
                        } else {
                            int findRowSkip = findRowSkip();
                            int i5 = iArr[2];
                            if (findRowSkip > i5) {
                                i2 += (findRowSkip - i5) - 2;
                                i3 = width - 1;
                            }
                        }
                        iArr[0] = 0;
                        iArr[1] = 0;
                        iArr[2] = 0;
                        iArr[3] = 0;
                        iArr[4] = 0;
                        i4 = 0;
                        i = 2;
                    } else {
                        iArr[0] = iArr[2];
                        iArr[1] = iArr[3];
                        iArr[2] = iArr[4];
                        iArr[3] = 1;
                        iArr[4] = 0;
                    }
                    i4 = 3;
                } else {
                    i4++;
                    iArr[i4] = iArr[i4] + 1;
                }
                i3++;
            }
            if (foundPatternCross(iArr) && handlePossibleCenter(iArr, i2, width)) {
                i = iArr[0];
                if (this.hasSkipped) {
                    z2 = haveMultiplyConfirmedCenters();
                }
            }
            i2 += i;
        }
        FinderPattern[] selectBestPatterns = selectBestPatterns();
        ResultPoint.orderBestPatterns(selectBestPatterns);
        return new FinderPatternInfo(selectBestPatterns);
    }

    private static float centerFromEnd(int[] iArr, int i) {
        return ((i - iArr[4]) - iArr[3]) - (iArr[2] / 2.0f);
    }

    protected static boolean foundPatternCross(int[] iArr) {
        int i;
        int i2;
        int i3 = 0;
        for (int i4 = 0; i4 < 5; i4++) {
            int i5 = iArr[i4];
            if (i5 == 0) {
                return false;
            }
            i3 += i5;
        }
        return i3 >= 7 && Math.abs(i - (iArr[0] << 8)) < (i2 = (i = (i3 << 8) / 7) / 2) && Math.abs(i - (iArr[1] << 8)) < i2 && Math.abs((i * 3) - (iArr[2] << 8)) < i2 * 3 && Math.abs(i - (iArr[3] << 8)) < i2 && Math.abs(i - (iArr[4] << 8)) < i2;
    }

    private int[] getCrossCheckStateCount() {
        int[] iArr = this.crossCheckStateCount;
        iArr[0] = 0;
        iArr[1] = 0;
        iArr[2] = 0;
        iArr[3] = 0;
        iArr[4] = 0;
        return iArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x0082, code lost:
        if (r9[3] < r12) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0086, code lost:
        if (r10 >= r1) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x008c, code lost:
        if (r0.get(r11, r10) == false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x008e, code lost:
        r8 = r9[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0090, code lost:
        if (r8 >= r12) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0092, code lost:
        r9[4] = r8 + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0099, code lost:
        r11 = r9[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x009b, code lost:
        if (r11 < r12) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x009d, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00b2, code lost:
        if ((java.lang.Math.abs(((((r9[0] + r9[1]) + r9[2]) + r9[3]) + r11) - r13) * 5) < (r13 * 2)) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00b4, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00b9, code lost:
        if (foundPatternCross(r9) == false) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00bf, code lost:
        return centerFromEnd(r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckVertical(int r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            com.google.zxing.common.BitMatrix r0 = r9.image
            int r1 = r0.getHeight()
            int[] r9 = r9.getCrossCheckStateCount()
            r2 = r10
        Lb:
            r3 = 2
            r4 = 1
            if (r2 < 0) goto L1d
            boolean r5 = r0.get(r11, r2)
            if (r5 == 0) goto L1d
            r5 = r9[r3]
            int r5 = r5 + r4
            r9[r3] = r5
            int r2 = r2 + (-1)
            goto Lb
        L1d:
            r5 = 2143289344(0x7fc00000, float:NaN)
            if (r2 >= 0) goto L22
            return r5
        L22:
            if (r2 < 0) goto L35
            boolean r6 = r0.get(r11, r2)
            if (r6 != 0) goto L35
            r6 = r9[r4]
            if (r6 > r12) goto L35
            int r6 = r6 + 1
            r9[r4] = r6
            int r2 = r2 + (-1)
            goto L22
        L35:
            if (r2 < 0) goto Lbf
            r6 = r9[r4]
            if (r6 <= r12) goto L3d
            goto Lbf
        L3d:
            r6 = 0
            if (r2 < 0) goto L51
            boolean r7 = r0.get(r11, r2)
            if (r7 == 0) goto L51
            r7 = r9[r6]
            if (r7 > r12) goto L51
            int r7 = r7 + 1
            r9[r6] = r7
            int r2 = r2 + (-1)
            goto L3d
        L51:
            r2 = r9[r6]
            if (r2 <= r12) goto L56
            return r5
        L56:
            int r10 = r10 + r4
        L57:
            if (r10 >= r1) goto L67
            boolean r2 = r0.get(r11, r10)
            if (r2 == 0) goto L67
            r2 = r9[r3]
            int r2 = r2 + r4
            r9[r3] = r2
            int r10 = r10 + 1
            goto L57
        L67:
            if (r10 != r1) goto L6a
            return r5
        L6a:
            r2 = 3
            if (r10 >= r1) goto L7e
            boolean r7 = r0.get(r11, r10)
            if (r7 != 0) goto L7e
            r7 = r9[r2]
            if (r7 >= r12) goto L7e
            int r7 = r7 + 1
            r9[r2] = r7
            int r10 = r10 + 1
            goto L6a
        L7e:
            if (r10 == r1) goto Lbf
            r7 = r9[r2]
            if (r7 < r12) goto L85
            goto Lbf
        L85:
            r7 = 4
            if (r10 >= r1) goto L99
            boolean r8 = r0.get(r11, r10)
            if (r8 == 0) goto L99
            r8 = r9[r7]
            if (r8 >= r12) goto L99
            int r8 = r8 + 1
            r9[r7] = r8
            int r10 = r10 + 1
            goto L85
        L99:
            r11 = r9[r7]
            if (r11 < r12) goto L9e
            return r5
        L9e:
            r12 = r9[r6]
            r0 = r9[r4]
            int r12 = r12 + r0
            r0 = r9[r3]
            int r12 = r12 + r0
            r0 = r9[r2]
            int r12 = r12 + r0
            int r12 = r12 + r11
            int r12 = r12 - r13
            int r11 = java.lang.Math.abs(r12)
            int r11 = r11 * 5
            int r13 = r13 * r3
            if (r11 < r13) goto Lb5
            return r5
        Lb5:
            boolean r11 = foundPatternCross(r9)
            if (r11 == 0) goto Lbf
            float r5 = centerFromEnd(r9, r10)
        Lbf:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x0082, code lost:
        if (r9[3] < r12) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0086, code lost:
        if (r10 >= r1) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x008c, code lost:
        if (r0.get(r10, r11) == false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x008e, code lost:
        r8 = r9[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0090, code lost:
        if (r8 >= r12) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0092, code lost:
        r9[4] = r8 + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0099, code lost:
        r11 = r9[4];
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x009b, code lost:
        if (r11 < r12) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x009d, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00b1, code lost:
        if ((java.lang.Math.abs(((((r9[0] + r9[1]) + r9[2]) + r9[3]) + r11) - r13) * 5) < r13) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00b3, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00b8, code lost:
        if (foundPatternCross(r9) == false) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00be, code lost:
        return centerFromEnd(r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckHorizontal(int r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            com.google.zxing.common.BitMatrix r0 = r9.image
            int r1 = r0.getWidth()
            int[] r9 = r9.getCrossCheckStateCount()
            r2 = r10
        Lb:
            r3 = 2
            r4 = 1
            if (r2 < 0) goto L1d
            boolean r5 = r0.get(r2, r11)
            if (r5 == 0) goto L1d
            r5 = r9[r3]
            int r5 = r5 + r4
            r9[r3] = r5
            int r2 = r2 + (-1)
            goto Lb
        L1d:
            r5 = 2143289344(0x7fc00000, float:NaN)
            if (r2 >= 0) goto L22
            return r5
        L22:
            if (r2 < 0) goto L35
            boolean r6 = r0.get(r2, r11)
            if (r6 != 0) goto L35
            r6 = r9[r4]
            if (r6 > r12) goto L35
            int r6 = r6 + 1
            r9[r4] = r6
            int r2 = r2 + (-1)
            goto L22
        L35:
            if (r2 < 0) goto Lbe
            r6 = r9[r4]
            if (r6 <= r12) goto L3d
            goto Lbe
        L3d:
            r6 = 0
            if (r2 < 0) goto L51
            boolean r7 = r0.get(r2, r11)
            if (r7 == 0) goto L51
            r7 = r9[r6]
            if (r7 > r12) goto L51
            int r7 = r7 + 1
            r9[r6] = r7
            int r2 = r2 + (-1)
            goto L3d
        L51:
            r2 = r9[r6]
            if (r2 <= r12) goto L56
            return r5
        L56:
            int r10 = r10 + r4
        L57:
            if (r10 >= r1) goto L67
            boolean r2 = r0.get(r10, r11)
            if (r2 == 0) goto L67
            r2 = r9[r3]
            int r2 = r2 + r4
            r9[r3] = r2
            int r10 = r10 + 1
            goto L57
        L67:
            if (r10 != r1) goto L6a
            return r5
        L6a:
            r2 = 3
            if (r10 >= r1) goto L7e
            boolean r7 = r0.get(r10, r11)
            if (r7 != 0) goto L7e
            r7 = r9[r2]
            if (r7 >= r12) goto L7e
            int r7 = r7 + 1
            r9[r2] = r7
            int r10 = r10 + 1
            goto L6a
        L7e:
            if (r10 == r1) goto Lbe
            r7 = r9[r2]
            if (r7 < r12) goto L85
            goto Lbe
        L85:
            r7 = 4
            if (r10 >= r1) goto L99
            boolean r8 = r0.get(r10, r11)
            if (r8 == 0) goto L99
            r8 = r9[r7]
            if (r8 >= r12) goto L99
            int r8 = r8 + 1
            r9[r7] = r8
            int r10 = r10 + 1
            goto L85
        L99:
            r11 = r9[r7]
            if (r11 < r12) goto L9e
            return r5
        L9e:
            r12 = r9[r6]
            r0 = r9[r4]
            int r12 = r12 + r0
            r0 = r9[r3]
            int r12 = r12 + r0
            r0 = r9[r2]
            int r12 = r12 + r0
            int r12 = r12 + r11
            int r12 = r12 - r13
            int r11 = java.lang.Math.abs(r12)
            int r11 = r11 * 5
            if (r11 < r13) goto Lb4
            return r5
        Lb4:
            boolean r11 = foundPatternCross(r9)
            if (r11 == 0) goto Lbe
            float r5 = centerFromEnd(r9, r10)
        Lbe:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckHorizontal(int, int, int, int):float");
    }

    protected final boolean handlePossibleCenter(int[] iArr, int i, int i2) {
        boolean z = false;
        int i3 = iArr[0] + iArr[1] + iArr[2] + iArr[3] + iArr[4];
        int centerFromEnd = (int) centerFromEnd(iArr, i2);
        float crossCheckVertical = crossCheckVertical(i, centerFromEnd, iArr[2], i3);
        if (!Float.isNaN(crossCheckVertical)) {
            float crossCheckHorizontal = crossCheckHorizontal(centerFromEnd, (int) crossCheckVertical, iArr[2], i3);
            if (!Float.isNaN(crossCheckHorizontal)) {
                float f = i3 / 7.0f;
                int i4 = 0;
                while (true) {
                    if (i4 >= this.possibleCenters.size()) {
                        break;
                    }
                    FinderPattern finderPattern = this.possibleCenters.get(i4);
                    if (finderPattern.aboutEquals(f, crossCheckVertical, crossCheckHorizontal)) {
                        this.possibleCenters.set(i4, finderPattern.combineEstimate(crossCheckVertical, crossCheckHorizontal, f));
                        z = true;
                        break;
                    }
                    i4++;
                }
                if (!z) {
                    FinderPattern finderPattern2 = new FinderPattern(crossCheckHorizontal, crossCheckVertical, f);
                    this.possibleCenters.add(finderPattern2);
                    ResultPointCallback resultPointCallback = this.resultPointCallback;
                    if (resultPointCallback != null) {
                        resultPointCallback.foundPossibleResultPoint(finderPattern2);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int findRowSkip() {
        if (this.possibleCenters.size() <= 1) {
            return 0;
        }
        FinderPattern finderPattern = null;
        for (FinderPattern finderPattern2 : this.possibleCenters) {
            if (finderPattern2.getCount() >= 2) {
                if (finderPattern != null) {
                    this.hasSkipped = true;
                    return ((int) (Math.abs(finderPattern.getX() - finderPattern2.getX()) - Math.abs(finderPattern.getY() - finderPattern2.getY()))) / 2;
                }
                finderPattern = finderPattern2;
            }
        }
        return 0;
    }

    private boolean haveMultiplyConfirmedCenters() {
        int size = this.possibleCenters.size();
        float f = 0.0f;
        float f2 = 0.0f;
        int i = 0;
        for (FinderPattern finderPattern : this.possibleCenters) {
            if (finderPattern.getCount() >= 2) {
                i++;
                f2 += finderPattern.getEstimatedModuleSize();
            }
        }
        if (i < 3) {
            return false;
        }
        float f3 = f2 / size;
        for (FinderPattern finderPattern2 : this.possibleCenters) {
            f += Math.abs(finderPattern2.getEstimatedModuleSize() - f3);
        }
        return f <= f2 * 0.05f;
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        float f;
        int size = this.possibleCenters.size();
        if (size < 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        float f2 = 0.0f;
        if (size > 3) {
            float f3 = 0.0f;
            float f4 = 0.0f;
            for (FinderPattern finderPattern : this.possibleCenters) {
                float estimatedModuleSize = finderPattern.getEstimatedModuleSize();
                f3 += estimatedModuleSize;
                f4 += estimatedModuleSize * estimatedModuleSize;
            }
            float f5 = f3 / size;
            float sqrt = (float) Math.sqrt((f4 / f) - (f5 * f5));
            Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(f5));
            float max = Math.max(0.2f * f5, sqrt);
            int i = 0;
            while (i < this.possibleCenters.size() && this.possibleCenters.size() > 3) {
                if (Math.abs(this.possibleCenters.get(i).getEstimatedModuleSize() - f5) > max) {
                    this.possibleCenters.remove(i);
                    i--;
                }
                i++;
            }
        }
        if (this.possibleCenters.size() > 3) {
            for (FinderPattern finderPattern2 : this.possibleCenters) {
                f2 += finderPattern2.getEstimatedModuleSize();
            }
            Collections.sort(this.possibleCenters, new CenterComparator(f2 / this.possibleCenters.size()));
            List<FinderPattern> list = this.possibleCenters;
            list.subList(3, list.size()).clear();
        }
        return new FinderPattern[]{this.possibleCenters.get(0), this.possibleCenters.get(1), this.possibleCenters.get(2)};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class FurthestFromAverageComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private FurthestFromAverageComparator(float f) {
            this.average = f;
        }

        @Override // java.util.Comparator
        public int compare(FinderPattern finderPattern, FinderPattern finderPattern2) {
            float abs = Math.abs(finderPattern2.getEstimatedModuleSize() - this.average);
            float abs2 = Math.abs(finderPattern.getEstimatedModuleSize() - this.average);
            if (abs < abs2) {
                return -1;
            }
            return abs == abs2 ? 0 : 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class CenterComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private CenterComparator(float f) {
            this.average = f;
        }

        @Override // java.util.Comparator
        public int compare(FinderPattern finderPattern, FinderPattern finderPattern2) {
            if (finderPattern2.getCount() == finderPattern.getCount()) {
                float abs = Math.abs(finderPattern2.getEstimatedModuleSize() - this.average);
                float abs2 = Math.abs(finderPattern.getEstimatedModuleSize() - this.average);
                if (abs < abs2) {
                    return 1;
                }
                return abs == abs2 ? 0 : -1;
            }
            return finderPattern2.getCount() - finderPattern.getCount();
        }
    }
}
