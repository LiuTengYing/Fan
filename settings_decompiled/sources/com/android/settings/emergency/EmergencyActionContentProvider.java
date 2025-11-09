package com.android.settings.emergency;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settingslib.emergencynumber.EmergencyNumberUtils;
/* loaded from: classes.dex */
public class EmergencyActionContentProvider extends ContentProvider {
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

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, String str3, Bundle bundle) {
        int callingUid = Binder.getCallingUid();
        Log.d("EmergencyActionContentP", "calling pid/uid" + Binder.getCallingPid() + "/" + callingUid);
        if (!isEmergencyInfo(getContext())) {
            throw new SecurityException("Uid is not allowed: " + callingUid);
        } else if (!TextUtils.equals(str2, "com.android.settings.emergency.MAKE_EMERGENCY_CALL")) {
            throw new IllegalArgumentException("Unsupported operation");
        } else {
            placeEmergencyCall(getContext());
            return new Bundle();
        }
    }

    private static boolean isEmergencyInfo(Context context) {
        return TextUtils.equals(context.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], context.getString(R$string.config_aosp_emergency_package_name));
    }

    private static void placeEmergencyCall(Context context) {
        if (!context.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            Log.i("EmergencyActionContentP", "Telephony is not supported, skipping.");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL", true);
        bundle.putInt("android.telecom.extra.CALL_SOURCE", 2);
        ((TelecomManager) context.getSystemService(TelecomManager.class)).placeCall(Uri.fromParts("tel", new EmergencyNumberUtils(context).getPoliceNumber(), null), bundle);
    }
}
