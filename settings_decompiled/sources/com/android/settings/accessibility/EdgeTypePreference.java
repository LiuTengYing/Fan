package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;
import com.android.settings.R$array;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class EdgeTypePreference extends ListDialogPreference {
    public EdgeTypePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = context.getResources();
        setValues(resources.getIntArray(R$array.captioning_edge_type_selector_values));
        setTitles(resources.getStringArray(R$array.captioning_edge_type_selector_titles));
        setDialogLayoutResource(R$layout.grid_picker_dialog);
        setListItemLayoutResource(R$layout.preset_picker_item);
    }

    @Override // androidx.preference.Preference
    public boolean shouldDisableDependents() {
        return getValue() == 0 || super.shouldDisableDependents();
    }

    @Override // com.android.settings.accessibility.ListDialogPreference
    protected void onBindListItem(View view, int i) {
        SubtitleView findViewById = view.findViewById(R$id.preview);
        findViewById.setForegroundColor(-1);
        findViewById.setBackgroundColor(0);
        findViewById.setTextSize(getContext().getResources().getDisplayMetrics().density * 32.0f);
        findViewById.setEdgeType(getValueAt(i));
        findViewById.setEdgeColor(-16777216);
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(R$id.summary)).setText(titleAt);
        }
    }
}
