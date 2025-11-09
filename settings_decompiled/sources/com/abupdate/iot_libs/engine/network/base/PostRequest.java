package com.abupdate.iot_libs.engine.network.base;

import android.content.Context;
import android.text.TextUtils;
import com.abupdate.http_libs.response.Response;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.engine.security.FotaException;
import com.abupdate.iot_libs.interact.response.BaseResponse;
import com.abupdate.iot_libs.utils.JsonAnalyticsUtil;
import com.abupdate.trace.Trace;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public abstract class PostRequest<T extends BaseResponse, V> extends Request<T> {
    protected T baseResponse;
    protected Context context;
    protected String productId = "";

    public abstract V parseSuccessResult(Response response);

    /* JADX INFO: Access modifiers changed from: protected */
    public PostRequest(Context context, T t) {
        this.context = context;
        this.baseResponse = t;
    }

    public PostRequest setProductId(String str) {
        this.productId = str;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r4v1, types: [T, java.lang.Object] */
    @Override // com.abupdate.iot_libs.engine.network.base.Request
    public T parseNetworkResponse(Response response) {
        if (response == null) {
            T t = this.baseResponse;
            t.isOK = false;
            if (t.errorCode == 0) {
                t.errorCode = 8003;
            }
        } else if (!response.isResultOk()) {
            T t2 = this.baseResponse;
            t2.isOK = false;
            t2.errorCode = 3003;
        } else {
            Trace.d("PostRequest", "parseNetworkResponse() result:" + response.getContent());
            int parseStatus = parseStatus(response.getContent());
            if (isSuccess(parseStatus)) {
                T t3 = this.baseResponse;
                t3.isOK = true;
                t3.result = parseSuccessResult(response);
                return this.baseResponse;
            }
            T t4 = this.baseResponse;
            t4.isOK = false;
            t4.errorCode = parseStatus;
        }
        parseFailedResult(response);
        return this.baseResponse;
    }

    protected void parseFailedResult(Response response) {
        Trace.e("PostRequest", "parseFailedResult error:" + this.baseResponse.errorCode);
        if (response != null && response.getException() != null) {
            Trace.e("PostRequest", String.format("%s doPostJson exception:", this.productId), response.getException());
        }
        int i = this.baseResponse.errorCode;
        if ((i == 2001 || i == 2103) && !TextUtils.isEmpty(this.productId)) {
            try {
                DataManager.getInstance().getRegisterInfoByProduct(this.productId);
                throw null;
            } catch (FotaException e) {
                e.printStackTrace();
            }
        }
        if (this.baseResponse.errorCode == 1002) {
            try {
                OtaEntity entityByProduct = DataManager.getInstance().getEntityByProduct(this.productId);
                if (entityByProduct != null && entityByProduct.isMainProduct()) {
                    Trace.d("PostRequest", "parseFailedResult() reset product info.");
                    entityByProduct.getProductInfo();
                    throw null;
                }
                Trace.e("PostRequest", "parseFailedResult() not need to reset product info.");
            } catch (FotaException e2) {
                e2.printStackTrace();
                Trace.e("PostRequest", "parseFailedResult() Exception: ", e2);
            }
        }
    }

    public int parseStatus(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("status")) {
                return jSONObject.getInt("status");
            }
            return 2002;
        } catch (JSONException e) {
            e.printStackTrace();
            return 2002;
        }
    }

    public boolean isSuccess(int i) {
        return JsonAnalyticsUtil.isSuccess(i);
    }
}
