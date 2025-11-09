package com.abupdate.iot_libs.data.local;

import com.abupdate.iot_libs.OtaAgentPolicy;
/* loaded from: classes.dex */
public class DownParamInfo {
    public int _id;
    public String deltaID;
    public String downEnd;
    public String downIp;
    public int downSize;
    public String downStart;
    public String downloadStatus;
    public String mid = OtaAgentPolicy.getParamsController().getParams().mid;
    public String productId;

    public String toString() {
        return "DownParamInfo{\nmid='" + this.mid + "'\ndeltaID='" + this.deltaID + "'\ndownloadStatus='" + this.downloadStatus + "'\ndownStart='" + this.downStart + "'\ndownEnd='" + this.downEnd + "'\n}";
    }
}
