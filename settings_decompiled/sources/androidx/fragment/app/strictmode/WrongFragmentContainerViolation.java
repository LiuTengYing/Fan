package androidx.fragment.app.strictmode;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public final class WrongFragmentContainerViolation extends Violation {
    private final ViewGroup mContainer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WrongFragmentContainerViolation(Fragment fragment, ViewGroup viewGroup) {
        super(fragment);
        this.mContainer = viewGroup;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Attempting to add fragment " + this.mFragment + " to container " + this.mContainer + " which is not a FragmentContainerView";
    }
}
