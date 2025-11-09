package com.android.settings.notification.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class BubblePreference extends Preference implements View.OnClickListener {
    private ButtonViewHolder mBubbleAllButton;
    private ButtonViewHolder mBubbleNoneButton;
    private ButtonViewHolder mBubbleSelectedButton;
    private Context mContext;
    RestrictedPreferenceHelper mHelper;
    private int mSelectedPreference;
    private boolean mSelectedVisible;

    public BubblePreference(Context context) {
        this(context, null);
    }

    public BubblePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubblePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BubblePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        RestrictedPreferenceHelper restrictedPreferenceHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
        this.mHelper = restrictedPreferenceHelper;
        restrictedPreferenceHelper.useAdminDisabledSummary(true);
        this.mContext = context;
        setLayoutResource(R$layout.bubble_preference);
    }

    public void setSelectedPreference(int i) {
        this.mSelectedPreference = i;
    }

    public int getSelectedPreference() {
        return this.mSelectedPreference;
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mHelper.setDisabledByAdmin(enforcedAdmin)) {
            notifyChanged();
        }
    }

    public void setSelectedVisibility(boolean z) {
        this.mSelectedVisible = z;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        boolean isDisabledByAdmin = this.mHelper.isDisabledByAdmin();
        View findViewById = preferenceViewHolder.findViewById(16908304);
        if (isDisabledByAdmin) {
            this.mHelper.onBindViewHolder(preferenceViewHolder);
            findViewById.setVisibility(0);
        } else {
            findViewById.setVisibility(8);
        }
        preferenceViewHolder.itemView.setClickable(false);
        View findViewById2 = preferenceViewHolder.findViewById(R$id.bubble_all);
        ButtonViewHolder buttonViewHolder = new ButtonViewHolder(findViewById2, (ImageView) preferenceViewHolder.findViewById(R$id.bubble_all_icon), (TextView) preferenceViewHolder.findViewById(R$id.bubble_all_label), 1);
        this.mBubbleAllButton = buttonViewHolder;
        buttonViewHolder.setSelected(this.mContext, this.mSelectedPreference == 1);
        findViewById2.setTag(1);
        findViewById2.setOnClickListener(this);
        findViewById2.setVisibility(isDisabledByAdmin ? 8 : 0);
        View findViewById3 = preferenceViewHolder.findViewById(R$id.bubble_selected);
        ButtonViewHolder buttonViewHolder2 = new ButtonViewHolder(findViewById3, (ImageView) preferenceViewHolder.findViewById(R$id.bubble_selected_icon), (TextView) preferenceViewHolder.findViewById(R$id.bubble_selected_label), 2);
        this.mBubbleSelectedButton = buttonViewHolder2;
        buttonViewHolder2.setSelected(this.mContext, this.mSelectedPreference == 2);
        findViewById3.setTag(2);
        findViewById3.setOnClickListener(this);
        findViewById3.setVisibility((!this.mSelectedVisible || isDisabledByAdmin) ? 8 : 0);
        View findViewById4 = preferenceViewHolder.findViewById(R$id.bubble_none);
        ButtonViewHolder buttonViewHolder3 = new ButtonViewHolder(findViewById4, (ImageView) preferenceViewHolder.findViewById(R$id.bubble_none_icon), (TextView) preferenceViewHolder.findViewById(R$id.bubble_none_label), 0);
        this.mBubbleNoneButton = buttonViewHolder3;
        buttonViewHolder3.setSelected(this.mContext, this.mSelectedPreference == 0);
        findViewById4.setTag(0);
        findViewById4.setOnClickListener(this);
        findViewById4.setVisibility(isDisabledByAdmin ? 8 : 0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        callChangeListener(Integer.valueOf(intValue));
        this.mBubbleAllButton.setSelected(this.mContext, intValue == 1);
        this.mBubbleSelectedButton.setSelected(this.mContext, intValue == 2);
        this.mBubbleNoneButton.setSelected(this.mContext, intValue == 0);
    }

    /* loaded from: classes.dex */
    private class ButtonViewHolder {
        private int mId;
        private ImageView mImageView;
        private TextView mTextView;
        private View mView;

        ButtonViewHolder(View view, ImageView imageView, TextView textView, int i) {
            this.mView = view;
            this.mImageView = imageView;
            this.mTextView = textView;
            this.mId = i;
        }

        void setSelected(Context context, boolean z) {
            int i;
            ColorStateList colorAttr;
            View view = this.mView;
            Context context2 = BubblePreference.this.mContext;
            if (z) {
                i = R$drawable.button_border_selected;
            } else {
                i = R$drawable.button_border_unselected;
            }
            view.setBackground(context2.getDrawable(i));
            this.mView.setSelected(z);
            if (z) {
                colorAttr = Utils.getColorAccent(context);
            } else {
                colorAttr = Utils.getColorAttr(context, 16842806);
            }
            this.mImageView.setImageTintList(colorAttr);
            this.mTextView.setTextColor(colorAttr);
        }
    }
}
