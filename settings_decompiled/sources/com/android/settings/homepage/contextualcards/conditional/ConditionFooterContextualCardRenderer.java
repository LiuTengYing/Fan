package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class ConditionFooterContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE = R$layout.conditional_card_footer;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public ConditionFooterContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new ConditionFooterCardHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        final MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.conditional.ConditionFooterContextualCardRenderer$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConditionFooterContextualCardRenderer.this.lambda$bindView$0(metricsFeatureProvider, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$0(MetricsFeatureProvider metricsFeatureProvider, View view) {
        metricsFeatureProvider.action(0, 373, 1502, null, 0);
        ConditionContextualCardController conditionContextualCardController = (ConditionContextualCardController) this.mControllerRendererPool.getController(this.mContext, 5);
        conditionContextualCardController.setIsExpanded(false);
        conditionContextualCardController.onConditionsChanged();
    }

    /* loaded from: classes.dex */
    public static class ConditionFooterCardHolder extends RecyclerView.ViewHolder {
        public ConditionFooterCardHolder(View view) {
            super(view);
        }
    }
}
