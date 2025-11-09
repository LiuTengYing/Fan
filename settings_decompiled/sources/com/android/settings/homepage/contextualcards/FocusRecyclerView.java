package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes.dex */
public class FocusRecyclerView extends RecyclerView {
    private FocusListener mListener;

    /* loaded from: classes.dex */
    public interface FocusListener {
        void onWindowFocusChanged(boolean z);
    }

    public FocusRecyclerView(Context context) {
        super(context);
    }

    public FocusRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        FocusListener focusListener = this.mListener;
        if (focusListener != null) {
            focusListener.onWindowFocusChanged(z);
        }
    }

    public void setListener(FocusListener focusListener) {
        this.mListener = focusListener;
    }
}
