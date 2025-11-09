package com.android.settings.slices;

import android.content.IntentFilter;
/* loaded from: classes.dex */
public interface Sliceable {
    default Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return null;
    }

    default IntentFilter getIntentFilter() {
        return null;
    }

    default int getSliceHighlightMenuRes() {
        return 0;
    }

    default boolean hasAsyncUpdate() {
        return false;
    }

    default boolean isPublicSlice() {
        return false;
    }

    default boolean isSliceable() {
        return false;
    }

    default boolean useDynamicSliceSummary() {
        return false;
    }
}
