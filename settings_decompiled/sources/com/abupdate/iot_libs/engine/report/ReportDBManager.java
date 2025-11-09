package com.abupdate.iot_libs.engine.report;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.data.db.ReportDBHelper;
import com.abupdate.iot_libs.data.local.DownParamInfo;
import com.abupdate.iot_libs.data.local.ErrorFileParamInfo;
import com.abupdate.iot_libs.data.local.EventTrackParamInfo;
import com.abupdate.iot_libs.data.local.UpgradeParamInfo;
import com.abupdate.iot_libs.data.remote.PushMessageInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ReportDBManager {
    private SQLiteDatabase db;
    private ReportDBHelper helper;

    public ReportDBManager(Context context) {
        ReportDBHelper reportDBHelper = new ReportDBHelper(context);
        this.helper = reportDBHelper;
        this.db = reportDBHelper.getWritableDatabase();
    }

    public void delete(DownParamInfo downParamInfo) {
        this.db.delete("report_down", "down_start_time = ?", new String[]{String.valueOf(downParamInfo.downStart)});
    }

    public void delete(UpgradeParamInfo upgradeParamInfo) {
        this.db.delete("report_upgrade", "_id = ?", new String[]{String.valueOf(upgradeParamInfo._id)});
    }

    public void delete(PushMessageInfo pushMessageInfo) {
        this.db.delete("push_response", "_id = ?", new String[]{String.valueOf(pushMessageInfo._id)});
    }

    public void delete(ErrorFileParamInfo errorFileParamInfo) {
        this.db.delete("report_error_log", "_id = ?", new String[]{String.valueOf(errorFileParamInfo._id)});
    }

    public List<DownParamInfo> queryDown() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("report_down");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            DownParamInfo downParamInfo = new DownParamInfo();
            downParamInfo._id = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            downParamInfo.productId = queryByTableName.getString(queryByTableName.getColumnIndex("productId"));
            downParamInfo.deltaID = queryByTableName.getString(queryByTableName.getColumnIndex("delta_id"));
            downParamInfo.downloadStatus = queryByTableName.getString(queryByTableName.getColumnIndex("download_status"));
            downParamInfo.downStart = queryByTableName.getString(queryByTableName.getColumnIndex("down_start_time"));
            downParamInfo.downEnd = queryByTableName.getString(queryByTableName.getColumnIndex("down_end_time"));
            downParamInfo.downSize = queryByTableName.getInt(queryByTableName.getColumnIndex("down_size"));
            downParamInfo.downIp = queryByTableName.getString(queryByTableName.getColumnIndex("down_ip"));
            arrayList.add(downParamInfo);
        }
        if (queryByTableName != null) {
            queryByTableName.close();
        }
        return arrayList;
    }

    public List<UpgradeParamInfo> queryUpgrade() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("report_upgrade");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            UpgradeParamInfo upgradeParamInfo = new UpgradeParamInfo();
            upgradeParamInfo._id = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            upgradeParamInfo.productId = queryByTableName.getString(queryByTableName.getColumnIndex("productId"));
            upgradeParamInfo.deltaID = queryByTableName.getString(queryByTableName.getColumnIndex("delta_id"));
            upgradeParamInfo.updateStatus = queryByTableName.getString(queryByTableName.getColumnIndex("updateStatus"));
            arrayList.add(upgradeParamInfo);
        }
        if (queryByTableName != null) {
            queryByTableName.close();
        }
        return arrayList;
    }

    public List<EventTrackParamInfo> queryEventTrack() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("report_event_track");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            EventTrackParamInfo eventTrackParamInfo = new EventTrackParamInfo();
            eventTrackParamInfo._id = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            eventTrackParamInfo.productId = queryByTableName.getString(queryByTableName.getColumnIndex("product_id"));
            eventTrackParamInfo.deltaID = queryByTableName.getString(queryByTableName.getColumnIndex("delta_id"));
            eventTrackParamInfo.recordTime = queryByTableName.getLong(queryByTableName.getColumnIndex("record_time"));
            eventTrackParamInfo.eventCode = queryByTableName.getInt(queryByTableName.getColumnIndex("event_code"));
            eventTrackParamInfo.detailsCode = queryByTableName.getInt(queryByTableName.getColumnIndex("details_code"));
            arrayList.add(eventTrackParamInfo);
        }
        if (queryByTableName != null) {
            queryByTableName.close();
        }
        return arrayList;
    }

    public List<PushMessageInfo> queryPushData() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("push_response");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            PushMessageInfo pushMessageInfo = new PushMessageInfo();
            pushMessageInfo._id = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            pushMessageInfo.msgId = queryByTableName.getString(queryByTableName.getColumnIndex("msgId"));
            arrayList.add(pushMessageInfo);
        }
        if (queryByTableName != null) {
            queryByTableName.close();
        }
        return arrayList;
    }

    public List<ErrorFileParamInfo> queryErrorLogData() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("report_error_log");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            int i = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            ErrorFileParamInfo errorFileParamInfo = new ErrorFileParamInfo(OtaAgentPolicy.getParamsController().getParams().mid, queryByTableName.getString(queryByTableName.getColumnIndex("delta_id")), queryByTableName.getString(queryByTableName.getColumnIndex("error_type")), queryByTableName.getString(queryByTableName.getColumnIndex("upload_file")));
            errorFileParamInfo._id = i;
            arrayList.add(errorFileParamInfo);
        }
        if (queryByTableName != null) {
            queryByTableName.close();
        }
        return arrayList;
    }

    private Cursor queryByTableName(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        return sQLiteDatabase.rawQuery("SELECT * FROM " + str, null);
    }
}
