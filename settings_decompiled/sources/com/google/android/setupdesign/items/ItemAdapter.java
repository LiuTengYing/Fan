package com.google.android.setupdesign.items;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.google.android.setupdesign.items.ItemHierarchy;
/* loaded from: classes2.dex */
public class ItemAdapter extends BaseAdapter implements ItemHierarchy.Observer {
    private final ItemHierarchy itemHierarchy;
    private final ViewTypes viewTypes = new ViewTypes();

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public ItemAdapter(ItemHierarchy itemHierarchy) {
        this.itemHierarchy = itemHierarchy;
        itemHierarchy.registerObserver(this);
        refreshViewTypes();
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.itemHierarchy.getCount();
    }

    @Override // android.widget.Adapter
    public IItem getItem(int i) {
        return this.itemHierarchy.getItemAt(i);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return this.viewTypes.get(getItem(i).getLayoutResource());
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return this.viewTypes.size();
    }

    private void refreshViewTypes() {
        for (int i = 0; i < getCount(); i++) {
            this.viewTypes.add(getItem(i).getLayoutResource());
        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        IItem item = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(item.getLayoutResource(), viewGroup, false);
        }
        item.onBindView(view);
        return view;
    }

    public void onChanged(ItemHierarchy itemHierarchy) {
        refreshViewTypes();
        notifyDataSetChanged();
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy.Observer
    public void onItemRangeChanged(ItemHierarchy itemHierarchy, int i, int i2) {
        onChanged(itemHierarchy);
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy.Observer
    public void onItemRangeInserted(ItemHierarchy itemHierarchy, int i, int i2) {
        onChanged(itemHierarchy);
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int i) {
        return getItem(i).isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ViewTypes {
        private int nextPosition;
        private final SparseIntArray positionMap;

        private ViewTypes() {
            this.positionMap = new SparseIntArray();
            this.nextPosition = 0;
        }

        public int add(int i) {
            if (this.positionMap.indexOfKey(i) < 0) {
                this.positionMap.put(i, this.nextPosition);
                this.nextPosition++;
            }
            return this.positionMap.get(i);
        }

        public int size() {
            return this.positionMap.size();
        }

        public int get(int i) {
            return this.positionMap.get(i);
        }
    }
}
