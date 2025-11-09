package com.abupdate.iot_libs.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/* loaded from: classes.dex */
public class SotaDBHelper extends SQLiteOpenHelper {
    public SotaDBHelper(Context context) {
        super(context, "sota_sdk.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS report(_id INTEGER PRIMARY KEY AUTOINCREMENT, appName VARCHAR, packageName VARCHAR, versionCode INTEGER, versionName VARCHAR,reportType VARCHAR,status INTEGER)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table IF EXISTS report");
        onCreate(sQLiteDatabase);
    }
}
