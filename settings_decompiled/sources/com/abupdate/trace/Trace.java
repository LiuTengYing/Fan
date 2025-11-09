package com.abupdate.trace;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes.dex */
public class Trace {
    private static String global_tag = "iport";
    private static String m_log_path = Environment.getExternalStorageDirectory() + String.format("/%s_log.txt", global_tag);
    private static int offset = 0;
    private static int s_level = 7;
    private static int s_log_size = 500;
    private static boolean s_show_code_position = false;
    private static boolean s_write_file = true;

    private static void println(int i, String str, String str2) {
        if (i >= s_level) {
            if (s_write_file) {
                write_log(str, str2);
            }
            if (s_show_code_position) {
                str2 = str2 + getCodePosition();
            }
            printAndroidLog(i, global_tag + str, str2);
        }
    }

    private static void printAndroidLog(int i, String str, String str2) {
        if (i == 2) {
            Log.v(str, str2);
        } else if (i == 3) {
            Log.d(str, str2);
        } else if (i == 4) {
            Log.i(str, str2);
        } else if (i == 5) {
            Log.w(str, str2);
        } else if (i != 6) {
        } else {
            Log.e(str, str2);
        }
    }

    public static void d(String str, String str2) {
        println(3, str, str2);
    }

    public static void d(String str, String str2, Object... objArr) {
        println(3, str, String.format(str2, objArr));
    }

    public static void i(String str, String str2) {
        println(4, str, str2);
    }

    public static void i(String str, String str2, Object... objArr) {
        println(4, str, String.format(str2, objArr));
    }

    public static void w(String str, String str2) {
        println(5, str, str2);
    }

    public static void e(String str, String str2) {
        println(6, str, str2);
    }

    public static void e(String str, String str2, Throwable th) {
        println(6, str, str2 + '\n' + getStackTraceString(th));
    }

    public static void e(String str, Throwable th) {
        println(6, str, getStackTraceString(th));
    }

    private static String getStackTraceString(Throwable th) {
        if (th == null) {
            return "";
        }
        for (Throwable th2 = th; th2 != null; th2 = th2.getCause()) {
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    private static String getCodePosition() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int i = offset + 5;
        offset = 0;
        return String.format(".(%s:%s) %s()", stackTrace[i].getFileName(), Integer.valueOf(stackTrace[i].getLineNumber()), stackTrace[i].getMethodName());
    }

    private static boolean check_log_file() {
        File file = new File(m_log_path);
        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                Log.e(global_tag, getStackTraceString(new IOException("Can't create the directory of trace. Please check the trace path.")));
                return false;
            }
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                Log.e("Trace", getStackTraceString(e));
                return false;
            }
        } else if (file.length() > s_log_size * 1024) {
            file.renameTo(new File(m_log_path + "(1)"));
            return true;
        } else {
            return true;
        }
    }

    private static void write_log(String str, String str2) {
        FileOutputStream fileOutputStream;
        File file = new File(m_log_path);
        if (check_log_file()) {
            String formatLog = getFormatLog(str, str2);
            FileOutputStream fileOutputStream2 = null;
            try {
                try {
                    try {
                        fileOutputStream = new FileOutputStream(file, file.length() <= ((long) (s_log_size * 1024)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                fileOutputStream.write(formatLog.getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.close();
            } catch (Exception e3) {
                e = e3;
                fileOutputStream2 = fileOutputStream;
                e.printStackTrace();
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
                throw th;
            }
        }
    }

    private static String getFormatLog(String str, String str2) {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        return format + " " + String.format("%s ", convertThreadId((int) Thread.currentThread().getId())) + String.format("%s: ", str) + str2;
    }

    private static String convertThreadId(int i) {
        String valueOf = String.valueOf(i);
        int length = 5 - valueOf.length();
        if (length < 0) {
            valueOf = valueOf.substring(-length, valueOf.length());
        }
        while (length > 0) {
            valueOf = "0" + valueOf;
            length--;
        }
        return valueOf;
    }
}
