package com.android.net.module.util;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class ProxyUtils {
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$");
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$");

    public static String exclusionListAsString(String[] strArr) {
        return strArr == null ? "" : TextUtils.join(",", strArr);
    }

    public static int validate(String str, String str2, String str3) {
        int parseInt;
        Matcher matcher = HOSTNAME_PATTERN.matcher(str);
        Matcher matcher2 = EXCLLIST_PATTERN.matcher(str3);
        if (matcher.matches()) {
            if (matcher2.matches()) {
                if (str.length() <= 0 || str2.length() != 0) {
                    if (str2.length() > 0) {
                        if (str.length() == 0) {
                            return 1;
                        }
                        try {
                            parseInt = Integer.parseInt(str2);
                        } catch (NumberFormatException unused) {
                        }
                        return (parseInt <= 0 || parseInt > 65535) ? 4 : 0;
                    }
                    return 0;
                }
                return 3;
            }
            return 5;
        }
        return 2;
    }
}
