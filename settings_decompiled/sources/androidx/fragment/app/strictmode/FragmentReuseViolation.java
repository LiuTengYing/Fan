package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class FragmentReuseViolation extends Violation {
    private final String mPreviousWho;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentReuseViolation(Fragment fragment, String str) {
        super(fragment);
        this.mPreviousWho = str;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to reuse fragment " + this.mFragment + " with previous ID " + this.mPreviousWho;
    }
}
