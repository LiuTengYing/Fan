package com.abupdate.iot_libs;

import android.content.Context;
import com.abupdate.iot_libs.data.local.FotaParamController;
import com.abupdate.iot_libs.engine.DataManager;
import com.abupdate.iot_libs.interact.task.CheckVersionTask;
/* loaded from: classes.dex */
public class OtaAgentPolicy {
    public static Context sCx;

    public static CheckVersionTask checkVersion() {
        return new CheckVersionTask();
    }

    public static DataManager getOtaEntityController() {
        return DataManager.getInstance();
    }

    public static FotaParamController getParamsController() {
        return FotaParamController.getInstance();
    }
}
