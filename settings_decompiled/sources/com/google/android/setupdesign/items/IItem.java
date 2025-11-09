package com.google.android.setupdesign.items;

import android.view.View;
/* loaded from: classes2.dex */
public interface IItem {
    int getLayoutResource();

    boolean isEnabled();

    void onBindView(View view);
}
