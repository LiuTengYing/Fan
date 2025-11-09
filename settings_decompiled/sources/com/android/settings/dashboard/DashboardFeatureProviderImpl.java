package com.android.settings.dashboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$dimen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.activityembedding.ActivityEmbeddingUtils;
import com.android.settings.dashboard.profileselector.ProfileSelectDialog;
import com.android.settings.homepage.TopLevelHighlightMixin;
import com.android.settings.homepage.TopLevelSettings;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.drawer.ActivityTile;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AdaptiveIcon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class DashboardFeatureProviderImpl implements DashboardFeatureProvider {
    private final CategoryManager mCategoryManager;
    protected final Context mContext;
    private String mLastSummaryFromUri = "";
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;

    public DashboardFeatureProviderImpl(Context context) {
        this.mContext = context.getApplicationContext();
        this.mCategoryManager = CategoryManager.get(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public DashboardCategory getTilesForCategory(String str) {
        return this.mCategoryManager.getTilesByCategory(this.mContext, str);
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public List<DashboardCategory> getAllCategories() {
        return this.mCategoryManager.getCategories(this.mContext);
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public String getDashboardKeyForTile(Tile tile) {
        if (tile == null) {
            return null;
        }
        if (tile.hasKey()) {
            return tile.getKey(this.mContext);
        }
        return "dashboard_tile_pref_" + tile.getIntent().getComponent().getClassName();
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public List<DynamicDataObserver> bindPreferenceToTileAndGetObservers(final FragmentActivity fragmentActivity, final DashboardFragment dashboardFragment, boolean z, Preference preference, final Tile tile, final String str, int i) {
        String str2;
        String str3;
        if (preference == null) {
            return null;
        }
        if ("top_level_wellbeing".equals(str) || "top_level_google".equals(str)) {
            Log.d("DashboardFeatureImpl", "bindPreferenceToTileAndGetObservers: " + str);
            return null;
        }
        if (!TextUtils.isEmpty(str)) {
            preference.setKey(str);
        } else {
            preference.setKey(getDashboardKeyForTile(tile));
        }
        ArrayList arrayList = new ArrayList();
        DynamicDataObserver bindTitleAndGetObserver = bindTitleAndGetObserver(preference, tile);
        if (bindTitleAndGetObserver != null) {
            arrayList.add(bindTitleAndGetObserver);
        }
        DynamicDataObserver bindSummaryAndGetObserver = bindSummaryAndGetObserver(preference, tile);
        if (bindSummaryAndGetObserver != null) {
            arrayList.add(bindSummaryAndGetObserver);
        }
        DynamicDataObserver bindSwitchAndGetObserver = bindSwitchAndGetObserver(preference, tile);
        if (bindSwitchAndGetObserver != null) {
            arrayList.add(bindSwitchAndGetObserver);
        }
        bindIcon(preference, tile, z);
        if (tile instanceof ActivityTile) {
            final int metricsCategory = dashboardFragment.getMetricsCategory();
            Bundle metaData = tile.getMetaData();
            if (metaData != null) {
                str3 = metaData.getString("com.android.settings.FRAGMENT_CLASS");
                str2 = metaData.getString("com.android.settings.intent.action");
            } else {
                str2 = null;
                str3 = null;
            }
            if (!TextUtils.isEmpty(str3)) {
                preference.setFragment(str3);
            } else {
                final Intent intent = new Intent(tile.getIntent());
                intent.putExtra(":settings:source_metrics", metricsCategory);
                if (str2 != null) {
                    intent.setAction(str2);
                }
                if (dashboardFragment instanceof TopLevelSettings) {
                    ActivityEmbeddingRulesController.registerTwoPanePairRuleForSettingsHome(this.mContext, new ComponentName(tile.getPackageName(), tile.getComponentName()), str2, true);
                }
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference2) {
                        boolean lambda$bindPreferenceToTileAndGetObservers$0;
                        lambda$bindPreferenceToTileAndGetObservers$0 = DashboardFeatureProviderImpl.this.lambda$bindPreferenceToTileAndGetObservers$0(dashboardFragment, str, fragmentActivity, tile, intent, metricsCategory, preference2);
                        return lambda$bindPreferenceToTileAndGetObservers$0;
                    }
                });
            }
        }
        if (tile.hasOrder()) {
            String packageName = fragmentActivity.getPackageName();
            int order = tile.getOrder();
            if (TextUtils.equals(packageName, tile.getIntent().getComponent().getPackageName()) || i == Integer.MAX_VALUE) {
                preference.setOrder(order);
            } else {
                preference.setOrder(order + i);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$bindPreferenceToTileAndGetObservers$0(DashboardFragment dashboardFragment, String str, FragmentActivity fragmentActivity, Tile tile, Intent intent, int i, Preference preference) {
        TopLevelHighlightMixin topLevelHighlightMixin;
        boolean z;
        if ((dashboardFragment instanceof TopLevelSettings) && ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this.mContext)) {
            TopLevelSettings topLevelSettings = (TopLevelSettings) dashboardFragment;
            topLevelHighlightMixin = topLevelSettings.getHighlightMixin();
            z = topLevelSettings.isDuplicateClick(preference);
            topLevelSettings.setHighlightPreferenceKey(str);
        } else {
            topLevelHighlightMixin = null;
            z = false;
        }
        launchIntentOrSelectProfile(fragmentActivity, tile, intent, i, topLevelHighlightMixin, z);
        return true;
    }

    private DynamicDataObserver createDynamicDataObserver(final String str, final Uri uri, final Preference preference) {
        return new DynamicDataObserver() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl.1
            @Override // com.android.settings.dashboard.DynamicDataObserver
            public Uri getUri() {
                return uri;
            }

            @Override // com.android.settings.dashboard.DynamicDataObserver
            public void onDataChanged() {
                String str2 = str;
                str2.hashCode();
                char c = 65535;
                switch (str2.hashCode()) {
                    case -2097433649:
                        if (str2.equals("getDynamicTitle")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1844463779:
                        if (str2.equals("getDynamicSummary")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 162535197:
                        if (str2.equals("isChecked")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        DashboardFeatureProviderImpl.this.refreshTitle(uri, preference, this);
                        return;
                    case 1:
                        DashboardFeatureProviderImpl.this.refreshSummary(uri, preference, this);
                        return;
                    case 2:
                        DashboardFeatureProviderImpl.this.refreshSwitch(uri, preference, this);
                        return;
                    default:
                        return;
                }
            }
        };
    }

    private DynamicDataObserver bindTitleAndGetObserver(Preference preference, Tile tile) {
        CharSequence title = tile.getTitle(this.mContext.getApplicationContext());
        if (title != null) {
            preference.setTitle(title);
            return null;
        } else if (tile.getMetaData() == null || !tile.getMetaData().containsKey("com.android.settings.title_uri")) {
            return null;
        } else {
            preference.setTitle(R$string.summary_placeholder);
            return createDynamicDataObserver("getDynamicTitle", TileUtils.getCompleteUri(tile, "com.android.settings.title_uri", "getDynamicTitle"), preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshTitle(final Uri uri, final Preference preference, final DynamicDataObserver dynamicDataObserver) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshTitle$2(uri, preference, dynamicDataObserver);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshTitle$2(Uri uri, final Preference preference, DynamicDataObserver dynamicDataObserver) {
        final String textFromUri = TileUtils.getTextFromUri(this.mContext, uri, new ArrayMap(), "com.android.settings.title");
        if (TextUtils.equals(textFromUri, preference.getTitle())) {
            return;
        }
        dynamicDataObserver.post(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                Preference.this.setTitle(textFromUri);
            }
        });
    }

    private DynamicDataObserver bindSummaryAndGetObserver(Preference preference, Tile tile) {
        CharSequence summary = tile.getSummary(this.mContext);
        if (summary != null) {
            preference.setSummary(summary);
            return null;
        } else if (tile.getMetaData() == null || !tile.getMetaData().containsKey("com.android.settings.summary_uri")) {
            return null;
        } else {
            preference.setSummary(this.mLastSummaryFromUri);
            return createDynamicDataObserver("getDynamicSummary", TileUtils.getCompleteUri(tile, "com.android.settings.summary_uri", "getDynamicSummary"), preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSummary(final Uri uri, final Preference preference, final DynamicDataObserver dynamicDataObserver) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSummary$4(uri, preference, dynamicDataObserver);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshSummary$4(Uri uri, final Preference preference, DynamicDataObserver dynamicDataObserver) {
        final String textFromUri = TileUtils.getTextFromUri(this.mContext, uri, new ArrayMap(), "com.android.settings.summary");
        if (!TextUtils.equals(textFromUri, preference.getSummary())) {
            dynamicDataObserver.post(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    Preference.this.setSummary(textFromUri);
                }
            });
        }
        this.mLastSummaryFromUri = textFromUri;
    }

    private DynamicDataObserver bindSwitchAndGetObserver(Preference preference, Tile tile) {
        if (tile.hasSwitch()) {
            final Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.switch_uri", "onCheckedChanged");
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda3
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference2, Object obj) {
                    boolean lambda$bindSwitchAndGetObserver$5;
                    lambda$bindSwitchAndGetObserver$5 = DashboardFeatureProviderImpl.this.lambda$bindSwitchAndGetObserver$5(completeUri, preference2, obj);
                    return lambda$bindSwitchAndGetObserver$5;
                }
            });
            Uri completeUri2 = TileUtils.getCompleteUri(tile, "com.android.settings.switch_uri", "isChecked");
            setSwitchEnabled(preference, false);
            return createDynamicDataObserver("isChecked", completeUri2, preference);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$bindSwitchAndGetObserver$5(Uri uri, Preference preference, Object obj) {
        onCheckedChanged(uri, preference, ((Boolean) obj).booleanValue());
        return true;
    }

    private void onCheckedChanged(final Uri uri, final Preference preference, final boolean z) {
        setSwitchEnabled(preference, false);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$onCheckedChanged$7(uri, z, preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckedChanged$7(Uri uri, final boolean z, final Preference preference) {
        final Bundle putBooleanToUriAndGetResult = TileUtils.putBooleanToUriAndGetResult(this.mContext, uri, new ArrayMap(), "checked_state", z);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$onCheckedChanged$6(preference, putBooleanToUriAndGetResult, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckedChanged$6(Preference preference, Bundle bundle, boolean z) {
        setSwitchEnabled(preference, true);
        if (bundle.getBoolean("set_checked_error")) {
            setSwitchChecked(preference, !z);
            String string = bundle.getString("set_checked_error_message");
            if (TextUtils.isEmpty(string)) {
                return;
            }
            Toast.makeText(this.mContext, string, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSwitch(final Uri uri, final Preference preference, final DynamicDataObserver dynamicDataObserver) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSwitch$9(uri, dynamicDataObserver, preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshSwitch$9(Uri uri, DynamicDataObserver dynamicDataObserver, final Preference preference) {
        final boolean booleanFromUri = TileUtils.getBooleanFromUri(this.mContext, uri, new ArrayMap(), "checked_state");
        dynamicDataObserver.post(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSwitch$8(preference, booleanFromUri);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshSwitch$8(Preference preference, boolean z) {
        setSwitchChecked(preference, z);
        setSwitchEnabled(preference, true);
    }

    private void setSwitchChecked(Preference preference, boolean z) {
        if (preference instanceof PrimarySwitchPreference) {
            ((PrimarySwitchPreference) preference).setChecked(z);
        } else if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked(z);
        }
    }

    private void setSwitchEnabled(Preference preference, boolean z) {
        if (preference instanceof PrimarySwitchPreference) {
            ((PrimarySwitchPreference) preference).setSwitchEnabled(z);
        } else {
            preference.setEnabled(z);
        }
    }

    void bindIcon(final Preference preference, final Tile tile, final boolean z) {
        if (tile.getMetaData() != null && tile.getMetaData().containsKey("com.android.settings.icon_uri")) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DashboardFeatureProviderImpl.this.lambda$bindIcon$11(tile, preference, z);
                }
            });
            return;
        }
        Icon icon = tile.getIcon(preference.getContext());
        if (icon == null) {
            return;
        }
        setPreferenceIcon(preference, tile, z, tile.getPackageName(), icon);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindIcon$11(final Tile tile, final Preference preference, final boolean z) {
        String packageName;
        Intent intent = tile.getIntent();
        if (!TextUtils.isEmpty(intent.getPackage())) {
            packageName = intent.getPackage();
        } else {
            packageName = intent.getComponent() != null ? intent.getComponent().getPackageName() : null;
        }
        ArrayMap arrayMap = new ArrayMap();
        Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.icon_uri", "getProviderIcon");
        final Pair<String, Integer> iconFromUri = TileUtils.getIconFromUri(this.mContext, packageName, completeUri, arrayMap);
        if (iconFromUri == null) {
            Log.w("DashboardFeatureImpl", "Failed to get icon from uri " + completeUri);
            return;
        }
        final Icon createWithResource = Icon.createWithResource((String) iconFromUri.first, ((Integer) iconFromUri.second).intValue());
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFeatureProviderImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$bindIcon$10(preference, tile, z, iconFromUri, createWithResource);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindIcon$10(Preference preference, Tile tile, boolean z, Pair pair, Icon icon) {
        setPreferenceIcon(preference, tile, z, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, 17170445));
        setPreferenceIcon(preference, tile, z, (String) pair.first, icon);
    }

    private void setPreferenceIcon(Preference preference, Tile tile, boolean z, String str, Icon icon) {
        Drawable loadDrawable = icon.loadDrawable(preference.getContext());
        if (loadDrawable == null) {
            Log.w("DashboardFeatureImpl", "Set null preference icon for: " + str);
            preference.setIcon((Drawable) null);
            return;
        }
        if (TextUtils.equals(tile.getCategory(), "com.android.settings.category.ia.homepage")) {
            loadDrawable.setTint(Utils.getHomepageIconColor(preference.getContext()));
        } else if (z && !TextUtils.equals(this.mContext.getPackageName(), str)) {
            AdaptiveIcon adaptiveIcon = new AdaptiveIcon(this.mContext, loadDrawable, R$dimen.dashboard_tile_foreground_image_inset);
            adaptiveIcon.setBackgroundColor(this.mContext, tile);
            loadDrawable = adaptiveIcon;
        }
        preference.setIcon(loadDrawable);
    }

    private void launchIntentOrSelectProfile(FragmentActivity fragmentActivity, Tile tile, Intent intent, int i, TopLevelHighlightMixin topLevelHighlightMixin, boolean z) {
        if (!isIntentResolvable(intent)) {
            Log.w("DashboardFeatureImpl", "Cannot resolve intent, skipping. " + intent);
            return;
        }
        ProfileSelectDialog.updateUserHandlesIfNeeded(this.mContext, tile);
        if (tile.userHandle == null || tile.isPrimaryProfileOnly()) {
            if (z) {
                return;
            }
            this.mMetricsFeatureProvider.logStartedIntent(intent, i);
            fragmentActivity.startActivity(intent);
        } else if (tile.userHandle.size() == 1) {
            if (z) {
                return;
            }
            this.mMetricsFeatureProvider.logStartedIntent(intent, i);
            fragmentActivity.startActivityAsUser(intent, tile.userHandle.get(0));
        } else {
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (userHandle != null && tile.userHandle.contains(userHandle)) {
                if (z) {
                    return;
                }
                this.mMetricsFeatureProvider.logStartedIntent(intent, i);
                fragmentActivity.startActivityAsUser(intent, userHandle);
                return;
            }
            List<UserHandle> resolvableUsers = getResolvableUsers(intent, tile);
            if (resolvableUsers.size() != 1) {
                this.mMetricsFeatureProvider.logStartedIntent(intent, i);
                ProfileSelectDialog.show(fragmentActivity.getSupportFragmentManager(), tile, i, topLevelHighlightMixin, topLevelHighlightMixin, topLevelHighlightMixin);
            } else if (z) {
            } else {
                this.mMetricsFeatureProvider.logStartedIntent(intent, i);
                fragmentActivity.startActivityAsUser(intent, resolvableUsers.get(0));
            }
        }
    }

    private boolean isIntentResolvable(Intent intent) {
        return this.mPackageManager.resolveActivity(intent, 0) != null;
    }

    private List<UserHandle> getResolvableUsers(Intent intent, Tile tile) {
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        ArrayList arrayList = new ArrayList();
        Iterator<UserHandle> it = tile.userHandle.iterator();
        while (it.hasNext()) {
            UserHandle next = it.next();
            if (!Utils.isCloneProfile(next, userManager) && this.mPackageManager.resolveActivityAsUser(intent, 0, next.getIdentifier()) != null) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }
}
