package androidx.slice;

import android.app.PendingIntent;
import android.app.slice.Slice;
import android.app.slice.SliceManager;
import android.app.slice.SliceSpec;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Process;
import android.util.Log;
import androidx.core.app.CoreComponentFactory;
import androidx.slice.SliceConvert;
import androidx.slice.SliceProvider;
import androidx.slice.compat.CompatPermissionManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class SliceProvider extends ContentProvider implements CoreComponentFactory.CompatWrapped {
    private static Clock sClock;
    private static Set<SliceSpec> sSpecs;
    private String[] mAuthorities;
    private String mAuthority;
    private final String[] mAutoGrantPermissions;
    private final Object mCompatLock;
    private Context mContext;
    private List<Uri> mPinnedSliceUris;
    private final Object mPinnedSliceUrisLock;

    @Override // android.content.ContentProvider
    public final int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Uri canonicalize(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public final String getType(Uri uri) {
        return "vnd.android.slice";
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public abstract Slice onBindSlice(Uri uri);

    public PendingIntent onCreatePermissionRequest(Uri uri, String str) {
        return null;
    }

    public abstract boolean onCreateSliceProvider();

    public void onSlicePinned(Uri uri) {
    }

    public void onSliceUnpinned(Uri uri) {
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public SliceProvider(String... strArr) {
        this.mContext = null;
        this.mCompatLock = new Object();
        this.mPinnedSliceUrisLock = new Object();
        this.mAutoGrantPermissions = strArr;
    }

    public SliceProvider() {
        this.mContext = null;
        this.mCompatLock = new Object();
        this.mPinnedSliceUrisLock = new Object();
        this.mAutoGrantPermissions = new String[0];
    }

    @Override // androidx.core.app.CoreComponentFactory.CompatWrapped
    public Object getWrapper() {
        final String[] strArr = this.mAutoGrantPermissions;
        return new android.app.slice.SliceProvider(this, strArr) { // from class: androidx.slice.compat.SliceProviderWrapperContainer$SliceProviderWrapper
            private String[] mAutoGrantPermissions;
            private SliceManager mSliceManager;
            private SliceProvider mSliceProvider;

            @Override // android.content.ContentProvider
            public boolean onCreate() {
                return true;
            }

            {
                super(strArr);
                this.mAutoGrantPermissions = (strArr == null || strArr.length == 0) ? null : null;
                this.mSliceProvider = this;
            }

            @Override // android.app.slice.SliceProvider, android.content.ContentProvider
            public void attachInfo(Context context, ProviderInfo providerInfo) {
                this.mSliceProvider.attachInfo(context, providerInfo);
                super.attachInfo(context, providerInfo);
                this.mSliceManager = (SliceManager) context.getSystemService(SliceManager.class);
            }

            @Override // android.app.slice.SliceProvider
            public PendingIntent onCreatePermissionRequest(Uri uri) {
                if (this.mAutoGrantPermissions != null) {
                    checkPermissions(uri);
                }
                PendingIntent onCreatePermissionRequest = this.mSliceProvider.onCreatePermissionRequest(uri, getCallingPackage());
                return onCreatePermissionRequest != null ? onCreatePermissionRequest : super.onCreatePermissionRequest(uri);
            }

            @Override // android.app.slice.SliceProvider, android.content.ContentProvider
            public Bundle call(String str, String str2, Bundle bundle) {
                Intent intent;
                if (this.mAutoGrantPermissions != null) {
                    Uri uri = null;
                    if ("bind_slice".equals(str)) {
                        if (bundle != null) {
                            uri = (Uri) bundle.getParcelable("slice_uri");
                        }
                    } else if ("map_slice".equals(str) && (intent = (Intent) bundle.getParcelable("slice_intent")) != null) {
                        uri = onMapIntentToUri(intent);
                    }
                    if (uri != null && this.mSliceManager.checkSlicePermission(uri, Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
                        checkPermissions(uri);
                    }
                }
                if ("androidx.remotecallback.method.PROVIDER_CALLBACK".equals(str)) {
                    return this.mSliceProvider.call(str, str2, bundle);
                }
                return super.call(str, str2, bundle);
            }

            private void checkPermissions(Uri uri) {
                String[] strArr2;
                if (uri != null) {
                    for (String str : this.mAutoGrantPermissions) {
                        if (getContext().checkCallingPermission(str) == 0) {
                            this.mSliceManager.grantSlicePermission(str, uri);
                            getContext().getContentResolver().notifyChange(uri, null);
                            return;
                        }
                    }
                }
            }

            @Override // android.app.slice.SliceProvider
            public Slice onBindSlice(Uri uri, Set<SliceSpec> set) {
                SliceProvider.setSpecs(SliceConvert.wrap(set));
                try {
                    return SliceConvert.unwrap(this.mSliceProvider.onBindSlice(uri));
                } catch (Exception e) {
                    Log.wtf("SliceProviderWrapper", "Slice with URI " + uri.toString() + " is invalid.", e);
                    return null;
                } finally {
                    SliceProvider.setSpecs(null);
                }
            }

            @Override // android.app.slice.SliceProvider
            public void onSlicePinned(Uri uri) {
                this.mSliceProvider.onSlicePinned(uri);
                this.mSliceProvider.handleSlicePinned(uri);
            }

            @Override // android.app.slice.SliceProvider
            public void onSliceUnpinned(Uri uri) {
                this.mSliceProvider.onSliceUnpinned(uri);
                this.mSliceProvider.handleSliceUnpinned(uri);
            }

            @Override // android.app.slice.SliceProvider
            public Collection<Uri> onGetSliceDescendants(Uri uri) {
                return this.mSliceProvider.onGetSliceDescendants(uri);
            }

            @Override // android.app.slice.SliceProvider
            public Uri onMapIntentToUri(Intent intent) {
                return this.mSliceProvider.onMapIntentToUri(intent);
            }
        };
    }

    @Override // android.content.ContentProvider
    public final boolean onCreate() {
        return onCreateSliceProvider();
    }

    protected CompatPermissionManager onCreatePermissionManager(String[] strArr) {
        Context context = getContext();
        return new CompatPermissionManager(context, "slice_perms_" + getClass().getName(), Process.myUid(), strArr);
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo providerInfo) {
        super.attachInfo(context, providerInfo);
        if (this.mContext == null) {
            this.mContext = context;
            if (providerInfo != null) {
                setAuthorities(providerInfo.authority);
            }
        }
    }

    private void setAuthorities(String str) {
        if (str != null) {
            if (str.indexOf(59) == -1) {
                this.mAuthority = str;
                this.mAuthorities = null;
                return;
            }
            this.mAuthority = null;
            this.mAuthorities = str.split(";");
        }
    }

    public void handleSlicePinned(Uri uri) {
        List<Uri> pinnedSlices = getPinnedSlices();
        if (pinnedSlices.contains(uri)) {
            return;
        }
        pinnedSlices.add(uri);
    }

    public void handleSliceUnpinned(Uri uri) {
        List<Uri> pinnedSlices = getPinnedSlices();
        if (pinnedSlices.contains(uri)) {
            pinnedSlices.remove(uri);
        }
    }

    public Uri onMapIntentToUri(Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }

    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        return Collections.emptyList();
    }

    public List<Uri> getPinnedSlices() {
        synchronized (this.mPinnedSliceUrisLock) {
            if (this.mPinnedSliceUris == null) {
                this.mPinnedSliceUris = new ArrayList(SliceManager.getInstance(getContext()).getPinnedSlices());
            }
        }
        return this.mPinnedSliceUris;
    }

    public static void setSpecs(Set<SliceSpec> set) {
        sSpecs = set;
    }

    public static Set<SliceSpec> getCurrentSpecs() {
        return sSpecs;
    }

    public static Clock getClock() {
        return sClock;
    }
}
