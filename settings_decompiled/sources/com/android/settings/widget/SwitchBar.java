package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.R$attr;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SwitchBar extends LinearLayout implements CompoundButton.OnCheckedChangeListener {
    private static final int[] XML_ATTRIBUTES = {R$attr.switchBarMarginStart, R$attr.switchBarMarginEnd, R$attr.switchBarBackgroundColor, R$attr.switchBarBackgroundActivatedColor, R$attr.switchBarRestrictionIcon};
    private int mBackgroundActivatedColor;
    private int mBackgroundColor;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private String mLabel;
    private boolean mLoggingIntialized;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mMetricsTag;
    private String mOffText;
    private String mOnText;
    private ImageView mRestrictedIcon;
    private String mSummary;
    private final TextAppearanceSpan mSummarySpan;
    private ToggleSwitch mSwitch;
    private final List<OnSwitchChangeListener> mSwitchChangeListeners;
    private TextView mTextView;

    /* loaded from: classes.dex */
    public interface OnSwitchChangeListener {
        void onSwitchChanged(Switch r1, boolean z);
    }

    public SwitchBar(Context context) {
        this(context, null);
    }

    public SwitchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SwitchBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SwitchBar(final Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSwitchChangeListeners = new ArrayList();
        this.mEnforcedAdmin = null;
        LayoutInflater.from(context).inflate(R$layout.switch_bar, this);
        setFocusable(true);
        setClickable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XML_ATTRIBUTES);
        this.mBackgroundColor = obtainStyledAttributes.getColor(2, 0);
        this.mBackgroundActivatedColor = obtainStyledAttributes.getColor(3, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(4);
        obtainStyledAttributes.recycle();
        this.mTextView = (TextView) findViewById(R$id.switch_text);
        this.mSummarySpan = new TextAppearanceSpan(((LinearLayout) this).mContext, R$style.TextAppearance_Small_SwitchBar);
        ((ViewGroup.MarginLayoutParams) this.mTextView.getLayoutParams()).setMarginStart((int) obtainStyledAttributes.getDimension(0, 0.0f));
        ToggleSwitch toggleSwitch = (ToggleSwitch) findViewById(R$id.switch_widget);
        this.mSwitch = toggleSwitch;
        toggleSwitch.setSaveEnabled(false);
        this.mSwitch.setFocusable(false);
        this.mSwitch.setClickable(false);
        ((ViewGroup.MarginLayoutParams) this.mSwitch.getLayoutParams()).setMarginEnd((int) obtainStyledAttributes.getDimension(1, 0.0f));
        setBackgroundColor(this.mBackgroundColor);
        setSwitchBarText(R$string.switch_on_text, R$string.switch_off_text);
        addOnSwitchChangeListener(new OnSwitchChangeListener() { // from class: com.android.settings.widget.SwitchBar$$ExternalSyntheticLambda0
            @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                SwitchBar.this.lambda$new$0(r1, z);
            }
        });
        ImageView imageView = (ImageView) findViewById(R$id.restricted_icon);
        this.mRestrictedIcon = imageView;
        imageView.setImageDrawable(drawable);
        this.mRestrictedIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.SwitchBar.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SwitchBar.this.mDisabledByAdmin) {
                    MetricsFeatureProvider metricsFeatureProvider = SwitchBar.this.mMetricsFeatureProvider;
                    metricsFeatureProvider.action(0, 853, 0, SwitchBar.this.mMetricsTag + "/switch_bar|restricted", 1);
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(context, SwitchBar.this.mEnforcedAdmin);
                }
            }
        });
        setVisibility(8);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Switch r1, boolean z) {
        setTextViewLabelAndBackground(z);
    }

    @Override // android.view.View
    public boolean performClick() {
        return getDelegatingView().performClick();
    }

    public void setMetricsTag(String str) {
        this.mMetricsTag = str;
    }

    public void setTextViewLabelAndBackground(boolean z) {
        this.mLabel = z ? this.mOnText : this.mOffText;
        setBackgroundColor(z ? this.mBackgroundActivatedColor : this.mBackgroundColor);
        updateText();
    }

    public void setSwitchBarText(int i, int i2) {
        this.mOnText = getResources().getString(i);
        this.mOffText = getResources().getString(i2);
        setTextViewLabelAndBackground(isChecked());
    }

    public void setSummary(String str) {
        this.mSummary = str;
        updateText();
    }

    private void updateText() {
        if (TextUtils.isEmpty(this.mSummary)) {
            this.mTextView.setText(this.mLabel);
            return;
        }
        SpannableStringBuilder append = new SpannableStringBuilder(this.mLabel).append('\n');
        int length = append.length();
        append.append((CharSequence) this.mSummary);
        append.setSpan(this.mSummarySpan, length, append.length(), 0);
        this.mTextView.setText(append);
    }

    public void setChecked(boolean z) {
        setTextViewLabelAndBackground(z);
        this.mSwitch.setChecked(z);
    }

    public void setCheckedInternal(boolean z) {
        setTextViewLabelAndBackground(z);
        this.mSwitch.setCheckedInternal(z);
    }

    public boolean isChecked() {
        return this.mSwitch.isChecked();
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (z && this.mDisabledByAdmin) {
            setDisabledByAdmin(null);
            return;
        }
        super.setEnabled(z);
        this.mTextView.setEnabled(z);
        this.mSwitch.setEnabled(z);
    }

    View getDelegatingView() {
        return this.mDisabledByAdmin ? this.mRestrictedIcon : this.mSwitch;
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
        if (enforcedAdmin != null) {
            super.setEnabled(true);
            this.mDisabledByAdmin = true;
            this.mTextView.setEnabled(false);
            this.mSwitch.setEnabled(false);
            this.mSwitch.setVisibility(8);
            this.mRestrictedIcon.setVisibility(0);
            return;
        }
        this.mDisabledByAdmin = false;
        this.mSwitch.setVisibility(0);
        this.mRestrictedIcon.setVisibility(8);
        setEnabled(true);
    }

    public final ToggleSwitch getSwitch() {
        return this.mSwitch;
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        setVisibility(0);
        this.mSwitch.setOnCheckedChangeListener(this);
    }

    public boolean isShowing() {
        return getVisibility() == 0;
    }

    public void propagateChecked(boolean z) {
        int size = this.mSwitchChangeListeners.size();
        for (int i = 0; i < size; i++) {
            this.mSwitchChangeListeners.get(i).onSwitchChanged(this.mSwitch, z);
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.mLoggingIntialized) {
            MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
            metricsFeatureProvider.action(0, 853, 0, this.mMetricsTag + "/switch_bar", z ? 1 : 0);
        }
        this.mLoggingIntialized = true;
        propagateChecked(z);
    }

    public void addOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        if (this.mSwitchChangeListeners.contains(onSwitchChangeListener)) {
            throw new IllegalStateException("Cannot add twice the same OnSwitchChangeListener");
        }
        this.mSwitchChangeListeners.add(onSwitchChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settings.widget.SwitchBar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean checked;
        boolean visible;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.checked = ((Boolean) parcel.readValue(null)).booleanValue();
            this.visible = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeValue(Boolean.valueOf(this.checked));
            parcel.writeValue(Boolean.valueOf(this.visible));
        }

        public String toString() {
            return "SwitchBar.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + " visible=" + this.visible + "}";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.checked = this.mSwitch.isChecked();
        savedState.visible = isShowing();
        return savedState;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSwitch.setCheckedInternal(savedState.checked);
        setTextViewLabelAndBackground(savedState.checked);
        setVisibility(savedState.visible ? 0 : 8);
        this.mSwitch.setOnCheckedChangeListener(savedState.visible ? this : null);
        requestLayout();
    }
}
