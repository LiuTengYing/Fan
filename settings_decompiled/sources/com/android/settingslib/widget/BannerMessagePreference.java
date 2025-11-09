package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.utils.BuildCompatUtils;
/* loaded from: classes2.dex */
public class BannerMessagePreference extends Preference {
    private static final boolean IS_AT_LEAST_S = BuildCompatUtils.isAtLeastS();
    private AttentionLevel mAttentionLevel;
    private final DismissButtonInfo mDismissButtonInfo;
    private final ButtonInfo mNegativeButtonInfo;
    private final ButtonInfo mPositiveButtonInfo;
    private String mSubtitle;

    /* loaded from: classes2.dex */
    public enum AttentionLevel {
        HIGH(0, R$color.banner_background_attention_high, R$color.banner_accent_attention_high),
        MEDIUM(1, R$color.banner_background_attention_medium, R$color.banner_accent_attention_medium),
        LOW(2, R$color.banner_background_attention_low, R$color.banner_accent_attention_low);
        
        private final int mAccentColorResId;
        private final int mAttrValue;
        private final int mBackgroundColorResId;

        AttentionLevel(int i, int i2, int i3) {
            this.mAttrValue = i;
            this.mBackgroundColorResId = i2;
            this.mAccentColorResId = i3;
        }

        static AttentionLevel fromAttr(int i) {
            AttentionLevel[] values;
            for (AttentionLevel attentionLevel : values()) {
                if (attentionLevel.mAttrValue == i) {
                    return attentionLevel;
                }
            }
            throw new IllegalArgumentException();
        }

        public int getAccentColorResId() {
            return this.mAccentColorResId;
        }

        public int getBackgroundColorResId() {
            return this.mBackgroundColorResId;
        }
    }

    public BannerMessagePreference(Context context) {
        super(context);
        this.mPositiveButtonInfo = new ButtonInfo();
        this.mNegativeButtonInfo = new ButtonInfo();
        this.mDismissButtonInfo = new DismissButtonInfo();
        this.mAttentionLevel = AttentionLevel.HIGH;
        init(context, null);
    }

    public BannerMessagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPositiveButtonInfo = new ButtonInfo();
        this.mNegativeButtonInfo = new ButtonInfo();
        this.mDismissButtonInfo = new DismissButtonInfo();
        this.mAttentionLevel = AttentionLevel.HIGH;
        init(context, attributeSet);
    }

    public BannerMessagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPositiveButtonInfo = new ButtonInfo();
        this.mNegativeButtonInfo = new ButtonInfo();
        this.mDismissButtonInfo = new DismissButtonInfo();
        this.mAttentionLevel = AttentionLevel.HIGH;
        init(context, attributeSet);
    }

    public BannerMessagePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mPositiveButtonInfo = new ButtonInfo();
        this.mNegativeButtonInfo = new ButtonInfo();
        this.mDismissButtonInfo = new DismissButtonInfo();
        this.mAttentionLevel = AttentionLevel.HIGH;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setSelectable(false);
        setLayoutResource(R$layout.settingslib_banner_message);
        if (!IS_AT_LEAST_S || attributeSet == null) {
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.BannerMessagePreference);
        this.mAttentionLevel = AttentionLevel.fromAttr(obtainStyledAttributes.getInt(R$styleable.BannerMessagePreference_attentionLevel, 0));
        this.mSubtitle = obtainStyledAttributes.getString(R$styleable.BannerMessagePreference_subtitle);
        obtainStyledAttributes.recycle();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Context context = getContext();
        TextView textView = (TextView) preferenceViewHolder.findViewById(R$id.banner_title);
        CharSequence title = getTitle();
        textView.setText(title);
        textView.setVisibility(title == null ? 8 : 0);
        ((TextView) preferenceViewHolder.findViewById(R$id.banner_summary)).setText(getSummary());
        this.mPositiveButtonInfo.mButton = (Button) preferenceViewHolder.findViewById(R$id.banner_positive_btn);
        this.mNegativeButtonInfo.mButton = (Button) preferenceViewHolder.findViewById(R$id.banner_negative_btn);
        Resources.Theme theme = context.getTheme();
        int color = context.getResources().getColor(this.mAttentionLevel.getAccentColorResId(), theme);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R$id.banner_icon);
        if (imageView != null) {
            Drawable icon = getIcon();
            if (icon == null) {
                icon = getContext().getDrawable(R$drawable.ic_warning);
            }
            imageView.setImageDrawable(icon);
            imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        if (IS_AT_LEAST_S) {
            int color2 = context.getResources().getColor(this.mAttentionLevel.getBackgroundColorResId(), theme);
            preferenceViewHolder.setDividerAllowedAbove(false);
            preferenceViewHolder.setDividerAllowedBelow(false);
            preferenceViewHolder.itemView.getBackground().setTint(color2);
            this.mPositiveButtonInfo.mColor = color;
            this.mNegativeButtonInfo.mColor = color;
            this.mDismissButtonInfo.mButton = (ImageButton) preferenceViewHolder.findViewById(R$id.banner_dismiss_btn);
            this.mDismissButtonInfo.setUpButton();
            TextView textView2 = (TextView) preferenceViewHolder.findViewById(R$id.banner_subtitle);
            textView2.setText(this.mSubtitle);
            textView2.setVisibility(this.mSubtitle != null ? 0 : 8);
        } else {
            preferenceViewHolder.setDividerAllowedAbove(true);
            preferenceViewHolder.setDividerAllowedBelow(true);
        }
        this.mPositiveButtonInfo.setUpButton();
        this.mNegativeButtonInfo.setUpButton();
    }

    public BannerMessagePreference setPositiveButtonOnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != this.mPositiveButtonInfo.mListener) {
            this.mPositiveButtonInfo.mListener = onClickListener;
            notifyChanged();
        }
        return this;
    }

    public BannerMessagePreference setPositiveButtonText(int i) {
        return setPositiveButtonText(getContext().getString(i));
    }

    public BannerMessagePreference setPositiveButtonText(String str) {
        if (!TextUtils.equals(str, this.mPositiveButtonInfo.mText)) {
            this.mPositiveButtonInfo.mText = str;
            notifyChanged();
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class ButtonInfo {
        private Button mButton;
        private int mColor;
        private boolean mIsVisible = true;
        private View.OnClickListener mListener;
        private CharSequence mText;

        ButtonInfo() {
        }

        void setUpButton() {
            this.mButton.setText(this.mText);
            this.mButton.setOnClickListener(this.mListener);
            if (BannerMessagePreference.IS_AT_LEAST_S) {
                this.mButton.setTextColor(this.mColor);
            }
            if (shouldBeVisible()) {
                this.mButton.setVisibility(0);
            } else {
                this.mButton.setVisibility(8);
            }
        }

        private boolean shouldBeVisible() {
            return this.mIsVisible && !TextUtils.isEmpty(this.mText);
        }
    }

    /* loaded from: classes2.dex */
    static class DismissButtonInfo {
        private ImageButton mButton;
        private boolean mIsVisible = true;
        private View.OnClickListener mListener;

        DismissButtonInfo() {
        }

        void setUpButton() {
            this.mButton.setOnClickListener(this.mListener);
            if (shouldBeVisible()) {
                this.mButton.setVisibility(0);
            } else {
                this.mButton.setVisibility(8);
            }
        }

        private boolean shouldBeVisible() {
            return this.mIsVisible && this.mListener != null;
        }
    }
}
