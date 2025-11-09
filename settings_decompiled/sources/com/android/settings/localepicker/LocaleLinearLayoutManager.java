package com.android.settings.localepicker;

import android.content.Context;
import android.view.View;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class LocaleLinearLayoutManager extends LinearLayoutManager {
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveBottom;
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveDown;
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveTop;
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveUp;
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionRemove;
    private final LocaleDragAndDropAdapter mAdapter;
    private final Context mContext;

    public LocaleLinearLayoutManager(Context context, LocaleDragAndDropAdapter localeDragAndDropAdapter) {
        super(context);
        this.mContext = context;
        this.mAdapter = localeDragAndDropAdapter;
        this.mActionMoveUp = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R$id.action_drag_move_up, context.getString(R$string.action_drag_label_move_up));
        this.mActionMoveDown = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R$id.action_drag_move_down, context.getString(R$string.action_drag_label_move_down));
        this.mActionMoveTop = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R$id.action_drag_move_top, context.getString(R$string.action_drag_label_move_top));
        this.mActionMoveBottom = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R$id.action_drag_move_bottom, context.getString(R$string.action_drag_label_move_bottom));
        this.mActionRemove = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R$id.action_drag_remove, context.getString(R$string.action_drag_label_remove));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
        int itemCount = getItemCount();
        int position = getPosition(view);
        StringBuilder sb = new StringBuilder();
        int i = position + 1;
        sb.append(i);
        sb.append(", ");
        sb.append((Object) ((LocaleDragCell) view).getCheckbox().getContentDescription());
        accessibilityNodeInfoCompat.setContentDescription(sb.toString());
        if (this.mAdapter.isRemoveMode()) {
            return;
        }
        if (position > 0) {
            accessibilityNodeInfoCompat.addAction(this.mActionMoveUp);
            accessibilityNodeInfoCompat.addAction(this.mActionMoveTop);
        }
        if (i < itemCount) {
            accessibilityNodeInfoCompat.addAction(this.mActionMoveDown);
            accessibilityNodeInfoCompat.addAction(this.mActionMoveBottom);
        }
        if (itemCount > 1) {
            accessibilityNodeInfoCompat.addAction(this.mActionRemove);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x004e  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean performAccessibilityActionForItem(androidx.recyclerview.widget.RecyclerView.Recycler r6, androidx.recyclerview.widget.RecyclerView.State r7, android.view.View r8, int r9, android.os.Bundle r10) {
        /*
            r5 = this;
            int r0 = r5.getItemCount()
            int r1 = r5.getPosition(r8)
            int r2 = com.android.settings.R$id.action_drag_move_up
            r3 = 0
            r4 = 1
            if (r9 != r2) goto L19
            if (r1 <= 0) goto L4c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            int r7 = r1 + (-1)
            r6.onItemMove(r1, r7)
        L17:
            r3 = r4
            goto L4c
        L19:
            int r2 = com.android.settings.R$id.action_drag_move_down
            if (r9 != r2) goto L27
            int r6 = r1 + 1
            if (r6 >= r0) goto L4c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r7 = r5.mAdapter
            r7.onItemMove(r1, r6)
            goto L17
        L27:
            int r2 = com.android.settings.R$id.action_drag_move_top
            if (r9 != r2) goto L33
            if (r1 == 0) goto L4c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.onItemMove(r1, r3)
            goto L17
        L33:
            int r2 = com.android.settings.R$id.action_drag_move_bottom
            if (r9 != r2) goto L40
            int r0 = r0 - r4
            if (r1 == r0) goto L4c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.onItemMove(r1, r0)
            goto L17
        L40:
            int r2 = com.android.settings.R$id.action_drag_remove
            if (r9 != r2) goto L54
            if (r0 <= r4) goto L4c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.removeItem(r1)
            goto L17
        L4c:
            if (r3 == 0) goto L53
            com.android.settings.localepicker.LocaleDragAndDropAdapter r5 = r5.mAdapter
            r5.doTheUpdate()
        L53:
            return r3
        L54:
            boolean r5 = super.performAccessibilityActionForItem(r6, r7, r8, r9, r10)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.localepicker.LocaleLinearLayoutManager.performAccessibilityActionForItem(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, android.view.View, int, android.os.Bundle):boolean");
    }
}
