package androidx.fragment.app.strictmode;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class FragmentTagUsageViolation extends Violation {
    private final ViewGroup mContainer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentTagUsageViolation(Fragment fragment, ViewGroup viewGroup) {
        super(fragment);
        this.mContainer = viewGroup;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to use <fragment> tag to add fragment " + this.mFragment + " to container " + this.mContainer;
    }
}
