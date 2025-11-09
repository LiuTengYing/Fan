package com.android.settings.common.data;

import android.graphics.drawable.Drawable;
/* loaded from: classes.dex */
public class AppData {
    private String className;
    private boolean isAutoStart;
    private String labelName;
    private Drawable mIcon;
    private String mName;

    public Drawable getmIcon() {
        return this.mIcon;
    }

    public void setmIcon(Drawable drawable) {
        this.mIcon = drawable;
    }

    public String getmName() {
        return this.mName;
    }

    public void setmName(String str) {
        this.mName = str;
    }

    public boolean isAutoStart() {
        return this.isAutoStart;
    }

    public void setAutoStart(boolean z) {
        this.isAutoStart = z;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String str) {
        this.className = str;
    }

    public String getLabelName() {
        return this.labelName;
    }

    public void setLabelName(String str) {
        this.labelName = str;
    }

    public String toString() {
        return "AppData{mIcon=" + this.mIcon + ", mName='" + this.mName + "', className='" + this.className + "', labelName='" + this.labelName + "', isAutoStart=" + this.isAutoStart + '}';
    }
}
