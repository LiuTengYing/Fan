package com.android.settings.homepage.contextualcards;
/* loaded from: classes.dex */
public interface ContextualCardController {
    void onActionClick(ContextualCard contextualCard);

    void onDismissed(ContextualCard contextualCard);

    void onPrimaryClick(ContextualCard contextualCard);

    void setCardUpdateListener(ContextualCardUpdateListener contextualCardUpdateListener);
}
