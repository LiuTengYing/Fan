package com.android.settings.homepage.contextualcards.legacysuggestion;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
/* loaded from: classes.dex */
public class LegacySuggestionContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE = R$layout.legacy_suggestion_tile;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public LegacySuggestionContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new LegacySuggestionViewHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, final ContextualCard contextualCard) {
        LegacySuggestionViewHolder legacySuggestionViewHolder = (LegacySuggestionViewHolder) viewHolder;
        final ContextualCardController controller = this.mControllerRendererPool.getController(this.mContext, contextualCard.getCardType());
        legacySuggestionViewHolder.icon.setImageDrawable(contextualCard.getIconDrawable());
        legacySuggestionViewHolder.title.setText(contextualCard.getTitleText());
        legacySuggestionViewHolder.summary.setText(contextualCard.getSummaryText());
        legacySuggestionViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardRenderer$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ContextualCardController.this.onPrimaryClick(contextualCard);
            }
        });
        legacySuggestionViewHolder.closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardRenderer$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ContextualCardController.this.onDismissed(contextualCard);
            }
        });
    }

    /* loaded from: classes.dex */
    private static class LegacySuggestionViewHolder extends RecyclerView.ViewHolder {
        public final View closeButton;
        public final ImageView icon;
        public final TextView summary;
        public final TextView title;

        public LegacySuggestionViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
            this.summary = (TextView) view.findViewById(16908304);
            this.closeButton = view.findViewById(R$id.close_button);
        }
    }
}
