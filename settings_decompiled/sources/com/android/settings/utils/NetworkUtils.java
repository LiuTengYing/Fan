package com.android.settings.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.SettingsApplication;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class NetworkUtils {
    public static Handler ResponseForOkhttps = new Handler() { // from class: com.android.settings.utils.NetworkUtils.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                ResponseBody body = ((Response) message.obj).body();
                Objects.requireNonNull(body);
                ResponseBody responseBody = body;
                String string = body.string();
                Log.i("hzqok", string);
                String decrypt = AESCBCUtil.decrypt(new JSONObject(string).getString("res"));
                Log.i("hzqok", "result = " + decrypt);
                if (decrypt != null && decrypt.length() > 0) {
                    JSONObject jSONObject = new JSONObject(decrypt).getJSONObject("data").getJSONObject("current");
                    SettingsApplication.sunrise = jSONObject.getString("sunrise");
                    SettingsApplication.sunset = jSONObject.getString("sunset");
                    SystemProperties.set("persist.fyt.lighttime_sunrise", SettingsApplication.sunrise);
                    SystemProperties.set("persist.fyt.lighttime_sunset", SettingsApplication.sunset);
                    SystemProperties.set("persist.fyt.lighttime_updatetime", new SimpleDateFormat("yyyyMM").format(new Date()));
                }
                Log.i("hzqok", decrypt);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    };

    public static boolean isNetWorkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }
}
