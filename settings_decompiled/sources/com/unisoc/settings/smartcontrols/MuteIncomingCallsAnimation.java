package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class MuteIncomingCallsAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static MuteIncomingCallsAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        MuteIncomingCallsAnimation muteIncomingCallsAnimation = new MuteIncomingCallsAnimation();
        mPreference = smartSwitchPreference;
        return muteIncomingCallsAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.mute_incoming_calls, R$id.mute_incoming_calls_display, R$drawable.mute_incoming_calls_anim, "mute_incoming_calls");
    }
}
