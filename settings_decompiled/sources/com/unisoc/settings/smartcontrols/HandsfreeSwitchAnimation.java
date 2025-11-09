package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class HandsfreeSwitchAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static HandsfreeSwitchAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        HandsfreeSwitchAnimation handsfreeSwitchAnimation = new HandsfreeSwitchAnimation();
        mPreference = smartSwitchPreference;
        return handsfreeSwitchAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.handsfree_switch, R$id.handsfree_switch_display, R$drawable.handsfree_switch_anim, "handsfree_switch");
    }
}
