package com.android.settings.inputmethod;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.UserDictionary;
import android.util.ArraySet;
import androidx.loader.content.CursorLoader;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes.dex */
public class UserDictionaryCursorLoader extends CursorLoader {
    static final String[] QUERY_PROJECTION = {"_id", "word", "shortcut"};
    private final String mLocale;

    public UserDictionaryCursorLoader(Context context, String str) {
        super(context);
        this.mLocale = str;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.loader.content.AsyncTaskLoader
    public Cursor loadInBackground() {
        Cursor query;
        String[] strArr = QUERY_PROJECTION;
        MatrixCursor matrixCursor = new MatrixCursor(strArr);
        if ("".equals(this.mLocale)) {
            query = getContext().getContentResolver().query(UserDictionary.Words.CONTENT_URI, strArr, "locale is null", null, "UPPER(word)");
        } else {
            String str = this.mLocale;
            if (str == null) {
                str = Locale.getDefault().toString();
            }
            query = getContext().getContentResolver().query(UserDictionary.Words.CONTENT_URI, strArr, "locale=?", new String[]{str}, "UPPER(word)");
        }
        ArraySet arraySet = new ArraySet();
        query.moveToFirst();
        while (!query.isAfterLast()) {
            int i = query.getInt(0);
            String string = query.getString(1);
            String string2 = query.getString(2);
            int hash = Objects.hash(string, string2);
            if (!arraySet.contains(Integer.valueOf(hash))) {
                arraySet.add(Integer.valueOf(hash));
                matrixCursor.addRow(new Object[]{Integer.valueOf(i), string, string2});
            }
            query.moveToNext();
        }
        return matrixCursor;
    }
}
