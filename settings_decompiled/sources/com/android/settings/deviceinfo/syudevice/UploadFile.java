package com.android.settings.deviceinfo.syudevice;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
/* loaded from: classes.dex */
public class UploadFile {
    public static void upload(String str, String str2, String str3, String str4, String str5) throws IOException {
        String hexString = Long.toHexString(System.currentTimeMillis());
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + hexString);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        try {
            PrintWriter printWriter = new PrintWriter((Writer) new OutputStreamWriter(outputStream, "UTF-8"), true);
            File file = new File(str2);
            printWriter.append((CharSequence) ("--" + hexString)).append((CharSequence) "\r\n");
            printWriter.append((CharSequence) ("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"")).append((CharSequence) "\r\n");
            StringBuilder sb = new StringBuilder();
            sb.append("Content-Type: ");
            sb.append(URLConnection.guessContentTypeFromName(file.getName()));
            printWriter.append((CharSequence) sb.toString()).append((CharSequence) "\r\n");
            printWriter.append((CharSequence) "\r\n").flush();
            Files.copy(file.toPath(), outputStream);
            outputStream.flush();
            printWriter.append((CharSequence) "\r\n").flush();
            printWriter.append((CharSequence) ("--" + hexString)).append((CharSequence) "\r\n");
            printWriter.append((CharSequence) "Content-Disposition: form-data; name=\"type\"").append((CharSequence) "\r\n").append((CharSequence) "\r\n");
            printWriter.append((CharSequence) str3).append((CharSequence) "\r\n").flush();
            printWriter.append((CharSequence) ("--" + hexString)).append((CharSequence) "\r\n");
            printWriter.append((CharSequence) "Content-Disposition: form-data; name=\"devices\"").append((CharSequence) "\r\n").append((CharSequence) "\r\n");
            printWriter.append((CharSequence) str4).append((CharSequence) "\r\n");
            printWriter.flush();
            printWriter.append((CharSequence) ("--" + hexString)).append((CharSequence) "\r\n");
            printWriter.append((CharSequence) "Content-Disposition: form-data; name=\"platform\"").append((CharSequence) "\r\n").append((CharSequence) "\r\n");
            printWriter.append((CharSequence) str5).append((CharSequence) "\r\n");
            printWriter.flush();
            printWriter.append((CharSequence) "\r\n").flush();
            printWriter.append((CharSequence) ("--" + hexString + "--")).append((CharSequence) "\r\n");
            printWriter.close();
            outputStream.close();
            int responseCode = httpURLConnection.getResponseCode();
            Log.d("UploadFile", "upload: " + responseCode);
            httpURLConnection.disconnect();
        } catch (Throwable th) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
