package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.widget.EventInfo;
import androidx.slice.widget.SliceView;
import com.android.settings.R$id;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.overlay.FeatureFactory;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SliceFullCardRendererHelper {
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceFullCardRendererHelper(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new SliceViewHolder(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void bindView(RecyclerView.ViewHolder viewHolder, final ContextualCard contextualCard, Slice slice) {
        final SliceViewHolder sliceViewHolder = (SliceViewHolder) viewHolder;
        sliceViewHolder.sliceView.setScrollable(false);
        sliceViewHolder.sliceView.setTag(contextualCard.getSliceUri());
        sliceViewHolder.sliceView.setMode(2);
        sliceViewHolder.sliceView.setSlice(slice);
        sliceViewHolder.sliceView.setOnSliceActionListener(new SliceView.OnSliceActionListener() { // from class: com.android.settings.homepage.contextualcards.slices.SliceFullCardRendererHelper$$ExternalSyntheticLambda0
            @Override // androidx.slice.widget.SliceView.OnSliceActionListener
            public final void onSliceAction(EventInfo eventInfo, SliceItem sliceItem) {
                SliceFullCardRendererHelper.this.lambda$bindView$0(contextualCard, sliceViewHolder, eventInfo, sliceItem);
            }
        });
        sliceViewHolder.sliceView.setShowTitleItems(true);
        if (contextualCard.isLargeCard()) {
            sliceViewHolder.sliceView.setShowHeaderDivider(true);
            sliceViewHolder.sliceView.setShowActionDividers(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$0(ContextualCard contextualCard, SliceViewHolder sliceViewHolder, EventInfo eventInfo, SliceItem sliceItem) {
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1666, ContextualCardLogUtils.buildCardClickLog(contextualCard, eventInfo.rowIndex, eventInfo.actionType, sliceViewHolder.getAdapterPosition()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SliceViewHolder extends RecyclerView.ViewHolder {
        public final SliceView sliceView;

        public SliceViewHolder(View view) {
            super(view);
            this.sliceView = (SliceView) view.findViewById(R$id.slice_view);
        }
    }
}
