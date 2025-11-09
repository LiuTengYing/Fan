package com.abupdate.iot_libs.engine.network.base;
/* loaded from: classes.dex */
public abstract class BaseRequestStack {
    public Object doRequest(Request request) {
        return request.parseNetworkResponse(request.doRequest());
    }
}
