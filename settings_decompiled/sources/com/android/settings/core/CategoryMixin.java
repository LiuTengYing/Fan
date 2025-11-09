package com.android.settings.core;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.core.CategoryMixin;
import com.android.settings.dashboard.CategoryManager;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class CategoryMixin implements LifecycleObserver {
    private static final ArraySet<ComponentName> sTileDenylist = new ArraySet<>();
    private int mCategoriesUpdateTaskCount;
    private final Context mContext;
    private final PackageReceiver mPackageReceiver = new PackageReceiver();
    private final List<CategoryListener> mCategoryListeners = new ArrayList();
    private boolean mFirstOnResume = true;

    /* loaded from: classes.dex */
    public interface CategoryHandler {
        CategoryMixin getCategoryMixin();
    }

    /* loaded from: classes.dex */
    public interface CategoryListener {
        void onCategoriesChanged(Set<String> set);
    }

    public CategoryMixin(Context context) {
        this.mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
        if (this.mFirstOnResume) {
            Log.d("CategoryMixin", "Skip categories update");
            this.mFirstOnResume = false;
            return;
        }
        updateCategories();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mPackageReceiver);
    }

    public void addCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.add(categoryListener);
    }

    public void removeCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.remove(categoryListener);
    }

    public void updateCategories() {
        updateCategories(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addToDenylist(ComponentName componentName) {
        sTileDenylist.add(componentName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeFromDenylist(ComponentName componentName) {
        sTileDenylist.remove(componentName);
    }

    void onCategoriesChanged(final Set<String> set) {
        this.mCategoryListeners.forEach(new Consumer() { // from class: com.android.settings.core.CategoryMixin$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((CategoryMixin.CategoryListener) obj).onCategoriesChanged(set);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCategories(boolean z) {
        if (this.mCategoriesUpdateTaskCount < 2) {
            new CategoriesUpdateTask().execute(Boolean.valueOf(z));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CategoriesUpdateTask extends AsyncTask<Boolean, Void, Set<String>> {
        private final CategoryManager mCategoryManager;
        private Map<ComponentName, Tile> mPreviousTileMap;

        CategoriesUpdateTask() {
            CategoryMixin.this.mCategoriesUpdateTaskCount++;
            this.mCategoryManager = CategoryManager.get(CategoryMixin.this.mContext);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Set<String> doInBackground(Boolean... boolArr) {
            this.mPreviousTileMap = this.mCategoryManager.getTileByComponentMap();
            this.mCategoryManager.reloadAllCategories(CategoryMixin.this.mContext);
            this.mCategoryManager.updateCategoryFromDenylist(CategoryMixin.sTileDenylist);
            return getChangedCategories(boolArr[0].booleanValue());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Set<String> set) {
            if (set == null || !set.isEmpty()) {
                CategoryMixin.this.onCategoriesChanged(set);
            }
            CategoryMixin categoryMixin = CategoryMixin.this;
            categoryMixin.mCategoriesUpdateTaskCount--;
        }

        private Set<String> getChangedCategories(boolean z) {
            if (z) {
                final ArraySet arraySet = new ArraySet();
                Map<ComponentName, Tile> tileByComponentMap = this.mCategoryManager.getTileByComponentMap();
                tileByComponentMap.forEach(new BiConsumer() { // from class: com.android.settings.core.CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        CategoryMixin.CategoriesUpdateTask.this.lambda$getChangedCategories$0(arraySet, (ComponentName) obj, (Tile) obj2);
                    }
                });
                ArraySet arraySet2 = new ArraySet(this.mPreviousTileMap.keySet());
                arraySet2.removeAll(tileByComponentMap.keySet());
                arraySet2.forEach(new Consumer() { // from class: com.android.settings.core.CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        CategoryMixin.CategoriesUpdateTask.this.lambda$getChangedCategories$1(arraySet, (ComponentName) obj);
                    }
                });
                return arraySet;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$0(Set set, ComponentName componentName, Tile tile) {
            Tile tile2 = this.mPreviousTileMap.get(componentName);
            if (tile2 == null) {
                Log.i("CategoryMixin", "Tile added: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            } else if (TextUtils.equals(tile.getTitle(CategoryMixin.this.mContext), tile2.getTitle(CategoryMixin.this.mContext)) && TextUtils.equals(tile.getSummary(CategoryMixin.this.mContext), tile2.getSummary(CategoryMixin.this.mContext))) {
            } else {
                Log.i("CategoryMixin", "Tile changed: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$1(Set set, ComponentName componentName) {
            Log.i("CategoryMixin", "Tile removed: " + componentName.flattenToShortString());
            set.add(this.mPreviousTileMap.get(componentName).getCategory());
        }
    }

    /* loaded from: classes.dex */
    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            CategoryMixin.this.updateCategories(true);
        }
    }
}
