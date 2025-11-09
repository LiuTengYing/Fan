package com.abupdate.iot_libs.engine.report;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.abupdate.iot_libs.data.db.SotaDBHelper;
import com.abupdate.iot_libs.data.remote.SotaReportInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SotaReportDBManager {
    private SQLiteDatabase db;
    private SotaDBHelper helper;

    public SotaReportDBManager(Context context) {
        SotaDBHelper sotaDBHelper = new SotaDBHelper(context);
        this.helper = sotaDBHelper;
        this.db = sotaDBHelper.getWritableDatabase();
    }

    public void delete(SotaReportInfo sotaReportInfo) {
        this.db.delete("report", "_id = ?", new String[]{String.valueOf(sotaReportInfo._id)});
    }

    public List<SotaReportInfo> query() {
        ArrayList arrayList = new ArrayList();
        Cursor queryByTableName = queryByTableName("report");
        while (queryByTableName != null && queryByTableName.moveToNext()) {
            int i = queryByTableName.getInt(queryByTableName.getColumnIndex("_id"));
            SotaReportInfo sotaReportInfo = new SotaReportInfo(queryByTableName.getString(queryByTableName.getColumnIndex("appName")), queryByTableName.getString(queryByTableName.getColumnIndex("packageName")), queryByTableName.getInt(queryByTableName.getColumnIndex("versionCode")), queryByTableName.getString(queryByTableName.getColumnIndex("versionName")), queryByTableName.getString(queryByTableName.getColumnIndex("reportType")), queryByTableName.getInt(queryByTableName.getColumnIndex("status")));
            sotaReportInfo.setId(i);
            arrayList.add(sotaReportInfo);
        }
        queryByTableName.close();
        return arrayList;
    }

    private Cursor queryByTableName(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        return sQLiteDatabase.rawQuery("SELECT * FROM " + str, null);
    }
}
