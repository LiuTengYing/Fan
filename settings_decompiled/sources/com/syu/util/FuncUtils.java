package com.syu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
/* loaded from: classes2.dex */
public class FuncUtils {
    private static Hashtable<String, String> LOCALE_TO_CHARSET_MAP;
    public static HashMap<String, Typeface> mTypeFaces;

    static {
        Hashtable<String, String> hashtable = new Hashtable<>();
        LOCALE_TO_CHARSET_MAP = hashtable;
        hashtable.put("ar", "ISO-8859-6");
        LOCALE_TO_CHARSET_MAP.put("be", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("bg", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("ca", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("cs", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("da", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("de", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("el", "ISO-8859-7");
        LOCALE_TO_CHARSET_MAP.put("es", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("et", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("fi", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("fr", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("hr", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("hu", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("is", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("it", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("iw", "ISO-8859-8");
        LOCALE_TO_CHARSET_MAP.put("ja", "Shift_JIS");
        LOCALE_TO_CHARSET_MAP.put("ko", "EUC-KR");
        LOCALE_TO_CHARSET_MAP.put("lt", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("lv", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("mk", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("nl", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("no", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("pl", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("pt", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("ro", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("ru", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("sh", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("sk", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("sl", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("sq", "ISO-8859-2");
        LOCALE_TO_CHARSET_MAP.put("sr", "ISO-8859-5");
        LOCALE_TO_CHARSET_MAP.put("sv", "ISO-8859-1");
        LOCALE_TO_CHARSET_MAP.put("tr", "ISO-8859-9");
        LOCALE_TO_CHARSET_MAP.put("uk", "ISO-8859-5");
        mTypeFaces = new HashMap<>();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x0032 -> B:71:0x0085). Please submit an issue!!! */
    public static String readStrFromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader2;
        String str = null;
        try {
            try {
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            try {
                inputStreamReader = new InputStreamReader(inputStream);
            } catch (Exception e2) {
                e = e2;
                inputStreamReader = null;
                bufferedReader2 = null;
            } catch (Throwable th2) {
                bufferedReader = null;
                th = th2;
                inputStreamReader = null;
            }
            try {
                bufferedReader2 = new BufferedReader(inputStreamReader);
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    while (true) {
                        String readLine = bufferedReader2.readLine();
                        if (readLine == null) {
                            break;
                        }
                        stringBuffer.append(readLine);
                    }
                    str = stringBuffer.toString();
                    try {
                        bufferedReader2.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    try {
                        inputStreamReader.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    inputStream.close();
                } catch (Exception e5) {
                    e = e5;
                    e.printStackTrace();
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (Exception e6) {
                            e6.printStackTrace();
                        }
                    }
                    if (inputStreamReader != null) {
                        try {
                            inputStreamReader.close();
                        } catch (Exception e7) {
                            e7.printStackTrace();
                        }
                    }
                    inputStream.close();
                    return str;
                }
            } catch (Exception e8) {
                e = e8;
                bufferedReader2 = null;
            } catch (Throwable th3) {
                bufferedReader = null;
                th = th3;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e9) {
                        e9.printStackTrace();
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (Exception e10) {
                        e10.printStackTrace();
                    }
                }
                try {
                    inputStream.close();
                } catch (Exception e11) {
                    e11.printStackTrace();
                }
                throw th;
            }
        }
        return str;
    }

    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception unused) {
        }
        return packageInfo != null;
    }
}
