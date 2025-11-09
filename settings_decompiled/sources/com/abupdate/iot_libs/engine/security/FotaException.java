package com.abupdate.iot_libs.engine.security;
/* loaded from: classes.dex */
public class FotaException extends Exception {
    private Throwable cause;
    private int reasonCode;

    private String getErrorMessage(int i) {
        switch (i) {
            case 201:
                return "AndroidManifest element and permissions is lack";
            case 202:
                return "Fota [DeviceInfo] initPackagePath device parameters exception";
            case 203:
                return "AndroidManifest meta-data is null or should start with fota/";
            case 204:
                return "error log file not exist";
            case 205:
                return "productID is invalid ";
            case 206:
                return "not config main product";
            case 207:
                return "mid can not be empty";
            case 208:
                return "download file path is invalid";
            case 209:
            default:
                return "UnExpect Exception";
            case 210:
                return "Repeat configuration of main project information";
            case 211:
                return "Incoming parameters(secondary project) is already exists ";
            case 212:
                return "Secondary project info is invalid";
        }
    }

    public FotaException(int i) {
        this.reasonCode = i;
    }

    public int getReasonCode() {
        return this.reasonCode;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return getErrorMessage(this.reasonCode);
    }

    @Override // java.lang.Throwable
    public String toString() {
        String str = getMessage() + " (" + this.reasonCode + ")";
        if (this.cause != null) {
            return str + " - " + this.cause.toString();
        }
        return str;
    }
}
