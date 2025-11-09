package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class QuickBrowseAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static QuickBrowseAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        QuickBrowseAnimation quickBrowseAnimation = new QuickBrowseAnimation();
        mPreference = smartSwitchPreference;
        return quickBrowseAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.quick_browse, R$id.quick_browse_display, R$drawable.quick_browse_anim, "quick_browse");
    }
}
