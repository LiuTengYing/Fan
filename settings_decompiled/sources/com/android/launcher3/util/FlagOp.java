package com.android.launcher3.util;
/* loaded from: classes.dex */
public interface FlagOp {
    public static final FlagOp NO_OP = new FlagOp() { // from class: com.android.launcher3.util.FlagOp$$ExternalSyntheticLambda0
        @Override // com.android.launcher3.util.FlagOp
        public final int apply(int i) {
            int lambda$static$0;
            lambda$static$0 = FlagOp.lambda$static$0(i);
            return lambda$static$0;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    static /* synthetic */ int lambda$static$0(int i) {
        return i;
    }

    int apply(int i);

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default int lambda$addFlag$1(int i, int i2) {
        return apply(i2) | i;
    }

    default FlagOp addFlag(final int i) {
        return new FlagOp() { // from class: com.android.launcher3.util.FlagOp$$ExternalSyntheticLambda1
            @Override // com.android.launcher3.util.FlagOp
            public final int apply(int i2) {
                int lambda$addFlag$1;
                lambda$addFlag$1 = FlagOp.this.lambda$addFlag$1(i, i2);
                return lambda$addFlag$1;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default int lambda$removeFlag$2(int i, int i2) {
        return apply(i2) & (~i);
    }

    default FlagOp removeFlag(final int i) {
        return new FlagOp() { // from class: com.android.launcher3.util.FlagOp$$ExternalSyntheticLambda2
            @Override // com.android.launcher3.util.FlagOp
            public final int apply(int i2) {
                int lambda$removeFlag$2;
                lambda$removeFlag$2 = FlagOp.this.lambda$removeFlag$2(i, i2);
                return lambda$removeFlag$2;
            }
        };
    }

    default FlagOp setFlag(int i, boolean z) {
        return z ? addFlag(i) : removeFlag(i);
    }
}
