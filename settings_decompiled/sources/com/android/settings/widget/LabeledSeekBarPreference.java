package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.internal.util.Preconditions;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$styleable;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class LabeledSeekBarPreference extends SeekBarPreference {
    private final int mIconEndContentDescriptionId;
    private final int mIconEndId;
    private final int mIconStartContentDescriptionId;
    private final int mIconStartId;
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private Preference.OnPreferenceChangeListener mStopListener;
    private final int mTextEndId;
    private final int mTextStartId;
    private final int mTickMarkId;

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R$layout.preference_labeled_slider);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LabeledSeekBarPreference);
        boolean z = false;
        this.mTextStartId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_textStart, 0);
        this.mTextEndId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_textEnd, 0);
        this.mTickMarkId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_tickMark, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_iconStart, 0);
        this.mIconStartId = resourceId;
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_iconEnd, 0);
        this.mIconEndId = resourceId2;
        int resourceId3 = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_iconStartContentDescription, 0);
        this.mIconStartContentDescriptionId = resourceId3;
        Preconditions.checkArgument(resourceId3 == 0 || resourceId != 0, "The resource of the iconStart attribute may be invalid or not set, you should set the iconStart attribute and have the valid resource.");
        int resourceId4 = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_iconEndContentDescription, 0);
        this.mIconEndContentDescriptionId = resourceId4;
        Preconditions.checkArgument((resourceId4 == 0 || resourceId2 != 0) ? true : z, "The resource of the iconEnd attribute may be invalid or not set, you should set the iconEnd attribute and have the valid resource.");
        obtainStyledAttributes.recycle();
    }

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.seekBarPreferenceStyle, 17957081), 0);
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        boolean z = true;
        boolean z2 = textView != null && textView.getVisibility() == 0;
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(16908310);
        if (textView2 != null && !isSelectable() && isEnabled() && z2) {
            textView2.setTextColor(Utils.getColorAttr(getContext(), 16842806));
        }
        TextView textView3 = (TextView) preferenceViewHolder.findViewById(16908308);
        int i = this.mTextStartId;
        if (i > 0) {
            textView3.setText(i);
        }
        TextView textView4 = (TextView) preferenceViewHolder.findViewById(16908309);
        int i2 = this.mTextEndId;
        if (i2 > 0) {
            textView4.setText(i2);
        }
        View findViewById = preferenceViewHolder.findViewById(R$id.label_frame);
        if (this.mTextStartId <= 0 && this.mTextEndId <= 0) {
            z = false;
        }
        findViewById.setVisibility(z ? 0 : 8);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(16909465);
        if (this.mTickMarkId != 0) {
            seekBar.setTickMark(getContext().getDrawable(this.mTickMarkId));
        }
        updateIconStartIfNeeded((ViewGroup) preferenceViewHolder.findViewById(R$id.icon_start_frame), (ImageView) preferenceViewHolder.findViewById(R$id.icon_start), seekBar);
        updateIconEndIfNeeded((ViewGroup) preferenceViewHolder.findViewById(R$id.icon_end_frame), (ImageView) preferenceViewHolder.findViewById(R$id.icon_end), seekBar);
    }

    public void setOnPreferenceChangeStopListener(Preference.OnPreferenceChangeListener onPreferenceChangeListener) {
        this.mStopListener = onPreferenceChangeListener;
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(seekBar, i, z);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
        Preference.OnPreferenceChangeListener onPreferenceChangeListener = this.mStopListener;
        if (onPreferenceChangeListener != null) {
            onPreferenceChangeListener.onPreferenceChange(this, Integer.valueOf(seekBar.getProgress()));
        }
        notifyChanged();
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mSeekBarChangeListener = onSeekBarChangeListener;
    }

    private void updateIconStartIfNeeded(ViewGroup viewGroup, ImageView imageView, SeekBar seekBar) {
        if (this.mIconStartId == 0) {
            return;
        }
        if (imageView.getDrawable() == null) {
            imageView.setImageResource(this.mIconStartId);
        }
        if (this.mIconStartContentDescriptionId != 0) {
            viewGroup.setContentDescription(viewGroup.getContext().getString(this.mIconStartContentDescriptionId));
        }
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.LabeledSeekBarPreference$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LabeledSeekBarPreference.this.lambda$updateIconStartIfNeeded$0(view);
            }
        });
        viewGroup.setVisibility(0);
        setIconViewAndFrameEnabled(imageView, seekBar.getProgress() > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconStartIfNeeded$0(View view) {
        int progress = getProgress();
        if (progress > 0) {
            setProgress(progress - 1);
        }
    }

    private void updateIconEndIfNeeded(ViewGroup viewGroup, ImageView imageView, SeekBar seekBar) {
        if (this.mIconEndId == 0) {
            return;
        }
        if (imageView.getDrawable() == null) {
            imageView.setImageResource(this.mIconEndId);
        }
        if (this.mIconEndContentDescriptionId != 0) {
            viewGroup.setContentDescription(viewGroup.getContext().getString(this.mIconEndContentDescriptionId));
        }
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.LabeledSeekBarPreference$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LabeledSeekBarPreference.this.lambda$updateIconEndIfNeeded$1(view);
            }
        });
        viewGroup.setVisibility(0);
        setIconViewAndFrameEnabled(imageView, seekBar.getProgress() < seekBar.getMax());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconEndIfNeeded$1(View view) {
        int progress = getProgress();
        if (progress < getMax()) {
            setProgress(progress + 1);
        }
    }

    private static void setIconViewAndFrameEnabled(View view, boolean z) {
        view.setEnabled(z);
        ((ViewGroup) view.getParent()).setEnabled(z);
    }
}
