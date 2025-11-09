package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ContextualCardLoader extends AsyncLoaderCompat<List<ContextualCard>> {
    static final String CONTEXTUAL_CARD_COUNT = "contextual_card_count";
    static final int DEFAULT_CARD_COUNT = 3;
    private final Context mContext;
    Uri mNotifyUri;
    private final ContentObserver mObserver;

    /* loaded from: classes.dex */
    public interface CardContentLoaderListener {
        void onFinishCardLoading(List<ContextualCard> list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(List<ContextualCard> list) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContextualCardLoader(Context context) {
        super(context);
        this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.homepage.contextualcards.ContextualCardLoader.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                if (ContextualCardLoader.this.isStarted()) {
                    ContextualCardLoader contextualCardLoader = ContextualCardLoader.this;
                    contextualCardLoader.mNotifyUri = uri;
                    contextualCardLoader.forceLoad();
                }
            }
        };
        this.mContext = context.getApplicationContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat, androidx.loader.content.Loader
    public void onStartLoading() {
        super.onStartLoading();
        this.mNotifyUri = null;
        this.mContext.getContentResolver().registerContentObserver(CardContentProvider.REFRESH_CARD_URI, false, this.mObserver);
        this.mContext.getContentResolver().registerContentObserver(CardContentProvider.DELETE_CARD_URI, false, this.mObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat, androidx.loader.content.Loader
    public void onStopLoading() {
        super.onStopLoading();
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public List<ContextualCard> loadInBackground() {
        ArrayList arrayList = new ArrayList();
        if (this.mContext.getResources().getBoolean(R$bool.config_use_legacy_suggestion)) {
            Log.d("ContextualCardLoader", "Skipping - in legacy suggestion mode");
            return arrayList;
        }
        Cursor contextualCardsFromProvider = getContextualCardsFromProvider();
        try {
            if (contextualCardsFromProvider.getCount() > 0) {
                contextualCardsFromProvider.moveToFirst();
                while (!contextualCardsFromProvider.isAfterLast()) {
                    ContextualCard contextualCard = new ContextualCard(contextualCardsFromProvider);
                    if (isLargeCard(contextualCard)) {
                        arrayList.add(contextualCard.mutate().setIsLargeCard(true).build());
                    } else {
                        arrayList.add(contextualCard);
                    }
                    contextualCardsFromProvider.moveToNext();
                }
            }
            contextualCardsFromProvider.close();
            return getDisplayableCards(arrayList);
        } catch (Throwable th) {
            if (contextualCardsFromProvider != null) {
                try {
                    contextualCardsFromProvider.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    List<ContextualCard> getDisplayableCards(List<ContextualCard> list) {
        List<ContextualCard> filterEligibleCards = filterEligibleCards(list);
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        final int cardCount = getCardCount();
        filterEligibleCards.forEach(new Consumer() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLoader$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ContextualCardLoader.lambda$getDisplayableCards$0(arrayList, cardCount, arrayList3, (ContextualCard) obj);
            }
        });
        final int size = cardCount - arrayList.size();
        filterEligibleCards.forEach(new Consumer() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLoader$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ContextualCardLoader.lambda$getDisplayableCards$1(arrayList2, size, arrayList3, (ContextualCard) obj);
            }
        });
        arrayList2.addAll(arrayList);
        if (!CardContentProvider.DELETE_CARD_URI.equals(this.mNotifyUri)) {
            FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1664, ContextualCardLogUtils.buildCardListLog(arrayList3));
        }
        return arrayList2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getDisplayableCards$0(List list, int i, List list2, ContextualCard contextualCard) {
        if (contextualCard.getCategory() != 6) {
            return;
        }
        if (list.size() < i) {
            list.add(contextualCard);
        } else {
            list2.add(contextualCard);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getDisplayableCards$1(List list, int i, List list2, ContextualCard contextualCard) {
        if (contextualCard.getCategory() == 6) {
            return;
        }
        if (list.size() < i) {
            list.add(contextualCard);
        } else {
            list2.add(contextualCard);
        }
    }

    int getCardCount() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), CONTEXTUAL_CARD_COUNT, 3);
    }

    Cursor getContextualCardsFromProvider() {
        return FeatureFactory.getFactory(this.mContext).getContextualCardFeatureProvider(this.mContext).getContextualCards();
    }

    List<ContextualCard> filterEligibleCards(List<ContextualCard> list) {
        if (list.isEmpty()) {
            return list;
        }
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(list.size());
        ArrayList arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        try {
            arrayList2 = newFixedThreadPool.invokeAll((List) list.stream().map(new Function() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLoader$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    EligibleCardChecker lambda$filterEligibleCards$2;
                    lambda$filterEligibleCards$2 = ContextualCardLoader.this.lambda$filterEligibleCards$2((ContextualCard) obj);
                    return lambda$filterEligibleCards$2;
                }
            }).collect(Collectors.toList()), 400L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.w("ContextualCardLoader", "Failed to get eligible states for all cards", e);
        }
        newFixedThreadPool.shutdown();
        for (int i = 0; i < arrayList2.size(); i++) {
            Future future = (Future) arrayList2.get(i);
            if (future.isCancelled()) {
                Log.w("ContextualCardLoader", "Timeout getting eligible state for card: " + list.get(i).getSliceUri());
            } else {
                try {
                    ContextualCard contextualCard = (ContextualCard) future.get();
                    if (contextualCard != null) {
                        arrayList.add(contextualCard);
                    }
                } catch (Exception e2) {
                    Log.w("ContextualCardLoader", "Failed to get eligible state for card", e2);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ EligibleCardChecker lambda$filterEligibleCards$2(ContextualCard contextualCard) {
        return new EligibleCardChecker(this.mContext, contextualCard);
    }

    private boolean isLargeCard(ContextualCard contextualCard) {
        return contextualCard.getSliceUri().equals(CustomSliceRegistry.CONTEXTUAL_WIFI_SLICE_URI) || contextualCard.getSliceUri().equals(CustomSliceRegistry.BLUETOOTH_DEVICES_SLICE_URI);
    }
}
