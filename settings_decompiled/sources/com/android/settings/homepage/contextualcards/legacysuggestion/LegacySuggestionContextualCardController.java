package com.android.settings.homepage.contextualcards.legacysuggestion;

import android.app.PendingIntent;
import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardUpdateListener;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCard;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.suggestions.SuggestionController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class LegacySuggestionContextualCardController implements ContextualCardController, LifecycleObserver, OnStart, OnStop, SuggestionController.ServiceConnectionListener {
    private ContextualCardUpdateListener mCardUpdateListener;
    private final Context mContext;
    SuggestionController mSuggestionController;
    final List<ContextualCard> mSuggestions = new ArrayList();

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onActionClick(ContextualCard contextualCard) {
    }

    @Override // com.android.settingslib.suggestions.SuggestionController.ServiceConnectionListener
    public void onServiceDisconnected() {
    }

    public LegacySuggestionContextualCardController(Context context) {
        this.mContext = context;
        if (!context.getResources().getBoolean(R$bool.config_use_legacy_suggestion)) {
            Log.w("LegacySuggestCardCtrl", "Legacy suggestion contextual card disabled, skipping.");
        } else {
            this.mSuggestionController = new SuggestionController(context, FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSuggestionServiceComponent(), this);
        }
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onPrimaryClick(ContextualCard contextualCard) {
        try {
            ((LegacySuggestionContextualCard) contextualCard).getPendingIntent().send();
        } catch (PendingIntent.CanceledException unused) {
            Log.w("LegacySuggestCardCtrl", "Failed to start suggestion " + contextualCard.getTitleText());
        }
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onDismissed(ContextualCard contextualCard) {
        this.mSuggestionController.dismissSuggestions(((LegacySuggestionContextualCard) contextualCard).getSuggestion());
        this.mSuggestions.remove(contextualCard);
        updateAdapter();
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void setCardUpdateListener(ContextualCardUpdateListener contextualCardUpdateListener) {
        this.mCardUpdateListener = contextualCardUpdateListener;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        SuggestionController suggestionController = this.mSuggestionController;
        if (suggestionController == null) {
            return;
        }
        suggestionController.start();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        SuggestionController suggestionController = this.mSuggestionController;
        if (suggestionController == null) {
            return;
        }
        suggestionController.stop();
    }

    @Override // com.android.settingslib.suggestions.SuggestionController.ServiceConnectionListener
    public void onServiceConnected() {
        loadSuggestions();
    }

    private void loadSuggestions() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LegacySuggestionContextualCardController.this.lambda$loadSuggestions$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSuggestions$0() {
        SuggestionController suggestionController = this.mSuggestionController;
        if (suggestionController == null || this.mCardUpdateListener == null) {
            return;
        }
        List<Suggestion> suggestions = suggestionController.getSuggestions();
        String valueOf = suggestions == null ? "null" : String.valueOf(suggestions.size());
        Log.d("LegacySuggestCardCtrl", "Loaded suggests: " + valueOf);
        ArrayList arrayList = new ArrayList();
        if (suggestions != null) {
            for (Suggestion suggestion : suggestions) {
                LegacySuggestionContextualCard.Builder builder = new LegacySuggestionContextualCard.Builder();
                if (suggestion.getIcon() != null) {
                    builder.setIconDrawable(suggestion.getIcon().loadDrawable(this.mContext));
                }
                builder.setPendingIntent(suggestion.getPendingIntent()).setSuggestion(suggestion).setName(suggestion.getId()).setTitleText(suggestion.getTitle().toString()).setSummaryText(suggestion.getSummary().toString()).setViewType(LegacySuggestionContextualCardRenderer.VIEW_TYPE);
                arrayList.add(builder.build());
            }
        }
        this.mSuggestions.clear();
        this.mSuggestions.addAll(arrayList);
        updateAdapter();
    }

    private void updateAdapter() {
        final ArrayMap arrayMap = new ArrayMap();
        arrayMap.put(2, this.mSuggestions);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LegacySuggestionContextualCardController.this.lambda$updateAdapter$1(arrayMap);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAdapter$1(Map map) {
        this.mCardUpdateListener.onContextualCardUpdated(map);
    }
}
