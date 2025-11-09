package com.abupdate.iot_libs.engine.check;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.local.OtaEntity;
import com.abupdate.iot_libs.data.remote.VersionInfo;
import com.abupdate.iot_libs.interact.response.CommonResponse;
import com.abupdate.trace.Trace;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CheckAllProjectTask {
    Object lock = new Object();
    int count = 0;

    public static CheckAllProjectTask newInstance() {
        return new CheckAllProjectTask();
    }

    public CommonResponse<List<VersionInfo>> executed() {
        CommonResponse<List<VersionInfo>> commonResponse = new CommonResponse<>();
        List<OtaEntity> otaEntities = OtaAgentPolicy.getOtaEntityController().getOtaEntities();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        this.count = otaEntities.size();
        if (otaEntities.size() == 0) {
            Trace.e("CheckAllProjectTask", "FOTA entity size is 0");
            commonResponse.setErrorCode(1001);
            return commonResponse;
        }
        Trace.d("CheckAllProjectTask", "entities size: " + OtaAgentPolicy.getOtaEntityController().getOtaEntities().size());
        for (OtaEntity otaEntity : otaEntities) {
            otaEntity.getProductInfo();
            if (otaEntity.isMainProduct()) {
                throw null;
            }
            reduceCount();
        }
        lock();
        return handleErrorCode(arrayList, arrayList2);
    }

    private void reduceCount() {
        int i = this.count - 1;
        this.count = i;
        if (i <= 0) {
            unLock();
        }
    }

    private void lock() {
        if (this.count <= 0) {
            return;
        }
        synchronized (this.lock) {
            try {
                this.lock.wait(60000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unLock() {
        synchronized (this.lock) {
            try {
                this.lock.notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CommonResponse handleErrorCode(List<VersionInfo> list, List<Integer> list2) {
        boolean z;
        if (list2.size() > 0) {
            z = false;
            for (Integer num : list2) {
                if (num.intValue() == 3003) {
                    z = true;
                }
            }
        } else {
            z = false;
        }
        CommonResponse commonResponse = new CommonResponse();
        if (z) {
            commonResponse.setErrorCode(3003);
        } else if (list.size() == 0 && list2.size() > 0) {
            commonResponse.setErrorCode(list2.get(0).intValue());
        } else {
            commonResponse.setResult(list);
        }
        return commonResponse;
    }
}
