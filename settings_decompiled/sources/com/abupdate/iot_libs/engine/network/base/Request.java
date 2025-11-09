package com.abupdate.iot_libs.engine.network.base;

import com.abupdate.http_libs.response.Response;
/* loaded from: classes.dex */
public abstract class Request<T> {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Response doRequest();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract T parseNetworkResponse(Response response);
}
