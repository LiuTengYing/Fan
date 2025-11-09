package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class EasyBellAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static EasyBellAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        EasyBellAnimation easyBellAnimation = new EasyBellAnimation();
        mPreference = smartSwitchPreference;
        return easyBellAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.easy_bell, R$id.easy_bell_display, R$drawable.easy_bell_anim, "easy_bell");
    }
}
