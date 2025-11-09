package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class GetTargetFragmentRequestCodeUsageViolation extends TargetFragmentUsageViolation {
    /* JADX INFO: Access modifiers changed from: package-private */
    public GetTargetFragmentRequestCodeUsageViolation(Fragment fragment) {
        super(fragment);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to get target request code from fragment " + this.mFragment;
    }
}
