package com.unisoc.settings.smartcontrols;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.widget.SmartSwitchPreference;
/* loaded from: classes2.dex */
public class MuteAlarmsAnimation extends DialogFragment {
    private static SmartSwitchPreference mPreference;

    public static MuteAlarmsAnimation newInstance(SmartSwitchPreference smartSwitchPreference) {
        MuteAlarmsAnimation muteAlarmsAnimation = new MuteAlarmsAnimation();
        mPreference = smartSwitchPreference;
        return muteAlarmsAnimation;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return Utils.createAnimationDialog(getActivity(), mPreference, R$layout.mute_alarms, R$id.mute_alarms_display, R$drawable.mute_alarms_anim, "mute_alarms");
    }
}
