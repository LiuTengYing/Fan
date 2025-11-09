package androidx.slice.compat;

import android.content.Context;
/* loaded from: classes.dex */
public class CompatPermissionManager {
    private final String[] mAutoGrantPermissions;
    private final Context mContext;
    private final int mMyUid;
    private final String mPrefsName;

    public CompatPermissionManager(Context context, String str, int i, String[] strArr) {
        this.mContext = context;
        this.mPrefsName = str;
        this.mMyUid = i;
        this.mAutoGrantPermissions = strArr;
    }
}
