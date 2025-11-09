package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class SmartPickUpAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static SmartPickUpAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        SmartPickUpAnimation smartPickUpAnimation = new SmartPickUpAnimation();
        mPreference = smartSwitchPreference;
        return smartPickUpAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.smart_pick_up, R$id.smart_pick_up_display, R$drawable.smart_pick_up_anim, "doze_pulse_on_pick_up");
    }
}
