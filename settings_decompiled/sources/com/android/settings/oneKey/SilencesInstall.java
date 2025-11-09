package com.android.settings.oneKey;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.util.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class SilencesInstall {
    private Context mContext;
    int mSessionId = -1;

    public SilencesInstall(Context context) {
        this.mContext = context;
    }

    public String installApp(String str) {
        String str2;
        Log.d("SilencesInstall", "installApp()------->" + str);
        File file = new File(str);
        if (!file.exists()) {
            Log.d("SilencesInstall", "文件不存在");
        }
        PackageInfo packageArchiveInfo = this.mContext.getPackageManager().getPackageArchiveInfo(str, 5);
        if (packageArchiveInfo != null) {
            String str3 = packageArchiveInfo.packageName;
            int i = packageArchiveInfo.versionCode;
            String str4 = packageArchiveInfo.versionName;
            Log.d("ApkActivity", "packageName=" + str3 + ", versionCode=" + i + ", versionName=" + str4);
        }
        PackageInstaller packageInstaller = this.mContext.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
        Log.d("SilencesInstall", "apkFile length" + file.length());
        sessionParams.setSize(file.length());
        try {
            this.mSessionId = packageInstaller.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SilencesInstall", "sessionId---->" + this.mSessionId);
        str2 = "fail";
        if (this.mSessionId != -1) {
            boolean onTransfesApkFile = onTransfesApkFile(str);
            Log.d("SilencesInstall", "copySuccess---->" + onTransfesApkFile);
            if (onTransfesApkFile) {
                boolean execInstallAPP = execInstallAPP();
                str2 = execInstallAPP ? "Success" : "fail";
                Log.d("SilencesInstall", "静默成功---" + execInstallAPP);
            }
        }
        return str2;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00b0 A[Catch: IOException -> 0x00ac, TRY_LEAVE, TryCatch #10 {IOException -> 0x00ac, blocks: (B:50:0x00a8, B:54:0x00b0), top: B:61:0x00a8 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean onTransfesApkFile(java.lang.String r11) {
        /*
            r10 = this;
            java.lang.String r0 = "SilencesInstall"
            java.lang.String r1 = "---------->onTransfesApkFile()<---------------------"
            android.util.Log.d(r0, r1)
            r1 = 0
            r2 = 0
            java.io.File r3 = new java.io.File     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            r3.<init>(r11)     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            android.content.Context r11 = r10.mContext     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            android.content.pm.PackageManager r11 = r11.getPackageManager()     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            android.content.pm.PackageInstaller r11 = r11.getPackageInstaller()     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            int r10 = r10.mSessionId     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            android.content.pm.PackageInstaller$Session r10 = r11.openSession(r10)     // Catch: java.lang.Throwable -> L86 java.io.IOException -> L8a
            java.lang.String r5 = "base.apk"
            r6 = 0
            long r8 = r3.length()     // Catch: java.lang.Throwable -> L7c java.io.IOException -> L81
            r4 = r10
            java.io.OutputStream r11 = r4.openWrite(r5, r6, r8)     // Catch: java.lang.Throwable -> L7c java.io.IOException -> L81
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L76 java.io.IOException -> L79
            r4.<init>(r3)     // Catch: java.lang.Throwable -> L76 java.io.IOException -> L79
            r2 = 1048576(0x100000, float:1.469368E-39)
            byte[] r2 = new byte[r2]     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            r3 = r1
        L35:
            int r5 = r4.read(r2)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            r6 = -1
            if (r5 == r6) goto L41
            int r3 = r3 + r5
            r11.write(r2, r1, r5)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            goto L35
        L41:
            r10.fsync(r11)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            r2.<init>()     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            java.lang.String r5 = "streamed "
            r2.append(r5)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            r2.append(r3)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            java.lang.String r3 = " bytes"
            r2.append(r3)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            android.util.Log.d(r0, r2)     // Catch: java.lang.Throwable -> L72 java.io.IOException -> L74
            r1 = 1
            r10.close()
            if (r11 == 0) goto L6a
            r11.close()     // Catch: java.io.IOException -> L68
            goto L6a
        L68:
            r10 = move-exception
            goto L6e
        L6a:
            r4.close()     // Catch: java.io.IOException -> L68
            goto L9f
        L6e:
            r10.printStackTrace()
            goto L9f
        L72:
            r0 = move-exception
            goto L7f
        L74:
            r0 = move-exception
            goto L84
        L76:
            r0 = move-exception
            r4 = r2
            goto L7f
        L79:
            r0 = move-exception
            r4 = r2
            goto L84
        L7c:
            r0 = move-exception
            r11 = r2
            r4 = r11
        L7f:
            r2 = r10
            goto La1
        L81:
            r0 = move-exception
            r11 = r2
            r4 = r11
        L84:
            r2 = r10
            goto L8d
        L86:
            r0 = move-exception
            r11 = r2
            r4 = r11
            goto La1
        L8a:
            r0 = move-exception
            r11 = r2
            r4 = r11
        L8d:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> La0
            if (r2 == 0) goto L95
            r2.close()
        L95:
            if (r11 == 0) goto L9a
            r11.close()     // Catch: java.io.IOException -> L68
        L9a:
            if (r4 == 0) goto L9f
            r4.close()     // Catch: java.io.IOException -> L68
        L9f:
            return r1
        La0:
            r0 = move-exception
        La1:
            if (r2 == 0) goto La6
            r2.close()
        La6:
            if (r11 == 0) goto Lae
            r11.close()     // Catch: java.io.IOException -> Lac
            goto Lae
        Lac:
            r10 = move-exception
            goto Lb4
        Lae:
            if (r4 == 0) goto Lb7
            r4.close()     // Catch: java.io.IOException -> Lac
            goto Lb7
        Lb4:
            r10.printStackTrace()
        Lb7:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.oneKey.SilencesInstall.onTransfesApkFile(java.lang.String):boolean");
    }

    private boolean execInstallAPP() {
        Log.d("SilencesInstall", "--------------------->execInstallAPP()<------------------");
        PackageInstaller.Session session = null;
        try {
            try {
                session = this.mContext.getPackageManager().getPackageInstaller().openSession(this.mSessionId);
                Intent intent = new Intent(this.mContext, InstallResultReceiver.class);
                intent.putExtra("flag", 1);
                session.commit(PendingIntent.getBroadcast(this.mContext, 1, intent, 201326592).getIntentSender());
                session.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                if (session != null) {
                    session.close();
                }
                return false;
            }
        } catch (Throwable th) {
            if (session != null) {
                session.close();
            }
            throw th;
        }
    }
}
