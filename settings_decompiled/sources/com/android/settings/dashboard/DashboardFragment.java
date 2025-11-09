package com.android.settings.dashboard;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$array;
import com.android.settings.R$drawable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.bluetooth.BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.CategoryMixin;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class DashboardFragment extends SettingsPreferenceFragment implements CategoryMixin.CategoryListener, PreferenceGroup.OnExpandButtonClickListener, BasePreferenceController.UiBlockListener {
    public static final String CATEGORY = "category";
    private static final String TAG = "DashboardFragment";
    private static final long TIMEOUT_MILLIS = 50;
    UiBlockerController mBlockerController;
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private boolean mListeningToCategoryChange;
    private DashboardTilePlaceholderPreferenceController mPlaceholderPreferenceController;
    private List<String> mSuppressInjectedTileKeys;
    final ArrayMap<String, List<DynamicDataObserver>> mDashboardTilePrefKeys = new ArrayMap<>();
    private final Map<Class, List<AbstractPreferenceController>> mPreferenceControllers = new ArrayMap();
    private final List<DynamicDataObserver> mRegisteredObservers = new ArrayList();
    private final List<AbstractPreferenceController> mControllers = new ArrayList();

    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getLogTag();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public abstract int getPreferenceScreenResId();

    protected boolean shouldForceRoundedIcon() {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSuppressInjectedTileKeys = Arrays.asList(context.getResources().getStringArray(R$array.config_suppress_injected_tile_keys));
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        List<AbstractPreferenceController> createPreferenceControllers = createPreferenceControllers(context);
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId()), createPreferenceControllers);
        if (createPreferenceControllers != null) {
            this.mControllers.addAll(createPreferenceControllers);
        }
        this.mControllers.addAll(filterControllers);
        final Lifecycle settingsLifecycle = getSettingsLifecycle();
        filterControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda14
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$0(Lifecycle.this, (BasePreferenceController) obj);
            }
        });
        final int metricsCategory = getMetricsCategory();
        this.mControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda15
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$1(metricsCategory, (AbstractPreferenceController) obj);
            }
        });
        DashboardTilePlaceholderPreferenceController dashboardTilePlaceholderPreferenceController = new DashboardTilePlaceholderPreferenceController(context);
        this.mPlaceholderPreferenceController = dashboardTilePlaceholderPreferenceController;
        this.mControllers.add(dashboardTilePlaceholderPreferenceController);
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            addPreferenceController(abstractPreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$0(Lifecycle lifecycle, BasePreferenceController basePreferenceController) {
        if (basePreferenceController instanceof LifecycleObserver) {
            lifecycle.addObserver((LifecycleObserver) basePreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$1(int i, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof BasePreferenceController) {
            ((BasePreferenceController) abstractPreferenceController).setMetricsCategory(i);
        }
    }

    void checkUiBlocker(List<AbstractPreferenceController> list) {
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$checkUiBlocker$2(arrayList, arrayList2, (AbstractPreferenceController) obj);
            }
        });
        if (arrayList.isEmpty()) {
            return;
        }
        UiBlockerController uiBlockerController = new UiBlockerController(arrayList);
        this.mBlockerController = uiBlockerController;
        uiBlockerController.start(new Runnable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFragment.this.lambda$checkUiBlocker$4(arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$2(List list, List list2, AbstractPreferenceController abstractPreferenceController) {
        if ((abstractPreferenceController instanceof BasePreferenceController.UiBlocker) && abstractPreferenceController.isAvailable()) {
            BasePreferenceController basePreferenceController = (BasePreferenceController) abstractPreferenceController;
            basePreferenceController.setUiBlockListener(this);
            list.add(abstractPreferenceController.getPreferenceKey());
            list2.add(basePreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$4(List list) {
        updatePreferenceVisibility(this.mPreferenceControllers);
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((BasePreferenceController) obj).setUiBlockerFinished(true);
            }
        });
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceManager().setPreferenceComparisonCallback(new PreferenceManager.SimplePreferenceComparisonCallback());
        if (bundle != null) {
            updatePreferenceStates();
        }
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryListener
    public void onCategoriesChanged(Set<String> set) {
        String categoryKey = getCategoryKey();
        if (this.mDashboardFeatureProvider.getTilesForCategory(categoryKey) == null) {
            return;
        }
        if (set == null) {
            refreshDashboardTiles(getLogTag());
        } else if (set.contains(categoryKey)) {
            Log.i(TAG, "refresh tiles for " + categoryKey);
            refreshDashboardTiles(getLogTag());
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        checkUiBlocker(this.mControllers);
        refreshAllPreferences(getLogTag());
        this.mControllers.stream().map(new Function() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Preference lambda$onCreatePreferences$5;
                lambda$onCreatePreferences$5 = DashboardFragment.this.lambda$onCreatePreferences$5((AbstractPreferenceController) obj);
                return lambda$onCreatePreferences$5;
            }
        }).filter(new Predicate() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda10
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Preference) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda11
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onCreatePreferences$6((Preference) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Preference lambda$onCreatePreferences$5(AbstractPreferenceController abstractPreferenceController) {
        return findPreference(abstractPreferenceController.getPreferenceKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$6(Preference preference) {
        preference.getExtras().putInt(CATEGORY, getMetricsCategory());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey()) == null) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof CategoryMixin.CategoryHandler) {
            this.mListeningToCategoryChange = true;
            ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().addCategoryListener(this);
        }
        final ContentResolver contentResolver = getContentResolver();
        this.mDashboardTilePrefKeys.values().stream().filter(new Predicate() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((List) obj);
            }
        }).flatMap(new BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2()).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onStart$7(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$7(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        if (this.mRegisteredObservers.contains(dynamicDataObserver)) {
            return;
        }
        lambda$registerDynamicDataObservers$11(contentResolver, dynamicDataObserver);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        RecyclerView listView = getListView();
        if (listView != null) {
            listView.setScrollBarStyle(R$drawable.nest_scroll_view_style);
        }
        updatePreferenceStates();
        writeElapsedTimeMetric(1729, "isParalleledControllers:false");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                if (abstractPreferenceController.handlePreferenceTreeClick(preference)) {
                    writePreferenceClickMetric(preference);
                    return true;
                }
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        unregisterDynamicDataObservers(new ArrayList(this.mRegisteredObservers));
        if (this.mListeningToCategoryChange) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CategoryMixin.CategoryHandler) {
                ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().removeCategoryListener(this);
            }
            this.mListeningToCategoryChange = false;
        }
    }

    public void onExpandButtonClick() {
        this.mMetricsFeatureProvider.action(0, 834, getMetricsCategory(), null, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> T use(Class<T> cls) {
        List<AbstractPreferenceController> list = this.mPreferenceControllers.get(cls);
        if (list != null) {
            if (list.size() > 1) {
                Log.w(TAG, "Multiple controllers of Class " + cls.getSimpleName() + " found, returning first one.");
            }
            return (T) list.get(0);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> List<T> useAll(Class<T> cls) {
        return (List<T>) this.mPreferenceControllers.getOrDefault(cls, Collections.emptyList());
    }

    protected void addPreferenceController(AbstractPreferenceController abstractPreferenceController) {
        if (this.mPreferenceControllers.get(abstractPreferenceController.getClass()) == null) {
            this.mPreferenceControllers.put(abstractPreferenceController.getClass(), new ArrayList());
        }
        this.mPreferenceControllers.get(abstractPreferenceController.getClass()).add(abstractPreferenceController);
    }

    public String getCategoryKey() {
        return DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean displayTile(Tile tile) {
        if (this.mSuppressInjectedTileKeys == null || !tile.hasKey()) {
            return true;
        }
        return !this.mSuppressInjectedTileKeys.contains(tile.getKey(getContext()));
    }

    private void displayResourceTiles() {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId <= 0) {
            return;
        }
        addPreferencesFromResource(preferenceScreenResId);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setOnExpandButtonClickListener(this);
        displayResourceTilesToScreen(preferenceScreen);
    }

    protected void displayResourceTilesToScreen(final PreferenceScreen preferenceScreen) {
        this.mPreferenceControllers.values().stream().flatMap(new BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2()).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda13
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractPreferenceController) obj).displayPreference(PreferenceScreen.this);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<List<AbstractPreferenceController>> getPreferenceControllers() {
        return this.mPreferenceControllers.values();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePreferenceStates() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                if (abstractPreferenceController.isAvailable()) {
                    String preferenceKey = abstractPreferenceController.getPreferenceKey();
                    if (TextUtils.isEmpty(preferenceKey)) {
                        Log.d(TAG, String.format("Preference key is %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                    } else {
                        Preference findPreference = preferenceScreen.findPreference(preferenceKey);
                        if (findPreference == null) {
                            Log.d(TAG, String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                        } else {
                            abstractPreferenceController.updateState(findPreference);
                        }
                    }
                }
            }
        }
    }

    private void refreshAllPreferences(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        displayResourceTiles();
        refreshDashboardTiles(str);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Log.d(str, "All preferences added, reporting fully drawn");
            activity.reportFullyDrawn();
        }
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    void updatePreferenceVisibility(Map<Class, List<AbstractPreferenceController>> map) {
        UiBlockerController uiBlockerController;
        if (getPreferenceScreen() == null || map == null || (uiBlockerController = this.mBlockerController) == null) {
            return;
        }
        boolean isBlockerFinished = uiBlockerController.isBlockerFinished();
        for (List<AbstractPreferenceController> list : map.values()) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                Preference findPreference = findPreference(abstractPreferenceController.getPreferenceKey());
                if (findPreference != null) {
                    boolean z = true;
                    if (abstractPreferenceController instanceof BasePreferenceController.UiBlocker) {
                        findPreference.setVisible((isBlockerFinished && abstractPreferenceController.isAvailable() && ((BasePreferenceController) abstractPreferenceController).getSavedPrefVisibility()) ? false : false);
                    } else {
                        findPreference.setVisible((isBlockerFinished && abstractPreferenceController.isAvailable()) ? false : false);
                    }
                }
            }
        }
    }

    private void refreshDashboardTiles(String str) {
        boolean z;
        List<DynamicDataObserver> bindPreferenceToTileAndGetObservers;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        DashboardCategory tilesForCategory = this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey());
        if (tilesForCategory == null) {
            Log.d(str, "NO dashboard tiles for " + str);
            return;
        }
        List<Tile> tiles = tilesForCategory.getTiles();
        if (tiles == null) {
            Log.d(str, "tile list is empty, skipping category " + tilesForCategory.key);
            return;
        }
        ArrayMap arrayMap = new ArrayMap(this.mDashboardTilePrefKeys);
        boolean shouldForceRoundedIcon = shouldForceRoundedIcon();
        final ArrayList arrayList = new ArrayList();
        for (Tile tile : tiles) {
            String dashboardKeyForTile = this.mDashboardFeatureProvider.getDashboardKeyForTile(tile);
            Log.d(TAG, "refreshDashboardTiles: " + dashboardKeyForTile);
            if (!"top_level_wellbeing".equals(dashboardKeyForTile) && !"top_level_google".equals(dashboardKeyForTile)) {
                if (TextUtils.isEmpty(dashboardKeyForTile)) {
                    Log.d(str, "tile does not contain a key, skipping " + tile);
                } else if (displayTile(tile)) {
                    if (this.mDashboardTilePrefKeys.containsKey(dashboardKeyForTile)) {
                        boolean z2 = shouldForceRoundedIcon;
                        bindPreferenceToTileAndGetObservers = this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), this, z2, preferenceScreen.findPreference(dashboardKeyForTile), tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                        z = shouldForceRoundedIcon;
                    } else {
                        Preference createPreference = createPreference(tile);
                        z = shouldForceRoundedIcon;
                        bindPreferenceToTileAndGetObservers = this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), this, shouldForceRoundedIcon, createPreference, tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                        preferenceScreen.addPreference(createPreference);
                        registerDynamicDataObservers(bindPreferenceToTileAndGetObservers);
                        this.mDashboardTilePrefKeys.put(dashboardKeyForTile, bindPreferenceToTileAndGetObservers);
                    }
                    if (bindPreferenceToTileAndGetObservers != null) {
                        arrayList.addAll(bindPreferenceToTileAndGetObservers);
                    }
                    arrayMap.remove(dashboardKeyForTile);
                    shouldForceRoundedIcon = z;
                }
            }
        }
        for (Map.Entry entry : arrayMap.entrySet()) {
            String str2 = (String) entry.getKey();
            this.mDashboardTilePrefKeys.remove(str2);
            Preference findPreference = preferenceScreen.findPreference(str2);
            if (findPreference != null) {
                preferenceScreen.removePreference(findPreference);
            }
            unregisterDynamicDataObservers((List) entry.getValue());
        }
        if (arrayList.isEmpty()) {
            return;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFragment.this.lambda$refreshDashboardTiles$10(arrayList, countDownLatch);
            }
        }).start();
        Log.d(str, "Start waiting observers");
        awaitObserverLatch(countDownLatch);
        Log.d(str, "Stop waiting observers");
        arrayList.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DynamicDataObserver) obj).updateUi();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshDashboardTiles$10(List list, CountDownLatch countDownLatch) {
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$refreshDashboardTiles$9((DynamicDataObserver) obj);
            }
        });
        countDownLatch.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshDashboardTiles$9(DynamicDataObserver dynamicDataObserver) {
        awaitObserverLatch(dynamicDataObserver.getCountDownLatch());
    }

    @Override // com.android.settings.core.BasePreferenceController.UiBlockListener
    public void onBlockerWorkFinished(BasePreferenceController basePreferenceController) {
        this.mBlockerController.countDown(basePreferenceController.getPreferenceKey());
        basePreferenceController.setUiBlockerFinished(this.mBlockerController.isBlockerFinished());
    }

    protected Preference createPreference(Tile tile) {
        if (tile instanceof ProviderTile) {
            return new SwitchPreference(getPrefContext());
        }
        if (tile.hasSwitch()) {
            return new PrimarySwitchPreference(getPrefContext());
        }
        return new Preference(getPrefContext());
    }

    void registerDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final ContentResolver contentResolver = getContentResolver();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$registerDynamicDataObservers$11(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: registerDynamicDataObserver */
    public void lambda$registerDynamicDataObservers$11(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d(TAG, "register observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        contentResolver.registerContentObserver(dynamicDataObserver.getUri(), false, dynamicDataObserver);
        this.mRegisteredObservers.add(dynamicDataObserver);
    }

    private void unregisterDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final ContentResolver contentResolver = getContentResolver();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$unregisterDynamicDataObservers$12(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterDynamicDataObservers$12(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d(TAG, "unregister observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        this.mRegisteredObservers.remove(dynamicDataObserver);
        contentResolver.unregisterContentObserver(dynamicDataObserver);
    }

    private void awaitObserverLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException unused) {
        }
    }
}
