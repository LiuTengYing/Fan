package com.google.android.setupcompat.util;

import android.util.Log;
/* loaded from: classes2.dex */
public final class Logger {
    private final String prefix;

    public Logger(String str) {
        this.prefix = "[" + str + "] ";
    }

    public boolean isD() {
        return Log.isLoggable("SetupLibrary", 3);
    }

    public boolean isI() {
        return Log.isLoggable("SetupLibrary", 4);
    }

    public void atDebug(String str) {
        if (isD()) {
            Log.d("SetupLibrary", this.prefix.concat(str));
        }
    }

    public void atInfo(String str) {
        if (isI()) {
            Log.i("SetupLibrary", this.prefix.concat(str));
        }
    }

    public void w(String str) {
        Log.w("SetupLibrary", this.prefix.concat(str));
    }

    public void e(String str) {
        Log.e("SetupLibrary", this.prefix.concat(str));
    }

    public void e(String str, Throwable th) {
        Log.e("SetupLibrary", this.prefix.concat(str), th);
    }
}
