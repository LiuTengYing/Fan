package com.android.settings.accessibility;

import android.content.Context;
import java.lang.Number;
import java.util.List;
/* loaded from: classes.dex */
abstract class PreviewSizeData<T extends Number> {
    private final Context mContext;
    private T mDefaultValue;
    private int mInitialIndex;
    private List<T> mValues;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreviewSizeData(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<T> getValues() {
        return this.mValues;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setValues(List<T> list) {
        this.mValues = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public T getDefaultValue() {
        return this.mDefaultValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDefaultValue(T t) {
        this.mDefaultValue = t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getInitialIndex() {
        return this.mInitialIndex;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInitialIndex(int i) {
        this.mInitialIndex = i;
    }
}
