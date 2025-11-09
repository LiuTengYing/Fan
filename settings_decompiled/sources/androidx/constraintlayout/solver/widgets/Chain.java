package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Chain {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        int i2;
        int i3;
        ChainHead[] chainHeadArr;
        if (i == 0) {
            int i4 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i3 = i4;
            i2 = 0;
        } else {
            i2 = 2;
            i3 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i5 = 0; i5 < i3; i5++) {
            ChainHead chainHead = chainHeadArr[i5];
            chainHead.define();
            applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
        if (r7 == 2) goto L307;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x003c, code lost:
        if (r7 == 2) goto L307;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x003e, code lost:
        r5 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0040, code lost:
        r5 = false;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:104:0x018e  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x01a8  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x01c5  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x024c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:154:0x02a5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:213:0x0392  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x039d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:226:0x03b0  */
    /* JADX WARN: Removed duplicated region for block: B:272:0x0477  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x04ac  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x04bf A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:286:0x04d2  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x04d5  */
    /* JADX WARN: Removed duplicated region for block: B:290:0x04db  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x04de  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x04e2  */
    /* JADX WARN: Removed duplicated region for block: B:298:0x04f2  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x04f6 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:311:0x0393 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:321:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r38, androidx.constraintlayout.solver.LinearSystem r39, int r40, int r41, androidx.constraintlayout.solver.widgets.ChainHead r42) {
        /*
            Method dump skipped, instructions count: 1303
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Chain.applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer, androidx.constraintlayout.solver.LinearSystem, int, int, androidx.constraintlayout.solver.widgets.ChainHead):void");
    }
}
