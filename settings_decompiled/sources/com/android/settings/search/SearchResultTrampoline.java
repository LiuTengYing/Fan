package com.android.settings.search;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsApplication;
import com.android.settings.SubSettings;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.activityembedding.ActivityEmbeddingUtils;
import com.android.settings.homepage.DeepLinkHomepageActivityInternal;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settings.overlay.FeatureFactory;
import java.net.URISyntaxException;
/* loaded from: classes.dex */
public class SearchResultTrampoline extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ComponentName callingActivity = getCallingActivity();
        FeatureFactory.getFactory(this).getSearchFeatureProvider().verifyLaunchSearchResultPageCaller(this, callingActivity);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY");
        if (!TextUtils.isEmpty(intent.getStringExtra(":settings:show_fragment"))) {
            String stringExtra2 = intent.getStringExtra(":settings:fragment_args_key");
            int intExtra = intent.getIntExtra(":settings:show_fragment_tab", 0);
            Bundle bundle2 = new Bundle();
            bundle2.putString(":settings:fragment_args_key", stringExtra2);
            bundle2.putInt(":settings:show_fragment_tab", intExtra);
            intent.putExtra(":settings:show_fragment_args", bundle2);
            intent.setClass(this, SubSettings.class);
        } else {
            String stringExtra3 = intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI");
            if (TextUtils.isEmpty(stringExtra3)) {
                Log.e("SearchResultTrampoline", "No EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI for deep link");
                finish();
                return;
            }
            try {
                intent = Intent.parseUri(stringExtra3, 1);
            } catch (URISyntaxException e) {
                Log.e("SearchResultTrampoline", "Failed to parse deep link intent: " + e);
                finish();
                return;
            }
        }
        intent.addFlags(33554432);
        if (!ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this)) {
            startActivity(intent);
        } else if (isSettingsIntelligence(callingActivity)) {
            if (FeatureFlagUtils.isEnabled(this, "settings_search_always_expand")) {
                startActivity(SettingsActivity.getTrampolineIntent(intent, stringExtra).setClass(this, DeepLinkHomepageActivityInternal.class).addFlags(276824064));
            } else {
                ActivityEmbeddingRulesController.registerSubSettingsPairRule(this, false);
                intent.setFlags(intent.getFlags() & (-268435457));
                startActivity(intent);
                SettingsHomepageActivity homeActivity = ((SettingsApplication) getApplicationContext()).getHomeActivity();
                if (homeActivity != null) {
                    homeActivity.getMainFragment().setHighlightMenuKey(stringExtra, true);
                }
            }
        } else {
            startActivity(SettingsActivity.getTrampolineIntent(intent, stringExtra).addFlags(268435456));
        }
        finish();
    }

    private boolean isSettingsIntelligence(ComponentName componentName) {
        return componentName != null && TextUtils.equals(componentName.getPackageName(), FeatureFactory.getFactory(this).getSearchFeatureProvider().getSettingsIntelligencePkgName(this));
    }
}
