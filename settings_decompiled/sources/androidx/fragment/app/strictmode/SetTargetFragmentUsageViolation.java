package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class SetTargetFragmentUsageViolation extends TargetFragmentUsageViolation {
    private final int mRequestCode;
    private final Fragment mTargetFragment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetTargetFragmentUsageViolation(Fragment fragment, Fragment fragment2, int i) {
        super(fragment);
        this.mTargetFragment = fragment2;
        this.mRequestCode = i;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to set target fragment " + this.mTargetFragment + " with request code " + this.mRequestCode + " for fragment " + this.mFragment;
    }
}
