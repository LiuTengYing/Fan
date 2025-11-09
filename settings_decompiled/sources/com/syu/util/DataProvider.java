package com.syu.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.FileUtils;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.SettingsApplication;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class DataProvider extends ContentProvider {
    public static SQLiteDatabase db;
    private static UriMatcher uriMatcher;
    private static final Uri NOTIFY_URI_AUTOSTART = Uri.parse("content://com.syu.settingsProvider/autostartlist");
    private static final Uri NOTIFY_URI_PROJECTION = Uri.parse("content://com.syu.settingsProvider/projectionlist");
    private static final Uri NOTIFY_URI_FILTERLIST = Uri.parse("content://com.syu.settingsProvider/filterlist");
    private static String lastPathSegment_autoStart = "autostartlist";
    private static String lastPathSegment_Projections = "projectionlist";
    private static String lastPathSegment_secondaryscreen = "secondaryscreen";
    private static String lastPathSegment_filter = "filterlist";
    public static String table_autoStart = "autostartapks";
    public static String table_projections = "projectionapks";
    public static String table_filter = "filterlist";
    public static String table_secondary = "secondaryscreenapks";
    public static String url = "/data/data/com.android.settings/settings_data.db";

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    static {
        UriMatcher uriMatcher2 = new UriMatcher(-1);
        uriMatcher = uriMatcher2;
        uriMatcher2.addURI("com.syu.settingsProvider", lastPathSegment_autoStart, 100);
        uriMatcher.addURI("com.syu.settingsProvider", lastPathSegment_Projections, 100);
        uriMatcher.addURI("com.syu.settingsProvider", lastPathSegment_filter, 99);
        uriMatcher.addURI("com.syu.settingsProvider", lastPathSegment_secondaryscreen, 100);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        openDatabase();
        int match = uriMatcher.match(uri);
        if (match != 100) {
            if (match == 99) {
                return db.query(table_filter, null, null, null, null, null, null);
            }
            return null;
        }
        String tableName = getTableName(uri);
        SQLiteDatabase sQLiteDatabase = db;
        sQLiteDatabase.execSQL("create table if not exists " + tableName + "(packageName varchar(255), className varchar(255),labelName varchar(255))");
        if (tableName != null) {
            return db.query(tableName, null, null, null, null, null, null);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        try {
            openDatabase();
            String tableName = getTableName(uri);
            if (tableName != null) {
                db.delete(tableName, null, null);
                JSONObject jSONObject = new JSONObject(str);
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject(keys.next());
                    Iterator<String> keys2 = jSONObject2.keys();
                    ContentValues contentValues2 = new ContentValues();
                    while (keys2.hasNext()) {
                        String next = keys2.next();
                        contentValues2.put(next, jSONObject2.getString(next));
                    }
                    db.insert(tableName, null, contentValues2);
                }
                return 0;
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void openDatabase() {
        new Thread(new Runnable() { // from class: com.syu.util.DataProvider.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    File file = new File(DataProvider.url);
                    if (!file.exists()) {
                        try {
                            FileUtils.copyToFile(SettingsApplication.mApplication.getResources().getAssets().open("settings_data.db"), file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    DataProvider.db = SQLiteDatabase.openDatabase(DataProvider.url, null, 0);
                } catch (Exception e2) {
                    Log.d("fangli", "run: " + e2.toString());
                }
            }
        }).start();
        SystemProperties.set("persist.lsec.hassystemuidataprovider", "true");
    }

    public String getTableName(Uri uri) {
        if (lastPathSegment_autoStart.equals(uri.getLastPathSegment())) {
            return table_autoStart;
        }
        if (lastPathSegment_Projections.equals(uri.getLastPathSegment())) {
            return table_projections;
        }
        if (lastPathSegment_secondaryscreen.equals(uri.getLastPathSegment())) {
            return table_secondary;
        }
        return null;
    }
}
