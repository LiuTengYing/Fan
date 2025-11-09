package com.abupdate.iot_libs.interact.response;
/* loaded from: classes.dex */
public abstract class BaseResponse<T> {
    public int errorCode;
    public boolean isOK;
    public T result;

    public boolean isNetError() {
        return this.errorCode == 3003;
    }

    public T getResult() {
        return this.result;
    }
}
