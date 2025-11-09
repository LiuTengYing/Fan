package com.syu.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes2.dex */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    static CrashHandler mInstance;
    final String CARSH_DIR_PATH = "/sdcard/crash";
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    HashMap<String, String> infos;
    Context mContext;
    Thread.UncaughtExceptionHandler mDefaultHandler;
    String pkgName;

    public static CrashHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CrashHandler(context);
        }
        return mInstance;
    }

    public CrashHandler(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.pkgName = applicationContext.getPackageName().replace(".", "_");
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        if (!handleException(th)) {
            if (this.mDefaultHandler == null) {
                this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            }
            this.mDefaultHandler.uncaughtException(thread, th);
            return;
        }
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    private boolean handleException(Throwable th) {
        if (th == null) {
            return false;
        }
        collectInfo();
        saveCarshException(th);
        return true;
    }

    void collectInfo() {
        if (this.infos == null) {
            this.infos = new HashMap<>();
        }
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 1);
            if (packageInfo != null) {
                String str = packageInfo.versionName;
                if (str == null) {
                    str = "null";
                }
                String sb = new StringBuilder(String.valueOf(packageInfo.versionCode)).toString();
                this.infos.put("versionName", str);
                this.infos.put("versionCode", sb);
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    void saveCarshException(Throwable th) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : this.infos.entrySet()) {
            stringBuffer.append(String.valueOf(entry.getKey()) + "=" + entry.getValue() + "\n");
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        try {
            String str = "crash-" + this.dateFormat.format(new Date()) + "-" + this.pkgName + ".txt";
            File file = new File("/sdcard/crash");
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/crash/" + str);
            fileOutputStream.write(stringBuffer.toString().getBytes());
            fileOutputStream.close();
        } catch (Exception unused) {
        }
    }
}
