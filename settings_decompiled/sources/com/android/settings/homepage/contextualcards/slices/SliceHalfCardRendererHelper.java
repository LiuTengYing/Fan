package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceMetadata;
import androidx.slice.core.SliceAction;
import com.android.settings.R$id;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.overlay.FeatureFactory;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SliceHalfCardRendererHelper {
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceHalfCardRendererHelper(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new HalfCardViewHolder(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void bindView(RecyclerView.ViewHolder viewHolder, final ContextualCard contextualCard, Slice slice) {
        final HalfCardViewHolder halfCardViewHolder = (HalfCardViewHolder) viewHolder;
        final SliceAction primaryAction = SliceMetadata.from(this.mContext, slice).getPrimaryAction();
        halfCardViewHolder.icon.setImageDrawable(primaryAction.getIcon().loadDrawable(this.mContext));
        halfCardViewHolder.title.setText(primaryAction.getTitle());
        halfCardViewHolder.content.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.slices.SliceHalfCardRendererHelper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SliceHalfCardRendererHelper.this.lambda$bindView$0(primaryAction, contextualCard, halfCardViewHolder, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$0(SliceAction sliceAction, ContextualCard contextualCard, HalfCardViewHolder halfCardViewHolder, View view) {
        try {
            sliceAction.getAction().send();
        } catch (PendingIntent.CanceledException unused) {
            Log.w("SliceHCRendererHelper", "Failed to start intent " + ((Object) sliceAction.getTitle()));
        }
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1666, ContextualCardLogUtils.buildCardClickLog(contextualCard, 0, 3, halfCardViewHolder.getAdapterPosition()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class HalfCardViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout content;
        public final ImageView icon;
        public final TextView title;

        public HalfCardViewHolder(View view) {
            super(view);
            this.content = (LinearLayout) view.findViewById(R$id.content);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
        }
    }
}
