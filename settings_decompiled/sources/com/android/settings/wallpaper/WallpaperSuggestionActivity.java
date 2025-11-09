package com.android.settings.wallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchIndexableData;
import com.android.settings.R$string;
import com.android.settings.display.WallpaperPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class WallpaperSuggestionActivity extends StyleSuggestionActivityBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.wallpaper.WallpaperSuggestionActivity.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            WallpaperPreferenceController wallpaperPreferenceController = new WallpaperPreferenceController(context, "unused key");
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            String title = wallpaperPreferenceController.getTitle();
            searchIndexableRaw.title = title;
            searchIndexableRaw.screenTitle = title;
            ComponentName componentName = wallpaperPreferenceController.getComponentName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = componentName.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = componentName.getClassName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "wallpaper_type";
            searchIndexableRaw.keywords = wallpaperPreferenceController.getKeywords();
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };
    private String mWallpaperLaunchExtra;

    @Override // com.android.settings.wallpaper.StyleSuggestionActivityBase
    protected void addExtras(Intent intent) {
        if (WizardManagerHelper.isAnySetupWizard(intent)) {
            intent.putExtra("com.android.launcher3.WALLPAPER_FLAVOR", "wallpaper_only");
            String string = getResources().getString(R$string.config_wallpaper_picker_launch_extra);
            this.mWallpaperLaunchExtra = string;
            intent.putExtra(string, "app_launched_suw");
            return;
        }
        intent.putExtra("com.android.launcher3.WALLPAPER_FLAVOR", "focus_wallpaper");
    }

    public static boolean isSuggestionComplete(Context context) {
        return !StyleSuggestionActivityBase.isWallpaperServiceEnabled(context) || ((WallpaperManager) context.getSystemService("wallpaper")).getWallpaperId(1) > 0;
    }
}
