package com.android.settings.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.android.settingslib.search.SearchIndexableResources;
import com.android.settingslib.search.SearchIndexableResourcesMobile;
/* loaded from: classes.dex */
public class SearchFeatureProviderImpl implements SearchFeatureProvider {
    private SearchIndexableResources mSearchIndexableResources;

    protected boolean isSignatureAllowlisted(Context context, String str) {
        return false;
    }

    @Override // com.android.settings.search.SearchFeatureProvider
    public void verifyLaunchSearchResultPageCaller(Context context, ComponentName componentName) {
        if (componentName == null) {
            throw new IllegalArgumentException("ExternalSettingsTrampoline intents must be called with startActivityForResult");
        }
        String packageName = componentName.getPackageName();
        boolean z = TextUtils.equals(packageName, context.getPackageName()) || TextUtils.equals(getSettingsIntelligencePkgName(context), packageName);
        boolean isSignatureAllowlisted = isSignatureAllowlisted(context, componentName.getPackageName());
        if (!z && !isSignatureAllowlisted) {
            throw new SecurityException("Search result intents must be called with from an allowlisted package.");
        }
    }

    @Override // com.android.settings.search.SearchFeatureProvider
    public SearchIndexableResources getSearchIndexableResources() {
        if (this.mSearchIndexableResources == null) {
            this.mSearchIndexableResources = new SearchIndexableResourcesMobile();
        }
        return this.mSearchIndexableResources;
    }

    @Override // com.android.settings.search.SearchFeatureProvider
    public Intent buildSearchIntent(Context context, int i) {
        return new Intent("android.settings.APP_SEARCH_SETTINGS").setPackage(getSettingsIntelligencePkgName(context)).putExtra("android.intent.extra.REFERRER", buildReferrer(context, i));
    }

    private static Uri buildReferrer(Context context, int i) {
        return new Uri.Builder().scheme("android-app").authority(context.getPackageName()).path(String.valueOf(i)).build();
    }
}
