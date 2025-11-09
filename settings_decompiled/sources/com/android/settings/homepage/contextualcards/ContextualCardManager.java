package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.R$bool;
import com.android.settings.bluetooth.BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardLoader;
import com.android.settings.homepage.contextualcards.conditional.ConditionalCardController;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ContextualCardManager implements ContextualCardLoader.CardContentLoaderListener, ContextualCardUpdateListener, LifecycleObserver, OnSaveInstanceState {
    static final long CARD_CONTENT_LOADER_TIMEOUT_MS = 1000;
    static final String KEY_CONTEXTUAL_CARDS = "key_contextual_cards";
    static final String KEY_GLOBAL_CARD_LOADER_TIMEOUT = "global_card_loader_timeout_key";
    private final Context mContext;
    boolean mIsFirstLaunch;
    private final Lifecycle mLifecycle;
    private ContextualCardUpdateListener mListener;
    List<String> mSavedCards;
    long mStartTime;
    final List<ContextualCard> mContextualCards = new ArrayList();
    private final List<LifecycleObserver> mLifecycleObservers = new ArrayList();
    final ControllerRendererPool mControllerRendererPool = new ControllerRendererPool();

    public ContextualCardManager(Context context, Lifecycle lifecycle, Bundle bundle) {
        this.mContext = context;
        this.mLifecycle = lifecycle;
        lifecycle.addObserver(this);
        if (bundle == null) {
            this.mIsFirstLaunch = true;
            this.mSavedCards = null;
        } else {
            this.mSavedCards = bundle.getStringArrayList(KEY_CONTEXTUAL_CARDS);
        }
        for (int i : getSettingsCards()) {
            setupController(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadContextualCards(LoaderManager loaderManager, boolean z) {
        if (this.mContext.getResources().getBoolean(R$bool.config_use_legacy_suggestion)) {
            Log.w("ContextualCardManager", "Legacy suggestion contextual card enabled, skipping contextual cards.");
            return;
        }
        this.mStartTime = System.currentTimeMillis();
        CardContentLoaderCallbacks cardContentLoaderCallbacks = new CardContentLoaderCallbacks(this.mContext);
        cardContentLoaderCallbacks.setListener(this);
        if (!z) {
            loaderManager.initLoader(1, null, cardContentLoaderCallbacks);
            return;
        }
        this.mIsFirstLaunch = true;
        loaderManager.restartLoader(1, null, cardContentLoaderCallbacks);
    }

    private void loadCardControllers() {
        for (ContextualCard contextualCard : this.mContextualCards) {
            setupController(contextualCard.getCardType());
        }
    }

    int[] getSettingsCards() {
        return !FeatureFlagUtils.isEnabled(this.mContext, "settings_conditionals") ? new int[]{2} : new int[]{3, 2};
    }

    void setupController(int i) {
        ContextualCardController controller = this.mControllerRendererPool.getController(this.mContext, i);
        if (controller == null) {
            Log.w("ContextualCardManager", "Cannot find ContextualCardController for type " + i);
            return;
        }
        controller.setCardUpdateListener(this);
        if (!(controller instanceof LifecycleObserver) || this.mLifecycleObservers.contains(controller)) {
            return;
        }
        LifecycleObserver lifecycleObserver = (LifecycleObserver) controller;
        this.mLifecycleObservers.add(lifecycleObserver);
        this.mLifecycle.addObserver(lifecycleObserver);
    }

    List<ContextualCard> sortCards(List<ContextualCard> list) {
        List<ContextualCard> list2 = (List) list.stream().sorted(new Comparator() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda6
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$sortCards$0;
                lambda$sortCards$0 = ContextualCardManager.lambda$sortCards$0((ContextualCard) obj, (ContextualCard) obj2);
                return lambda$sortCards$0;
            }
        }).collect(Collectors.toList());
        List list3 = (List) list2.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$sortCards$1;
                lambda$sortCards$1 = ContextualCardManager.lambda$sortCards$1((ContextualCard) obj);
                return lambda$sortCards$1;
            }
        }).collect(Collectors.toList());
        list2.removeAll(list3);
        list2.addAll(list3);
        return list2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortCards$0(ContextualCard contextualCard, ContextualCard contextualCard2) {
        return Double.compare(contextualCard2.getRankingScore(), contextualCard.getRankingScore());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$sortCards$1(ContextualCard contextualCard) {
        return contextualCard.getCategory() == 6;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardUpdateListener
    public void onContextualCardUpdated(Map<Integer, List<ContextualCard>> map) {
        List list;
        final Set<Integer> keySet = map.keySet();
        if (keySet.isEmpty()) {
            final TreeSet<Integer> treeSet = new TreeSet<Integer>() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager.1
                {
                    add(3);
                    add(4);
                    add(5);
                }
            };
            list = (List) this.mContextualCards.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onContextualCardUpdated$2;
                    lambda$onContextualCardUpdated$2 = ContextualCardManager.lambda$onContextualCardUpdated$2(treeSet, (ContextualCard) obj);
                    return lambda$onContextualCardUpdated$2;
                }
            }).collect(Collectors.toList());
        } else {
            list = (List) this.mContextualCards.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onContextualCardUpdated$3;
                    lambda$onContextualCardUpdated$3 = ContextualCardManager.lambda$onContextualCardUpdated$3(keySet, (ContextualCard) obj);
                    return lambda$onContextualCardUpdated$3;
                }
            }).collect(Collectors.toList());
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        arrayList.addAll((Collection) map.values().stream().flatMap(new BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2()).collect(Collectors.toList()));
        this.mContextualCards.clear();
        this.mContextualCards.addAll(getCardsWithViewType(sortCards(arrayList)));
        loadCardControllers();
        if (this.mListener != null) {
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(0, this.mContextualCards);
            this.mListener.onContextualCardUpdated(arrayMap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onContextualCardUpdated$2(Set set, ContextualCard contextualCard) {
        return set.contains(Integer.valueOf(contextualCard.getCardType()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onContextualCardUpdated$3(Set set, ContextualCard contextualCard) {
        return !set.contains(Integer.valueOf(contextualCard.getCardType()));
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardLoader.CardContentLoaderListener
    public void onFinishCardLoading(List<ContextualCard> list) {
        long currentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        Log.d("ContextualCardManager", "Total loading time = " + currentTimeMillis);
        List<ContextualCard> cardsToKeep = getCardsToKeep(list);
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        if (!this.mIsFirstLaunch) {
            onContextualCardUpdated((Map) cardsToKeep.stream().collect(Collectors.groupingBy(new Function() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda8
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((ContextualCard) obj).getCardType());
                }
            })));
            metricsFeatureProvider.action(this.mContext, 1663, ContextualCardLogUtils.buildCardListLog(cardsToKeep));
            return;
        }
        if (currentTimeMillis <= getCardLoaderTimeout()) {
            onContextualCardUpdated((Map) list.stream().collect(Collectors.groupingBy(new Function() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda8
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((ContextualCard) obj).getCardType());
                }
            })));
            metricsFeatureProvider.action(this.mContext, 1663, ContextualCardLogUtils.buildCardListLog(list));
        } else {
            metricsFeatureProvider.action(0, 1685, 1502, null, (int) currentTimeMillis);
        }
        metricsFeatureProvider.action(this.mContext, 1662, (int) (System.currentTimeMillis() - this.mStartTime));
        this.mIsFirstLaunch = false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putStringArrayList(KEY_CONTEXTUAL_CARDS, (ArrayList) this.mContextualCards.stream().map(new Function() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ContextualCard) obj).getName();
            }
        }).collect(Collectors.toCollection(new ContextualCardManager$$ExternalSyntheticLambda5())));
    }

    public void onWindowFocusChanged(boolean z) {
        boolean z2 = false;
        for (ContextualCard contextualCard : new ArrayList(this.mContextualCards)) {
            ContextualCardController controller = getControllerRendererPool().getController(this.mContext, contextualCard.getCardType());
            if (controller instanceof ConditionalCardController) {
                z2 = true;
            }
            if (z && (controller instanceof OnStart)) {
                ((OnStart) controller).onStart();
            }
            if (!z && (controller instanceof OnStop)) {
                ((OnStop) controller).onStop();
            }
        }
        if (z2) {
            return;
        }
        ContextualCardController controller2 = getControllerRendererPool().getController(this.mContext, 3);
        if (z && (controller2 instanceof OnStart)) {
            ((OnStart) controller2).onStart();
        }
        if (z || !(controller2 instanceof OnStop)) {
            return;
        }
        ((OnStop) controller2).onStop();
    }

    public ControllerRendererPool getControllerRendererPool() {
        return this.mControllerRendererPool;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListener(ContextualCardUpdateListener contextualCardUpdateListener) {
        this.mListener = contextualCardUpdateListener;
    }

    List<ContextualCard> getCardsWithViewType(List<ContextualCard> list) {
        return list.isEmpty() ? list : getCardsWithSuggestionViewType(getCardsWithStickyViewType(list));
    }

    long getCardLoaderTimeout() {
        return Settings.Global.getLong(this.mContext.getContentResolver(), KEY_GLOBAL_CARD_LOADER_TIMEOUT, CARD_CONTENT_LOADER_TIMEOUT_MS);
    }

    private List<ContextualCard> getCardsWithSuggestionViewType(List<ContextualCard> list) {
        ArrayList arrayList = new ArrayList(list);
        int i = 1;
        while (i < arrayList.size()) {
            int i2 = i - 1;
            ContextualCard contextualCard = (ContextualCard) arrayList.get(i2);
            ContextualCard contextualCard2 = (ContextualCard) arrayList.get(i);
            if (contextualCard2.getCategory() == 1 && contextualCard.getCategory() == 1) {
                ContextualCard.Builder mutate = contextualCard.mutate();
                int i3 = SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH;
                arrayList.set(i2, mutate.setViewType(i3).build());
                arrayList.set(i, contextualCard2.mutate().setViewType(i3).build());
                i++;
            }
            i++;
        }
        return arrayList;
    }

    private List<ContextualCard> getCardsWithStickyViewType(List<ContextualCard> list) {
        ArrayList arrayList = new ArrayList(list);
        for (int i = 0; i < arrayList.size(); i++) {
            ContextualCard contextualCard = list.get(i);
            if (contextualCard.getCategory() == 6) {
                arrayList.set(i, contextualCard.mutate().setViewType(SliceContextualCardRenderer.VIEW_TYPE_STICKY).build());
            }
        }
        return arrayList;
    }

    List<ContextualCard> getCardsToKeep(List<ContextualCard> list) {
        if (this.mSavedCards != null) {
            List<ContextualCard> list2 = (List) list.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getCardsToKeep$4;
                    lambda$getCardsToKeep$4 = ContextualCardManager.this.lambda$getCardsToKeep$4((ContextualCard) obj);
                    return lambda$getCardsToKeep$4;
                }
            }).collect(Collectors.toList());
            this.mSavedCards = null;
            return list2;
        }
        return (List) list.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getCardsToKeep$5;
                lambda$getCardsToKeep$5 = ContextualCardManager.this.lambda$getCardsToKeep$5((ContextualCard) obj);
                return lambda$getCardsToKeep$5;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getCardsToKeep$4(ContextualCard contextualCard) {
        return this.mSavedCards.contains(contextualCard.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getCardsToKeep$5(ContextualCard contextualCard) {
        return this.mContextualCards.contains(contextualCard);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class CardContentLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ContextualCard>> {
        private Context mContext;
        private ContextualCardLoader.CardContentLoaderListener mListener;

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<ContextualCard>> loader) {
        }

        CardContentLoaderCallbacks(Context context) {
            this.mContext = context.getApplicationContext();
        }

        protected void setListener(ContextualCardLoader.CardContentLoaderListener cardContentLoaderListener) {
            this.mListener = cardContentLoaderListener;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<ContextualCard>> onCreateLoader(int i, Bundle bundle) {
            if (i == 1) {
                return new ContextualCardLoader(this.mContext);
            }
            throw new IllegalArgumentException("Unknown loader id: " + i);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<ContextualCard>> loader, List<ContextualCard> list) {
            ContextualCardLoader.CardContentLoaderListener cardContentLoaderListener = this.mListener;
            if (cardContentLoaderListener != null) {
                cardContentLoaderListener.onFinishCardLoading(list);
            }
        }
    }
}
