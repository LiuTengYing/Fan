package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class ConditionHeaderContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE = R$layout.conditional_card_header;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public ConditionHeaderContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new ConditionHeaderCardHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        final ConditionHeaderCardHolder conditionHeaderCardHolder = (ConditionHeaderCardHolder) viewHolder;
        final MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        conditionHeaderCardHolder.icons.removeAllViews();
        ((ConditionHeaderContextualCard) contextualCard).getConditionalCards().forEach(new Consumer() { // from class: com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ConditionHeaderContextualCardRenderer.this.lambda$bindView$0(conditionHeaderCardHolder, (ContextualCard) obj);
            }
        });
        conditionHeaderCardHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConditionHeaderContextualCardRenderer.this.lambda$bindView$1(metricsFeatureProvider, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$0(ConditionHeaderCardHolder conditionHeaderCardHolder, ContextualCard contextualCard) {
        ImageView imageView = (ImageView) LayoutInflater.from(this.mContext).inflate(R$layout.conditional_card_header_icon, (ViewGroup) conditionHeaderCardHolder.icons, false);
        imageView.setImageDrawable(contextualCard.getIconDrawable());
        conditionHeaderCardHolder.icons.addView(imageView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$1(MetricsFeatureProvider metricsFeatureProvider, View view) {
        metricsFeatureProvider.action(0, 373, 1502, null, 1);
        ConditionContextualCardController conditionContextualCardController = (ConditionContextualCardController) this.mControllerRendererPool.getController(this.mContext, 4);
        conditionContextualCardController.setIsExpanded(true);
        conditionContextualCardController.onConditionsChanged();
    }

    /* loaded from: classes.dex */
    public static class ConditionHeaderCardHolder extends RecyclerView.ViewHolder {
        public final ImageView expandIndicator;
        public final LinearLayout icons;

        public ConditionHeaderCardHolder(View view) {
            super(view);
            this.icons = (LinearLayout) view.findViewById(R$id.header_icons_container);
            this.expandIndicator = (ImageView) view.findViewById(R$id.expand_indicator);
        }
    }
}
