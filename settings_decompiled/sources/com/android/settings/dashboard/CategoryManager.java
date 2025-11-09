package com.android.settings.dashboard;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.settings.homepage.HighlightableMenu;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.settingslib.drawer.CategoryKey;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class CategoryManager {
    private static CategoryManager sInstance;
    private List<DashboardCategory> mCategories;
    private final InterestingConfigChanges mInterestingConfigChanges;
    private final Map<Pair<String, String>, Tile> mTileByComponentCache = new ArrayMap();
    private final Map<String, DashboardCategory> mCategoryByKeyMap = new ArrayMap();

    private void logTiles(Context context) {
    }

    public static CategoryManager get(Context context) {
        if (sInstance == null) {
            sInstance = new CategoryManager(context);
        }
        return sInstance;
    }

    CategoryManager(Context context) {
        InterestingConfigChanges interestingConfigChanges = new InterestingConfigChanges();
        this.mInterestingConfigChanges = interestingConfigChanges;
        interestingConfigChanges.applyNewConfig(context.getResources());
    }

    public synchronized DashboardCategory getTilesByCategory(Context context, String str) {
        tryInitCategories(context);
        return this.mCategoryByKeyMap.get(str);
    }

    public synchronized List<DashboardCategory> getCategories(Context context) {
        tryInitCategories(context);
        return this.mCategories;
    }

    public synchronized void reloadAllCategories(Context context) {
        boolean applyNewConfig = this.mInterestingConfigChanges.applyNewConfig(context.getResources());
        this.mCategories = null;
        tryInitCategories(context, applyNewConfig);
    }

    public synchronized void updateCategoryFromDenylist(Set<ComponentName> set) {
        if (this.mCategories == null) {
            Log.w("CategoryManager", "Category is null, skipping denylist update");
            return;
        }
        for (int i = 0; i < this.mCategories.size(); i++) {
            DashboardCategory dashboardCategory = this.mCategories.get(i);
            int i2 = 0;
            while (i2 < dashboardCategory.getTilesCount()) {
                if (set.contains(dashboardCategory.getTile(i2).getIntent().getComponent())) {
                    int i3 = i2 - 1;
                    dashboardCategory.removeTile(i2);
                    i2 = i3;
                }
                i2++;
            }
        }
    }

    public synchronized Map<ComponentName, Tile> getTileByComponentMap() {
        final ArrayMap arrayMap = new ArrayMap();
        List<DashboardCategory> list = this.mCategories;
        if (list == null) {
            Log.w("CategoryManager", "Category is null, no tiles");
            return arrayMap;
        }
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.CategoryManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CategoryManager.lambda$getTileByComponentMap$0(arrayMap, (DashboardCategory) obj);
            }
        });
        return arrayMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getTileByComponentMap$0(Map map, DashboardCategory dashboardCategory) {
        for (int i = 0; i < dashboardCategory.getTilesCount(); i++) {
            Tile tile = dashboardCategory.getTile(i);
            map.put(tile.getIntent().getComponent(), tile);
        }
    }

    private synchronized void tryInitCategories(Context context) {
        tryInitCategories(context, false);
    }

    private synchronized void tryInitCategories(Context context, boolean z) {
        if (this.mCategories == null) {
            boolean isEmpty = this.mCategoryByKeyMap.isEmpty();
            if (z) {
                this.mTileByComponentCache.clear();
            }
            this.mCategoryByKeyMap.clear();
            List<DashboardCategory> categories = TileUtils.getCategories(context, this.mTileByComponentCache);
            this.mCategories = categories;
            for (DashboardCategory dashboardCategory : categories) {
                this.mCategoryByKeyMap.put(dashboardCategory.key, dashboardCategory);
            }
            backwardCompatCleanupForCategory(this.mTileByComponentCache, this.mCategoryByKeyMap);
            sortCategories(context, this.mCategoryByKeyMap);
            filterDuplicateTiles(this.mCategoryByKeyMap);
            if (isEmpty) {
                logTiles(context);
                DashboardCategory dashboardCategory2 = this.mCategoryByKeyMap.get("com.android.settings.category.ia.homepage");
                if (dashboardCategory2 == null) {
                    return;
                }
                for (Tile tile : dashboardCategory2.getTiles()) {
                    String key = tile.getKey(context);
                    if (!"Digital Wellbeing & parental controls".equals(tile.getTitle(context)) && !"Google".equals(tile.getTitle(context))) {
                        if (TextUtils.isEmpty(key)) {
                            Log.w("CategoryManager", "Key hint missing for homepage tile: " + ((Object) tile.getTitle(context)));
                        } else {
                            Log.d("CategoryManager", "tryInitCategories: " + ((Object) tile.getTitle(context)));
                            HighlightableMenu.addMenuKey(key);
                        }
                    }
                    return;
                }
            }
        }
    }

    synchronized void backwardCompatCleanupForCategory(Map<Pair<String, String>, Tile> map, Map<String, DashboardCategory> map2) {
        HashMap hashMap = new HashMap();
        for (Map.Entry<Pair<String, String>, Tile> entry : map.entrySet()) {
            String str = (String) entry.getKey().first;
            List list = (List) hashMap.get(str);
            if (list == null) {
                list = new ArrayList();
                hashMap.put(str, list);
            }
            list.add(entry.getValue());
        }
        for (Map.Entry entry2 : hashMap.entrySet()) {
            List<Tile> list2 = (List) entry2.getValue();
            Iterator it = list2.iterator();
            boolean z = true;
            boolean z2 = false;
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (!CategoryKey.KEY_COMPAT_MAP.containsKey(((Tile) it.next()).getCategory())) {
                    break;
                } else {
                    z2 = true;
                }
            }
            if (z2 && !z) {
                for (Tile tile : list2) {
                    String str2 = CategoryKey.KEY_COMPAT_MAP.get(tile.getCategory());
                    tile.setCategory(str2);
                    DashboardCategory dashboardCategory = map2.get(str2);
                    if (dashboardCategory == null) {
                        dashboardCategory = new DashboardCategory(str2);
                        map2.put(str2, dashboardCategory);
                    }
                    dashboardCategory.addTile(tile);
                }
            }
        }
    }

    synchronized void sortCategories(Context context, Map<String, DashboardCategory> map) {
        for (Map.Entry<String, DashboardCategory> entry : map.entrySet()) {
            entry.getValue().sortTiles(context.getPackageName());
        }
    }

    synchronized void filterDuplicateTiles(Map<String, DashboardCategory> map) {
        for (Map.Entry<String, DashboardCategory> entry : map.entrySet()) {
            DashboardCategory value = entry.getValue();
            int tilesCount = value.getTilesCount();
            ArraySet arraySet = new ArraySet();
            ArraySet arraySet2 = new ArraySet();
            for (int i = tilesCount - 1; i >= 0; i--) {
                Tile tile = value.getTile(i);
                if (tile instanceof ProviderTile) {
                    String description = tile.getDescription();
                    if (arraySet.contains(description)) {
                        value.removeTile(i);
                    } else {
                        arraySet.add(description);
                    }
                } else {
                    ComponentName component = tile.getIntent().getComponent();
                    if (arraySet2.contains(component)) {
                        value.removeTile(i);
                    } else {
                        arraySet2.add(component);
                    }
                }
            }
        }
    }
}
