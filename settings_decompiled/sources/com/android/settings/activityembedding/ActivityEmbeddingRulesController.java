package com.android.settings.activityembedding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.window.embedding.ActivityFilter;
import androidx.window.embedding.ActivityRule;
import androidx.window.embedding.SplitController;
import androidx.window.embedding.SplitPairFilter;
import androidx.window.embedding.SplitPairRule;
import androidx.window.embedding.SplitPlaceholderRule;
import com.android.settings.Settings;
import com.android.settings.SubSettings;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroductionInternal;
import com.android.settings.homepage.DeepLinkHomepageActivity;
import com.android.settings.homepage.DeepLinkHomepageActivityInternal;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.users.AvatarPickerActivity;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class ActivityEmbeddingRulesController {
    private static final ComponentName COMPONENT_NAME_WILDCARD = new ComponentName("*", "*");
    private final Context mContext;
    private final SplitController mSplitController = SplitController.getInstance();

    public ActivityEmbeddingRulesController(Context context) {
        this.mContext = context;
    }

    public void initRules() {
        if (!ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this.mContext)) {
            Log.d("ActivityEmbeddingCtrl", "Not support this feature now");
            return;
        }
        this.mSplitController.clearRegisteredRules();
        registerHomepagePlaceholderRule();
        registerAlwaysExpandRule();
    }

    public static void registerTwoPanePairRule(Context context, ComponentName componentName, ComponentName componentName2, String str, int i, int i2, boolean z) {
        if (ActivityEmbeddingUtils.isEmbeddingActivityEnabled(context)) {
            HashSet hashSet = new HashSet();
            hashSet.add(new SplitPairFilter(componentName, componentName2, str));
            SplitController.getInstance().registerRule(new SplitPairRule(hashSet, i, i2, z, ActivityEmbeddingUtils.getMinCurrentScreenSplitWidthPx(context), ActivityEmbeddingUtils.getMinSmallestScreenSplitWidthPx(context), ActivityEmbeddingUtils.getSplitRatio(context), 3));
        }
    }

    public static void registerTwoPanePairRuleForSettingsHome(Context context, ComponentName componentName, String str, boolean z, boolean z2, boolean z3) {
        if (ActivityEmbeddingUtils.isEmbeddingActivityEnabled(context)) {
            registerTwoPanePairRule(context, new ComponentName(context, Settings.class), componentName, str, z ? 2 : 0, z2 ? 2 : 0, z3);
            registerTwoPanePairRule(context, new ComponentName(context, SettingsHomepageActivity.class), componentName, str, z ? 2 : 0, z2 ? 2 : 0, z3);
            registerTwoPanePairRule(context, new ComponentName(context, DeepLinkHomepageActivity.class), componentName, str, z ? 1 : 0, z2 ? 1 : 0, z3);
            registerTwoPanePairRule(context, new ComponentName(context, DeepLinkHomepageActivityInternal.class), componentName, str, z ? 1 : 0, z2 ? 1 : 0, z3);
        }
    }

    public static void registerTwoPanePairRuleForSettingsHome(Context context, ComponentName componentName, String str, boolean z) {
        if (ActivityEmbeddingUtils.isEmbeddingActivityEnabled(context)) {
            registerTwoPanePairRuleForSettingsHome(context, componentName, str, true, true, z);
        }
    }

    public static void registerSubSettingsPairRule(Context context, boolean z) {
        if (ActivityEmbeddingUtils.isEmbeddingActivityEnabled(context)) {
            registerTwoPanePairRuleForSettingsHome(context, new ComponentName(context, SubSettings.class), null, z);
            registerTwoPanePairRuleForSettingsHome(context, COMPONENT_NAME_WILDCARD, "android.intent.action.SAFETY_CENTER", z);
        }
    }

    private void registerHomepagePlaceholderRule() {
        HashSet hashSet = new HashSet();
        addActivityFilter(hashSet, SettingsHomepageActivity.class);
        addActivityFilter(hashSet, Settings.class);
        Intent intent = new Intent(this.mContext, Settings.NetworkDashboardActivity.class);
        intent.putExtra(":settings:is_second_layer_page", true);
        this.mSplitController.registerRule(new SplitPlaceholderRule(hashSet, intent, true, 2, ActivityEmbeddingUtils.getMinCurrentScreenSplitWidthPx(this.mContext), ActivityEmbeddingUtils.getMinSmallestScreenSplitWidthPx(this.mContext), ActivityEmbeddingUtils.getSplitRatio(this.mContext), 3));
    }

    private void registerAlwaysExpandRule() {
        HashSet hashSet = new HashSet();
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_search_always_expand")) {
            addActivityFilter(hashSet, FeatureFactory.getFactory(this.mContext).getSearchFeatureProvider().buildSearchIntent(this.mContext, 1502));
        }
        addActivityFilter(hashSet, FingerprintEnrollIntroduction.class);
        addActivityFilter(hashSet, FingerprintEnrollIntroductionInternal.class);
        addActivityFilter(hashSet, FingerprintEnrollEnrolling.class);
        addActivityFilter(hashSet, AvatarPickerActivity.class);
        this.mSplitController.registerRule(new ActivityRule(hashSet, true));
    }

    private static void addActivityFilter(Set<ActivityFilter> set, Intent intent) {
        set.add(new ActivityFilter(COMPONENT_NAME_WILDCARD, intent.getAction()));
    }

    private void addActivityFilter(Set<ActivityFilter> set, Class<? extends Activity> cls) {
        set.add(new ActivityFilter(new ComponentName(this.mContext, cls), null));
    }
}
