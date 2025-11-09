package com.android.settings.utils;

import android.graphics.drawable.Drawable;
/* loaded from: classes.dex */
public class Info {
    private Drawable appIcon;
    private String appName;
    private String appTime;
    private String appVersion;
    private String packageName;

    public String getAppTime() {
        return this.appTime;
    }

    public void setAppTime(String str) {
        this.appTime = str;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(String str) {
        this.appVersion = str;
    }
}
