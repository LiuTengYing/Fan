package com.abupdate.iot_libs.interact.task;

import com.abupdate.iot_libs.data.remote.VersionInfo;
import com.abupdate.iot_libs.engine.check.Checker;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.trace.Trace;
import java.util.List;
/* loaded from: classes.dex */
public class CheckVersionTask {
    public CommonResponse<List<VersionInfo>> executed() {
        Trace.d("CheckVersionTask", "%s%s%s", "--------------------------", "check version executed", "--------------------------");
        return Checker.getInstance().checkVersionTask();
    }
}
