package com.android.settings.utils;

import android.icu.text.ListFormatter;
import android.text.TextUtils;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class LocaleUtils {
    public static CharSequence getConcatenatedString(List<CharSequence> list) {
        ListFormatter listFormatter = ListFormatter.getInstance(Locale.getDefault());
        CharSequence charSequence = list.get(list.size() - 1);
        list.add("fake last item");
        String format = listFormatter.format(list);
        return format.subSequence(0, TextUtils.indexOf(format, charSequence) + charSequence.length());
    }
}
