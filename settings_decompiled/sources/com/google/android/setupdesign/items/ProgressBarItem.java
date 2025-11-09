package com.google.android.setupdesign.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.setupdesign.R$layout;
/* loaded from: classes2.dex */
public class ProgressBarItem extends Item {
    @Override // com.google.android.setupdesign.items.Item, com.google.android.setupdesign.items.IItem
    public boolean isEnabled() {
        return false;
    }

    @Override // com.google.android.setupdesign.items.Item, com.google.android.setupdesign.items.IItem
    public void onBindView(View view) {
    }

    public ProgressBarItem() {
    }

    public ProgressBarItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.setupdesign.items.Item
    protected int getDefaultLayoutResource() {
        return R$layout.sud_items_progress_bar;
    }
}
