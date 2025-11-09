package com.abupdate.iot_libs.data.local;

import com.abupdate.iot_libs.OtaAgentPolicy;
/* loaded from: classes.dex */
public class UpgradeParamInfo {
    public int _id;
    public String deltaID;
    public String mid = OtaAgentPolicy.getParamsController().getParams().mid;
    public String productId;
    public String updateStatus;

    public String toString() {
        return "UpgradeParamInfo{\nupdateStatus='" + this.updateStatus + "'\ndeltaID='" + this.deltaID + "'\nmid='" + this.mid + "'\n}";
    }
}
