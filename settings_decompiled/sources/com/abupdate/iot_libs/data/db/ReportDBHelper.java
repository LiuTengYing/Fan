package com.abupdate.iot_libs.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/* loaded from: classes.dex */
public class ReportDBHelper extends SQLiteOpenHelper {
    public ReportDBHelper(Context context) {
        super(context, "fota_sdk.db", (SQLiteDatabase.CursorFactory) null, 3);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS report_down(_id INTEGER PRIMARY KEY AUTOINCREMENT, productId VARCHAR, delta_id VARCHAR, download_status VARCHAR, down_start_time VARCHAR, down_end_time VARCHAR,down_size INTEGER,down_ip VARCHAR)");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS report_upgrade(_id INTEGER PRIMARY KEY AUTOINCREMENT, productId VARCHAR, delta_id VARCHAR, updateStatus VARCHAR )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS report_event_track(_id INTEGER PRIMARY KEY AUTOINCREMENT, product_id VARCHAR, delta_id VARCHAR, event_code VARCHAR, details_code VARCHAR, record_time VARCHAR )");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS push_response(_id INTEGER PRIMARY KEY AUTOINCREMENT, msgId VARCHAR)");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS report_error_log(_id INTEGER PRIMARY KEY AUTOINCREMENT, delta_id VARCHAR, error_type VARCHAR, upload_file VARCHAR)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table IF EXISTS report_down");
        sQLiteDatabase.execSQL("drop table IF EXISTS report_upgrade");
        sQLiteDatabase.execSQL("drop table IF EXISTS report_event_track");
        sQLiteDatabase.execSQL("drop table IF EXISTS push_response");
        sQLiteDatabase.execSQL("drop table IF EXISTS report_error_log");
        onCreate(sQLiteDatabase);
    }
}
