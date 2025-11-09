package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.embedding.SplitController;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.homepage.SettingsHomepageActivity;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class HighlightableTopLevelPreferenceAdapter extends PreferenceGroupAdapter implements SettingsHomepageActivity.HomepageLoadedListener {
    private String mHighlightKey;
    private boolean mHighlightNeeded;
    private int mHighlightPosition;
    private final SettingsHomepageActivity mHomepageActivity;
    private final int mIconColorHighlight;
    private final int mIconColorNormal;
    private final RecyclerView mRecyclerView;
    private int mScrollPosition;
    private boolean mScrolled;
    private final int mSummaryColorHighlight;
    private final int mSummaryColorNormal;
    private final int mTitleColorHighlight;
    private final int mTitleColorNormal;
    private SparseArray<PreferenceViewHolder> mViewHolders;
    private static final int RES_NORMAL_BACKGROUND = R$drawable.homepage_selectable_item_background;
    private static final int RES_HIGHLIGHTED_BACKGROUND = R$drawable.homepage_highlighted_item_background;

    public HighlightableTopLevelPreferenceAdapter(SettingsHomepageActivity settingsHomepageActivity, PreferenceGroup preferenceGroup, RecyclerView recyclerView, String str, boolean z) {
        super(preferenceGroup);
        this.mHighlightPosition = -1;
        this.mScrollPosition = -1;
        this.mRecyclerView = recyclerView;
        this.mHighlightKey = str;
        this.mScrolled = !z;
        this.mViewHolders = new SparseArray<>();
        this.mHomepageActivity = settingsHomepageActivity;
        Context context = preferenceGroup.getContext();
        this.mTitleColorNormal = Utils.getColorAttrDefaultColor(context, 16842806);
        this.mTitleColorHighlight = context.getColor(R$color.accent_select_primary_text);
        int i = R$color.title_text_color;
        this.mSummaryColorNormal = context.getColor(i);
        this.mSummaryColorHighlight = context.getColor(i);
        this.mIconColorNormal = com.android.settings.Utils.getHomepageIconColor(context);
        this.mIconColorHighlight = com.android.settings.Utils.getHomepageIconColorHighlight(context);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.PreferenceGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        this.mViewHolders.put(i, preferenceViewHolder);
        updateBackground(preferenceViewHolder, i);
    }

    void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        String str;
        if (!isHighlightNeeded()) {
            removeHighlightBackground(preferenceViewHolder);
        } else if (i == this.mHighlightPosition && (str = this.mHighlightKey) != null && TextUtils.equals(str, getItem(i).getKey())) {
            addHighlightBackground(preferenceViewHolder);
        } else {
            removeHighlightBackground(preferenceViewHolder);
        }
    }

    public void requestHighlight() {
        if (this.mRecyclerView == null) {
            return;
        }
        int i = this.mHighlightPosition;
        if (TextUtils.isEmpty(this.mHighlightKey)) {
            this.mHighlightPosition = -1;
            this.mScrolled = true;
            if (i >= 0) {
                notifyItemChanged(i);
                return;
            }
            return;
        }
        int preferenceAdapterPosition = getPreferenceAdapterPosition(this.mHighlightKey);
        if (preferenceAdapterPosition < 0) {
            return;
        }
        boolean isHighlightNeeded = isHighlightNeeded();
        if (isHighlightNeeded) {
            this.mScrollPosition = preferenceAdapterPosition;
            lambda$scroll$0();
        }
        if (isHighlightNeeded != this.mHighlightNeeded) {
            Log.d("HighlightableTopLevelAdapter", "Highlight needed change: " + isHighlightNeeded);
            this.mHighlightNeeded = isHighlightNeeded;
            this.mHighlightPosition = preferenceAdapterPosition;
            notifyItemChanged(preferenceAdapterPosition);
            if (isHighlightNeeded) {
                return;
            }
            removeHighlightAt(i);
        } else if (preferenceAdapterPosition == this.mHighlightPosition) {
        } else {
            this.mHighlightPosition = preferenceAdapterPosition;
            Log.d("HighlightableTopLevelAdapter", "Request highlight position " + preferenceAdapterPosition);
            Log.d("HighlightableTopLevelAdapter", "Is highlight needed: " + isHighlightNeeded);
            if (isHighlightNeeded) {
                notifyItemChanged(preferenceAdapterPosition);
                if (i >= 0) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void highlightPreference(String str, boolean z) {
        this.mHighlightKey = str;
        this.mScrolled = !z;
        requestHighlight();
    }

    @Override // com.android.settings.homepage.SettingsHomepageActivity.HomepageLoadedListener
    public void onHomepageLoaded() {
        lambda$scroll$0();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: scroll */
    public void lambda$scroll$0() {
        if (this.mScrolled || this.mScrollPosition < 0 || this.mHomepageActivity.addHomepageLoadedListener(this)) {
            return;
        }
        View childAt = this.mRecyclerView.getChildAt(this.mScrollPosition);
        if (childAt == null) {
            this.mRecyclerView.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightableTopLevelPreferenceAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HighlightableTopLevelPreferenceAdapter.this.lambda$scroll$0();
                }
            }, 100L);
            return;
        }
        this.mScrolled = true;
        Log.d("HighlightableTopLevelAdapter", "Scroll to position " + this.mScrollPosition);
        RecyclerView recyclerView = this.mRecyclerView;
        recyclerView.nestedScrollBy(0, -recyclerView.getHeight());
        int top = childAt.getTop();
        if (top > 0) {
            this.mRecyclerView.nestedScrollBy(0, top);
        }
    }

    private void removeHighlightAt(int i) {
        if (i >= 0) {
            PreferenceViewHolder preferenceViewHolder = this.mViewHolders.get(i);
            if (preferenceViewHolder != null) {
                removeHighlightBackground(preferenceViewHolder);
            }
            notifyItemChanged(i);
        }
    }

    private void addHighlightBackground(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        view.setBackgroundResource(RES_HIGHLIGHTED_BACKGROUND);
        TextView textView = (TextView) view.findViewById(16908310);
        TextView textView2 = (TextView) view.findViewById(16908304);
        if (textView != null) {
            textView.setTextColor(this.mTitleColorNormal);
        }
        if (textView2 != null) {
            textView2.setTextColor(this.mSummaryColorNormal);
        }
    }

    private void removeHighlightBackground(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        view.setBackgroundResource(RES_NORMAL_BACKGROUND);
        TextView textView = (TextView) view.findViewById(16908310);
        TextView textView2 = (TextView) view.findViewById(16908304);
        if (textView != null) {
            textView.setTextColor(this.mTitleColorNormal);
        }
        if (textView2 != null) {
            textView2.setTextColor(this.mSummaryColorNormal);
        }
    }

    private boolean isHighlightNeeded() {
        return SplitController.getInstance().isActivityEmbedded(this.mHomepageActivity);
    }
}
