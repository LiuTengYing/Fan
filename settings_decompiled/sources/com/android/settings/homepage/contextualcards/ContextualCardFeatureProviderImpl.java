package com.android.settings.homepage.contextualcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class ContextualCardFeatureProviderImpl implements ContextualCardFeatureProvider {
    private final Context mContext;

    public ContextualCardFeatureProviderImpl(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardFeatureProvider
    public Cursor getContextualCards() {
        SQLiteDatabase readableDatabase = CardDatabaseHelper.getInstance(this.mContext).getReadableDatabase();
        final long currentTimeMillis = System.currentTimeMillis() - 86400000;
        Cursor query = readableDatabase.query("cards", null, "dismissed_timestamp < ? OR dismissed_timestamp IS NULL", new String[]{String.valueOf(currentTimeMillis)}, null, null, "score DESC");
        ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.homepage.contextualcards.ContextualCardFeatureProviderImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Object lambda$getContextualCards$0;
                lambda$getContextualCards$0 = ContextualCardFeatureProviderImpl.this.lambda$getContextualCards$0(currentTimeMillis);
                return lambda$getContextualCards$0;
            }
        });
        return query;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$getContextualCards$0(long j) throws Exception {
        return Integer.valueOf(resetDismissedTime(j));
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardFeatureProvider
    public int markCardAsDismissed(Context context, String str) {
        SQLiteDatabase writableDatabase = CardDatabaseHelper.getInstance(this.mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dismissed_timestamp", Long.valueOf(System.currentTimeMillis()));
        int update = writableDatabase.update("cards", contentValues, "name=?", new String[]{str});
        context.getContentResolver().notifyChange(CardContentProvider.DELETE_CARD_URI, null);
        return update;
    }

    int resetDismissedTime(long j) {
        SQLiteDatabase writableDatabase = CardDatabaseHelper.getInstance(this.mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.putNull("dismissed_timestamp");
        int update = writableDatabase.update("cards", contentValues, "dismissed_timestamp < ? AND dismissed_timestamp IS NOT NULL", new String[]{String.valueOf(j)});
        if (Build.IS_DEBUGGABLE) {
            Log.d("ContextualCardFeatureProvider", "Reset " + update + " records of dismissed time.");
        }
        return update;
    }
}
