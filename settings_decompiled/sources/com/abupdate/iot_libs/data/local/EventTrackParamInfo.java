package com.abupdate.iot_libs.data.local;

import com.abupdate.iot_libs.OtaAgentPolicy;
/* loaded from: classes.dex */
public class EventTrackParamInfo {
    public int _id;
    public String deltaID;
    public int detailsCode;
    public int eventCode;
    public String mid = OtaAgentPolicy.getParamsController().getParams().mid;
    public String productId;
    public long recordTime;

    public String toString() {
        return "EventTrackParamInfo{mid='" + this.mid + "', deltaID='" + this.deltaID + "', eventCode=" + this.eventCode + ", productId='" + this.productId + "', recordTime=" + this.recordTime + ", detailsCode=" + this.detailsCode + '}';
    }
}
