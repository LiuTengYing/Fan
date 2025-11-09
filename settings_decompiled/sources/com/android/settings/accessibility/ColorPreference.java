package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class ColorPreference extends ListDialogPreference {
    private ColorDrawable mPreviewColor;
    private boolean mPreviewEnabled;

    public ColorPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R$layout.grid_picker_dialog);
        setListItemLayoutResource(R$layout.color_picker_item);
    }

    @Override // androidx.preference.Preference
    public boolean shouldDisableDependents() {
        return Color.alpha(getValue()) == 0 || super.shouldDisableDependents();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ListDialogPreference
    public CharSequence getTitleAt(int i) {
        CharSequence titleAt = super.getTitleAt(i);
        if (titleAt != null) {
            return titleAt;
        }
        int valueAt = getValueAt(i);
        return getContext().getString(R$string.color_custom, Integer.valueOf(Color.red(valueAt)), Integer.valueOf(Color.green(valueAt)), Integer.valueOf(Color.blue(valueAt)));
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mPreviewEnabled) {
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R$id.color_preview);
            int value = getValue();
            if (Color.alpha(value) < 255) {
                imageView.setBackgroundResource(R$drawable.transparency_tileable);
            } else {
                imageView.setBackground(null);
            }
            ColorDrawable colorDrawable = this.mPreviewColor;
            if (colorDrawable == null) {
                ColorDrawable colorDrawable2 = new ColorDrawable(value);
                this.mPreviewColor = colorDrawable2;
                imageView.setImageDrawable(colorDrawable2);
            } else {
                colorDrawable.setColor(value);
            }
            CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                imageView.setContentDescription(summary);
            } else {
                imageView.setContentDescription(null);
            }
            imageView.setAlpha(isEnabled() ? 1.0f : 0.2f);
        }
    }

    @Override // com.android.settings.accessibility.ListDialogPreference
    protected void onBindListItem(View view, int i) {
        int valueAt = getValueAt(i);
        int alpha = Color.alpha(valueAt);
        ImageView imageView = (ImageView) view.findViewById(R$id.color_swatch);
        if (alpha < 255) {
            imageView.setBackgroundResource(R$drawable.transparency_tileable);
        } else {
            imageView.setBackground(null);
        }
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable).setColor(valueAt);
        } else {
            imageView.setImageDrawable(new ColorDrawable(valueAt));
        }
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(R$id.summary)).setText(titleAt);
        }
    }
}
