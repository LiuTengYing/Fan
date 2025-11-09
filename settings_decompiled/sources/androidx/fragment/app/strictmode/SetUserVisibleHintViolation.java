package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class SetUserVisibleHintViolation extends Violation {
    private final boolean mIsVisibleToUser;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetUserVisibleHintViolation(Fragment fragment, boolean z) {
        super(fragment);
        this.mIsVisibleToUser = z;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to set user visible hint to " + this.mIsVisibleToUser + " for fragment " + this.mFragment;
    }
}
