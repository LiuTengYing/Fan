package com.android.settings.datetime.timezone.model;

import android.content.Context;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settingslib.utils.AsyncLoaderCompat;
/* loaded from: classes.dex */
public class TimeZoneDataLoader extends AsyncLoaderCompat<TimeZoneData> {

    /* loaded from: classes.dex */
    public interface OnDataReadyCallback {
        void onTimeZoneDataReady(TimeZoneData timeZoneData);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(TimeZoneData timeZoneData) {
    }

    public TimeZoneDataLoader(Context context) {
        super(context);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public TimeZoneData loadInBackground() {
        return TimeZoneData.getInstance();
    }

    /* loaded from: classes.dex */
    public static class LoaderCreator implements LoaderManager.LoaderCallbacks<TimeZoneData> {
        private final OnDataReadyCallback mCallback;
        private final Context mContext;

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<TimeZoneData> loader) {
        }

        public LoaderCreator(Context context, OnDataReadyCallback onDataReadyCallback) {
            this.mContext = context;
            this.mCallback = onDataReadyCallback;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<TimeZoneData> onCreateLoader(int i, Bundle bundle) {
            return new TimeZoneDataLoader(this.mContext);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<TimeZoneData> loader, TimeZoneData timeZoneData) {
            OnDataReadyCallback onDataReadyCallback = this.mCallback;
            if (onDataReadyCallback != null) {
                onDataReadyCallback.onTimeZoneDataReady(timeZoneData);
            }
        }
    }
}
