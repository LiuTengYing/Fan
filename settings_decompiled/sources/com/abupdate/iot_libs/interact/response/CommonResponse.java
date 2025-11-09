package com.abupdate.iot_libs.interact.response;
/* loaded from: classes.dex */
public class CommonResponse<T> extends BaseResponse<T> {
    public CommonResponse<T> setErrorCode(int i) {
        this.isOK = false;
        this.errorCode = i;
        return this;
    }

    public CommonResponse<T> setResult(T t) {
        this.isOK = true;
        this.result = t;
        return this;
    }
}
