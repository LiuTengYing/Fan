package com.abupdate.iot_libs.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/* loaded from: classes.dex */
public class ParamsDBHelper extends SQLiteOpenHelper {
    public ParamsDBHelper(Context context) {
        super(context, "fota_params.db", (SQLiteDatabase.CursorFactory) null, 4);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS table_name_fota_params(_id INTEGER PRIMARY KEY AUTOINCREMENT, tracePath VARCHAR, showTrace BOOLEAN, reportLog BOOLEAN, mid VARCHAR, verifyCode VARCHAR, keepConnect BOOLEAN,httpRetryTimes INTEGER,httpRedirectTimes INTEGER,httpSocketTimeout INTEGER,httpConnectTimeout INTEGER,downloadConnectTimeout INTEGER,downloadReadTimeout INTEGER,downloadRetryTime INTEGER,useDefaultClientTrigger INTEGER,clientDefaultTriggerCycle INTEGER,useDefaultClientStatusMechanism INTEGER)");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS table_name_fota_entity_info(productID VARCHAR PRIMARY KEY, productSecret VARCHAR, deviceKey VARCHAR, deviceId VARCHAR, deviceSecret VARCHAR, downloadStatus INTEGER, versionData VARCHAR, oldVersion VARCHAR,snVerify INTEGER,mqttStatus INTEGER)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table IF EXISTS table_name_fota_params");
        sQLiteDatabase.execSQL("drop table IF EXISTS table_name_fota_entity_info");
        onCreate(sQLiteDatabase);
    }
}
