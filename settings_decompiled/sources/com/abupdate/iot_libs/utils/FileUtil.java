package com.abupdate.iot_libs.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class FileUtil {
    public static boolean createOrExistsDir(String str) {
        if (isSpace(str)) {
            return false;
        }
        File file = new File(str);
        if (file.exists()) {
            if (!file.isDirectory()) {
                return false;
            }
        } else if (!file.mkdirs()) {
            return false;
        }
        return true;
    }

    public static void closeIO(Closeable... closeableArr) {
        if (closeableArr == null) {
            return;
        }
        for (Closeable closeable : closeableArr) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isSpace(String str) {
        if (str == null) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
