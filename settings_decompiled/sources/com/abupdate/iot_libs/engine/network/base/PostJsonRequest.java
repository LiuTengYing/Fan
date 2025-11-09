package com.abupdate.iot_libs.engine.network.base;

import android.content.Context;
import com.abupdate.iot_libs.interact.response.BaseResponse;
/* loaded from: classes.dex */
public abstract class PostJsonRequest<T extends BaseResponse, V> extends PostRequest<T, V> {
    /* JADX INFO: Access modifiers changed from: protected */
    public PostJsonRequest(Context context, T t) {
        super(context, t);
    }
}
