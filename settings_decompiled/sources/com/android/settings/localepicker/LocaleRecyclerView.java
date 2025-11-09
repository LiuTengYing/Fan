package com.android.settings.localepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes.dex */
class LocaleRecyclerView extends RecyclerView {
    public LocaleRecyclerView(Context context) {
        super(context);
    }

    public LocaleRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LocaleRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        LocaleDragAndDropAdapter localeDragAndDropAdapter;
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && (localeDragAndDropAdapter = (LocaleDragAndDropAdapter) getAdapter()) != null) {
            localeDragAndDropAdapter.doTheUpdate();
        }
        return super.onTouchEvent(motionEvent);
    }
}
