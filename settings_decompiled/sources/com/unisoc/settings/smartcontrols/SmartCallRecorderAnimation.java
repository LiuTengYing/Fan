package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class SmartCallRecorderAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static SmartCallRecorderAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        SmartCallRecorderAnimation smartCallRecorderAnimation = new SmartCallRecorderAnimation();
        mPreference = smartSwitchPreference;
        return smartCallRecorderAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.smart_call_recorder, R$id.smart_call_recorder_display, R$drawable.smart_call_recorder_anim, "smart_call_recorder");
    }
}
