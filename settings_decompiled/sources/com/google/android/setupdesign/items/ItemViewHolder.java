package com.google.android.setupdesign.items;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.DividerItemDecoration;
/* loaded from: classes2.dex */
public class ItemViewHolder extends RecyclerView.ViewHolder implements DividerItemDecoration.DividedViewHolder {
    private boolean isEnabled;
    private IItem item;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ItemViewHolder(View view) {
        super(view);
    }

    @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
    public boolean isDividerAllowedAbove() {
        IItem iItem = this.item;
        return iItem instanceof Dividable ? ((Dividable) iItem).isDividerAllowedAbove() : this.isEnabled;
    }

    @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
    public boolean isDividerAllowedBelow() {
        IItem iItem = this.item;
        return iItem instanceof Dividable ? ((Dividable) iItem).isDividerAllowedBelow() : this.isEnabled;
    }

    public void setEnabled(boolean z) {
        this.isEnabled = z;
        this.itemView.setClickable(z);
        this.itemView.setEnabled(z);
        this.itemView.setFocusable(z);
    }

    public void setItem(IItem iItem) {
        this.item = iItem;
    }

    public IItem getItem() {
        return this.item;
    }
}
