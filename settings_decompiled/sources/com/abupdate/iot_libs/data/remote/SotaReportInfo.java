package com.abupdate.iot_libs.data.remote;
/* loaded from: classes.dex */
public class SotaReportInfo {
    public int _id;
    private String appName;
    private String packageName;
    private int reportStatus;
    private String reportType;
    private int versionCode;
    private String versionName;

    public void setId(int i) {
        this._id = i;
    }

    public SotaReportInfo(String str, String str2, int i, String str3, String str4, int i2) {
        this.appName = str;
        this.packageName = str2;
        this.versionCode = i;
        this.versionName = str3;
        this.reportType = str4;
        this.reportStatus = i2;
    }
}
