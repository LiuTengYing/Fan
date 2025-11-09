package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class GetRetainInstanceUsageViolation extends RetainInstanceUsageViolation {
    /* JADX INFO: Access modifiers changed from: package-private */
    public GetRetainInstanceUsageViolation(Fragment fragment) {
        super(fragment);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to get retain instance for fragment " + this.mFragment;
    }
}
