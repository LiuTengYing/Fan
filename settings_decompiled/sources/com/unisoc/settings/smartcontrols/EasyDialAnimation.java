package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class EasyDialAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static EasyDialAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        EasyDialAnimation easyDialAnimation = new EasyDialAnimation();
        mPreference = smartSwitchPreference;
        return easyDialAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.easy_dial, R$id.easy_dial_display, R$drawable.easy_dial_anim, "easy_dial");
    }
}
