package com.android.settings.accessibility;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.setupdesign.GlifPreferenceLayout;
import com.google.android.setupdesign.util.LayoutStyler;
/* loaded from: classes.dex */
public class TextReadingPreferenceFragmentForSetupWizard extends TextReadingPreferenceFragment {
    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.accessibility.TextReadingPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1915;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        String string = getContext().getString(R$string.accessibility_text_reading_options_title);
        Drawable drawable = getContext().getDrawable(R$drawable.ic_accessibility_visibility);
        drawable.setTintList(Utils.getColorAttr(getContext(), 16843827));
        AccessibilitySetupWizardUtils.updateGlifPreferenceLayout(getContext(), (GlifPreferenceLayout) view, string, null, drawable);
        updateResetButtonPadding();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
    }

    private void updateResetButtonPadding() {
        LayoutStyler.applyPartnerCustomizationLayoutPaddingStyle((ViewGroup) ((LayoutPreference) findPreference("reset")).findViewById(R$id.reset_button).getParent());
    }
}
