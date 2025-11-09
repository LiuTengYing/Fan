package androidx.slice.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.lifecycle.LiveData;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.SliceSpecs;
import androidx.slice.SliceViewManager;
import androidx.slice.widget.SliceLiveData;
import java.util.Arrays;
import java.util.Set;
/* loaded from: classes.dex */
public final class SliceLiveData {
    public static final SliceSpec OLD_BASIC;
    public static final SliceSpec OLD_LIST;
    public static final Set<SliceSpec> SUPPORTED_SPECS;

    /* loaded from: classes.dex */
    public interface OnErrorListener {
        void onSliceError(int i, Throwable th);
    }

    static {
        SliceSpec sliceSpec = new SliceSpec("androidx.app.slice.BASIC", 1);
        OLD_BASIC = sliceSpec;
        SliceSpec sliceSpec2 = new SliceSpec("androidx.app.slice.LIST", 1);
        OLD_LIST = sliceSpec2;
        SUPPORTED_SPECS = new ArraySet(Arrays.asList(SliceSpecs.BASIC, SliceSpecs.LIST, SliceSpecs.LIST_V2, sliceSpec, sliceSpec2));
    }

    public static LiveData<Slice> fromUri(Context context, Uri uri, OnErrorListener onErrorListener) {
        return new SliceLiveDataImpl(context.getApplicationContext(), uri, onErrorListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SliceLiveDataImpl extends LiveData<Slice> {
        final OnErrorListener mListener;
        final SliceViewManager mSliceViewManager;
        Uri mUri;
        private final Runnable mUpdateSlice = new Runnable() { // from class: androidx.slice.widget.SliceLiveData.SliceLiveDataImpl.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SliceLiveDataImpl sliceLiveDataImpl = SliceLiveDataImpl.this;
                    Uri uri = sliceLiveDataImpl.mUri;
                    Slice bindSlice = uri != null ? sliceLiveDataImpl.mSliceViewManager.bindSlice(uri) : sliceLiveDataImpl.mSliceViewManager.bindSlice(sliceLiveDataImpl.mIntent);
                    SliceLiveDataImpl sliceLiveDataImpl2 = SliceLiveDataImpl.this;
                    if (sliceLiveDataImpl2.mUri == null && bindSlice != null) {
                        sliceLiveDataImpl2.mUri = bindSlice.getUri();
                        SliceLiveDataImpl sliceLiveDataImpl3 = SliceLiveDataImpl.this;
                        sliceLiveDataImpl3.mSliceViewManager.registerSliceCallback(sliceLiveDataImpl3.mUri, sliceLiveDataImpl3.mSliceCallback);
                    }
                    SliceLiveDataImpl.this.postValue(bindSlice);
                } catch (IllegalArgumentException e) {
                    SliceLiveDataImpl.this.onSliceError(3, e);
                    SliceLiveDataImpl.this.postValue(null);
                } catch (Exception e2) {
                    SliceLiveDataImpl.this.onSliceError(0, e2);
                    SliceLiveDataImpl.this.postValue(null);
                }
            }
        };
        final SliceViewManager.SliceCallback mSliceCallback = new SliceViewManager.SliceCallback() { // from class: androidx.slice.widget.SliceLiveData$SliceLiveDataImpl$$ExternalSyntheticLambda0
            @Override // androidx.slice.SliceViewManager.SliceCallback
            public final void onSliceUpdated(Slice slice) {
                SliceLiveData.SliceLiveDataImpl.this.lambda$new$0(slice);
            }
        };
        final Intent mIntent = null;

        SliceLiveDataImpl(Context context, Uri uri, OnErrorListener onErrorListener) {
            this.mSliceViewManager = SliceViewManager.getInstance(context);
            this.mUri = uri;
            this.mListener = onErrorListener;
        }

        @Override // androidx.lifecycle.LiveData
        protected void onActive() {
            AsyncTask.execute(this.mUpdateSlice);
            Uri uri = this.mUri;
            if (uri != null) {
                this.mSliceViewManager.registerSliceCallback(uri, this.mSliceCallback);
            }
        }

        @Override // androidx.lifecycle.LiveData
        protected void onInactive() {
            Uri uri = this.mUri;
            if (uri != null) {
                this.mSliceViewManager.unregisterSliceCallback(uri, this.mSliceCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(Slice slice) {
            postValue(slice);
        }

        void onSliceError(int i, Throwable th) {
            OnErrorListener onErrorListener = this.mListener;
            if (onErrorListener != null) {
                onErrorListener.onSliceError(i, th);
            } else {
                Log.e("SliceLiveData", "Error binding slice", th);
            }
        }
    }
}
