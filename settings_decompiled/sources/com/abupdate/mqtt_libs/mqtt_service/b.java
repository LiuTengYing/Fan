package com.abupdate.mqtt_libs.mqtt_service;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/* loaded from: classes.dex */
class b implements com.abupdate.mqtt_libs.mqtt_service.c {

    /* renamed from: a  reason: collision with root package name */
    private SQLiteDatabase f4a = null;
    private c b;
    private MqttTraceHandler c;

    public b(MqttService mqttService, Context context) {
        this.b = null;
        this.c = mqttService;
        this.b = new c(this.c, context);
        this.c.traceDebug("DatabaseMessageStore", "DatabaseMessageStore<init> complete");
    }

    @Override // com.abupdate.mqtt_libs.mqtt_service.c
    public void close() {
        SQLiteDatabase sQLiteDatabase = this.f4a;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
    }

    @Override // com.abupdate.mqtt_libs.mqtt_service.c
    public void b(String str) {
        int delete;
        this.f4a = this.b.getWritableDatabase();
        String[] strArr = {str};
        if (str == null) {
            this.c.traceDebug("DatabaseMessageStore", "clearArrivedMessages: clearing the table");
            delete = this.f4a.delete("MqttArrivedMessageTable", null, null);
        } else {
            MqttTraceHandler mqttTraceHandler = this.c;
            mqttTraceHandler.traceDebug("DatabaseMessageStore", "clearArrivedMessages: clearing the table of " + str + " messages");
            delete = this.f4a.delete("MqttArrivedMessageTable", "clientHandle=?", strArr);
        }
        MqttTraceHandler mqttTraceHandler2 = this.c;
        mqttTraceHandler2.traceDebug("DatabaseMessageStore", "clearArrivedMessages: rows affected = " + delete);
    }

    /* loaded from: classes.dex */
    private static class c extends SQLiteOpenHelper {

        /* renamed from: a  reason: collision with root package name */
        private MqttTraceHandler f5a;

        public c(MqttTraceHandler mqttTraceHandler, Context context) {
            super(context, "mqttAndroidService.db", (SQLiteDatabase.CursorFactory) null, 1);
            this.f5a = mqttTraceHandler;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            this.f5a.traceDebug("MQTTDatabaseHelper", "onUpgrade");
            try {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS MqttArrivedMessageTable");
                onCreate(sQLiteDatabase);
                this.f5a.traceDebug("MQTTDatabaseHelper", "onUpgrade complete");
            } catch (SQLException e) {
                this.f5a.traceException("MQTTDatabaseHelper", "onUpgrade", e);
                throw e;
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            MqttTraceHandler mqttTraceHandler = this.f5a;
            mqttTraceHandler.traceDebug("MQTTDatabaseHelper", "onCreate {CREATE TABLE MqttArrivedMessageTable(messageId TEXT PRIMARY KEY, clientHandle TEXT, destinationName TEXT, payload BLOB, qos INTEGER, retained TEXT, duplicate TEXT, mtimestamp INTEGER);}");
            try {
                sQLiteDatabase.execSQL("CREATE TABLE MqttArrivedMessageTable(messageId TEXT PRIMARY KEY, clientHandle TEXT, destinationName TEXT, payload BLOB, qos INTEGER, retained TEXT, duplicate TEXT, mtimestamp INTEGER);");
                this.f5a.traceDebug("MQTTDatabaseHelper", "created the table");
            } catch (SQLException e) {
                this.f5a.traceException("MQTTDatabaseHelper", "onCreate", e);
                throw e;
            }
        }
    }
}
