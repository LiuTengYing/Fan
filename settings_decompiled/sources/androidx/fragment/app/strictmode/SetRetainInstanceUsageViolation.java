package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class SetRetainInstanceUsageViolation extends RetainInstanceUsageViolation {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SetRetainInstanceUsageViolation(Fragment fragment) {
        super(fragment);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to set retain instance for fragment " + this.mFragment;
    }
}
