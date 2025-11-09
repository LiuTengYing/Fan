package com.abupdate.http_libs.exception;
/* loaded from: classes.dex */
public class HttpException extends Exception {
    private Throwable cause;
    private int reasonCode;

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
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
