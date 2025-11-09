package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class SmartWakeAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static SmartWakeAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        SmartWakeAnimation smartWakeAnimation = new SmartWakeAnimation();
        mPreference = smartSwitchPreference;
        return smartWakeAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.smart_wake, R$id.smart_wake_display, R$drawable.smart_wake_anim, "wake_gesture_enabled");
    }
}
