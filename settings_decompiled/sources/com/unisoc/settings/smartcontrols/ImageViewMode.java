package com.unisoc.settings.smartcontrols;

import android.view.View;
/* loaded from: classes2.dex */
public class ImageViewMode {
    private int ImageId;
    private int summary;
    private int title;
    private View view;

    public ImageViewMode(View view, int i, int i2, int i3) {
        this.view = view;
        this.ImageId = i;
        this.title = i2;
        this.summary = i3;
    }

    public View getView() {
        return this.view;
    }

    public int getImageId() {
        return this.ImageId;
    }

    public int getTitle() {
        return this.title;
    }

    public int getSummary() {
        return this.summary;
    }
}
