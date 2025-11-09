package com.unisoc.settings.timerpower;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.unisoc.settings.timerpower.Alarm;
/* loaded from: classes2.dex */
public class AlarmProvider extends ContentProvider {
    private static final UriMatcher sURLMatcher;
    private SQLiteOpenHelper mOpenHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sURLMatcher = uriMatcher;
        uriMatcher.addURI("com.android.settings.alarm", "timerpower", 1);
        uriMatcher.addURI("com.android.settings.alarm", "timerpower/#", 2);
    }

    /* loaded from: classes2.dex */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, "timerpower.db", (SQLiteDatabase.CursorFactory) null, 6);
            Log.v("AlarmProvider DatabaseHelper");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            Log.v("AlarmProvider DB  OnCreate");
            sQLiteDatabase.execSQL("CREATE TABLE timerpowers (_id INTEGER PRIMARY KEY,hour INTEGER, minutes INTEGER, daysofweek INTEGER, alarmtime INTEGER, enabled INTEGER, vibrate INTEGER, message TEXT, alert TEXT);");
            sQLiteDatabase.execSQL("INSERT INTO timerpowers (hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert) VALUES (8, 30, 31, 0, 0, 0, 'on', '');");
            sQLiteDatabase.execSQL("INSERT INTO timerpowers (hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert) VALUES (9, 00, 96, 0, 0, 0, 'off', '');");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS timerpowers");
            onCreate(sQLiteDatabase);
        }
    }

    public AlarmProvider() {
        Log.v("AlarmProvider");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Log.v("AlarmProvider::OnCreate");
        this.mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        int match = sURLMatcher.match(uri);
        if (match == 1) {
            sQLiteQueryBuilder.setTables("timerpowers");
        } else if (match == 2) {
            sQLiteQueryBuilder.setTables("timerpowers");
            sQLiteQueryBuilder.appendWhere("_id=");
            sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
        } else {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getReadableDatabase(), strArr, str, strArr2, null, null, str2);
        if (query == null) {
            Log.v("Alarms.query: failed");
        } else {
            query.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return query;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);
        if (match != 1) {
            if (match == 2) {
                return "vnd.android.cursor.item/timerpowers";
            }
            throw new IllegalArgumentException("Unknown URL");
        }
        return "vnd.android.cursor.dir/timerpowers";
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int match = sURLMatcher.match(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (match == 2) {
            long parseLong = Long.parseLong(uri.getPathSegments().get(1));
            int update = writableDatabase.update("timerpowers", contentValues, "_id=" + parseLong, null);
            Log.v("notifyChange() rowId: " + parseLong + " url " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
            return update;
        }
        throw new UnsupportedOperationException("Cannot update URL: " + uri);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (sURLMatcher.match(uri) != 1) {
            throw new IllegalArgumentException("Cannot insert into URL: " + uri);
        }
        long insert = this.mOpenHelper.getWritableDatabase().insert("timerpowers", "message", new ContentValues(contentValues));
        if (insert < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }
        Log.v("Added timerpower rowId = " + insert);
        Uri withAppendedId = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, insert);
        getContext().getContentResolver().notifyChange(withAppendedId, null);
        return withAppendedId;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int delete;
        String str2;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        int match = sURLMatcher.match(uri);
        if (match == 1) {
            delete = writableDatabase.delete("timerpowers", str, strArr);
        } else if (match == 2) {
            String str3 = uri.getPathSegments().get(1);
            if (TextUtils.isEmpty(str)) {
                str2 = "_id=" + str3;
            } else {
                str2 = "_id=" + str3 + " AND (" + str + ")";
            }
            delete = writableDatabase.delete("timerpowers", str2, strArr);
        } else {
            throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delete;
    }
}
