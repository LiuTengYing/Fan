package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import com.android.settings.R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardFeedbackDialog;
import com.android.settings.homepage.contextualcards.ContextualCardUpdateListener;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class SliceContextualCardController implements ContextualCardController {
    private ContextualCardUpdateListener mCardUpdateListener;
    private final Context mContext;

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onActionClick(ContextualCard contextualCard) {
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onPrimaryClick(ContextualCard contextualCard) {
    }

    public SliceContextualCardController(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onDismissed(final ContextualCard contextualCard) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SliceContextualCardController.this.lambda$onDismissed$0(contextualCard);
            }
        });
        showFeedbackDialog(contextualCard);
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1665, ContextualCardLogUtils.buildCardDismissLog(contextualCard));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismissed$0(ContextualCard contextualCard) {
        FeatureFactory.getFactory(this.mContext).getContextualCardFeatureProvider(this.mContext).markCardAsDismissed(this.mContext, contextualCard.getName());
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void setCardUpdateListener(ContextualCardUpdateListener contextualCardUpdateListener) {
        this.mCardUpdateListener = contextualCardUpdateListener;
    }

    void showFeedbackDialog(ContextualCard contextualCard) {
        String string = this.mContext.getString(R$string.config_contextual_card_feedback_email);
        if (isFeedbackEnabled(string)) {
            Intent intent = new Intent(this.mContext, ContextualCardFeedbackDialog.class);
            intent.putExtra("card_name", getSimpleCardName(contextualCard));
            intent.putExtra("feedback_email", string);
            intent.addFlags(268435456);
            this.mContext.startActivity(intent);
        }
    }

    boolean isFeedbackEnabled(String str) {
        return !TextUtils.isEmpty(str) && Build.IS_DEBUGGABLE;
    }

    private String getSimpleCardName(ContextualCard contextualCard) {
        String[] split = contextualCard.getName().split("/");
        return split[split.length - 1];
    }
}
