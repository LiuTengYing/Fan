package com.android.settings.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import com.android.settings.SettingsApplication;
import com.android.settings.homepage.SettingsHomepageActivity;
/* loaded from: classes.dex */
public class SearchStateReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (FeatureFlagUtils.isEnabled(context, "settings_search_always_expand")) {
            return;
        }
        if (intent == null) {
            Log.w("SearchStateReceiver", "Null intent");
            return;
        }
        SettingsHomepageActivity homeActivity = ((SettingsApplication) context.getApplicationContext()).getHomeActivity();
        if (homeActivity == null) {
            return;
        }
        String action = intent.getAction();
        Log.d("SearchStateReceiver", "action: " + action);
        if (TextUtils.equals("com.android.settings.SEARCH_START", action)) {
            homeActivity.getMainFragment().setMenuHighlightShowed(false);
        } else if (TextUtils.equals("com.android.settings.SEARCH_EXIT", action)) {
            homeActivity.getMainFragment().setMenuHighlightShowed(true);
        }
    }
}
