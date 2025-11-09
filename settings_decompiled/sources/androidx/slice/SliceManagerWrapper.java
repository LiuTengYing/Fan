package androidx.slice;

import android.content.Context;
import android.net.Uri;
import java.util.List;
import java.util.Set;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SliceManagerWrapper extends SliceManager {
    private final android.app.slice.SliceManager mManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceManagerWrapper(Context context) {
        this((android.app.slice.SliceManager) context.getSystemService(android.app.slice.SliceManager.class));
    }

    SliceManagerWrapper(android.app.slice.SliceManager sliceManager) {
        this.mManager = sliceManager;
    }

    @Override // androidx.slice.SliceManager
    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(uri));
    }

    @Override // androidx.slice.SliceManager
    public List<Uri> getPinnedSlices() {
        return this.mManager.getPinnedSlices();
    }
}
