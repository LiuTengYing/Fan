package androidx.slice;

import android.content.Context;
import android.net.Uri;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class SliceManager {
    public abstract List<Uri> getPinnedSlices();

    public abstract Set<SliceSpec> getPinnedSpecs(Uri uri);

    public static SliceManager getInstance(Context context) {
        return new SliceManagerWrapper(context);
    }
}
