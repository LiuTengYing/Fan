package com.abupdate.iot_libs.data.db;

import android.database.sqlite.SQLiteDatabase;
import com.abupdate.iot_libs.OtaAgentPolicy;
/* loaded from: classes.dex */
public class ParamsDBManager {
    private static ParamsDBManager mInstance;
    private SQLiteDatabase db;
    private final ParamsDBHelper dbHelper = new ParamsDBHelper(OtaAgentPolicy.sCx);

    public static ParamsDBManager getInstance() {
        if (mInstance == null) {
            synchronized (ParamsDBManager.class) {
                if (mInstance == null) {
                    mInstance = new ParamsDBManager();
                }
            }
        }
        return mInstance;
    }

    private ParamsDBManager() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0151, code lost:
        if (r2 != null) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized com.abupdate.iot_libs.data.local.FotaParamController.FotaParams getFotaParams() {
        /*
            Method dump skipped, instructions count: 345
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.abupdate.iot_libs.data.db.ParamsDBManager.getFotaParams():com.abupdate.iot_libs.data.local.FotaParamController$FotaParams");
    }
}
