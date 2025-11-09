package com.google.android.setupdesign.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$layout;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.util.ItemStyler;
import com.google.android.setupdesign.util.LayoutStyler;
/* loaded from: classes2.dex */
public class Item extends AbstractItem {
    private CharSequence contentDescription;
    private boolean enabled;
    private Drawable icon;
    private int iconGravity;
    private int iconTint;
    private int layoutRes;
    private CharSequence summary;
    private CharSequence title;
    private boolean visible;

    public Item() {
        this.enabled = true;
        this.visible = true;
        this.iconTint = 0;
        this.iconGravity = 16;
        this.layoutRes = getDefaultLayoutResource();
    }

    public Item(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.enabled = true;
        this.visible = true;
        this.iconTint = 0;
        this.iconGravity = 16;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudItem);
        this.enabled = obtainStyledAttributes.getBoolean(R$styleable.SudItem_android_enabled, true);
        this.icon = obtainStyledAttributes.getDrawable(R$styleable.SudItem_android_icon);
        this.title = obtainStyledAttributes.getText(R$styleable.SudItem_android_title);
        this.summary = obtainStyledAttributes.getText(R$styleable.SudItem_android_summary);
        this.contentDescription = obtainStyledAttributes.getText(R$styleable.SudItem_android_contentDescription);
        this.layoutRes = obtainStyledAttributes.getResourceId(R$styleable.SudItem_android_layout, getDefaultLayoutResource());
        this.visible = obtainStyledAttributes.getBoolean(R$styleable.SudItem_android_visible, true);
        this.iconTint = obtainStyledAttributes.getColor(R$styleable.SudItem_sudIconTint, 0);
        this.iconGravity = obtainStyledAttributes.getInt(R$styleable.SudItem_sudIconGravity, 16);
        obtainStyledAttributes.recycle();
    }

    protected int getDefaultLayoutResource() {
        return R$layout.sud_items_default;
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
        notifyItemChanged();
    }

    @Override // com.google.android.setupdesign.items.AbstractItem, com.google.android.setupdesign.items.ItemHierarchy
    public int getCount() {
        return isVisible() ? 1 : 0;
    }

    @Override // com.google.android.setupdesign.items.IItem
    public boolean isEnabled() {
        return this.enabled;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIconGravity(int i) {
        this.iconGravity = i;
    }

    @Override // com.google.android.setupdesign.items.IItem
    public int getLayoutResource() {
        return this.layoutRes;
    }

    public void setSummary(CharSequence charSequence) {
        this.summary = charSequence;
        notifyItemChanged();
    }

    public CharSequence getSummary() {
        return this.summary;
    }

    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
        notifyItemChanged();
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public CharSequence getContentDescription() {
        return this.contentDescription;
    }

    public boolean isVisible() {
        return this.visible;
    }

    private boolean hasSummary(CharSequence charSequence) {
        return charSequence != null && charSequence.length() > 0;
    }

    @Override // com.google.android.setupdesign.items.AbstractItemHierarchy
    public int getViewId() {
        return getId();
    }

    public void onBindView(View view) {
        ((TextView) view.findViewById(R$id.sud_items_title)).setText(getTitle());
        TextView textView = (TextView) view.findViewById(R$id.sud_items_summary);
        CharSequence summary = getSummary();
        if (hasSummary(summary)) {
            textView.setText(summary);
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
        view.setContentDescription(getContentDescription());
        View findViewById = view.findViewById(R$id.sud_items_icon_container);
        Drawable icon = getIcon();
        if (icon != null) {
            ImageView imageView = (ImageView) view.findViewById(R$id.sud_items_icon);
            imageView.setImageDrawable(null);
            onMergeIconStateAndLevels(imageView, icon);
            imageView.setImageDrawable(icon);
            int i = this.iconTint;
            if (i != 0) {
                imageView.setColorFilter(i);
            } else {
                imageView.clearColorFilter();
            }
            ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
            if (layoutParams instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) layoutParams).gravity = this.iconGravity;
            }
            findViewById.setVisibility(0);
        } else {
            findViewById.setVisibility(8);
        }
        view.setId(getViewId());
        if (!(this instanceof ExpandableSwitchItem) && view.getId() != R$id.sud_layout_header) {
            LayoutStyler.applyPartnerCustomizationLayoutPaddingStyle(view);
        }
        ItemStyler.applyPartnerCustomizationItemStyle(view);
    }

    protected void onMergeIconStateAndLevels(ImageView imageView, Drawable drawable) {
        imageView.setImageState(drawable.getState(), false);
        imageView.setImageLevel(drawable.getLevel());
    }
}
