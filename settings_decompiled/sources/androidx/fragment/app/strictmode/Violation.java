package androidx.fragment.app.strictmode;

import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public abstract class Violation extends RuntimeException {
    final Fragment mFragment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Violation(Fragment fragment) {
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return this.mFragment;
    }
}
