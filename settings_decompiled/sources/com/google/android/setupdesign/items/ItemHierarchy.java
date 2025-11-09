package com.google.android.setupdesign.items;
/* loaded from: classes2.dex */
public interface ItemHierarchy {

    /* loaded from: classes2.dex */
    public interface Observer {
        void onItemRangeChanged(ItemHierarchy itemHierarchy, int i, int i2);

        void onItemRangeInserted(ItemHierarchy itemHierarchy, int i, int i2);
    }

    int getCount();

    IItem getItemAt(int i);

    void registerObserver(Observer observer);
}
