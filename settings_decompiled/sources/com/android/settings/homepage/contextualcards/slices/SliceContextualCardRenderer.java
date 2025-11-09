package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.widget.SliceLiveData;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.homepage.contextualcards.CardContentProvider;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
import com.android.settings.homepage.contextualcards.slices.SliceFullCardRendererHelper;
import com.android.settings.homepage.contextualcards.slices.SliceHalfCardRendererHelper;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class SliceContextualCardRenderer implements ContextualCardRenderer, LifecycleObserver {
    public static final int VIEW_TYPE_FULL_WIDTH = R$layout.contextual_slice_full_tile;
    public static final int VIEW_TYPE_HALF_WIDTH = R$layout.contextual_slice_half_tile;
    public static final int VIEW_TYPE_STICKY = R$layout.contextual_slice_sticky_tile;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;
    private final SliceFullCardRendererHelper mFullCardHelper;
    private final SliceHalfCardRendererHelper mHalfCardHelper;
    private final LifecycleOwner mLifecycleOwner;
    final Map<Uri, LiveData<Slice>> mSliceLiveDataMap = new ArrayMap();
    final Set<RecyclerView.ViewHolder> mFlippedCardSet = new ArraySet();

    public SliceContextualCardRenderer(Context context, LifecycleOwner lifecycleOwner, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mLifecycleOwner = lifecycleOwner;
        this.mControllerRendererPool = controllerRendererPool;
        lifecycleOwner.getLifecycle().addObserver(this);
        this.mFullCardHelper = new SliceFullCardRendererHelper(context);
        this.mHalfCardHelper = new SliceHalfCardRendererHelper(context);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        if (i == VIEW_TYPE_HALF_WIDTH) {
            return this.mHalfCardHelper.createViewHolder(view);
        }
        return this.mFullCardHelper.createViewHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(final RecyclerView.ViewHolder viewHolder, final ContextualCard contextualCard) {
        final Uri sliceUri = contextualCard.getSliceUri();
        if (!"content".equals(sliceUri.getScheme())) {
            Log.w("SliceCardRenderer", "Invalid uri, skipping slice: " + sliceUri);
            return;
        }
        if (viewHolder.getItemViewType() != VIEW_TYPE_HALF_WIDTH) {
            ((SliceFullCardRendererHelper.SliceViewHolder) viewHolder).sliceView.setSlice(contextualCard.getSlice());
        }
        LiveData<Slice> liveData = this.mSliceLiveDataMap.get(sliceUri);
        if (liveData == null) {
            liveData = SliceLiveData.fromUri(this.mContext, sliceUri, new SliceLiveData.OnErrorListener() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda1
                @Override // androidx.slice.widget.SliceLiveData.OnErrorListener
                public final void onSliceError(int i, Throwable th) {
                    SliceContextualCardRenderer.this.lambda$bindView$1(sliceUri, i, th);
                }
            });
            this.mSliceLiveDataMap.put(sliceUri, liveData);
        }
        final View findViewById = viewHolder.itemView.findViewById(R$id.dismissal_swipe_background);
        liveData.removeObservers(this.mLifecycleOwner);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
        liveData.observe(this.mLifecycleOwner, new Observer() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda2
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SliceContextualCardRenderer.this.lambda$bindView$2(viewHolder, contextualCard, findViewById, (Slice) obj);
            }
        });
        if (viewHolder.getItemViewType() != VIEW_TYPE_STICKY) {
            initDismissalActions(viewHolder, contextualCard);
            if (contextualCard.isPendingDismiss()) {
                showDismissalView(viewHolder);
                this.mFlippedCardSet.add(viewHolder);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$1(final Uri uri, int i, Throwable th) {
        Log.w("SliceCardRenderer", "Slice may be null. uri = " + uri + ", error = " + i);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SliceContextualCardRenderer.this.lambda$bindView$0(uri);
            }
        });
        this.mContext.getContentResolver().notifyChange(CardContentProvider.REFRESH_CARD_URI, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$0(Uri uri) {
        this.mSliceLiveDataMap.get(uri).removeObservers(this.mLifecycleOwner);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindView$2(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard, View view, Slice slice) {
        if (slice == null) {
            return;
        }
        if (slice.hasHint("error")) {
            Log.w("SliceCardRenderer", "Slice has HINT_ERROR, skipping rendering. uri=" + slice.getUri());
            this.mSliceLiveDataMap.get(slice.getUri()).removeObservers(this.mLifecycleOwner);
            this.mContext.getContentResolver().notifyChange(CardContentProvider.REFRESH_CARD_URI, null);
            return;
        }
        if (viewHolder.getItemViewType() == VIEW_TYPE_HALF_WIDTH) {
            this.mHalfCardHelper.bindView(viewHolder, contextualCard, slice);
        } else {
            this.mFullCardHelper.bindView(viewHolder, contextualCard, slice);
        }
        if (view != null) {
            view.setVisibility(0);
        }
    }

    private void initDismissalActions(final RecyclerView.ViewHolder viewHolder, final ContextualCard contextualCard) {
        ((Button) viewHolder.itemView.findViewById(R$id.keep)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SliceContextualCardRenderer.this.lambda$initDismissalActions$3(viewHolder, view);
            }
        });
        ((Button) viewHolder.itemView.findViewById(R$id.remove)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SliceContextualCardRenderer.this.lambda$initDismissalActions$4(contextualCard, viewHolder, view);
            }
        });
        ViewCompat.setAccessibilityDelegate(getInitialView(viewHolder), new AccessibilityDelegateCompat() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.addAction(1048576);
                accessibilityNodeInfoCompat.setDismissable(true);
            }

            @Override // androidx.core.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i == 1048576) {
                    SliceContextualCardRenderer.this.mControllerRendererPool.getController(SliceContextualCardRenderer.this.mContext, contextualCard.getCardType()).onDismissed(contextualCard);
                }
                return super.performAccessibilityAction(view, i, bundle);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initDismissalActions$3(RecyclerView.ViewHolder viewHolder, View view) {
        this.mFlippedCardSet.remove(viewHolder);
        lambda$onStop$5(viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initDismissalActions$4(ContextualCard contextualCard, RecyclerView.ViewHolder viewHolder, View view) {
        this.mControllerRendererPool.getController(this.mContext, contextualCard.getCardType()).onDismissed(contextualCard);
        this.mFlippedCardSet.remove(viewHolder);
        lambda$onStop$5(viewHolder);
        this.mSliceLiveDataMap.get(contextualCard.getSliceUri()).removeObservers(this.mLifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mFlippedCardSet.forEach(new Consumer() { // from class: com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SliceContextualCardRenderer.this.lambda$onStop$5((RecyclerView.ViewHolder) obj);
            }
        });
        this.mFlippedCardSet.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: resetCardView */
    public void lambda$onStop$5(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.findViewById(R$id.dismissal_view).setVisibility(8);
        getInitialView(viewHolder).setVisibility(0);
    }

    private void showDismissalView(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.findViewById(R$id.dismissal_view).setVisibility(0);
        getInitialView(viewHolder).setVisibility(4);
    }

    private View getInitialView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_HALF_WIDTH) {
            return ((SliceHalfCardRendererHelper.HalfCardViewHolder) viewHolder).content;
        }
        return ((SliceFullCardRendererHelper.SliceViewHolder) viewHolder).sliceView;
    }
}
